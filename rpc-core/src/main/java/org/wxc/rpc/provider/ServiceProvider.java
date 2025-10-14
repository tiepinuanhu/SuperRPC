package org.wxc.rpc.provider;

import org.wxc.rpc.config.RPCServiceConfig;

/**
 * 可以发布服务和获取服务
 * @author wangxinchao
 * @date 2025/10/14 14:26
 */
public interface ServiceProvider {


    /**
     * 发布服务
     * @param rpcServiceConfig
     */
    void publishService(RPCServiceConfig rpcServiceConfig);




    /**
     * 根据服务名获取服务（接口的实现类）
     * 服务名由服务接口实现类的全类名+version+group组成
     * @param serviceName
     * @return
     */
    Object getService(String serviceName);

}
