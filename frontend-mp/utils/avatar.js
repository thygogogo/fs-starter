const EMPTY_AVATAR = ''
const DEFAULT_AVATAR = '/images/avatar.jpg'

function resolveAvatarUrl(avatar) {
  return avatar || DEFAULT_AVATAR
}

function getUserAvatarUrl() {
  const userInfo = wx.getStorageSync('userInfo') || {}
  return resolveAvatarUrl(userInfo.avatarUrl)
}

module.exports = {
  EMPTY_AVATAR,
  DEFAULT_AVATAR,
  resolveAvatarUrl,
  getUserAvatarUrl
}
