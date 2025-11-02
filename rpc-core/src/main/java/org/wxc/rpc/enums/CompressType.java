package org.wxc.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

/**
 * @author wangxinchao
 * @date 2025/10/28 21:31
 */
@Getter
@ToString
@AllArgsConstructor
public enum CompressType {

    GZIP((byte) 1, "gzip");

    private final byte code;
    private final String desc;

    public static CompressType valueOf(byte code){
        return Arrays.stream(values())
                .filter(o -> o.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unknown compress type code:" + code));
    }
}
