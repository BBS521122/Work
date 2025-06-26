package com.work.work.Controller;

import com.github.pagehelper.PageInfo;
import com.work.work.Service.AdminService;
import com.work.work.Service.UserService;
import com.work.work.dto.user.UserQueryDTO;
import com.work.work.utils.User;
import com.work.work.vo.HttpResponseEntity;
import com.work.work.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    AdminService adminService;


    @PostMapping("/add-user")
    public HttpResponseEntity<Integer> addUser(@RequestBody User user) {
        int result = adminService.addUser(user);
        return new HttpResponseEntity<>(200, result, "success");
    }

    @PostMapping("/update-user")
    public HttpResponseEntity<Integer> updateUser(@RequestBody User user) {
        int result = adminService.updateUser(user);
        return new HttpResponseEntity<>(200, result, "success");
    }

    @PostMapping("/delete-user")
    public HttpResponseEntity<Integer> deleteUser(@RequestBody User user) {
        int result = adminService.deleteUser(user.getId());
        return new HttpResponseEntity<>(200, result, "success");
    }

    @PostMapping("/get-user")
    public HttpResponseEntity<PageInfo<UserVO>> getStudent(@RequestBody UserQueryDTO userQueryDTO,
                                                         @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        PageInfo<UserVO> res = adminService.getUser(userQueryDTO, pageNum, pageSize);
        return new HttpResponseEntity<>(200, res, "success");
    }
}
