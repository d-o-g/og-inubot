package com.inubot;

import com.inubot.model.Account;
import com.inubot.model.Owned;
import com.inubot.model.Script;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Septron
 * @since July 07, 2015
 */
public class Handler extends ChannelHandlerAdapter {

	private static final Map<String, Integer> instances = new HashMap<>();

	private static final byte LOGIN = 0;
	private static final byte REQUEST_SCRIPTS = 1;
	private static final byte INSTANCE_COUNT = 5;


	private static final Logger logger = LoggerFactory.getLogger(Handler.class);

	private static String getMD5(String message)
			throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.reset();
		md5.update(message.getBytes());
		byte[] digest = md5.digest();
		return String.format("%0" + (digest.length << 1) + "x", new BigInteger(1, digest));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		if (cause.toString().contains("closed"))
			return;
		ByteBuf buf = Unpooled.buffer();
		buf.writeByte(INSTANCE_COUNT);
		buf.writeByte(0);
		ctx.write(buf);
		ctx.flush();
		ctx.close();
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		logger.info(getAddressFromContext(ctx) + " Disconnected!");
		if (instances.get(getAddressFromContext(ctx)) != null) {
			int count = instances.get(getAddressFromContext(ctx));
			if (count != 0) {
				if (count == 1) {
					instances.remove(getAddressFromContext(ctx));
				} else {
					instances.put(getAddressFromContext(ctx), count - 1);
				}
			}
		}
	}

	private String getAddressFromContext(ChannelHandlerContext ctx) {
		InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
		InetAddress inetaddress = socketAddress.getAddress();
		return inetaddress.getHostAddress();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		ByteBuf buffer = (ByteBuf) msg;
		logger.info(getAddressFromContext(ctx) + " ");
		int opcode = buffer.readByte();
		switch (opcode) {
			case LOGIN: {
				if (!buffer.isReadable()) {
					ByteBuf buf = Unpooled.buffer();
					buf.writeByte(INSTANCE_COUNT);
					buf.writeByte(0);
					ctx.write(buf);
					ctx.flush();
					return;
				}

				int ulength = buffer.readInt();
				byte[] uarray = new byte[ulength];
				for (int i = 0; i < ulength; i++) {
					uarray[i] = buffer.readByte();
				}
				String username = new String(uarray, "UTF-8");

				int plength = buffer.readInt();
				byte[] parray = new byte[plength];
				for (int i = 0; i < plength; i++) {
					parray[i] = buffer.readByte();
				}
				String password = new String(parray, "UTF-8");

				Session session = Application.factory().openSession();
				Account account = (Account) session.createQuery("from Account where username=:username")
						.setParameter("username", username).uniqueResult();
				if (account == null) {
					logger.info("Account doesn't exist!");
					ByteBuf buf = Unpooled.buffer();
					buf.writeByte(INSTANCE_COUNT);
					buf.writeByte(0);
					ctx.write(buf);
					ctx.flush();
					return;
				}

				String hash = getMD5(getMD5(account.getSalt()) + getMD5(password));

				if (hash.equals(account.getPassword())) {
					logger.info("Successfully logged " + username + " into account! (" + account.getGroup() + ")");
					ctx.writeAndFlush(Unpooled.wrappedBuffer(new byte[]{4}));

					int count;
					if (instances.get(getAddressFromContext(ctx)) == null)
						count = 0;
					else
						count = instances.get(getAddressFromContext(ctx));

					Account.UserGroup group = account.getUserGroup();

					logger.info(count + " count - allowed " + group.getMaximumInstances());
					if (count < group.getMaximumInstances()) {
						instances.put(getAddressFromContext(ctx), count + 1);
						ByteBuf buf = Unpooled.buffer();
						buf.writeByte(INSTANCE_COUNT);
						buf.writeByte(1);
						ctx.write(buf);
						ctx.flush();
					} else {
						ByteBuf buf = Unpooled.buffer();
						buf.writeByte(INSTANCE_COUNT);
						buf.writeByte(0);
						ctx.write(buf);
						ctx.flush();
						return;
					}

					List owneds = session.createQuery("from Owned where uid=:uid").setParameter("uid", account.getId()).list();
					for (Object asd : owneds) {
						Script script = (Script) session.createQuery("from Script where id=:id")
								.setParameter("id", ((Owned) asd).getId()).uniqueResult();
						byte[] data = Loader.scripts.get(script.getName());
						if (data == null) {
							logger.info("no have " + script.getName());
							continue;
						}
						ByteBuf buf = Unpooled.buffer();
						buf.writeByte(REQUEST_SCRIPTS);
						buf.writeBytes(data);
						ctx.write(buf);
						ctx.flush();
						logger.info("Sent: " + script.getName());
					}
				} else {
					logger.info("Failed to log " + username + " into account!");
					ByteBuf buf = Unpooled.buffer();
					buf.writeByte(INSTANCE_COUNT);
					buf.writeByte(0);
					ctx.write(buf);
					ctx.flush();
				}
				break;
			}
		}
	}
}
