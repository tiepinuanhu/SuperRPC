package org.wxc.rpc.handler;

import lombok.extern.slf4j.Slf4j;
import org.wxc.rpc.dto.RpcRequest;
import org.wxc.rpc.provider.ServiceProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 专门处理RPC请求的handler
 * @author wangxinchao
 * @date 2025/10/14 16:19
 */
@Slf4j
public class RPCReqHandler {
    private final ServiceProvider serviceProvider;


    public RPCReqHandler(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }


    /**
     * 根据传入的request调用对应的接口实现类
     * 有可能实现类中没有该方法（方法签名）所以会抛出NoSuchMethodException
     * 也有可能，方法的权限是private，无法直接调用，
     * 所以会抛出IllegalAccessException
     * @param request
     * @return
     */
    public Object invoke(RpcRequest request) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        String serviceName = request.rpcServiceName();
        Object service = serviceProvider.getService(serviceName);
        log.debug("获取到的服务：{}", service.getClass().getCanonicalName());
        Method method = service.getClass().getMethod(request.getMethodName(),
                request.getParameterTypes());
        return method.invoke(service, request.getParameters());
    }
}
