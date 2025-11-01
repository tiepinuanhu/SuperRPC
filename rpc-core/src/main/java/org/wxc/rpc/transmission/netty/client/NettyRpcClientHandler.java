package org.wxc.rpc.transmission.netty.client;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.wxc.rpc.constant.RpcConstant;
import org.wxc.rpc.dto.RpcMsg;
import org.wxc.rpc.dto.RpcResponse;
import org.wxc.rpc.enums.CompressType;
import org.wxc.rpc.enums.MsgType;
import org.wxc.rpc.enums.SerializeType;
import org.wxc.rpc.enums.VersionType;

import static org.wxc.rpc.transmission.netty.codec.NettyRpcEncoder.ID_GEN;

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
        RpcResponse rpcResponse = (RpcResponse) rpcMsg.getData();
        AttributeKey<RpcResponse> key = AttributeKey.valueOf(RpcConstant.NETTY_RPC_KEY);
        ctx.channel().attr(key).set(rpcResponse);
        ctx.close();
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            // 根据空闲类型处理
            if (!(event.state() == IdleState.WRITER_IDLE)) {
                super.userEventTriggered(ctx, evt);
                return;
            }
            // 5s没有写时间，则发送心跳请求
            RpcMsg heartBeat = RpcMsg.builder()
                .msgType(MsgType.RPC_REQ)
                .versionType(VersionType.VERSION_1)
                .serializeType(SerializeType.KRYO)
                .compressType(CompressType.GZIP)
                .build();
            log.debug("服务端发送心跳：{}", heartBeat);
            ctx.writeAndFlush(heartBeat)
                .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端发送异常", cause);
        ctx.close();
    }
}
