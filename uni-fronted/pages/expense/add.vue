<template>
  <view class="add-expense-page">
    <view class="form-container">
      <!-- 支出原因 -->
      <view class="form-group">
        <view class="form-label">支出原因 <text class="required">*</text></view>
        <view class="reason-select">
          <view
            v-for="reason in commonReasons"
            :key="reason"
            class="reason-option"
            :class="{ active: formData.reason === reason }"
            @click="selectReason(reason)"
          >
            {{ reason }}
          </view>
        </view>
        <input
          v-if="formData.reason === 'custom'"
          v-model="formData.customReason"
          type="text"
          placeholder="请输入支出原因"
          class="form-input"
          style="margin-top: 10rpx"
        />
      </view>

      <!-- 支出金额 -->
      <view class="form-group">
        <view class="form-label">支出金额 <text class="required">*</text></view>
        <view class="amount-input-group">
          <view class="currency">¥</view>
          <input
            v-model.number="formData.amount"
            type="number"
            placeholder="请输入金额"
            class="form-input"
          />
        </view>
      </view>

      <!-- 工作日期 -->
      <view class="form-group">
        <view class="form-label">工作日期 <text class="required">*</text></view>
        <picker 
          mode="date" 
          :value="formData.expenseDate" 
          start="2000-01-01" 
          :end="endDate" 
          @change="onDateChange"
        >
          <input
            v-model="formData.expenseDate"
            type="text"
            placeholder="请选择日期"
            class="form-input"
            readonly
          />
        </picker>
      </view>

      <!-- 备注（可选） -->
      <view class="form-group">
        <view class="form-label">备注</view>
        <textarea
          v-model="formData.remark"
          placeholder="输入备注信息"
          class="form-textarea"
          rows="3"
        />
      </view>

      <!-- 提交按钮 -->
      <view class="form-actions">
        <button class="btn-cancel" @click="goBack">取消</button>
        <button class="btn-submit" @click="submit" :disabled="submitting">
          {{ submitting ? '保存中...' : '保存' }}
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import { addExpenseRecord } from '../../api/expense'
import { getToday } from '../../utils/dateUtil'

export default {
  data() {
    return {
      commonReasons: [
        '饮食',
        '交通',
        '住宿',
        '购物',
        '医疗',
        '娱乐',
        '其他'
      ],
      formData: {
        reason: '',
        amount: null,
        expenseDate: getToday(),
        remark: '',
        customReason: ''
      },
      endDate: new Date().getFullYear() + '-12-31',
      submitting: false
    }
  },

  methods: {
    /**
     * 选择支出原因
     */
    selectReason(reason) {
      this.formData.reason = reason
    },

    /**
     * 选择支出日期
     */
    // selectExpenseDate() {
    //   uni.chooseDate({
    //     startDate: '2000-01-01',
    //     endDate: new Date().getFullYear() + '-12-31',
    //     success: (result) => {
    //       this.formData.expenseDate = result.date
    //     }
    //   })
    // },

    onDateChange(e) {
      this.formData.expenseDate = e.detail.value;
      console.log('选中的日期:', this.formData.expenseDate);
    },

    /**
     * 验证表单
     */
    validateForm() {
      const { reason, amount, expenseDate } = this.formData

      if (!reason || reason === '') {
        uni.showToast({
          title: '请选择支出原因',
          icon: 'none'
        })
        return false
      }

      if (reason === 'custom' && !this.formData.customReason) {
        uni.showToast({
          title: '请输入自定义原因',
          icon: 'none'
        })
        return false
      }

      if (!amount || amount <= 0) {
        uni.showToast({
          title: '请输入有效的支出金额',
          icon: 'none'
        })
        return false
      }

      if (!expenseDate) {
        uni.showToast({
          title: '请选择支出日期',
          icon: 'none'
        })
        return false
      }

      return true
    },

    /**
     * 提交表单
     */
    submit() {
      if (!this.validateForm()) return
      if (this.submitting) return

      this.submitting = true

      const submitData = {
        reason: this.formData.reason === 'custom' ? this.formData.customReason : this.formData.reason,
        amount: this.formData.amount,
        expenseDate: this.formData.expenseDate,
        remark: this.formData.remark
      }

      addExpenseRecord(submitData)
        .then((response) => {
          uni.showToast({
            title: '保存成功',
            icon: 'success'
          })
          // 延迟返回
          setTimeout(() => {
            this.goBack()
          }, 1000)
        })
        .catch((error) => {
          uni.showToast({
            title: error.message || '保存失败',
            icon: 'none'
          })
        })
        .finally(() => {
          this.submitting = false
        })
    },

    /**
     * 返回上一页
     */
    goBack() {
      uni.navigateBack()
    }
  },

  onLoad() {
    // 初始化日期为今天
    this.formData.expenseDate = getToday()
  }
}
</script>

<style scoped>
.add-expense-page {
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 20rpx;
}

.form-container {
  background: #fff;
  margin: 10rpx;
  padding: 20rpx;
  border-radius: 12rpx;
}

.form-group {
  margin-bottom: 20rpx;
}

.form-label {
  font-size: 14rpx;
  color: #333;
  margin-bottom: 10rpx;
  font-weight: bold;
}

.required {
  color: #f56c6c;
}

/* 原因选择 */
.reason-select {
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
}

.reason-option {
  padding: 12rpx 16rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 6rpx;
  background: #f9f9f9;
  font-size: 13rpx;
  color: #666;
  transition: all 0.3s;
}

.reason-option.active {
  border-color: #f5576c;
  background: #ffe6e8;
  color: #f5576c;
}

/* 输入框 */
.form-input,
.form-textarea {
  width: 100%;
  padding: 12rpx;
  border: 1rpx solid #ddd;
  border-radius: 6rpx;
  font-size: 14rpx;
  background: #f9f9f9;
  box-sizing: border-box;
}

.form-input:focus,
.form-textarea:focus {
  border-color: #f5576c;
  background: #fff;
}

.form-textarea {
  min-height: 100rpx;
  resize: vertical;
}

/* 金额输入 */
.amount-input-group {
  display: flex;
  align-items: center;
  border: 1rpx solid #ddd;
  border-radius: 6rpx;
  background: #f9f9f9;
  overflow: hidden;
}

.currency {
  padding: 0 12rpx;
  font-size: 16rpx;
  color: #666;
  font-weight: bold;
}

.amount-input-group .form-input {
  flex: 1;
  border: none;
  padding: 12rpx;
}

/* 按钮组 */
.form-actions {
  display: flex;
  gap: 12rpx;
  margin-top: 30rpx;
}

.btn-cancel,
.btn-submit {
  flex: 1;
  padding: 14rpx 0;
  border: none;
  border-radius: 6rpx;
  font-size: 16rpx;
  font-weight: bold;
}

.btn-cancel {
  background: #f0f0f0;
  color: #666;
}

.btn-submit {
  background: #f5576c;
  color: #fff;
}

.btn-submit:disabled {
  opacity: 0.6;
}
</style>
