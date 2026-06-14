Page({
  data: {
    phone: '400-888-6699',
    email: 'contact@junyouxiang.com',
    address: '浙江省温州市瑞安市迷彩共享空间军优享服务中心',
    officialAccount: {
      name: '军优享',
      id: 'junyouxiang_official',
      tip: '打开微信 → 搜索公众号 → 关注「军优享」'
    }
  },

  onCallPhone() {
    wx.makePhoneCall({
      phoneNumber: this.data.phone.replace(/-/g, ''),
      fail() {}
    })
  },

  onCopyEmail() {
    wx.setClipboardData({
      data: this.data.email,
      success() {
        wx.showToast({ title: '邮箱已复制', icon: 'success' })
      }
    })
  },

  onCopyAddress() {
    wx.setClipboardData({
      data: this.data.address,
      success() {
        wx.showToast({ title: '地址已复制', icon: 'success' })
      }
    })
  },

  onCopyOfficialAccount() {
    wx.setClipboardData({
      data: this.data.officialAccount.name,
      success() {
        wx.showToast({ title: '公众号名称已复制', icon: 'success' })
      }
    })
  }
})
