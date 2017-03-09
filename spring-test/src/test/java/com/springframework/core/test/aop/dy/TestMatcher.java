package com.springframework.core.test.aop.dy;

import com.spring.study.aop.dy.NaiveWaiter;
import com.spring.study.aop.dy.Seller;
import com.spring.study.aop.dy.WaiterDelegate;
import org.junit.Test;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Created by free on 17-3-6.
 */
public class TestMatcher {

    @Test
    public void testStaticMatcher(){

        Resource resource=new ClassPathResource("aop/spring-aop-dynamic.xml");
        XmlBeanFactory xmlBeanFactory=new XmlBeanFactory(resource);
        Seller seller= (Seller) xmlBeanFactory.getBean("seller2");
        NaiveWaiter waiter= (NaiveWaiter) xmlBeanFactory.getBean("waiter");
        WaiterDelegate waiterDelegate= (WaiterDelegate) xmlBeanFactory.getBean("waiterDelegate");
        seller.greetTo("嘻嘻");
        waiter.greetTo("哈哈");

        waiterDelegate.service("ll");
        // BeanNameAutoProxyCreator


    }

    @Test
    public void testBeanNameAutoProxyCreator(){
        ApplicationContext ac=new ClassPathXmlApplicationContext("aop/spring-aop-beanName.xml");

        Seller seller= (Seller) ac.getBean("seller");
        seller.greetTo("xxx");

    }
}
