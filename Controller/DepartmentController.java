package com.example.train.Controller;

import com.example.train.entity.Department;
import com.example.train.mapper.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin
public class DepartmentController {
    @Autowired
    private DepartmentMapper departmentMapper;

    // 获取所有部门 (保持与BookController相同的风格)
    @RequestMapping("/get-all-dept")
    public List<Department> getAllDepartments() {
        return departmentMapper.getAllDepartments();
    }

    // 添加部门
    @RequestMapping("/add-dept")
    public int addDepartment(@RequestBody Department department) {
        // 只有当前端没传 status 时才设置默认值
        if (department.getStatus() == null) {
            department.setStatus(1); // 默认正常
        }
        department.setCreateTime(LocalDateTime.now());
        department.setCreateBy("admin");
        department.setDeleted(0);

        return departmentMapper.addDepartment(department);
    }

    // 更新部门
    @RequestMapping("/update-dept")
    public int updateDepartment(@RequestBody Department department) {
        department.setUpdateTime(LocalDateTime.now());
        department.setUpdateBy("admin");
        int result = departmentMapper.updateDepartment(department);

        // 如果本次是停用操作，则递归停用所有子部门
        if (department.getStatus() != null && department.getStatus() == 1) {
            disableChildDepartments(department.getId());
        }
        // 如果本次是启用操作，则递归启用所有子部门
        if (department.getStatus() != null && department.getStatus() == 0) {
            enableChildDepartments(department.getId());
        }
        return result;
    }

    /**
     * 递归停用所有子部门
     */
    private void disableChildDepartments(Long parentId) {
        List<Department> children = departmentMapper.getDepartmentsByParentId(parentId);
        for (Department child : children) {
            if (child.getStatus() != null && child.getStatus() == 0) {
                child.setStatus(1);
                child.setUpdateTime(LocalDateTime.now());
                child.setUpdateBy("admin");
                departmentMapper.updateDepartment(child);
                disableChildDepartments(child.getId());
            }
        }
    }

    /**
     * 递归启用所有子部门
     */
    private void enableChildDepartments(Long parentId) {
        List<Department> children = departmentMapper.getDepartmentsByParentId(parentId);
        for (Department child : children) {
            if (child.getStatus() != null && child.getStatus() == 1) {
                child.setStatus(0);
                child.setUpdateTime(LocalDateTime.now());
                child.setUpdateBy("admin");
                departmentMapper.updateDepartment(child);
                enableChildDepartments(child.getId());
            }
        }
    }

    // 删除部门
    @RequestMapping("/delete-dept")
    public int deleteDepartment(@RequestParam Long id) {
        return departmentMapper.deleteDepartment(id);
    }


    // 搜索部门
    @RequestMapping("/search-dept")
    public List<Department> searchDepartments(
            @RequestParam(required = false) String deptName,
            @RequestParam(required = false) Integer status) {
        return departmentMapper.searchDepartments(deptName, status);
    }
}