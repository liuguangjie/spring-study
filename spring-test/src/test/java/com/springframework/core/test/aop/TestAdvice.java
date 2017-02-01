package com.springframework.core.test.aop;

import com.spring.study.aop.AspectJAop;
import com.spring.study.aop.HelloWorld;
import com.spring.study.aop.HelloWorldImpl1;
import com.spring.study.aop.SpringBeforeAdvice;
import com.springframework.core.test.BaseApplicationContext;
import org.junit.Test;
import org.springframework.aop.BeforeAdvice;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.ProxyFactory;

/**
 * Created by free on 17-1-28.
 */
public class TestAdvice{

    /**
     * aop源代码还得好好看看 还不是很懂
     */

    @Test
    public void testBeforeAdvice(){

        HelloWorld helloWorld=new HelloWorldImpl1();

        BeforeAdvice beforeAdvice=new SpringBeforeAdvice();

        ProxyFactory proxyFactory=new ProxyFactory();
        //proxyFactory.setInterfaces(helloWorld.getClass().getInterfaces());
        proxyFactory.setTarget(helloWorld);

        proxyFactory.addAdvice(beforeAdvice);

        helloWorld = (HelloWorld) proxyFactory.getProxy();
        helloWorld.printHelloWorld();
        System.out.println(helloWorld);
    }

    @Test
    public void testAspectJ(){
        HelloWorld helloWorld=new HelloWorldImpl1();
        AspectJProxyFactory proxyFactory=new AspectJProxyFactory();
        proxyFactory.setTarget(helloWorld);
        proxyFactory.addAspect(AspectJAop.class);

        HelloWorld helloWorld1=proxyFactory.getProxy();
        helloWorld1.printHelloWorld();

    }
}
