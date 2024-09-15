package com.schedulerManager.SchedulerManagerSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SchedulerManagerSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchedulerManagerSystemApplication.class, args);
	}

}
