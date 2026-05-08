/**
 * HTTP请求拦截器和配置
 * 处理请求头、错误处理、token管理等
 */

const BASE_URL = 'http://localhost:8080/api'

/**
 * 发送HTTP请求
 * @param {string} method - 请求方法
 * @param {string} url - 请求URL
 * @param {object} data - 请求数据
 * @param {boolean} needAuth - 是否需要认证
 */
async function request(method, url, data = {}, needAuth = true) {
  // 构建完整URL
  const fullUrl = `${BASE_URL}${url}`

  // 构建请求头
  const header = {
    'Content-Type': 'application/json'
  }

  // 添加认证token
  if (needAuth) {
    const token = uni.getStorageSync('token')
    if (token) {
      header['Authorization'] = `Bearer ${token}`
    }
  }

  return new Promise((resolve, reject) => {
    uni.request({
      url: fullUrl,
      method: method,
      data: method === 'GET' ? {} : data,
      header: header,
      success: (res) => {
        const { code, message, data: responseData } = res.data

        // 处理响应状态
        if (code === 200) {
          resolve(responseData)
        } else if (code === 401) {
          // token过期，跳转到登录页
          uni.removeStorageSync('token')
          uni.removeStorageSync('userInfo')
          uni.reLaunch({
            url: '/pages/login/index'
          })
          reject(new Error(message || '登录已过期，请重新登录'))
        } else {
          reject(new Error(message || '请求失败'))
        }
      },
      fail: (err) => {
        reject(new Error(err.errMsg || '网络请求失败'))
      }
    })
  })
}

/**
 * GET请求
 */
export const GET = (url, params = {}, needAuth = true) => {
  const queryStr = Object.keys(params)
    .map(key => `${key}=${params[key]}`)
    .join('&')
  const finalUrl = queryStr ? `${url}?${queryStr}` : url
  return request('GET', finalUrl, {}, needAuth)
}

/**
 * POST请求
 */
export const POST = (url, data = {}, needAuth = true) => {
  return request('POST', url, data, needAuth)
}

/**
 * PUT请求
 */
export const PUT = (url, data = {}, needAuth = true) => {
  return request('PUT', url, data, needAuth)
}

/**
 * DELETE请求
 */
export const DELETE = (url, data = {}, needAuth = true) => {
  return request('DELETE', url, data, needAuth)
}
