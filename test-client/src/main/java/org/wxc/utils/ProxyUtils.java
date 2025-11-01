package org.wxc.utils;

import org.wxc.rpc.factory.SingletonFactory;
import org.wxc.rpc.proxy.PRCClientProxy;
import org.wxc.rpc.transmission.netty.client.NettyRpcClient;

/**
 * @author wangxinchao
 * @date 2025/10/16 14:11
 */
public class ProxyUtils {

    private static final NettyRpcClient rpcClient =
            SingletonFactory.getInstance(NettyRpcClient.class);

    private static final PRCClientProxy proxy = new PRCClientProxy(rpcClient);
    /**
     * 提供一个接口，返回一个接口的代理对象
     * 这个代理对象，与服务端8888端口建立，发送rpc请求获取数据并返回
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> T getProxy(Class<T> clazz) {
        return proxy.getProxy(clazz);
    }
}
