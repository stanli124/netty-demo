package cn.itcast.server.handler;

import cn.itcast.message.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class loginRequestHandler extends SimpleChannelInboundHandler<LoginResponseMessage> {
    CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);   //用来控制多个线程之间的同步
    AtomicBoolean Log = new AtomicBoolean(false);

//    @Override
//    //该方法在发生读事件时执行
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
////                            log.debug("{} ", msg);
//        LoginResponseMessage response = (LoginResponseMessage) msg;
//        if (response.isSuccess()){
//            Log.set(true);
//        }
////        System.out.println(Thread.currentThread());
//        WAIT_FOR_LOGIN.countDown(); //登录成功后唤醒阻塞的线程
//    }

    @Override
    //该方法在有响应消息的时候执行
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LoginResponseMessage message) throws Exception {
        System.out.println(message.isSuccess());
        if (message.isSuccess()){
            Log.set(true);
        }
//        System.out.println(Thread.currentThread());
        WAIT_FOR_LOGIN.countDown(); //登录成功后唤醒阻塞的线程
    }

    @Override
    //该方法在连接建立执行
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println(Thread.currentThread());
        new Thread(()->{ //这里是在nioEventLoopGroup这个线程组里面，又开了一个线程来处理用户输入
            System.out.println(Thread.currentThread());
            AtomicBoolean isQuit = new AtomicBoolean(false);
            //单独开启另一个线程，实现用户登录和向服务器发送各种消息
            Scanner scan = new Scanner(System.in);
            System.out.println("请输入用户名：");
            String name = scan.nextLine();
            System.out.println("请输入密码：");
            String pass = scan.nextLine();
            //构造登录消息，并发往服务端
            LoginRequestMessage login = new LoginRequestMessage(name, pass);
            ctx.writeAndFlush(login);

            System.out.println("已发送登录请求，等待服务器响应......");

            try {
                WAIT_FOR_LOGIN.await(); //在等待服务器响应的时候，阻塞当前线程继续向下执行
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!Log.get()){
                //
                System.out.println("进入Log.get");
                ctx.channel().close();
                return;
            }

            while (true){
                if (isQuit.get()){
                    break;
                }
                System.out.println("=============================菜单==============================");
                System.out.println("send [username] [content]");
                System.out.println("gsend [group name] [content]");
                System.out.println("gcreate [group name] [m1,m2,m3...]");
                System.out.println("gmembers [groupname]");
                System.out.println("gjoin [group name]");
                System.out.println("gquit [group name]");
                System.out.println("quit");
                System.out.println("==============================================================");

                String command = scan.nextLine();
                String[] strs = command.split(" ");
//                System.out.println(strs);
                switch (strs[0]){
                    case "send":  //发送单聊消息
                        ctx.writeAndFlush(new ChatRequestMessage(name, strs[1], strs[2]));  //发送单聊消息
                        break;
                    case "gsend":  //发送群聊消息
                        ctx.writeAndFlush(new GroupChatRequestMessage(name, strs[1], strs[2]));
                        break;
                    case "gcreate":  //创建聊天室
                        Set<String> members = new HashSet<>(Arrays.asList(strs[2].split(",")));
                        members.add(name);
                        ctx.writeAndFlush(new GroupCreateRequestMessage(strs[1], members));
                        break;
                    case "gmembers": //拉人进群
                        ctx.writeAndFlush(new GroupMembersRequestMessage(strs[1]));
                        break;
                    case "gjoin":  //加入聊天室
                        ctx.writeAndFlush(new GroupJoinRequestMessage(name, strs[1]));
                        break;
                    case "gquit":  //退出聊天室
                        ctx.writeAndFlush(new GroupQuitRequestMessage(name, strs[1]));
                        break;
                    case "quit":  //退出登录
                        System.out.println("进入quit");
                        ctx.writeAndFlush(new QuitRequestMessage(name));
                        ctx.channel().close(); //关闭是异步的
                        isQuit.set(true);
                        break;
                }
            }
        }, "login-Thread").start();
    }

}
