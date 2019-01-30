package com.web.cloudapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.web.cloudapp.Repository")
public class CloudappApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudappApplication.class, args);
    }

}

