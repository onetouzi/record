package com.workrecord.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.workrecord.dto.request.IncomeRecordRequest;
import com.workrecord.dto.request.PageRequest;
import com.workrecord.dto.response.IncomeRecordResponse;
import com.workrecord.dto.response.IncomeStatisticsResponse;
import com.workrecord.dto.response.PageResponse;
import com.workrecord.dto.response.RecordIdResponse;
import com.workrecord.entity.IncomeRecord;
import com.workrecord.exception.BusinessException;
import com.workrecord.mapper.IncomeRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收入记录业务服务
 *
 * 核心业务逻辑:
 * 1. 记时(type=1): 金额 = 正常工时 × 正常单价 + 加班工时 × 加班单价
 * 2. 记件(type=2): 金额 = 数量 × 单价
 * 3. 记平方(type=3): 金额 = 面积 × 单价
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IncomeRecordService {

    private final IncomeRecordMapper incomeRecordMapper;

    /** 收入类型: 记时 */
    private static final int TYPE_HOURS = 1;
    /** 收入类型: 记件 */
    private static final int TYPE_QUANTITY = 2;
    /** 收入类型: 记平方 */
    private static final int TYPE_AREA = 3;

    /**
     * 新增收入记录
     * 根据收入类型自动计算总金额
     * @param userId 用户ID
     * @param request 收入记录请求
     * @return 新增记录的ID
     */
    @Transactional
    public RecordIdResponse addIncomeRecord(Long userId, IncomeRecordRequest request) {
        // 验证收入类型
        validateIncomeType(request);

        // 计算总金额（核心业务逻辑）
        BigDecimal totalAmount = calculateTotalAmount(request);

        // 创建收入记录
        IncomeRecord record = new IncomeRecord();
        record.setUserId(userId);
        record.setIncomeType(request.getIncomeType());
        record.setWorkHours(request.getWorkHours());
        record.setOvertimeHours(request.getOvertimeHours());
        record.setOvertimeUnitPrice(request.getOvertimeUnitPrice());
        record.setQuantity(request.getQuantity());
        record.setArea(request.getArea());
        record.setUnitPrice(request.getUnitPrice());
        record.setTotalAmount(totalAmount);
        record.setServiceObject(request.getServiceObject());
        record.setContactPhone(request.getContactPhone());
        record.setWorkDate(request.getWorkDate());
        record.setRemark(request.getRemark());

        incomeRecordMapper.insert(record);
        log.info("新增收入记录成功, userId: {}, type: {}, amount: {}",
                userId, request.getIncomeType(), totalAmount);

        RecordIdResponse response = new RecordIdResponse();
        response.setId(record.getId());
        return response;
    }

    /**
     * 计算总金额（核心业务逻辑）
     * 根据收入类型选择不同的计算方式:
     * - 记时(type=1): 金额 = 正常工时 × 正常单价 + 加班工时 × 加班单价
     * - 记件(type=2): 金额 = 数量 × 单价
     * - 记平方(type=3): 金额 = 面积 × 单价
     */
    private BigDecimal calculateTotalAmount(IncomeRecordRequest request) {
        BigDecimal unitPrice = request.getUnitPrice();
        Integer incomeType = request.getIncomeType();

        BigDecimal totalAmount;

        switch (incomeType) {
            case TYPE_HOURS:
                // 记时: 金额 = 正常工时 × 正常单价 + 加班工时 × 加班单价
                if (request.getWorkHours() == null || request.getWorkHours().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BusinessException(400, "记时类型必须填写有效的工作时长");
                }
                totalAmount = request.getWorkHours().multiply(unitPrice);
                
                // 如果有加班工时，加上加班费
                if (request.getOvertimeHours() != null && request.getOvertimeHours().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal overtimeUnitPrice = request.getOvertimeUnitPrice();
                    if (overtimeUnitPrice == null || overtimeUnitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new BusinessException(400, "加班工时必须填写有效的加班单价");
                    }
                    totalAmount = totalAmount.add(request.getOvertimeHours().multiply(overtimeUnitPrice));
                }
                break;

            case TYPE_QUANTITY:
                // 记件: 金额 = 数量 × 单价
                if (request.getQuantity() == null || request.getQuantity() <= 0) {
                    throw new BusinessException(400, "记件类型必须填写有效的数量");
                }
                totalAmount = new BigDecimal(request.getQuantity()).multiply(unitPrice);
                break;

            case TYPE_AREA:
                // 记平方: 金额 = 面积 × 单价
                if (request.getArea() == null || request.getArea().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BusinessException(400, "记平方类型必须填写有效的面积");
                }
                totalAmount = request.getArea().multiply(unitPrice);
                break;

            default:
                throw new BusinessException(400, "无效的收入类型");
        }

        // 保留两位小数
        return totalAmount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 验证收入类型参数
     */
    private void validateIncomeType(IncomeRecordRequest request) {
        Integer incomeType = request.getIncomeType();
        if (incomeType == null || incomeType < TYPE_HOURS || incomeType > TYPE_AREA) {
            throw new BusinessException(400, "收入类型必须是1(记时)、2(记件)或3(记平方)");
        }
    }

    /**
     * 获取收入记录列表（分页）
     * @param userId 用户ID
     * @param request 分页请求
     * @return 分页响应
     */
    public PageResponse<IncomeRecordResponse> getIncomeRecordList(Long userId, PageRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<IncomeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IncomeRecord::getUserId, userId)
               .orderByDesc(IncomeRecord::getWorkDate)
               .orderByDesc(IncomeRecord::getCreateTime);

        // 添加日期范围筛选
        if (request.getStartDate() != null) {
            wrapper.ge(IncomeRecord::getWorkDate, request.getStartDate());
        }
        if (request.getEndDate() != null) {
            wrapper.le(IncomeRecord::getWorkDate, request.getEndDate());
        }

        // 分页查询
        Page<IncomeRecord> page = new Page<>(request.getPage(), request.getSize());
        Page<IncomeRecord> result = incomeRecordMapper.selectPage(page, wrapper);

        // 转换响应
        PageResponse<IncomeRecordResponse> response = new PageResponse<>();
        response.setTotal(result.getTotal());
        response.setList(convertToList(result.getRecords()));
        return response;
    }

    /**
     * 获取收入统计
     * @param userId 用户ID
     * @param dateType 统计类型: day/week/month
     * @param date 统计日期
     * @return 统计响应
     */
    public IncomeStatisticsResponse getIncomeStatistics(Long userId, String dateType, LocalDate date) {
        // 根据统计类型计算日期范围
        LocalDate startDate;
        LocalDate endDate;

        switch (dateType.toLowerCase()) {
            case "day":
                startDate = date;
                endDate = date;
                break;
            case "week":
                // 计算当周的周一和周日
                startDate = date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
                endDate = date.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
                break;
            case "month":
                // 计算当月的首日和末日
                startDate = date.with(TemporalAdjusters.firstDayOfMonth());
                endDate = date.with(TemporalAdjusters.lastDayOfMonth());
                break;
            default:
                throw new BusinessException(400, "统计类型必须是day、week或month");
        }

        // 查询统计数据
        BigDecimal totalIncome = incomeRecordMapper.sumTotalAmount(userId, startDate, endDate);
        Integer workDays = incomeRecordMapper.countWorkDays(userId, startDate, endDate);
        List<Map<String, Object>> typeList = incomeRecordMapper.sumByIncomeType(userId, startDate, endDate);

        // 构建各类型收入统计Map
        Map<String, BigDecimal> incomeTypes = new HashMap<>();
        for (Map<String, Object> item : typeList) {
            String type = String.valueOf(item.get("income_type"));
            BigDecimal amount = (BigDecimal) item.get("amount");
            incomeTypes.put(type, amount.setScale(2, RoundingMode.HALF_UP));
        }

        // 构建响应
        IncomeStatisticsResponse response = new IncomeStatisticsResponse();
        response.setTotalIncome(totalIncome != null ? totalIncome.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        response.setIncomeTypes(incomeTypes);
        response.setWorkDays(workDays);
        return response;
    }

    /**
     * 转换实体列表为响应列表
     */
    private List<IncomeRecordResponse> convertToList(List<IncomeRecord> records) {
        return records.stream().map(this::convertToResponse).toList();
    }

    /**
     * 转换实体为响应
     */
    private IncomeRecordResponse convertToResponse(IncomeRecord record) {
        IncomeRecordResponse response = new IncomeRecordResponse();
        response.setId(record.getId());
        response.setIncomeType(record.getIncomeType());
        response.setWorkHours(record.getWorkHours());
        response.setOvertimeHours(record.getOvertimeHours());
        response.setOvertimeUnitPrice(record.getOvertimeUnitPrice());
        response.setQuantity(record.getQuantity());
        response.setArea(record.getArea());
        response.setUnitPrice(record.getUnitPrice());
        response.setTotalAmount(record.getTotalAmount());
        response.setServiceObject(record.getServiceObject());
        response.setContactPhone(record.getContactPhone());
        response.setWorkDate(record.getWorkDate());
        response.setRemark(record.getRemark());
        response.setCreateTime(record.getCreateTime());
        return response;
    }
}