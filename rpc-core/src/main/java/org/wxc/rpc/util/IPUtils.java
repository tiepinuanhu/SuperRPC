package org.wxc.rpc.util;

import cn.hutool.core.util.StrUtil;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * @author wangxinchao
 * @date 2025/10/18 15:24
 */
public class IPUtils {

    /**
     * 将InetSocketAddress转换成ip:port字符串
     * @param address
     * @return
     */
    public static String InetToIpPort(InetSocketAddress address) {
        if (Objects.isNull(address)) {
            throw new IllegalArgumentException("address is null");
        }
        String host = address.getHostString();
        if (Objects.equals(host, "localhost")) {
            host = "127.0.0.1";
        }
        return host + StrUtil.COLON + address.getPort();
    }

    /**
     * 将ip:port字符串转换成InetSocketAddress
     * @param ipPort
     * @return
     */
    public static InetSocketAddress IpPortToInet(String ipPort) {
        if (StrUtil.isBlank(ipPort)) {
            throw new IllegalArgumentException("ipPort is null");
        }
        String[] split = ipPort.split(StrUtil.COLON);
        if (split.length != 2) {
            throw new IllegalArgumentException("ipPort is invalid");
        }
        return new InetSocketAddress(split[0], Integer.parseInt(split[1]));
    }
}
