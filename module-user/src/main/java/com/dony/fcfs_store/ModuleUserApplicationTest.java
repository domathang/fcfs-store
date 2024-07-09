package com.dony.fcfs_store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.dony.fcfs_store", "com.dony.common.exception"})
public class ModuleUserApplicationTest {

	public static void main(String[] args) {
		SpringApplication.run(ModuleUserApplicationTest.class, args);
	}

}
