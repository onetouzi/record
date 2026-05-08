package com.workrecord.controller;

import com.workrecord.common.Result;
import com.workrecord.dto.request.ExpenseRecordRequest;
import com.workrecord.dto.request.PageRequest;
import com.workrecord.dto.request.StatisticsRequest;
import com.workrecord.dto.response.ExpenseRecordResponse;
import com.workrecord.dto.response.ExpenseStatisticsResponse;
import com.workrecord.dto.response.PageResponse;
import com.workrecord.dto.response.RecordIdResponse;
import com.workrecord.service.ExpenseRecordService;
import com.workrecord.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 支出记录控制器
 * 处理支出记录的增删查、统计等相关接口
 */
@Slf4j
@RestController
@RequestMapping("/api/expense")
@RequiredArgsConstructor
public class ExpenseRecordController {

    private final ExpenseRecordService expenseRecordService;

    /**
     * 新增支出记录
     *
     * @param request 支出记录请求对象
     * @return 新增记录ID
     */
    @PostMapping
    public Result<RecordIdResponse> addExpenseRecord(@RequestBody ExpenseRecordRequest request) {
        log.info("新增支出记录，reason: {}, amount: {}", request.getReason(), request.getAmount());

        // 从上下文获取当前用户ID
        Long userId = UserContext.getUserId();

        // 调用服务添加支出记录
        RecordIdResponse response = expenseRecordService.addExpenseRecord(userId, request);

        return Result.success("支出记录添加成功", response);
    }

    /**
     * 获取支出记录列表（分页）
     * 支持按日期范围筛选
     *
     * @param page 页码（默认1）
     * @param size 每页数量（默认10）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 分页记录列表
     */
    @GetMapping("/list")
    public Result<PageResponse<ExpenseRecordResponse>> getExpenseRecordList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "start_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "end_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("获取支出记录列表，page: {}, size: {}, startDate: {}, endDate: {}",
                page, size, startDate, endDate);

        // 从上下文获取当前用户ID
        Long userId = UserContext.getUserId();

        // 构建分页请求
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        pageRequest.setStartDate(startDate);
        pageRequest.setEndDate(endDate);

        // 调用服务获取记录列表
        PageResponse<ExpenseRecordResponse> response = expenseRecordService.getExpenseRecordList(userId, pageRequest);

        return Result.success("获取支出记录列表成功", response);
    }

    /**
     * 获取支出统计数据
     * 支持按天、周、月进行统计
     *
     * @param dateType 统计类型：day(按天)|week(按周)|month(按月)
     * @param date 统计日期
     * @return 支出统计响应
     */
    @GetMapping("/statistics")
    public Result<ExpenseStatisticsResponse> getExpenseStatistics(
            @RequestParam(value = "date_type") String dateType,
            @RequestParam(value = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("获取支出统计，dateType: {}, date: {}", dateType, date);

        // 从上下文获取当前用户ID
        Long userId = UserContext.getUserId();

        // 调用服务获取统计数据
        ExpenseStatisticsResponse response = expenseRecordService.getExpenseStatistics(userId, dateType, date);

        return Result.success("获取支出统计成功", response);
    }
}
