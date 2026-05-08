/**
 * 日期处理工具函数
 */

/**
 * 格式化日期
 * @param {Date|string} date - 日期对象或字符串
 * @param {string} format - 格式字符串 (YYYY-MM-DD, YYYY年MM月等)
 * @returns {string} 格式化后的日期字符串
 */
export const formatDate = (date, format = 'YYYY-MM-DD') => {
  if (!date) return ''
  
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const minute = String(d.getMinutes()).padStart(2, '0')
  const second = String(d.getSeconds()).padStart(2, '0')

  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hour)
    .replace('mm', minute)
    .replace('ss', second)
}

/**
 * 获取当月第一天
 * @param {Date|string} date - 日期对象或字符串
 * @returns {string} 月份第一天 (YYYY-MM-DD)
 */
export const getFirstDayOfMonth = (date = new Date()) => {
  const d = new Date(date)
  d.setDate(1)
  return formatDate(d, 'YYYY-MM-DD')
}

/**
 * 获取当月最后一天
 * @param {Date|string} date - 日期对象或字符串
 * @returns {string} 月份最后一天 (YYYY-MM-DD)
 */
export const getLastDayOfMonth = (date = new Date()) => {
  const d = new Date(date)
  d.setMonth(d.getMonth() + 1)
  d.setDate(0)
  return formatDate(d, 'YYYY-MM-DD')
}

/**
 * 获取今天的日期
 * @returns {string} 今天日期 (YYYY-MM-DD)
 */
export const getToday = () => {
  return formatDate(new Date(), 'YYYY-MM-DD')
}

/**
 * 获取昨天的日期
 * @returns {string} 昨天日期 (YYYY-MM-DD)
 */
export const getYesterday = () => {
  const d = new Date()
  d.setDate(d.getDate() - 1)
  return formatDate(d, 'YYYY-MM-DD')
}

/**
 * 获取本周一
 * @returns {string} 本周一日期 (YYYY-MM-DD)
 */
export const getMonday = () => {
  const d = new Date()
  const day = d.getDay()
  const diff = d.getDate() - day + (day === 0 ? -6 : 1)
  d.setDate(diff)
  return formatDate(d, 'YYYY-MM-DD')
}

/**
 * 获取本周日
 * @returns {string} 本周日日期 (YYYY-MM-DD)
 */
export const getSunday = () => {
  const d = new Date()
  const day = d.getDay()
  const diff = d.getDate() - day + 7 - (day === 0 ? 1 : 0)
  d.setDate(diff)
  return formatDate(d, 'YYYY-MM-DD')
}

/**
 * 判断是否为同一天
 * @param {Date|string} date1 - 日期1
 * @param {Date|string} date2 - 日期2
 * @returns {boolean}
 */
export const isSameDay = (date1, date2) => {
  return formatDate(date1, 'YYYY-MM-DD') === formatDate(date2, 'YYYY-MM-DD')
}

/**
 * 获取两个日期间隔天数
 * @param {Date|string} startDate - 开始日期
 * @param {Date|string} endDate - 结束日期
 * @returns {number} 天数
 */
export const getDaysDiff = (startDate, endDate) => {
  const d1 = new Date(startDate)
  const d2 = new Date(endDate)
  const time = Math.abs(d2 - d1)
  return Math.ceil(time / (1000 * 60 * 60 * 24))
}

/**
 * 获取指定月份的天数
 * @param {number} year - 年份
 * @param {number} month - 月份 (1-12)
 * @returns {number} 天数
 */
export const getDaysInMonth = (year, month) => {
  return new Date(year, month, 0).getDate()
}
