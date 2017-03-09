package com.springframework.core.test.property;

import com.spring.study.beans.Bananer;
import com.spring.study.collection.InjectionCollections;
import com.spring.study.property.BeanWrapList;
import org.junit.Test;
import org.springframework.beans.*;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by free on 17-3-7.
 */
public class SpringPropertyInjection {

    @Test
    public void testPropertyInjection(){

        Bananer bananer =new Bananer();
        MutablePropertyValues mvp=new MutablePropertyValues();
        mvp.addPropertyValue(new PropertyValue("bananers","xxxxxxx"));
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bananer);
        bw.setPropertyValues(mvp);

        System.out.println(bananer.getBananers());

    }


    @Test
    public void testBeanWrapList() throws IOException {
        BeanWrapList beanWrapList=new BeanWrapList();
        beanWrapList.getBananerList().add(new Bananer());

        Map<String,Bananer> map=new HashMap<String, Bananer>();

        map.put("bananerKey",new Bananer());

        beanWrapList.setBananerMap(map);

        MutablePropertyValues mvp=new MutablePropertyValues();
        mvp.addPropertyValue(new PropertyValue("list","xxxxxxx"));
        mvp.addPropertyValue("bananerList[0].bananers","hhh");
        mvp.addPropertyValue("bananerMap['bananerKey'].bananers","你好呀");
        /*mvp.addPropertyValue(new PropertyValue("num",new int[8]));
        mvp.addPropertyValue(new PropertyValue("num[2]","88"));*/
        /*mvp.addPropertyValue(new PropertyValue("bananer",new Bananer()));
        mvp.addPropertyValue(new PropertyValue("bananer.bananers","test property"));
        mvp.addPropertyValue(new PropertyValue("resource","aop/spring-aop.xml"));
        mvp.addPropertyValue(new PropertyValue("date","1992-07-08 12:12:36"));*/
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(beanWrapList);
        bw.registerCustomEditor(Date.class,new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
        bw.setPropertyValues(mvp);
        //DefaultListableBeanFactory
        //CustomEditorConfigurer
        Object obj=bw.getPropertyValue("bananerMap");

        System.out.println(obj);
        for (Bananer bananer : beanWrapList.getBananerList()){


            System.out.println(bananer.getBananers());
        }

    }

    @Test
    public void testDateEditor(){
        ApplicationContext ac=new ClassPathXmlApplicationContext("property/spring-property.xml");
        BeanWrapList beanWrapList=ac.getBean(BeanWrapList.class);
        System.out.println(beanWrapList.getDate());
    }

    @Test
    public void testValue(){
        Resource resource=new ClassPathResource("property/spring-property.xml");
        XmlBeanFactory xmlBeanFactory=new XmlBeanFactory(resource);
        InjectionCollections injectionCollections=xmlBeanFactory.getBean(InjectionCollections.class);

        System.out.println(injectionCollections.getList().get(0));
    }
}
