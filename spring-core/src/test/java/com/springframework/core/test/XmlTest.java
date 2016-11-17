package com.springframework.core.test;

import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.*;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2016/11/15.
 */
public class XmlTest {


    /**




     */


    private String config="classpath:spring-context.xml";
    @Test
    public void testSpringXml(){
        /*ClassPathResource resource=new ClassPathResource(config);

        System.out.println(resource.exists());*/

        ResourceLoader resourceLoader=new DefaultResourceLoader();
        Resource resource1=resourceLoader.getResource(config);
        //System.out.println(resource);
        //ApplicationContext applicationContext=new ClassPathXmlApplicationContext(config);
        //System.out.println(applicationContext);
        XmlBeanFactory beanFactory=new XmlBeanFactory(resource1);

        Object object=beanFactory.getBean("ss");
        System.out.println(object);
    }

    @Test
    public void testUrl(){
        try {
            URL url=new URL("");
            Resource resource=new UrlResource(config);
            System.out.println(resource);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSubString() throws IOException {

        /**
         第一波
        String s="classpath:ssss";
        System.out.println(s.substring("classpath:".length()));
        System.out.println(s);
        */

        /**
         第二波
         PathMatchingResourcePatternResolver patternResolver=new PathMatchingResourcePatternResolver();
         System.out.println(patternResolver);
         String locationPattern = "classpath*:"+config+",classpath*:spring-context-test.xml";
         Resource[] resources=patternResolver.getResources(locationPattern);

         System.out.println(resources.length);
         */

        /**
         第三波

         */
        Map<String,String> map = new ConcurrentHashMap<String,String>();

        map.put("sss","sss");
        System.out.println(map.get("sss"));




    }


    /**
     * 位运算
     */
    @Test
    public void testBitSum(){
        //101000
        System.out.println(10 >> 2);

        /**
        2 10   0
        2  5   1
        2  2   0
        2  1   1
         */

    }

}
