package org.wxc.rpc.registry.Impl;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.wxc.rpc.constant.RpcConstant;
import org.wxc.rpc.factory.SingletonFactory;
import org.wxc.rpc.registry.ServiceRegistry;
import org.wxc.rpc.registry.zk.ZkClient;
import org.wxc.rpc.util.IPUtils;

import java.net.InetSocketAddress;

/**
 * @author wangxinchao
 * @date 2025/10/18 15:16
 */
@Slf4j
public class ZKServiceRegistry implements ServiceRegistry {

    private final ZkClient zkClient;


    public ZKServiceRegistry() {
        this(SingletonFactory.getInstance(ZkClient.class));
    }

    public ZKServiceRegistry(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    @Override
    public void registerService(String rpcServiceName,
                                InetSocketAddress address) {
        log.info("开始注册服务: rpcServiceName:{}, address:{}", rpcServiceName, address);
        String ipPort = IPUtils.InetToIpPort(address);
        String servicePath = RpcConstant.ZK_ROOT_PATH
                + StrUtil.SLASH + rpcServiceName
                + StrUtil.SLASH + ipPort;
        zkClient.createPersistentNode(servicePath);
    }

    public static void main(String[] args) {
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8888);
        System.out.println(IPUtils.InetToIpPort(address));
    }
}
