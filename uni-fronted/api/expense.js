/**
 * 支出API服务
 * 处理支出记录的增删查、统计等接口
 */

import { POST, GET } from '../utils/request'

/**
 * 新增支出记录
 * @param {object} record - 支出记录对象
 * @returns {Promise<object>} 返回新增记录的ID
 */
export const addExpenseRecord = (record) => {
  return POST('/expense', record)
}

/**
 * 获取支出记录列表
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 * @param {string} startDate - 开始日期 (YYYY-MM-DD)
 * @param {string} endDate - 结束日期 (YYYY-MM-DD)
 * @returns {Promise<object>} 分页支出记录列表
 */
export const getExpenseRecordList = (page = 1, size = 10, startDate = null, endDate = null) => {
  const params = { page, size }
  if (startDate) params.start_date = startDate
  if (endDate) params.end_date = endDate
  return GET('/expense/list', params)
}

/**
 * 获取支出统计
 * @param {string} dateType - 统计类型 (day|week|month)
 * @param {string} date - 统计日期 (YYYY-MM-DD)
 * @returns {Promise<object>} 统计数据
 */
export const getExpenseStatistics = (dateType, date) => {
  return GET('/expense/statistics', {
    date_type: dateType,
    date: date
  })
}
