package com.sneakyDateReforged.ms_profil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.sneakyDateReforged.ms_profil.client")
public class MsProfilApplication {
	public static void main(String[] args) {
		SpringApplication.run(MsProfilApplication.class, args);
	}
}
