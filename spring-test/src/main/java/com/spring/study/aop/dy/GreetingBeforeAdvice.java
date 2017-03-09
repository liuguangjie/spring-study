package com.spring.study.aop.dy;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * Created by free on 17-3-6.
 */
public class GreetingBeforeAdvice implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {

        System.out.println("beforeAdvice  "+target.getClass().getName()+"."+method.getName());
    }
}
