package com.springframework.core.test.event;

import com.spring.study.event.ContentEvent;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by free on 17-4-9.
    spring 事件机制 test
 */
public class SpringEventTest {

    @Test
    public void testEvent(){
        ApplicationContext ac=new ClassPathXmlApplicationContext("event/spring-event.xml");
        ac.publishEvent(new ContentEvent(" 打球 "));
    }
}
