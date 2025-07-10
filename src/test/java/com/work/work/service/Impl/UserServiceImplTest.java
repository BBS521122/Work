package com.work.work.service.Impl;

import com.work.work.dto.UpdateDTO;
import com.work.work.dto.user.UserLoginDTO;
import com.work.work.enums.RoleEnum;
import com.work.work.mapper.RedisTokenMapper;
import com.work.work.mapper.sql.UserMapper;
import com.work.work.security.UserDetailsImpl;
import com.work.work.service.MinioService;
import com.work.work.utils.User;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private RedisTokenMapper redisTokenMapper;
    
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

    // ----------------- login() 测试 -----------------
    @Test
    void login_WithValidCredentials_ShouldReturnUserLoginVO() {
        // 准备
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUser()).thenReturn(testUser);
        when(userDetails.getRoleEnum()).thenReturn(RoleEnum.user);
        when(userMapper.findRoleEnumByUserId(anyLong())).thenReturn(RoleEnum.user);
        when(userMapper.findStateByUserId(anyLong())).thenReturn(String.valueOf(1));
        
        // 执行
        UserLoginVO result = userService.login(validLoginDTO);
        
        // 验证
        assertNotNull(result);
        assertNotNull(result.getToken());
        assertEquals(RoleEnum.user, result.getRole());
        verify(redisTokenMapper).setToken(anyString(), anyString(), anyLong(), any());
    }

    @Test
    void login_WithInvalidCredentials_ShouldThrowException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));
        
        assertThrows(RuntimeException.class, () -> userService.login(invalidLoginDTO));
    }

    // ----------------- addUser() 测试 -----------------
    @Test
    void addUser_WithValidUser_ShouldReturnUserId() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.insert(any(User.class))).thenReturn(1);
        
        int result = userService.addUser(testUser);
        assertEquals(1, result);
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    void addUser_WithNullPassword_ShouldThrowException() {
        testUser.setPassword(null);
        assertThrows(NullPointerException.class, () -> userService.addUser(testUser));
    }

    // ----------------- updatePassword() 测试 -----------------
    @Test
    void updatePassword_WithValidInput_ShouldReturnUpdateCount() {
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userMapper.updatePassword(anyLong(), anyString())).thenReturn(1);
        
        int result = userService.updatePassword(1L, "newPassword");
        assertEquals(1, result);
    }

    @Test
    void updatePassword_WithEmptyPassword_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, 
            () -> userService.updatePassword(1L, ""));
    }

    // ----------------- confirmPassword() 测试 -----------------
    @Test
    void confirmPassword_WithCorrectPassword_ShouldReturnTrue() {
        when(userMapper.confirmPassword(anyLong())).thenReturn("encodedPassword");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        
        assertTrue(userService.confirmPassword(1L, "correctPassword"));
    }

    @Test
    void confirmPassword_WithWrongPassword_ShouldReturnFalse() {
        when(userMapper.confirmPassword(anyLong())).thenReturn("encodedPassword");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        
        assertFalse(userService.confirmPassword(1L, "wrongPassword"));
    }

    // ----------------- updateUserAvatar() 测试 -----------------
    @Test
    void updateUserAvatar_WithValidFile_ShouldReturnUrl() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(userMapper.getAvatarById(anyLong())).thenReturn("oldAvatar.jpg");
        when(minioService.uploadFile(any())).thenReturn("newAvatar.jpg");
        when(userMapper.updateAvatarById(anyLong(), anyString())).thenReturn(1);
        when(minioService.getSignedUrl(anyString())).thenReturn("http://avatar.url");
        
        String result = userService.updateUserAvatar(1L, file);
        assertEquals("http://avatar.url", result);
        verify(minioService).deleteFile("oldAvatar.jpg");
    }

    @Test
    void updateUserAvatar_WithNullFile_ShouldThrowException() {
        assertThrows(NullPointerException.class, 
            () -> userService.updateUserAvatar(1L, null));
    }

    // ----------------- update() 测试 -----------------
    @Test
    void update_WithValidUpdateDTO_ShouldReturnUpdateCount() {
        UpdateDTO updateDTO = new UpdateDTO();
        updateDTO.setNickname("newNickname");
        
        when(userMapper.update(anyLong(), any(UpdateDTO.class))).thenReturn(1);
        
        int result = userService.update(1L, updateDTO);
        assertEquals(1, result);
    }

    @Test
    void update_WithNullUpdateDTO_ShouldThrowException() {
        assertThrows(NullPointerException.class, 
            () -> userService.update(1L, null));
    }
}
