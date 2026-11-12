import { adminApi } from './admin'

export const configApi = {
  // 获取所有配置
  getConfigs() {
    return adminApi.getConfigList()
  },

  // 获取配置值
  getConfig(key) {
    return adminApi.getConfig(key)
  },

  // 更新配置
  updateConfig(key, value) {
    return adminApi.updateConfig(key, value)
  },

  // 批量更新配置
  updateConfigs(configMap) {
    return adminApi.updateConfigs(configMap)
  },

  // 保存基础配置
  saveBasicConfig(config) {
    return adminApi.updateConfigs(config)
  },

  // 保存考试配置
  saveExamConfig(config) {
    const payload = Object.fromEntries(
      Object.entries(config).map(([key, value]) => [`exam.${key}`, String(value)])
    )
    return adminApi.updateConfigs(payload)
  },

  // 保存邮件配置
  saveEmailConfig(config) {
    const payload = Object.fromEntries(
      Object.entries(config).map(([key, value]) => [`email.${key}`, String(value)])
    )
    return adminApi.updateConfigs(payload)
  },

  // 保存存储配置
  saveStorageConfig(config) {
    const payload = Object.fromEntries(
      Object.entries(config).map(([key, value]) => [`storage.${key}`, String(value)])
    )
    return adminApi.updateConfigs(payload)
  },

  // 测试邮件发送
  testEmail() {
    return adminApi.testEmail()
  }
}
