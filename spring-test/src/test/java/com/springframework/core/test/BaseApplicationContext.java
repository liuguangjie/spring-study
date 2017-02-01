package com.springframework.core.test;

import org.junit.After;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by free on 17-1-8.
 */
public abstract class BaseApplicationContext implements  LoadConfig{
    protected ApplicationContext applicationContext=null;

    @Before
    public void init(){
        System.setProperty("aop.config","aop/spring-aop.xml");
        applicationContext=new ClassPathXmlApplicationContext(loadConfig());
    }


    @After
    public void destroy(){

        AbstractApplicationContext classPathXmlApplicationContext =(AbstractApplicationContext)applicationContext;
        classPathXmlApplicationContext.destroy();
    }


    public String[] loadConfig() {
        throw new RuntimeException("no xml settings file");
    }
}
