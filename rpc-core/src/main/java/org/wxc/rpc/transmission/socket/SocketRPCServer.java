package org.wxc.rpc.transmission.socket;

import lombok.extern.slf4j.Slf4j;
import org.wxc.rpc.dto.RPCRequest;
import org.wxc.rpc.transmission.RPCServer;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 使用socket实现RPC服务端
 * @author wangxinchao
 * @date 2025/10/14 11:35
 */
@Slf4j
public class SocketRPCServer implements RPCServer {
    @Override
    public void start() {
        // 服务端绑定端口8888
        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            Socket socket;
            // 如果接收到了请求（socket连接）
            while ((socket = serverSocket.accept()) != null) {
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                RPCRequest request = (RPCRequest)objectInputStream.readObject();

            }
        } catch (Exception e) {
            log.error("Socket RPC Server error");
        }
    }
}
