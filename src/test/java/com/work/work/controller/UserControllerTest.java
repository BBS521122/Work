package com.work.work.controller;

import com.work.work.context.UserContext;
import com.work.work.dto.UpdateDTO;
import com.work.work.dto.user.PasswordDTO;
import com.work.work.dto.user.UserLoginDTO;
import com.work.work.mapper.RedisTokenMapper;
import com.work.work.mapper.sql.UserMapper;
import com.work.work.service.UserService;
import com.work.work.utils.User;
import com.work.work.vo.HttpResponseEntity;
import com.work.work.vo.SettingVO;
import com.work.work.vo.UserLoginVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserService userService;

    @Mock
    private RedisTokenMapper redisTokenMapper;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_Success() {
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setName("testUser");
        loginDTO.setPassword("password");

        UserLoginVO expectedResponse = new UserLoginVO();
        expectedResponse.setName("testUser");
        expectedResponse.setToken("testToken");

        when(userService.login(any(UserLoginDTO.class))).thenReturn(expectedResponse);

        HttpResponseEntity<UserLoginVO> response = userController.login(loginDTO);

        assertEquals(200, response.getCode());
        assertEquals(expectedResponse, response.getData());
        assertEquals("success", response.getMessage());
    }

    @Test
    void login_Failure() {
        UserLoginDTO loginDTO = new UserLoginDTO();
        when(userService.login(any(UserLoginDTO.class))).thenThrow(new RuntimeException("Invalid credentials"));

        HttpResponseEntity<UserLoginVO> response = userController.login(loginDTO);

        assertEquals(400, response.getCode());
        assertNull(response.getData());
        assertEquals("error", response.getMessage());
    }

    @Test
    void updatePassword_Success() {
        try (MockedStatic<UserContext> mocked = mockStatic(UserContext.class)) {
            mocked.when(UserContext::getUserId).thenReturn(1L);

            PasswordDTO passwordDTO = new PasswordDTO();
            passwordDTO.setOldPassword("oldPass");
            passwordDTO.setNewPassword("newPass");

            when(userService.confirmPassword(anyLong(), anyString())).thenReturn(true);
            when(userService.updatePassword(anyLong(), anyString())).thenReturn(1);

            HttpResponseEntity<Integer> response = userController.updatePassword(passwordDTO);

            assertEquals(200, response.getCode());
            assertEquals(1, response.getData());
            assertEquals("success", response.getMessage());
        }
    }

    @Test
    void updatePassword_Failure() {
        try (MockedStatic<UserContext> mocked = mockStatic(UserContext.class)) {
            mocked.when(UserContext::getUserId).thenReturn(1L);

            PasswordDTO passwordDTO = new PasswordDTO();
            passwordDTO.setOldPassword("wrongPass");
            passwordDTO.setNewPassword("newPass");

            when(userService.confirmPassword(anyLong(), anyString())).thenReturn(false);

            HttpResponseEntity<Integer> response = userController.updatePassword(passwordDTO);

            assertEquals(400, response.getCode());
            assertEquals(0, response.getData());
            assertEquals("error", response.getMessage());
        }
    }

    @Test
    void getUser_Success() {
        try (MockedStatic<UserContext> mocked = mockStatic(UserContext.class)) {
            mocked.when(UserContext::getUserId).thenReturn(1L);

            SettingVO expectedSetting = new SettingVO();
            expectedSetting.setNickname("testUser");

            when(userService.getUser(anyLong())).thenReturn(expectedSetting);

            HttpResponseEntity<SettingVO> response = userController.getUser();

            assertEquals(200, response.getCode());
            assertEquals(expectedSetting, response.getData());
            assertEquals("success", response.getMessage());
        }
    }

    @Test
    void layout_Success() {
        String token = "testToken";
        HttpResponseEntity<String> response = userController.layout("Bearer " + token);

        assertEquals(200, response.getCode());
        assertEquals("success", response.getData());
        verify(redisTokenMapper, times(1)).deleteToken(eq(token));
    }

    @Test
    void getUserAvatar_Success() {
        try (MockedStatic<UserContext> mocked = mockStatic(UserContext.class)) {
            mocked.when(UserContext::getUserId).thenReturn(1L);

            String expectedUrl = "http://example.com/avatar.jpg";
            when(userService.getUserAvatarUrl(anyLong())).thenReturn(expectedUrl);

            HttpResponseEntity<String> response = userController.getUserAvatar();

            assertEquals(200, response.getCode());
            assertEquals(expectedUrl, response.getData());
        }
    }

    @Test
    void updateUserAvatar_Success() throws Exception {
        try (MockedStatic<UserContext> mocked = mockStatic(UserContext.class)) {
            mocked.when(UserContext::getUserId).thenReturn(1L);

            MultipartFile file = new MockMultipartFile(
                    "avatar",
                    "avatar.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "test image content".getBytes()
            );

            String expectedUrl = "http://example.com/new-avatar.jpg";
            when(userService.updateUserAvatar(anyLong(), any(MultipartFile.class))).thenReturn(expectedUrl);

            HttpResponseEntity<String> response = userController.updateUserAvatar(file);

            assertEquals(200, response.getCode());
            assertEquals(expectedUrl, response.getData());
        }
    }

    @Test
    void confirmPassword_Success() {
        try (MockedStatic<UserContext> mocked = mockStatic(UserContext.class)) {
            mocked.when(UserContext::getUserId).thenReturn(1L);

            String expectedRole = "ADMIN";
            when(userMapper.selectRoleById(anyLong())).thenReturn(expectedRole);

            HttpResponseEntity<String> response = userController.confirmPassword();

            assertEquals(200, response.getCode());
            assertEquals(expectedRole, response.getData());
        }
    }

    @Test
    void update_Success() {
        try (MockedStatic<UserContext> mocked = mockStatic(UserContext.class)) {
            mocked.when(UserContext::getUserId).thenReturn(1L);

            UpdateDTO updateDTO = new UpdateDTO();
            updateDTO.setNickname("New Nickname");

            when(userService.update(anyLong(), any(UpdateDTO.class))).thenReturn(1);

            HttpResponseEntity<Integer> response = userController.update(updateDTO);

            assertEquals(200, response.getCode());
            assertEquals(1, response.getData());
            assertEquals("success", response.getMessage());
        }
    }
}