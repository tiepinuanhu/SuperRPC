package org.wxc.rpc.provider.Impl;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.wxc.rpc.config.RPCServiceConfig;
import org.wxc.rpc.provider.ServiceProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangxinchao
 * @date 2025/10/14 14:56
 */
@Slf4j
public class SimpleServiceProvider implements ServiceProvider {


    // 服务注册表
    private final Map<String, Object> SERVICE_CACHE = new HashMap<>();

    /**
     * 注册服务
     * @param rpcServiceConfig
     */
    @Override
    public void publishService(RPCServiceConfig rpcServiceConfig) {
        List<String> rpcServiceNames = rpcServiceConfig.rpcServiceNames();

        if (CollUtil.isEmpty(rpcServiceNames)) {
            throw new RuntimeException("该服务没有实现接口");
        }
        rpcServiceNames.forEach(rpcServiceName -> {
            SERVICE_CACHE.put(rpcServiceName, rpcServiceConfig.getService());
        });
        log.debug("发布了服务：{}", rpcServiceNames);
    }

    /**
     * 获取服务
     * @param serviceName
     * @return
     */
    @Override
    public Object getService(String serviceName) {
        if (!SERVICE_CACHE.containsKey(serviceName)) {
            throw new IllegalArgumentException("找不到该服务: "+ serviceName);
        }
        return SERVICE_CACHE.get(serviceName);
    }
}
