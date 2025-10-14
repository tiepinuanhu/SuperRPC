package org.wxc.rpc.transmission.socket;

import lombok.extern.slf4j.Slf4j;
import org.wxc.rpc.config.RPCServiceConfig;
import org.wxc.rpc.dto.RPCRequest;
import org.wxc.rpc.dto.RPCResponse;
import org.wxc.rpc.provider.Impl.SimpleServiceProvider;
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


    // 用于注册服务和获取服务
    private final SimpleServiceProvider serviceProvider;

    public SocketRPCServer(int port) {
        this(port, new SimpleServiceProvider());

    }

    public SocketRPCServer(int port, SimpleServiceProvider serviceProvider) {
        this.port = port;
        this.serviceProvider = serviceProvider;
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
                Object data = invoke(request);
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


    /**
     * 根据传入的request调用对应的接口实现类
     * 有可能实现类中没有该方法（方法签名）所以会抛出NoSuchMethodException
     * 也有可能，方法的权限是private，无法直接调用，
     * 所以会抛出IllegalAccessException
     * @param request
     * @return
     */
    private Object invoke(RPCRequest request) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String serviceName = request.rpcServiceName();
        Object service = serviceProvider.getService(serviceName);
        log.debug("获取到的服务：{}", service.getClass().getCanonicalName());
        Method method = service.getClass().getMethod(request.getMethodName(),
                request.getParameterTypes());
        return method.invoke(service, request.getParameters());
    }
}
