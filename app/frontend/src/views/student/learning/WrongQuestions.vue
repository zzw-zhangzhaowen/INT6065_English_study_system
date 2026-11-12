<template>
  <div class="wrong-questions">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-info">
        <h2>错题本</h2>
        <p>记录您的错题，针对性复习提升</p>
      </div>
      <div class="header-actions">
        <el-button @click="exportWrong">
          <el-icon><Download /></el-icon>
          导出错题
        </el-button>
        <el-button type="primary" @click="startPractice">
          <el-icon><VideoPlay /></el-icon>
          开始练习
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon total">
          <el-icon :size="24"><Document /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总错题数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon need-review">
          <el-icon :size="24"><Warning /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.needReview }}</div>
          <div class="stat-label">待复习</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon reviewing">
          <el-icon :size="24"><Loading /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.reviewing }}</div>
          <div class="stat-label">复习中</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon mastered">
          <el-icon :size="24"><CircleCheck /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.mastered }}</div>
          <div class="stat-label">已掌握</div>
        </div>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <div class="filter-left">
        <el-select v-model="filterType" placeholder="题目类型" clearable style="width: 120px">
          <el-option label="全部类型" value="" />
          <el-option label="听力" value="listening" />
          <el-option label="阅读" value="reading" />
          <el-option label="写作" value="writing" />
          <el-option label="翻译" value="translation" />
        </el-select>
        <el-select v-model="filterStatus" placeholder="掌握程度" clearable style="width: 120px">
          <el-option label="全部状态" value="" />
          <el-option label="未掌握" value="not_mastered" />
          <el-option label="基本掌握" value="basic" />
          <el-option label="已掌握" value="mastered" />
        </el-select>
        <el-select v-model="filterTime" placeholder="时间范围" clearable style="width: 120px">
          <el-option label="全部时间" value="" />
          <el-option label="最近7天" value="7" />
          <el-option label="最近30天" value="30" />
          <el-option label="最近90天" value="90" />
        </el-select>
      </div>
      <div class="filter-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索题目内容"
          :prefix-icon="Search"
          clearable
          style="width: 240px"
        />
      </div>
    </div>

    <!-- 错题列表 -->
    <div class="wrong-list">
      <div v-for="item in wrongList" :key="item.id" class="wrong-item">
        <div class="item-header">
          <div class="item-tags">
            <el-tag size="small">{{ getQuestionTypeName(item.type) }}</el-tag>
            <el-tag :type="getStatusType(item.status)" size="small">
              {{ getStatusText(item.status) }}
            </el-tag>
          </div>
          <div class="item-meta">
            <span class="error-count">错误 {{ item.errorCount }} 次</span>
            <span class="last-error">最近错误：{{ item.lastErrorTime }}</span>
          </div>
        </div>
        <div class="item-content">
          <div class="question-stem">{{ item.question }}</div>
          <div v-if="item.options && item.options.length" class="question-options">
            <div
              v-for="option in item.options"
              :key="option.key"
              class="option-item"
              :class="{
                correct: item.correctAnswer.includes(option.key)
              }"
            >
              <span class="option-label">{{ option.key }}.</span>
              <span class="option-text">{{ option.content }}</span>
              <el-icon v-if="item.correctAnswer.includes(option.key)" class="correct-icon">
                <CircleCheck />
              </el-icon>
            </div>
          </div>
        </div>
        <div class="item-footer">
          <div class="footer-left">
            <span class="source">来源：{{ item.source }}</span>
          </div>
          <div class="footer-right">
            <el-button type="primary" link @click="viewAnalysis(item)">
              查看解析
            </el-button>
            <el-button type="primary" link @click="practiceSimilar(item)">
              相似练习
            </el-button>
            <el-button
              :type="item.status === 'mastered' ? 'success' : 'default'"
              link
              @click="toggleMastered(item)"
            >
              {{ item.status === 'mastered' ? '已掌握' : '标记掌握' }}
            </el-button>
          </div>
        </div>

        <!-- 解析展开区域 -->
        <el-collapse-transition>
          <div v-if="item.showAnalysis" class="analysis-section">
            <div class="analysis-header">
              <h4>题目解析</h4>
            </div>
            <div class="analysis-content">
              <div class="correct-answer">
                <span class="label">正确答案：</span>
                <span class="value">{{ item.correctAnswer }}</span>
              </div>
              <div class="explanation">
                <span class="label">解析：</span>
                <p>{{ item.explanation }}</p>
              </div>
              <div v-if="item.knowledgePoints" class="knowledge-points">
                <span class="label">相关知识点：</span>
                <div class="points-list">
                  <el-tag
                    v-for="point in item.knowledgePoints"
                    :key="point"
                    size="small"
                    type="info"
                  >
                    {{ point }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
        </el-collapse-transition>
      </div>
    </div>

    <!-- 空状态 -->
    <el-empty v-if="wrongList.length === 0" description="暂无错题记录" />

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        background
      />
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Download, VideoPlay, Document, Warning, Loading, CircleCheck, Search
} from '@element-plus/icons-vue'
import { learningApi } from '@/api/learning'

const router = useRouter()

// 将选项统一为 [{key: 'A', content: '...'}, ...] 格式
const normalizeOptions = (raw) => {
  if (!raw) return []
  const arr = typeof raw === 'string' ? JSON.parse(raw) : raw
  if (!Array.isArray(arr) || arr.length === 0) return []
  if (typeof arr[0] === 'object' && arr[0].key) return arr
  return arr.map((text, i) => ({ key: String.fromCharCode(65 + i), content: String(text) }))
}

// 将答案统一为字母字符串，如 "A" 或 "A,B"
const normalizeAnswer = (answer) => {
  if (answer == null) return ''
  const s = String(answer).trim()
  if (/^[A-Za-z]/.test(s)) return s.toUpperCase()
  // 数字索引转字母，支持逗号分隔的多选
  return s.split(',').map(v => {
    const n = parseInt(v.trim(), 10)
    return isNaN(n) ? v.trim() : String.fromCharCode(65 + n)
  }).join(',')
}

// 统计数据
const stats = ref({
  total: 0,
  needReview: 0,
  reviewing: 0,
  mastered: 0
})

// 筛选条件
const filterType = ref('')
const filterStatus = ref('')
const filterTime = ref('')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 错题列表
const wrongList = ref([])

const getStatusType = (status) => {
  const types = {
    not_mastered: 'danger',
    basic: 'warning',
    mastered: 'success'
  }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = {
    not_mastered: '未掌握',
    basic: '基本掌握',
    mastered: '已掌握'
  }
  return texts[status] || ''
}

const getQuestionTypeName = (type) => {
  const types = {
    single: '单选题',
    multiple: '多选题',
    fill: '填空题',
    short: '简答题',
    listening: '听力',
    reading: '阅读',
    writing: '写作',
    translation: '翻译'
  }
  return types[type] || type
}

const exportWrong = () => {
  // 导出错题为文本格式
  let content = '错题本导出\n\n'
  wrongList.value.forEach((item, index) => {
    content += `${index + 1}. ${item.question}\n`
    if (item.options && item.options.length) {
      item.options.forEach(opt => {
        content += `  ${opt.key}. ${opt.content}\n`
      })
    }
    content += `正确答案: ${item.correctAnswer}\n`
    content += `解析: ${item.explanation || '无'}\n\n`
  })
  const blob = new Blob([content], { type: 'text/plain;charset=utf-8' })
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = '错题本.txt'
  a.click()
  window.URL.revokeObjectURL(url)
  ElMessage.success('错题导出成功')
}

const startPractice = () => {
  router.push('/student/learning/practice/wrong')
}

const viewAnalysis = (item) => {
  item.showAnalysis = !item.showAnalysis
}

const practiceSimilar = (item) => {
  router.push(`/student/learning/practice/similar/${item.id}`)
}

const toggleMastered = async (item) => {
  try {
    const newLevel = item.status === 'mastered' ? 0 : 2
    await learningApi.markAsMastered(item.id, newLevel)
    if (item.status === 'mastered') {
      item.status = 'not_mastered'
      stats.value.mastered--
      stats.value.needReview++
      ElMessage.info('已取消掌握标记')
    } else {
      item.status = 'mastered'
      stats.value.mastered++
      stats.value.needReview--
      ElMessage.success('已标记为掌握')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const loadWrongQuestions = async () => {
  try {
    const res = await learningApi.getWrongQuestions({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      questionType: filterType.value || null,
      masteryLevel: filterStatus.value === 'not_mastered' ? 0 : filterStatus.value === 'basic' ? 1 : filterStatus.value === 'mastered' ? 2 : null,
      days: filterTime.value || null,
      keyword: searchKeyword.value || null
    })
    if (res.data) {
      wrongList.value = (res.data.records || []).map(item => ({
        id: item.id,
        questionId: item.questionId,
        type: item.questionType || 'single',
        question: item.title || item.content,
        options: normalizeOptions(item.options),
        correctAnswer: normalizeAnswer(item.answer),
        errorCount: item.wrongCount,
        lastErrorTime: item.lastWrongTime,
        source: item.source || '练习',
        explanation: item.analysis,
        knowledgePoints: [],
        status: item.masteryLevel === 0 ? 'not_mastered' : item.masteryLevel === 1 ? 'basic' : 'mastered',
        showAnalysis: false
      }))
      total.value = res.data.total || 0
    }
    // 加载统计
    const statsRes = await learningApi.getWrongQuestionStatistics()
    if (statsRes.data) {
      stats.value = {
        total: statsRes.data.total || 0,
        needReview: statsRes.data.notMastered || 0,
        reviewing: statsRes.data.basicMastered || 0,
        mastered: statsRes.data.mastered || 0
      }
    }
  } catch (error) {
    console.error('加载错题失败:', error)
  }
}

// 监听筛选条件变化（重置页码后由分页watcher触发加载，避免双重调用）
watch([filterType, filterStatus, filterTime], () => {
  currentPage.value = 1
  // 如果页码已经是1，watcher不会触发，需要手动加载
  loadWrongQuestions()
}, { deep: true })

// 监听分页变化（仅在页码或每页数量真正变化时触发）
watch([currentPage, pageSize], (newVal, oldVal) => {
  // 避免筛选重置页码时的双重调用
  if (newVal[0] === oldVal[0] && newVal[1] === oldVal[1]) return
  loadWrongQuestions()
})

// 搜索防抖
let searchTimer = null
watch(searchKeyword, () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    currentPage.value = 1
    loadWrongQuestions()
  }, 500)
})

onMounted(() => {
  loadWrongQuestions()
})

onUnmounted(() => {
  if (searchTimer) clearTimeout(searchTimer)
})
</script>

<style lang="scss" scoped>
.wrong-questions {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .header-info {
      h2 {
        font-size: 24px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 8px;
      }

      p {
        color: var(--text-secondary);
      }
    }

    .header-actions {
      display: flex;
      gap: 12px;
    }
  }

  .stats-cards {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 24px;

    @media (max-width: 992px) {
      grid-template-columns: repeat(2, 1fr);
    }

    .stat-card {
      background: var(--bg-primary);
      border-radius: var(--radius-lg);
      padding: 20px;
      display: flex;
      align-items: center;
      gap: 16px;
      box-shadow: var(--shadow-sm);

      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: var(--radius-md);
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;

        &.total {
          background: #818cf8;
        }

        &.need-review {
          background: #fca5a5;
        }

        &.reviewing {
          background: #fbbf24;
        }

        &.mastered {
          background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
        }
      }

      .stat-info {
        .stat-value {
          font-size: 24px;
          font-weight: 700;
          color: var(--text-primary);
        }

        .stat-label {
          font-size: 13px;
          color: var(--text-secondary);
        }
      }
    }
  }

  .filter-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding: 16px;
    background: var(--bg-primary);
    border-radius: var(--radius-lg);

    .filter-left {
      display: flex;
      gap: 12px;
    }
  }

  .wrong-list {
    .wrong-item {
      background: var(--bg-primary);
      border-radius: var(--radius-lg);
      padding: 24px;
      margin-bottom: 16px;
      box-shadow: var(--shadow-sm);

      .item-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        .item-tags {
          display: flex;
          gap: 8px;
        }

        .item-meta {
          display: flex;
          gap: 16px;
          font-size: 13px;
          color: var(--text-secondary);

          .error-count {
            color: var(--danger-color);
          }
        }
      }

      .item-content {
        .question-stem {
          font-size: 15px;
          color: var(--text-primary);
          line-height: 1.6;
          margin-bottom: 16px;
        }

        .question-options {
          .option-item {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 12px 16px;
            border-radius: var(--radius-md);
            margin-bottom: 8px;
            background: var(--bg-secondary);

            &.correct {
              background: rgba(82, 196, 26, 0.1);
              border: 1px solid var(--success-color);

              .option-label, .option-text {
                color: var(--success-color);
              }
            }

            &.wrong {
              background: rgba(255, 77, 79, 0.1);
              border: 1px solid var(--danger-color);

              .option-label, .option-text {
                color: var(--danger-color);
              }
            }

            .option-label {
              font-weight: 500;
              color: var(--text-secondary);
            }

            .option-text {
              flex: 1;
              color: var(--text-primary);
            }

            .correct-icon {
              color: var(--success-color);
            }

            .wrong-icon {
              color: var(--danger-color);
            }
          }
        }
      }

      .item-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 16px;
        padding-top: 16px;
        border-top: 1px solid var(--border-light);

        .source {
          font-size: 13px;
          color: var(--text-placeholder);
        }
      }

      .analysis-section {
        margin-top: 16px;
        padding: 20px;
        background: var(--bg-secondary);
        border-radius: var(--radius-md);

        .analysis-header {
          margin-bottom: 16px;

          h4 {
            font-size: 15px;
            font-weight: 600;
            color: var(--text-primary);
          }
        }

        .analysis-content {
          .correct-answer,
          .explanation,
          .knowledge-points {
            margin-bottom: 12px;

            .label {
              font-weight: 500;
              color: var(--text-secondary);
            }

            .value {
              color: var(--success-color);
              font-weight: 600;
            }

            p {
              margin-top: 8px;
              color: var(--text-regular);
              line-height: 1.6;
            }
          }

          .points-list {
            display: flex;
            gap: 8px;
            margin-top: 8px;
          }
        }
      }
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: center;
    padding: 24px 0;
  }
}
</style>
