package org.wxc.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

/**
 * 四种消息类型
 * @author wangxinchao
 * @date 2025/10/28 21:25
 */
@Getter
@ToString
@AllArgsConstructor
public enum MsgType {

    HEARTBEAT_REQ((byte) 1,"心跳请求"),
    HEARTBEAT_RESP((byte) 2,"心跳响应"),
    RPC_REQ((byte) 3,"RPC请求"),
    RPC_RESP((byte) 4,"RPC响应");


    private final byte code;
    private final String desc;

    public boolean isHeartBeat(){
        return this == HEARTBEAT_REQ || this == HEARTBEAT_RESP;
    }


    public boolean isRequest(){
        return this == RPC_REQ || this == HEARTBEAT_REQ;
    }


    /**
     * 根据code获取消息类型
     * @param code
     * @return
     */
    public static MsgType valueOf(byte code){
//        for (MsgType value : values()) {
//            if (value.code == code){
//                return value;
//            }
//        }
        return Arrays.stream(values())
                .filter(msgType -> msgType.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unknown msg type code:" + code));
    }

}
