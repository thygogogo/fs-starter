const { LOCAL, BASE_URL } = require('../config')
const AUTH_SKIP_URLS = ['/api/app/wx/login']

function isPublicUrl(url) {
  return AUTH_SKIP_URLS.some((path) => url.includes(path))
}

function request(options) {
  return new Promise((resolve, reject) => {
    const optionalAuth = options.skipAuth || isPublicUrl(options.url)
    const storedToken = wx.getStorageSync('token')
    const requestToken = optionalAuth ? (storedToken || '') : storedToken

    let data = options.data
    if (data && typeof data === 'object' && !Array.isArray(data)) {
      const filtered = {}
      Object.keys(data).forEach((key) => {
        if (data[key] !== undefined && data[key] !== null && data[key] !== '') {
          filtered[key] = data[key]
        }
      })
      data = filtered
    }

    let url = options.url
    if (LOCAL && url.startsWith('/api')) {
      url = url.substring(4)
    }

    wx.request({
      url: BASE_URL + url,
      method: options.method || 'GET',
      data,
      header: {
        'Content-Type': 'application/json',
        Authorization: requestToken || '',
        ...options.header,
      },
      success(res) {
        if (res.statusCode === 401 && !optionalAuth) {
          const currentToken = wx.getStorageSync('token')
          if (requestToken && requestToken === currentToken) {
            const { clearLoginInfo, buildLoginUrl } = require('./auth')
            clearLoginInfo()
            const pages = getCurrentPages()
            const currentRoute = pages.length ? pages[pages.length - 1].route : ''
            const hasLoginPage = pages.some((p) => p.route === 'pages/login/login')
            if (currentRoute !== 'pages/login/login' && !hasLoginPage) {
              wx.showModal({
                title: '提示',
                content: '登录已过期，请重新登录',
                showCancel: false,
                success() {
                  wx.navigateTo({ url: buildLoginUrl() })
                },
              })
            }
          }
          reject(new Error('未登录'))
          return
        }

        if (res.statusCode >= 400) {
          wx.showToast({ title: '请求失败', icon: 'none' })
          reject(new Error(`HTTP ${res.statusCode}`))
          return
        }

        const body = res.data
        if (!body || body.code !== 200) {
          wx.showToast({ title: (body && body.msg) || '请求失败', icon: 'none' })
          reject(new Error(body && body.msg))
          return
        }

        resolve(body)
      },
      fail(err) {
        wx.showToast({ title: '网络错误', icon: 'none' })
        reject(err)
      },
    })
  })
}

function get(url, data, options = {}) {
  return request({ url, method: 'GET', data, ...options })
}

function post(url, data, options = {}) {
  return request({ url, method: 'POST', data, ...options })
}

function put(url, data, options = {}) {
  return request({ url, method: 'PUT', data, ...options })
}

function del(url, data, options = {}) {
  return request({ url, method: 'DELETE', data, ...options })
}

module.exports = { request, get, post, put, del }
