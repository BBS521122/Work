package com.work.work.mapper.sql;


import com.work.work.dto.RequestDTO;
import com.work.work.dto.SearchDTO;
import com.work.work.entity.Tenant;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TenantMapper {

    @Insert("INSERT INTO test_union.tenant (name,  cover, contact_person, phone, admin, note) " +
            "VALUES (#{name}, #{cover}, #{contactPerson}, #{phone}, #{admin}, #{note})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertTenant(Tenant tenant);

    @Update("UPDATE test_union.tenant SET name = #{name}, note = #{note}," +
            "cover=#{cover} WHERE id = #{id}")
    int updateTenant(Tenant tenant);

    @Select("SELECT * FROM test_union.tenant WHERE id = #{id}")
    Tenant selectTenantById(Long id);

    @Select("SELECT cover FROM test_union.tenant WHERE id = #{id}")
    String selectCoverById(Long id);

    @Select("<script>" +
            "SELECT id\n" +
            "    FROM test_union.tenant\n" +
            "    <where>\n" +
            "      <if test=\"id != null and id != ''\">\n" +
            "        AND id LIKE CONCAT(#{id}, '%')\n" +
            "      </if>\n" +
            "      <if test=\"name != null and name != ''\">\n" +
            "        AND name LIKE CONCAT('%', #{name}, '%')\n" +
            "      </if>\n" +
            "      <if test=\"contact != null and contact != ''\">\n" +
            "        AND contact_person LIKE CONCAT('%', #{contact}, '%')\n" +
            "      </if>\n" +
            "      <if test=\"phone != null and phone != ''\">\n" +
            "        AND phone LIKE CONCAT(#{phone}, '%')\n" +
            "      </if>\n" +
            "    </where>" +
            "</script>")
    List<Long> get(SearchDTO searchDTO);

    @Delete("DELETE FROM test_union.tenant WHERE id = #{id}")
    int deleteTenant(Long id);

}
