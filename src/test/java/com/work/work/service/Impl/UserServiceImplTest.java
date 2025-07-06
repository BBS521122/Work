package com.work.work.service.Impl;

import com.work.work.constant.JwtClaimsConstant;
import com.work.work.converter.UserConverter;
import com.work.work.dto.UpdateDTO;
import com.work.work.dto.user.UserLoginDTO;
import com.work.work.enums.RoleEnum;
import com.work.work.mapper.RedisTokenMapper;
import com.work.work.mapper.sql.UserMapper;
import com.work.work.properties.JwtProperties;
import com.work.work.security.UserDetailsImpl;
import com.work.work.service.MinioService;
import com.work.work.utils.JwtUtils;
import com.work.work.utils.User;
import com.work.work.vo.SettingVO;
import com.work.work.vo.UserLoginVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtProperties jwtProperties;
    @Mock
    private RedisTokenMapper redisTokenMapper;
    @Mock
    private UserConverter userConverter;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MinioService minioService;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserLoginDTO validLoginDTO;
    private UserLoginDTO invalidLoginDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testUser");
        testUser.setPassword("encodedPassword");

        validLoginDTO = new UserLoginDTO();
        validLoginDTO.setName("testUser");
        validLoginDTO.setPassword("correctPassword");

        invalidLoginDTO = new UserLoginDTO();
        invalidLoginDTO.setName("wrongUser");
        invalidLoginDTO.setPassword("wrongPassword");
    }

    // login
    @Test
    void login_正常登录返回VO() {
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUser()).thenReturn(testUser);
        when(userDetails.getRoleEnum()).thenReturn(RoleEnum.user);
        when(jwtProperties.getSecretKey()).thenReturn("secret");
        when(jwtProperties.getTtl()).thenReturn(1000L);
        when(userMapper.findRoleEnumByUserId(anyLong())).thenReturn(RoleEnum.user);
        when(userMapper.findStateByUserId(anyLong())).thenReturn("1");
        when(userConverter.userToUserLoginVO(any())).thenReturn(new UserLoginVO());
        mockStaticJwtUtils();

        UserLoginVO vo = userService.login(validLoginDTO);
        assertNotNull(vo);
        verify(redisTokenMapper).setToken(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    void login_认证失败抛异常() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));
        assertThrows(RuntimeException.class, () -> userService.login(invalidLoginDTO));
    }

    // addUser
    @Test
    void addUser_正常插入() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.insert(any(User.class))).thenReturn(1);
        int result = userService.addUser(testUser);
        assertEquals(1, result);
    }

    @Test
    void addUser_密码为null_密码加密器抛异常() {
        testUser.setPassword(null);
        when(passwordEncoder.encode(isNull())).thenThrow(new NullPointerException("password is null"));
        assertThrows(NullPointerException.class, () -> userService.addUser(testUser));
    }

    // updatePassword
    @Test
    void updatePassword_正常() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.updatePassword(anyLong(), anyString())).thenReturn(1);
        int result = userService.updatePassword(1L, "newPassword");
        assertEquals(1, result);
    }

    // getUser
    @Test
    void getUser_正常() {
        SettingVO vo = new SettingVO();
        when(userMapper.getUserByUserId(anyLong())).thenReturn(vo);
        SettingVO result = userService.getUser(1L);
        assertEquals(vo, result);
    }

    // confirmPassword
    @Test
    void confirmPassword_密码正确返回true() {
        when(userMapper.confirmPassword(anyLong())).thenReturn("encodedPassword");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        assertTrue(userService.confirmPassword(1L, "correctPassword"));
    }

    @Test
    void confirmPassword_密码错误返回false() {
        when(userMapper.confirmPassword(anyLong())).thenReturn("encodedPassword");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        assertFalse(userService.confirmPassword(1L, "wrongPassword"));
    }

    // getUserAvatarUrl
    @Test
    void getUserAvatarUrl_正常() {
        when(userMapper.getAvatarById(anyLong())).thenReturn("avatar.jpg");
        when(minioService.getSignedUrl(anyString())).thenReturn("http://avatar.url");
        String url = userService.getUserAvatarUrl(1L);
        assertEquals("http://avatar.url", url);
    }

    // updateUserAvatar
    @Test
    void updateUserAvatar_正常() {
        MultipartFile file = mock(MultipartFile.class);
        when(userMapper.getAvatarById(anyLong())).thenReturn("oldAvatar.jpg");
        when(minioService.uploadFile(any())).thenReturn("newAvatar.jpg");
        when(userMapper.updateAvatarById(anyLong(), anyString())).thenReturn(1);
        when(minioService.getSignedUrl(anyString())).thenReturn("http://avatar.url");
        String url = userService.updateUserAvatar(1L, file);
        assertEquals("http://avatar.url", url);
        verify(minioService).deleteFile("oldAvatar.jpg");
    }

    @Test
    void updateUserAvatar_旧头像为null不删除() {
        MultipartFile file = mock(MultipartFile.class);
        when(userMapper.getAvatarById(anyLong())).thenReturn(null);
        when(minioService.uploadFile(any())).thenReturn("newAvatar.jpg");
        when(userMapper.updateAvatarById(anyLong(), anyString())).thenReturn(1);
        when(minioService.getSignedUrl(anyString())).thenReturn("http://avatar.url");
        String url = userService.updateUserAvatar(1L, file);
        assertEquals("http://avatar.url", url);
        verify(minioService, never()).deleteFile(isNull());
    }

    @Test
    void updateUserAvatar_数据库更新失败_删除新头像抛异常() {
        MultipartFile file = mock(MultipartFile.class);
        when(userMapper.getAvatarById(anyLong())).thenReturn("oldAvatar.jpg");
        when(minioService.uploadFile(any())).thenReturn("newAvatar.jpg");
        when(userMapper.updateAvatarById(anyLong(), anyString())).thenReturn(0);
        doNothing().when(minioService).deleteFile("newAvatar.jpg");
        assertThrows(RuntimeException.class, () -> userService.updateUserAvatar(1L, file));
        verify(minioService, atLeastOnce()).deleteFile("newAvatar.jpg");
    }

    @Test
    void updateUserAvatar_上传新头像后异常_删除新头像() {
        MultipartFile file = mock(MultipartFile.class);
        when(userMapper.getAvatarById(anyLong())).thenReturn("oldAvatar.jpg");
        when(minioService.uploadFile(any())).thenReturn("newAvatar.jpg");
        when(userMapper.updateAvatarById(anyLong(), anyString())).thenThrow(new RuntimeException("db error"));
        doNothing().when(minioService).deleteFile("newAvatar.jpg");
        assertThrows(RuntimeException.class, () -> userService.updateUserAvatar(1L, file));
        verify(minioService).deleteFile("newAvatar.jpg");
    }

    // update
    @Test
    void update_正常() {
        UpdateDTO updateDTO = new UpdateDTO();
        updateDTO.setNickname("newNickname");
        when(userMapper.update(anyLong(), any(UpdateDTO.class))).thenReturn(1);
        int result = userService.update(1L, updateDTO);
        assertEquals(1, result);
    }

    @Test
    void update_updateDTO为null抛异常() {
        assertThrows(NullPointerException.class, () -> userService.update(1L, null));
    }

    // mock JwtUtils.createJWT 静态方法
    private void mockStaticJwtUtils() {
        mockStatic(JwtUtils.class).when(() -> JwtUtils.createJWT(anyString(), anyLong(), anyMap()))
                .thenReturn("mockedToken");
    }
    @Test
    void login_认证返回null抛异常() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        assertThrows(RuntimeException.class, () -> userService.login(validLoginDTO));
    }

    @Test
    void updateUserAvatar_旧头像为空字符串不删除() {
        MultipartFile file = mock(MultipartFile.class);
        when(userMapper.getAvatarById(anyLong())).thenReturn("");
        when(minioService.uploadFile(any())).thenReturn("newAvatar.jpg");
        when(userMapper.updateAvatarById(anyLong(), anyString())).thenReturn(1);
        when(minioService.getSignedUrl(anyString())).thenReturn("http://avatar.url");
        String url = userService.updateUserAvatar(1L, file);
        assertEquals("http://avatar.url", url);
        verify(minioService, never()).deleteFile(any());
    }
    @Test
    void updateUserAvatar_删除新头像时抛异常_依然抛主异常() {
        MultipartFile file = mock(MultipartFile.class);
        when(userMapper.getAvatarById(anyLong())).thenReturn("oldAvatar.jpg");
        when(minioService.uploadFile(any())).thenReturn("newAvatar.jpg");
        when(userMapper.updateAvatarById(anyLong(), anyString())).thenThrow(new RuntimeException("db error"));
        // 让deleteFile无论什么参数都抛异常
        doThrow(new RuntimeException("delete error")).when(minioService).deleteFile(anyString());

        assertThrows(RuntimeException.class, () -> userService.updateUserAvatar(1L, file));
    }
    @Test
    void updateUserAvatar_获取旧头像抛异常_newName为null不删新头像() {
        MultipartFile file = mock(MultipartFile.class);
        // 让 getAvatarById 抛异常，模拟 newName 还没赋值就异常
        when(userMapper.getAvatarById(anyLong())).thenThrow(new RuntimeException("db error"));

        // minioService.uploadFile 不会被调用，所以不用mock
        // minioService.deleteFile 也不会被调用

        assertThrows(RuntimeException.class, () -> userService.updateUserAvatar(1L, file));
        // 验证 deleteFile 从未被调用
        verify(minioService, never()).deleteFile(any());
    }
}