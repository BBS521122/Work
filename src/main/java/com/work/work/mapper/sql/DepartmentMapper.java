package com.work.work.mapper.sql;


import com.work.work.entity.Department;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DepartmentMapper {
    // 获取所有部门
    @Select("SELECT * FROM test_union.sys_department ")
    List<Department> getAllDepartments();

    // 添加部门（直接返回 int，表示影响的行数）
    @Insert("INSERT INTO test_union.sys_department(parentId, deptName, manager, phone, email, sort, status, createBy, createTime) " +
            "VALUES(#{parentId}, #{deptName}, #{manager}, #{phone}, #{email}, #{sort}, #{status}, #{createBy}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    // 主键回填
    int addDepartment(Department department);

    // 更新部门（直接返回 int）
    @Update("UPDATE test_union.sys_department SET " +
            "parentId = #{parentId}, " +
            "deptName = #{deptName}, " +
            "manager = #{manager}, " +
            "phone = #{phone}, " +
            "email = #{email}, " +
            "sort = #{sort}, " +
            "status = #{status}, " +
            "updateBy = #{updateBy}, " +
            "updateTime = #{updateTime} " +
            "WHERE id = #{id} ")
    int updateDepartment(Department department);

    // 删除部门（逻辑删除，返回影响行数）
    @Delete("DELETE FROM test_union.sys_department WHERE id = #{id}")
    int deleteDepartment(Long id);

    // 搜索部门
    @Select("<script>" +
            "SELECT * FROM test_union.sys_department WHERE 1=1 " +
            "<if test='deptName != null and deptName != \"\"'> AND deptName LIKE CONCAT('%', #{deptName}, '%')</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            "</script>")
    List<Department> searchDepartments(
            @Param("deptName") String deptName,
            @Param("status") Integer status);
}
