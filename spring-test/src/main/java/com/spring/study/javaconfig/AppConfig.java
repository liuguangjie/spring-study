package com.spring.study.javaconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by free on 17-1-25.
 */
@Configuration
public class AppConfig {


    @Bean(name = "helloConfig")
    public HelloConfig createConfig() {
        return new HelloConfigImpl();
    }

}
