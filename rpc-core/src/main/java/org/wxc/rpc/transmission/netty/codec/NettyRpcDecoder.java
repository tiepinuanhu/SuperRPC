package org.wxc.rpc.transmission.netty.codec;

import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * LengthFieldBasedFrameDecoder也是Netty提供的一个解码器
 * 它可以解决TCP粘包和半包问题
 * @author wangxinchao
 * @date 2025/10/29 19:59
 */
public class NettyRpcDecoder extends LengthFieldBasedFrameDecoder {

    public NettyRpcDecoder() {
        this(Integer.MAX_VALUE, 0, 4);
    }
    public NettyRpcDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }
}
