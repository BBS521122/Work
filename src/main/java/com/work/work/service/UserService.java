package com.work.work.service;

import com.work.work.dto.UpdateDTO;
import com.work.work.dto.user.UserLoginDTO;
import com.work.work.utils.User;
import com.work.work.vo.SettingVO;
import com.work.work.vo.UserLoginVO;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserLoginVO login(UserLoginDTO userLoginDTO);

    int addUser(User user);

    int updatePassword(long id, String password);

    SettingVO getUser(long id);

    boolean confirmPassword(long id,String password);

    String getUserAvatarUrl(long id);

    String updateUserAvatar(long id, MultipartFile file);

    int update(long id, UpdateDTO updateDTO);

}
