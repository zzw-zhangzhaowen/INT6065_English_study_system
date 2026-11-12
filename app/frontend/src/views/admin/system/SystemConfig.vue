<template>
  <div v-loading="loading" class="system-config">
    <div class="page-header">
      <h2>系统配置</h2>
    </div>

    <el-tabs v-model="activeTab" class="config-tabs">
      <el-tab-pane label="基础设置" name="basic">
        <el-form ref="basicFormRef" :model="basicConfig" :rules="basicRules" label-width="140px" class="config-form">
          <el-form-item label="系统名称" prop="systemName">
            <el-input v-model="basicConfig.systemName" style="width: 400px" />
          </el-form-item>
          <el-form-item label="系统Logo">
            <div class="logo-upload">
              <div v-if="basicConfig.systemLogo" class="logo-preview">
                <img :src="getFullUrl(basicConfig.systemLogo)" alt="系统Logo" />
                <div class="logo-actions">
                  <el-button type="danger" size="small" @click="basicConfig.systemLogo = ''">移除</el-button>
                </div>
              </div>
              <el-upload
                action="#"
                :show-file-list="false"
                :http-request="handleLogoUpload"
                accept="image/png,image/jpeg,image/gif,image/svg+xml"
                :before-upload="beforeLogoUpload"
              >
                <el-button :loading="uploadingLogo">
                  {{ basicConfig.systemLogo ? '更换Logo' : '上传Logo' }}
                </el-button>
              </el-upload>
              <div class="upload-tip">支持 PNG、JPG、GIF、SVG 格式，建议尺寸 200x200 以内</div>
            </div>
          </el-form-item>
          <el-form-item label="版权信息" prop="copyright">
            <el-input v-model="basicConfig.copyright" style="width: 400px" />
          </el-form-item>
          <el-form-item label="备案号" prop="icp">
            <el-input v-model="basicConfig.icp" style="width: 400px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="savingBasic" @click="saveBasicConfig">保存设置</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="考试设置" name="exam">
        <el-form ref="examFormRef" :model="examConfig" :rules="examRules" label-width="140px" class="config-form">
          <el-form-item label="默认考试时长" prop="defaultDuration">
            <el-input-number v-model="examConfig.defaultDuration" :min="10" :max="300" />
            <span style="margin-left: 8px">分钟</span>
          </el-form-item>
          <el-form-item label="切屏警告次数" prop="switchWarning">
            <el-input-number v-model="examConfig.switchWarning" :min="1" :max="10" />
          </el-form-item>
          <el-form-item label="自动提交" prop="autoSubmit">
            <el-switch v-model="examConfig.autoSubmit" />
            <span style="margin-left: 8px; color: var(--text-secondary)">考试时间结束后自动提交</span>
          </el-form-item>
          <el-form-item label="成绩保留小数" prop="scoreDecimal">
            <el-input-number v-model="examConfig.scoreDecimal" :min="0" :max="2" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="savingExam" @click="saveExamConfig">保存设置</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="邮件设置" name="email">
        <el-form ref="emailFormRef" :model="emailConfig" :rules="emailRules" label-width="140px" class="config-form">
          <el-form-item label="SMTP服务器" prop="smtpHost">
            <el-input v-model="emailConfig.smtpHost" style="width: 400px" />
          </el-form-item>
          <el-form-item label="SMTP端口" prop="smtpPort">
            <el-input-number v-model="emailConfig.smtpPort" :min="1" :max="65535" />
          </el-form-item>
          <el-form-item label="发件人邮箱" prop="senderEmail">
            <el-input v-model="emailConfig.senderEmail" style="width: 400px" />
          </el-form-item>
          <el-form-item label="发件人密码" prop="senderPassword">
            <el-input v-model="emailConfig.senderPassword" type="password" style="width: 400px" show-password />
          </el-form-item>
          <el-form-item>
            <el-button :loading="testingEmail" @click="testEmail">测试邮件</el-button>
            <el-button type="primary" :loading="savingEmail" @click="saveEmailConfig">保存设置</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="存储设置" name="storage">
        <el-form ref="storageFormRef" :model="storageConfig" :rules="storageRules" label-width="140px" class="config-form">
          <el-form-item label="存储方式" prop="type">
            <el-radio-group v-model="storageConfig.type">
              <el-radio value="local">本地存储</el-radio>
              <el-radio value="oss">阿里云OSS</el-radio>
              <el-radio value="cos">腾讯云COS</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="上传大小限制" prop="maxSize">
            <el-input-number v-model="storageConfig.maxSize" :min="1" :max="1024" />
            <span style="margin-left: 8px">MB</span>
          </el-form-item>
          <el-form-item label="允许的文件类型" prop="allowTypes">
            <el-input v-model="storageConfig.allowTypes" style="width: 400px" placeholder="jpg,png,pdf,mp4" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="savingStorage" @click="saveStorageConfig">保存设置</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '@/api/admin'
import { learningApi } from '@/api/learning'

const activeTab = ref('basic')
const loading = ref(false)
const savingBasic = ref(false)
const savingExam = ref(false)
const savingEmail = ref(false)
const savingStorage = ref(false)
const testingEmail = ref(false)
const uploadingLogo = ref(false)

const basicFormRef = ref(null)
const examFormRef = ref(null)
const emailFormRef = ref(null)
const storageFormRef = ref(null)

const basicConfig = reactive({
  systemName: '',
  systemLogo: '',
  copyright: '',
  icp: ''
})

const examConfig = reactive({
  defaultDuration: 120,
  switchWarning: 3,
  autoSubmit: true,
  scoreDecimal: 1
})

const emailConfig = reactive({
  smtpHost: '',
  smtpPort: 465,
  senderEmail: '',
  senderPassword: ''
})

const storageConfig = reactive({
  type: 'local',
  maxSize: 100,
  allowTypes: 'jpg,png,gif,pdf,doc,docx,mp3,mp4'
})

const basicRules = {
  systemName: [{ required: true, message: '请输入系统名称', trigger: 'blur' }],
  copyright: [{ required: true, message: '请输入版权信息', trigger: 'blur' }]
}

const examRules = {
  defaultDuration: [{ required: true, message: '请输入默认考试时长', trigger: 'change' }],
  switchWarning: [{ required: true, message: '请输入切屏警告次数', trigger: 'change' }],
  scoreDecimal: [{ required: true, message: '请输入成绩小数位', trigger: 'change' }]
}

const emailRules = {
  smtpHost: [{ required: true, message: '请输入SMTP服务器', trigger: 'blur' }],
  smtpPort: [{ required: true, message: '请输入SMTP端口', trigger: 'change' }],
  senderEmail: [
    { required: true, message: '请输入发件人邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: ['blur', 'change'] }
  ]
}

const storageRules = {
  type: [{ required: true, message: '请选择存储方式', trigger: 'change' }],
  maxSize: [{ required: true, message: '请输入上传大小限制', trigger: 'change' }],
  allowTypes: [{ required: true, message: '请输入允许的文件类型', trigger: 'blur' }]
}

const toInt = (value, fallback) => {
  const n = Number(value)
  return Number.isFinite(n) ? n : fallback
}

const toBool = (value, fallback = true) => {
  if (typeof value === 'boolean') return value
  if (typeof value === 'string') {
    const normalized = value.trim().toLowerCase()
    if (normalized === 'true') return true
    if (normalized === 'false') return false
  }
  if (typeof value === 'number') return value !== 0
  return fallback
}

const getFullUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  return `${baseURL}${url}`
}

const beforeLogoUpload = (file) => {
  const isImage = ['image/png', 'image/jpeg', 'image/gif', 'image/svg+xml'].includes(file.type)
  if (!isImage) {
    ElMessage.error('只能上传 PNG、JPG、GIF、SVG 格式的图片')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

const handleLogoUpload = async ({ file }) => {
  uploadingLogo.value = true
  try {
    const res = await learningApi.uploadImage(file)
    basicConfig.systemLogo = res.data?.url || res.data
    ElMessage.success('Logo上传成功，请点击保存设置')
  } catch (error) {
    console.error('上传Logo失败:', error)
    ElMessage.error('上传Logo失败')
  } finally {
    uploadingLogo.value = false
  }
}

const buildConfigPayload = () => {
  const payload = {
    systemName: basicConfig.systemName,
    systemLogo: basicConfig.systemLogo || '',
    copyright: basicConfig.copyright,
    icp: basicConfig.icp,
    examDefaultDuration: String(examConfig.defaultDuration),
    examSwitchWarning: String(examConfig.switchWarning),
    examAutoSubmit: String(examConfig.autoSubmit),
    examScoreDecimal: String(examConfig.scoreDecimal),
    smtpHost: emailConfig.smtpHost,
    smtpPort: String(emailConfig.smtpPort),
    senderEmail: emailConfig.senderEmail,
    storageType: storageConfig.type,
    storageMaxSize: String(storageConfig.maxSize),
    storageAllowTypes: storageConfig.allowTypes,
    'email.host': emailConfig.smtpHost,
    'email.username': emailConfig.senderEmail,
    'email.port': String(emailConfig.smtpPort)
  }

  if (emailConfig.senderPassword) {
    payload.senderPassword = emailConfig.senderPassword
    payload['email.password'] = emailConfig.senderPassword
  }

  return payload
}

const loadConfigs = async () => {
  loading.value = true
  try {
    const res = await adminApi.getConfigList()
    const configs = res.data || {}

    basicConfig.systemName = configs.systemName || configs['system.name'] || '英语等级智能学习与考试系统'
    basicConfig.systemLogo = configs.systemLogo || ''
    basicConfig.copyright = configs.copyright || '© 2024 English Learning System'
    basicConfig.icp = configs.icp || ''

    examConfig.defaultDuration = toInt(configs.examDefaultDuration, 120)
    examConfig.switchWarning = toInt(configs.examSwitchWarning, 3)
    examConfig.autoSubmit = toBool(configs.examAutoSubmit, true)
    examConfig.scoreDecimal = toInt(configs.examScoreDecimal, 1)

    emailConfig.smtpHost = configs.smtpHost || configs['email.host'] || ''
    emailConfig.smtpPort = toInt(configs.smtpPort || configs['email.port'], 465)
    emailConfig.senderEmail = configs.senderEmail || configs['email.username'] || ''
    emailConfig.senderPassword = ''

    storageConfig.type = configs.storageType || 'local'
    storageConfig.maxSize = toInt(configs.storageMaxSize, 100)
    storageConfig.allowTypes = configs.storageAllowTypes || 'jpg,png,gif,pdf,doc,docx,mp3,mp4'
  } catch (error) {
    console.error('加载配置失败:', error)
    ElMessage.error('加载配置失败')
  } finally {
    loading.value = false
  }
}

const saveBasicConfig = async () => {
  if (!basicFormRef.value) return
  const valid = await basicFormRef.value.validate().catch(() => false)
  if (!valid) return

  savingBasic.value = true
  try {
    await adminApi.updateConfigs(buildConfigPayload())
    ElMessage.success('基础设置保存成功')
    await loadConfigs()
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error(error?.message || '保存失败')
  } finally {
    savingBasic.value = false
  }
}

const saveExamConfig = async () => {
  if (!examFormRef.value) return
  const valid = await examFormRef.value.validate().catch(() => false)
  if (!valid) return

  savingExam.value = true
  try {
    await adminApi.updateConfigs(buildConfigPayload())
    ElMessage.success('考试设置保存成功')
    await loadConfigs()
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error(error?.message || '保存失败')
  } finally {
    savingExam.value = false
  }
}

const testEmail = async () => {
  if (!emailFormRef.value) return
  const valid = await emailFormRef.value.validate().catch(() => false)
  if (!valid) return

  testingEmail.value = true
  try {
    await adminApi.updateConfigs(buildConfigPayload())
    await adminApi.testEmail()
    ElMessage.success('测试邮件已发送，请检查收件箱')
  } catch (error) {
    console.error('发送测试邮件失败:', error)
    ElMessage.error(error?.message || '发送测试邮件失败')
  } finally {
    testingEmail.value = false
  }
}

const saveEmailConfig = async () => {
  if (!emailFormRef.value) return
  const valid = await emailFormRef.value.validate().catch(() => false)
  if (!valid) return

  savingEmail.value = true
  try {
    await adminApi.updateConfigs(buildConfigPayload())
    ElMessage.success('邮件设置保存成功')
    await loadConfigs()
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error(error?.message || '保存失败')
  } finally {
    savingEmail.value = false
  }
}

const saveStorageConfig = async () => {
  if (!storageFormRef.value) return
  const valid = await storageFormRef.value.validate().catch(() => false)
  if (!valid) return

  savingStorage.value = true
  try {
    await adminApi.updateConfigs(buildConfigPayload())
    ElMessage.success('存储设置保存成功')
    await loadConfigs()
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error(error?.message || '保存失败')
  } finally {
    savingStorage.value = false
  }
}

onMounted(() => {
  loadConfigs()
})
</script>

<style lang="scss" scoped>
.system-config {
  .page-header {
    margin-bottom: 24px;

    h2 {
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary);
    }
  }

  .config-tabs {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;

    :deep(.el-tabs__header) {
      margin-bottom: 24px;
    }
  }

  .config-form {
    max-width: 600px;
  }

  .logo-upload {
    .logo-preview {
      margin-bottom: 12px;
      position: relative;
      display: inline-block;

      img {
        max-width: 200px;
        max-height: 80px;
        border: 1px solid var(--border-color, #dcdfe6);
        border-radius: 4px;
        padding: 4px;
        background: #fff;
      }

      .logo-actions {
        margin-top: 8px;
      }
    }

    .upload-tip {
      font-size: 12px;
      color: var(--text-secondary, #909399);
      margin-top: 8px;
    }
  }
}
</style>
