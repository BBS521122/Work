package com.example.train.Controller;

import com.example.train.entity.Department;
import com.example.train.mapper.DepartmentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class DepartmentControllerTest {

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentController departmentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllDepartments() {
        // 准备测试数据
        Department dept1 = createDepartment(1L, "研发部");
        Department dept2 = createDepartment(2L, "市场部");
        List<Department> departments = Arrays.asList(dept1, dept2);

        // 模拟Mapper行为
        when(departmentMapper.getAllDepartments()).thenReturn(departments);

        // 执行测试
        List<Department> result = departmentController.getAllDepartments();

        // 验证结果
        assertEquals(2, result.size());
        assertEquals("研发部", result.get(0).getDeptName());
    }

    @Test
    void addDepartment() {
        // 准备测试数据
        Department newDept = createDepartment(null, "新部门");
        newDept.setStatus(null);

        // 模拟Mapper行为
        when(departmentMapper.addDepartment(any(Department.class))).thenReturn(1);

        // 执行测试
        int result = departmentController.addDepartment(newDept);

        // 验证结果
        assertEquals(1, result);
        assertEquals(1, newDept.getStatus()); // 验证默认状态设置
        assertNotNull(newDept.getCreateTime()); // 验证创建时间设置
    }

    @Test
    void addDepartment_statusNotNull() {
        Department newDept = createDepartment(null, "有状态部门");
        newDept.setStatus(0); // 这里设为0或1都可以

        when(departmentMapper.addDepartment(any(Department.class))).thenReturn(1);

        int result = departmentController.addDepartment(newDept);

        assertEquals(1, result);
        assertEquals(0, newDept.getStatus()); // 保持原值，不会被覆盖
    }

    @Test
    void updateDepartment() {
        // 准备测试数据
        Department updatedDept = createDepartment(1L, "更新部门");
        updatedDept.setStatus(1);

        // 模拟Mapper行为
        when(departmentMapper.updateDepartment(any(Department.class))).thenReturn(1);

        // 执行测试
        int result = departmentController.updateDepartment(updatedDept);

        // 验证结果
        assertEquals(1, result);
        assertNotNull(updatedDept.getUpdateTime()); // 验证更新时间设置
    }

    @Test
    void deleteDepartment() {
        // 模拟Mapper行为
        when(departmentMapper.deleteDepartment(anyLong())).thenReturn(1);

        // 执行测试
        int result = departmentController.deleteDepartment(1L);

        // 验证结果
        assertEquals(1, result);
    }

    @Test
    void searchDepartments() {
        // 准备测试数据
        Department dept1 = createDepartment(1L, "技术部");
        List<Department> departments = Arrays.asList(dept1);

        // 模拟Mapper行为
        when(departmentMapper.searchDepartments(anyString(), anyInt()))
                .thenReturn(departments);

        // 执行测试
        List<Department> result = departmentController.searchDepartments("技术", 0);

        // 验证结果
        assertEquals(1, result.size());
        assertEquals("技术部", result.get(0).getDeptName());
    }

    // 辅助方法：创建部门对象
    private Department createDepartment(Long id, String name) {
        Department dept = new Department();
        dept.setId(id != null ? id : 1L);
        dept.setDeptName(name);
        dept.setParentId(0L);
        dept.setSort(1);
        dept.setStatus(0);
        return dept;
    }

    // 新增：测试递归禁用子部门
    @Test
    void updateDepartment_disableChildren() {
        Department parent = createDepartment(1L, "父部门");
        parent.setStatus(1); // 停用

        Department child = createDepartment(2L, "子部门");
        child.setStatus(0); // 子部门原本启用（0=启用，1=停用）

        when(departmentMapper.updateDepartment(any(Department.class))).thenReturn(1);
        when(departmentMapper.getDepartmentsByParentId(eq(1L))).thenReturn(Arrays.asList(child));
        when(departmentMapper.getDepartmentsByParentId(eq(2L))).thenReturn(Arrays.asList());

        int result = departmentController.updateDepartment(parent);
        assertEquals(1, result);

        ArgumentCaptor<Department> captor = ArgumentCaptor.forClass(Department.class);
        verify(departmentMapper, atLeast(2)).updateDepartment(captor.capture());
        // 检查 child 被设置为 1（停用）
        boolean childDisabled = captor.getAllValues().stream().anyMatch(d -> d.getId() == 2L && d.getStatus() == 1);
        assertTrue(childDisabled);
    }

    @Test
    void updateDepartment_enableChildren() {
        Department parent = createDepartment(1L, "父部门");
        parent.setStatus(0); // 启用

        Department child = createDepartment(2L, "子部门");
        child.setStatus(1); // 子部门原本停用（1=停用，0=启用）

        when(departmentMapper.updateDepartment(any(Department.class))).thenReturn(1);
        when(departmentMapper.getDepartmentsByParentId(eq(1L))).thenReturn(Arrays.asList(child));
        when(departmentMapper.getDepartmentsByParentId(eq(2L))).thenReturn(Arrays.asList());

        int result = departmentController.updateDepartment(parent);
        assertEquals(1, result);

        ArgumentCaptor<Department> captor = ArgumentCaptor.forClass(Department.class);
        verify(departmentMapper, atLeast(2)).updateDepartment(captor.capture());
        // 检查 child 被设置为 0（启用）
        boolean childEnabled = captor.getAllValues().stream().anyMatch(d -> d.getId() == 2L && d.getStatus() == 0);
        assertTrue(childEnabled);
    }

    @Test
    void updateDepartment_statusNull() {
        Department dept = createDepartment(1L, "无状态部门");
        dept.setStatus(null);

        when(departmentMapper.updateDepartment(any(Department.class))).thenReturn(1);

        int result = departmentController.updateDepartment(dept);
        assertEquals(1, result);
        // 不会进入递归分支
    }

    @Test
    void disableChildDepartments_childStatusNull() {
        Department parent = createDepartment(1L, "父部门");
        parent.setStatus(1);

        Department child = createDepartment(2L, "子部门");
        child.setStatus(null);

        when(departmentMapper.updateDepartment(any(Department.class))).thenReturn(1);
        when(departmentMapper.getDepartmentsByParentId(eq(1L))).thenReturn(Arrays.asList(child));
        when(departmentMapper.getDepartmentsByParentId(eq(2L))).thenReturn(Arrays.asList());

        int result = departmentController.updateDepartment(parent);
        assertEquals(1, result);
        // child.status 为 null，不会进入递归分支
    }

    @Test
    void disableChildDepartments_childStatusOther() {
        Department parent = createDepartment(1L, "父部门");
        parent.setStatus(1);

        Department child = createDepartment(2L, "子部门");
        child.setStatus(2); // 不是0也不是1

        when(departmentMapper.updateDepartment(any(Department.class))).thenReturn(1);
        when(departmentMapper.getDepartmentsByParentId(eq(1L))).thenReturn(Arrays.asList(child));
        when(departmentMapper.getDepartmentsByParentId(eq(2L))).thenReturn(Arrays.asList());

        int result = departmentController.updateDepartment(parent);
        assertEquals(1, result);
        // child.status 为2，不会进入递归分支
    }

    @Test
    void enableChildDepartments_childStatusNull() {
        Department parent = createDepartment(1L, "父部门");
        parent.setStatus(0);

        Department child = createDepartment(2L, "子部门");
        child.setStatus(null);

        when(departmentMapper.updateDepartment(any(Department.class))).thenReturn(1);
        when(departmentMapper.getDepartmentsByParentId(eq(1L))).thenReturn(Arrays.asList(child));
        when(departmentMapper.getDepartmentsByParentId(eq(2L))).thenReturn(Arrays.asList());

        int result = departmentController.updateDepartment(parent);
        assertEquals(1, result);
        // child.status 为 null，不会进入递归分支
    }

    @Test
    void enableChildDepartments_childStatusOther() {
        Department parent = createDepartment(1L, "父部门");
        parent.setStatus(0);

        Department child = createDepartment(2L, "子部门");
        child.setStatus(0); // 不是1

        when(departmentMapper.updateDepartment(any(Department.class))).thenReturn(1);
        when(departmentMapper.getDepartmentsByParentId(eq(1L))).thenReturn(Arrays.asList(child));
        when(departmentMapper.getDepartmentsByParentId(eq(2L))).thenReturn(Arrays.asList());

        int result = departmentController.updateDepartment(parent);
        assertEquals(1, result);
        // child.status 为0，不会进入递归分支
    }
}