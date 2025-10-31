package org.wxc.rpc.transmission.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.wxc.rpc.compress.Compress;
import org.wxc.rpc.compress.Impl.GzipCompress;
import org.wxc.rpc.constant.RpcConstant;
import org.wxc.rpc.dto.RpcMsg;
import org.wxc.rpc.factory.SingletonFactory;
import org.wxc.rpc.serialize.Impl.KryoSerializer;
import org.wxc.rpc.serialize.Serialazer;

import java.util.Objects;

/**
 * 自己协议的编码器
 * @author wangxinchao
 * @date 2025/10/29 19:59
 */
public class NettyRpcEncoder extends MessageToByteEncoder<RpcMsg> {


    /**
     * 只有将数据写入到ByteBuf，Netty才能将数据写入到Channel中
     * @param ctx
     * @param rpcMsg
     * @param byteBuf
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMsg rpcMsg,
                          ByteBuf byteBuf) {
        // 向buf中写入协议头部分的数据
        byteBuf.writeBytes(RpcConstant.RPC_MAGIC_NUMBER); // magic number 4B
        byteBuf.writeByte(rpcMsg.getVersionType().getCode()); // version 1B
        // 写指针向右移动4位，后面再计算数据长度
        byteBuf.writerIndex(byteBuf.writerIndex() + 4);
        byteBuf.writeByte(rpcMsg.getMsgType().getCode()); // message type 1B
        byteBuf.writeByte(rpcMsg.getSerializeType().getCode()); // serialize type 1B
        byteBuf.writeByte(rpcMsg.getCompressType().getCode()); // compress type 1B
        byteBuf.writeInt(rpcMsg.getRequestId()); // request id 4B


        int msgLen = RpcConstant.REQ_HEAD_LEN;

        if (!rpcMsg.getMsgType().isHeartBeat()
            && !Objects.isNull(rpcMsg.getData())) {
            byte[] bytes = data2Bytes(rpcMsg);
            byteBuf.writeBytes(bytes);
            msgLen += bytes.length;
        }
        int curIndex = byteBuf.writerIndex();
        // 移动写指针到数据长度的位置
        byteBuf.writerIndex(curIndex - msgLen + RpcConstant.RPC_MAGIC_NUMBER.length + 1);
        byteBuf.writeInt(msgLen);
        // 写指针移动到数据末尾
        byteBuf.writerIndex(curIndex);
    }


    private byte[] data2Bytes(RpcMsg rpcMsg) {
//        SerializeType serializeType = rpcMsg.getSerializeType();
//        CompressType compressType = rpcMsg.getCompressType();
        Serialazer serializer = SingletonFactory.getInstance(KryoSerializer.class);
        Compress compress = SingletonFactory.getInstance(GzipCompress.class);
        byte[] serialized = serializer.serialize(rpcMsg.getData());
        byte[] compressed = compress.compress(serialized);
        return compressed;
    }
}
