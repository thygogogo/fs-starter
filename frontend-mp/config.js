/**
 * 接口环境配置
 * LOCAL = true  → 调用本地后端 http://localhost:8888
 * LOCAL = false → 调用远程接口
 */
const LOCAL = true

const BASE_URL = LOCAL ? 'http://localhost:8888' : 'https://your-api.example.com'

module.exports = { LOCAL, BASE_URL }
