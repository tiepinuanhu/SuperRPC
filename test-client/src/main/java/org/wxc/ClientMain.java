package org.wxc;

import org.wxc.rpc.dto.RPCRequest;
import org.wxc.rpc.dto.RPCResponse;
import org.wxc.rpc.transmission.RPCClient;
import org.wxc.rpc.transmission.socket.SocketRPCClient;

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
        RPCResponse<?> response = rpcClient.send(request);
        System.out.println("response.getData() = " + response.getData());
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
