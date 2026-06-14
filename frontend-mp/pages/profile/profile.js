const { checkLogin, clearLoginInfo, isLoggedIn, goToLoginPage } = require('../../utils/auth')
const { getProfile } = require('../../api/user')
const { EMPTY_AVATAR, resolveAvatarUrl } = require('../../utils/avatar')
const { formatDateTime } = require('../../utils/format-datetime')

Page({
  data: {
    userInfo: { avatarUrl: EMPTY_AVATAR },
    userId: '',
    registerTime: '',
    isLoggedIn: false,
    menuList: [
      { id: 1, name: '个人设置', icon: '', path: '/pages/settings/settings', needLogin: true },
      { id: 2, name: '联系我们', icon: '', path: '/pages/contact-us/contact-us', needLogin: false },
      { id: 3, name: '用户协议', icon: '', path: '/pages/agreement/agreement', needLogin: false },
      { id: 4, name: '隐私政策', icon: '', path: '/pages/privacy/privacy', needLogin: false },
    ],
    visibleMenuList: [],
  },

  onShow() {
    this.loadUserInfo()
  },

  loadUserInfo() {
    const loggedIn = isLoggedIn()
    const visibleMenuList = this.data.menuList

    if (!loggedIn) {
      this.setData({
        userInfo: { avatarUrl: EMPTY_AVATAR },
        userId: '',
        registerTime: '',
        isLoggedIn: false,
        visibleMenuList,
      })
      return
    }

    const cached = wx.getStorageSync('userInfo') || {}
    this.setData({
      userInfo: {
        nickName: cached.nickName || '',
        avatarUrl: resolveAvatarUrl(cached.avatarUrl),
      },
      userId: cached.userId || '',
      registerTime: cached.registerTime ? formatDateTime(cached.registerTime) : '',
      isLoggedIn: true,
      visibleMenuList,
    })

    getProfile()
      .then((res) => {
        const profile = res.data
        const userInfo = {
          nickName: profile.nickname || cached.nickName || '',
          avatarUrl: resolveAvatarUrl(profile.avatar || cached.avatarUrl),
        }
        wx.setStorageSync('userInfo', {
          ...cached,
          nickName: userInfo.nickName,
          avatarUrl: userInfo.avatarUrl,
          userId: String(profile.id || cached.userId || ''),
          registerTime: profile.createTime || cached.registerTime,
        })
        this.setData({
          userInfo,
          userId: String(profile.id || cached.userId || ''),
          registerTime: profile.createTime ? formatDateTime(profile.createTime) : this.data.registerTime,
        })
      })
      .catch(() => {})
  },

  onMenuTap(e) {
    const item = e.currentTarget.dataset.item
    if (item.needLogin) {
      checkLogin().then((ok) => {
        if (!ok) return
        if (item.path) wx.navigateTo({ url: item.path })
      })
      return
    }
    if (item.path) wx.navigateTo({ url: item.path })
  },

  onLoginTap() {
    goToLoginPage('pages/profile/profile')
  },

  onLogoutTap() {
    wx.showModal({
      title: '提示',
      content: '确定退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          clearLoginInfo()
          this.loadUserInfo()
        }
      },
    })
  },
})
