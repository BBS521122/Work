package com.work.work.Service;

import com.work.work.dto.user.UserLoginDTO;
import com.work.work.vo.UserLoginVO;

public interface UserService {

    UserLoginVO login(UserLoginDTO userLoginDTO);

    int addUser(UserLoginDTO userLoginDTO);

    int updatePassword(int id, String password);

}
