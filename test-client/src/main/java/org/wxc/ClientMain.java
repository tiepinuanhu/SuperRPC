package org.wxc;

import org.wxc.api.User;
import org.wxc.api.UserService;
import org.wxc.rpc.dto.RPCRequest;
import org.wxc.rpc.dto.RPCResponse;
import org.wxc.rpc.proxy.PRCClientProxy;
import org.wxc.rpc.transmission.RPCClient;
import org.wxc.rpc.transmission.socket.client.SocketRPCClient;
import org.wxc.utils.ProxyUtils;

import java.lang.reflect.Proxy;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hello world!
 *
 */
public class ClientMain {
    public static void main( String[] args) {
        UserService userService = ProxyUtils.getProxy(UserService.class);
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            pool.execute(() -> {
                User user = userService.getUser(1L);
                System.out.println("user = " + user);
            });
        }
    }
}
