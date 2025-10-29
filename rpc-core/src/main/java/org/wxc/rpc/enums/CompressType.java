package org.wxc.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author wangxinchao
 * @date 2025/10/28 21:31
 */
@Getter
@ToString
@AllArgsConstructor
public enum CompressType {

    GZIP((byte) 1, "gzip")

    ;

    private final byte code;
    private final String desc;
}
