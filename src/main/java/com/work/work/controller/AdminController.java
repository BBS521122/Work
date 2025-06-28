package com.work.work.controller;

import com.github.pagehelper.PageInfo;
import com.work.work.service.AdminService;
import com.work.work.dto.user.UserQueryDTO;
import com.work.work.utils.User;
import com.work.work.vo.HttpResponseEntity;
import com.work.work.vo.StateVO;
import com.work.work.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    // 添加日志记录器实例
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AdminController.class);

    @Autowired
    AdminService adminService;


    @PostMapping("/add-user")
    public HttpResponseEntity<Integer> addUser(@RequestBody User user) {
        user.setTime(new Date());
        int result = adminService.addUser(user);
        return new HttpResponseEntity<>(200, result, "success");
    }

    @PostMapping("/update-user")
    public HttpResponseEntity<Integer> updateUser(@RequestBody User user) {
        int result = adminService.updateUser(user);
        return new HttpResponseEntity<>(200, result, "success");
    }

    @PostMapping("/update-state")
    public HttpResponseEntity<Integer> updateState(@RequestBody StateVO stateVO) {
        int result = adminService.updateState(stateVO);
        return new HttpResponseEntity<>(200, result, "success");
    }

    @GetMapping("/delete-user")
    public HttpResponseEntity<Integer> deleteUser(long id) {
        int result = adminService.deleteUser(id);
        return new HttpResponseEntity<>(200, result, "success");
    }

    @PostMapping("/get-user")
    public HttpResponseEntity<PageInfo<UserVO>> getStudent(@RequestBody UserQueryDTO userQueryDTO,
                                                           @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        PageInfo<UserVO> res = adminService.getUser(userQueryDTO, pageNum, pageSize);
        return new HttpResponseEntity<>(200, res, "success");
    }

    @PostMapping("/import-user")
    public HttpResponseEntity<Integer> importUsers(@RequestBody List<User> users) {
        try {
            // 参数校验
            if (users == null || users.isEmpty()) {
                return new HttpResponseEntity<>(400, null, "导入数据不能为空");
            }

            // 数据校验
            List<String> errorMessages = new ArrayList<>();
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                if (user.getName() == null || user.getName().trim().isEmpty()) {
                    errorMessages.add("第" + (i + 1) + "行：用户名不能为空");
                }
                if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                    errorMessages.add("第" + (i + 1) + "行：密码不能为空");
                }
                if (user.getNickname() == null || user.getNickname().trim().isEmpty()) {
                    errorMessages.add("第" + (i + 1) + "行：密码不能为空");
                }
                if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                    errorMessages.add("第" + (i + 1) + "行：密码不能为空");
                }
                // 其他校验...
            }

            if (!errorMessages.isEmpty()) {
                return new HttpResponseEntity<>(400, null, String.join("; ", errorMessages));
            }

            // 设置默认值
            Date now = new Date();
            users.forEach(user -> {
                user.setTime(now);
                if (user.getState() == null) {
                    user.setState("正常"); // 默认启用
                }
            });

            int successCount = adminService.batchAddUsers(users);
            return new HttpResponseEntity<>(200, successCount,
                    String.format("成功导入%d条数据，共%d条", successCount, users.size()));
        } catch (Exception e) {
            log.error("批量导入用户异常", e);
            return new HttpResponseEntity<>(500, null, "系统异常，导入失败");
        }
    }
}
