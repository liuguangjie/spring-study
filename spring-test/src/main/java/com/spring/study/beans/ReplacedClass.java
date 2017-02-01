package com.spring.study.beans;

import org.springframework.beans.factory.support.MethodReplacer;

import java.lang.reflect.Method;

/**
 * Created by free on 17-1-3.
 */
public class ReplacedClass implements MethodReplacer {
    public Object reimplement(Object obj, Method method, Object[] args) throws Throwable {
        System.out.println(obj);
        System.out.println(method.getName());
        System.out.println("I am replacer...");
        return null;
    }
}
