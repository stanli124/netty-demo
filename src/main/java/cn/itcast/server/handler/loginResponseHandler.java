package cn.itcast.server.handler;

import cn.itcast.message.LoginRequestMessage;
import cn.itcast.message.LoginResponseMessage;
import cn.itcast.server.service.UserServiceFactory;
import cn.itcast.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 改进：没做密码校验
 */

@ChannelHandler.Sharable
@Slf4j
public class loginResponseHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                            List<Object> aa = (List<Object>) msg;
//                            LoginRequestMessage a = (LoginRequestMessage) aa.get(0);
//                            if (UserServiceFactory.getUserService().login(a.getUsername(), a.getPassword())){
//                                log.debug("{}",a);
//                                System.out.println("lpc");
//                                ctx.writeAndFlush(new LoginResponseMessage(true, "登录成功"));
//                            }else {
//                                ctx.writeAndFlush(new LoginResponseMessage(false, "登录名或密码错误"));
//                            }
//                            System.out.println(msg);


        if (msg instanceof LoginRequestMessage){  //判断是否是登录请求信息
            LoginRequestMessage message = (LoginRequestMessage) msg;
            String name = message.getUsername();
            String pass = message.getPassword();



            boolean login = UserServiceFactory.getUserService().login(name, pass);  //去内存中检查是否存在输入的用用户

            boolean toUserExist = UserServiceFactory.getUserService().userExist(name);
            LoginResponseMessage message1;
            if (toUserExist){
                //一定要把用户的username和发起连接请求的channel绑定起来,不然后面拿不到用户对应的channel
                SessionFactory.getSession().bind(ctx.channel(), name);
                message1 = new LoginResponseMessage(true, "登录成功");
            }else {
                message1 = new LoginResponseMessage(false, "用户名或密码错误");
            }
            ctx.writeAndFlush(message1);
        }else {
            log.debug("{}不是登录消息", msg);
            ctx.fireChannelRead(msg); //如果不加这句代码,那么消息就不会往后面传递,因为前面只接收了LoginRequestMessage类型消息;所以一定要调用这一句
        }
    }


}
