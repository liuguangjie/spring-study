package com.spring.study.postprocessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Created by free on 17-1-5.
 */
public class TestBeanPostProcessor  implements BeanPostProcessor{
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("com.spring.study.postprocessor.TestBeanPostProcessor.postProcessBeforeInitialization() #########");

        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("com.spring.study.postprocessor.TestBeanPostProcessor.postProcessAfterInitialization() #########");

        return bean;
    }
}
