package org.wxc.rpc.provider.Impl;

import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import org.wxc.rpc.config.RPCServiceConfig;
import org.wxc.rpc.constant.RpcConstant;
import org.wxc.rpc.factory.SingletonFactory;
import org.wxc.rpc.provider.ServiceProvider;
import org.wxc.rpc.registry.Impl.ZKServiceRegistry;
import org.wxc.rpc.registry.ServiceRegistry;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangxinchao
 * @date 2025/10/18 16:33
 */
public class ZKServiceProvider implements ServiceProvider {
    private final Map<String, Object> SERVICE_CACHE = new HashMap<>();

    private final ServiceRegistry serviceRegistry;

    public ZKServiceProvider() {
        this(SingletonFactory.getInstance(ZKServiceRegistry.class));
    }

    public ZKServiceProvider(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void publishService(RPCServiceConfig rpcServiceConfig) {
        rpcServiceConfig.rpcServiceNames().forEach(rpcServiceName -> {
                publishService(rpcServiceName, rpcServiceConfig.getService());

        });
    }
    @SneakyThrows
    private void publishService(String rpcServiceName, Object service) {
        SERVICE_CACHE.put(rpcServiceName, service);
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        int serverPort = RpcConstant.SERVER_PORT;
        InetSocketAddress address = new InetSocketAddress(hostAddress, serverPort);
        // 注册到zookeeper
        serviceRegistry.registerService(rpcServiceName, address);

        SERVICE_CACHE.put(rpcServiceName, service);
    }

    @Override
    public Object getService(String serviceName) {
        if (StrUtil.isBlank(serviceName)) {
            throw new IllegalArgumentException("服务名不能为空");
        }
        if (!SERVICE_CACHE.containsKey(serviceName)) {
            throw new IllegalArgumentException("找不到该服务: " + serviceName);
        }
        return SERVICE_CACHE.get(serviceName);
    }
}
