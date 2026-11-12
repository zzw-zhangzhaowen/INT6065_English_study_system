<template>
  <div class="message-center">
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- 收件箱 -->
      <el-tab-pane label="收件箱" name="inbox">
        <div class="message-header">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索消息..."
            :prefix-icon="Search"
            clearable
            @input="handleSearch"
            style="width: 300px; margin-right: 10px;"
          />
          <el-button type="primary" @click="markAllRead" :disabled="unreadCount === 0">
            全部标为已读
          </el-button>
        </div>

        <el-table
          v-loading="loading"
          :data="messages"
          style="width: 100%"
          @row-click="handleRowClick"
          :row-class-name="getRowClassName"
        >
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag v-if="row.isRead === 0" type="danger" size="small">未读</el-tag>
              <el-tag v-else type="info" size="small">已读</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
          <el-table-column label="类型" width="100">
            <template #default="{ row }">
              <el-tag :type="getMessageTypeTag(row.type)">{{ getMessageTypeText(row.type) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="接收时间" width="180" />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button v-if="row.isRead === 0" type="primary" link @click.stop="markRead(row.id)">
                标为已读
              </el-button>
              <el-button type="danger" link @click.stop="deleteMsg(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadMessages"
          @current-change="loadMessages"
          style="margin-top: 20px; justify-content: flex-end;"
        />
      </el-tab-pane>

      <!-- 发件箱 -->
      <el-tab-pane label="发件箱" name="outbox">
        <el-table
          v-loading="loading"
          :data="messages"
          style="width: 100%"
          @row-click="handleRowClick"
        >
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
          <el-table-column label="类型" width="100">
            <template #default="{ row }">
              <el-tag :type="getMessageTypeTag(row.type)">{{ getMessageTypeText(row.type) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="发送时间" width="180" />
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button type="danger" link @click.stop="deleteMsg(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadMessages"
          @current-change="loadMessages"
          style="margin-top: 20px; justify-content: flex-end;"
        />
      </el-tab-pane>
    </el-tabs>

    <!-- 消息详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="currentMessage.title"
      width="600px"
      @closed="handleDialogClose"
    >
      <div class="message-detail">
        <div class="detail-item">
          <span class="label">类型：</span>
          <el-tag :type="getMessageTypeTag(currentMessage.type)">
            {{ getMessageTypeText(currentMessage.type) }}
          </el-tag>
        </div>
        <div class="detail-item">
          <span class="label">时间：</span>
          <span>{{ currentMessage.createTime }}</span>
        </div>
        <div class="detail-item">
          <span class="label">内容：</span>
        </div>
        <div class="message-content">
          {{ currentMessage.content }}
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { messageApi } from '@/api/message'

const activeTab = ref('inbox')
const loading = ref(false)
const messages = ref([])
const searchKeyword = ref('')
const dialogVisible = ref(false)
const currentMessage = ref({})

const pagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const unreadCount = ref(0)

// 加载未读消息数量
const loadUnreadCount = async () => {
  try {
    const res = await messageApi.getUnreadCount()
    unreadCount.value = res.data || 0
  } catch (error) {
    console.error('加载未读数量失败:', error)
    unreadCount.value = 0
  }
}

// 加载消息列表
const loadMessages = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.value.pageNum,
      pageSize: pagination.value.pageSize
    }
    if (searchKeyword.value.trim()) {
      params.keyword = searchKeyword.value.trim()
    }

    let res
    if (activeTab.value === 'inbox') {
      res = await messageApi.getInbox(params)
    } else {
      res = await messageApi.getOutbox(params)
    }
    
    if (res.data?.records) {
      messages.value = res.data.records
      pagination.value.total = res.data.total || 0
    } else if (Array.isArray(res.data)) {
      messages.value = res.data
      pagination.value.total = res.data.length
    }
    
    // 刷新未读数量
    await loadUnreadCount()
  } catch (error) {
    console.error('加载消息失败:', error)
    ElMessage.error('加载消息失败')
    messages.value = []
  } finally {
    loading.value = false
  }
}

// 切换标签页
const handleTabChange = (tab) => {
  pagination.value.pageNum = 1
  loadMessages()
}

// 搜索消息（防抖）
let searchTimer = null
const handleSearch = () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    pagination.value.pageNum = 1
    loadMessages()
  }, 500)
}

// 行点击事件
const handleRowClick = async (row) => {
  currentMessage.value = row
  dialogVisible.value = true
  
  // 如果是未读消息，标记为已读
  if (row.isRead === 0 && activeTab.value === 'inbox') {
    await markRead(row.id)
  }
}

// 标记单条消息为已读
const markRead = async (id) => {
  try {
    await messageApi.markAsRead(id)
    ElMessage.success('已标记为已读')
    await loadMessages()
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('操作失败')
  }
}

// 标记全部已读
const markAllRead = async () => {
  try {
    await ElMessageBox.confirm('确定要将所有消息标记为已读吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await messageApi.markAllAsRead()
    ElMessage.success('已全部标记为已读')
    await loadMessages()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量标记已读失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 删除消息
const deleteMsg = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条消息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await messageApi.deleteMessage(id)
    ElMessage.success('删除成功')
    loadMessages()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除消息失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 对话框关闭事件
const handleDialogClose = () => {
  currentMessage.value = {}
}

// 获取行样式
const getRowClassName = ({ row }) => {
  return row.isRead === 0 ? 'unread-row' : ''
}

// 获取消息类型标签颜色
const getMessageTypeTag = (type) => {
  const typeMap = {
    'NOTIFICATION': '',
    'REMINDER': 'warning',
    'BROADCAST': 'success',
    'PRIVATE': 'info'
  }
  return typeMap[type] || ''
}

// 获取消息类型文本
const getMessageTypeText = (type) => {
  const typeMap = {
    'NOTIFICATION': '通知',
    'REMINDER': '提醒',
    'BROADCAST': '广播',
    'PRIVATE': '私信'
  }
  return typeMap[type] || '通知'
}

onMounted(() => {
  loadMessages()
})

onUnmounted(() => {
  clearTimeout(searchTimer)
})

// 暴露刷新方法供外部调用
defineExpose({
  refresh: loadMessages
})
</script>

<style lang="scss" scoped>
.message-center {
  padding: 20px;
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
}

.message-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

:deep(.unread-row) {
  background-color: var(--el-color-primary-light-9);
  font-weight: 600;
}

:deep(.el-table__row) {
  cursor: pointer;
}

.message-detail {
  padding: 10px 0;
}

.detail-item {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.detail-item .label {
  font-weight: 600;
  color: var(--text-secondary);
  min-width: 60px;
  margin-right: 10px;
}

.message-content {
  padding: 15px;
  background: var(--bg-tertiary, var(--el-fill-color-light));
  border-radius: var(--radius-sm, 4px);
  line-height: 1.8;
  color: var(--text-primary);
  white-space: pre-wrap;
  word-break: break-word;
}

:deep(.el-pagination) {
  display: flex;
}
</style>
