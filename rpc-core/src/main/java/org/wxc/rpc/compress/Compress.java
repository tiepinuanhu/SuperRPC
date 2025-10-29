package org.wxc.rpc.compress;

/**
 * @author wangxinchao
 * @date 2025/10/29 19:55
 */
public interface Compress {
    // 压缩, 将大字节数组压缩成小字节数组
    byte[] compress(byte[] bytes);
    // 解压, 将小字节数组解压成大字节数组
    byte[] decompress(byte[] bytes);
}
