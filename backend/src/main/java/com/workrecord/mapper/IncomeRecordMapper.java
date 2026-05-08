package com.workrecord.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.workrecord.entity.IncomeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 收入记录Mapper接口
 */
@Mapper
public interface IncomeRecordMapper extends BaseMapper<IncomeRecord> {

    /**
     * 统计指定时间范围内的总收入
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 总收入金额
     */
    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM income_record " +
            "WHERE user_id = #{userId} AND work_date BETWEEN #{startDate} AND #{endDate}")
    BigDecimal sumTotalAmount(@Param("userId") Long userId,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);

    /**
     * 按收入类型统计金额
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 各类型的收入统计列表
     */
    @Select("SELECT income_type, COALESCE(SUM(total_amount), 0) as amount " +
            "FROM income_record WHERE user_id = #{userId} " +
            "AND work_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY income_type")
    List<Map<String, Object>> sumByIncomeType(@Param("userId") Long userId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    /**
     * 统计工作天数
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 工作天数
     */
    @Select("SELECT COUNT(DISTINCT work_date) FROM income_record " +
            "WHERE user_id = #{userId} AND work_date BETWEEN #{startDate} AND #{endDate}")
    Integer countWorkDays(@Param("userId") Long userId,
                          @Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate);
}