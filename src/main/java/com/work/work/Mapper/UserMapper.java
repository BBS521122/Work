package com.work.work.Mapper;


import com.work.work.enums.RoleEnum;
import com.work.work.utils.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("select * from test_union.user")
    User findAllUser();

    @Select("select * from test_union.user where name = #{name}")
    User getUserByUsername(String name);

    @Insert("insert into test_union.user " +
            "VALUES (#{id},#{name},#{gender},#{password},#{state},#{department},#{email},#{time},#{phone},#{role},#{post},#{photo},#{nickname})")
    int insert(User user);

    @ResultType(RoleEnum.class)
    RoleEnum findRoleEnumByUserId(@Param("userId") long userId);

    @Update("update test_union.user set password = #{encodedPassword} where id = #{id}")
    int updatePassword(int id, String encodedPassword);

    @Update("update test_union.user set " +
            "gender=#{gender},state=#{state},department=#{department}," +
            "phone=#{phone},role=#{role},post=#{role} " +
            "where id=#{id}")
    int updateUser(User user);

    @Delete("delete from test_union.user where id = #{id}")
    int deleteUser(long id);
}
