package com.techtest.computedashboardapi.configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AwsConfiguration {

    @Value("${aws.key.access}")
    public String AWS_ACCESS_KEY;

    @Value("${aws.key.secret}")
    public String AWS_SECRET_KEY;

    @Bean
    public AWSCredentials awsCredentials() {
        return new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public AmazonEC2 ec2Client(String region) {
        return AmazonEC2ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials()))
                .withRegion(region)
                .build();
    }

}
