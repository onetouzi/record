<template>
  <view class="login-container">
    <!-- 顶部装饰 -->
    <view class="login-header">
      <view class="app-logo">💼</view>
      <view class="app-title">零工记账</view>
      <view class="app-subtitle">轻松记录每一笔收入</view>
    </view>

    <!-- 登录表单 -->
    <view class="login-form">
      <view class="form-item">
        <input
          type="text"
          placeholder="请输入手机号（可选）"
          v-model="phone"
          class="form-input"
          placeholder-class="placeholder-text"
        />
      </view>
    </view>

    <!-- 登录按钮 -->
    <view class="login-button-group">
      <button
        class="wx-login-btn"
        @click="loginWithWeChat"
        :disabled="loading"
      >
        {{ loading ? '登录中...' : '🔐 微信登录' }}
      </button>
      <view class="login-tip">点击按钮使用微信快速登录</view>
    </view>

    <!-- 底部信息 -->
    <view class="login-footer">
      <view class="footer-text">安全声明</view>
      <view class="footer-desc">
        我们保证您的隐私安全。登录后，您可以记录工作收入和日常支出。
      </view>
    </view>
  </view>
</template>

<script>
import { wxLogin, saveLoginInfo } from '../../api/user'

export default {
  data() {
    return {
      phone: '',
      loading: false
    }
  },
  
  methods: {
    /**
     * 微信登录处理
     */
    loginWithWeChat() {
      if (this.loading) return

      this.loading = true
      uni.login({
        provider: 'weixin',
        success: (loginRes) => {
          // 使用code换取openid
          this.performLogin(loginRes.authorizationCode || loginRes.code)
        },
        fail: (err) => {
          this.loading = false
          uni.showToast({
            title: '登录失败',
            icon: 'none'
          })
        }
      })
    },

    /**
     * 执行登录请求
     */
    performLogin(code) {
      wxLogin(code)
        .then((response) => {
          // 保存登录信息
          saveLoginInfo(response)
          // 跳转到首页
          uni.reLaunch({
            url: '/pages/income/index'
          })
        })
        .catch((error) => {
          uni.showToast({
            title: error.message || '登录失败，请重试',
            icon: 'none'
          })
        })
        .finally(() => {
          this.loading = false
        })
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 100vh;
  padding: 40rpx 20rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 60rpx;
}

.app-logo {
  font-size: 80rpx;
  margin-bottom: 20rpx;
}

.app-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 10rpx;
}

.app-subtitle {
  font-size: 14rpx;
  color: rgba(255, 255, 255, 0.7);
}

.login-form {
  margin-top: 80rpx;
}

.form-item {
  margin-bottom: 20rpx;
}

.form-input {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 8rpx;
  padding: 16rpx 20rpx;
  font-size: 14rpx;
  color: #333;
}

.placeholder-text {
  color: #bbb;
}

.login-button-group {
  margin-bottom: 40rpx;
}

.wx-login-btn {
  background: #fff;
  color: #667eea;
  border: none;
  border-radius: 8rpx;
  padding: 16rpx 0;
  font-size: 16rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
}

.wx-login-btn:disabled {
  opacity: 0.7;
}

.login-tip {
  text-align: center;
  font-size: 12rpx;
  color: rgba(255, 255, 255, 0.6);
}

.login-footer {
  padding-bottom: 40rpx;
}

.footer-text {
  text-align: center;
  color: #fff;
  font-size: 14rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.footer-desc {
  text-align: center;
  color: rgba(255, 255, 255, 0.7);
  font-size: 12rpx;
  line-height: 18rpx;
}
</style>
