package org.wxc.rpc.transmission.netty.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.wxc.rpc.dto.RpcMsg;
import org.wxc.rpc.dto.RpcRequest;
import org.wxc.rpc.dto.RpcResponse;
import org.wxc.rpc.enums.CompressType;
import org.wxc.rpc.enums.MsgType;
import org.wxc.rpc.enums.SerializeType;
import org.wxc.rpc.enums.VersionType;
import org.wxc.rpc.factory.SingletonFactory;
import org.wxc.rpc.handler.RpcReqHandler;
import org.wxc.rpc.provider.ServiceProvider;

import java.util.IdentityHashMap;

/**
 * @author wangxinchao
 * @date 2025/10/28 20:41
 */
@Slf4j
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<RpcMsg> {



    private final RpcReqHandler rpcReqHandler;

    public NettyRpcServerHandler(ServiceProvider serviceProvider) {
        this.rpcReqHandler = new RpcReqHandler(serviceProvider);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                RpcMsg rpcMsg) throws Exception {
        log.debug("收到客户端请求: {}", rpcMsg);
        MsgType msgType;
        Object data;
        if (rpcMsg.getMsgType().isHeartBeat()) {
            msgType = MsgType.HEARTBEAT_RESP;
            data = null;
        } else {
            msgType = MsgType.RPC_RESP;
            RpcRequest rpcRequest = (RpcRequest) rpcMsg.getData();
            data = handleRpcRequest(rpcRequest);
        }
        RpcMsg respMsg = RpcMsg.builder()
            .data(data)
            .requestId(rpcMsg.getRequestId())
            .msgType(msgType)
            .versionType(VersionType.VERSION_1)
            .serializeType(SerializeType.KRYO)
            .compressType(CompressType.GZIP)
            .build();
        ctx.writeAndFlush(respMsg)
            .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端发送异常", cause);
        ctx.close();
    }


    private RpcResponse<?> handleRpcRequest(RpcRequest rpcRequest) {
        try {
            Object invoked = rpcReqHandler.invoke(rpcRequest);
            return RpcResponse.success(rpcRequest.getRequestId(), invoked);
        } catch (Exception e) {
            log.error("服务端处理请求异常", e);
            return RpcResponse.fail(rpcRequest.getRequestId(), e.getMessage());
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        boolean isNeedCloseChannel = evt instanceof IdleStateEvent
                && ((IdleStateEvent)evt).state() == IdleState.READER_IDLE;
        if (!isNeedCloseChannel) {
            super.userEventTriggered(ctx, evt);
            return;
        }
        log.debug("服务端30s没有收到客户端的心跳，服务端关闭连接: {}", ctx.channel());
        ctx.channel().close();
        ctx.close();

    }
}
