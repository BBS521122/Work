package com.work.work.Controller;


import com.work.work.Service.UserService;
import com.work.work.context.UserContext;
import com.work.work.dto.user.UpdatePasswordDTO;
import com.work.work.dto.user.UserLoginDTO;
import com.work.work.vo.HttpResponseEntity;
import com.work.work.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public HttpResponseEntity<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        UserLoginVO userLoginVO = userService.login(userLoginDTO);
        return new HttpResponseEntity<UserLoginVO>(200, userLoginVO, "success");
    }

    @PostMapping("/register")
    public HttpResponseEntity<String> register(@RequestBody UserLoginDTO userLoginDTO) {
        userService.addUser(userLoginDTO);
        return new HttpResponseEntity<String>(200, "success",null);
    }

    @PostMapping("/update-password")
    public HttpResponseEntity<Integer>  updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO) {
        int id = UserContext.getUserId();
        String password = updatePasswordDTO.getPassword();
        int res = userService.updatePassword(id,password);
        return new HttpResponseEntity<>(200, res, "success");
    }

}
