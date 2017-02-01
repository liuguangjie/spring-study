package com.springframework.core.test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by free on 17-1-27.
 */
public class LoadClassFilterApplicationContext extends AnnotationConfigApplicationContext {

    public LoadClassFilterApplicationContext() {
        super();
    }



    public void registerConfigClass(Class... clazz){
        register(clazz);
        refresh();
    }

    public <T> T getBean(Class<T> requiredType,Class... configClasses){
        registerConfigClass(configClasses);
        return getBean(requiredType);
    }



}
