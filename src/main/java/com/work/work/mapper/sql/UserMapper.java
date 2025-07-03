package com.work.work.mapper.sql;


import com.work.work.dto.UpdateDTO;
import com.work.work.enums.RoleEnum;
import com.work.work.utils.User;
import com.work.work.vo.SettingVO;
import com.work.work.vo.UserVO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("select * from userData")
    User findAllUser();

    @Select("select * from userData where name = 'admin'")
    User getUserByUsername(String name);

    @Select("select * from userData where id=#{id}")
    User getUserByUsername1(int id);

    @Insert("insert into userData(name, gender, password, state, department, email, time, phone, role, post, avatar, nickname) " +
            "VALUES (#{name},#{gender},#{password},#{state},#{department},#{email},#{time},#{phone},#{role},#{post},#{avatar},#{nickname})")
    int insert(User user);

    @Select("select role from userData where id = #{id}")
    @ResultType(RoleEnum.class)
    RoleEnum findRoleEnumByUserId(@Param("id") long id);

    @Select("select state from userData where id = #{id}")
    String findStateByUserId(@Param("id") long id);

    @Update("update userData set password = #{encodedPassword} where id = #{id}")
    int updatePassword(long id, String encodedPassword);

    @Update("update userData set " +
            "gender=#{gender},state=#{state},department=#{department}," +
            "phone=#{phone},role=#{role},post=#{role} " +
            "where id=#{id}")
    int updateUser(User user);

    @Delete("delete from userData where id = #{id}")
    int deleteUser(long id);

    @Select("select gender, email, phone, nickname" +
            " from userData where id = #{id}")
    @Results({
            @Result(property = "gender", column = "gender"),
            @Result(property = "email", column = "email"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "nickname", column = "nickname")
    })
    SettingVO getUserByUserId(long id);

    @Select("select password from userData where id = #{id}")
    String confirmPassword(long id);

    @Select("SELECT avatar FROM userData WHERE id = #{id}")
    String getAvatarById(@Param("id") long id);

    @Update("UPDATE userData SET avatar = #{avatarUrl} WHERE id = #{id}")
    int updateAvatarById(@Param("id") long id, @Param("avatarUrl") String avatarUrl);

    @Select("SELECT name FROM userData WHERE id = #{id}")
    String selectNameById(@Param("id") long id);

    @Select("SELECT role FROM userData WHERE id = #{id}")
    String selectRoleById(@Param("id") long id);

    @Update("update userData set " +
            "nickname=#{updateDTO.nickname},phone=#{updateDTO.phone},email=#{updateDTO.email},gender=#{updateDTO.gender} " +
            "where id =#{id}")
    int update(Long id, UpdateDTO updateDTO);
}
