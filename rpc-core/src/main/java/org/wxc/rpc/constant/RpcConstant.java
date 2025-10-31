package org.wxc.rpc.constant;

/**
 * @author wangxinchao
 * @date 2025/10/18 14:57
 */
public class RpcConstant {

    /**
     * RPC 服务端默认端口
     */
    public static final int SERVER_PORT = 8888;

    public static final String ZK_IP = "124.70.131.122";

    public static final int ZK_PORT = 2181;

    public static final String ZK_ROOT_PATH = "/rpc";


    public static final String NETTY_RPC_KEY = "RpcResp";


    public static final byte[] RPC_MAGIC_NUMBER = {(byte)'p', (byte)'r', (byte)'p', (byte)'c'};

    public static final int REQ_HEAD_LEN = 16;
    public static final int RESP_MAX_LEN = 1024 * 1024 * 8;
}
