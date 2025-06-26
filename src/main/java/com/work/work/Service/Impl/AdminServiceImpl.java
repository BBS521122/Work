package com.work.work.Service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.work.work.Mapper.AdminMapper;
import com.work.work.Mapper.RedisTokenMapper;
import com.work.work.Mapper.UserMapper;
import com.work.work.Service.AdminService;
import com.work.work.converter.UserConverter;
import com.work.work.dto.user.UserLoginDTO;
import com.work.work.dto.user.UserQueryDTO;
import com.work.work.properties.JwtProperties;
import com.work.work.utils.User;
import com.work.work.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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


}
