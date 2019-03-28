package com.web.cloudapp.config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.web.cloudapp.CloudappApplication;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;

@Configuration
@ComponentScan(basePackageClasses = CloudappApplication.class, excludeFilters = @Filter({Controller.class, Configuration.class}))
public class awsConfig {
    @Bean
    public AmazonS3 s3() {
        DefaultAWSCredentialsProviderChain provider = new DefaultAWSCredentialsProviderChain();
        return AmazonS3ClientBuilder.standard()
                .withCredentials(provider)
                .build();
    }
}
