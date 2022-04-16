package cn.itcast.server.handler;

import cn.itcast.message.GroupChatRequestMessage;
import cn.itcast.message.GroupChatResponseMessage;
import cn.itcast.server.session.GroupSession;
import cn.itcast.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.Channel;

import java.util.List;

/**
 * 处理群聊消息
 */
public class GroupChatRequestHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        //取得群聊管理器
        String groupName = msg.getGroupName();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();

        //检查是否存在群聊
        boolean isExist = groupSession.isExistGroup(groupName);
        //发送用户是否存在群聊中
        boolean userIsExist = groupSession.getMembers(groupName).contains(msg.getFrom());

        //存在群聊，向所有成员发送消息
        if (isExist && userIsExist){
            List<Channel> channels = groupSession.getMembersChannel(groupName);
            for (Channel ch : channels){
                ch.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(),msg.getContent()));
            }
        }else if (!isExist){
            ctx.writeAndFlush(new GroupChatResponseMessage(false, groupName+"群聊未创建"));
        }
        else { //不存在群聊，返回发送失败消息
            ctx.writeAndFlush(new GroupChatResponseMessage(false, "未加入群聊" + groupName));
        }

    }
}
