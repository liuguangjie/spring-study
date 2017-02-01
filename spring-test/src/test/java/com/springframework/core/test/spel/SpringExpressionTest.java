package com.springframework.core.test.spel;

import com.spring.study.spel.Customer;
import com.springframework.core.test.BaseApplicationContext;
import org.junit.Test;

/**
 * Created by free on 17-1-27.
 */
public class SpringExpressionTest extends BaseApplicationContext{

    @Override
    public String[] loadConfig() {
        return new String[]{"classpath:spel/spring-spel.xml"};
    }

    @Test
    public void testXmlSpEl(){
        Customer customer=applicationContext.getBean("customerBean", Customer.class);
        System.out.println(customer.getItem());
        System.out.println(customer.getItemName());
    }
}
