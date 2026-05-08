package com.workrecord.controller;

import com.workrecord.common.Result;
import com.workrecord.dto.request.IncomeRecordRequest;
import com.workrecord.dto.request.PageRequest;
import com.workrecord.dto.request.StatisticsRequest;
import com.workrecord.dto.response.IncomeRecordResponse;
import com.workrecord.dto.response.IncomeStatisticsResponse;
import com.workrecord.dto.response.PageResponse;
import com.workrecord.dto.response.RecordIdResponse;
import com.workrecord.service.IncomeRecordService;
import com.workrecord.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 收入记录控制器
 * 处理收入记录的增删查、统计等相关接口
 */
@Slf4j
@RestController
@RequestMapping("/api/income")
@RequiredArgsConstructor
public class IncomeRecordController {

    private final IncomeRecordService incomeRecordService;

    /**
     * 新增收入记录
     * 支持三种收入类型：
     * 1-记时 (按工作时长计算)
     * 2-记件 (按数量计算)
     * 3-记平方 (按面积计算)
     *
     * @param request 收入记录请求对象
     * @return 新增记录ID
     */
    @PostMapping
    public Result<RecordIdResponse> addIncomeRecord(@RequestBody IncomeRecordRequest request) {
        log.info("新增收入记录，type: {}", request.getIncomeType());

        // 从上下文获取当前用户ID
        Long userId = UserContext.getUserId();

        // 调用服务添加收入记录
        RecordIdResponse response = incomeRecordService.addIncomeRecord(userId, request);

        return Result.success("收入记录添加成功", response);
    }

    /**
     * 获取收入记录列表（分页）
     * 支持按日期范围筛选
     *
     * @param page 页码（默认1）
     * @param size 每页数量（默认10）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 分页记录列表
     */
    @GetMapping("/list")
    public Result<PageResponse<IncomeRecordResponse>> getIncomeRecordList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "start_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "end_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("获取收入记录列表，page: {}, size: {}, startDate: {}, endDate: {}",
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
        PageResponse<IncomeRecordResponse> response = incomeRecordService.getIncomeRecordList(userId, pageRequest);

        return Result.success("获取收入记录列表成功", response);
    }

    /**
     * 获取收入统计数据
     * 支持按天、周、月进行统计
     *
     * @param dateType 统计类型：day(按天)|week(按周)|month(按月)
     * @param date 统计日期
     * @return 收入统计响应
     */
    @GetMapping("/statistics")
    public Result<IncomeStatisticsResponse> getIncomeStatistics(
            @RequestParam(value = "date_type") String dateType,
            @RequestParam(value = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("获取收入统计，dateType: {}, date: {}", dateType, date);

        // 从上下文获取当前用户ID
        Long userId = UserContext.getUserId();

        // 调用服务获取统计数据
        IncomeStatisticsResponse response = incomeRecordService.getIncomeStatistics(userId, dateType, date);

        return Result.success("获取收入统计成功", response);
    }
}
