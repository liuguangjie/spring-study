package com.springframework.core.test.factory;

import com.spring.study.beans.Person;
import com.springframework.core.test.BaseApplicationContext;
import org.junit.Test;

/**
 * Created by free on 17-1-30.
 */
public class FactoryBeanTest extends BaseApplicationContext {
    @Override
    public String[] loadConfig() {
        return new String[]{"classpath:factory/spring-factory.xml"};
    }
    @Test
    public void testProxyFactoryBean(){
        Person person= applicationContext.getBean(Person.class);
        Person person1=applicationContext.getBean(Person.class);
        System.out.println(person1);
        System.out.println(person);
        person.show();
    }



}
