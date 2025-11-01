package org.wxc.rpc.transmission.socket.server;

import lombok.extern.slf4j.Slf4j;
import org.wxc.rpc.config.RPCServiceConfig;
import org.wxc.rpc.constant.RpcConstant;
import org.wxc.rpc.factory.SingletonFactory;
import org.wxc.rpc.handler.RpcReqHandler;
import org.wxc.rpc.provider.Impl.ZKServiceProvider;
import org.wxc.rpc.provider.ServiceProvider;
import org.wxc.rpc.transmission.RPCServer;
import org.wxc.rpc.util.ShutdownHookUtils;
import org.wxc.rpc.util.ThreadPoolUtils;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * 使用socket实现RPC服务端
 * @author wangxinchao
 * @date 2025/10/14 11:35
 */
@Slf4j
public class SocketRpcServer implements RPCServer {


    private final int port;


    private final ServiceProvider serviceProvider;

    private final RpcReqHandler reqHandler;


    /**
     * 使用线程池处理RPC请求
     */
    private final ExecutorService pool;

    public SocketRpcServer() {
        this(RpcConstant.SERVER_PORT);
    }

    public SocketRpcServer(int port) {
        this(port, SingletonFactory.getInstance(ZKServiceProvider.class));

    }

    public SocketRpcServer(int port, ServiceProvider serviceProvider) {
        this.port = port;
        this.serviceProvider = serviceProvider;
        this.reqHandler = new RpcReqHandler(serviceProvider);
        // 初始化线程池，并起一个名字，
        // 多次获取线程池只要名字一样，则返回同一个线程池
        this.pool = ThreadPoolUtils
                .createCpuIntensiveThreadPool("socket-rpc-server-");
    }

    /**
     * 服务端执行逻辑
     * 1. 绑定ShutdownHook，执行程序结束后的清理逻辑
     */
    @Override
    public void start() {
        ShutdownHookUtils.clearAll();
        // 服务端绑定端口8888
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("Server started at {}", port);
            Socket socket;
            // 如果接收到了请求（socket连接）
            while ((socket = serverSocket.accept()) != null) {
                // 向线程池提交RPC请求，
                // socket用于获取请求数据，
                // reqHandler用于处理请求
                pool.submit(new SocketReqHandler(socket, reqHandler));
            }
        } catch (Exception e) {
            log.error("Socket RPC Server error");
        }
    }

    @Override
    public void publishService(RPCServiceConfig config) {
        serviceProvider.publishService(config);
    }




}
