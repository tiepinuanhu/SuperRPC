package org.wxc.rpc.proxy;

import cn.hutool.core.util.IdUtil;
import org.wxc.rpc.config.RPCServiceConfig;
import org.wxc.rpc.dto.RPCRequest;
import org.wxc.rpc.dto.RPCResponse;
import org.wxc.rpc.enums.RPCRespStatus;
import org.wxc.rpc.exception.RPCException;
import org.wxc.rpc.transmission.RPCClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * @author wangxinchao
 * @date 2025/10/16 13:19
 */
public class PRCClientProxy implements InvocationHandler {


    // 不显示传入被代理的对象，
    // 在客户端，调不到实际的被代理对象
    // 只是在代理逻辑中发送RPC请求


    private final RPCClient rpcClient;

    private final RPCServiceConfig config;


    public PRCClientProxy(RPCClient rpcClient) {
        this(rpcClient, new RPCServiceConfig());
    }

    public PRCClientProxy(RPCClient rpcClient, RPCServiceConfig config) {
        this.rpcClient = rpcClient;
        this.config = config;
    }

    /**
     * 代理对象调用方法时，会调用此方法
     * @param proxy 代理对象
     * @param method 代理对象调用的方法
     * @param args 方法传入的实参
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("调用方法前");

        RPCRequest request = RPCRequest.builder()
                .requestId(IdUtil.fastSimpleUUID())
                // 通过方法获取对应的接口的全类名
                .interfaceName(method.getDeclaringClass().getCanonicalName())
                .methodName(method.getName())
                .parameters(args)
                .parameterTypes(method.getParameterTypes())
                .group(config.getGroup())
                .version(config.getVersion())
                .build();
        // 这里并没有调用method，因为客户端没办法创建一个被代理的对象
        // 所以只是根据method的信息，发送RPC请求
        RPCResponse<?> response = rpcClient.send(request);
        check(request, response);
        Object data = response.getData();
        return data;
    }

    /**
     * 生成代理对象
     * @param clazz 接口
     * @return
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        // 创建一个代理对象
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                this);
    }


    private void check(RPCRequest rpcRequest, RPCResponse<?> rpcResponse) {
        if (Objects.isNull(rpcResponse)) {
            throw new RPCException("Response is null");
        }

        // 检查响应的id和请求的id是否一致
        if (!Objects.equals(rpcResponse.getRequestId(),
                rpcRequest.getRequestId())) {
            throw new RPCException("Response id is not equal to request id");
        }
        if (RPCRespStatus.isFail(rpcResponse.getCode())) {
            throw new RPCException("响应值失败" + rpcResponse.getMessage());
        }
    }

}
