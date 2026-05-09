package com.pharmacy.pos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * PharmacyPosApplication
 *
 * Entry point for the Pharmacy POS backend.
 *
 * Startup checklist:
 *  ✅ PostgreSQL running on localhost:5432 (or via Docker Compose)
 *  ✅ Redis running on localhost:6379
 *  ✅ application-dev.yml has correct DB credentials
 *
 * Default login after first startup:
 *  Email    : owner@pharmacy.com
 *  Password : Admin@1234
 *  ⚠️  You will be prompted to change the password on first login.
 */
@SpringBootApplication
public class PosApplication {

	public static void main(String[] args) {
		SpringApplication.run(PosApplication.class, args);
	}

}
