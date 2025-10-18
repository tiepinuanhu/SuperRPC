package org.wxc.rpc.factory;

import lombok.SneakyThrows;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangxinchao
 * @date 2025/10/18 15:07
 */
public class SingletonFactory {
    private static final Map<Class<?>, Object> INSTANCE_CACHE
            = new ConcurrentHashMap<>();
    private SingletonFactory(){}


    @SneakyThrows
    public static <T> T getInstance(Class<T> clazz) {
        if (Objects.isNull(clazz)) {
            throw new IllegalArgumentException("class can't be null");
        }
        // map里有单例，则返回
        if (INSTANCE_CACHE.containsKey(clazz)) {
            return clazz.cast(INSTANCE_CACHE.get(clazz));
        }
        // 如果没有，则加锁创建
        synchronized (SingletonFactory.class) {
            if (INSTANCE_CACHE.containsKey(clazz)) {
                return (T) INSTANCE_CACHE.get(clazz);
            }
            T instance = clazz.getConstructor().newInstance();
            INSTANCE_CACHE.put(clazz, instance);
            return instance;
        }
    }
}
