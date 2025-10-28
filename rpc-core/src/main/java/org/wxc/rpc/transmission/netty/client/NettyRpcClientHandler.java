package org.wxc.rpc.transmission.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.wxc.rpc.constant.RpcConstant;
import org.wxc.rpc.dto.RpcResponse;

/**
 * @author wangxinchao
 * @date 2025/10/28 20:32
 */
@Slf4j
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String rpcResponse) throws Exception {
        log.debug("收到服务端返回: {}", rpcResponse);
        AttributeKey<String> key = AttributeKey.valueOf(RpcConstant.NETTY_RPC_KEY);
        ctx.channel().attr(key).set(rpcResponse);
        ctx.close();
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端发送异常", cause);
        ctx.close();
    }
}
