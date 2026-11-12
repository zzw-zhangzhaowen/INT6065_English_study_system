<template>
  <el-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    :title="`管理题目 - ${examName}`"
    fullscreen
    @open="onOpen"
  >
    <div class="question-dialog-content">
      <!-- 左侧：已选题目 -->
      <div class="selected-panel">
        <div class="panel-header">
          <h4>已选题目 ({{ examQuestions.length }})</h4>
          <span class="total-score">总分：{{ totalScore }}</span>
        </div>
        <el-table :data="examQuestions" size="small" max-height="calc(100vh - 220px)" v-loading="loadingSelected">
          <el-table-column label="序号" width="60" type="index" />
          <el-table-column label="题目" min-width="180" show-overflow-tooltip>
            <template #default="{ row }">
              {{ row.title || row.content || '—' }}
            </template>
          </el-table-column>
          <el-table-column label="类型" width="90">
            <template #default="{ row }">
              <el-tag size="small">{{ getTypeName(row.questionType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="难度" width="70">
            <template #default="{ row }">
              <el-tag :type="getDifficultyType(row.difficulty)" size="small">{{ getDifficultyText(row.difficulty) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="分值" width="100">
            <template #default="{ row }">
              {{ row.score }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="70">
            <template #default="{ row }">
              <el-button type="danger" link size="small" @click="removeQuestion(row)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 右侧：题库浏览 -->
      <div class="bank-panel">
        <div class="panel-header">
          <h4>题库</h4>
        </div>
        <div class="bank-filters">
          <el-select v-model="bankType" placeholder="题目类型" clearable size="small" style="width: 110px">
            <el-option label="单选题" value="SINGLE_CHOICE" />
            <el-option label="多选题" value="MULTI_CHOICE" />
            <el-option label="判断题" value="TRUE_FALSE" />
            <el-option label="填空题" value="FILL_BLANK" />
            <el-option label="简答题" value="SHORT_ANSWER" />
            <el-option label="阅读理解" value="READING" />
            <el-option label="听力题" value="LISTENING" />
            <el-option label="翻译题" value="TRANSLATION" />
            <el-option label="写作题" value="WRITING" />
            <el-option label="完形填空" value="CLOZE" />
          </el-select>
          <el-select v-model="bankDifficulty" placeholder="难度" clearable size="small" style="width: 90px">
            <el-option label="简单" :value="1" />
            <el-option label="中等" :value="2" />
            <el-option label="困难" :value="3" />
          </el-select>
          <el-input v-model="bankKeyword" placeholder="搜索题目" clearable size="small" style="width: 180px" />
          <el-button type="primary" size="small" @click="loadBankQuestions">搜索</el-button>
        </div>
        <el-table
          ref="bankTableRef"
          :data="bankQuestions"
          size="small"
          max-height="calc(100vh - 310px)"
          v-loading="loadingBank"
          @selection-change="handleBankSelection"
        >
          <el-table-column type="selection" width="45" :selectable="isSelectable" />
          <el-table-column prop="id" label="ID" width="65" />
          <el-table-column label="题目" min-width="200" show-overflow-tooltip>
            <template #default="{ row }">
              <span v-if="isInExam(row.id)" style="color: var(--el-color-success);">[已添加] </span>
              {{ row.title || row.content || '—' }}
            </template>
          </el-table-column>
          <el-table-column label="类型" width="90">
            <template #default="{ row }">
              <el-tag size="small">{{ getTypeName(row.questionType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="难度" width="70">
            <template #default="{ row }">
              <el-tag :type="getDifficultyType(row.difficulty)" size="small">{{ getDifficultyText(row.difficulty) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="默认分值" width="80">
            <template #default="{ row }">
              {{ row.score || 5 }}
            </template>
          </el-table-column>
        </el-table>
        <div class="bank-footer">
          <el-button type="primary" size="small" :disabled="selectedBankQuestions.length === 0" @click="addSelected">
            添加选中 ({{ selectedBankQuestions.length }})
          </el-button>
          <el-pagination
            v-model:current-page="bankPage"
            v-model:page-size="bankPageSize"
            :total="bankTotal"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            size="small"
            @size-change="loadBankQuestions"
            @current-change="loadBankQuestions"
          />
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { examApi } from '@/api/exam'
import { questionApi } from '@/api/question'

const props = defineProps({
  modelValue: Boolean,
  examId: Number,
  examName: { type: String, default: '' }
})
const emit = defineEmits(['update:modelValue', 'updated'])

const loadingSelected = ref(false)
const loadingBank = ref(false)
const examQuestions = ref([])
const bankQuestions = ref([])
const selectedBankQuestions = ref([])
const bankTableRef = ref(null)

// 题库筛选
const bankType = ref('')
const bankDifficulty = ref('')
const bankKeyword = ref('')
const bankPage = ref(1)
const bankPageSize = ref(20)
const bankTotal = ref(0)

// 已选题目的 questionId 集合
const examQuestionIds = computed(() => new Set(examQuestions.value.map(q => q.questionId)))

const totalScore = computed(() => {
  return examQuestions.value.reduce((sum, q) => sum + (parseFloat(q.score) || 0), 0)
})

const onOpen = () => {
  loadExamQuestions()
  loadBankQuestions()
}

const loadExamQuestions = async () => {
  if (!props.examId) return
  loadingSelected.value = true
  try {
    const res = await examApi.getExamQuestions(props.examId)
    examQuestions.value = (res.data || []).map(q => ({
      examQuestionId: q.examQuestionId,
      questionId: q.questionId,
      title: q.title,
      questionType: q.questionType,
      difficulty: q.difficulty,
      score: q.score,
      content: q.content,
      order: q.order
    }))
  } catch (e) {
    console.error('加载考试题目失败', e)
  } finally {
    loadingSelected.value = false
  }
}

const loadBankQuestions = async () => {
  loadingBank.value = true
  try {
    const params = {
      pageNum: bankPage.value,
      pageSize: bankPageSize.value
    }
    if (bankType.value) params.questionType = bankType.value
    if (bankDifficulty.value) params.difficulty = bankDifficulty.value
    if (bankKeyword.value) params.keyword = bankKeyword.value

    const res = await questionApi.getQuestionList(params)
    if (res.data) {
      bankQuestions.value = res.data.records || []
      bankTotal.value = res.data.total || 0
    }
  } catch (e) {
    console.error('加载题库失败', e)
  } finally {
    loadingBank.value = false
  }
}

const isInExam = (questionId) => examQuestionIds.value.has(questionId)
const isSelectable = (row) => !isInExam(row.id)

const handleBankSelection = (selection) => {
  selectedBankQuestions.value = selection
}

const addSelected = async () => {
  if (selectedBankQuestions.value.length === 0) return
  try {
    const questions = selectedBankQuestions.value.map(q => ({
      questionId: q.id,
      questionScore: q.score || 5
    }))
    await examApi.batchAddExamQuestions(props.examId, questions)
    ElMessage.success(`成功添加 ${questions.length} 道题目`)
    loadExamQuestions()
    emit('updated')
    // 清除选择
    if (bankTableRef.value) bankTableRef.value.clearSelection()
    selectedBankQuestions.value = []
  } catch (e) {
    ElMessage.error(e.message || '添加失败')
  }
}

const removeQuestion = (row) => {
  ElMessageBox.confirm('确定移除该题目？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await examApi.removeExamQuestion(props.examId, row.questionId)
      ElMessage.success('已移除')
      loadExamQuestions()
      emit('updated')
    } catch (e) {
      ElMessage.error(e.message || '移除失败')
    }
  }).catch(() => {})
}

const getTypeName = (type) => {
  const map = {
    SINGLE_CHOICE: '单选题', MULTI_CHOICE: '多选题', TRUE_FALSE: '判断题',
    FILL_BLANK: '填空题', SHORT_ANSWER: '简答题', READING: '阅读理解',
    LISTENING: '听力题', TRANSLATION: '翻译题', WRITING: '写作题', CLOZE: '完形填空'
  }
  return map[type] || type || '—'
}

const getDifficultyType = (d) => ({ 1: 'success', 2: 'warning', 3: 'danger' }[d] || 'info')
const getDifficultyText = (d) => ({ 1: '简单', 2: '中等', 3: '困难' }[d] || '—')
</script>

<style lang="scss" scoped>
.question-dialog-content {
  display: flex;
  gap: 16px;
  height: calc(100vh - 130px);
}

.selected-panel {
  flex: 0 0 42%;
  display: flex;
  flex-direction: column;
  border-right: 1px solid var(--el-border-color-lighter);
  padding-right: 16px;
}

.bank-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;

  h4 {
    margin: 0;
    font-size: 15px;
    font-weight: 600;
  }

  .total-score {
    font-size: 14px;
    font-weight: 600;
    color: var(--el-color-primary);
  }
}

.bank-filters {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.bank-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  flex-wrap: wrap;
  gap: 8px;
}
</style>
