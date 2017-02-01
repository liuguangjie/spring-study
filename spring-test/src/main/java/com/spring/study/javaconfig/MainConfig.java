package com.spring.study.javaconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by free on 17-1-27.
 */
@Configuration
@Import({HappyConfig.class, AppConfig.class})
public class MainConfig {
}
