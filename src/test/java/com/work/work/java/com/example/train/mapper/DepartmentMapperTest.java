package com.example.train.mapper;

import com.example.train.entity.Department;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class DepartmentMapperTest {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Test
    @Sql(scripts = "/test-data.sql")
    void getAllDepartments() {
        List<Department> departments = departmentMapper.getAllDepartments();
        assertNotNull(departments);
        assertTrue(departments.size() > 0);
    }

    @Test
    @Sql(scripts = "/test-data.sql")
    void addDepartment() {
        Department newDept = new Department();
        newDept.setParentId(1L);
        newDept.setDeptName("测试部门");
        newDept.setSort(10);
        newDept.setStatus(0);
        newDept.setCreateBy("test");
        newDept.setCreateTime(LocalDateTime.now());

        int result = departmentMapper.addDepartment(newDept);
        assertEquals(1, result);
        assertNotNull(newDept.getId());
    }

    @Test
    @Sql(scripts = "/test-data.sql")
    void updateDepartment() {
        Department dept = departmentMapper.searchDepartments("研发部", null).get(0);
        dept.setDeptName("研发中心");

        int result = departmentMapper.updateDepartment(dept);
        assertEquals(1, result);

        Department updatedDept = departmentMapper.searchDepartments("研发中心", null).get(0);
        assertEquals("研发中心", updatedDept.getDeptName());
    }

    @Test
    @Sql(scripts = "/test-data.sql")
    void deleteDepartment() {
        int beforeCount = departmentMapper.getAllDepartments().size();
        int result = departmentMapper.deleteDepartment(1L);
        int afterCount = departmentMapper.getAllDepartments().size();

        assertEquals(1, result);
        assertEquals(beforeCount - 1, afterCount);
    }

    @Test
    @Sql(scripts = "/test-data.sql")
    void searchDepartments() {
        List<Department> result = departmentMapper.searchDepartments("研发", null);
        assertFalse(result.isEmpty());
        assertEquals("研发部", result.get(0).getDeptName());

        result = departmentMapper.searchDepartments(null, 1);
        assertEquals(1, result.get(0).getStatus());
    }
}