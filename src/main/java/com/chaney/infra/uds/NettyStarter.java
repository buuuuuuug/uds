package com.chaney.infra.uds;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollIoHandler;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerDomainSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.uring.IoUring;
import io.netty.channel.uring.IoUringIoHandler;
import io.netty.channel.uring.IoUringServerDomainSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultThreadFactory;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Component
public class NettyStarter implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(NettyStarter.class);

    private static final String NETTY_START_THREAD_NAME = "netty-start-thread";
    private ExecutorService nettyStartExecutor;
    private volatile Boolean started = false;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    @Override
    public void run(String... args) {
        nettyStartExecutor = Executors.newSingleThreadExecutor(r -> new Thread(r, NETTY_START_THREAD_NAME));
        nettyStartExecutor.submit(new NettyRunTask());
    }

    /**
     * netty启动方法
     */
    private void startNetty() {
        log.info("netty启动...");
        // 构建event loop group
        var serverChannelClass = initEventLoop();
        // 构建server
        try {
            ServerBootstrap serverBootstrap = buildServerBootstrap(serverChannelClass);
            ChannelFuture future = serverBootstrap.bind(new DomainSocketAddress("tmp/demo.sock"));
            future.await(10, TimeUnit.SECONDS);
            if (future.isSuccess()) {

                log.info("ready to accept connections");
            }
            this.started = true;
        } catch (Exception e) {
            log.error("netty 启动异常", e);
        }
    }

    private ServerBootstrap buildServerBootstrap(Class<? extends ServerChannel> serverChannelClass) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup).channel(serverChannelClass).option(ChannelOption.SO_BACKLOG, 128)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                        pipeline.addLast(new ProcessMessageHandler());
                    }
                });
        return serverBootstrap;
    }

    /**
     * 构造eventLoop
     */
    private Class<? extends ServerChannel> initEventLoop() {
        int bossThreadCount = 1;
        int workerThreadCount = NettyRuntime.availableProcessors() * 2;

        ThreadFactory bossThreadFactory = new DefaultThreadFactory("boss");
        ThreadFactory workerThreadFactory = new DefaultThreadFactory("worker");
//        ThreadFactory bossThreadFactory = Thread.ofVirtual().name("boss-vth-", 1).factory();
//        ThreadFactory workerThreadFactory = Thread.ofVirtual().name("work-vth-", 1).factory();
        IoHandlerFactory ioHandlerFactory;
        Class<? extends ServerChannel> serverChannelClass;
        if (IoUring.isAvailable()) {
            log.info("netty event loop group 使用 io_uring方式构建");
            ioHandlerFactory = IoUringIoHandler.newFactory();
            serverChannelClass = IoUringServerDomainSocketChannel.class;
        } else if (Epoll.isAvailable()) {
            log.info("netty event loop group 使用 epoll方式构建");
            ioHandlerFactory = EpollIoHandler.newFactory();
            serverChannelClass = EpollServerDomainSocketChannel.class;
        } else {
            log.info("netty event loop group 使用 nio方式构建");
            ioHandlerFactory = NioIoHandler.newFactory();
            serverChannelClass = NioServerDomainSocketChannel.class;
        }
        bossGroup = new MultiThreadIoEventLoopGroup(bossThreadCount, bossThreadFactory, ioHandlerFactory);
        workerGroup = new MultiThreadIoEventLoopGroup(workerThreadCount, workerThreadFactory, ioHandlerFactory);
        return serverChannelClass;
    }

    /**
     * spring容器关闭回调
     */
    @PreDestroy
    public void stop() {
        // 关闭netty
        log.info("开始关闭netty..");
        // 默认最长15s
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }

        // 关闭其他资源
        if (nettyStartExecutor != null) {
            nettyStartExecutor.close();
        }
    }

    /**
     * netty启动任务
     */
    public class NettyRunTask implements Runnable {

        @Override
        public void run() {
            startNetty();
        }
    }
}
