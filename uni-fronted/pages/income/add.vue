<template>
  <view class="add-income-page">
    <view class="form-container">
      <!-- 收入类型选择 -->
      <view class="form-group">
        <view class="form-label">收入类型 <text class="required">*</text></view>
        <view class="income-type-select">
          <view
            v-for="type in incomeTypes"
            :key="type.id"
            class="type-option"
            :class="{ active: formData.incomeType === type.id }"
            @click="selectIncomeType(type.id)"
          >
            <view class="type-icon">{{ type.icon }}</view>
            <view class="type-text">{{ type.name }}</view>
          </view>
        </view>
      </view>

      <!-- 动态字段 - 根据收入类型显示 -->
      <!-- 记时类型 -->
      <view v-if="formData.incomeType === 1" class="form-group">
        <view class="form-label">正常工时(小时) <text class="required">*</text></view>
        <input
          v-model.number="formData.workHours"
          type="number"
          placeholder="请输入正常工时"
          class="form-input"
        />
      </view>

      <!-- 加班工时和加班单价（仅记时类型显示） -->
      <view v-if="formData.incomeType === 1" class="overtime-section">
        <view class="section-title">加班信息（可选）</view>
        <view class="form-group">
          <view class="form-label">加班工时(小时)</view>
          <input
            v-model.number="formData.overtimeHours"
            type="number"
            placeholder="请输入加班工时"
            class="form-input"
          />
        </view>
        <view class="form-group">
          <view class="form-label">加班单价</view>
          <input
            v-model.number="formData.overtimeUnitPrice"
            type="number"
            placeholder="请输入加班单价"
            class="form-input"
          />
        </view>
      </view>

      <view v-if="formData.incomeType === 2" class="form-group">
        <view class="form-label">数量 <text class="required">*</text></view>
        <input
          v-model.number="formData.quantity"
          type="number"
          placeholder="请输入数量"
          class="form-input"
        />
      </view>

      <view v-if="formData.incomeType === 3" class="form-group">
        <view class="form-label">面积(平方米) <text class="required">*</text></view>
        <input
          v-model.number="formData.area"
          type="number"
          placeholder="请输入面积"
          class="form-input"
        />
      </view>

      <!-- 单价 -->
      <view class="form-group">
        <view class="form-label">单价 <text class="required">*</text></view>
        <input
          v-model.number="formData.unitPrice"
          type="number"
          placeholder="请输入单价"
          class="form-input"
        />
      </view>

      <!-- 工作日期 -->
      <view class="form-group">
        <view class="form-label">工作日期 <text class="required">*</text></view>
        <picker 
          mode="date" 
          :value="formData.workDate" 
          start="2000-01-01" 
          :end="endDate" 
          @change="onDateChange"
        >
          <input
            v-model="formData.workDate"
            type="text"
            placeholder="请选择日期"
            class="form-input"
            readonly
          />
        </picker>
      </view>


      <!-- 服务对象（可选） -->
      <view class="form-group">
        <view class="form-label">服务对象</view>
        <input
          v-model="formData.serviceObject"
          type="text"
          placeholder="输入客户名称或备注"
          class="form-input"
        />
      </view>

      <!-- 联系电话（可选） -->
      <view class="form-group">
        <view class="form-label">联系电话</view>
        <input
          v-model="formData.contactPhone"
          type="text"
          placeholder="输入联系电话"
          class="form-input"
        />
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

      <!-- 预计金额 -->
      <view class="form-group">
        <view class="form-label">预计金额</view>
        <view class="amount-display">
          {{ formatCurrency(calculateAmount()) }}
        </view>
        <!-- 加班类型显示详细计算 -->
        <view v-if="formData.incomeType === 1 && formData.overtimeHours > 0" class="amount-detail">
          <text>正常：{{ formData.workHours }}h × ¥{{ formData.unitPrice }} = ¥{{ (formData.workHours * formData.unitPrice).toFixed(2) }}</text>
          <text v-if="formData.overtimeHours > 0"> + 加班：{{ formData.overtimeHours }}h × ¥{{ formData.overtimeUnitPrice }} = ¥{{ (formData.overtimeHours * formData.overtimeUnitPrice).toFixed(2) }}</text>
        </view>
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
import { addIncomeRecord } from '../../api/income'
import { formatCurrency } from '../../utils/format'
import { getToday } from '../../utils/dateUtil'

export default {
  data() {
    return {
      incomeTypes: [
        { id: 1, name: '记时', icon: '⏰' },
        { id: 2, name: '记件', icon: '📦' },
        { id: 3, name: '记平方', icon: '📐' }
      ],
      formData: {
        incomeType: 1,
        workHours: null,
        overtimeHours: null,
        overtimeUnitPrice: null,
        quantity: null,
        area: null,
        unitPrice: null,
        workDate: null,
        serviceObject: '',
        contactPhone: '',
        remark: ''
      },
      endDate: new Date().getFullYear() + '-12-31',
      submitting: false
    }
  },

  methods: {
    /**
     * 选择收入类型
     */
    selectIncomeType(typeId) {
      this.formData.incomeType = typeId
      // 清空相关字段
      this.formData.workHours = null
      this.formData.overtimeHours = null
      this.formData.overtimeUnitPrice = null
      this.formData.quantity = null
      this.formData.area = null
    },

    onDateChange(e) {
      this.formData.workDate = e.detail.value;
      console.log('选中的日期:', this.formData.workDate);
    },

    /**
     * 计算金额
     */
    calculateAmount() {
      const { incomeType, unitPrice, workHours, overtimeHours, overtimeUnitPrice, quantity, area } = this.formData

      if (!unitPrice) return 0

      switch (incomeType) {
        case 1:
          let normalAmount = workHours ? workHours * unitPrice : 0
          let overtimeAmount = 0
          if (overtimeHours && overtimeUnitPrice) {
            overtimeAmount = overtimeHours * overtimeUnitPrice
          }
          return normalAmount + overtimeAmount
        case 2:
          return quantity ? quantity * unitPrice : 0
        case 3:
          return area ? area * unitPrice : 0
        default:
          return 0
      }
    },

    /**
     * 验证表单
     */
    validateForm() {
      const { incomeType, unitPrice, workDate } = this.formData

      if (!unitPrice || unitPrice <= 0) {
        uni.showToast({
          title: '请输入有效的单价',
          icon: 'none'
        })
        return false
      }

      switch (incomeType) {
        case 1:
          if (!this.formData.workHours || this.formData.workHours <= 0) {
            uni.showToast({
              title: '请输入有效的工作时长',
              icon: 'none'
            })
            return false
          }
          // 如果填写了加班工时，必须填写加班单价
          if (this.formData.overtimeHours && this.formData.overtimeHours > 0) {
            if (!this.formData.overtimeUnitPrice || this.formData.overtimeUnitPrice <= 0) {
              uni.showToast({
                title: '请输入有效的加班单价',
                icon: 'none'
              })
              return false
            }
          }
          break
        case 2:
          if (!this.formData.quantity || this.formData.quantity <= 0) {
            uni.showToast({
              title: '请输入有效的数量',
              icon: 'none'
            })
            return false
          }
          break
        case 3:
          if (!this.formData.area || this.formData.area <= 0) {
            uni.showToast({
              title: '请输入有效的面积',
              icon: 'none'
            })
            return false
          }
          break
      }

      if (!workDate) {
        uni.showToast({
          title: '请选择工作日期',
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

      addIncomeRecord(this.formData)
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
    },

    /**
     * 格式化货币
     */
    formatCurrency(amount) {
      return formatCurrency(amount)
    }
  },

  onLoad() {
    // 初始化日期为今天
    this.formData.workDate = getToday()
  }
}
</script>

<style scoped>
.add-income-page {
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

/* 收入类型选择 */
.income-type-select {
  display: flex;
  gap: 12rpx;
}

.type-option {
  flex: 1;
  text-align: center;
  padding: 20rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 8rpx;
  background: #f9f9f9;
  transition: all 0.3s;
}

.type-option.active {
  border-color: #667eea;
  background: #f0f1ff;
}

.type-icon {
  font-size: 24rpx;
  margin-bottom: 8rpx;
}

.type-text {
  font-size: 12rpx;
  color: #666;
}

/* 加班信息区域 */
.overtime-section {
  background: #f8f9fa;
  border-radius: 8rpx;
  padding: 16rpx;
  margin-bottom: 20rpx;
  border-left: 4rpx solid #ffa726;
}

.overtime-section .section-title {
  font-size: 13rpx;
  color: #ffa726;
  font-weight: bold;
  margin-bottom: 12rpx;
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
  border-color: #667eea;
  background: #fff;
}

.form-textarea {
  min-height: 100rpx;
  resize: vertical;
}

/* 金额显示 */
.amount-display {
  padding: 12rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 6rpx;
  text-align: center;
  font-size: 18rpx;
  font-weight: bold;
}

.amount-detail {
  margin-top: 8rpx;
  padding: 8rpx;
  background: #fff3e0;
  border-radius: 4rpx;
  font-size: 12rpx;
  color: #666;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
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
  background: #667eea;
  color: #fff;
}

.btn-submit:disabled {
  opacity: 0.6;
}
</style>