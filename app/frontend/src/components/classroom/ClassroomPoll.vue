<template>
  <div class="classroom-poll">
    <!-- 教师：创建投票区域 -->
    <div v-if="isTeacher" class="poll-create-area">
      <el-button type="primary" size="small" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon> 发起投票
      </el-button>
    </div>

    <!-- 投票列表 -->
    <div class="poll-list">
      <div v-if="polls.length === 0" class="poll-empty">
        <el-icon :size="36" color="#ddd"><DataAnalysis /></el-icon>
        <p>暂无投票</p>
      </div>

      <div v-for="poll in polls" :key="poll.id" class="poll-item">
        <div class="poll-header">
          <div class="poll-title">{{ poll.title }}</div>
          <div class="poll-meta">
            <el-tag :type="poll.status === 'ACTIVE' ? 'success' : 'info'" size="small">
              {{ poll.status === 'ACTIVE' ? '进行中' : '已结束' }}
            </el-tag>
            <el-tag size="small" type="warning">{{ poll.pollType === 'SINGLE' ? '单选' : '多选' }}</el-tag>
            <span class="poll-votes">{{ poll.totalVotes || 0 }} 票</span>
          </div>
        </div>

        <!-- 选项列表 -->
        <div class="poll-options">
          <div v-for="opt in poll.options" :key="opt.id" class="poll-option"
               :class="{ 'is-selected': selectedOptions[poll.id]?.includes(opt.id), 'is-voted': votedPolls[poll.id] }"
               @click="selectOption(poll, opt)">
            <div class="option-content">
              <el-radio v-if="poll.pollType === 'SINGLE' && poll.status === 'ACTIVE' && !votedPolls[poll.id]"
                        :model-value="selectedOptions[poll.id]?.[0]" :value="opt.id" @click.stop>
                {{ opt.content }}
              </el-radio>
              <el-checkbox v-else-if="poll.pollType === 'MULTIPLE' && poll.status === 'ACTIVE' && !votedPolls[poll.id]"
                           :model-value="selectedOptions[poll.id]?.includes(opt.id)"
                           @click.stop>
                {{ opt.content }}
              </el-checkbox>
              <span v-else>{{ opt.content }}</span>
            </div>
            <!-- 投票结果进度条 -->
            <div class="option-result" v-if="votedPolls[poll.id] || poll.status === 'CLOSED' || isTeacher">
              <el-progress
                :percentage="getPercentage(opt.voteCount, poll.totalVotes)"
                :stroke-width="16"
                :text-inside="true"
                :format="() => `${opt.voteCount}票 (${getPercentage(opt.voteCount, poll.totalVotes)}%)`"
              />
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="poll-actions">
          <el-button v-if="poll.status === 'ACTIVE' && !votedPolls[poll.id] && !isTeacher"
                     type="primary" size="small"
                     :disabled="!selectedOptions[poll.id]?.length"
                     @click="submitVote(poll)">
            提交投票
          </el-button>
          <span v-if="votedPolls[poll.id] && !isTeacher" class="voted-tip">
            <el-icon><Check /></el-icon> 已投票
          </span>
          <el-button v-if="isTeacher && poll.status === 'ACTIVE'"
                     type="danger" size="small" plain @click="closePoll(poll)">
            结束投票
          </el-button>
        </div>
      </div>
    </div>

    <!-- 创建投票对话框 -->
    <el-dialog v-model="showCreateDialog" title="发起投票" width="500px" :close-on-click-modal="false">
      <el-form :model="createForm" label-width="80px">
        <el-form-item label="投票标题">
          <el-input v-model="createForm.title" placeholder="请输入投票标题" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="投票类型">
          <el-radio-group v-model="createForm.pollType">
            <el-radio value="SINGLE">单选</el-radio>
            <el-radio value="MULTIPLE">多选</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="投票选项">
          <div class="option-inputs">
            <div v-for="(opt, index) in createForm.options" :key="index" class="option-input-row">
              <el-input v-model="createForm.options[index]" :placeholder="`选项 ${index + 1}`" />
              <el-button v-if="createForm.options.length > 2" text type="danger" @click="removeOption(index)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
            <el-button v-if="createForm.options.length < 8" text type="primary" @click="addOption">
              <el-icon><Plus /></el-icon> 添加选项
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="匿名投票">
          <el-switch v-model="createForm.anonymous" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="createPoll" :disabled="!canCreate">发起投票</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { DataAnalysis, Plus, Delete, Check } from '@element-plus/icons-vue'
import { classroomApi } from '@/api/classroom'

const props = defineProps({
  classroomId: { type: [String, Number], required: true },
  stompClient: { type: Object, default: null },
  wsConnected: { type: Boolean, default: false },
  isTeacher: { type: Boolean, default: false }
})

const polls = ref([])
const showCreateDialog = ref(false)
const selectedOptions = reactive({})
const votedPolls = reactive({})

const createForm = reactive({
  title: '',
  pollType: 'SINGLE',
  options: ['', ''],
  anonymous: false
})

const canCreate = computed(() => {
  return createForm.title.trim() &&
    createForm.options.filter(o => o.trim()).length >= 2
})

const getPercentage = (count, total) => {
  if (!total || total === 0) return 0
  return Math.round((count / total) * 100)
}

const loadPolls = async () => {
  try {
    const res = await classroomApi.getPolls(props.classroomId)
    if (res.data) {
      polls.value = res.data
    }
  } catch (e) {
    console.error('加载投票列表失败:', e)
  }
}

const selectOption = (poll, opt) => {
  if (poll.status !== 'ACTIVE' || votedPolls[poll.id]) return

  if (poll.pollType === 'SINGLE') {
    selectedOptions[poll.id] = [opt.id]
  } else {
    if (!selectedOptions[poll.id]) selectedOptions[poll.id] = []
    const idx = selectedOptions[poll.id].indexOf(opt.id)
    if (idx >= 0) {
      selectedOptions[poll.id].splice(idx, 1)
    } else {
      selectedOptions[poll.id].push(opt.id)
    }
  }
}

const submitVote = (poll) => {
  const optionIds = selectedOptions[poll.id]
  if (!optionIds?.length) {
    ElMessage.warning('请选择一个选项')
    return
  }

  if (props.stompClient && props.wsConnected) {
    props.stompClient.publish({
      destination: `/app/classroom/${props.classroomId}/poll`,
      body: JSON.stringify({
        action: 'VOTE',
        pollId: poll.id,
        optionIds
      })
    })
    votedPolls[poll.id] = true
  }
}

const closePoll = (poll) => {
  if (props.stompClient && props.wsConnected) {
    props.stompClient.publish({
      destination: `/app/classroom/${props.classroomId}/poll`,
      body: JSON.stringify({
        action: 'CLOSE_POLL',
        pollId: poll.id
      })
    })
  }
}

const createPoll = () => {
  const validOptions = createForm.options.filter(o => o.trim())
  if (validOptions.length < 2) {
    ElMessage.warning('至少需要2个有效选项')
    return
  }

  if (props.stompClient && props.wsConnected) {
    props.stompClient.publish({
      destination: `/app/classroom/${props.classroomId}/poll`,
      body: JSON.stringify({
        action: 'CREATE_POLL',
        title: createForm.title.trim(),
        pollType: createForm.pollType,
        anonymous: createForm.anonymous,
        options: validOptions
      })
    })
    showCreateDialog.value = false
    resetCreateForm()
    ElMessage.success('投票已发起')
  }
}

const resetCreateForm = () => {
  createForm.title = ''
  createForm.pollType = 'SINGLE'
  createForm.options = ['', '']
  createForm.anonymous = false
}

const addOption = () => {
  if (createForm.options.length < 8) {
    createForm.options.push('')
  }
}

const removeOption = (index) => {
  if (createForm.options.length > 2) {
    createForm.options.splice(index, 1)
  }
}

// 处理WebSocket推送的投票消息
const handlePollMessage = (data) => {
  switch (data.action) {
    case 'CREATE_POLL':
      if (data.data) {
        const exists = polls.value.find(p => p.id === data.data.id)
        if (!exists) {
          polls.value.unshift(data.data)
        }
      }
      break
    case 'VOTE':
      if (data.data) {
        const idx = polls.value.findIndex(p => p.id === data.data.id)
        if (idx >= 0) {
          polls.value[idx] = data.data
        }
      }
      break
    case 'CLOSE_POLL':
      if (data.data) {
        const idx = polls.value.findIndex(p => p.id === data.data.id)
        if (idx >= 0) {
          polls.value[idx] = data.data
        }
      }
      break
  }
}

defineExpose({ handlePollMessage, loadPolls })

onMounted(() => {
  loadPolls()
})
</script>

<style lang="scss" scoped>
.classroom-poll {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.poll-create-area {
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.poll-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px 12px;
}

.poll-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #ccc;

  p {
    margin-top: 8px;
    font-size: 13px;
  }
}

.poll-item {
  padding: 14px;
  margin-bottom: 10px;
  background: #fafafa;
  border-radius: 8px;

  .poll-header {
    margin-bottom: 12px;

    .poll-title {
      font-size: 15px;
      font-weight: 600;
      color: #333;
      margin-bottom: 6px;
    }

    .poll-meta {
      display: flex;
      align-items: center;
      gap: 8px;

      .poll-votes {
        font-size: 12px;
        color: #999;
        margin-left: auto;
      }
    }
  }
}

.poll-options {
  .poll-option {
    padding: 10px 12px;
    margin-bottom: 6px;
    background: #fff;
    border: 1px solid #eee;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s;

    &:hover:not(.is-voted) {
      border-color: #409eff;
      background: #f0f7ff;
    }

    &.is-selected {
      border-color: #409eff;
      background: #ecf5ff;
    }

    &.is-voted {
      cursor: default;
    }

    .option-content {
      margin-bottom: 4px;
      font-size: 14px;
    }

    .option-result {
      margin-top: 6px;
    }
  }
}

.poll-actions {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 8px;

  .voted-tip {
    font-size: 13px;
    color: #67c23a;
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

.option-inputs {
  width: 100%;

  .option-input-row {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
  }
}
</style>
