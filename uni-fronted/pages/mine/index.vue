<template>
  <view class="mine-page">
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <view class="user-header">
        <view class="user-avatar">👤</view>
        <view class="user-info">
          <view class="user-name">{{ userInfo.nickname || '用户昵称' }}</view>
          <view class="user-id">ID: {{ userInfo.id }}</view>
        </view>
      </view>
      <view class="user-phone" v-if="userInfo.phone">
        📱 {{ userInfo.phone }}
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
      <view class="menu-title">功能菜单</view>
      <view class="menu-list">
        <view class="menu-item" @click="goToPage('/pages/income/index')">
          <view class="menu-icon">📊</view>
          <view class="menu-name">收入管理</view>
          <view class="menu-arrow">→</view>
        </view>
        <view class="menu-item" @click="goToPage('/pages/expense/index')">
          <view class="menu-icon">💳</view>
          <view class="menu-name">支出管理</view>
          <view class="menu-arrow">→</view>
        </view>
        <view class="menu-item" @click="goToPage('/pages/statistics/index')">
          <view class="menu-icon">📈</view>
          <view class="menu-name">数据统计</view>
          <view class="menu-arrow">→</view>
        </view>
      </view>
    </view>

    <!-- 设置菜单 -->
    <view class="menu-section">
      <view class="menu-title">设置</view>
      <view class="menu-list">
        <view class="menu-item" @click="showAbout">
          <view class="menu-icon">ℹ️</view>
          <view class="menu-name">关于应用</view>
          <view class="menu-arrow">→</view>
        </view>
        <view class="menu-item" @click="logout">
          <view class="menu-icon">🚪</view>
          <view class="menu-name">退出登录</view>
          <view class="menu-arrow">→</view>
        </view>
      </view>
    </view>

    <!-- 关于应用 -->
    <view class="about-section">
      <view class="about-title">零工记账</view>
      <view class="about-version">版本 1.0.0</view>
      <view class="about-desc">
        为打零工人员量身定制的记账小程序
      </view>
      <view class="about-copyright">
        © 2024 零工记账 All Rights Reserved
      </view>
    </view>
  </view>
</template>

<script>
import { getLocalUserInfo, clearLoginInfo } from '../../api/user'
import { formatCurrency } from '../../utils/format'

export default {
  data() {
    return {
      userInfo: {},
    }
  },

  methods: {
    /**
     * 初始化页面
     */
    initPage() {
      this.loadUserInfo()
    },

    /**
     * 加载用户信息
     */
    loadUserInfo() {
      const userInfo = getLocalUserInfo()
      if (userInfo) {
        this.userInfo = userInfo
      }
    },


    /**
     * 格式化货币
     */
    formatCurrency(amount) {
      return formatCurrency(amount)
    },

    /**
     * 跳转页面
     */
    goToPage(url) {
      uni.switchTab({
        url: url
      })
    },

    /**
     * 显示关于应用
     */
    showAbout() {
      uni.showModal({
        title: '关于应用',
        content: '零工记账 v1.0.0\n\n为打零工人员量身定制的记账小程序，帮助工人轻松记录收入和日常支出。',
        showCancel: false,
        confirmText: '确定'
      })
    },

    /**
     * 退出登录
     */
    logout() {
      uni.showModal({
        title: '确认退出',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            clearLoginInfo()
            uni.reLaunch({
              url: '/pages/login/index'
            })
          }
        }
      })
    }
  },

  onLoad() {
    this.initPage()
  },

  onShow() {
    // 每次显示时刷新信息
    this.loadUserInfo()
  }
}
</script>

<style scoped>
.mine-page {
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 20rpx;
}

/* 用户卡片 */
.user-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin: 10rpx;
  padding: 20rpx;
  border-radius: 12rpx;
  color: #fff;
}

.user-header {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 16rpx;
  padding-bottom: 16rpx;
  border-bottom: 1rpx solid rgba(255, 255, 255, 0.2);
}

.user-avatar {
  font-size: 48rpx;
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 16rpx;
  font-weight: bold;
  margin-bottom: 6rpx;
}

.user-id {
  font-size: 12rpx;
  opacity: 0.8;
}

.user-phone {
  font-size: 12rpx;
  opacity: 0.8;
}


.stat-box {
  flex: 1;
  background: #fff;
  padding: 16rpx;
  border-radius: 12rpx;
  text-align: center;
  box-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
}

.stat-icon {
  font-size: 28rpx;
  margin-bottom: 8rpx;
}

.stat-text {
  font-size: 12rpx;
  color: #999;
  margin-bottom: 8rpx;
}

.stat-value {
  font-size: 16rpx;
  color: #667eea;
  font-weight: bold;
}

.stat-value.negative {
  color: #f5576c;
}

/* 菜单部分 */
.menu-section {
  margin: 10rpx;
  background: #fff;
  border-radius: 12rpx;
  overflow: hidden;
  margin-bottom: 15rpx;
}

.menu-title {
  font-size: 14rpx;
  font-weight: bold;
  color: #333;
  padding: 16rpx 16rpx 10rpx;
  background: #f9f9f9;
  border-bottom: 1rpx solid #eee;
}

.menu-list {
  display: flex;
  flex-direction: column;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 16rpx;
  border-bottom: 1rpx solid #eee;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon {
  font-size: 20rpx;
  min-width: 30rpx;
}

.menu-name {
  flex: 1;
  font-size: 14rpx;
  color: #333;
}

.menu-arrow {
  font-size: 14rpx;
  color: #ccc;
}

/* 关于部分 */
.about-section {
  text-align: center;
  padding: 40rpx 20rpx;
  background: #fff;
  margin: 10rpx;
  border-radius: 12rpx;
}

.about-title {
  font-size: 18rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 8rpx;
}

.about-version {
  font-size: 13rpx;
  color: #999;
  margin-bottom: 12rpx;
}

.about-desc {
  font-size: 12rpx;
  color: #666;
  margin-bottom: 20rpx;
  line-height: 20rpx;
}

.about-copyright {
  font-size: 11rpx;
  color: #ccc;
  padding-top: 12rpx;
  border-top: 1rpx solid #eee;
}
</style>
