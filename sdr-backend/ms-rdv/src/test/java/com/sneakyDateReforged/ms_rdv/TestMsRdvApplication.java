package com.sneakyDateReforged.ms_rdv;

import org.springframework.boot.SpringApplication;

public class TestMsRdvApplication {

	public static void main(String[] args) {
		SpringApplication.from(MsRdvApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
