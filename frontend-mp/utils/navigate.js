/**
 * 页面栈为空或仅一层时，返回 Tab 页兜底
 */
const DEFAULT_TAB = '/pages/index/index'

function safeNavigateBack(fallbackTab = DEFAULT_TAB) {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    wx.navigateBack()
    return
  }
  wx.switchTab({
    url: fallbackTab,
    fail: () => wx.reLaunch({ url: fallbackTab }),
  })
}

module.exports = { safeNavigateBack }
