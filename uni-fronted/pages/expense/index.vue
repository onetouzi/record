<template>
  <view class="expense-page">
    <!-- 顶部统计卡片 -->
    <view class="statistics-card">
      <view class="card-header">
        <view class="month">{{ currentMonth }}</view>
        <view class="date-selector" @click="selectMonth">
          📅
        </view>
      </view>

      <view class="statistics-content">
        <view class="stat-item">
          <view class="stat-label">本月支出</view>
          <view class="stat-value">{{ formatCurrency(monthlyExpense) }}</view>
        </view>
        <view class="divider"></view>
        <view class="stat-item">
          <view class="stat-label">日均支出</view>
          <view class="stat-value">{{ formatCurrency(averageDailyExpense) }}</view>
        </view>
      </view>

      <!-- 支出原因分布 -->
      <view class="expense-categories">
        <view
          v-for="(amount, reason) in expenseReasons"
          :key="reason"
          class="reason-badge"
        >
          <view class="reason-name">{{ truncateText(reason, 10) }}</view>
          <view class="reason-amount">{{ formatCurrency(amount) }}</view>
        </view>
      </view>
    </view>

    <!-- 记支出按钮 -->
    <view class="quick-action">
      <button class="add-record-btn" @click="goToAddExpense">
        ➕ 新增支出记录
      </button>
    </view>

    <!-- 支出记录列表 -->
    <view class="records-section">
      <view class="section-header">
        <view class="section-title">最近支出</view>
        <view class="section-count">共{{ recordCount }}条</view>
      </view>

      <!-- 加载中 -->
      <view v-if="loading" class="loading">
        <text>加载中...</text>
      </view>

      <!-- 记录列表 -->
      <view v-else-if="expenseRecords.length > 0" class="records-list">
        <view
          v-for="record in expenseRecords"
          :key="record.id"
          class="record-item"
        >
          <view class="record-header">
            <view class="record-title">
              <view class="reason-tag">{{ record.reason }}</view>
            </view>
            <view class="record-amount">-{{ formatCurrency(record.amount) }}</view>
          </view>

          <view class="record-details">
            <view class="detail-item">
              <view class="detail-label">支出日期</view>
              <view class="detail-value">{{ record.expenseDate }}</view>
            </view>
            <view class="detail-item">
              <view class="detail-label">原因</view>
              <view class="detail-value">{{ record.reason }}</view>
            </view>
          </view>

          <view v-if="record.remark" class="record-remark">
            📝 {{ record.remark }}
          </view>
        </view>
      </view>

      <!-- 无数据提示 -->
      <view v-else class="empty-state">
        <view class="empty-icon">💰</view>
        <view class="empty-text">还没有支出记录</view>
        <view class="empty-desc">点击下方按钮添加您的第一笔支出</view>
      </view>
    </view>

    <!-- 加载更多 -->
    <view v-if="hasMore && !loading" class="load-more">
      <button @click="loadMore" class="load-more-btn">加载更多</button>
    </view>
  </view>
</template>

<script>
import { getExpenseRecordList, getExpenseStatistics } from '../../api/expense'
import { formatCurrency, truncateText } from '../../utils/format'
import { formatDate, getFirstDayOfMonth, getLastDayOfMonth } from '../../utils/dateUtil'

export default {
  data() {
    return {
      expenseRecords: [],
      monthlyExpense: 0,
      averageDailyExpense: 0,
      expenseReasons: {},
      loading: true,
      currentPage: 1,
      hasMore: true,
      currentMonth: ''
    }
  },

  computed: {
    recordCount() {
      return this.expenseRecords.length
    }
  },

  methods: {
    /**
     * 初始化页面
     */
    initPage() {
      this.currentMonth = formatDate(new Date(), 'YYYY年MM月')
      this.loadStatistics()
      this.loadRecords()
    },

    /**
     * 加载统计数据
     */
    loadStatistics() {
      const today = formatDate(new Date())
      getExpenseStatistics('month', today)
        .then((response) => {
          this.monthlyExpense = response.totalExpense || 0
          this.expenseReasons = response.expenseByReason || {}
          
          // 计算日均支出
          const daysInMonth = new Date(new Date().getFullYear(), new Date().getMonth() + 1, 0).getDate()
          this.averageDailyExpense = this.monthlyExpense / daysInMonth
        })
        .catch((error) => {
          console.error('加载统计数据失败:', error)
        })
    },

    /**
     * 加载支出记录列表
     */
    loadRecords() {
      this.loading = true
      this.currentPage = 1

      const startDate = getFirstDayOfMonth()
      const endDate = getLastDayOfMonth()

      getExpenseRecordList(this.currentPage, 10, startDate, endDate)
        .then((response) => {
          this.expenseRecords = response.list || []
          this.hasMore = this.expenseRecords.length >= 10
        })
        .catch((error) => {
          uni.showToast({
            title: error.message || '加载失败',
            icon: 'none'
          })
        })
        .finally(() => {
          this.loading = false
        })
    },

    /**
     * 加载更多记录
     */
    loadMore() {
      this.currentPage++
      const startDate = getFirstDayOfMonth()
      const endDate = getLastDayOfMonth()

      getExpenseRecordList(this.currentPage, 10, startDate, endDate)
        .then((response) => {
          const newRecords = response.list || []
          this.expenseRecords = this.expenseRecords.concat(newRecords)
          this.hasMore = newRecords.length >= 10
        })
        .catch((error) => {
          uni.showToast({
            title: error.message || '加载失败',
            icon: 'none'
          })
        })
    },

    /**
     * 格式化货币
     */
    formatCurrency(amount) {
      return formatCurrency(amount)
    },

    /**
     * 截断文本
     */
    truncateText(text, length) {
      return truncateText(text, length)
    },

    /**
     * 跳转到添加支出页面
     */
    goToAddExpense() {
      uni.navigateTo({
        url: '/pages/expense/add'
      })
    },

    /**
     * 选择月份
     */
    selectMonth() {
      console.log('选择月份')
    }
  },

  onLoad() {
    this.initPage()
  },

  onShow() {
    // 每次显示时刷新数据
    this.loadRecords()
    this.loadStatistics()
  }
}
</script>

<style scoped>
.expense-page {
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 20rpx;
}

/* 统计卡片 */
.statistics-card {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  margin: 10rpx 10rpx 20rpx;
  padding: 20rpx;
  border-radius: 12rpx;
  color: #fff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.month {
  font-size: 18rpx;
  font-weight: bold;
}

.date-selector {
  font-size: 24rpx;
  cursor: pointer;
}

.statistics-content {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid rgba(255, 255, 255, 0.2);
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-label {
  font-size: 12rpx;
  opacity: 0.8;
  margin-bottom: 8rpx;
}

.stat-value {
  font-size: 24rpx;
  font-weight: bold;
}

.divider {
  width: 1rpx;
  background: rgba(255, 255, 255, 0.2);
}

.expense-categories {
  display: flex;
  gap: 10rpx;
  flex-wrap: wrap;
}

.reason-badge {
  padding: 8rpx 12rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 6rpx;
  text-align: center;
  max-width: 45%;
}

.reason-name {
  font-size: 11rpx;
  margin-bottom: 2rpx;
  opacity: 0.8;
}

.reason-amount {
  font-size: 12rpx;
  font-weight: bold;
}

/* 快速操作 */
.quick-action {
  margin: 0 10rpx 20rpx;
}

.add-record-btn {
  background: #f5576c;
  color: #fff;
  border: none;
  border-radius: 8rpx;
  padding: 14rpx 0;
  font-size: 16rpx;
  font-weight: bold;
}

/* 记录部分 */
.records-section {
  background: #fff;
  margin: 10rpx;
  border-radius: 12rpx;
  padding: 16rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
  padding-bottom: 12rpx;
  border-bottom: 1rpx solid #eee;
}

.section-title {
  font-size: 16rpx;
  font-weight: bold;
  color: #333;
}

.section-count {
  font-size: 12rpx;
  color: #999;
}

.loading {
  text-align: center;
  padding: 40rpx 20rpx;
  color: #999;
}

.records-list {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.record-item {
  background: #f9f9f9;
  padding: 12rpx;
  border-radius: 8rpx;
  border-left: 4rpx solid #f5576c;
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.record-title {
  display: flex;
  align-items: center;
  gap: 8rpx;
  flex: 1;
}

.reason-tag {
  padding: 4rpx 8rpx;
  background: #f5576c;
  color: #fff;
  border-radius: 4rpx;
  font-size: 11rpx;
  max-width: 60rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.record-amount {
  font-size: 16rpx;
  font-weight: bold;
  color: #f5576c;
}

.record-details {
  display: flex;
  justify-content: space-between;
  font-size: 12rpx;
  margin-bottom: 8rpx;
}

.detail-item {
  flex: 1;
}

.detail-label {
  color: #999;
  margin-bottom: 4rpx;
}

.detail-value {
  color: #333;
  font-weight: bold;
}

.record-remark {
  font-size: 12rpx;
  color: #666;
  padding-top: 8rpx;
  border-top: 1rpx dashed #eee;
}

.empty-state {
  text-align: center;
  padding: 60rpx 20rpx;
}

.empty-icon {
  font-size: 80rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 16rpx;
  color: #333;
  margin-bottom: 10rpx;
}

.empty-desc {
  font-size: 12rpx;
  color: #999;
}

.load-more {
  text-align: center;
  padding: 20rpx;
}

.load-more-btn {
  background: #f5576c;
  color: #fff;
  border: none;
  border-radius: 8rpx;
  padding: 10rpx 30rpx;
  font-size: 14rpx;
}
</style>
