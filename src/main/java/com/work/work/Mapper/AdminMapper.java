package com.work.work.Mapper;


import com.work.work.dto.user.UserQueryDTO;
import com.work.work.utils.User;
import com.work.work.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminMapper {

    List<UserVO> queryUsers(UserQueryDTO query);

}
