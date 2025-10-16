package org.wxc.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import sun.security.provider.certpath.OCSPResponse;

/**
 * @author wangxinchao
 * @date 2025/10/13 21:37
 */
@Getter
@ToString
@AllArgsConstructor
public enum RPCRespStatus {

    SUCCESS(200, "success"),
    FAIL(500, "fail");

    private final int code;
    private final String message;


    public static boolean isSuccess(int code) {
        return SUCCESS.getCode() == code;
    }
    public static boolean isFail(int code) {
        return !isSuccess(code);
    }

}
