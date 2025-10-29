package org.wxc.rpc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wxc.rpc.enums.CompressType;
import org.wxc.rpc.enums.MsgType;
import org.wxc.rpc.enums.SerializeType;
import org.wxc.rpc.enums.VersionType;

import java.io.Serializable;

/**
 * 协议结构
 * @author wangxinchao
 * @date 2025/10/28 21:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcMsg implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requestId;
    private VersionType versionType;
    private MsgType msgType;
    private SerializeType serializeType;
    private CompressType compressType;
    private Object data;
}
