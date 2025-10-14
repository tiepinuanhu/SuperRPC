package org.wxc.rpc.util;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @Author Mr.Pan
 * @Date 2025/2/21
 **/
@Slf4j
public final class ThreadPoolUtils {
    private static final Map<String, ExecutorService> THREAD_POOL_CACHE = new ConcurrentHashMap<>();
    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();
    private static final int CPU_INTENSIVE_NUM = CPU_NUM + 1;
    private static final int IO_INTENSIVE_NUM = CPU_NUM * 2;
    private static final int DEFAULT_KEEP_ALIVE_TIME = 60;
    private static final int DEFAULT_QUEUE_SIZE = 128;

    public static ExecutorService createCpuIntensiveThreadPool(String poolName) {
        return createThreadPool(CPU_INTENSIVE_NUM, poolName);
    }

    public static ExecutorService createIoIntensiveThreadPool(String poolName) {
        return createThreadPool(IO_INTENSIVE_NUM, poolName);
    }

    public static ExecutorService createThreadPool(
        int corePoolSize,
        String poolName
    ) {
        return createThreadPool(corePoolSize, corePoolSize, poolName);
    }

    public static ExecutorService createThreadPool(
        int corePoolSize,
        int maxPoolSize,
        String poolName
    ) {
        return createThreadPool(corePoolSize, maxPoolSize, DEFAULT_KEEP_ALIVE_TIME, DEFAULT_QUEUE_SIZE, poolName);
    }

    public static ExecutorService createThreadPool(
        int corePoolSize,
        int maxPoolSize,
        long keepAliveTime,
        int queueSize,
        String poolName
    ) {
        return createThreadPool(corePoolSize, maxPoolSize, keepAliveTime, queueSize, poolName, false);
    }

    /**
     * 创建线程池的方法，上面的其他方法都是重载的方法
     * @param corePoolSize 核心线程数量
     * @param maxPoolSize 最大线程数量
     * @param keepAliveTime 临时线程的最大存活时间
     * @param queueSize 阻塞队列大小
     * @param poolName 线程池名称，不用的业务使用不同的线程池
     * @param isDaemon 是否启动守护线程
     * @return
     */
    public static ExecutorService createThreadPool(
        int corePoolSize,
        int maxPoolSize,
        long keepAliveTime,
        int queueSize,
        String poolName,
        boolean isDaemon
    ) {
        if (THREAD_POOL_CACHE.containsKey(poolName)) {
            return THREAD_POOL_CACHE.get(poolName);
        }

        ExecutorService executorService = new ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            keepAliveTime,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(queueSize),
            createThreadFactory(poolName, isDaemon)
        );

        log.debug("创建线程池: {}", poolName);

        THREAD_POOL_CACHE.put(poolName, executorService);
        return executorService;
    }

    public static ThreadFactory createThreadFactory(String poolName) {
        return createThreadFactory(poolName, false);
    }

    /**
     * 创建线程工厂
     * @param poolName
     * @param isDaemon
     * @return
     */
    public static ThreadFactory createThreadFactory(String poolName, boolean isDaemon) {
        ThreadFactoryBuilder threadFactoryBuilder = ThreadFactoryBuilder.create()
            .setDaemon(isDaemon);

        if (StrUtil.isBlank(poolName)) {
            return threadFactoryBuilder.build();
        }

        return threadFactoryBuilder.setNamePrefix(poolName)
            .build();
    }

    /**
     * 关闭所有业务线程池
     */
    public static void shutdownAll() {
        THREAD_POOL_CACHE.entrySet().parallelStream()
            .forEach(entry -> {
                String poolName = entry.getKey();
                ExecutorService executorService = entry.getValue();

                // 关闭线程池
                executorService.shutdown();
                log.info("{}, 线程池开始停止...", poolName);

                try {
                    // 如果线程池在10s内没有停止，则强制停止
                    if (executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                        log.info("{}, 线程池已停止", poolName);
                    } else {
                        log.info("{}, 线程池10s内未停止, 强制停止", poolName);
                        executorService.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    log.error("{}, 线程池停止异常", poolName);
                    executorService.shutdownNow();
                }
            });
    }
}
