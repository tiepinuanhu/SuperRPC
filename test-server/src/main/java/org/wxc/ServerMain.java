package org.wxc;

import org.wxc.rpc.config.RPCServiceConfig;
import org.wxc.rpc.transmission.RPCServer;
import org.wxc.rpc.transmission.socket.server.SocketRPCServer;
import org.wxc.service.UserServiceImpl;


public class ServerMain {
    public static void main( String[] args ) {
        RPCServer rpcServer = new SocketRPCServer(8888);

        // 发布服务：UserServiceImpl
        UserServiceImpl userService = new UserServiceImpl();
        RPCServiceConfig rpcServiceConfig = new RPCServiceConfig(userService);
        rpcServer.publishService(rpcServiceConfig);
        // 启动服务
        rpcServer.start();
    }
}
