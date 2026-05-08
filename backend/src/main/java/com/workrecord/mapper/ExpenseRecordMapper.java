package com.workrecord.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.workrecord.entity.ExpenseRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 支出记录Mapper接口
 */
@Mapper
public interface ExpenseRecordMapper extends BaseMapper<ExpenseRecord> {

    /**
     * 统计指定时间范围内的总支出
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 总支出金额
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM expense_record " +
            "WHERE user_id = #{userId} AND expense_date BETWEEN #{startDate} AND #{endDate}")
    BigDecimal sumTotalAmount(@Param("userId") Long userId,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);

    /**
     * 按支出原因统计金额
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 各支出原因的统计列表
     */
    @Select("SELECT reason, COALESCE(SUM(amount), 0) as total_amount " +
            "FROM expense_record WHERE user_id = #{userId} " +
            "AND expense_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY reason ORDER BY total_amount DESC")
    List<Map<String, Object>> sumByReason(@Param("userId") Long userId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
}