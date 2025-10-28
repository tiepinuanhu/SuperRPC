package org.wxc;

import org.wxc.api.User;
import org.wxc.api.UserService;
import org.wxc.rpc.config.RPCServiceConfig;
import org.wxc.rpc.proxy.PRCClientProxy;
import org.wxc.rpc.transmission.RPCServer;
import org.wxc.rpc.transmission.netty.server.NettyRpcServer;
import org.wxc.rpc.transmission.socket.server.SocketRPCServer;
import org.wxc.service.UserServiceImpl;


public class ServerMain {
    public static void main( String[] args ) {
//        RPCServer rpcServer = new SocketRPCServer();
//
//        // 发布服务：UserServiceImpl
//        RPCServiceConfig rpcServiceConfig = new RPCServiceConfig(new UserServiceImpl());
//        rpcServer.publishService(rpcServiceConfig);
//        // 启动服务
//        rpcServer.start();

        NettyRpcServer nettyRpcServer = new NettyRpcServer();
        nettyRpcServer.start();

    }
}
