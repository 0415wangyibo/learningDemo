package com.wangyibo.fulltextdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.wangyibo.fulltextdemo.*"})
@MapperScan(basePackages = {"com.wangyibo.fulltextdemo.mapper"})
public class FullTextDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(FullTextDemoApplication.class, args);
	}

}
