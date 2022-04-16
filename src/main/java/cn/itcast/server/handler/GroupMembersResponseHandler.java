package cn.itcast.server.handler;

import cn.itcast.message.GroupMembersRequestMessage;
import cn.itcast.message.GroupMembersResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Set;

public class GroupMembersResponseHandler extends SimpleChannelInboundHandler<GroupMembersResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersResponseMessage msg) throws Exception {
        Set<String> members = msg.getMembers();
        if (members!=null){
            System.out.println(msg.getGroupname()+"当前群成员有:");
            for (String s : members){
                System.out.print(s+" ");
            }
        }else {
            System.out.println("当前群聊不存在");
        }

    }
}
