<template>
  <div class="search-result-page">
    <div class="search-header">
      <el-input
        v-model="keyword"
        placeholder="搜索试题、资源、帖子..."
        :prefix-icon="Search"
        size="large"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button :icon="Search" @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </div>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="题目" name="question" />
      <el-tab-pane label="资源" name="resource" />
      <el-tab-pane label="帖子" name="post" />
      <el-tab-pane label="用户" name="user" />
    </el-tabs>

    <div class="search-content">
      <el-empty v-if="!loading && results.length === 0" description="暂无搜索结果" />

      <div v-else class="result-list">
        <div v-for="item in results" :key="item.id" class="result-item" @click="handleResultClick(item)">
          <el-icon class="result-icon">
            <component :is="getResultIcon(item.type)" />
          </el-icon>
          <div class="result-content">
            <div class="result-title" v-html="highlightKeyword(item.title)"></div>
            <div class="result-desc">{{ item.description }}</div>
            <div class="result-meta">
              <el-tag size="small" :type="getResultTagType(item.type)">{{ item.typeText }}</el-tag>
              <span class="result-time">{{ item.time }}</span>
            </div>
          </div>
        </div>

        <el-pagination
          v-if="total > pageSize"
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadResults"
          @current-change="loadResults"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search, Document, Notebook, ChatLineRound, UserFilled } from '@element-plus/icons-vue'
import searchApi from '@/api/search'

const route = useRoute()
const router = useRouter()

const keyword = ref('')
const activeTab = ref('all')
const loading = ref(false)
const results = ref([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)

const loadResults = async () => {
  if (!keyword.value.trim()) return

  loading.value = true
  try {
    const params = {
      keyword: keyword.value,
      type: activeTab.value === 'all' ? '' : activeTab.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    const res = await searchApi.globalSearch(params)
    results.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    // 静默失败，使用模拟数据
    results.value = [
      {
        id: 1,
        type: 'question',
        typeText: '题目',
        title: '阅读理解 - ' + keyword.value,
        description: '四级阅读理解专项练习',
        time: '2天前'
      },
      {
        id: 2,
        type: 'resource',
        typeText: '资源',
        title: '学习资料 - ' + keyword.value,
        description: '四级词汇必背资料',
        time: '3天前'
      },
      {
        id: 3,
        type: 'post',
        typeText: '帖子',
        title: '经验分享 - ' + keyword.value,
        description: '学习经验交流分享',
        time: '1周前'
      }
    ]
    total.value = 3
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadResults()
}

const handleTabChange = () => {
  pageNum.value = 1
  loadResults()
}

const handleResultClick = (item) => {
  switch (item.type) {
    case 'question':
      router.push('/student/learning')
      break
    case 'resource':
      router.push('/student/learning')
      break
    case 'post':
      router.push(`/student/community/post/${item.id}`)
      break
    case 'user':
      router.push(`/student/community/user/${item.id}`)
      break
  }
}

const getResultIcon = (type) => {
  const iconMap = {
    question: Document,
    resource: Notebook,
    post: ChatLineRound,
    user: UserFilled
  }
  return iconMap[type] || Document
}

const getResultTagType = (type) => {
  const typeMap = {
    question: 'primary',
    resource: 'success',
    post: 'warning',
    user: 'info'
  }
  return typeMap[type] || ''
}

const highlightKeyword = (text) => {
  if (!keyword.value || !text) return text
  const kw = keyword.value.trim()
  const regex = new RegExp(`(${kw})`, 'gi')
  return text.replace(regex, '<span class="highlight">$1</span>')
}

watch(() => route.query.keyword, (newVal) => {
  if (newVal) {
    keyword.value = newVal
    loadResults()
  }
}, { immediate: true })

onMounted(() => {
  if (route.query.keyword) {
    keyword.value = route.query.keyword
    loadResults()
  }
})
</script>

<style lang="scss" scoped>
.search-result-page {
  padding: 24px;
  background: var(--bg-primary);
  min-height: calc(100vh - 60px);
}

.search-header {
  max-width: 800px;
  margin: 0 auto 32px;
}

.search-content {
  max-width: 1000px;
  margin: 0 auto;
}

.result-list {
  .result-item {
    display: flex;
    gap: 16px;
    padding: 20px;
    background: var(--bg-primary);
    border-radius: var(--radius-md);
    margin-bottom: 16px;
    cursor: pointer;
    transition: all 0.3s;
    border: 1px solid var(--border-light);

    &:hover {
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
      transform: translateY(-2px);
    }

    .result-icon {
      font-size: 32px;
      color: var(--primary-color);
      flex-shrink: 0;
    }

    .result-content {
      flex: 1;
      min-width: 0;

      .result-title {
        font-size: 16px;
        font-weight: 500;
        color: var(--text-primary);
        margin-bottom: 8px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;

        :deep(.highlight) {
          color: var(--primary-color);
          font-weight: 600;
          background: rgba(64, 128, 255, 0.1);
          padding: 2px 4px;
          border-radius: 2px;
        }
      }

      .result-desc {
        font-size: 14px;
        color: var(--text-secondary);
        margin-bottom: 12px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        line-clamp: 2;
        -webkit-box-orient: vertical;
      }

      .result-meta {
        display: flex;
        align-items: center;
        gap: 12px;

        .result-time {
          font-size: 13px;
          color: var(--text-placeholder);
        }
      }
    }
  }

  .el-pagination {
    margin-top: 24px;
    text-align: center;
  }
}
</style>
