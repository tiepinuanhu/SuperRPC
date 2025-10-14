package org.wxc;

import org.wxc.rpc.transmission.RPCServer;
import org.wxc.rpc.transmission.socket.SocketRPCServer;


public class App {
    public static void main( String[] args ) {
        RPCServer rpcServer = new SocketRPCServer(8888);
        rpcServer.start();
    }
}
