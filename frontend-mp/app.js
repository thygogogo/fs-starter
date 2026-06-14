const { silentLogin, isLoggedIn, enableSilentLogin } = require('./utils/auth')

App({
  onLaunch() {
    this.globalData.isLoggedIn = isLoggedIn()
    if (!this.globalData.isLoggedIn && enableSilentLogin) {
      silentLogin().then((ok) => {
        this.globalData.isLoggedIn = ok
      })
    }
  },
  globalData: {
    userInfo: null,
    isLoggedIn: false,
    justLoggedIn: false,
  },
})
