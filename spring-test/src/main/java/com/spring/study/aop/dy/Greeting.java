package com.spring.study.aop.dy;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by free on 17-3-6.
 */
public class Greeting extends StaticMethodMatcherPointcutAdvisor {


    @Override
    public boolean matches(Method method, Class<?> targetClass) {

        return "greetTo".equals(method.getName());
    }

    @Override
    public ClassFilter getClassFilter() {
        return createMatcheClass();
    }

    public ClassFilter createMatcheClass(){
        ClassFilter classFilter=new MatcheClass();
        return classFilter;
    }

    protected static List<Class> classFilters=new ArrayList<Class>(5);
    static {
        classFilters.add(Waiter.class);
        classFilters.add(NaiveWaiter.class);
        classFilters.add(Seller.class);
    }


    private static class MatcheClass implements ClassFilter{

        @Override
        public boolean matches(Class<?> clazz) {

            return classFilters.contains(clazz);
        }
    }
}
