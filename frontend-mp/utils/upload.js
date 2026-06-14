const { LOCAL, BASE_URL } = require('../config')

function buildUploadUrl(path) {
  let url = path
  if (LOCAL && url.startsWith('/api')) {
    url = url.substring(4)
  }
  return BASE_URL + url
}

function uploadFile(filePath, path = '/api/app/file/upload') {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    wx.uploadFile({
      url: buildUploadUrl(path),
      filePath,
      name: 'file',
      header: {
        Authorization: token || ''
      },
      success(res) {
        if (res.statusCode === 401) {
          reject(new Error('登录已过期，请重新登录'))
          return
        }
        if (res.statusCode >= 400) {
          reject(new Error(`上传失败(${res.statusCode})`))
          return
        }

        let data
        try {
          data = JSON.parse(res.data)
        } catch (error) {
          reject(new Error('上传响应解析失败'))
          return
        }

        if (!data || data.code !== 200) {
          reject(new Error((data && data.msg) || '上传失败'))
          return
        }

        resolve(data.data)
      },
      fail(err) {
        reject(err)
      }
    })
  })
}

module.exports = { uploadFile, buildUploadUrl }
