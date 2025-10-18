package org.wxc.rpc.loadbalance;

import java.util.List;

/**
 * @author wangxinchao
 * @date 2025/10/18 16:25
 */
public interface LoadBalance {

    public String select(List<String> serviceAddresses);
}
