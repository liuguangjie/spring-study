package com.spring.study.javaconfig;

import com.spring.study.beans.Happy;
import com.spring.study.beans.Mood;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by free on 17-1-27.
 */
@Configuration
public class HappyConfig {

    @Bean(name = "happy",initMethod = "init")
    public Mood createMood(){
        return new Happy();
    }
}
