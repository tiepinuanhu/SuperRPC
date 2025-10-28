package org.wxc;

import org.wxc.api.User;
import org.wxc.api.UserService;
import org.wxc.rpc.dto.RpcRequest;
import org.wxc.rpc.transmission.netty.client.NettyRpcClient;
import org.wxc.utils.ProxyUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hello world!
 *
 */
public class ClientMain {
    public static void main( String[] args) {
//        UserService userService = ProxyUtils.getProxy(UserService.class);
//        ExecutorService pool = Executors.newFixedThreadPool(10);
//        for (int i = 0; i < 10; i++) {
//            pool.execute(() -> {
//                User user = userService.getUser(1L);
//                System.out.println("user = " + user);
//            });
//        }

        NettyRpcClient rpcClient = new NettyRpcClient();
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName("org.wxc.api.UserService").build();
        rpcClient.send(rpcRequest);
    }
}
