package org.wxc.rpc.registry;

import org.wxc.rpc.dto.RPCRequest;

import java.net.InetSocketAddress;

/**
 * @author wangxinchao
 * @date 2025/10/18 14:49
 */
public interface ServiceDiscovery {
    /**
     * 根据服务名称查找服务
     * @param request
     * @return
     */
    InetSocketAddress lookupService(RPCRequest request);
}
