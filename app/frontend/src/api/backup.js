import request from '@/utils/request'

export const backupApi = {
  // 创建备份
  createBackup() {
    return request({
      url: '/backup',
      method: 'post'
    })
  },

  // 获取备份列表
  getBackupList() {
    return request({
      url: '/backup/list',
      method: 'get'
    })
  },

  // 恢复备份
  restoreBackup(filename) {
    return request({
      url: `/backup/restore/${filename}`,
      method: 'post'
    })
  },

  // 删除备份
  deleteBackup(filename) {
    return request({
      url: `/backup/${filename}`,
      method: 'delete'
    })
  },

  // 下载备份
  downloadBackup(filename) {
    return request({
      url: `/backup/download/${filename}`,
      method: 'get',
      responseType: 'blob'
    })
  },

  // 上传并恢复备份
  uploadAndRestore(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
      url: '/backup/upload-restore',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}
