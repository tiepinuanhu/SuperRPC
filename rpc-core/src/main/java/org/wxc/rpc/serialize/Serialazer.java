package org.wxc.rpc.serialize;

/**
 * @author wangxinchao
 * @date 2025/10/28 21:39
 */
public interface Serialazer {
    byte[] serialize(Object obj);
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
