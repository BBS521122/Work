package com.work.work.controller;


import com.work.work.entity.Department;
import com.work.work.mapper.sql.DepartmentMapper;
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
        return departmentMapper.updateDepartment(department);
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
        //System.out.println("deptName=" + deptName + ", status=" + status);
        return departmentMapper.searchDepartments(deptName, status);
    }
}