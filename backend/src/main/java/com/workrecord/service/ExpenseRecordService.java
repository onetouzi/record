package com.workrecord.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.workrecord.dto.request.ExpenseRecordRequest;
import com.workrecord.dto.request.PageRequest;
import com.workrecord.dto.response.ExpenseItemResponse;
import com.workrecord.dto.response.ExpenseRecordResponse;
import com.workrecord.dto.response.ExpenseStatisticsResponse;
import com.workrecord.dto.response.PageResponse;
import com.workrecord.dto.response.RecordIdResponse;
import com.workrecord.entity.ExpenseRecord;
import com.workrecord.exception.BusinessException;
import com.workrecord.mapper.ExpenseRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

/**
 * 支出记录业务服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseRecordService {

    private final ExpenseRecordMapper expenseRecordMapper;

    /**
     * 新增支出记录
     * @param userId 用户ID
     * @param request 支出记录请求
     * @return 新增记录的ID
     */
    @Transactional
    public RecordIdResponse addExpenseRecord(Long userId, ExpenseRecordRequest request) {
        // 创建支出记录
        ExpenseRecord record = new ExpenseRecord();
        record.setUserId(userId);
        record.setReason(request.getReason());
        record.setAmount(request.getAmount());
        record.setExpenseDate(request.getExpenseDate());
        record.setRemark(request.getRemark());

        expenseRecordMapper.insert(record);
        log.info("新增支出记录成功, userId: {}, reason: {}, amount: {}",
                userId, request.getReason(), request.getAmount());

        RecordIdResponse response = new RecordIdResponse();
        response.setId(record.getId());
        return response;
    }

    /**
     * 获取支出记录列表（分页）
     * @param userId 用户ID
     * @param request 分页请求
     * @return 分页响应
     */
    public PageResponse<ExpenseRecordResponse> getExpenseRecordList(Long userId, PageRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<ExpenseRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExpenseRecord::getUserId, userId)
               .orderByDesc(ExpenseRecord::getExpenseDate)
               .orderByDesc(ExpenseRecord::getCreateTime);

        // 添加日期范围筛选
        if (request.getStartDate() != null) {
            wrapper.ge(ExpenseRecord::getExpenseDate, request.getStartDate());
        }
        if (request.getEndDate() != null) {
            wrapper.le(ExpenseRecord::getExpenseDate, request.getEndDate());
        }

        // 分页查询
        Page<ExpenseRecord> page = new Page<>(request.getPage(), request.getSize());
        Page<ExpenseRecord> result = expenseRecordMapper.selectPage(page, wrapper);

        // 转换响应
        PageResponse<ExpenseRecordResponse> response = new PageResponse<>();
        response.setTotal(result.getTotal());
        response.setList(convertToList(result.getRecords()));
        return response;
    }

    /**
     * 获取支出统计
     * @param userId 用户ID
     * @param dateType 统计类型: day/week/month
     * @param date 统计日期
     * @return 统计响应
     */
    public ExpenseStatisticsResponse getExpenseStatistics(Long userId, String dateType, LocalDate date) {
        // 根据统计类型计算日期范围
        LocalDate startDate;
        LocalDate endDate;

        switch (dateType.toLowerCase()) {
            case "day":
                startDate = date;
                endDate = date;
                break;
            case "week":
                startDate = date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
                endDate = date.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
                break;
            case "month":
                startDate = date.with(TemporalAdjusters.firstDayOfMonth());
                endDate = date.with(TemporalAdjusters.lastDayOfMonth());
                break;
            default:
                throw new BusinessException(400, "统计类型必须是day、week或month");
        }

        // 查询统计数据
        BigDecimal totalExpense = expenseRecordMapper.sumTotalAmount(userId, startDate, endDate);
        List<Map<String, Object>> reasonList = expenseRecordMapper.sumByReason(userId, startDate, endDate);

        // 构建各支出项目统计列表
        List<ExpenseItemResponse> expenseItems = reasonList.stream()
                .map(item -> new ExpenseItemResponse(
                        (String) item.get("reason"),
                        ((BigDecimal) item.get("total_amount")).setScale(2, RoundingMode.HALF_UP)
                ))
                .toList();

        // 构建响应
        ExpenseStatisticsResponse response = new ExpenseStatisticsResponse();
        response.setTotalExpense(totalExpense != null ? totalExpense.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        response.setExpenseItems(expenseItems);
        return response;
    }

    /**
     * 转换实体列表为响应列表
     */
    private List<ExpenseRecordResponse> convertToList(List<ExpenseRecord> records) {
        return records.stream().map(this::convertToResponse).toList();
    }

    /**
     * 转换实体为响应
     */
    private ExpenseRecordResponse convertToResponse(ExpenseRecord record) {
        ExpenseRecordResponse response = new ExpenseRecordResponse();
        response.setId(record.getId());
        response.setReason(record.getReason());
        response.setAmount(record.getAmount());
        response.setExpenseDate(record.getExpenseDate());
        response.setRemark(record.getRemark());
        response.setCreateTime(record.getCreateTime());
        return response;
    }
}