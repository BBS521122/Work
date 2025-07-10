package com.work.work.controller;


import com.work.work.dto.UpdateDTO;
import com.work.work.mapper.sql.UserMapper;
import com.work.work.service.UserService;
import com.work.work.context.UserContext;
import com.work.work.dto.user.PasswordDTO;
import com.work.work.dto.user.UserLoginDTO;
import com.work.work.mapper.RedisTokenMapper;
import com.work.work.utils.User;
import com.work.work.vo.HttpResponseEntity;
import com.work.work.vo.SettingVO;
import com.work.work.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;

@RestController()
@CrossOrigin
@RequestMapping("/user")
@Configuration
public class UserController {
    @Autowired
    UserMapper userMapper;

    @Autowired
    UserService userService;

    @Autowired
    RedisTokenMapper redisTokenMapper;


    @PostMapping("/login")
    public HttpResponseEntity<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        System.out.println(userLoginDTO.getName());
        UserLoginVO userLoginVO;
        try {
            userLoginVO = userService.login(userLoginDTO);
            System.out.println(userLoginVO.getName());
            return new HttpResponseEntity<UserLoginVO>(200, userLoginVO, "success");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new HttpResponseEntity<UserLoginVO>(400, null, "error");
        }

    }

    @PostMapping("/register")
    public HttpResponseEntity<String> register(@RequestBody User user) {
        Date date = new Date(System.currentTimeMillis());
        user.setTime(date);
        userService.addUser(user);
        return new HttpResponseEntity<String>(200, "success", null);
    }

    @PostMapping("/update-password")
    public HttpResponseEntity<Integer> updatePassword(@RequestBody PasswordDTO passwordDTO) {
        System.out.println("test:" + passwordDTO.getOldPassword());
        long id = UserContext.getUserId();
        if (userService.confirmPassword(id, passwordDTO.getOldPassword())) {
            int res = userService.updatePassword(id, passwordDTO.getNewPassword());
            return new HttpResponseEntity<>(200, res, "success");
        }
        return new HttpResponseEntity<>(400, 0, "error");
    }

    @GetMapping("/get")
    public HttpResponseEntity<SettingVO> getUser() {
        long id = UserContext.getUserId();
        SettingVO res = userService.getUser(id);
        return new HttpResponseEntity<>(200, res, "success");
    }

    @PostMapping("/layout")
    public HttpResponseEntity<String> layout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        System.out.println(token);
        redisTokenMapper.deleteToken(token);
        return new HttpResponseEntity<>(200, "success", null);
    }

    @GetMapping("/get-avatar")
    public HttpResponseEntity<String> getUserAvatar() {
        long id = UserContext.getUserId();
        String url = userService.getUserAvatarUrl(id);
        return new HttpResponseEntity<>(200, url, null);
    }

    @PostMapping(
            path = "/update_avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public HttpResponseEntity<String> updateUserAvatar(@RequestParam("file") MultipartFile file) {
        System.out.println(file.getOriginalFilename());
        long id = UserContext.getUserId();
        String url = userService.updateUserAvatar(id, file);
        return new HttpResponseEntity<>(200, url, null);
    }

    @GetMapping("/confirm")
    public HttpResponseEntity<String> confirmPassword() {
        long id = UserContext.getUserId();
        String res = userMapper.selectRoleById(id);
        return new HttpResponseEntity<>(200, res, null);
    }

    @PostMapping("/update")
    public HttpResponseEntity<Integer> update(@RequestBody UpdateDTO updateDTO) {
        long id = UserContext.getUserId();
        int res = userService.update(id, updateDTO);
        return new HttpResponseEntity<>(200, res, "success");
    }
}
