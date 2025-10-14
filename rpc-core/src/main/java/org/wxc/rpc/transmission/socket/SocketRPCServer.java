package org.wxc.rpc.transmission.socket;

import lombok.extern.slf4j.Slf4j;
import org.wxc.rpc.dto.RPCRequest;
import org.wxc.rpc.dto.RPCResponse;
import org.wxc.rpc.transmission.RPCServer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    public SocketRPCServer(int port) {
        this.port = port;
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



                // 处理请求
                String data = "21312312312";
                // 返回结果
                RPCResponse<String> success =
                        RPCResponse.success(request.getRequestId(),  data);

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                objectOutputStream.writeObject(success);

                objectOutputStream.flush();
            }
        } catch (Exception e) {
            log.error("Socket RPC Server error");
        }
    }
}
