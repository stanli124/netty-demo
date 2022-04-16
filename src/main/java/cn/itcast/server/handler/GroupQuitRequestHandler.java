package cn.itcast.server.handler;

import cn.itcast.message.GroupQuitRequestMessage;
import cn.itcast.message.GroupQuitResponseMessage;
import cn.itcast.server.session.Group;
import cn.itcast.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroupQuitRequestHandler extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitRequestMessage msg) throws Exception {
        //若组存在，则移除成员
        Group group = GroupSessionFactory.getGroupSession().removeMember(msg.getGroupName(), msg.getUsername());
//        log.debug(String.valueOf(msg));
        if (group!=null){
            ctx.writeAndFlush(new GroupQuitResponseMessage(true, msg.getUsername())+"已退出群聊: "+msg.getUsername());
        }else {
            ctx.writeAndFlush(new GroupQuitResponseMessage(true, msg.getUsername()) + "群聊不存在");
        }
    }
}
