package org.wxc.rpc.registry.Impl;

import cn.hutool.core.util.StrUtil;
import org.wxc.rpc.constant.RpcConstant;
import org.wxc.rpc.dto.RpcRequest;
import org.wxc.rpc.factory.SingletonFactory;
import org.wxc.rpc.loadbalance.Impl.RandomLoadBalance;
import org.wxc.rpc.loadbalance.LoadBalance;
import org.wxc.rpc.registry.ServiceDiscovery;
import org.wxc.rpc.registry.zk.ZkClient;
import org.wxc.rpc.util.IPUtils;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author wangxinchao
 * @date 2025/10/18 16:21
 */
public class ZKServiceDiscovery implements ServiceDiscovery {
    private final ZkClient zkClient;


    private final LoadBalance loadBalance;

    public ZKServiceDiscovery() {
        this(SingletonFactory.getInstance(ZkClient.class),
                SingletonFactory.getInstance(RandomLoadBalance.class));
    }

    public ZKServiceDiscovery(ZkClient zkClient, LoadBalance loadBalance) {
        this.zkClient = zkClient;
        this.loadBalance = loadBalance;
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest request) {
        String servicePath = RpcConstant.ZK_ROOT_PATH + StrUtil.SLASH + request.rpcServiceName();
        List<String> children = zkClient.getChildren(servicePath);
        String addr = loadBalance.select(children);
        InetSocketAddress address = IPUtils.IpPortToInet(addr);
        return address;
    }
}
