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
        Person person= (Person) applicationContext.getBean("person0");
        person.show();
    }

    @Test
    public void testStaticFactoryMethod(){
        Person person= (Person) applicationContext.getBean("person1");
        person.show();

    }


}
