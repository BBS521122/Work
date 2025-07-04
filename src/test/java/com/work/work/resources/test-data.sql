DELETE FROM sys_department;
-- 插入测试数据到正式表
INSERT INTO sys_department (id, parentId, deptName, manager, phone, email, sort, status, createBy, createTime, updateBy, updateTime)
VALUES (1, 0, '研发部', '张经理', '13800138000', 'dev@example.com', 1, 0, 'admin', '2023-01-01 00:00:00', null, null);

INSERT INTO sys_department (id, parentId, deptName, manager, phone, email, sort, status, createBy, createTime, updateBy, updateTime)
VALUES (2, 1, '前端组', '李组长', '13900139000', 'frontend@example.com', 1, 0, 'admin', '2023-01-02 00:00:00', null, null);

INSERT INTO sys_department (id, parentId, deptName, manager, phone, email, sort, status, createBy, createTime, updateBy, updateTime)
VALUES (3, 0, '人事部', '王经理', '13700137000', 'hr@example.com', 2, 1, 'admin', '2023-01-03 00:00:00', null, null);