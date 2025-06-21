package com.work.work.Service.Impl;

import com.work.work.Mapper.RedisTokenMapper;
import com.work.work.Mapper.UserMapper;
import com.work.work.Service.UserService;
import com.work.work.constant.JwtClaimsConstant;
import com.work.work.converter.UserConverter;
import com.work.work.dto.user.UserLoginDTO;
import com.work.work.enums.RoleEnum;
import com.work.work.properties.JwtProperties;
import com.work.work.security.UserDetailsImpl;
import com.work.work.utils.JwtUtils;
import com.work.work.utils.User;
import com.work.work.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

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

    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO) {

        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        if (authentication == null) {
            throw new RuntimeException("Invalid username or password");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        RoleEnum role = userDetails.getRoleEnum();

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.ID, user.getId());
        claims.put(JwtClaimsConstant.USERNAME, user.getName());
        claims.put(JwtClaimsConstant.PASSWORD, user.getPassword());
        claims.put(JwtClaimsConstant.ROLE, role.name());
        String token = JwtUtils.createJWT(jwtProperties.getSecretKey(), jwtProperties.getTtl(), claims);

        // 将token存入redis
        redisTokenMapper.setToken(token, token, 24, TimeUnit.HOURS);

        UserLoginVO userLoginVO = userConverter.userToUserLoginVO(user);
        userLoginVO.setToken(token);
        userLoginVO.setRole(userMapper.findRoleEnumByUserId(user.getId()));
        return userLoginVO;
    }

    @Override
    public int addUser(UserLoginDTO userLoginDTO){
        User user=userConverter.userLoginDTOToUser(userLoginDTO);
        String password=userLoginDTO.getPassword();
        String ciphertext=passwordEncoder.encode(password);
        user.setPassword(ciphertext);
        return userMapper.insert(user);
    }

    @Override
    public int updatePassword(int id, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        return userMapper.updatePassword(id, encodedPassword);
    }

}
