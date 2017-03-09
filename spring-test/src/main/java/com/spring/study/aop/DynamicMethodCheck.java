package com.spring.study.aop;

import com.spring.study.aop.dy.NaiveWaiter;
import com.spring.study.aop.dy.Seller;
import com.spring.study.aop.dy.Waiter;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.DynamicMethodMatcherPointcut;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by free on 17-1-31.
 */
public class DynamicMethodCheck extends DynamicMethodMatcherPointcut {

    private static Set<String> methodNames=new HashSet<String>();
    protected static List<Class> classFilters=new ArrayList<Class>();


    static {
        classFilters.add(Waiter.class);
        classFilters.add(NaiveWaiter.class);
        classFilters.add(Seller.class);
        classFilters.add(HelloWorld.class);
        classFilters.add(HelloWorldImpl1.class);
        classFilters.add(HelloWorldImpl2.class);
        for ( Class clazz : classFilters){
            Method[] methodArray=ReflectionUtils.getAllDeclaredMethods(clazz);
            synchronized (methodArray){
                for ( Method method:methodArray){
                    methodNames.add(method.getName());
                }
            }


        }
    }
    @Override
    public ClassFilter getClassFilter() {
        return createClassFilter();
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        System.out.println("matches(method,clazz)对 方法"+method.getName()+"做静态检查");
        return methodNames.contains(method.getName());
    }

    public boolean matches(Method method, Class<?> targetClass, Object[] args) {
        System.out.println("matches(method,clazz,args)对 方法 "+method.getName()+"做动态检查 args="+args.length);
        return methodNames.contains(method.getName());
    }

    public ClassFilter createClassFilter(){
        return new MatcheClass();
    }

    private static class MatcheClass implements ClassFilter{

        @Override
        public boolean matches(Class<?> clazz) {
            System.out.println("调用getClassFilter()对 类"+clazz.getName()+"做静态检查");
            return classFilters.contains(clazz);
        }
    }
}
