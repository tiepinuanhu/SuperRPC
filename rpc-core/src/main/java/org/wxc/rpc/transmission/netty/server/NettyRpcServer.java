package org.wxc.rpc.transmission.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.wxc.rpc.config.RPCServiceConfig;
import org.wxc.rpc.constant.RpcConstant;
import org.wxc.rpc.transmission.RPCServer;
import org.wxc.rpc.transmission.netty.codec.NettyRpcDecoder;
import org.wxc.rpc.transmission.netty.codec.NettyRpcEncoder;

/**
 * @author wangxinchao
 * @date 2025/10/28 17:10
 */
@Slf4j
public class NettyRpcServer implements RPCServer {

    public NettyRpcServer(ServiceProvider serviceProvider) {
        this(serviceProvider, RpcConstant.SERVER_PORT);
    }
    public NettyRpcServer(int port) {
        this(SingletonFactory.getInstance(ZKServiceProvider.class),
                port);
    }
    public NettyRpcServer() {
        this(SingletonFactory.getInstance(ZKServiceProvider.class),
                RpcConstant.SERVER_PORT);
    }


    public NettyRpcServer(ServiceProvider serviceProvider, int port) {
        this.serviceProvider = serviceProvider;
        this.port = port;
    }

    @Override
    public void start() {
        NioEventLoopGroup bossEventLoopGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerEventLoopGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossEventLoopGroup, workerEventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyRpcDecoder());
                            ch.pipeline().addLast(new NettyRpcEncoder());
                            ch.pipeline().addLast(new NettyRpcServerHandler());
                        }
                    });
            ShutdownHookUtils.clearAll();
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            log.info("服务启动成功,  端口：{}", port);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("启动服务失败");
            throw new RuntimeException(e);
        } finally {
            bossEventLoopGroup.shutdownGracefully();
            workerEventLoopGroup.shutdownGracefully();
        }
    }

    @Override
    public void publishService(RPCServiceConfig config) {

    }
}
