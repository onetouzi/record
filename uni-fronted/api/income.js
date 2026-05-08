/**
 * 收入API服务
 * 处理收入记录的增删查、统计等接口
 */

import { POST, GET } from '../utils/request'

/**
 * 新增收入记录
 * @param {object} record - 收入记录对象
 * @returns {Promise<object>} 返回新增记录的ID
 */
export const addIncomeRecord = (record) => {
  return POST('/income', record)
}

/**
 * 获取收入记录列表
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 * @param {string} startDate - 开始日期 (YYYY-MM-DD)
 * @param {string} endDate - 结束日期 (YYYY-MM-DD)
 * @returns {Promise<object>} 分页收入记录列表
 */
export const getIncomeRecordList = (page = 1, size = 10, startDate = null, endDate = null) => {
  const params = { page, size }
  if (startDate) params.start_date = startDate
  if (endDate) params.end_date = endDate
  return GET('/income/list', params)
}

/**
 * 获取收入统计
 * @param {string} dateType - 统计类型 (day|week|month)
 * @param {string} date - 统计日期 (YYYY-MM-DD)
 * @returns {Promise<object>} 统计数据
 */
export const getIncomeStatistics = (dateType, date) => {
  return GET('/income/statistics', {
    date_type: dateType,
    date: date
  })
}
