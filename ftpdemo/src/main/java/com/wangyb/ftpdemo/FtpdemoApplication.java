package com.wangyb.ftpdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ThreadPoolExecutor;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class FtpdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(FtpdemoApplication.class, args);
	}

	@Bean(name = "taskExecutor")
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		//6个线程执行下载上传任务，多线程之间相互牵制，导致被IO阻塞的线程出现异常，从而结束被阻塞的线程
		executor.setCorePoolSize(6);
		executor.setMaxPoolSize(6);
		//最多1000个等待队列
		executor.setQueueCapacity(1000);
		executor.setThreadGroupName("taskExecutor");
		//如果队列已满，丢弃最早的
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
		executor.initialize();
		return executor;
	}

	@Bean
	public TaskScheduler scheduledExecutorService() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		//四个线程执行定时任务
		scheduler.setPoolSize(4);
		scheduler.setThreadNamePrefix("schedule-");
		return scheduler;
	}
}
