package com.fsj;

import com.fsj.util.CommonUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
// 在启动类中添加对mapper包扫描@MapperScan
// 不需要在每个Mapper类上面添加注解@Mapper
@MapperScan("com.fsj.dao")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        CommonUtil.print("http://127.0.0.1:8080/mybatis/queryAll");
    }
}