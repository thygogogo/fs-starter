const { get, put } = require('../utils/request')

function getProfile() {
  return get('/api/app/user/profile')
}

function updateProfile(data) {
  return put('/api/app/user/profile', data)
}

module.exports = { getProfile, updateProfile }
