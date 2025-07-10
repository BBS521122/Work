package com.work.work.service.Impl;

import com.work.work.dto.UpdateDTO;
import com.work.work.mapper.RedisTokenMapper;
import com.work.work.mapper.sql.UserMapper;
import com.work.work.service.MinioService;
import com.work.work.service.UserService;
import com.work.work.constant.JwtClaimsConstant;
import com.work.work.converter.UserConverter;
import com.work.work.dto.user.UserLoginDTO;
import com.work.work.enums.RoleEnum;
import com.work.work.properties.JwtProperties;
import com.work.work.security.UserDetailsImpl;
import com.work.work.utils.JwtUtils;
import com.work.work.utils.User;
import com.work.work.vo.SettingVO;
import com.work.work.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    MinioService minioService;

    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO) {

        String name = userLoginDTO.getName();
        String password = userLoginDTO.getPassword();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(name, password);
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            throw new RuntimeException(e);
        }
        if (authentication == null) {
            System.out.println("Invalid name or password");
            throw new RuntimeException("Invalid name or password");
        }
        System.out.println("Invalid name or password");
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
        userLoginVO.setState(userMapper.findStateByUserId(user.getId()));
        return userLoginVO;
    }

    @Override
    public int addUser(User user) {
        String password = user.getPassword();
        String ciphertext = passwordEncoder.encode(password);
        user.setPassword(ciphertext);
        return userMapper.insert(user);
    }

    @Override
    public int updatePassword(long id, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        return userMapper.updatePassword(id, encodedPassword);
    }

    @Override
    public SettingVO getUser(long id) {
        return userMapper.getUserByUserId(id);
    }

    @Override
    public boolean confirmPassword(long id, String password) {
        String encodedPassword = userMapper.confirmPassword(id);
        return passwordEncoder.matches(password, encodedPassword);
    }

    @Override
    public String getUserAvatarUrl(long id) {
        String objectName = userMapper.getAvatarById(id);
        return minioService.getSignedUrl(objectName);
    }

    @Override
    public String updateUserAvatar(long id, MultipartFile file) {
        String oldName = null;
        String newName = null;
        try {
            // 1. 获取旧头像名称
            oldName = userMapper.getAvatarById(id);

            // 2. 上传新头像
            newName = minioService.uploadFile(file);

            // 3. 更新数据库记录
            if (userMapper.updateAvatarById(id, newName) <= 0) {
                // 数据库更新失败，删除新上传的头像
                minioService.deleteFile(newName);
                throw new RuntimeException("更新用户头像信息失败");
            }

            // 4. 删除旧头像（放在最后执行，失败不影响主流程）
            if (oldName != null && !oldName.isEmpty()) {
                try {
                    minioService.deleteFile(oldName);
                } catch (Exception e) {
                    // 记录日志，忽略异常
                }
            }

            return minioService.getSignedUrl(newName);
        } catch (Exception e) {
            // 如果上传新头像后出现异常，尝试删除新头像
            if (newName != null) {
                try {
                    minioService.deleteFile(newName);
                } catch (Exception ex) {
                    // 记录日志，忽略异常
                }
            }
            throw new RuntimeException("更新头像失败: " + e.getMessage(), e);
        }
    }

    @Override
    public int update(long id, UpdateDTO updateDTO) {
        System.out.println("updateDTO = " + updateDTO.getNickname());
        return userMapper.update(id, updateDTO);
    }

}
