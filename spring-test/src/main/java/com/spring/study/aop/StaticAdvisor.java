package com.spring.study.aop;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;

import java.lang.reflect.Method;

/**
 * Created by free on 17-1-30.
 */
public class StaticAdvisor extends StaticMethodMatcherPointcutAdvisor {

    public boolean matches(Method method, Class<?> targetClass) {
        return "printHelloWorld".equalsIgnoreCase(method.getName());
    }

    @Override
    public ClassFilter getClassFilter() {
        return new ClassFilter(){

            public boolean matches(Class<?> clazz) {

                return HelloWorld.class.isAssignableFrom(clazz);
            }
        };
    }
}
