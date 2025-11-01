package org.wxc.rpc.transmission.netty.client;

import org.wxc.rpc.dto.RpcResponse;
import org.wxc.rpc.exception.RPCException;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UnprocessedRpcReq {
    private static final Map<String, CompletableFuture<RpcResponse<?>>> RESP_CF_MAP = new ConcurrentHashMap<>();

    public static void put(String reqId, CompletableFuture<RpcResponse<?>> cf) {
        RESP_CF_MAP.put(reqId, cf);
    }

    public static void complete(RpcResponse<?> resp) {
        CompletableFuture<RpcResponse<?>> cf = RESP_CF_MAP.remove(resp.getRequestId());

        if (Objects.isNull(cf)) {
            throw new RPCException("UnprocessedRpcReq请求异常");
        }

        cf.complete(resp);
    }
}
