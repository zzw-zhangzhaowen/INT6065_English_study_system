<template>
  <div class="note-edit">
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
      <h2>{{ isEdit ? '编辑笔记' : '新建笔记' }}</h2>
    </div>

    <div class="note-form" v-loading="loading">
      <el-form :model="noteForm" label-width="80px">
        <el-form-item label="标题" required>
          <el-input v-model="noteForm.title" placeholder="请输入笔记标题" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="标签">
          <el-select
            v-model="noteForm.tags"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="选择或输入标签"
            style="width: 100%"
          >
            <el-option label="听力" value="听力" />
            <el-option label="阅读" value="阅读" />
            <el-option label="写作" value="写作" />
            <el-option label="词汇" value="词汇" />
            <el-option label="语法" value="语法" />
            <el-option label="CET4" value="CET4" />
            <el-option label="CET6" value="CET6" />
          </el-select>
        </el-form-item>

        <el-form-item label="关联资源">
          <el-select
            v-model="noteForm.resourceId"
            filterable
            clearable
            placeholder="选择关联的学习资源"
            style="width: 100%"
          >
            <el-option
              v-for="res in resources"
              :key="res.id"
              :label="res.resourceName"
              :value="res.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="内容" required>
          <el-input
            v-model="noteForm.content"
            type="textarea"
            :rows="15"
            placeholder="请输入笔记内容..."
            maxlength="10000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="saveNote" :loading="saving">
            {{ isEdit ? '保存修改' : '创建笔记' }}
          </el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { learningApi } from '@/api/learning'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const resources = ref([])

const noteForm = reactive({
  title: '',
  content: '',
  tags: [],
  resourceId: null
})

const isEdit = computed(() => route.params.id && route.params.id !== 'new')

const goBack = () => {
  router.back()
}

const loadNote = async () => {
  if (!isEdit.value) return

  loading.value = true
  try {
    const res = await learningApi.getNoteDetail(route.params.id)
    if (res.data) {
      noteForm.title = res.data.title || ''
      noteForm.content = res.data.content || ''
      noteForm.tags = res.data.tags ? res.data.tags.split(',') : []
      noteForm.resourceId = res.data.resourceId || null
    }
  } catch (error) {
    console.error('加载笔记失败:', error)
    ElMessage.error('加载笔记失败')
  } finally {
    loading.value = false
  }
}

const loadResources = async () => {
  try {
    const res = await learningApi.getResourceList({ pageNum: 1, pageSize: 100 })
    if (res.data?.records) {
      resources.value = res.data.records
    }
  } catch (error) {
    console.error('加载资源列表失败:', error)
  }
}

const saveNote = async () => {
  if (!noteForm.title.trim()) {
    ElMessage.warning('请输入笔记标题')
    return
  }
  if (!noteForm.content.trim()) {
    ElMessage.warning('请输入笔记内容')
    return
  }

  saving.value = true
  try {
    const data = {
      title: noteForm.title,
      content: noteForm.content,
      tags: noteForm.tags.join(','),
      resourceId: noteForm.resourceId
    }

    if (isEdit.value) {
      await learningApi.updateNote(route.params.id, data)
      ElMessage.success('笔记更新成功')
    } else {
      await learningApi.createNote(data)
      ElMessage.success('笔记创建成功')
    }
    router.back()
  } catch (error) {
    console.error('保存笔记失败:', error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadResources()
  loadNote()
})
</script>

<style lang="scss" scoped>
.note-edit {
  .page-header {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 24px;

    h2 {
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 0;
    }
  }

  .note-form {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;
    max-width: 800px;
  }
}
</style>
