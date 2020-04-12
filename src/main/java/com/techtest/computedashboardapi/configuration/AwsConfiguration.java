package com.techtest.computedashboardapi.configuration;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;

@Configuration
public class AwsConfiguration {

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public Ec2Client ec2Client(Region region) {
        return Ec2Client.builder().credentialsProvider(DefaultCredentialsProvider.create())
                .region(region)
                .build();
    }

}
