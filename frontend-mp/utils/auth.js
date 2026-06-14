const { post } = require('./request')
const { DEFAULT_AVATAR } = require('./avatar')

const { envVersion } = wx.getAccountInfoSync().miniProgram
const FORCE_SILENT_LOGIN = false
const enableSilentLogin = FORCE_SILENT_LOGIN || envVersion !== 'develop'

const LOGIN_PAGE = 'pages/login/login'
const TAB_PAGES = ['pages/index/index', 'pages/profile/profile']
const DEFAULT_TAB = 'pages/index/index'

let silentLoginPromise = null
let checkLoginPromise = null
let loginTipShowing = false

function getCurrentRoute() {
  const pages = getCurrentPages()
  return pages.length ? pages[pages.length - 1].route : ''
}

function isLoginPage() {
  return getCurrentRoute() === LOGIN_PAGE
}

function hasLoginPageInStack() {
  return getCurrentPages().some((p) => p.route === LOGIN_PAGE)
}

function isLoggedIn() {
  return !!wx.getStorageSync('token')
}

function saveLoginInfo(data) {
  const { token, userId, username, nickname } = data || {}
  if (!token) return false

  wx.setStorageSync('token', token)
  wx.setStorageSync('userInfo', {
    userId,
    nickName: username || nickname || '',
    avatarUrl: DEFAULT_AVATAR
  })

  const app = getApp()
  if (app) app.globalData.isLoggedIn = true
  return true
}

function clearLoginInfo() {
  wx.clearStorageSync()

  const app = getApp()
  if (app && app.globalData) {
    app.globalData.isLoggedIn = false
    app.globalData.userInfo = null
    app.globalData.justLoggedIn = false
  }
}

/**
 * 检查是否已登录，未登录则尝试静默登录，仍失败则弹窗引导登录
 * @returns {Promise<boolean>} 是否已登录
 */
async function checkLogin() {
  if (isLoggedIn()) return true
  // 已在登录页，避免重复弹「去登录」
  if (isLoginPage()) return false

  if (checkLoginPromise) return checkLoginPromise

  checkLoginPromise = (async () => {
    if (isLoggedIn()) return true

    if (enableSilentLogin && !hasLoginPageInStack()) {
      const ok = await silentLogin()
      if (ok) return true
    }

    return showLoginTip()
  })()

  try {
    return await checkLoginPromise
  } finally {
    checkLoginPromise = null
  }
}

function buildLoginUrl() {
  const from = getCurrentRoute()
  if (!from || from === LOGIN_PAGE) return '/pages/login/login'
  return `/pages/login/login?from=${encodeURIComponent(from)}`
}

/**
 * 登录成功后离开登录页
 * Tab 来源页必须用 switchTab（Tab 不在页面栈中，navigateBack 无效）
 * @param {string} [preferredRoute] 来源页 route，如 pages/profile/profile
 * @param {Function} [onDone] 离开完成后的回调（用于通知 opener 登录成功）
 */
function exitLoginPage(preferredRoute, onDone) {
  const done = typeof onDone === 'function' ? onDone : () => {}
  const pages = getCurrentPages()
  if (!pages.length || pages[pages.length - 1].route !== LOGIN_PAGE) {
    done()
    return
  }

  const pickTab = (route) => {
    if (route && TAB_PAGES.includes(route)) return route
    if (preferredRoute && TAB_PAGES.includes(preferredRoute)) return preferredRoute
    return DEFAULT_TAB
  }

  const goTab = (route) => {
    const tab = pickTab(route)
    wx.switchTab({
      url: '/' + tab,
      success: done,
      fail: () => wx.reLaunch({ url: '/' + tab, success: done, fail: done })
    })
  }

  // 从 Tab 进入登录：直接 switchTab 回来源 Tab
  if (preferredRoute && TAB_PAGES.includes(preferredRoute)) {
    goTab(preferredRoute)
    return
  }

  let delta = 0
  for (let i = pages.length - 1; i >= 0; i--) {
    if (pages[i].route === LOGIN_PAGE) delta++
    else break
  }

  const targetIndex = pages.length - delta - 1
  if (targetIndex < 0) {
    goTab(preferredRoute)
    return
  }

  const targetRoute = pages[targetIndex].route
  if (TAB_PAGES.includes(targetRoute)) {
    goTab(targetRoute)
    return
  }

  wx.navigateBack({
    delta,
    success: done,
    fail: () => goTab(targetRoute)
  })
}

function openLoginPage(resolve) {
  wx.navigateTo({
    url: buildLoginUrl(),
    events: {
      loginSuccess: () => resolve(isLoggedIn())
    },
    fail: () => resolve(false)
  })
}

function navigateToLogin(resolve) {
  const pages = getCurrentPages()
  const topRoute = pages.length ? pages[pages.length - 1].route : ''

  if (topRoute === LOGIN_PAGE) {
    resolve(false)
    return
  }

  const loginIndex = pages.findIndex((p) => p.route === LOGIN_PAGE)
  if (loginIndex >= 0 && loginIndex < pages.length - 1) {
    wx.navigateBack({
      delta: pages.length - 1 - loginIndex,
      fail: () => openLoginPage(resolve)
    })
    return
  }

  openLoginPage(resolve)
}

/**
 * 直接打开登录页（不弹窗），用于「我的」页点击未登录区域
 */
function goToLoginPage(fromRoute) {
  if (isLoggedIn()) return Promise.resolve(true)
  if (isLoginPage()) return Promise.resolve(false)
  const url = fromRoute
    ? `/pages/login/login?from=${encodeURIComponent(fromRoute)}`
    : buildLoginUrl()
  return new Promise((resolve) => {
    wx.navigateTo({
      url,
      events: { loginSuccess: () => resolve(isLoggedIn()) },
      fail: () => resolve(false),
    })
  })
}

function showLoginTip() {
  if (loginTipShowing || isLoginPage()) {
    return Promise.resolve(false)
  }

  loginTipShowing = true
  return new Promise((resolve) => {
    wx.showModal({
      title: '提示',
      content: '您还未登录，是否前往登录？',
      confirmText: '去登录',
      success(res) {
        if (!res.confirm) {
          resolve(false)
          return
        }
        navigateToLogin(resolve)
      },
      fail: () => resolve(false),
      complete: () => {
        loginTipShowing = false
      }
    })
  })
}

/**
 * 静默登录：wx.login 获取 code → 调后端换 token
 * @returns {Promise<boolean>} 是否登录成功
 */
function silentLogin() {
  if (!enableSilentLogin) return Promise.resolve(false)
  if (isLoggedIn()) return Promise.resolve(true)
  if (isLoginPage() || hasLoginPageInStack()) return Promise.resolve(false)
  if (silentLoginPromise) return silentLoginPromise

  silentLoginPromise = new Promise((resolve) => {
    wx.login({
      success(loginRes) {
        if (!loginRes.code) {
          resolve(false)
          return
        }
        post('/api/app/wx/login', { code: loginRes.code }, { skipAuth: true })
          .then((res) => resolve(saveLoginInfo(res.data)))
          .catch(() => resolve(false))
      },
      fail: () => resolve(false)
    })
  }).finally(() => {
    silentLoginPromise = null
  })

  return silentLoginPromise
}

module.exports = {
  isLoggedIn,
  isLoginPage,
  saveLoginInfo,
  clearLoginInfo,
  checkLogin,
  silentLogin,
  exitLoginPage,
  buildLoginUrl,
  goToLoginPage,
  enableSilentLogin
}
