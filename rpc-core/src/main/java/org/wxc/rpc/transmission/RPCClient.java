package org.wxc.rpc.transmission;

import org.wxc.rpc.dto.RPCRequest;
import org.wxc.rpc.dto.RPCResponse;

/**
 * @author wangxinchao
 * @date 2025/10/13 21:48
 */
public interface RPCClient {

    RPCResponse<?> send(RPCRequest request);
}
