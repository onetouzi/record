package com.workrecord;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 打零工人员记账小程序 - 主启动类
 * @author workrecord
 */
@SpringBootApplication
@MapperScan("com.workrecord.mapper")
public class WorkRecordApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkRecordApplication.class, args);
    }
}