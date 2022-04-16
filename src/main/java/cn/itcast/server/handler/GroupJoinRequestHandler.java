package cn.itcast.server.handler;


import cn.itcast.message.GroupChatRequestMessage;
import cn.itcast.message.GroupChatResponseMessage;
import cn.itcast.message.GroupJoinRequestMessage;
import cn.itcast.message.GroupJoinResponseMessage;
import cn.itcast.server.session.GroupSession;
import cn.itcast.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 用来处理加入群聊的请求
 * --取得群聊名，没有对应群聊就返回错误消息
 * --取得用户名
 * --拿到群聊对应的session，将用户加入
 * --不能重复加入
 */
public class GroupJoinRequestHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        String from = msg.getUsername(); //被邀请人
//        String name = msg.getName();//邀请人

        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        boolean existGroup = groupSession.isExistGroup(groupName);
        if (existGroup){
            if (groupSession.getMembers(groupName).add(from)){
                ctx.writeAndFlush(new GroupJoinResponseMessage(true, "加入群聊" + groupName + "成功"));
            }else {
                ctx.writeAndFlush(new GroupJoinResponseMessage(false, groupName+"群聊不不能重复加入"));

            }
        }else {
            ctx.writeAndFlush(new GroupJoinResponseMessage(false, groupName+"群聊不存在"));
        }

    }
}
