package com.sneakyDateReforged.ms_invitation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.sneakyDateReforged.ms_invitation.client")
public class MsInvitationApplication {
	public static void main(String[] args) {
		SpringApplication.run(MsInvitationApplication.class, args);
	}
}
