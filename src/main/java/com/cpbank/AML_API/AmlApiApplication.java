package com.cpbank.AML_API;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AmlApiApplication {

	static {
		System.setProperty("jsse.enableSNIExtension", "false");
		System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");
	}

	public static void main(String[] args) {
		SpringApplication.run(AmlApiApplication.class, args);
	}
}
