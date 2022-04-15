package cn.itcast.server.handler;

import cn.itcast.message.ChatRequestMessage;
import cn.itcast.message.ChatResponseMessage;
import cn.itcast.server.service.UserServiceFactory;
import cn.itcast.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatRequestHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
//        System.out.println("入站");
        String toUser = msg.getTo(); //接收消息用户
        String fromUser = msg.getFrom(); //发送消息的用户
        //获得目的用户的channel
        Channel channel = SessionFactory.getSession().getChannel(toUser);
        boolean toUserExist = UserServiceFactory.getUserService().userExist(toUser);

        if (channel!=null){
            channel.writeAndFlush(new ChatResponseMessage(fromUser, msg.getContent()));
        }else {
            ChatResponseMessage message;
            if (toUserExist){
                message = new ChatResponseMessage(false, ""+toUser+"不在线");
            }else {
                message = new ChatResponseMessage(false, "" + toUser + "不存在");
            }
            ctx.writeAndFlush(message);
        }
    }
}
