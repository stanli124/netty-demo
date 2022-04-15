package cn.itcast.server.handler;

import cn.itcast.message.GroupChatResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroupChatResponseHandler extends SimpleChannelInboundHandler<GroupChatResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatResponseMessage msg) throws Exception {
        if (!msg.isSuccess()){
            log.debug(String.valueOf(msg));
        }else{
            log.debug("[{}]的消息: [{}]", msg.getFrom(),msg.getContent());
        }
//        log.debug("[{}]的消息: [{}]", msg.getFrom(),msg.getContent());

    }
}
