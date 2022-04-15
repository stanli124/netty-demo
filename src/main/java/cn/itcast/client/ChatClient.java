package cn.itcast.client;

import cn.itcast.protocol.MessageCodecSharable;
import cn.itcast.protocol.ProcotolFrameDecoder;
import cn.itcast.server.handler.ChatResponseHandler;
import cn.itcast.server.handler.GroupChatResponseHandler;
import cn.itcast.server.handler.GroupCreateResponseHandler;
import cn.itcast.server.handler.loginRequestHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup(); //创建线程组，来处理channel上的事件
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG); //打印日志
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable(); //自定义的编码解码协议
        loginRequestHandler LOGIN_REQUEST = new loginRequestHandler();
        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);   //用来控制多个线程之间的同步
        AtomicBoolean Log = new AtomicBoolean(false);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class); //绑定channel
            bootstrap.group(group);   //绑定线程组
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    //入站
                    ch.pipeline().addLast(new ProcotolFrameDecoder()); //用netty工具处理粘包、半包
//                    ch.pipeline().addLast(LOGGING_HANDLER);
                    //入站、出战
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    //入站
                    ch.pipeline().addLast(LOGIN_REQUEST);
                    //入站，处理其它用户发送的单聊消息
                    ch.pipeline().addLast(new ChatResponseHandler());
                    //入站，处理群聊创建请求的响应
                    ch.pipeline().addLast(new GroupCreateResponseHandler());
                    //入站，处理群聊消息
                    ch.pipeline().addLast(new GroupChatResponseHandler());
                }
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            //建立连接且登录后，服务器发送一个菜单，让客户端选择进行哪一项操作

            channel.closeFuture().sync(); //同步的等待关闭，在关闭之前不会向后面的代码继续执行
            log.debug("退出聊天室");
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
