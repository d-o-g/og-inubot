package me.septron.net.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;

import java.nio.charset.CharacterCodingException;

/**
 * @author Septron
 * @since June 21, 2015
 */
public class LoginHandler extends ChannelHandlerAdapter {

    private String username, password;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buffer = (ByteBuf) msg;
        try {
            username = CharsetUtil.getDecoder(CharsetUtil.UTF_8).decode(buffer.nioBuffer()).toString();
            password = CharsetUtil.getDecoder(CharsetUtil.UTF_8).decode(buffer.nioBuffer()).toString();
        } catch (CharacterCodingException e) {
            e.printStackTrace();
        }
    }
}
