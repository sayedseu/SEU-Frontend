package com.sayed.seu.forntend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class ForntendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForntendApplication.class, args);
    }

}
