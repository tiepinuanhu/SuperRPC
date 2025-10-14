package org.wxc;

import org.wxc.api.User;
import org.wxc.rpc.dto.RPCRequest;
import org.wxc.rpc.dto.RPCResponse;
import org.wxc.rpc.transmission.RPCClient;

/**
 * Unit test for simple App.
 */
public class App {
    public static void main(String[] args) {
        RPCClient rpcClient = new RPCClient() {
            @Override
            public RPCResponse<?> send(RPCRequest request) {
                return null;
            }
        };



//        RPCResponse<?> response = rpcClient.send(req);
//        User user = (User)response.getData();
//        System.out.println(user);
    }


//    private static <T> T invoke(Long id) {
//        RPCClient rpcClient = null;
//
//        RPCRequest req = RPCRequest.builder()
//                .requestId("123")
//                .interfaceName("org.wxc.api.UserService")
//                .methodName("getUser")
//                .parameterTypes(new Class[]{Long.class})
//                .parameters(new Object[]{id})
//                .build();
//
//
//        RPCResponse<?> response = rpcClient.send(req);
//
//        return (T) response.getData();
//    }
}
