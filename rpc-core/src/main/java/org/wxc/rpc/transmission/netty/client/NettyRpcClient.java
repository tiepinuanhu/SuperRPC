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
import org.wxc.rpc.dto.RpcMsg;
import org.wxc.rpc.dto.RpcRequest;
import org.wxc.rpc.dto.RpcResponse;
import org.wxc.rpc.enums.CompressType;
import org.wxc.rpc.enums.MsgType;
import org.wxc.rpc.enums.SerializeType;
import org.wxc.rpc.enums.VersionType;
import org.wxc.rpc.factory.SingletonFactory;
import org.wxc.rpc.registry.Impl.ZKServiceDiscovery;
import org.wxc.rpc.registry.ServiceDiscovery;
import org.wxc.rpc.transmission.RPCClient;
import org.wxc.rpc.transmission.netty.codec.NettyRpcDecoder;
import org.wxc.rpc.transmission.netty.codec.NettyRpcEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class NettyRpcClient implements RPCClient {
    private static final AtomicInteger ID_GEN = new AtomicInteger(0);


    private static final Bootstrap bootstrap;

    private static final int CONNECT_TIMEOUT_MILLIS = 5000;


    private final ServiceDiscovery serviceDiscovery;

    public NettyRpcClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public NettyRpcClient() {
        this(SingletonFactory.getInstance(ZKServiceDiscovery.class));
    }

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
                    ch.pipeline().addLast(new NettyRpcEncoder());
                    ch.pipeline().addLast(new NettyRpcDecoder());
                    ch.pipeline().addLast(new NettyRpcClientHandler());

                }
            });
    }

    /**
     * 发送RPC请求
     * @param rpcRequest
     * @return
     */
    @SneakyThrows
    @Override
    public RpcResponse<?> send(RpcRequest rpcRequest) {


        InetSocketAddress address = serviceDiscovery.lookupService(rpcRequest);
        // 与客户端建立连接
        ChannelFuture channelFuture = bootstrap.connect(address).sync();

        log.info("连接到xxxxxxxxxxxxxx");
        // 获取channel
        Channel channel = channelFuture.channel();


        RpcMsg rpcMsg = RpcMsg.builder()
                .requestId(ID_GEN.getAndIncrement())
                .msgType(MsgType.RPC_REQ)
                .versionType(VersionType.VERSION_1)
                .serializeType(SerializeType.KRYO)
                .compressType(CompressType.GZIP)
                .data(rpcRequest)
                .build();
        // 发送请求, 如果发送失败，则关闭连接
        channel.writeAndFlush(rpcMsg)
                .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        // 关闭连接
        channel.closeFuture().sync();
        // 获取服务端返回的数据
        AttributeKey<RpcResponse> key
                = AttributeKey.valueOf(RpcConstant.NETTY_RPC_KEY);
        RpcResponse rpcResponse = channel.attr(key).get();
        return rpcResponse;
    }
}