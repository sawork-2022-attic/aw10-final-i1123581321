package cn.edu.nju.cs.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class MessageConfig {

    @Bean
    public MessagePublisher publisher(){
        return new MessagePublisher();
    }
}
