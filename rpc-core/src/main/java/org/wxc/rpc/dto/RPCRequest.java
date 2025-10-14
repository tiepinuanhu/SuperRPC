package org.wxc.rpc.dto;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 客户端发送RPC请求的DTO对象
 * 将一次方法调用所需的信息封装成DTO对象
 * @author wangxinchao
 * @date 2025/10/13 21:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RPCRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    // 请求ID
    private String requestId;
    // 标记接口的版本, UserService可能有多个版本的实现类，
    // 例如UserServiceImpl1、UserServiceImpl2
    private String version;
    // 标记接口的实现类型，UserService可能有多个实现类，
    // 例如CommonUserServiceImpl、AdminUserServiceImpl
    private String group;

    // 服务名称(接口名称，例如org.wxc.service.UserService + version + group)
    private String interfaceName;
    // 接口的方法相关信息，便于反射调用
    // 方法名称
    private String methodName;
    // 参数类型
    private Class<?>[] parameterTypes;
    // 参数列表
    private Object[] parameters;



    public String rpcServiceName() {
        return interfaceName + "#"
                + StrUtil.blankToDefault(version, StrUtil.EMPTY) + "#"
                + StrUtil.blankToDefault(group, StrUtil.EMPTY);
    }
}
