package com.vlingampally.ITMD544_SongLyric;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class SongLyricApplication {

	public static void main(String[] args) {
		SpringApplication.run(SongLyricApplication.class, args);
	}

}
