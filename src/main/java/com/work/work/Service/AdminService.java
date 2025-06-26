package com.work.work.Service;

import com.github.pagehelper.PageInfo;
import com.work.work.dto.user.UserLoginDTO;
import com.work.work.dto.user.UserQueryDTO;
import com.work.work.utils.User;
import com.work.work.vo.UserVO;

public interface AdminService {


    int addUser(User user);

    int updateUser(User user);

    int deleteUser(long id);

    PageInfo<UserVO> getUser(UserQueryDTO userQueryDTO, int pageNum, int pageSize);
}
