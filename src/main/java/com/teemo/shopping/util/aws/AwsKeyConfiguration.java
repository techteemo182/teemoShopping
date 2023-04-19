package com.teemo.shopping.util.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsKeyConfiguration {
    @Value("${aws.access-key}")
    private String awsAccesskey;
    @Value("${aws.secret-key}")
    private String awsSecretKey;
    @Bean
    AWSCredentials awsCredentials() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(
            awsAccesskey,
            awsSecretKey
        );
        return awsCredentials;
    }
}
