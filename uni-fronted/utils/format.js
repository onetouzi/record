/**
 * 格式化工具函数
 * 处理货币、日期、数字等格式化
 */

/**
 * 格式化货币
 * @param {number} amount - 金额
 * @param {string} prefix - 前缀 (默认¥)
 * @returns {string} 格式化后的货币字符串
 */
export const formatCurrency = (amount, prefix = '¥') => {
  if (!amount && amount !== 0) return `${prefix}0.00`
  const num = parseFloat(amount)
  return `${prefix}${num.toFixed(2)}`
}

/**
 * 收入类型转换为文本
 * @param {number} type - 收入类型 (1-记时, 2-记件, 3-记平方)
 * @returns {string}
 */
export const incomeTypeToText = (type) => {
  const types = {
    1: '记时',
    2: '记件',
    3: '记平方'
  }
  return types[type] || '未知类型'
}

/**
 * 获取收入类型的颜色
 * @param {number} type - 收入类型
 * @returns {string} 颜色值
 */
export const getIncomeTypeColor = (type) => {
  const colors = {
    1: '#667eea',  // 蓝紫色 - 记时
    2: '#764ba2',  // 深紫色 - 记件
    3: '#f093fb'   // 粉色 - 记平方
  }
  return colors[type] || '#999999'
}

/**
 * 获取收入类型的单位
 * @param {number} type - 收入类型
 * @returns {string}
 */
export const getIncomeUnit = (type) => {
  const units = {
    1: '小时',
    2: '件',
    3: '平方米'
  }
  return units[type] || '单位'
}

/**
 * 截断文本
 * @param {string} text - 文本内容
 * @param {number} length - 截断长度
 * @param {string} suffix - 后缀
 * @returns {string}
 */
export const truncateText = (text, length = 20, suffix = '...') => {
  if (!text) return ''
  if (text.length <= length) return text
  return text.substring(0, length) + suffix
}

/**
 * 格式化百分数
 * @param {number} value - 值
 * @param {number} total - 总数
 * @returns {string}
 */
export const formatPercent = (value, total) => {
  if (!total || total === 0) return '0%'
  return `${((value / total) * 100).toFixed(1)}%`
}

/**
 * 格式化数字 (添加千位分隔符)
 * @param {number} num - 数字
 * @returns {string}
 */
export const formatNumber = (num) => {
  if (!num && num !== 0) return '0'
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

/**
 * 获取月份文本
 * @param {number} month - 月份 (1-12)
 * @returns {string}
 */
export const getMonthText = (month) => {
  const months = ['一月', '二月', '三月', '四月', '五月', '六月', 
                  '七月', '八月', '九月', '十月', '十一月', '十二月']
  return months[month - 1] || '未知月份'
}

/**
 * 获取星期文本
 * @param {number} day - 星期 (0-6, 0为周日)
 * @returns {string}
 */
export const getWeekText = (day) => {
  const weeks = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return weeks[day] || '未知'
}
