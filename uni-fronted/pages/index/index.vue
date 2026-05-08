<template>
  <view class="home-page">
    <!-- 顶部统计卡片 -->
    <view class="header-card">
      <view class="month-selector">
        <text class="arrow" @click="changeMonth(-1)">◀</text>
        <text class="month-text">{{ currentMonth }}</text>
        <text class="arrow" @click="changeMonth(1)">▶</text>
      </view>
      <view class="summary">
        <view class="summary-item income">
          <text class="label">本月收入</text>
          <text class="value">¥{{ monthlyIncome.toFixed(2) }}</text>
        </view>
        <view class="divider-vertical"></view>
        <view class="summary-item expense">
          <text class="label">本月支出</text>
          <text class="value">¥{{ monthlyExpense.toFixed(2) }}</text>
        </view>
        <view class="divider-vertical"></view>
        <view class="summary-item balance">
          <text class="label">本月结余</text>
          <text class="value">¥{{ monthlyBalance.toFixed(2) }}</text>
        </view>
      </view>
    </view>

    <!-- 日历组件 -->
    <view class="calendar-container">
      <!-- 星期标题 -->
      <view class="weekdays">
        <view v-for="day in weekdays" :key="day" class="weekday">{{ day }}</view>
      </view>
      
      <!-- 日历网格 -->
      <view class="calendar-grid">
        <!-- 空白占位 -->
        <view v-for="n in emptyDays" :key="'empty-' + n" class="day-cell empty"></view>
        
        <!-- 日期格子 -->
        <view 
          v-for="day in calendarDays" 
          :key="day.date" 
          class="day-cell"
          :class="{ 
            'today': day.isToday, 
            'has-data': day.hasData,
            'selected': selectedDate === day.date 
          }"
          @click="selectDay(day)"
        >
          <view class="day-number">{{ day.day }}</view>
          <view v-if="day.hasData" class="day-data">
            <view class="day-income" v-if="day.income > 0">
              +{{ day.income.toFixed(0) }}
            </view>
            <view class="day-expense" v-if="day.expense > 0">
              -{{ day.expense.toFixed(0) }}
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 选中日期详情 -->
    <view v-if="selectedDayData" class="day-detail-card">
      <view class="detail-header">
        <text class="detail-date">{{ selectedDate }}</text>
        <text class="detail-close" @click="closeDetail">✕</text>
      </view>
      
      <view class="detail-summary">
        <view class="detail-item">
          <text class="detail-label">收入</text>
          <text class="detail-value income">¥{{ selectedDayData.income.toFixed(2) }}</text>
        </view>
        <view class="detail-item">
          <text class="detail-label">支出</text>
          <text class="detail-value expense">¥{{ selectedDayData.expense.toFixed(2) }}</text>
        </view>
        <view class="detail-item">
          <text class="detail-label">结余</text>
          <text class="detail-value" :class="selectedDayData.balance >= 0 ? 'income' : 'expense'">
            ¥{{ selectedDayData.balance.toFixed(2) }}
          </text>
        </view>
      </view>

      <!-- 收入记录列表 -->
      <view v-if="selectedDayData.incomes.length > 0" class="detail-section">
        <view class="section-title">收入记录 ({{ selectedDayData.incomes.length }}条)</view>
        <view v-for="income in selectedDayData.incomes" :key="income.id" class="record-item">
          <view class="record-left">
            <view class="record-type" :style="{ background: getIncomeTypeColor(income.incomeType) }">
              {{ incomeTypeToText(income.incomeType) }}
            </view>
            <view class="record-info">
              <text class="record-name">{{ income.serviceObject || '无' }}</text>
              <text class="record-desc">{{ getIncomeDesc(income) }}</text>
            </view>
          </view>
          <view class="record-amount income">+¥{{ income.totalAmount.toFixed(2) }}</view>
        </view>
      </view>

      <!-- 支出记录列表 -->
      <view v-if="selectedDayData.expenses.length > 0" class="detail-section">
        <view class="section-title">支出记录 ({{ selectedDayData.expenses.length }}条)</view>
        <view v-for="expense in selectedDayData.expenses" :key="expense.id" class="record-item">
          <view class="record-left">
            <view class="record-type expense">支出</view>
            <view class="record-info">
              <text class="record-name">{{ expense.reason }}</text>
              <text class="record-desc">{{ expense.remark || '' }}</text>
            </view>
          </view>
          <view class="record-amount expense">-¥{{ expense.amount.toFixed(2) }}</view>
        </view>
      </view>

      <!-- 无数据提示 -->
      <view v-if="selectedDayData.incomes.length === 0 && selectedDayData.expenses.length === 0" class="no-data">
        <text>这天还没有记录哦~</text>
      </view>
    </view>

    <!-- 快捷操作 -->
    <view class="quick-actions">
      <button class="action-btn income-btn" @click="goToAddIncome">
        <text class="btn-icon">➕</text>
        <text class="btn-text">记收入</text>
      </button>
      <button class="action-btn expense-btn" @click="goToAddExpense">
        <text class="btn-icon">💸</text>
        <text class="btn-text">记支出</text>
      </button>
    </view>
  </view>
</template>

<script>
import { getIncomeRecordList, getIncomeStatistics } from '../../api/income'
import { getExpenseRecordList, getExpenseStatistics } from '../../api/expense'
import { formatDate, getFirstDayOfMonth, getLastDayOfMonth, getDaysInMonth } from '../../utils/dateUtil'
import { formatCurrency, incomeTypeToText, getIncomeTypeColor } from '../../utils/format'

export default {
  data() {
    return {
      weekdays: ['日', '一', '二', '三', '四', '五', '六'],
      currentDate: new Date(),
      currentMonth: '',
      calendarDays: [],
      emptyDays: 0,
      
      // 统计数据
      monthlyIncome: 0,
      monthlyExpense: 0,
      monthlyBalance: 0,
      
      // 每日数据
      dailyData: {},
      
      // 选中日期
      selectedDate: '',
      selectedDayData: null,
      
      // 所有记录和
      allIncomes: [],
      allExpenses: []
    }
  },

  methods: {
    /**
     * 初始化页面
     */
    initPage() {
      this.updateMonthDisplay()
      this.generateCalendar()
      this.loadData()
    },

    /**
     * 更新月份显示
     */
    updateMonthDisplay() {
      const year = this.currentDate.getFullYear()
      const month = this.currentDate.getMonth() + 1
      this.currentMonth = `${year}年${month}月`
    },

    /**
     * 生成日历
     */
    generateCalendar() {
      const year = this.currentDate.getFullYear()
      const month = this.currentDate.getMonth()
      const firstDay = new Date(year, month, 1)
      const daysInMonth = getDaysInMonth(year, month + 1)
      
      // 计算月初空白天数
      this.emptyDays = firstDay.getDay()
      
      // 生成日期数组
      this.calendarDays = []
      const today = new Date()
      
      for (let i = 1; i <= daysInMonth; i++) {
        const date = new Date(year, month, i)
        const dateStr = formatDate(date)
        const isToday = date.toDateString() === today.toDateString()
        
        const dayData = {
          day: i,
          date: dateStr,
          isToday: isToday,
          hasData: false,
          income: 0,
          expense: 0
        }
        
        // 检查是否有数据
        if (this.dailyData[dateStr]) {
          dayData.hasData = true
          dayData.income = this.dailyData[dateStr].income || 0
          dayData.expense = this.dailyData[dateStr].expense || 0
        }
        
        this.calendarDays.push(dayData)
      }
    },

    /**
     * 加载数据
     */
    loadData() {
      const startDate = getFirstDayOfMonth(this.currentDate)
      const endDate = getLastDayOfMonth(this.currentDate)
      
      // 加载收入数据
      getIncomeRecordList(1, 100, startDate, endDate)
        .then((response) => {
          this.allIncomes = response.list || []
          this.processIncomeData()
        })
        .catch((error) => {
          console.error('加载收入数据失败:', error)
        })
      
      // 加载支出数据
      getExpenseRecordList(1, 100, startDate, endDate)
        .then((response) => {
          this.allExpenses = response.list || []
          this.processExpenseData()
        })
        .catch((error) => {
          console.error('加载支出数据失败:', error)
        })
      
      // 加载统计数据
      this.loadStatistics()
    },

    /**
     * 处理收入数据
     */
    processIncomeData() {
      this.dailyData = {}
      this.monthlyIncome = 0
      
      this.allIncomes.forEach(income => {
        const date = income.workDate
        if (!this.dailyData[date]) {
          this.dailyData[date] = { income: 0, expense: 0, incomes: [], expenses: [] }
        }
        this.dailyData[date].income += income.totalAmount
        this.dailyData[date].incomes.push(income)
        this.monthlyIncome += income.totalAmount
      })
      
      this.updateBalance()
      this.generateCalendar()
    },

    /**
     * 处理支出数据
     */
    processExpenseData() {
      this.monthlyExpense = 0
      
      this.allExpenses.forEach(expense => {
        const date = expense.expenseDate
        if (!this.dailyData[date]) {
          this.dailyData[date] = { income: 0, expense: 0, incomes: [], expenses: [] }
        }
        this.dailyData[date].expense += expense.amount
        this.dailyData[date].expenses.push(expense)
        this.monthlyExpense += expense.amount
      })
      
      this.updateBalance()
      this.generateCalendar()
    },

    /**
     * 更新结余
     */
    updateBalance() {
      this.monthlyBalance = this.monthlyIncome - this.monthlyExpense
    },

    /**
     * 加载统计数据
     */
    loadStatistics() {
      const today = formatDate(new Date())
      
      getIncomeStatistics('month', today)
        .then((response) => {
          // 这里可以使用返回的统计数据
        })
        .catch((error) => {
          console.error('加载统计数据失败:', error)
        })
    },

    /**
     * 切换月份
     */
    changeMonth(delta) {
      this.currentDate.setMonth(this.currentDate.getMonth() + delta)
      this.updateMonthDisplay()
      this.loadData()
    },

    /**
     * 选择日期
     */
    selectDay(day) {
      this.selectedDate = day.date
      
      if (this.dailyData[day.date]) {
        this.selectedDayData = this.dailyData[day.date]
        this.selectedDayData.balance = this.selectedDayData.income - this.selectedDayData.expense
      } else {
        this.selectedDayData = {
          income: 0,
          expense: 0,
          balance: 0,
          incomes: [],
          expenses: []
        }
      }
    },

    /**
     * 关闭详情
     */
    closeDetail() {
      this.selectedDayData = null
      this.selectedDate = ''
    },

    /**
     * 获取收入描述
     */
    getIncomeDesc(income) {
      const unitMap = { 1: '小时', 2: '件', 3: '平方' }
      const valueMap = { 1: income.workHours, 2: income.quantity, 3: income.area }
      const unit = unitMap[income.incomeType] || ''
      const value = valueMap[income.incomeType] || 0
      
      if (income.incomeType === 1 && income.overtimeHours > 0) {
        return `正常${value}${unit} + 加班${income.overtimeHours}小时`
      }
      return `${value}${unit}`
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
     * 格式化货币
     */
    formatCurrency(amount) {
      return formatCurrency(amount)
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
     * 跳转到添加支出页面
     */
    goToAddExpense() {
      uni.navigateTo({
        url: '/pages/expense/add'
      })
    }
  },

  onLoad() {
    this.initPage()
  },

  onShow() {
    // 每次显示时刷新数据
    this.loadData()
  }
}
</script>

<style scoped>
.home-page {
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 140rpx;
}

/* 顶部统计卡片 */
.header-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20rpx;
  color: #fff;
}

.month-selector {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20rpx;
}

.arrow {
  font-size: 24rpx;
  padding: 10rpx 20rpx;
  cursor: pointer;
}

.month-text {
  font-size: 32rpx;
  font-weight: bold;
  margin: 0 20rpx;
}

.summary {
  display: flex;
  justify-content: space-around;
  align-items: center;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12rpx;
  padding: 20rpx;
}

.summary-item {
  text-align: center;
  flex: 1;
}

.summary-item .label {
  font-size: 12rpx;
  opacity: 0.8;
  display: block;
  margin-bottom: 8rpx;
}

.summary-item .value {
  font-size: 28rpx;
  font-weight: bold;
}

.divider-vertical {
  width: 1rpx;
  height: 40rpx;
  background: rgba(255, 255, 255, 0.3);
}

/* 日历组件 */
.calendar-container {
  background: #fff;
  margin: 10rpx;
  border-radius: 12rpx;
  padding: 20rpx;
}

.weekdays {
  display: flex;
  margin-bottom: 16rpx;
}

.weekday {
  flex: 1;
  text-align: center;
  font-size: 12rpx;
  color: #999;
  font-weight: bold;
}

.calendar-grid {
  display: flex;
  flex-wrap: wrap;
}

.day-cell {
  width: calc(100% / 7);
  aspect-ratio: 1;
  border-radius: 8rpx;
  margin: 4rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  position: relative;
  cursor: pointer;
  transition: all 0.2s;
}

.day-cell.empty {
  background: transparent;
}

.day-cell.today {
  background: #667eea;
  color: #fff;
}

.day-cell.has-data {
  background: #e8f5e9;
}

.day-cell.selected {
  border: 3rpx solid #667eea;
  box-shadow: 0 0 10rpx rgba(102, 126, 234, 0.3);
}

.day-number {
  font-size: 24rpx;
  font-weight: bold;
}

.day-data {
  margin-top: 4rpx;
  font-size: 10rpx;
  display: flex;
  gap: 4rpx;
}

.day-income {
  color: #4caf50;
  font-weight: bold;
}

.day-expense {
  color: #f44336;
  font-weight: bold;
}

.day-cell.today .day-income,
.day-cell.today .day-expense {
  color: rgba(255, 255, 255, 0.9);
}

/* 日期详情卡片 */
.day-detail-card {
  background: #fff;
  margin: 10rpx;
  border-radius: 12rpx;
  padding: 20rpx;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  padding-bottom: 16rpx;
  border-bottom: 1rpx solid #eee;
}

.detail-date {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.detail-close {
  font-size: 24rpx;
  color: #999;
  padding: 10rpx;
  cursor: pointer;
}

.detail-summary {
  display: flex;
  justify-content: space-around;
  background: #f8f9fa;
  border-radius: 8rpx;
  padding: 16rpx;
  margin-bottom: 20rpx;
}

.detail-item {
  text-align: center;
}

.detail-label {
  font-size: 12rpx;
  color: #999;
  display: block;
  margin-bottom: 8rpx;
}

.detail-value {
  font-size: 24rpx;
  font-weight: bold;
  color: #333;
}

.detail-value.income {
  color: #4caf50;
}

.detail-value.expense {
  color: #f44336;
}

.detail-section {
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 14rpx;
  color: #666;
  font-weight: bold;
  margin-bottom: 12rpx;
  padding-left: 10rpx;
  border-left: 4rpx solid #667eea;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12rpx;
  background: #f8f9fa;
  border-radius: 8rpx;
  margin-bottom: 8rpx;
}

.record-left {
  display: flex;
  align-items: center;
  gap: 12rpx;
  flex: 1;
}

.record-type {
  padding: 4rpx 8rpx;
  border-radius: 4rpx;
  color: #fff;
  font-size: 11rpx;
  background: #667eea;
}

.record-type.expense {
  background: #f44336;
}

.record-info {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.record-name {
  font-size: 14rpx;
  color: #333;
  font-weight: bold;
}

.record-desc {
  font-size: 12rpx;
  color: #999;
}

.record-amount {
  font-size: 16rpx;
  font-weight: bold;
}

.record-amount.income {
  color: #4caf50;
}

.record-amount.expense {
  color: #f44336;
}

.no-data {
  text-align: center;
  padding: 40rpx;
  color: #999;
  font-size: 14rpx;
}

/* 快捷操作 */
.quick-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 20rpx;
  padding: 20rpx;
  background: #fff;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  padding: 16rpx 0;
  border: none;
  border-radius: 12rpx;
  font-size: 16rpx;
  font-weight: bold;
}

.income-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.expense-btn {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #fff;
}

.btn-icon {
  font-size: 20rpx;
}
</style>