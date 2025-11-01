package org.wxc.rpc.transmission.netty.client;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.wxc.rpc.dto.RpcMsg;
import org.wxc.rpc.dto.RpcResponse;
import org.wxc.rpc.enums.CompressType;
import org.wxc.rpc.enums.MsgType;
import org.wxc.rpc.enums.SerializeType;
import org.wxc.rpc.enums.VersionType;


/**
 * @author wangxinchao
 * @date 2025/10/28 20:32
 */
@Slf4j
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<RpcMsg> {



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMsg rpcMsg) throws Exception {
        if (rpcMsg.getMsgType().isHeartBeat()) {
            log.debug("收到服务端心跳: {}", rpcMsg);
            return;
        }
        log.debug("收到服务端返回: {}", rpcMsg);
        RpcResponse<?> rpcResponse = (RpcResponse<?>) rpcMsg.getData();
        UnprocessedRpcReq.complete(rpcResponse);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        boolean isNeedHeartBeat =
                evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE;

        if (!isNeedHeartBeat) {
            super.userEventTriggered(ctx, evt);
            return;
        }
        // 5s没有写时间，则发送心跳请求
        RpcMsg heartBeat = RpcMsg.builder()
            .msgType(MsgType.HEARTBEAT_REQ)
            .versionType(VersionType.VERSION_1)
            .serializeType(SerializeType.KRYO)
            .compressType(CompressType.GZIP)
            .build();
        log.debug("服务端发送心跳：{}", heartBeat);
        ctx.writeAndFlush(heartBeat)
            .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端发送异常", cause);
        ctx.close();
    }
}
