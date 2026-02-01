package com.invoice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MlmApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(MlmApplication.class, args);
	}

}
