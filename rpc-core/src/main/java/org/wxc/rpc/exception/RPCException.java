package org.wxc.rpc.exception;

/**
 * @author wangxinchao
 * @date 2025/10/16 13:17
 */
public class RPCException extends RuntimeException {
    public RPCException() {
        super();
    }


    public RPCException(String message) {
        super(message);
    }


    public RPCException(String message, Throwable cause) {
        super(message, cause);
    }


    public RPCException(Throwable cause) {
        super(cause);
    }
}
