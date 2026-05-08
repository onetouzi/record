<template>
  <view class="statistics-page">
    <!-- 时间段选择 -->
    <view class="time-selector">
      <view
        v-for="type in timeTypes"
        :key="type"
        class="time-option"
        :class="{ active: selectedTimeType === type }"
        @click="selectTimeType(type)"
      >
        {{ getTimeTypeName(type) }}
      </view>
    </view>

    <!-- 总体统计 -->
    <view class="summary-card">
      <view class="summary-item">
        <view class="summary-label">总收入</view>
        <view class="summary-value income">{{ formatCurrency(totalIncome) }}</view>
      </view>
      <view class="summary-divider"></view>
      <view class="summary-item">
        <view class="summary-label">总支出</view>
        <view class="summary-value expense">{{ formatCurrency(totalExpense) }}</view>
      </view>
      <view class="summary-divider"></view>
      <view class="summary-item">
        <view class="summary-label">净收益</view>
        <view class="summary-value profit" :class="{ negative: netProfit < 0 }">
          {{ formatCurrency(netProfit) }}
        </view>
      </view>
    </view>

    <!-- 收入类型分析 -->
    <view class="analysis-card">
      <view class="card-title">收入类型分析</view>
      <view v-if="Object.keys(incomeBreakdown).length > 0" class="breakdown-list">
        <view
          v-for="(amount, type) in incomeBreakdown"
          :key="type"
          class="breakdown-item"
        >
          <view class="breakdown-label">
            <view class="label-name">{{ incomeTypeToText(parseInt(type)) }}</view>
            <view class="label-percent">
              {{ formatPercent(amount, totalIncome) }}
            </view>
          </view>
          <view class="breakdown-bar-container">
            <view
              class="breakdown-bar"
              :style="{
                width: formatPercent(amount, totalIncome),
                background: getIncomeTypeColor(parseInt(type))
              }"
            ></view>
          </view>
          <view class="breakdown-amount">{{ formatCurrency(amount) }}</view>
        </view>
      </view>
      <view v-else class="empty-message">
        暂无收入数据
      </view>
    </view>

    <!-- 支出原因分析 -->
    <view class="analysis-card">
      <view class="card-title">支出原因分析</view>
      <view v-if="Object.keys(expenseBreakdown).length > 0" class="breakdown-list">
        <view
          v-for="(amount, reason) in expenseBreakdown"
          :key="reason"
          class="breakdown-item"
        >
          <view class="breakdown-label">
            <view class="label-name">{{ truncateText(reason, 12) }}</view>
            <view class="label-percent">
              {{ formatPercent(amount, totalExpense) }}
            </view>
          </view>
          <view class="breakdown-bar-container">
            <view
              class="breakdown-bar"
              :style="{
                width: formatPercent(amount, totalExpense),
                background: '#f5576c'
              }"
            ></view>
          </view>
          <view class="breakdown-amount">{{ formatCurrency(amount) }}</view>
        </view>
      </view>
      <view v-else class="empty-message">
        暂无支出数据
      </view>
    </view>

    <!-- 日均统计 -->
    <view class="analysis-card">
      <view class="card-title">日均统计</view>
      <view class="daily-stats">
        <view class="stat-row">
          <view class="stat-label">平均日收入</view>
          <view class="stat-value">{{ formatCurrency(averageDailyIncome) }}</view>
        </view>
        <view class="stat-row">
          <view class="stat-label">平均日支出</view>
          <view class="stat-value">{{ formatCurrency(averageDailyExpense) }}</view>
        </view>
        <view class="stat-row">
          <view class="stat-label">平均日净收益</view>
          <view class="stat-value" :class="{ negative: averageNetProfit < 0 }">
            {{ formatCurrency(averageNetProfit) }}
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getIncomeStatistics } from '../../api/income'
import { getExpenseStatistics } from '../../api/expense'
import {
  formatCurrency,
  incomeTypeToText,
  getIncomeTypeColor,
  formatPercent,
  truncateText
} from '../../utils/format'
import { getToday } from '../../utils/dateUtil'

export default {
  data() {
    return {
      timeTypes: ['day', 'week', 'month'],
      selectedTimeType: 'month',
      totalIncome: 0,
      totalExpense: 0,
      incomeBreakdown: {},
      expenseBreakdown: {},
      averageDailyIncome: 0,
      averageDailyExpense: 0,
      loading: false
    }
  },

  computed: {
    netProfit() {
      return this.totalIncome - this.totalExpense
    },

    averageNetProfit() {
      return this.averageDailyIncome - this.averageDailyExpense
    }
  },

  methods: {
    /**
     * 选择时间段
     */
    selectTimeType(type) {
      this.selectedTimeType = type
      this.loadStatistics()
    },

    /**
     * 获取时间段名称
     */
    getTimeTypeName(type) {
      const names = {
        day: '按天',
        week: '按周',
        month: '按月'
      }
      return names[type] || type
    },

    /**
     * 加载统计数据
     */
    loadStatistics() {
      this.loading = true
      const today = getToday()

      Promise.all([
        getIncomeStatistics(this.selectedTimeType, today),
        getExpenseStatistics(this.selectedTimeType, today)
      ])
        .then(([incomeData, expenseData]) => {
          this.totalIncome = incomeData.totalIncome || 0
          this.incomeBreakdown = incomeData.incomeTypes || {}
          
          this.totalExpense = expenseData.totalExpense || 0
          this.expenseBreakdown = expenseData.expenseByReason || {}

          // 计算日均
          const daysCount = this.calculateDaysCount()
          this.averageDailyIncome = daysCount > 0 ? this.totalIncome / daysCount : 0
          this.averageDailyExpense = daysCount > 0 ? this.totalExpense / daysCount : 0
        })
        .catch((error) => {
          uni.showToast({
            title: error.message || '加载统计数据失败',
            icon: 'none'
          })
        })
        .finally(() => {
          this.loading = false
        })
    },

    /**
     * 计算天数
     */
    calculateDaysCount() {
      const today = new Date()
      
      switch (this.selectedTimeType) {
        case 'day':
          return 1
        case 'week':
          return 7
        case 'month':
          return new Date(today.getFullYear(), today.getMonth() + 1, 0).getDate()
        default:
          return 1
      }
    },

    /**
     * 格式化货币
     */
    formatCurrency(amount) {
      return formatCurrency(amount)
    },

    /**
     * 格式化百分比
     */
    formatPercent(value, total) {
      return formatPercent(value, total)
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
     * 截断文本
     */
    truncateText(text, length) {
      return truncateText(text, length)
    }
  },

  onLoad() {
    this.loadStatistics()
  }
}
</script>

<style scoped>
.statistics-page {
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 20rpx;
}

/* 时间选择器 */
.time-selector {
  display: flex;
  gap: 10rpx;
  padding: 15rpx 10rpx;
  background: #fff;
  border-bottom: 1rpx solid #eee;
}

.time-option {
  flex: 1;
  text-align: center;
  padding: 10rpx;
  border: 1rpx solid #ddd;
  border-radius: 6rpx;
  background: #f9f9f9;
  font-size: 13rpx;
  color: #666;
  transition: all 0.3s;
}

.time-option.active {
  border-color: #667eea;
  background: #f0f1ff;
  color: #667eea;
  font-weight: bold;
}

/* 总体统计 */
.summary-card {
  display: flex;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin: 10rpx;
  padding: 20rpx;
  border-radius: 12rpx;
  color: #fff;
}

.summary-item {
  flex: 1;
  text-align: center;
}

.summary-label {
  font-size: 12rpx;
  opacity: 0.8;
  margin-bottom: 8rpx;
}

.summary-value {
  font-size: 20rpx;
  font-weight: bold;
}

.summary-value.income {
  color: #90EE90;
}

.summary-value.expense {
  color: #FFB6C6;
}

.summary-value.profit {
  color: #FFD700;
}

.summary-value.negative {
  color: #FF6B6B;
}

.summary-divider {
  width: 1rpx;
  background: rgba(255, 255, 255, 0.2);
}

/* 分析卡片 */
.analysis-card {
  background: #fff;
  margin: 10rpx;
  padding: 16rpx;
  border-radius: 12rpx;
  margin-bottom: 15rpx;
}

.card-title {
  font-size: 16rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 16rpx;
  padding-bottom: 10rpx;
  border-bottom: 2rpx solid #eee;
}

.breakdown-list {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.breakdown-item {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.breakdown-label {
  width: 60rpx;
  display: flex;
  flex-direction: column;
  gap: 2rpx;
}

.label-name {
  font-size: 12rpx;
  color: #333;
  font-weight: bold;
}

.label-percent {
  font-size: 11rpx;
  color: #999;
}

.breakdown-bar-container {
  flex: 1;
  height: 20rpx;
  background: #f0f0f0;
  border-radius: 10rpx;
  overflow: hidden;
}

.breakdown-bar {
  height: 100%;
  border-radius: 10rpx;
  min-width: 20rpx;
}

.breakdown-amount {
  width: 60rpx;
  text-align: right;
  font-size: 12rpx;
  color: #333;
  font-weight: bold;
}

.empty-message {
  text-align: center;
  padding: 30rpx 20rpx;
  color: #999;
  font-size: 13rpx;
}

/* 日均统计 */
.daily-stats {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.stat-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12rpx;
  background: #f9f9f9;
  border-radius: 6rpx;
}

.stat-label {
  font-size: 13rpx;
  color: #666;
}

.stat-value {
  font-size: 14rpx;
  color: #333;
  font-weight: bold;
}

.stat-value.negative {
  color: #f5576c;
}
</style>
