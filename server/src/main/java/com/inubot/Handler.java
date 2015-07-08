package com.inubot;

import com.inubot.model.Account;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.hibernate.Session;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Septron
 * @since July 07, 2015
 */
public class Handler extends ChannelHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(Handler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buffer = (ByteBuf) msg;

        int opcode = buffer.readByte();
        switch (opcode) {
            case 0: {
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

                if (BCrypt.checkpw(password, account.getPassword())) {
                    logger.info("Successfully logged " + username + " into account!");
                } else {
                    logger.info("Failed to log " + username + " into account!");
                }
                break;
            }
        }
    }
}
