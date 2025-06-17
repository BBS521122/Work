<template>
  <div class="department-management">
    <!-- 头部区域 -->
    <div class="header">
      <h1>部门管理系统</h1>
      <div class="user-info">
        <el-avatar>{{ user.name.charAt(0) }}</el-avatar>
        <div>
          <div>{{ user.name }} ({{ user.role }})</div>
          <div class="last-login">最后登录: {{ user.lastLogin }}</div>
        </div>
      </div>
    </div>

    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="部门名称">
          <el-input v-model="searchForm.name" clearable placeholder="输入部门名称" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" clearable placeholder="全部">
            <el-option label="正常" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><plus /></el-icon> 新增部门
      </el-button>
      <div class="stats">
        <el-tag type="success">正常: {{ stats.active }}</el-tag>
        <el-tag type="danger">停用: {{ stats.inactive }}</el-tag>
      </div>
    </div>

    <!-- 部门表格 -->
    <el-table
      :data="tableData"
      row-key="id"
      :tree-props="{ children: 'children' }"
      v-loading="loading"
      border
    >
      <el-table-column prop="name" label="部门名称" min-width="200" />
      <el-table-column prop="manager" label="负责人" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'danger'">
            {{ row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="pagination.current"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      layout="total, sizes, prev, pager, next"
      @size-change="loadData"
      @current-change="loadData"
    />

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="上级部门" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="departmentTree"
            check-strictly
            placeholder="请选择"
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

// 用户信息
const user = reactive({
  name: '管理员',
  role: 'admin',
  lastLogin: '2023-07-20 14:30'
})

// 搜索表单
const searchForm = reactive({
  name: '',
  status: ''
})

// 分页配置
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 弹窗控制
const dialog = reactive({
  visible: false,
  title: '新增部门',
  isEdit: false
})

// 表单数据
const form = reactive({
  id: null,
  parentId: null,
  name: '',
  status: '0'
})

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
  parentId: [{ required: true, message: '请选择上级部门', trigger: 'change' }]
}

// 部门树数据
const departmentTree = ref([])

// 统计信息
const stats = computed(() => {
  const active = tableData.value.filter(d => d.status === '0').length
  const inactive = tableData.value.filter(d => d.status === '1').length
  return { active, inactive, total: active + inactive }
})

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    // 这里替换为实际API调用
    const mockData = [
      { id: 1, parentId: 0, name: '总部', status: '0' },
      { id: 2, parentId: 1, name: '技术部', status: '0' }
    ]
    tableData.value = mockData
    buildDepartmentTree()
  } finally {
    loading.value = false
  }
}

// 构建部门树
const buildDepartmentTree = () => {
  departmentTree.value = [
    { id: 0, name: '根部门', children: [] }
  ]
  // 实际项目中这里需要递归构建树形结构
}

// 重置搜索
const resetSearch = () => {
  searchForm.name = ''
  searchForm.status = ''
  loadData()
}

// 新增部门
const handleAdd = () => {
  dialog.title = '新增部门'
  dialog.isEdit = false
  dialog.visible = true
  Object.assign(form, {
    id: null,
    parentId: null,
    name: '',
    status: '0'
  })
}

// 编辑部门
const handleEdit = (row) => {
  dialog.title = '编辑部门'
  dialog.isEdit = true
  dialog.visible = true
  Object.assign(form, { ...row })
}

// 删除部门
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除 ${row.name} 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    loadData()
  })
}

// 提交表单
const submitForm = () => {
  // 这里添加实际API调用
  ElMessage.success(dialog.isEdit ? '修改成功' : '新增成功')
  dialog.visible = false
  loadData()
}

// 初始化加载
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.department-management {
  padding: 20px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}
.last-login {
  font-size: 12px;
  color: #888;
}
.search-card {
  margin-bottom: 20px;
}
.action-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}
.stats {
  display: flex;
  gap: 10px;
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>