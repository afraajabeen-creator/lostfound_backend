package com.campus.lostfound;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Campus Lost and Found Management System Backend.
 * 
 * VIVA TIP:
 * @SpringBootApplication is a convenience annotation that combines:
 * 1. @Configuration: Tags the class as the source of bean definitions.
 * 2. @EnableAutoConfiguration: Tells Spring Boot to configure beans based on classpath settings.
 * 3. @ComponentScan: Tells Spring to scan and discover beans in the 'com.campus.lostfound' package.
 */
@SpringBootApplication
public class LostFoundApplication {

    public static void main(String[] args) {
        SpringApplication.run(LostFoundApplication.class, args);
        System.out.println("==================================================================");
        System.out.println(">>> Campus Lost & Found Backend is running successfully! <<<");
        System.out.println("==================================================================");
    }
}
