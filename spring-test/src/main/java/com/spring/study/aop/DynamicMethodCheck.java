package com.spring.study.aop;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.DynamicMethodMatcherPointcut;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by free on 17-1-31.
 */
public class DynamicMethodCheck extends DynamicMethodMatcherPointcut {

    private static List<String> clientList=new ArrayList<String>();

    @Override
    public ClassFilter getClassFilter() {
        return new ClassFilter() {
            public boolean matches(Class<?> clazz) {
                System.out.println("调用getClassFilter()对 类"+clazz.getName()+"做静态检查");
                return HelloWorld.class.isAssignableFrom(clazz);
            }
        };
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        System.out.println("matches(method,clazz)对 方法"+method.getName()+"做静态检查");
        return "printHelloWorld".equals(method.getName());
    }

    public boolean matches(Method method, Class<?> targetClass, Object[] args) {
        System.out.println("matches(method,clazz,args)对 方法 "+method.getName()+"做动态检查 args="+args.length);
        return true;
    }
}
