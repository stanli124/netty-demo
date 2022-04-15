package cn.itcast.server.handler;

import cn.itcast.message.GroupCreateResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理群聊创建请求的响应消息
 */
@Slf4j
public class GroupCreateResponseHandler extends SimpleChannelInboundHandler<GroupCreateResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateResponseMessage msg) throws Exception {

        log.debug(msg.getReason());

    }




}
