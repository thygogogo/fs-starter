const { LOCAL, BASE_URL } = require('../config')
const AUTH_SKIP_URLS = [
  '/api/app/wx/login',
]

function isPublicUrl(url) {
  return AUTH_SKIP_URLS.some((path) => url.includes(path))
}

function request(options) {
  return new Promise((resolve, reject) => {
    const useShopToken = options.useShopToken === true
    const optionalAuth = options.skipAuth || isPublicUrl(options.url)
    const storedToken = useShopToken ? wx.getStorageSync('shopToken') : wx.getStorageSync('token')
    // 可选登录接口：有 token 仍携带（兼容未部署 @SaIgnore 的后端），无 token 则匿名访问
    const requestToken = optionalAuth ? (storedToken || '') : storedToken

    // 过滤 undefined / null / 空字符串参数
    let data = options.data
    if (data && typeof data === 'object' && !Array.isArray(data)) {
      const filtered = {}
      Object.keys(data).forEach(key => {
        if (data[key] !== undefined && data[key] !== null && data[key] !== '') {
          filtered[key] = data[key]
        }
      })
      data = filtered
    }

    // 本地模式去掉 /api 前缀，远程模式保持原路径
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
        'Authorization': requestToken || '',
        ...options.header
      },
      success(res) {
        console.log('[request]', options.url, 'statusCode:', res.statusCode, 'optionalAuth:', optionalAuth)
        if (res.statusCode === 401 && !optionalAuth) {
          const currentToken = useShopToken
            ? wx.getStorageSync('shopToken')
            : wx.getStorageSync('token')
          // 仅清除与当前 401 请求对应的 token，避免旧请求覆盖刚登录的新 token
          if (requestToken && requestToken === currentToken) {
            if (useShopToken) {
              wx.removeStorageSync('shopToken')
              wx.removeStorageSync('shopInfo')
              wx.showModal({
                title: '提示',
                content: '商家登录已过期，请重新登录',
                showCancel: false,
                success() {
                  wx.navigateTo({ url: '/pages/shop-login/shop-login' })
                }
              })
            } else {
              const { clearLoginInfo } = require('./auth')
              clearLoginInfo()
              const pages = getCurrentPages()
              const currentRoute = pages.length ? pages[pages.length - 1].route : ''
              console.log('[request] 401 currentRoute:', currentRoute)
              const hasLoginPage = pages.some((p) => p.route === 'pages/login/login')
              if (currentRoute !== 'pages/login/login' && !hasLoginPage) {
                wx.showModal({
                  title: '提示',
                  content: '登录已过期，请重新登录',
                  showCancel: false,
                  success() {
                    const { buildLoginUrl } = require('./auth')
                    wx.navigateTo({ url: buildLoginUrl() })
                  }
                })
              }
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

        const data = res.data
        if (!data || data.code !== 200) {
          wx.showToast({ title: (data && data.msg) || '请求失败', icon: 'none' })
          reject(new Error(data && data.msg))
          return
        }

        resolve(data)
      },
      fail(err) {
        wx.showToast({ title: '网络错误', icon: 'none' })
        reject(err)
      }
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
