package org.wxc.rpc.transmission.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.wxc.rpc.dto.RpcRequest;
import org.wxc.rpc.dto.RpcResponse;

/**
 * @author wangxinchao
 * @date 2025/10/28 20:41
 */
@Slf4j
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                String rpcRequest) throws Exception {
        log.debug("收到客户端请求: {}", rpcRequest);
//        RpcResponse<String> rpcResponse = RpcResponse.success("123", "响应数据");
        String rpcResponse = "响应数据";
        ctx.writeAndFlush(rpcResponse)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端发送异常", cause);
        ctx.close();
    }
}
