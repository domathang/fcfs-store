package com.dony.fcfs_store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"com.dony.fcfs_store", "com.dony.common"})
public class ModuleProductApplicationTest {

	public static void main(String[] args) {
		SpringApplication.run(ModuleProductApplicationTest.class, args);
	}

}
