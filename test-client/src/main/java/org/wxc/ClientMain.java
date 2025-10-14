package org.wxc;

import org.wxc.rpc.dto.RPCRequest;
import org.wxc.rpc.dto.RPCResponse;
import org.wxc.rpc.transmission.RPCClient;
import org.wxc.rpc.transmission.socket.client.SocketRPCClient;
import org.wxc.rpc.util.ThreadPoolUtils;

import java.util.concurrent.ExecutorService;

/**
 * Hello world!
 *
 */
public class ClientMain {
    public static void main( String[] args) {
        // 客户端没有导入服务端的依赖（也不应该导入）
        // 所以无法直接调用getUser方法
        RPCClient rpcClient = new SocketRPCClient("127.0.0.1", 8888);
        RPCRequest request = RPCRequest.builder()
                .interfaceName("org.wxc.api.UserService")
                .methodName("getUser")
                .parameters(new Object[]{1L})
                .parameterTypes(new Class[]{Long.class})
                .build();
        ExecutorService pool = ThreadPoolUtils.createIoIntensiveThreadPool("rpc-client-");
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            pool.submit(() -> {
                RPCResponse<?> response = rpcClient.send(request);
                System.out.println("data-" + finalI + ": " + response.getData());
            });

        }
    }


    public <T> T invoke(Long id) {
        RPCClient rpcClient = new SocketRPCClient("127.0.0.1", 8888);
        RPCRequest request = RPCRequest.builder()
                .interfaceName("org.wxc.api.UserService")
                .methodName("getUser")
                .parameters(new Object[]{id})
                .parameterTypes(new Class[]{Long.class})
                .build();
        RPCResponse<?> response = rpcClient.send(request);
        return (T) response.getData();
    }
}
