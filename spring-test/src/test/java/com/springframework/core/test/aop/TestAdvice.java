package com.springframework.core.test.aop;

import com.spring.study.aop.AspectJAop;
import com.spring.study.aop.HelloWorld;
import com.spring.study.aop.HelloWorldImpl1;
import com.spring.study.aop.SpringBeforeAdvice;
import com.spring.study.aop.dy.Seller;
import com.springframework.core.test.BaseApplicationContext;
import org.junit.Test;
import org.springframework.aop.BeforeAdvice;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import sun.misc.ProxyGenerator;

import javax.security.auth.Subject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by free on 17-1-28.
 */
public class TestAdvice {

    /**
     * aop源代码还得好好看看 还不是很懂
     */

    @Test
    public void testBeforeAdvice() {

        HelloWorld helloWorld = new HelloWorldImpl1();

        BeforeAdvice beforeAdvice = new SpringBeforeAdvice();

        ProxyFactory proxyFactory = new ProxyFactory();
        //proxyFactory.setInterfaces(helloWorld.getClass().getInterfaces());
        proxyFactory.setTarget(helloWorld);
        //proxyFactory.setProxyTargetClass(true);

        proxyFactory.addAdvice(beforeAdvice);

        helloWorld = (HelloWorld) proxyFactory.getProxy();
        helloWorld.printHelloWorld();
        //System.out.println(helloWorld);
        //createProxyClassFile();
    }

    @Test
    public void testProxyFactoryBean(){
        Resource resource=new ClassPathResource("aop/spring-aop.xml");
        XmlBeanFactory xmlBeanFactory=new XmlBeanFactory(resource);
        HelloWorld helloWorld= (HelloWorld) xmlBeanFactory.getBean("org.springframework.aop.framework.ProxyFactoryBean#0");
        helloWorld.printHelloWorld();

    }


    @Test
    public void testAspectJ(){
        HelloWorldImpl1 helloWorld=new HelloWorldImpl1();
        AspectJProxyFactory proxyFactory=new AspectJProxyFactory();
        proxyFactory.setTarget(helloWorld);
        proxyFactory.addAspect(AspectJAop.class);

        HelloWorldImpl1 helloWorld1=proxyFactory.getProxy();
        helloWorld1.printHelloWorld("sss");
    }
    @Test
    public void testSchema(){
        ApplicationContext ac=new ClassPathXmlApplicationContext("aop/spring-aspectJ.xml");
        HelloWorld helloWorld=ac.getBean(HelloWorld.class);
        helloWorld.printHelloWorld();


        System.out.println("======================");
        Seller seller=ac.getBean(Seller.class);
        seller.greetTo("xxxx");
    }


    private static void createProxyClassFile(){
        String name = "ProxySubject";
        byte[] data = ProxyGenerator.generateProxyClass(name,new Class[]{HelloWorld.class,SpringProxy.class,Advised.class});
        FileOutputStream out =null;
        try {
            out = new FileOutputStream(name+".class");
            System.out.println((new File("hello")).getAbsolutePath());
            out.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null!=out) try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
