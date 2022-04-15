package cn.itcast.server.handler;

import cn.itcast.message.ChatResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;


@Slf4j
//@ChannelHandler.Sharable
public class ChatResponseHandler extends SimpleChannelInboundHandler<ChatResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatResponseMessage msg) throws Exception {
        if (msg.isSuccess()) { //发送成功
            String fromUser = msg.getFrom();
            String content = msg.getContent();
            log.debug("[{}]: [{}]",fromUser, content);
        }else { //发送失败
            String reason = msg.getReason();
            log.debug("发送失败：{}",reason);
        }
    }


}
