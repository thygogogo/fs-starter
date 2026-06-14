const { checkLogin } = require('../../utils/auth')
const { DEFAULT_AVATAR, resolveAvatarUrl } = require('../../utils/avatar')
const { updateProfile } = require('../../api/user')
const { uploadFile } = require('../../utils/upload')

function saveUserInfo(partial) {
  const userInfo = wx.getStorageSync('userInfo') || {}
  wx.setStorageSync('userInfo', { ...userInfo, ...partial })
}

function syncProfileFromServer(profile) {
  if (!profile) return
  saveUserInfo({
    nickName: profile.nickname || '',
    avatarUrl: profile.avatar || DEFAULT_AVATAR
  })
}

Page({
  data: {
    nickName: '',
    avatarUrl: DEFAULT_AVATAR,
    saving: false
  },

  async onLoad() {
    if (!await checkLogin()) {
      wx.navigateBack()
      return
    }
    this.loadProfile()
  },

  onShow() {
    if (!wx.getStorageSync('token')) return
    this.loadProfile()
  },

  loadProfile() {
    const userInfo = wx.getStorageSync('userInfo') || {}
    this.setData({
      nickName: userInfo.nickName || '',
      avatarUrl: resolveAvatarUrl(userInfo.avatarUrl)
    })
  },

  onAvatarTap() {
    if (this.data.saving) return
    wx.chooseImage({
      count: 1,
      sourceType: ['album', 'camera'],
      sizeType: ['compressed'],
      success: (res) => {
        this.handleAvatarUpdate(res.tempFilePaths[0])
      }
    })
  },

  async handleAvatarUpdate(filePath) {
    this.setData({ saving: true })
    wx.showLoading({ title: '上传中', mask: true })
    try {
      const avatarUrl = await uploadFile(filePath)
      const res = await updateProfile({ avatar: avatarUrl })
      syncProfileFromServer(res.data)
      this.setData({ avatarUrl: resolveAvatarUrl(avatarUrl) })
      wx.showToast({ title: '头像已更新', icon: 'success' })
    } catch (err) {
      wx.showToast({ title: err.message || '头像更新失败', icon: 'none' })
    } finally {
      wx.hideLoading()
      this.setData({ saving: false })
    }
  },

  onNickNameTap() {
    if (this.data.saving) return
    wx.showModal({
      title: '修改用户名',
      editable: true,
      placeholderText: '请输入用户名',
      content: this.data.nickName,
      success: (res) => {
        if (!res.confirm) return
        const nickName = (res.content || '').trim()
        if (!nickName) {
          wx.showToast({ title: '用户名不能为空', icon: 'none' })
          return
        }
        if (nickName.length > 20) {
          wx.showToast({ title: '用户名不能超过20字', icon: 'none' })
          return
        }
        this.handleNicknameUpdate(nickName)
      }
    })
  },

  async handleNicknameUpdate(nickName) {
    this.setData({ saving: true })
    wx.showLoading({ title: '保存中', mask: true })
    try {
      const res = await updateProfile({ nickname: nickName })
      syncProfileFromServer(res.data)
      this.setData({ nickName })
      wx.showToast({ title: '用户名已更新', icon: 'success' })
    } catch (err) {
      wx.showToast({ title: err.message || '用户名更新失败', icon: 'none' })
    } finally {
      wx.hideLoading()
      this.setData({ saving: false })
    }
  },

  onAvatarError() {
    if (this.data.avatarUrl !== DEFAULT_AVATAR) {
      this.setData({ avatarUrl: DEFAULT_AVATAR })
    }
  }
})
