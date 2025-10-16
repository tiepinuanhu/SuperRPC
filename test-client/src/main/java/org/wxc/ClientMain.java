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

/**
 * Hello world!
 *
 */
public class ClientMain {
    public static void main( String[] args) {
        UserService userService = ProxyUtils.getProxy(UserService.class);
        User user = userService.getUser(1L);
        System.out.println("user = " + user);
    }
}
