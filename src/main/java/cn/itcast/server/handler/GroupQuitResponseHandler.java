package cn.itcast.server.handler;

import cn.itcast.message.GroupQuitResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroupQuitResponseHandler extends SimpleChannelInboundHandler<GroupQuitResponseMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitResponseMessage msg) throws Exception {
        System.out.println("进入响应消息");
        log.debug(String.valueOf(msg));
    }
}
