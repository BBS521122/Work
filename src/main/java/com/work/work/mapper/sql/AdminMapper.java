package com.work.work.mapper.sql;


import com.work.work.dto.user.UserQueryDTO;
import com.work.work.vo.UserVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminMapper {

    @Select("<script>" +
            "SELECT id, name, gender, state, department, email, time, phone, role, post, nickname\n" +
            "    FROM userData\n" +
            "    <where>\n" +
            "      <if test=\"name != null and name != ''\">\n" +
            "        AND name LIKE CONCAT('%', #{name}, '%')\n" +
            "      </if>\n" +
            "      <if test=\"phone != null\">\n" +
            "        AND phone LIKE CONCAT('%', #{phone}, '%')\n" +
            "      </if>\n" +
            "      <if test=\"state != null\">\n" +
            "        AND state = #{state}\n" +
            "      </if>\n" +
            "      <if test=\"startDate != null\">\n" +
            "        AND time &gt;= #{startDate}\n" +
            "      </if>\n" +
            "      <if test=\"endDate != null\">\n" +
            "          AND time &lt;= #{endDate}\n" +
            "      </if>\n" +
            "    </where>" +
            "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "gender", column = "gender"),
            @Result(property = "state", column = "state"),
            @Result(property = "department", column = "department"),
            @Result(property = "email", column = "email"),
            @Result(property = "time", column = "time"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "role", column = "role"),
            @Result(property = "post", column = "post"),
            @Result(property = "nickname", column = "nickname")
    })
    List<UserVO> queryUsers(UserQueryDTO query);

    @Update("update userData set state=#{state} where id=#{id}")
    int updateState(
            @Param("id") long id,
            @Param("state") String state
    );


}
