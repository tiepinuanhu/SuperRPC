package org.wxc.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

/**
 * @author wangxinchao
 * @date 2025/10/28 21:30
 */
@Getter
@ToString
@AllArgsConstructor
public enum SerializeType {
    KRYO((byte) 1, "kryo"),
    ;

    private final byte code;
    private final String desc;

    public static SerializeType valueOf(byte code){
        return Arrays.stream(values())
                .filter(o -> o.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unknown msg type code:" + code));
    }
}
