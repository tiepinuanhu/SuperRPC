package org.wxc.rpc.serialize.Impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import org.wxc.rpc.dto.RpcRequest;
import org.wxc.rpc.dto.RpcResponse;
import org.wxc.rpc.serialize.Serialazer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * @author wangxinchao
 * @date 2025/10/28 21:40
 */
@Slf4j
public class KryoSerializer implements Serialazer {

    /**
     * kryo不是线程安全的，所以每个线程都创建一个kryo对象
     */
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL
            = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        return kryo;
    });


    /**
     * 序列化
     * @param obj
     * @return
     */
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             Output output = new Output(bos)){
            Kryo kryo = KRYO_THREAD_LOCAL.get();
            // 使用kryo将对象序列化成字节数组
            kryo.writeObject(output, obj);
            output.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            log.error("kryo序列化失败", e);
            throw new RuntimeException(e);
        } finally {
            KRYO_THREAD_LOCAL.remove();
        }
    }

    /**
     * 反序列化
     * @param bytes
     * @param clazz
     * @return
     * @param <T>
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             Input input = new Input(bis)) {
            Kryo kryo = KRYO_THREAD_LOCAL.get();
            return kryo.readObject(input, clazz);
        } catch (IOException e) {
            log.error("kryo反序列化失败", e);
            throw new RuntimeException(e);
        } finally {
            KRYO_THREAD_LOCAL.remove();
        }
    }
}
