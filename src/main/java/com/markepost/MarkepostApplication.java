package com.markepost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // batch 기능 활성화
@SpringBootApplication
public class MarkepostApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarkepostApplication.class, args);
	}

}
