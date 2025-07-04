package com.example.train.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class DepartmentTest {

    @Test
    void testGetterSetter() {
        // 创建对象并设置值
        Department dept = new Department();
        dept.setId(1L);
        dept.setParentId(0L);
        dept.setDeptName("测试部门");
        dept.setManager("张经理");
        dept.setPhone("13800138000");
        dept.setEmail("test@example.com");
        dept.setSort(1);
        dept.setStatus(0);

        LocalDateTime now = LocalDateTime.now();
        dept.setCreateTime(now);
        dept.setUpdateTime(now);

        // 新增：补全未覆盖的字段
        dept.setCreateBy("admin");
        dept.setUpdateBy("admin2");
        dept.setDeleted(1);

        // 验证Getter方法
        assertEquals(1L, dept.getId());
        assertEquals(0L, dept.getParentId());
        assertEquals("测试部门", dept.getDeptName());
        assertEquals("张经理", dept.getManager());
        assertEquals("13800138000", dept.getPhone());
        assertEquals("test@example.com", dept.getEmail());
        assertEquals(1, dept.getSort());
        assertEquals(0, dept.getStatus());
        assertEquals(now, dept.getCreateTime());
        assertEquals(now, dept.getUpdateTime());
        // 新增断言
        assertEquals("admin", dept.getCreateBy());
        assertEquals("admin2", dept.getUpdateBy());
        assertEquals(1, dept.getDeleted());
    }

    @Test
    void testStatusType() {
        Department dept = new Department();

        // 验证Integer类型可以接受null值
        dept.setStatus(null);
        assertNull(dept.getStatus());

        dept.setStatus(1);
        assertEquals(1, dept.getStatus());
    }
}