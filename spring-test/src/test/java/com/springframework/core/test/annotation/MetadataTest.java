package com.springframework.core.test.annotation;

import com.spring.study.beans.Happy;
import com.springframework.core.test.BaseApplicationContext;
import com.springframework.core.test.BaseTest;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by free on 17-1-6.
 */
public class MetadataTest  extends BaseApplicationContext {

    private String configFile="annotation/spring-annotation.xml";

    public String[] loadConfig() {
        return new String[]{configFile};
    }


    @Test
    public void testAnnotation(){

        Happy happy=applicationContext.getBean("happy2",Happy.class);
        System.out.println(happy.getHi());
        happy.say();

    }


}
