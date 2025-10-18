package org.wxc.rpc.loadbalance.Impl;

import cn.hutool.core.util.RandomUtil;
import org.wxc.rpc.loadbalance.LoadBalance;

import java.util.List;
import java.util.Random;

/**
 * @author wangxinchao
 * @date 2025/10/18 16:28
 */
public class RandomLoadBalance implements LoadBalance {

    @Override
    public String select(List<String> serviceAddresses) {
        return RandomUtil.randomEle(serviceAddresses);
    }
}
