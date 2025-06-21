package com.work.work.converter;


//import com.example.managementsystem.dto.StudentAddNewDTO;
//import com.example.managementsystem.dto.TeacherAddNewDTO;
import com.work.work.dto.user.UserLoginDTO;
import com.work.work.utils.User;
import com.work.work.vo.UserLoginVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * UserConverter 接口用于定义 UserLoginDTO 和 User 实体之间的转换方法。
 * 使用 MapStruct 框架自动生成实现类。
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    /**
     * 将 UserLoginDTO 转换为 User 实体。
     *
     * @param userLoginDTO 包含用户登录信息的数据传输对象。
     * @return 转换后的 User 实体对象。
     */
    User userLoginDTOToUser(UserLoginDTO userLoginDTO);


    UserLoginVO userToUserLoginVO(User user);

//    @Mappings({
//            @Mapping(target = "id", ignore = true),
//            @Mapping(target = "avatar", ignore = true),
//    })
//    User studentAddNewDTOToUser(StudentAddNewDTO studentAddNewDTO);
//
//    User teacherAddNewDTOToUser(TeacherAddNewDTO teacherAddNewDTO);
}
