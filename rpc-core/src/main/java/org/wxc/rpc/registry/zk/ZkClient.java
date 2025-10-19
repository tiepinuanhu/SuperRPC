package org.wxc.rpc.registry.zk;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.wxc.rpc.constant.RpcConstant;
import org.wxc.rpc.util.IPUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 专门用于与zookeeper进行交互的类
 * 封装了CuratorFramework
 * @author wangxinchao
 * @date 2025/10/18 14:51
 */
@Slf4j
public class ZkClient {
    private final CuratorFramework client;


    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;


    /**
     * key: 接口的path, 例如 /rpc/{rpcServiceName}
     * value: 接口的服务地址列表，一个"{IP}:{port}"字符串列表
     * 用于查询服务的所有地址列表，结点的子结点
     */
    private static final Map<String, List<String>> SERVER_ADDRESSES_MAP
            = new ConcurrentHashMap<>();
    /**
     * element: /rpc/{rpcServiceName}/{IP}:{port}
     * 存储服务的完整path
     * 用于判断结点是否已经存在
     */
    private static final Set<String> SERVER_ADDRESSES_SET = new ConcurrentHashSet<>();


    public ZkClient() {
        this(RpcConstant.ZK_IP, RpcConstant.ZK_PORT);
    }

    /**
     * 构造函数，初始化ZK客户端
     * @param host
     * @param port
     */
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


    /**
     * 创建持久结点
     * @param path
     */
    @SneakyThrows
    public void createPersistentNode(String path) {
        if (StrUtil.isBlank(path)) {
            throw new IllegalArgumentException("path can not be null");
        }
        // 本地缓存有该结点
        if (SERVER_ADDRESSES_SET.contains(path)) {
            return;
        }
        // 如果本地没有，Zookeeper里面有，则本地也添加
        if (client.checkExists().forPath(path) != null) {
            SERVER_ADDRESSES_SET.add(path);
            log.debug("结点已存在: {}", path);
            return;
        }
        // 本地和ZK都没有该结点信息，则再zk创建结点并将结点path保存到本地缓存
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(path);
        SERVER_ADDRESSES_SET.add(path);
    }


    /**
     * 获取结点的所有孩子结点
     * @param path
     * @return
     */
    @SneakyThrows
    public List<String> getChildren(String path) {
        if (StrUtil.isBlank(path)) {
            throw new IllegalArgumentException("path can not be null");
        }
        // 本地缓存有该结点，返回其所有孩子结点
        if (SERVER_ADDRESSES_MAP.containsKey(path)) {
            return SERVER_ADDRESSES_MAP.get(path);
        }
        // 如果本地没有，则从zk中获取
        List<String> children = client.getChildren().forPath(path);
        SERVER_ADDRESSES_MAP.put(path, children);
        // 本地有缓存后，监听该结点的所有孩子结点变更
        watchNodeChildren(path);
        return children;
    }


    /**
     * 监听某结点的所有孩子结点变更
     * @param path
     * @throws Exception
     */
    private void watchNodeChildren(String path) throws Exception {
        PathChildrenCache cache
                = new PathChildrenCache(client, path, true);
        // 监听到子结点变化，则更新本地MAP
        cache.getListenable().addListener((client, event) -> {
            log.info("监听到结点变化: {}", event.getType());
            List<String> children = client.getChildren().forPath(path);
            SERVER_ADDRESSES_MAP.put(path, children);
        });
        cache.start();
    }


    /**
     * 服务关闭时，释放本机器注册的所有服务
     * 只需要在zookeeper上删除接口，本地的set在服务关闭后内存会释放
     * @param address
     */
    public void clearAll(InetSocketAddress address) {
        SERVER_ADDRESSES_SET.forEach(path -> {
            if (path.endsWith(IPUtils.InetToIpPort(address))) {
                log.info("删除结点: {}", path);
                try {
                    client.delete().deletingChildrenIfNeeded().forPath(path);
                    log.info("删除结点成功: {}", path);
                } catch (Exception e) {
                    log.error("删除结点失败: {}", path);
                }
            }
        });
    }




}
