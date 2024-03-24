package com.bluewhaletech.Ourry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.bluewhaletech.Ourry.properties")
public class OurryApplication {
	public static void main(String[] args) {
		SpringApplication.run(OurryApplication.class, args);
	}
}