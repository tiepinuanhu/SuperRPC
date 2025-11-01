package org.wxc.rpc.transmission.netty.codec;

import cn.hutool.core.util.ArrayUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.wxc.rpc.compress.Compress;
import org.wxc.rpc.compress.Impl.GzipCompress;
import org.wxc.rpc.constant.RpcConstant;
import org.wxc.rpc.dto.RpcMsg;
import org.wxc.rpc.dto.RpcRequest;
import org.wxc.rpc.dto.RpcResponse;
import org.wxc.rpc.enums.CompressType;
import org.wxc.rpc.enums.MsgType;
import org.wxc.rpc.enums.SerializeType;
import org.wxc.rpc.enums.VersionType;
import org.wxc.rpc.factory.SingletonFactory;
import org.wxc.rpc.serialize.Impl.KryoSerializer;
import org.wxc.rpc.serialize.Serialazer;

/**
 * LengthFieldBasedFrameDecoder也是Netty提供的一个解码器
 * 它可以解决TCP粘包和半包问题
 * @author wangxinchao
 * @date 2025/10/29 19:59
 */
public class NettyRpcDecoder extends LengthFieldBasedFrameDecoder {




    public NettyRpcDecoder() {
        super(RpcConstant.RESP_MAX_LEN, 5, 4, -9, 0);
    }


    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        return decodeFrame(frame);
    }


    /**
     * 传入ByteBuf，解析出RpcMsg
     * @param byteBuf
     * @return
     * @throws Exception
     */
    private Object decodeFrame(ByteBuf byteBuf) throws Exception {
        // 从buf中读取魔数
        readAndCheckMagicNumber(byteBuf);
        // 读取版本号
        byte version = byteBuf.readByte();
        VersionType versionType = VersionType.valueOf(version);

        int msgLen = byteBuf.readInt();

        byte msgTypeCode = byteBuf.readByte();
        MsgType msgType = MsgType.valueOf(msgTypeCode);

        byte serializerTypeCode = byteBuf.readByte();
        SerializeType serializeType = SerializeType.valueOf(serializerTypeCode);

        byte compressTypeCode = byteBuf.readByte();
        CompressType compressType = CompressType.valueOf(compressTypeCode);

        int requestId = byteBuf.readInt();

        Object data = readData(byteBuf, msgLen - RpcConstant.REQ_HEAD_LEN, msgType);

        return RpcMsg.builder()
                .data(data)
                .compressType(compressType)
                .msgType(msgType)
                .serializeType(serializeType)
                .versionType(versionType)
                .requestId(requestId)
                .build();
    }

    /**
     * 如果数据部分是请求
     * @param byteBuf
     * @param dataLen
     * @param msgType
     * @return
     * @throws Exception
     */
    private Object readData(ByteBuf byteBuf, int dataLen, MsgType msgType) throws Exception {
        // 请求需要读取成RpcRequest
        if (msgType.isRequest()) {
            return readData(byteBuf, dataLen, RpcRequest.class);
        }
        return readData(byteBuf, dataLen, RpcResponse.class);
    }

    /**
     *  从buf中读取byte数组，解压，反序列化成对象
     * @param byteBuf
     * @param dataLen
     * @param clazz
     * @return
     * @param <T>
     */
    private <T> T readData(ByteBuf byteBuf, int dataLen, Class<T> clazz) {
        if (dataLen <= 0) return null;
        byte[] data = new byte[dataLen];
        byteBuf.readBytes(data);

        Serialazer serializer = SingletonFactory.getInstance(KryoSerializer.class);
        Compress compress = SingletonFactory.getInstance(GzipCompress.class);

        byte[] decompressed = compress.decompress(data);
        return serializer.deserialize(decompressed, clazz);
    }


    private void readAndCheckMagicNumber(ByteBuf byteBuf) throws Exception {
        // 从buf中读取魔数
        byte[] magicBytes = new byte[RpcConstant.RPC_MAGIC_NUMBER.length];
        byteBuf.readBytes(magicBytes);
        if (!ArrayUtil.equals(magicBytes, RpcConstant.RPC_MAGIC_NUMBER)) {
            throw new Exception("magic number error");
        }
    }
}
