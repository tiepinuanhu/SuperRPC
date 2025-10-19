package org.wxc.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @author wangxinchao
 * @date 2025/10/18 14:48
 */
public interface ServiceRegistry {
    void registerService(String rpcServiceName, InetSocketAddress address);


    void clearAll();
}
