package cn.itcast.server;

import cn.itcast.message.QuitRequestMessage;
import cn.itcast.protocol.MessageCodecSharable;
import cn.itcast.protocol.ProcotolFrameDecoder;
import cn.itcast.server.handler.*;
import cn.itcast.server.session.SessionFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup(); //处理客户端连接请求，线程组
        NioEventLoopGroup worker = new NioEventLoopGroup(); //处理客户端的读写请求，线程组
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable(); //消息编解码器
        loginResponseHandler LOGIN_HANDLER = new loginResponseHandler();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 10);
            serverBootstrap.channel(NioServerSocketChannel.class); //添加服务器端的sockt实现
            serverBootstrap.group(boss, worker); //创建线程池和selector
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());  //入站,处理粘包、半包的情况
//                    ch.pipeline().addLast(LOGGING_HANDLER);   //入；打印日志信息
                    ch.pipeline().addLast(MESSAGE_CODEC);   //入站、出站；对入站信息解码，对出站信息编码
                    ch.pipeline().addLast(LOGIN_HANDLER);  //入站，处理登录消息。
                    //入站，处理单聊消息
                    ch.pipeline().addLast(new ChatRequestHandler());
                    //入站，处理群聊创建请求
                    ch.pipeline().addLast(new GroupCreateRequestHandler());
                    //入站，处理群发消息请求
                    ch.pipeline().addLast(new GroupChatRequestHandler());
                    //入站，处理加群消息
                    ch.pipeline().addLast(new GroupJoinRequestHandler());
                    //入站，处理退群消息
                    ch.pipeline().addLast(new GroupQuitRequestHandler());
                    //入站，处理查看群聊成员请求
                    ch.pipeline().addLast(new GroupMembersRequestHandler());
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            SessionFactory.getSession().unbind(ctx.channel());
//                            ctx.channel().close();
                            log.debug("{} 已经断开", ctx.channel());
                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            SessionFactory.getSession().unbind(ctx.channel());
                            log.debug("{} 已经异常断开 异常是{}", ctx.channel(), cause.getMessage());
                        }
                    });
                }
            });
            Channel channel = serverBootstrap.bind(8080).sync().channel();
            channel.closeFuture().sync();//closeFuture用来处理channel的关闭，sync则是同步等待channel的关闭；可以通过addListener的方式异步关闭
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
