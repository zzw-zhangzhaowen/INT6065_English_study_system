import request from '@/utils/request'

const photoApi = {
  // 获取photo文件夹中的图片列表
  getPhotoList() {
    return request({
      url: '/photo/list',
      method: 'get'
    })
  },

  // 上传图片到photo文件夹
  uploadPhoto(file) {
    const formData = new FormData()
    formData.append('file', file)
    
    return request({
      url: '/photo/upload',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 删除photo文件夹中的图片
  deletePhoto(filename) {
    return request({
      url: '/photo/delete',
      method: 'delete',
      params: { filename }
    })
  },

  // 替换photo文件夹中的图片
  replacePhoto(oldFilename, file) {
    const formData = new FormData()
    formData.append('file', file)
    
    return request({
      url: '/photo/replace',
      method: 'post',
      params: { oldFilename },
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}

export default photoApi
export { photoApi }
