package com.team.RecipeRadar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class RecipeRadarApplication {
	static {
		System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
	}
	public static void main(String[] args) {
		SpringApplication.run(RecipeRadarApplication.class, args);
	}

	@PostConstruct
	public void start(){
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

}
