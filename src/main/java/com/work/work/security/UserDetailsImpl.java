package com.work.work.security;

import com.work.work.utils.User;
import com.work.work.enums.RoleEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * UserDetailsImpl 类是 Spring Security 的 UserDetails 接口的实现。
 * 它封装了用户的详细信息，用于身份验证和授权。
 * 用于自定义UserDetails并作为返回值在UserDetailsServiceImpl的方法中作为返回值返回
 */
public class UserDetailsImpl implements UserDetails {
    /**
     * 用户实体对象，包含用户的基本信息。
     */
    private User user;

    /**
     * 用户权限信息
     */
    private RoleEnum roleEnum;

    /**
     * 获取用户的权限信息。
     * 当前实现返回一个空的权限集合。
     *
     * @return 用户的权限集合。
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 将角色转换为 Spring Security 可识别的 GrantedAuthority
        if (roleEnum != null) {
            return List.of(new SimpleGrantedAuthority("ROLE_" + roleEnum.name()));
        }
        return List.of();
    }

    /**
     * 获取用户的密码。
     *
     * @return 用户的密码。
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 获取用户的用户名。
     *
     * @return 用户的用户名。
     */
    @Override
    public String getUsername() {
        return user.getName();
    }

    public UserDetailsImpl(User user, RoleEnum roleEnum) {
        this.user = user;
        this.roleEnum = roleEnum;
    }

    public UserDetailsImpl() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RoleEnum getRoleEnum() {
        return roleEnum;
    }

    public void setRoleEnum(RoleEnum roleEnum) {
        this.roleEnum = roleEnum;
    }
}
