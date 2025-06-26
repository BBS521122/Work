package com.work.work.security;

import com.work.work.utils.User;
import com.work.work.enums.RoleEnum;
import com.work.work.mapper.sql.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 继承UserDetailsService接口（位于Spring Security调用链中）
 * 实现了根据传入的用户名返回相关信息
 * UserDetailsService接口实现方法传入username，返回对应用户UserDetails对象
 * 该类为自定义UserDetailsService实现类，根据用户名从数据库中读取相关信息
 * 可以用于登录Service中
 * 该类替换了默认的UserDetailsService
 * 可以实现自动验证密码
 * <br>---------------------------------<br>
 * 拓展点：
 * 如果需要多个UserDetailsService针对不同用户
 * 实现方法一：编写一个自定义的 UsernamePasswordAuthenticationFilter
 * 在其中根据请求路径或参数（如 loginType=admin），手动选择要调用的 UserDetailsService
 * 实现方法二：自定义一个 DelegatingUserDetailsService
 */

/*
 * 该类原本是为UsernamePasswordAuthenticationFilter调用链中由默认的 AuthenticationManager的实现ProviderManager调用，
 * 它会遍历所有的 AuthenticationProvider实例，通常是 DaoAuthenticationProvider，由DaoAuthenticationProvider中调用，返回UserDetail
 *
 */
@Service
public class UserDetailsServiceImp implements UserDetailsService {
    /**
     * 注入 UserMapper，用于与数据库交互获取用户信息。
     */
    @Autowired
    UserMapper userMapper;

    /**
     * 根据用户名加载用户信息。
     *
     * @param username 用户名
     * @return 包含用户详细信息的 UserDetails 对象
     * @throws UsernameNotFoundException 如果用户名不存在或查询失败
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        RoleEnum role;
        try {
            // 调用 UserMapper 获取用户信息
            user = userMapper.getUserByUsername(username);
            role = userMapper.findRoleEnumByUserId(user.getId());
        } catch (Exception e) {
            // 如果查询失败，抛出 UsernameNotFoundException 异常
            throw new UsernameNotFoundException(e.getMessage());
        }
        // 返回自定义的 UserDetails 实现类
        return new UserDetailsImpl(user,role);
    }
}
