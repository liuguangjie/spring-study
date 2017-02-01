package com.springframework.core.test.aop;

import com.spring.study.aop.HelloWorld;
import com.spring.study.aop.HelloWorldImpl1;
import com.spring.study.beans.Apple;
import com.springframework.core.test.BaseApplicationContext;
import org.junit.Test;

/**
 * Created by free on 17-1-8.
 */
public class TestAop  extends BaseApplicationContext {

    /**
     * aop源代码还得好好看看 还不是很懂
     */

    @Test
    public void testHelloWorld(){
        HelloWorld hw1 = (HelloWorld)applicationContext.getBean("helloWorldImpl");
        hw1.printHelloWorld();
        hw1.doPrint();
    }

    //private String configFile="aop/spring-aop.xml";
    private String configFile="aop/spring-aspectJ.xml";

    public String[] loadConfig() {
        return new String[]{configFile};
    }

    /**
     * 总结 spring的aop只为表达式匹配的类 创建代理
     */
    @Test
    public void testSpringAspectJ(){
        HelloWorldImpl1 hw1 = (HelloWorldImpl1)applicationContext.getBean("helloWorldImpl1");
        //hw1.printHelloWorld();
        hw1.printHelloWorld("xxxxxx");
        hw1.doPrint();

        Apple apple=applicationContext.getBean("apple", Apple.class);
        System.out.println(apple);
        System.out.println(new Object());
    }





}
