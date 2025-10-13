package org.wxc.service;

import org.wxc.api.User;
import org.wxc.api.UserService;

/**
 * 服务端实现接口，为客户端返回用户数据
 * @author wangxinchao
 * @date 2025/10/13 21:11
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(Long id) {
        return User.builder()
                .id(123L)
                .name("wxc")
                .build();
    }
}
