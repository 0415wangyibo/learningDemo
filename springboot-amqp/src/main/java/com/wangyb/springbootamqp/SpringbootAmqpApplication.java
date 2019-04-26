package com.wangyb.springbootamqp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringbootAmqpApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootAmqpApplication.class, args);
	}

}
