package com.paypalpay.paypalpayment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@ComponentScan
@Configuration
public class PaypalpaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaypalpaymentApplication.class, args);
	}

}
