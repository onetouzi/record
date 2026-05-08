/**
 * 用户API服务
 * 处理登录、获取用户信息等接口
 */

import { POST, GET } from '../utils/request'

/**
 * 微信登录
 * @param {string} code - 微信登录code
 * @returns {Promise<object>} 登录响应，包含token和用户信息
 */
export const wxLogin = (code) => {
  return POST('/user/login', { code }, false)
}

/**
 * 获取用户信息
 * @returns {Promise<object>} 用户信息
 */
export const getUserInfo = () => {
  return GET('/user/info')
}

/**
 * 存储登录信息
 * @param {object} loginResponse - 登录响应
 */
export const saveLoginInfo = (loginResponse) => {
  const { token, userInfo } = loginResponse
  uni.setStorageSync('token', token)
  uni.setStorageSync('userInfo', JSON.stringify(userInfo))
}

/**
 * 清除登录信息
 */
export const clearLoginInfo = () => {
  uni.removeStorageSync('token')
  uni.removeStorageSync('userInfo')
}

/**
 * 获取本地存储的用户信息
 * @returns {object} 用户信息
 */
export const getLocalUserInfo = () => {
  const userInfoStr = uni.getStorageSync('userInfo')
  return userInfoStr ? JSON.parse(userInfoStr) : null
}

/**
 * 检查是否已登录
 * @returns {boolean}
 */
export const isLoggedIn = () => {
  return !!uni.getStorageSync('token')
}
