<template>
  <div class="resource-home">
    <div class="page-header">
      <h2>学习资源</h2>
      <div class="header-actions">
        <el-button type="danger" :disabled="!selectedIds.length" @click="handleBatchDelete">
          批量删除
        </el-button>
        <el-button type="primary" @click="showUploadDialog">
          <el-icon><Upload /></el-icon>
          上传资源
        </el-button>
      </div>
    </div>

    <div class="resource-content">
      <div class="content-layout">
        <!-- 左侧分类树 -->
        <div class="category-panel">
          <div class="category-header">
            <span>资源分类</span>
            <el-button type="primary" link size="small" @click="showCategoryDialog()">
              <el-icon><Plus /></el-icon>
            </el-button>
          </div>
          <div class="category-tree">
            <div
              class="category-item"
              :class="{ active: !filterCategoryId }"
              @click="filterCategoryId = null; pageNum = 1; loadResources()"
            >全部分类</div>
            <el-tree
              :data="categoryTree"
              :props="{ label: 'categoryName', children: 'children' }"
              node-key="id"
              highlight-current
              @node-click="handleCategoryClick"
            >
              <template #default="{ node, data }">
                <span class="category-node">
                  <span>{{ node.label }}</span>
                  <span class="category-actions">
                    <el-icon size="12" @click.stop="showCategoryDialog(data)"><Edit /></el-icon>
                    <el-icon size="12" @click.stop="handleDeleteCategory(data)"><Delete /></el-icon>
                  </span>
                </span>
              </template>
            </el-tree>
          </div>
        </div>

        <!-- 右侧资源列表 -->
        <div class="resource-panel">
          <div class="filter-bar">
            <el-select v-model="filterType" placeholder="资源类型" clearable style="width: 120px" @change="loadResources">
              <el-option label="全部" value="" />
              <el-option label="视频" value="视频" />
              <el-option label="音频" value="音频" />
              <el-option label="文档" value="文档" />
            </el-select>
            <el-select v-model="filterLevel" placeholder="等级" clearable style="width: 120px" @change="loadResources">
              <el-option label="全部" value="" />
              <el-option label="CET4" value="CET4" />
              <el-option label="CET6" value="CET6" />
              <el-option label="IELTS" value="IELTS" />
              <el-option label="TOEFL" value="TOEFL" />
            </el-select>
            <el-select v-model="filterStatus" placeholder="状态" clearable style="width: 120px" @change="loadResources">
              <el-option label="全部" :value="null" />
              <el-option label="已发布" :value="1" />
              <el-option label="已下架" :value="0" />
            </el-select>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索资源名称/描述/标签"
              :prefix-icon="Search"
              clearable
              style="width: 260px"
              @input="debouncedSearch"
            />
          </div>

          <el-table
            :data="resources"
            style="width: 100%"
            v-loading="loading"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="50" />
            <el-table-column label="封面" width="100">
              <template #default="{ row }">
                <el-image
                  v-if="row.coverUrl"
                  :src="getFileUrl(row.coverUrl)"
                  style="width: 60px; height: 40px; border-radius: 4px;"
                  fit="cover"
                >
                  <template #error>
                    <el-image :src="DEFAULT_COVER" style="width: 60px; height: 40px; border-radius: 4px;" fit="cover" />
                  </template>
                </el-image>
                <el-image v-else :src="DEFAULT_COVER" style="width: 60px; height: 40px; border-radius: 4px;" fit="cover" />
              </template>
            </el-table-column>
            <el-table-column prop="resourceName" label="资源名称" min-width="150" />
            <el-table-column prop="resourceType" label="类型" width="80">
              <template #default="{ row }">
                <el-tag size="small">{{ row.resourceType }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="level" label="等级" width="80" />
            <el-table-column prop="tags" label="标签" width="150">
              <template #default="{ row }">
                <template v-if="row.tags">
                  <el-tag v-for="tag in row.tags.split(',')" :key="tag" size="small" type="info" style="margin: 2px;">{{ tag }}</el-tag>
                </template>
                <span v-else style="color: #999;">-</span>
              </template>
            </el-table-column>
            <el-table-column prop="viewCount" label="浏览" width="70" />
            <el-table-column prop="likeCount" label="点赞" width="70" />
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                  {{ row.status === 1 ? '已发布' : '已下架' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="上传时间" width="110">
              <template #default="{ row }">
                {{ row.createTime?.split(' ')[0] || '-' }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="260" fixed="right">
              <template #default="{ row }">
                <el-button
                  type="primary"
                  link
                  size="small"
                  :loading="previewingId === row.id"
                  @click="previewResource(row)"
                >预览</el-button>
                <el-button type="primary" link size="small" @click="handleDownload(row)">下载</el-button>
                <el-button type="primary" link size="small" @click="editResource(row)">编辑</el-button>
                <el-button
                  :type="row.status === 1 ? 'warning' : 'success'"
                  link size="small"
                  @click="toggleStatus(row)"
                >{{ row.status === 1 ? '下架' : '发布' }}</el-button>
                <el-button type="danger" link size="small" @click="deleteResource(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-bar">
            <el-pagination
              v-model:current-page="pageNum"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="loadResources"
              @current-change="loadResources"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 上传/编辑资源对话框 -->
    <el-dialog v-model="formDialogVisible" :title="isEditing ? '编辑资源' : '上传资源'" width="640px">
      <el-form ref="formRef" :model="resourceForm" :rules="formRules" label-width="100px">
        <el-form-item label="资源名称" prop="resourceName">
          <el-input v-model="resourceForm.resourceName" placeholder="请输入资源名称" />
        </el-form-item>
        <el-form-item label="资源类型" prop="resourceType">
          <el-select v-model="resourceForm.resourceType" placeholder="请选择资源类型" style="width: 100%">
            <el-option label="视频" value="视频" />
            <el-option label="音频" value="音频" />
            <el-option label="文档" value="文档" />
          </el-select>
        </el-form-item>
        <el-form-item label="资源分类" prop="categoryId">
          <el-tree-select
            v-model="resourceForm.categoryId"
            :data="categoryTree"
            :props="{ label: 'categoryName', children: 'children', value: 'id' }"
            placeholder="请选择分类"
            clearable
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="等级" prop="level">
          <el-select v-model="resourceForm.level" placeholder="请选择等级" style="width: 100%">
            <el-option label="CET4" value="CET4" />
            <el-option label="CET6" value="CET6" />
            <el-option label="IELTS" value="IELTS" />
            <el-option label="TOEFL" value="TOEFL" />
          </el-select>
        </el-form-item>
        <el-form-item label="技能类型" prop="skillType">
          <el-select v-model="resourceForm.skillType" placeholder="请选择技能类型" style="width: 100%">
            <el-option label="听力" value="听力" />
            <el-option label="阅读" value="阅读" />
            <el-option label="写作" value="写作" />
            <el-option label="词汇" value="词汇" />
            <el-option label="语法" value="语法" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-input v-model="resourceForm.tags" placeholder="多个标签用逗号分隔，如：听力,四级,真题" />
        </el-form-item>
        <el-form-item label="资源描述" prop="description">
          <el-input v-model="resourceForm.description" type="textarea" :rows="3" placeholder="请输入资源描述" />
        </el-form-item>
        <el-form-item label="封面图片">
          <div class="cover-upload">
            <div v-if="resourceForm.coverUrl" class="cover-preview">
              <el-image :src="getFileUrl(resourceForm.coverUrl) || DEFAULT_COVER" fit="cover" style="width: 120px; height: 80px; border-radius: 4px;" />
              <el-button type="danger" size="small" @click="resourceForm.coverUrl = ''">删除</el-button>
            </div>
            <el-button v-else type="primary" @click="selectCoverImage" :loading="coverUploading">
              <el-icon><Picture /></el-icon>
              选择封面图片
            </el-button>
          </div>
        </el-form-item>
        <el-form-item v-if="!isEditing" label="资源文件" prop="fileUrl">
          <div class="file-upload">
            <div v-if="resourceForm.fileUrl" class="file-info">
              <span>{{ uploadedFileName }}</span>
              <el-button type="danger" size="small" @click="resourceForm.fileUrl = ''; uploadedFileName = ''">删除</el-button>
            </div>
            <el-button v-else type="primary" @click="selectResourceFile" :loading="fileUploading">
              <el-icon><Upload /></el-icon>
              选择资源文件
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">
          {{ isEditing ? '保存' : '确定上传' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 分类管理对话框 -->
    <el-dialog v-model="categoryDialogVisible" :title="editingCategory ? '编辑分类' : '新增分类'" width="450px">
      <el-form :model="categoryForm" label-width="80px">
        <el-form-item label="分类名称" required>
          <el-input v-model="categoryForm.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="父级分类">
          <el-tree-select
            v-model="categoryForm.parentId"
            :data="categoryTree"
            :props="{ label: 'categoryName', children: 'children', value: 'id' }"
            placeholder="无（顶级分类）"
            clearable
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="排序号">
          <el-input-number v-model="categoryForm.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="categoryForm.description" type="textarea" :rows="2" placeholder="分类描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCategory" :loading="categorySubmitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload, Search, Picture, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { learningApi } from '@/api/learning'
import { getFileUrl, DEFAULT_COVER } from '@/utils/file'

// ========== 列表与筛选 ==========
const filterType = ref('')
const filterLevel = ref('')
const filterStatus = ref(null)
const filterCategoryId = ref(null)
const searchKeyword = ref('')
const resources = ref([])
const loading = ref(false)
const previewingId = ref(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedIds = ref([])

let searchTimer = null
const debouncedSearch = () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    pageNum.value = 1
    loadResources()
  }, 400)
}

const handleSelectionChange = (rows) => {
  selectedIds.value = rows.map(r => r.id)
}

const loadResources = async () => {
  loading.value = true
  try {
    const res = await learningApi.getResourceList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      resourceType: filterType.value || undefined,
      level: filterLevel.value || undefined,
      status: filterStatus.value,
      categoryId: filterCategoryId.value || undefined,
      keyword: searchKeyword.value || undefined
    })
    if (res.data && res.data.records) {
      resources.value = res.data.records
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载资源失败:', error)
  } finally {
    loading.value = false
  }
}

// ========== 分类树 ==========
const categoryTree = ref([])

const loadCategoryTree = async () => {
  try {
    const res = await learningApi.getCategoryTree()
    categoryTree.value = res.data || []
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const handleCategoryClick = (data) => {
  filterCategoryId.value = data.id
  pageNum.value = 1
  loadResources()
}

// ========== 分类管理对话框 ==========
const categoryDialogVisible = ref(false)
const editingCategory = ref(null)
const categoryForm = ref({ categoryName: '', parentId: null, sortOrder: 0, description: '' })
const categorySubmitting = ref(false)

const showCategoryDialog = (category = null) => {
  editingCategory.value = category
  if (category) {
    categoryForm.value = {
      categoryName: category.categoryName,
      parentId: category.parentId || null,
      sortOrder: category.sortOrder || 0,
      description: category.description || ''
    }
  } else {
    categoryForm.value = { categoryName: '', parentId: null, sortOrder: 0, description: '' }
  }
  categoryDialogVisible.value = true
}

const submitCategory = async () => {
  if (!categoryForm.value.categoryName) {
    ElMessage.warning('请输入分类名称')
    return
  }
  categorySubmitting.value = true
  try {
    const data = {
      categoryName: categoryForm.value.categoryName,
      parentId: categoryForm.value.parentId || 0,
      sortOrder: categoryForm.value.sortOrder,
      description: categoryForm.value.description
    }
    if (editingCategory.value) {
      await learningApi.updateCategory(editingCategory.value.id, data)
      ElMessage.success('分类更新成功')
    } else {
      await learningApi.createCategory(data)
      ElMessage.success('分类创建成功')
    }
    categoryDialogVisible.value = false
    loadCategoryTree()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  } finally {
    categorySubmitting.value = false
  }
}

const handleDeleteCategory = (data) => {
  ElMessageBox.confirm('确定要删除该分类吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await learningApi.deleteCategory(data.id)
      ElMessage.success('删除成功')
      loadCategoryTree()
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }).catch(() => {})
}

// ========== 资源表单（上传/编辑共用） ==========
const formDialogVisible = ref(false)
const formRef = ref(null)
const isEditing = ref(false)
const editingId = ref(null)
const resourceForm = ref({
  resourceName: '',
  resourceType: '视频',
  categoryId: null,
  level: 'CET4',
  skillType: '听力',
  tags: '',
  description: '',
  coverUrl: '',
  fileUrl: ''
})
const uploadedFileName = ref('')
const coverUploading = ref(false)
const fileUploading = ref(false)
const submitting = ref(false)

const formRules = {
  resourceName: [{ required: true, message: '请输入资源名称', trigger: 'blur' }],
  resourceType: [{ required: true, message: '请选择资源类型', trigger: 'change' }],
  fileUrl: [{ required: true, message: '请上传资源文件', trigger: 'change' }]
}

const showUploadDialog = () => {
  isEditing.value = false
  editingId.value = null
  resourceForm.value = {
    resourceName: '',
    resourceType: '视频',
    categoryId: null,
    level: 'CET4',
    skillType: '听力',
    tags: '',
    description: '',
    coverUrl: '',
    fileUrl: ''
  }
  uploadedFileName.value = ''
  formDialogVisible.value = true
}

const editResource = (row) => {
  isEditing.value = true
  editingId.value = row.id
  resourceForm.value = {
    resourceName: row.resourceName,
    resourceType: row.resourceType,
    categoryId: row.categoryId || null,
    level: row.level || '',
    skillType: row.skillType || '',
    tags: row.tags || '',
    description: row.description || '',
    coverUrl: row.coverUrl || '',
    fileUrl: row.fileUrl || ''
  }
  formDialogVisible.value = true
}

const selectCoverImage = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = async (e) => {
    const file = e.target.files[0]
    if (file) {
      coverUploading.value = true
      try {
        const res = await learningApi.uploadImage(file)
        resourceForm.value.coverUrl = res.data.url
        ElMessage.success('封面上传成功')
      } catch (error) {
        ElMessage.error('封面上传失败')
      } finally {
        coverUploading.value = false
      }
    }
  }
  input.click()
}

const selectResourceFile = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.mp4,.mp3,.wav,.pdf,.doc,.docx,.ppt,.pptx'
  input.onchange = async (e) => {
    const file = e.target.files[0]
    if (file) {
      fileUploading.value = true
      try {
        const ext = file.name.split('.').pop().toLowerCase()
        let uploadFn = learningApi.uploadDocument
        if (['mp4', 'avi', 'mov'].includes(ext)) uploadFn = learningApi.uploadVideo
        else if (['mp3', 'wav', 'ogg'].includes(ext)) uploadFn = learningApi.uploadAudio

        const res = await uploadFn(file)
        resourceForm.value.fileUrl = res.data.url
        uploadedFileName.value = file.name
        ElMessage.success('文件上传成功')
      } catch (error) {
        ElMessage.error('文件上传失败')
      } finally {
        fileUploading.value = false
      }
    }
  }
  input.click()
}

const submitForm = async () => {
  if (formRef.value) {
    try {
      await formRef.value.validate()
    } catch {
      return
    }
  }
  submitting.value = true
  try {
    const data = {
      resourceName: resourceForm.value.resourceName,
      resourceType: resourceForm.value.resourceType,
      categoryId: resourceForm.value.categoryId,
      level: resourceForm.value.level,
      skillType: resourceForm.value.skillType,
      tags: resourceForm.value.tags,
      description: resourceForm.value.description,
      coverUrl: resourceForm.value.coverUrl,
      fileUrl: resourceForm.value.fileUrl
    }
    if (isEditing.value) {
      await learningApi.updateResource(editingId.value, data)
      ElMessage.success('修改成功')
    } else {
      await learningApi.createResource(data)
      ElMessage.success('资源创建成功')
    }
    formDialogVisible.value = false
    loadResources()
  } catch (error) {
    ElMessage.error(isEditing.value ? '修改失败' : '创建失败')
  } finally {
    submitting.value = false
  }
}

// ========== 资源操作 ==========
const getPreviewPath = (resource = {}) => {
  return resource.fileUrl || resource.previewUrl || resource.resourceUrl || resource.url || resource.filePath || ''
}

const openPreview = (path) => {
  const previewUrl = getFileUrl(path)
  if (!previewUrl) return false
  window.open(previewUrl, '_blank', 'noopener,noreferrer')
  return true
}

const previewResource = async (row) => {
  if (previewingId.value === row.id) return

  previewingId.value = row.id
  const directPath = getPreviewPath(row)
  if (directPath) {
    openPreview(directPath)
    previewingId.value = null
    return
  }

  try {
    const res = await learningApi.getResourceDetail(row.id)
    const detailPath = getPreviewPath(res.data || {})
    if (detailPath) {
      const targetRow = resources.value.find(item => item.id === row.id)
      if (targetRow) {
        targetRow.fileUrl = detailPath
      }
      openPreview(detailPath)
      return
    }
    ElMessage.info('暂无预览链接')
  } catch (error) {
    ElMessage.error('获取预览链接失败')
  } finally {
    previewingId.value = null
  }
}

const handleDownload = async (row) => {
  const filePath = getPreviewPath(row)
  if (!filePath) {
    ElMessage.info('暂无可下载文件')
    return
  }
  try {
    await learningApi.downloadResource(row.id)
    const link = document.createElement('a')
    link.href = getFileUrl(filePath)
    link.download = row.resourceName || '资源文件'
    link.target = '_blank'
    link.click()
  } catch (error) {
    ElMessage.error('下载失败')
  }
}

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const label = newStatus === 1 ? '发布' : '下架'
  try {
    await learningApi.updateResourceStatus(row.id, newStatus)
    ElMessage.success(`${label}成功`)
    loadResources()
  } catch (error) {
    ElMessage.error(`${label}失败`)
  }
}

const deleteResource = (row) => {
  ElMessageBox.confirm('确定要删除该资源吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await learningApi.deleteResource(row.id)
      ElMessage.success('删除成功')
      loadResources()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个资源吗？`, '批量删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await learningApi.batchDeleteResource(selectedIds.value)
      ElMessage.success('批量删除成功')
      selectedIds.value = []
      loadResources()
    } catch (error) {
      ElMessage.error('批量删除失败')
    }
  }).catch(() => {})
}

// ========== 初始化 ==========
onMounted(() => {
  loadResources()
  loadCategoryTree()
})
</script>

<style lang="scss" scoped>
.resource-home {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    h2 {
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary);
    }

    .header-actions {
      display: flex;
      gap: 12px;
    }
  }

  .resource-content {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;

    .content-layout {
      display: flex;
      gap: 20px;
    }

    .category-panel {
      width: 220px;
      flex-shrink: 0;
      border-right: 1px solid var(--border-color, #ebeef5);
      padding-right: 20px;

      .category-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-weight: 600;
        margin-bottom: 12px;
        padding-bottom: 8px;
        border-bottom: 1px solid var(--border-color, #ebeef5);
      }

      .category-item {
        padding: 8px 12px;
        cursor: pointer;
        border-radius: 4px;
        margin-bottom: 4px;
        font-size: 14px;

        &:hover, &.active {
          background: var(--el-color-primary-light-9, #ecf5ff);
          color: var(--el-color-primary, #409eff);
        }
      }

      .category-node {
        display: flex;
        justify-content: space-between;
        align-items: center;
        width: 100%;

        .category-actions {
          display: none;
          gap: 6px;
          color: #999;

          .el-icon:hover {
            color: var(--el-color-primary);
          }
        }

        &:hover .category-actions {
          display: flex;
        }
      }
    }

    .resource-panel {
      flex: 1;
      min-width: 0;
    }

    .filter-bar {
      display: flex;
      gap: 12px;
      margin-bottom: 20px;
    }

    .pagination-bar {
      display: flex;
      justify-content: flex-end;
      margin-top: 20px;
    }
  }

  .cover-upload, .file-upload {
    .cover-preview, .file-info {
      display: flex;
      align-items: center;
      gap: 12px;
    }
  }
}
</style>
