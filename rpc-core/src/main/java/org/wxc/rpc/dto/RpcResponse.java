package org.wxc.rpc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wxc.rpc.enums.RPCRespStatus;

import java.io.Serializable;

/**
 * 远程调用执行结束，要返回给调用发起者的数据格式
 * @author wangxinchao
 * @date 2025/10/13 21:31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    // 一个response对应一个request
    private String requestId;
    private Integer code;
    private String message;
    private T data;


    /**
     * 成功的response方便的创建方法
     * 便于快速生成一个成功的response
     * @param requestId
     * @param data
     * @return
     * @param <T>
     */
    public static <T> RpcResponse<T> success(String requestId, T data) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setCode(200);
        response.setData(data);
        return response;
    }
    /**
     * 失败的response方便的创建方法
     *
     * @param requestId
     * @param status 因为失败原因有很多种，所以传入一个status让创建者自己确定
     * @return
     * @param <T>
     */
    public static <T> RpcResponse<T> fail(String requestId, RPCRespStatus status) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setCode(status.getCode());
        response.setMessage(status.getMessage());
        return response;
    }
    public static <T> RpcResponse<T> fail(String requestId, String  message) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setCode(RPCRespStatus.FAIL.getCode());
        response.setMessage(message);
        return response;
    }

}
