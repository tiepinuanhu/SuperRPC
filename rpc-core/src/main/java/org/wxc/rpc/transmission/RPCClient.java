package org.wxc.rpc.transmission;

import org.wxc.rpc.dto.RpcRequest;
import org.wxc.rpc.dto.RpcResponse;

/**
 * @author wangxinchao
 * @date 2025/10/13 21:48
 */
public interface RPCClient {

    RpcResponse<?> send(RpcRequest request);
}
