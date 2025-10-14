package org.wxc.rpc.util;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author Mr.Pan
 * @Date 2025/2/21
 **/
@Slf4j
public class ShutdownHookUtils {
    /**
     * 在JVM关闭时，关闭所有线程
     */
    public static void clearAll() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("系统结束运行, 清理资源");
//            ServiceRegistry serviceRegistry = SingletonFactory.getInstance(ZkServiceRegistry.class);
//            serviceRegistry.clearAll();
            ThreadPoolUtils.shutdownAll();
        }));
    }
}
