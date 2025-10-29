package org.wxc.rpc.compress.Impl;

import cn.hutool.core.util.ZipUtil;
import org.wxc.rpc.compress.Compress;

import java.util.Objects;

/**
 * 使用Gzip压缩
 * @author wangxinchao
 * @date 2025/10/29 19:56
 */
public class GzipCompress implements Compress {
    @Override
    public byte[] compress(byte[] bytes) {
        if (Objects.isNull(bytes) || bytes.length == 0) {
            return bytes;
        }
        return ZipUtil.gzip(bytes);
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        if (Objects.isNull(bytes) || bytes.length == 0) {
            return bytes;
        }
        return ZipUtil.unGzip(bytes);
    }
}
