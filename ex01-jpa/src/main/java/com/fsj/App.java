package com.fsj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.fsj.util.CommonUtil;

@SpringBootApplication
public class App 
{
    public static void main( String[] args )
    {
        SpringApplication.run(App.class, args);
        CommonUtil.print("http://127.0.0.1:8080/jpa/sort");
    }
}
