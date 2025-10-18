package org.wxc.rpc.registry.zk;

import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.wxc.rpc.constant.RpcConstant;

import java.util.List;

/**
 * 专门用于与zookeeper进行交互的类
 * @author wangxinchao
 * @date 2025/10/18 14:51
 */
@Slf4j
public class ZkClient {
    private static CuratorFramework client;


    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;

    public ZkClient() {
        this(RpcConstant.ZK_IP, RpcConstant.ZK_PORT);
    }

    public ZkClient(String host, int port) {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        client = CuratorFrameworkFactory.builder()
                .connectString(host + StrUtil.COLON + port)
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();
        log.info("zookeeper开始连接...");
        client.start();
        log.info("zookeeper连接成功");
    }


    @SneakyThrows
    public void createPersistentNode(String path) {
        if (StrUtil.isBlank(path)) {
            throw new IllegalArgumentException("path can not be null");
        }
        if (client.checkExists().forPath(path) != null) {
            log.debug("结点已存在: {}", path);
            return;
        }
        // 如果结点不存在
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(path);
    }


    @SneakyThrows
    public List<String> getChildren(String path) {
        if (StrUtil.isBlank(path)) {
            throw new IllegalArgumentException("path can not be null");
        }
        return client.getChildren().forPath(path);
    }

}
