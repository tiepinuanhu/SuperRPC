package org.wxc.rpc.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RPCServiceConfig唯一描述了一个接口的实现类
 * 可以使用RPCServiceConfig查询服务
 *
 * 根据接口的全类名和version，group可以唯一确定一个服务（接口的一个实现）
 * @author wangxinchao
 * @date 2025/10/14 14:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RPCServiceConfig {
    private String version = "";
    private String group = "";

    private Object service;

    public RPCServiceConfig(Object service) {
        this.service = service;
    }

    /**
     * 获取RPC服务名称列表
     *
     * @return RPC服务名称列表，每个名称格式为"接口名#版本号#分组名"
     */
    public List<String> rpcServiceNames() {
        // 将接口名称转换为完整的RPC服务名称格式
        return interfaceNames().stream()
                .map(interfaceName -> interfaceName + "#" + getVersion() + "#" + getGroup())
                .collect(Collectors.toList());
    }



    /**
     * 获取服务实现的所有接口名称列表
     * 一个服务可以实现多个接口，此方法用于获取当前服务实例所实现的全部接口的规范化名称
     * @return 包含所有实现接口规范化名称的字符串列表
     */
    private List<String> interfaceNames() {
        // 获取服务类实现的所有接口，转换为规范化名称后收集为列表
        return Arrays.stream(service.getClass().getInterfaces())
                .map(Class::getCanonicalName)
                .collect(Collectors.toList());
    }



}
