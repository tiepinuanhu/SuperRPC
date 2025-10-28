package org.wxc.rpc.transmission.netty.client;

import cn.hutool.json.JSONUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.wxc.rpc.constant.RpcConstant;
import org.wxc.rpc.dto.RpcRequest;
import org.wxc.rpc.dto.RpcResponse;
import org.wxc.rpc.transmission.RPCClient;

@Slf4j
public class NettyRpcClient implements RPCClient {

    private static final Bootstrap bootstrap;

    private static final int CONNECT_TIMEOUT_MILLIS = 5000;

    // 初始化客户端启动器BootStrap
    static {
        bootstrap = new Bootstrap()
            .group(new NioEventLoopGroup())
            .channel(NioSocketChannel.class)
            .handler(new LoggingHandler(LogLevel.INFO))
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MILLIS)
            .handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new StringEncoder());
                    ch.pipeline().addLast(new StringDecoder());
                    ch.pipeline().addLast(new NettyRpcClientHandler());

                }
            });
    }

    /**
     * 发送RPC请求
     * @param request
     * @return
     */
    @SneakyThrows
    @Override
    public RpcResponse<?> send(RpcRequest request) {
        // 与客户端建立连接
        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", RpcConstant.SERVER_PORT).sync();

        log.info("连接到xxxxxxxxxxxxxx");
        // 获取channel
        Channel channel = channelFuture.channel();

        // 发送请求, 如果发送失败，则关闭连接
        channel.writeAndFlush(request.getInterfaceName())
                .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        // 关闭连接
        channel.closeFuture().sync();
        // 获取服务端返回的数据
        AttributeKey<String> key
                = AttributeKey.valueOf(RpcConstant.NETTY_RPC_KEY);
        String s = channel.attr(key).get();
        System.out.println(s);
        return null;
    }
}