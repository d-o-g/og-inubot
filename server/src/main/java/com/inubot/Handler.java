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
    private static final byte OPENED_BOT = 2;
    private static final byte CLOSED_BOT = 3;
    private static final byte INSTANCE_COUNT = 5;


    private static final Logger logger = LoggerFactory.getLogger(Handler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

    }

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
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private boolean fuck = true;

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.info(hnnf(ctx) + " ");
        if (fuck)
            instances.remove(hnnf(ctx));
    }

    private String hnnf(ChannelHandlerContext ctx) {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        InetAddress inetaddress = socketAddress.getAddress();
        return inetaddress.getHostAddress();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ByteBuf buffer = (ByteBuf) msg;
        logger.info(hnnf(ctx) + " ");
        int opcode = buffer.readByte();
        switch (opcode) {
            case LOGIN: {
                StringBuilder builder = new StringBuilder();
                int ulength = buffer.readInt();
                for (int i = 0; i < ulength; i++) {
                    builder.append(buffer.readChar());
                }
                String username = builder.toString();

                builder = new StringBuilder();
                int plength = buffer.readInt();
                for (int i = 0; i < plength; i++) {
                    builder.append(buffer.readChar());
                }
                String password = builder.toString();

                Session session = Application.factory().openSession();
                Account account = (Account) session.createQuery("from Account where username=:username")
                        .setParameter("username", username).uniqueResult();
                if (account == null) {
                    logger.info("Account doesn't exist!");
                    return;
                }

                String hash = getMD5(getMD5(account.getSalt()) + getMD5(password));

                if (hash.equals(account.getPassword())) {
                    logger.info("Successfully logged " + username + " into account! (" + account.getId() + ")");
                    ctx.write(Unpooled.wrappedBuffer(new byte[] { 4 }));
                    List owneds = session.createQuery("from Owned where uid=:uid").setParameter("uid", account.getId()).list();

                    if (account.getUserGroup() == Account.UserGroup.ADMIN) {
                        for (byte[] data : Loader.scripts.values()) {
                            ctx.write(Unpooled.wrappedBuffer(new byte[]{REQUEST_SCRIPTS}));
                            ctx.flush();
                            ByteBuf buf = Unpooled.directBuffer(data.length);
                            buf.writeBytes(data);
                            ctx.write(buf);
                        }
                    } else {
                        for (Object asd : owneds) {
                            Script script = (Script) session.createQuery("from Script where id=:id")
                                    .setParameter("id", ((Owned) asd).getId()).uniqueResult();
                            byte[] data = Loader.scripts.get(script.getName());
                            if (data == null) {
                                logger.info("no have " + script.getName());
                                continue;
                            }
                            ctx.write(Unpooled.wrappedBuffer(new byte[]{REQUEST_SCRIPTS}));
                            ctx.flush();
                            ByteBuf buf = Unpooled.directBuffer(data.length);
                            buf.writeBytes(data);
                            ctx.write(buf);
                            logger.info("Sent: " + script.getName());
                        }
                        int count;
                        if (instances.get(hnnf(ctx)) == null)
                            count = 0;
                        else
                            count = instances.get(hnnf(ctx));
                        Account.UserGroup group = account.getUserGroup();
                        logger.info(count + " count - allowed " + group.getMaximumInstances());
                        if (count < group.getMaximumInstances()) {
                            instances.put(hnnf(ctx), (count += 1));
                            ctx.writeAndFlush(Unpooled.wrappedBuffer(new byte[]{INSTANCE_COUNT, 1}));
                        } else {
                            ctx.writeAndFlush(Unpooled.wrappedBuffer(new byte[]{INSTANCE_COUNT, 0}));
                            fuck = false;
                        }
                    }
                } else {
                    logger.info("Failed to log " + username + " into account!");
                }
                break;
            }
            case CLOSED_BOT: {
                StringBuilder builder = new StringBuilder();
                int ulength = buffer.readInt();
                for (int i = 0; i < ulength; i++) {
                    logger.info(builder.toString());
                    builder.append(buffer.readChar());
                }
                String name = builder.toString();

                Session session = Application.factory().openSession();
                Account account = (Account) session.createQuery("from Account where username=:username")
                        .setParameter("username", name).uniqueResult();
                if (account == null) {
                    return;
                }
                if (instances.containsKey(hnnf(ctx))) {
                    int count = instances.get(hnnf(ctx));
                    if (count == 1) {
                        instances.remove(hnnf(ctx));
                    } else {
                        instances.put(hnnf(ctx), count - 1);
                    }
                }
                break;
            }
        }
    }

}
