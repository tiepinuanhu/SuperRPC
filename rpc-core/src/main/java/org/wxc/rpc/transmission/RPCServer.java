package org.wxc.rpc.transmission;

import org.wxc.rpc.config.RPCServiceConfig;

/**
 * RPC服务端, 实现RPC服务端的只需要启动即可
 * 所执行的操作就是从socket获取Req，
 * 然后调用对应的接口实现，
 * 最后通过socket返回结果
 * @author wangxinchao
 * @date 2025/10/13 21:49
 */
public interface RPCServer {
    void start();


    void publishService(RPCServiceConfig config);
}
