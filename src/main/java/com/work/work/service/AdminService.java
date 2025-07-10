package com.work.work.service;

import com.github.pagehelper.PageInfo;
import com.work.work.dto.user.UserQueryDTO;
import com.work.work.utils.User;
import com.work.work.vo.StateVO;
import com.work.work.vo.UserVO;
import java.util.List;

public interface AdminService {


    int addUser(User user);

    int updateUser(User user);

    int deleteUser(long id);

    PageInfo<UserVO> getUser(UserQueryDTO userQueryDTO, int pageNum, int pageSize);

    int batchAddUsers(List<User> users);

    int updateState(StateVO stateVO);
}
