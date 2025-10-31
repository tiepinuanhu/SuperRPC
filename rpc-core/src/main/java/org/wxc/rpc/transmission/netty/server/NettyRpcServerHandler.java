package org.wxc.rpc.transmission.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.wxc.rpc.dto.RpcMsg;
import org.wxc.rpc.dto.RpcRequest;
import org.wxc.rpc.dto.RpcResponse;
import org.wxc.rpc.enums.CompressType;
import org.wxc.rpc.enums.MsgType;
import org.wxc.rpc.enums.SerializeType;
import org.wxc.rpc.enums.VersionType;

/**
 * @author wangxinchao
 * @date 2025/10/28 20:41
 */
@Slf4j
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<RpcMsg> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                RpcMsg rpcMsg) throws Exception {
        log.debug("收到客户端请求: {}", rpcMsg);
        RpcRequest rpcRequest = (RpcRequest) rpcMsg.getData();
        RpcResponse<String> rpcResponse = RpcResponse.success(rpcRequest.getRequestId()
                , "响应数据");
        RpcMsg rpcMsg1 = RpcMsg.builder()
                .data(rpcResponse)
                .requestId(rpcMsg.getRequestId())
                .msgType(MsgType.RPC_RESP)
                .versionType(VersionType.VERSION_1)
                .serializeType(SerializeType.KRYO)
                .compressType(CompressType.GZIP)
                .build();
        ctx.writeAndFlush(rpcMsg1)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端发送异常", cause);
        ctx.close();
    }
}
