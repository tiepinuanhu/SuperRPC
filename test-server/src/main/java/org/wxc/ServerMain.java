package org.wxc;

import org.wxc.rpc.config.RPCServiceConfig;
import org.wxc.rpc.transmission.RPCServer;
import org.wxc.rpc.transmission.netty.server.NettyRpcServer;
import org.wxc.service.UserServiceImpl;


public class ServerMain {
    public static void main( String[] args ) {
        // 创建RPC服务端
        RPCServer rpcServer = new NettyRpcServer();

        // 发布服务：UserServiceImpl
        RPCServiceConfig rpcServiceConfig = new RPCServiceConfig(new UserServiceImpl());
        rpcServer.publishService(rpcServiceConfig);
        // 启动服务
        rpcServer.start();
    }
}
