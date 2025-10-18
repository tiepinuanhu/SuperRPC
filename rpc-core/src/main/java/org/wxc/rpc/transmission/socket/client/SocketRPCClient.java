package org.wxc.rpc.transmission.socket.client;

import lombok.extern.slf4j.Slf4j;
import org.wxc.rpc.dto.RPCRequest;
import org.wxc.rpc.dto.RPCResponse;
import org.wxc.rpc.factory.SingletonFactory;
import org.wxc.rpc.registry.Impl.ZKServiceDiscovery;
import org.wxc.rpc.registry.ServiceDiscovery;
import org.wxc.rpc.transmission.RPCClient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * RPCClient的socket实现
 * @author wangxinchao
 * @date 2025/10/13 22:01
 */
@Slf4j
public class SocketRPCClient implements RPCClient {


    private final ServiceDiscovery serviceDiscovery;

    public SocketRPCClient() {
        this(SingletonFactory.getInstance(ZKServiceDiscovery.class));
    }

    public SocketRPCClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }


    /**
     * 创建socket，发送请求，获取返回结果
     * @param request
     * @return
     */
    @Override
    public RPCResponse<?> send(RPCRequest request) {
        InetSocketAddress address = serviceDiscovery.lookupService(request);
        // 与127.0.0.1:8080建立socket连接
        try (Socket socket = new Socket(address.getHostName(), address.getPort())) {
            // 发送请求
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(request);
            objectOutputStream.flush(); // 刷新缓冲区，立即发送数据
            // 获取返回结果
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            Object o = objectInputStream.readObject();
            return (RPCResponse<?>) o;
        } catch (Exception e) {
            log.error("Socket RPC Client error");
        }
        return null;
    }
}
