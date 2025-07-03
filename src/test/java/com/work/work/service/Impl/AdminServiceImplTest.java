package com.work.work.service.Impl;

import com.work.work.dto.user.UserQueryDTO;
import com.work.work.mapper.sql.AdminMapper;
import com.work.work.mapper.sql.UserMapper;
import com.work.work.utils.User;
import com.work.work.vo.StateVO;
import com.work.work.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserMapper userMapper;
    
    @Mock
    private AdminMapper adminMapper;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private AdminServiceImpl adminService;
    
    private User validUser;
    private StateVO validStateVO;
    private UserQueryDTO validQueryDTO;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setId(1L);
        validUser.setName("testUser");
        validUser.setPassword("password123");
        
        validStateVO = new StateVO();
        validStateVO.setId(1L);
        validStateVO.setState("正常");
        
        validQueryDTO = new UserQueryDTO();
        validQueryDTO.setName("testUser");
    }

    // ========== addUser 方法测试 ==========
    @Test
    void addUser_WithValidUser_ShouldReturnSuccess() {
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userMapper.insert(any())).thenReturn(1);
        
        int result = adminService.addUser(validUser);
        
        assertEquals(1, result);
        verify(passwordEncoder).encode("password123");
        verify(userMapper).insert(validUser);
    }

    @Test
    void addUser_WithNullPassword_ShouldThrowException() {
        validUser.setPassword(null);
        
        assertThrows(NullPointerException.class, () -> adminService.addUser(validUser));
    }

    // ========== updateUser 方法测试 ==========
    @Test
    void updateUser_WithValidUser_ShouldReturnSuccess() {
        when(userMapper.updateUser(any())).thenReturn(1);
        
        int result = adminService.updateUser(validUser);
        
        assertEquals(1, result);
        verify(userMapper).updateUser(validUser);
    }

    @Test
    void updateUser_WithNullUser_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> adminService.updateUser(null));
    }

    // ========== deleteUser 方法测试 ==========
    @Test
    void deleteUser_WithValidId_ShouldReturnSuccess() {
        when(userMapper.deleteUser(anyLong())).thenReturn(1);
        
        int result = adminService.deleteUser(1L);
        
        assertEquals(1, result);
        verify(userMapper).deleteUser(1L);
    }

    @Test
    void deleteUser_WithNegativeId_ShouldReturnZero() {
        int result = adminService.deleteUser(-1L);
        
        assertEquals(0, result);
    }

    // ========== getUser 方法测试 ==========
    @Test
    void getUser_WithValidQuery_ShouldReturnPageInfo() {
        UserVO userVO = new UserVO();
        userVO.setId(1L);
        userVO.setName("testUser");
        
        when(adminMapper.queryUsers(any())).thenReturn(Collections.singletonList(userVO));
        
        var result = adminService.getUser(validQueryDTO, 1, 10);
        
        assertNotNull(result);
        assertEquals(1, result.getList().size());
        assertEquals("testUser", result.getList().get(0).getName());
    }

    @Test
    void getUser_WithNullQuery_ShouldReturnEmptyList() {
        var result = adminService.getUser(null, 1, 10);
        
        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    // ========== batchAddUsers 方法测试 ==========
    @Test
    void batchAddUsers_WithValidUsers_ShouldReturnSuccessCount() {
        User user1 = new User();
        user1.setName("user1");
        user1.setPassword("pass1");
        
        User user2 = new User();
        user2.setName("user2");
        user2.setPassword("pass2");
        
        when(userMapper.getUserByUsername("user1")).thenReturn(null);
        when(userMapper.getUserByUsername("user2")).thenReturn(null);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userMapper.insert(any())).thenReturn(1);
        
        int result = adminService.batchAddUsers(Arrays.asList(user1, user2));
        
        assertEquals(2, result);
        verify(userMapper, times(2)).insert(any());
    }

    @Test
    void batchAddUsers_WithDuplicateUsername_ShouldSkipDuplicate() {
        User user1 = new User();
        user1.setName("user1");
        user1.setPassword("pass1");
        
        when(userMapper.getUserByUsername("user1")).thenReturn(new User());
        
        int result = adminService.batchAddUsers(Collections.singletonList(user1));
        
        assertEquals(0, result);
        verify(userMapper, never()).insert(any());
    }

    // ========== updateState 方法测试 ==========
    @Test
    void updateState_With1_ShouldReturnSuccess() {
        validStateVO.setState("正常");
        when(adminMapper.updateState(anyLong(), eq("正常"))).thenReturn(1);

        int result = adminService.updateState(validStateVO);

        assertEquals(1, result);
        verify(adminMapper).updateState(1L, "正常");
    }

    @Test
    void updateState_With2_ShouldReturnSuccess() {
        validStateVO.setState("停用");
        when(adminMapper.updateState(anyLong(), eq("停用"))).thenReturn(1);

        int result = adminService.updateState(validStateVO);

        assertEquals(1, result);
        verify(adminMapper).updateState(1L, "停用");
    }

    @Test
    void updateState_With3_ShouldReturnZero() {
        validStateVO.setState("");
        when(adminMapper.updateState(anyLong(), eq(""))).thenReturn(0);

        int result = adminService.updateState(validStateVO);

        assertEquals(0, result);
        verify(adminMapper).updateState(1L, "");
    }

    @Test
    void updateState_With4_ShouldReturnZero() {
        validStateVO.setState("无效状态");
        when(adminMapper.updateState(anyLong(), eq("无效状态"))).thenReturn(0);

        int result = adminService.updateState(validStateVO);

        assertEquals(0, result);
        verify(adminMapper).updateState(1L, "无效状态");
    }

    @Test
    void updateState_WithNull5_ShouldThrowException() {
        validStateVO.setState(null);

        assertThrows(IllegalArgumentException.class, () -> {
            adminService.updateState(validStateVO);
        });
    }
}
