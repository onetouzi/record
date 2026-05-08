<template>
  <view class="income-page">
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
          <view class="stat-label">本月收入</view>
          <view class="stat-value">{{ formatCurrency(monthlyIncome) }}</view>
        </view>
        <view class="divider"></view>
        <view class="stat-item">
          <view class="stat-label">工作天数</view>
          <view class="stat-value">{{ workDays }}天</view>
        </view>
      </view>

      <!-- 收入类型分布 -->
      <view class="income-types">
        <view
          v-for="(amount, type) in incomeTypes"
          :key="type"
          class="type-badge"
          :style="{ background: getIncomeTypeColor(parseInt(type)) }"
        >
          <view class="type-name">{{ incomeTypeToText(parseInt(type)) }}</view>
          <view class="type-amount">{{ formatCurrency(amount) }}</view>
        </view>
      </view>
    </view>

    <!-- 记工按钮 -->
    <view class="quick-action">
      <button class="add-record-btn" @click="goToAddIncome">
        ➕ 新增收入记录
      </button>
    </view>

    <!-- 收入记录列表 -->
    <view class="records-section">
      <view class="section-header">
        <view class="section-title">最近收入</view>
        <view class="section-count">共{{ recordCount }}条</view>
      </view>

      <!-- 加载中 -->
      <view v-if="loading" class="loading">
        <text>加载中...</text>
      </view>

      <!-- 记录列表 -->
      <view v-else-if="incomeRecords.length > 0" class="records-list">
        <view
          v-for="record in incomeRecords"
          :key="record.id"
          class="record-item"
        >
          <view class="record-header">
            <view class="record-title">
              <view class="income-type-tag" :style="{ background: getIncomeTypeColor(record.incomeType) }">
                {{ incomeTypeToText(record.incomeType) }}
              </view>
              <view class="record-name">{{ truncateText(record.serviceObject || '无名客户', 15) }}</view>
            </view>
            <view class="record-amount">{{ formatCurrency(record.totalAmount) }}</view>
          </view>

          <view class="record-details">
            <view class="detail-item">
              <view class="detail-label">工作日期</view>
              <view class="detail-value">{{ record.workDate }}</view>
            </view>
            <view class="detail-item">
              <view class="detail-label">{{ getIncomeUnit(record.incomeType) }}</view>
              <view class="detail-value">{{ getRecordValue(record) }}</view>
            </view>
            <view class="detail-item">
              <view class="detail-label">单价</view>
              <view class="detail-value">{{ formatCurrency(record.unitPrice) }}/{{ getIncomeUnit(record.incomeType) }}</view>
            </view>
          </view>

          <!-- 加班信息（仅记时类型且有加班时显示） -->
          <view v-if="record.incomeType === 1 && record.overtimeHours > 0" class="overtime-info">
            <view class="overtime-tag">🌙 加班</view>
            <view class="overtime-detail">
              加班 {{ record.overtimeHours }}小时 × ¥{{ formatCurrency(record.overtimeUnitPrice) }}/{{ getIncomeUnit(record.incomeType) }} = ¥{{ formatCurrency(record.overtimeHours * record.overtimeUnitPrice) }}
            </view>
          </view>

          <view v-if="record.remark" class="record-remark">
            📝 {{ record.remark }}
          </view>
        </view>
      </view>

      <!-- 无数据提示 -->
      <view v-else class="empty-state">
        <view class="empty-icon">📭</view>
        <view class="empty-text">还没有收入记录</view>
        <view class="empty-desc">点击下方按钮添加您的第一笔收入</view>
      </view>
    </view>

    <!-- 加载更多 -->
    <view v-if="hasMore && !loading" class="load-more">
      <button @click="loadMore" class="load-more-btn">加载更多</button>
    </view>
  </view>
</template>

<script>
import { getIncomeRecordList, getIncomeStatistics } from '../../api/income'
import { formatCurrency, incomeTypeToText, getIncomeTypeColor, getIncomeUnit, truncateText } from '../../utils/format'
import { formatDate, getFirstDayOfMonth, getLastDayOfMonth } from '../../utils/dateUtil'

export default {
  data() {
    return {
      incomeRecords: [],
      monthlyIncome: 0,
      workDays: 0,
      incomeTypes: {},
      loading: true,
      currentPage: 1,
      hasMore: true,
      currentMonth: ''
    }
  },

  computed: {
    recordCount() {
      return this.incomeRecords.length
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
      getIncomeStatistics('month', today)
        .then((response) => {
          this.monthlyIncome = response.totalIncome || 0
          this.incomeTypes = response.incomeTypes || {}
          // 计算工作天数（简单估计为记录条数）
          this.workDays = Object.keys(response.incomeTypes).length
        })
        .catch((error) => {
          console.error('加载统计数据失败:', error)
        })
    },

    /**
     * 加载收入记录列表
     */
    loadRecords() {
      this.loading = true
      this.currentPage = 1
      
      const startDate = getFirstDayOfMonth()
      const endDate = getLastDayOfMonth()

      getIncomeRecordList(this.currentPage, 10, startDate, endDate)
        .then((response) => {
          this.incomeRecords = response.list || []
          this.hasMore = this.incomeRecords.length >= 10
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

      getIncomeRecordList(this.currentPage, 10, startDate, endDate)
        .then((response) => {
          const newRecords = response.list || []
          this.incomeRecords = this.incomeRecords.concat(newRecords)
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
     * 获取记录值（根据收入类型返回不同的字段）
     */
    getRecordValue(record) {
      switch (record.incomeType) {
        case 1:
          let value = record.workHours || 0
          if (record.overtimeHours > 0) {
            value += '+' + record.overtimeHours + '加班'
          }
          return value
        case 2:
          return record.quantity || 0
        case 3:
          return record.area || 0
        default:
          return 0
      }
    },

    /**
     * 格式化货币
     */
    formatCurrency(amount) {
      return formatCurrency(amount)
    },

    /**
     * 收入类型转文本
     */
    incomeTypeToText(type) {
      return incomeTypeToText(type)
    },

    /**
     * 获取收入类型颜色
     */
    getIncomeTypeColor(type) {
      return getIncomeTypeColor(type)
    },

    /**
     * 获取收入单位
     */
    getIncomeUnit(type) {
      return getIncomeUnit(type)
    },

    /**
     * 截断文本
     */
    truncateText(text, length) {
      return truncateText(text, length)
    },

    /**
     * 跳转到添加收入页面
     */
    goToAddIncome() {
      uni.navigateTo({
        url: '/pages/income/add'
      })
    },

    /**
     * 选择月份
     */
    selectMonth() {
      // 可以实现月份选择器
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
.income-page {
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 20rpx;
}

/* 统计卡片 */
.statistics-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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

.income-types {
  display: flex;
  gap: 10rpx;
}

.type-badge {
  flex: 1;
  padding: 12rpx;
  border-radius: 8rpx;
  text-align: center;
}

.type-name {
  font-size: 12rpx;
  margin-bottom: 4rpx;
}

.type-amount {
  font-size: 14rpx;
  font-weight: bold;
}

/* 快速操作 */
.quick-action {
  margin: 0 10rpx 20rpx;
}

.add-record-btn {
  background: #667eea;
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
  border-left: 4rpx solid #667eea;
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

.income-type-tag {
  padding: 4rpx 8rpx;
  border-radius: 4rpx;
  color: #fff;
  font-size: 11rpx;
  min-width: 40rpx;
  text-align: center;
}

.record-name {
  font-size: 14rpx;
  color: #333;
}

.record-amount {
  font-size: 16rpx;
  font-weight: bold;
  color: #667eea;
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

/* 加班信息 */
.overtime-info {
  background: #fff3e0;
  padding: 8rpx 12rpx;
  border-radius: 6rpx;
  margin-bottom: 8rpx;
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.overtime-tag {
  font-size: 12rpx;
  color: #ffa726;
  font-weight: bold;
  white-space: nowrap;
}

.overtime-detail {
  font-size: 11rpx;
  color: #666;
  flex: 1;
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
  background: #667eea;
  color: #fff;
  border: none;
  border-radius: 8rpx;
  padding: 10rpx 30rpx;
  font-size: 14rpx;
}
</style>