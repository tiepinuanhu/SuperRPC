package org.wxc.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

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

}
