package org.wxc.rpc.transmission.netty.client;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author wangxinchao
 * @date 2025/11/1 19:47
 */
public class ChannelPool {


    private final Map<String, Channel> pool = new ConcurrentHashMap<>();


    public Channel get(InetSocketAddress address, Supplier<Channel> supplier) {
        String addressString = address.toString();
        Channel channel = pool.get(addressString);
        if (channel != null && channel.isActive()) {
            return channel;
        }
        Channel newChannel = supplier.get();
        pool.put(addressString, newChannel);
        return newChannel;
    }
}
