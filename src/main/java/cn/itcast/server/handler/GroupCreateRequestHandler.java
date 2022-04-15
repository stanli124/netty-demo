package cn.itcast.server.handler;

import cn.itcast.message.GroupCreateRequestMessage;
import cn.itcast.message.GroupCreateResponseMessage;
import cn.itcast.server.session.GroupSession;
import cn.itcast.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;


/**
 * 服务端接收来自客户端 创建群聊请求
 * 需要改进：在创建群聊的时候 没有判断是否存在非法用户；存在非法用户的话应该创建失败
 *
 */
public class GroupCreateRequestHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage message) throws Exception {
        //取得 群聊名称和群聊组员
        String groupName = message.getGroupName();
        Set<String> members = message.getMembers();

        //获得会话当前已有聊天室管理器
        GroupSession groupSession = GroupSessionFactory.getGroupSession();

        //判断当前聊天室是否存在
        boolean existGroup = groupSession.isExistGroup(groupName);

        GroupCreateResponseMessage createResponseMessage;
        if (existGroup){
            createResponseMessage = new GroupCreateResponseMessage(false, groupName+"聊天室已存在");
        }else {
            groupSession.createGroup(groupName, members);
            createResponseMessage = new GroupCreateResponseMessage(true, groupName+"聊天室创建成功");
            //向在线成员发送入群消息
            List<Channel> channels = groupSession.getMembersChannel(groupName);
            for (Channel ch : channels){
                ch.writeAndFlush(new GroupCreateResponseMessage(true, "您已被拉入群聊: " + groupName));
            }
        }
        ctx.writeAndFlush(createResponseMessage);
    }
}
