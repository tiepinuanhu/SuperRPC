package org.wxc.rpc.util;

import lombok.extern.slf4j.Slf4j;
import org.wxc.rpc.factory.SingletonFactory;
import org.wxc.rpc.registry.Impl.ZKServiceRegistry;

/**
 * @Author Mr.Pan
 * @Date 2025/2/21
 **/
@Slf4j
public class ShutdownHookUtils {
    /**
     * 在JVM关闭时，
     * 1. 关闭所有线程
     * 2. 注销zookeeper上本机的所有服务
     */
    public static void clearAll() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("系统结束运行, 清理资源");
            ZKServiceRegistry serviceRegistry
                    = SingletonFactory.getInstance(ZKServiceRegistry.class);
            serviceRegistry.clearAll();
            ThreadPoolUtils.shutdownAll();
        }));
    }
}
