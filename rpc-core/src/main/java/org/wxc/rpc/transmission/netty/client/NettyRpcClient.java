package org.wxc.rpc.transmission.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyRpcClient implements RPCClient {


    private static final Bootstrap bootstrap;

    private static final int CONNECT_TIMEOUT_MILLIS = 5000;


    private final ServiceDiscovery serviceDiscovery;


    private final ChannelPool channelPool;


    public NettyRpcClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
        channelPool = SingletonFactory.getInstance(ChannelPool.class);
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
                protected void initChannel(NioSocketChannel ch) {
                    ch.pipeline().addLast(new IdleStateHandler(0, 5,
                            0, TimeUnit.SECONDS));
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
        CompletableFuture<RpcResponse<?>> future = new CompletableFuture<>();
        UnprocessedRpcReq.put(rpcRequest.getRequestId(), future);

        InetSocketAddress address = serviceDiscovery.lookupService(rpcRequest);
        // 使用channelPool获取连接，这样客户端在Channel没有关闭的情况下，可以复用Channel
        // 不用每发一次请求都去建立连接，关闭连接
        Channel channel = channelPool.get(address, () -> connect(address));

        log.info("连接到{}", address);

        RpcMsg rpcMsg = RpcMsg.builder()
                .msgType(MsgType.RPC_REQ)
                .versionType(VersionType.VERSION_1)
                .serializeType(SerializeType.KRYO)
                .compressType(CompressType.GZIP)
                .data(rpcRequest)
                .build();
        // 发送请求, 如果发送失败，则关闭连接
        channel.writeAndFlush(rpcMsg)
            .addListener((ChannelFutureListener) listener ->{
                if (!listener.isSuccess()) {
                    listener.channel().close();
                    future.completeExceptionally(listener.cause());
                }
            });
        return future.get();
    }

    private Channel connect(InetSocketAddress address) {
        try {
            return bootstrap.connect(address).sync().channel();
        } catch (InterruptedException e) {
            log.debug("连接失败");
            throw new RuntimeException(e);
        }
    }
}