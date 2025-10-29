package org.wxc.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author wangxinchao
 * @date 2025/10/28 21:24
 */
@ToString
@Getter
@AllArgsConstructor
public enum VersionType {

    VERSION_1((byte)1, "v1.0"),
    ;

    private byte code;
    private String desc;

}
