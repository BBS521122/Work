package com.work.work.controller;

import com.github.pagehelper.PageInfo;
import com.work.work.dto.user.UserQueryDTO;
import com.work.work.service.AdminService;
import com.work.work.utils.User;
import com.work.work.vo.HttpResponseEntity;
import com.work.work.vo.StateVO;
import com.work.work.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addUser_Success() {
        User user = new User();
        user.setName("testUser");
        user.setPassword("password");
        user.setNickname("Test User");
        user.setTime(new Date());

        when(adminService.addUser(any(User.class))).thenReturn(1);

        HttpResponseEntity<Integer> response = adminController.addUser(user);

        assertEquals(200, response.getCode());
        assertEquals(1, response.getData());
        assertEquals("success", response.getMessage());
        verify(adminService, times(1)).addUser(any(User.class));
    }

    @Test
    void updateUser_Success() {
        User user = new User();
        user.setId(1L);
        user.setName("updatedUser");

        when(adminService.updateUser(any(User.class))).thenReturn(1);

        HttpResponseEntity<Integer> response = adminController.updateUser(user);

        assertEquals(200, response.getCode());
        assertEquals(1, response.getData());
        assertEquals("success", response.getMessage());
        verify(adminService, times(1)).updateUser(any(User.class));
    }

    @Test
    void updateState_Success() {
        StateVO stateVO = new StateVO();
        stateVO.setId(1L);
        stateVO.setState("正常");

        when(adminService.updateState(any(StateVO.class))).thenReturn(1);

        HttpResponseEntity<Integer> response = adminController.updateState(stateVO);

        assertEquals(200, response.getCode());
        assertEquals(1, response.getData());
        assertEquals("success", response.getMessage());
        verify(adminService, times(1)).updateState(any(StateVO.class));
    }

    @Test
    void deleteUser_Success() {
        long userId = 1L;
        when(adminService.deleteUser(anyLong())).thenReturn(1);

        HttpResponseEntity<Integer> response = adminController.deleteUser(userId);

        assertEquals(200, response.getCode());
        assertEquals(1, response.getData());
        assertEquals("success", response.getMessage());
        verify(adminService, times(1)).deleteUser(userId);
    }

    @Test
    void getUser_Success() {
        UserQueryDTO queryDTO = new UserQueryDTO();
        PageInfo<UserVO> pageInfo = new PageInfo<>();
        when(adminService.getUser(any(UserQueryDTO.class), anyInt(), anyInt())).thenReturn(pageInfo);

        HttpResponseEntity<PageInfo<UserVO>> response = adminController.getStudent(queryDTO, 1, 10);

        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
        assertEquals("success", response.getMessage());
        verify(adminService, times(1)).getUser(any(UserQueryDTO.class), anyInt(), anyInt());
    }

    @Test
    void importUsers_Success() {
        List<User> users = Arrays.asList(
                createValidUser(1L, "user1"),
                createValidUser(2L, "user2")
        );

        when(adminService.batchAddUsers(anyList())).thenReturn(2);

        HttpResponseEntity<Integer> response = adminController.importUsers(users);

        assertEquals(200, response.getCode());
        assertEquals(2, response.getData());
        assertTrue(response.getMessage().contains("成功导入2条数据"));
        verify(adminService, times(1)).batchAddUsers(anyList());
    }

    @Test
    void importUsers_EmptyList() {
        HttpResponseEntity<Integer> response = adminController.importUsers(new ArrayList<>());

        assertEquals(400, response.getCode());
        assertNull(response.getData());
        assertEquals("导入数据不能为空", response.getMessage());
        verify(adminService, never()).batchAddUsers(anyList());
    }

    @Test
    void importUsers_InvalidData() {
        List<User> users = Arrays.asList(
                createInvalidUser(1L), // Missing required fields
                createValidUser(2L, "user2")
        );

        HttpResponseEntity<Integer> response = adminController.importUsers(users);

        assertEquals(400, response.getCode());
        assertNull(response.getData());
        assertTrue(response.getMessage().contains("用户名不能为空"));
        verify(adminService, never()).batchAddUsers(anyList());
    }

    @Test
    void importUsers_Exception() {
        List<User> users = Arrays.asList(createValidUser(1L, "user1"));

        when(adminService.batchAddUsers(anyList())).thenThrow(new RuntimeException("Database error"));

        HttpResponseEntity<Integer> response = adminController.importUsers(users);

        assertEquals(500, response.getCode());
        assertNull(response.getData());
        assertEquals("系统异常，导入失败", response.getMessage());
    }

    private User createValidUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setName(username);
        user.setPassword("password");
        user.setNickname("Nickname");
        user.setState("正常");
        user.setTime(new Date());
        return user;
    }

    private User createInvalidUser(Long id) {
        User user = new User();
        user.setId(id);
        // Missing required fields
        return user;
    }
}