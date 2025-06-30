package com.work.work.mapper.sql;


import com.work.work.enums.RoleEnum;
import com.work.work.utils.User;
import com.work.work.vo.SettingVO;
import com.work.work.vo.UserVO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("select * from test_union.user")
    User findAllUser();

    @Select("select * from test_union.user where name = #{name}")
    User getUserByUsername(String name);

    @Insert("insert into test_union.user " +
            "VALUES (#{id},#{name},#{gender},#{password},#{state},#{department},#{email},#{time},#{phone},#{role},#{post},#{avatar},#{nickname})")
    int insert(User user);

    @Select("select role from test_union.user where id = #{id}")
    @ResultType(RoleEnum.class)
    RoleEnum findRoleEnumByUserId(@Param("id") long id);

    @Select("select state from test_union.user where id = #{id}")
    String findStateByUserId(@Param("id") long id);

    @Update("update test_union.user set password = #{encodedPassword} where id = #{id}")
    int updatePassword(long id, String encodedPassword);

    @Update("update test_union.user set " +
            "gender=#{gender},state=#{state},department=#{department}," +
            "phone=#{phone},role=#{role},post=#{role} " +
            "where id=#{id}")
    int updateUser(User user);

    @Delete("delete from test_union.user where id = #{id}")
    int deleteUser(long id);

    @Select("select gender, email, phone, nickname" +
            " from test_union.user where id = #{id}")
    @Results({
            @Result(property = "gender", column = "gender"),
            @Result(property = "email", column = "email"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "nickname", column = "nickname")
    })
    SettingVO getUserByUserId(long id);

    @Select("select password from test_union.user where id = #{id}")
    String confirmPassword(long id);

    @Select("SELECT avatar FROM test_union.user WHERE id = #{id}")
    String getAvatarById(@Param("id") long id);

    @Update("UPDATE test_union.user SET avatar = #{avatarUrl} WHERE id = #{id}")
    int updateAvatarById(@Param("id") long id, @Param("avatarUrl") String avatarUrl);

    @Select("SELECT name FROM test_union.user WHERE id = #{id}")
    String selectNameById(@Param("id") long id);
}
