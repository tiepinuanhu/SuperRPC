package org.wxc;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

@Slf4j
public class AppTest {


    CuratorFramework zkClient;
    /**
     * 测试连接
     */
    @Before
    public void testConnection() {
        // 重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        zkClient = CuratorFrameworkFactory.builder()
                // 要连接的服务器列表
                .connectString("124.70.131.122:2181")
                .retryPolicy(retryPolicy) // 重试策略
                .connectionTimeoutMs(60 * 1000) // 连接超时时间
                .sessionTimeoutMs(30 * 1000) // 会话超时时间
                .namespace("rpc")
                .build();
        // 开启连接，启动客户端
        zkClient.start();

        log.debug("zkClient.getState() = {}", zkClient.getState());
    }


    @Test
    public void testCreate() throws Exception {
        String path = zkClient.create()
                .forPath("/app1");
        log.debug("path = {}", path);
    }

    @Test
    public void testGet() throws Exception {
        Stat stat = new Stat();
        zkClient.getData()
                .storingStatIn(stat)
                .forPath("/app4");
        log.debug("stat = {}", stat);
    }
    @Test
    public void testSet() throws Exception {
        // 先查状态
        Stat stat = new Stat();
        zkClient.getData()
                .storingStatIn(stat)
                .forPath("/app4");
        // 根据之前查询的version修改
        Stat stat1 = zkClient.setData()
                .withVersion(123)
                .forPath("/app4", "haha".getBytes());
        log.debug("version = {}", stat1.getVersion());
    }
    @Test
    public void testDelete() throws Exception {
        zkClient.delete()
                .guaranteed()
                .inBackground((client, event) ->{
                    System.out.println("删除成功");
                })
                .forPath("/test0000000004");
    }
    @Test
    public void testWatcher() throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(
                zkClient, "/app2", true);
        pathChildrenCache.getListenable().addListener((client, event) -> {
            if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {
                String path = event.getData().getPath();
                byte[] data = event.getData().getData();
                log.debug("path = {}, data = {}", path, new String(data));
                System.out.println("子结点数据被更新");
            }
        });
        pathChildrenCache.start();
        while ( true) {}
    }


    @After
    public void close() {
        if (zkClient != null) {
            zkClient.close();
        }
    }
}
