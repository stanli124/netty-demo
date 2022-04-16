package cn.itcast.server.handler;

import cn.itcast.message.GroupJoinResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroupJoinResponseHandler extends SimpleChannelInboundHandler<GroupJoinResponseMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinResponseMessage msg) throws Exception {

      log.debug(String.valueOf(msg));

    }
}
