package com.work.work.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.work.work.controller.AdminController;
import com.work.work.mapper.sql.AdminMapper;
import com.work.work.mapper.RedisTokenMapper;
import com.work.work.mapper.sql.UserMapper;
import com.work.work.service.AdminService;
import com.work.work.converter.UserConverter;
import com.work.work.dto.user.UserQueryDTO;
import com.work.work.properties.JwtProperties;
import com.work.work.utils.User;
import com.work.work.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private RedisTokenMapper redisTokenMapper;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminMapper adminMapper;

    // 添加日志记录器实例
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AdminController.class);

    @Override
    public int addUser(User user) {
        String password = user.getPassword();
        String ciphertext = passwordEncoder.encode(password);
        user.setPassword(ciphertext);
        return userMapper.insert(user);
    }

    @Override
    public int updateUser(User user) {
        return userMapper.updateUser(user);
    }

    @Override
    public int deleteUser(long id) {
        return userMapper.deleteUser(id);
    }

    @Override
    public PageInfo<UserVO> getUser(UserQueryDTO userQueryDTO, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<UserVO> list = adminMapper.queryUsers(userQueryDTO);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional
    public int batchAddUsers(List<User> users) {
        int successCount = 0;
        for (User user : users) {
            try {
                // 检查用户名是否已存在
                if (userMapper.getUserByUsername(user.getName()) != null) {
                    log.warn("用户名已存在: {}", user.getName());
                    continue;
                }

                // 加密密码
                user.setPassword(passwordEncoder.encode(user.getPassword()));

                userMapper.insert(user);
                successCount++;
            } catch (Exception e) {
                log.error("添加用户失败: {}", user.getName(), e);
            }
        }
        return successCount;
    }
}
