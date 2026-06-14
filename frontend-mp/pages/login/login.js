const { post } = require('../../utils/request')
const { saveLoginInfo, isLoggedIn, exitLoginPage } = require('../../utils/auth')

Page({
  data: {
    loading: false
  },

  onLoad(options) {
    this._fromRoute = options.from || ''
    if (isLoggedIn()) {
      this.finishLogin(false)
    }
  },

  finishLogin(showToast) {
    const app = getApp()
    if (app && app.globalData) {
      app.globalData.isLoggedIn = true
      app.globalData.justLoggedIn = true
    }

    const notifyOpener = () => {
      const eventChannel = this.getOpenerEventChannel()
      if (eventChannel && eventChannel.emit) {
        eventChannel.emit('loginSuccess')
      }
    }

    // 先离开登录页，再通知 opener，避免「我的」页 onMenuTap 抢先跳转导致无法返回
    exitLoginPage(this._fromRoute, notifyOpener)

    if (showToast) {
      wx.showToast({ title: '登录成功', icon: 'success', duration: 1500 })
    }
  },

  onLogin() {
    if (this.data.loading) return
    if (isLoggedIn()) {
      this.finishLogin(false)
      return
    }

    this.doLogin()
  },

  doLogin() {
    this.setData({ loading: true })

    wx.login({
      success: (loginRes) => {
        if (!loginRes.code) {
          this.setData({ loading: false })
          wx.showToast({ title: '登录失败，请重试', icon: 'none' })
          return
        }

        post('/api/app/wx/login', { code: loginRes.code }, { skipAuth: true })
          .then((res) => {
            if (!saveLoginInfo(res.data)) {
              this.setData({ loading: false })
              wx.showToast({ title: '登录异常，请重试', icon: 'none' })
              return
            }
            this.setData({ loading: false })
            this.finishLogin(true)
          })
          .catch(() => {
            this.setData({ loading: false })
          })
      },
      fail: () => {
        this.setData({ loading: false })
        wx.showToast({ title: '登录失败，请重试', icon: 'none' })
      }
    })
  },

  goAgreement() {
    wx.navigateTo({ url: '/pages/agreement/agreement' })
  },

  goPrivacy() {
    wx.navigateTo({ url: '/pages/privacy/privacy' })
  }
})
