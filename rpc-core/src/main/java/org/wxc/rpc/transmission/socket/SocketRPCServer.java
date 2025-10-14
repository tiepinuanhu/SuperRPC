package org.wxc.rpc.transmission.socket;

import lombok.extern.slf4j.Slf4j;
import org.wxc.rpc.config.RPCServiceConfig;
import org.wxc.rpc.dto.RPCRequest;
import org.wxc.rpc.dto.RPCResponse;
import org.wxc.rpc.handler.RPCReqHandler;
import org.wxc.rpc.provider.Impl.SimpleServiceProvider;
import org.wxc.rpc.provider.ServiceProvider;
import org.wxc.rpc.transmission.RPCServer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 使用socket实现RPC服务端
 * @author wangxinchao
 * @date 2025/10/14 11:35
 */
@Slf4j
public class SocketRPCServer implements RPCServer {


    private final int port;


    private final ServiceProvider serviceProvider;

    private final RPCReqHandler reqHandler;

    public SocketRPCServer(int port) {
        this(port, new SimpleServiceProvider());

    }

    public SocketRPCServer(int port, ServiceProvider serviceProvider) {
        this.port = port;
        this.serviceProvider = serviceProvider;
        this.reqHandler = new RPCReqHandler(serviceProvider);
    }

    @Override
    public void start() {
        // 服务端绑定端口8888
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("Server started at {}", port);
            Socket socket;
            // 如果接收到了请求（socket连接）
            while ((socket = serverSocket.accept()) != null) {
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                RPCRequest request = (RPCRequest)objectInputStream.readObject();
                System.out.println("request = " + request);


                // 处理请求, 反射调用
                Object data = reqHandler.invoke(request);

                log.debug("调用结果：{}", data);
                // 返回结果
                RPCResponse<Object> success =
                        RPCResponse.success(request.getRequestId(),  data);

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                objectOutputStream.writeObject(success);

                objectOutputStream.flush();
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
