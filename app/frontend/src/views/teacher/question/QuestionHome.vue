<template>
  <div class="question-home">
    <div class="page-header">
      <h2>题库管理</h2>
      <div class="header-actions">
        <el-button @click="downloadTemplate">
          <el-icon><Download /></el-icon>
          下载模板
        </el-button>
        <el-button @click="importQuestions">
          <el-icon><Upload /></el-icon>
          批量导入
        </el-button>
        <el-button type="primary" @click="addQuestion">
          <el-icon><Plus /></el-icon>
          添加试题
        </el-button>
      </div>
    </div>

    <div class="question-content">
      <!-- 左侧分类树 -->
      <div class="category-tree">
        <div class="tree-header">
          <span>题库分类</span>
          <el-button type="primary" link size="small" @click="addCategory(null)">
            <el-icon><Plus /></el-icon>
          </el-button>
        </div>
        <div class="tree-actions">
          <el-button type="primary" link size="small" @click="showAllQuestions">全部题目</el-button>
        </div>
        <el-tree
          ref="categoryTreeRef"
          :data="categoryTree"
          :props="{ label: 'name', children: 'children' }"
          node-key="id"
          default-expand-all
          highlight-current
          @node-click="handleCategoryClick"
        >
          <template #default="{ node, data }">
            <span class="tree-node">
              <span>{{ node.label }}</span>
              <span class="node-count">({{ data.count || 0 }})</span>
              <span class="tree-node-actions">
                <el-button type="primary" link size="small" @click.stop="addCategory(data.id)" title="添加子分类">
                  <el-icon><Plus /></el-icon>
                </el-button>
                <el-button type="primary" link size="small" @click.stop="editCategory(data)" title="编辑">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button type="danger" link size="small" @click.stop="deleteCategory(data)" title="删除">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </span>
            </span>
          </template>
        </el-tree>
      </div>

      <!-- 右侧题目列表 -->
      <div class="question-list">
        <!-- 筛选栏 -->
        <div class="filter-bar">
          <div class="filter-left">
            <el-select v-model="filterType" placeholder="题目类型" clearable style="width: 120px">
              <el-option label="单选题" value="single" />
              <el-option label="多选题" value="multiple" />
              <el-option label="填空题" value="fill" />
              <el-option label="翻译题" value="translation" />
              <el-option label="写作题" value="writing" />
            </el-select>
            <el-select v-model="filterDifficulty" placeholder="难度" clearable style="width: 100px">
              <el-option label="简单" :value="1" />
              <el-option label="中等" :value="2" />
              <el-option label="困难" :value="3" />
            </el-select>
          </div>
          <div class="filter-right">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索题目标题或内容"
              :prefix-icon="Search"
              clearable
              style="width: 240px"
            />
          </div>
        </div>

        <!-- 题目表格 -->
        <el-table :data="questions" style="width: 100%" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="题目内容" show-overflow-tooltip />
          <el-table-column prop="questionType" label="类型" width="100">
            <template #default="{ row }">
              <el-tag size="small">{{ getTypeName(row.questionType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="difficulty" label="难度" width="80">
            <template #default="{ row }">
              <el-tag :type="getDifficultyType(row.difficulty)" size="small">
                {{ getDifficultyName(row.difficulty) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="usageCount" label="使用次数" width="100" />
          <el-table-column prop="correctRate" label="正确率" width="100">
            <template #default="{ row }">
              {{ row.correctRate || 0 }}%
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="editQuestion(row)">编辑</el-button>
              <el-button type="primary" link size="small" @click="previewQuestion(row)">预览</el-button>
              <el-button type="danger" link size="small" @click="deleteQuestion(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 批量操作 -->
        <div v-if="selectedQuestions.length > 0" class="batch-actions">
          <span>已选择 {{ selectedQuestions.length }} 项</span>
          <el-button size="small" @click="batchExport">批量导出</el-button>
          <el-button type="danger" size="small" @click="batchDelete">批量删除</el-button>
        </div>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            background
          />
        </div>
      </div>
    </div>

    <!-- 添加/编辑题目弹窗 -->
    <el-dialog v-model="showQuestionDialog" :title="isEdit ? '编辑试题' : '添加试题'" width="800px" @close="resetForm">
      <el-form :model="questionForm" label-width="100px">
        <el-form-item label="题目类型">
          <el-select v-model="questionForm.type" style="width: 200px">
            <el-option label="单选题" value="single" />
            <el-option label="多选题" value="multiple" />
            <el-option label="填空题" value="fill" />
            <el-option label="翻译题" value="translation" />
            <el-option label="写作题" value="writing" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属分类">
          <el-cascader
            v-model="questionForm.category"
            :options="categoryTree"
            :props="{ label: 'name', value: 'id', children: 'children', checkStrictly: true, emitPath: false }"
            style="width: 300px"
            placeholder="请选择分类"
          />
        </el-form-item>
        <el-form-item label="难度等级">
          <el-radio-group v-model="questionForm.difficulty">
            <el-radio :value="1">简单</el-radio>
            <el-radio :value="2">中等</el-radio>
            <el-radio :value="3">困难</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="题目内容">
          <el-input v-model="questionForm.stem" type="textarea" :rows="3" placeholder="请输入题目内容" />
        </el-form-item>
        <el-form-item v-if="['single', 'multiple'].includes(questionForm.type)" label="选项">
          <div v-for="(option, index) in questionForm.options" :key="index" class="option-input">
            <span class="option-label">{{ String.fromCharCode(65 + index) }}.</span>
            <el-input v-model="questionForm.options[index]" placeholder="请输入选项内容" />
            <el-button v-if="index > 1" type="danger" :icon="Delete" circle size="small" @click="removeOption(index)" />
          </div>
          <el-button type="primary" link @click="addOption">+ 添加选项</el-button>
        </el-form-item>
        <el-form-item label="正确答案">
          <el-input v-model="questionForm.answer" placeholder="请输入正确答案" />
        </el-form-item>
        <el-form-item label="答案解析">
          <el-input v-model="questionForm.explanation" type="textarea" :rows="3" placeholder="请输入答案解析" />
        </el-form-item>
        <el-form-item label="分值">
          <el-input-number v-model="questionForm.score" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="questionForm.keywords" placeholder="多个关键词用逗号分隔" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showQuestionDialog = false">取消</el-button>
        <el-button type="primary" @click="saveQuestion">保存</el-button>
      </template>
    </el-dialog>

    <!-- 添加分类弹窗 -->
    <el-dialog v-model="showCategoryDialog" :title="isCategoryEdit ? '编辑分类' : '添加分类'" width="500px">
      <el-form :model="categoryForm" label-width="100px">
        <el-form-item label="分类名称">
          <el-input v-model="categoryForm.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类编码">
          <el-input v-model="categoryForm.categoryCode" placeholder="留空自动生成" />
        </el-form-item>
        <el-form-item label="等级">
          <el-select v-model="categoryForm.level" placeholder="请选择等级" clearable style="width: 200px">
            <el-option label="CET4" value="CET4" />
            <el-option label="CET6" value="CET6" />
            <el-option label="IELTS" value="IELTS" />
            <el-option label="TOEFL" value="TOEFL" />
          </el-select>
        </el-form-item>
        <el-form-item label="技能类型">
          <el-select v-model="categoryForm.skillType" placeholder="请选择技能类型" clearable style="width: 200px">
            <el-option label="听力" value="听力" />
            <el-option label="阅读" value="阅读" />
            <el-option label="写作" value="写作" />
            <el-option label="翻译" value="翻译" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCategoryDialog = false">取消</el-button>
        <el-button type="primary" @click="saveCategory">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Upload, Download, Search, Delete, Edit } from '@element-plus/icons-vue'
import { questionApi } from '@/api/question'

const categoryTreeRef = ref(null)
const filterType = ref('')
const filterDifficulty = ref('')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const selectedQuestions = ref([])
const showQuestionDialog = ref(false)
const isEdit = ref(false)
const selectedCategoryId = ref(null)

// 分类树
const categoryTree = ref([])

// 题目列表
const questions = ref([])

// 分类弹窗
const showCategoryDialog = ref(false)
const isCategoryEdit = ref(false)
const categoryForm = reactive({
  id: null,
  parentId: null,
  categoryName: '',
  categoryCode: '',
  level: '',
  skillType: ''
})

// 题目表单
const defaultFormValues = {
  id: null,
  type: 'single',
  category: null,
  difficulty: 2,
  stem: '',
  options: ['', '', '', ''],
  answer: '',
  explanation: '',
  score: 5,
  keywords: ''
}

const questionForm = reactive({ ...defaultFormValues })

const resetForm = () => {
  Object.assign(questionForm, {
    ...defaultFormValues,
    options: ['', '', '', '']
  })
}

// ==================== 类型/难度映射 ====================

const getTypeName = (type) => {
  const names = {
    single: '单选题', SINGLE_CHOICE: '单选题',
    multiple: '多选题', MULTIPLE_CHOICE: '多选题',
    reading: '阅读题', READING: '阅读题',
    cloze: '完形填空', CLOZE: '完形填空',
    listening: '听力题', LISTENING: '听力题',
    fill: '填空题',
    translation: '翻译题', TRANSLATION: '翻译题',
    writing: '写作题', WRITING: '写作题'
  }
  return names[type] || type || '未知'
}

const getDifficultyType = (difficulty) => {
  const types = { 1: 'success', 2: 'warning', 3: 'danger', easy: 'success', medium: 'warning', hard: 'danger' }
  return types[difficulty] || ''
}

const getDifficultyName = (difficulty) => {
  const names = { 1: '简单', 2: '中等', 3: '困难', easy: '简单', medium: '中等', hard: '困难' }
  return names[difficulty] || difficulty || '未知'
}

// 难度：后端数字 -> 前端数字（统一用数字）
const mapDifficultyFromBackend = (d) => {
  if (typeof d === 'number') return d
  const map = { easy: 1, medium: 2, hard: 3 }
  return map[d] || 2
}

// ==================== 分类管理 ====================

const handleCategoryClick = (data) => {
  selectedCategoryId.value = data.id
  currentPage.value = 1
  loadQuestions()
}

const showAllQuestions = () => {
  selectedCategoryId.value = null
  if (categoryTreeRef.value) {
    categoryTreeRef.value.setCurrentKey(null)
  }
  currentPage.value = 1
  loadQuestions()
}

const handleSelectionChange = (selection) => {
  selectedQuestions.value = selection
}

const addCategory = (parentId) => {
  isCategoryEdit.value = false
  Object.assign(categoryForm, {
    id: null,
    parentId: parentId,
    categoryName: '',
    categoryCode: '',
    level: '',
    skillType: ''
  })
  showCategoryDialog.value = true
}

const editCategory = (data) => {
  isCategoryEdit.value = true
  Object.assign(categoryForm, {
    id: data.id,
    parentId: null,
    categoryName: data.categoryName || data.name,
    categoryCode: data.categoryCode || '',
    level: data.level || '',
    skillType: data.skillType || ''
  })
  showCategoryDialog.value = true
}

const deleteCategory = (data) => {
  ElMessageBox.confirm(`确定要删除分类"${data.name || data.categoryName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await questionApi.deleteCategory(data.id)
      ElMessage.success('删除成功')
      loadCategories()
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }).catch(() => {})
}

const saveCategory = async () => {
  if (!categoryForm.categoryName) {
    ElMessage.warning('请输入分类名称')
    return
  }
  try {
    const data = {
      categoryName: categoryForm.categoryName,
      categoryCode: categoryForm.categoryCode || undefined,
      parentId: categoryForm.parentId,
      level: categoryForm.level || undefined,
      skillType: categoryForm.skillType || undefined
    }
    if (isCategoryEdit.value) {
      await questionApi.updateCategory(categoryForm.id, data)
    } else {
      await questionApi.createCategory(data)
    }
    ElMessage.success('保存成功')
    showCategoryDialog.value = false
    loadCategories()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '保存失败')
  }
}

// ==================== 导入导出 ====================

const importQuestions = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.xlsx,.xls'
  input.onchange = async (e) => {
    const file = e.target.files[0]
    if (file) {
      try {
        const res = await questionApi.importQuestions(file)
        ElMessage.success(res.data?.message || '导入成功')
        loadQuestions()
        loadCategories()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '导入失败')
      }
    }
  }
  input.click()
}

const downloadTemplate = async () => {
  try {
    const res = await questionApi.downloadTemplate()
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = '题目导入模板.xlsx'
    a.click()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    ElMessage.error('下载模板失败')
  }
}

// ==================== 题目 CRUD ====================

const addQuestion = () => {
  isEdit.value = false
  resetForm()
  // 如果当前选中了分类，自动填入
  if (selectedCategoryId.value) {
    questionForm.category = selectedCategoryId.value
  }
  showQuestionDialog.value = true
}

const editQuestion = (row) => {
  isEdit.value = true
  resetForm()

  questionForm.id = row.id
  questionForm.type = row.questionType || 'single'
  questionForm.category = row.categoryId || null
  questionForm.difficulty = mapDifficultyFromBackend(row.difficulty)
  questionForm.stem = row.title || ''
  questionForm.answer = row.answer || ''
  questionForm.explanation = row.analysis || ''
  questionForm.score = row.score || 5
  questionForm.keywords = row.keywords || ''

  // 正确解析 options：后端存的是 JSON 字符串
  if (row.options) {
    try {
      const parsed = typeof row.options === 'string' ? JSON.parse(row.options) : row.options
      questionForm.options = Array.isArray(parsed) ? parsed : ['', '', '', '']
    } catch {
      questionForm.options = ['', '', '', '']
    }
  } else {
    questionForm.options = ['', '', '', '']
  }

  showQuestionDialog.value = true
}

const previewQuestion = (row) => {
  // 使用纯文本预览，避免 XSS
  const title = row.title || ''
  const type = getTypeName(row.questionType)
  const diff = getDifficultyName(row.difficulty)
  const rate = row.correctRate || 0

  let optionsText = ''
  if (row.options) {
    try {
      const opts = typeof row.options === 'string' ? JSON.parse(row.options) : row.options
      if (Array.isArray(opts)) {
        optionsText = '\n选项：\n' + opts.map((o, i) => `  ${String.fromCharCode(65 + i)}. ${o}`).join('\n')
      }
    } catch { /* ignore */ }
  }

  const answer = row.answer ? `\n答案：${row.answer}` : ''
  const analysis = row.analysis ? `\n解析：${row.analysis}` : ''

  ElMessageBox.alert(
    `题目：${title}\n类型：${type}\n难度：${diff}\n正确率：${rate}%${optionsText}${answer}${analysis}`,
    '题目预览',
    { confirmButtonText: '关闭' }
  )
}

const deleteQuestion = (row) => {
  ElMessageBox.confirm('确定要删除这道题目吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await questionApi.deleteQuestion(row.id)
      ElMessage.success('删除成功')
      loadQuestions()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const addOption = () => {
  if (questionForm.options.length < 6) {
    questionForm.options.push('')
  }
}

const removeOption = (index) => {
  questionForm.options.splice(index, 1)
}

const saveQuestion = async () => {
  if (!questionForm.stem) {
    ElMessage.warning('请输入题目内容')
    return
  }
  if (!questionForm.answer) {
    ElMessage.warning('请输入正确答案')
    return
  }

  try {
    const data = {
      questionType: questionForm.type,
      categoryId: questionForm.category,
      difficulty: questionForm.difficulty,
      title: questionForm.stem,
      content: questionForm.stem,
      options: JSON.stringify(questionForm.options),
      answer: questionForm.answer,
      analysis: questionForm.explanation,
      score: questionForm.score,
      keywords: questionForm.keywords || undefined
    }
    if (isEdit.value) {
      await questionApi.updateQuestion(questionForm.id, data)
    } else {
      await questionApi.createQuestion(data)
    }
    ElMessage.success('保存成功')
    showQuestionDialog.value = false
    loadQuestions()
    loadCategories()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '保存失败')
  }
}

// ==================== 批量操作 ====================

const batchExport = async () => {
  if (selectedQuestions.value.length === 0) {
    ElMessage.warning('请先选择要导出的题目')
    return
  }
  try {
    const ids = selectedQuestions.value.map(q => q.id).join(',')
    const res = await questionApi.exportQuestions({ ids })
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = '题目导出.xlsx'
    a.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

const batchDelete = () => {
  if (selectedQuestions.value.length === 0) {
    ElMessage.warning('请先选择要删除的题目')
    return
  }
  ElMessageBox.confirm(`确定要删除选中的 ${selectedQuestions.value.length} 道题目吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const ids = selectedQuestions.value.map(q => q.id)
      await questionApi.batchDelete(ids)
      ElMessage.success('删除成功')
      loadQuestions()
      loadCategories()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// ==================== 数据加载 ====================

const loadQuestions = async () => {
  try {
    const res = await questionApi.getQuestionList({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      categoryId: selectedCategoryId.value || undefined,
      questionType: filterType.value || undefined,
      difficulty: filterDifficulty.value || undefined,
      keyword: searchKeyword.value || undefined
    })
    if (res.data) {
      questions.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载题目失败:', error)
  }
}

const loadCategories = async () => {
  try {
    const res = await questionApi.getCategoryTree()
    if (res.data) {
      categoryTree.value = res.data
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

// 监听筛选条件变化
watch([filterType, filterDifficulty, currentPage, pageSize], () => {
  loadQuestions()
})

// 搜索防抖
let searchTimer = null
watch(searchKeyword, () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    currentPage.value = 1
    loadQuestions()
  }, 500)
})

onMounted(() => {
  loadQuestions()
  loadCategories()
})
</script>

<style lang="scss" scoped>
.question-home {
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

  .question-content {
    display: flex;
    gap: 24px;
  }

  .category-tree {
    width: 280px;
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 16px;
    flex-shrink: 0;

    .tree-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;
      font-weight: 600;
      color: var(--text-primary);
    }

    .tree-actions {
      margin-bottom: 12px;
      padding-bottom: 8px;
      border-bottom: 1px solid var(--border-color, #eee);
    }

    .tree-node {
      display: flex;
      align-items: center;
      gap: 4px;
      width: 100%;

      .node-count {
        font-size: 12px;
        color: var(--text-placeholder);
      }

      .tree-node-actions {
        margin-left: auto;
        display: none;

        .el-button {
          padding: 2px;
        }
      }

      &:hover .tree-node-actions {
        display: flex;
      }
    }
  }

  .question-list {
    flex: 1;
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;

    .filter-bar {
      display: flex;
      justify-content: space-between;
      margin-bottom: 20px;

      .filter-left {
        display: flex;
        gap: 12px;
      }
    }

    .batch-actions {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 16px;
      background: var(--bg-secondary);
      border-radius: var(--radius-md);
      margin-top: 16px;

      span {
        color: var(--text-secondary);
      }
    }

    .pagination-wrapper {
      display: flex;
      justify-content: center;
      padding-top: 24px;
    }
  }

  .option-input {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;

    .option-label {
      width: 24px;
      font-weight: 500;
    }

    .el-input {
      flex: 1;
    }
  }
}
</style>
