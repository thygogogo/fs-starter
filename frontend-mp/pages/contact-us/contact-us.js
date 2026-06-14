Page({
  data: {
    phone: '400-000-0000',
    email: 'support@example.com',
    address: '请在 contact-us.js 中配置您的联系信息',
    officialAccount: {
      name: 'YOUR_OFFICIAL_ACCOUNT',
      id: 'your_official_account_id',
      tip: '打开微信 → 搜索公众号 → 关注',
    },
  },

  onCallPhone() {
    wx.makePhoneCall({
      phoneNumber: this.data.phone.replace(/-/g, ''),
      fail() {},
    })
  },

  onCopyEmail() {
    wx.setClipboardData({
      data: this.data.email,
      success() {
        wx.showToast({ title: '邮箱已复制', icon: 'success' })
      },
    })
  },

  onCopyAddress() {
    wx.setClipboardData({
      data: this.data.address,
      success() {
        wx.showToast({ title: '地址已复制', icon: 'success' })
      },
    })
  },
})
