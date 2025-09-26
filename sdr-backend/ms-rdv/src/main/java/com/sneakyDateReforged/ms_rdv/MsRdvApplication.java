package com.sneakyDateReforged.ms_rdv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsRdvApplication {
	public static void main(String[] args) {
		SpringApplication.run(MsRdvApplication.class, args);
	}
}
