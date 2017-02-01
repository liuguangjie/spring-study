package com.spring.study.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * Created by free on 17-1-28.
 */
public class SpringBeforeAdvice implements MethodBeforeAdvice/*,AfterReturningAdvice,MethodInterceptor*/{
    /**
     * 前置通知
     * @param method method being invoked
     * @param args arguments to the method
     * @param target target of the method invocation. May be <code>null</code>.
     * @throws Throwable
     */
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("在目标方法之前执行 >>>> 目标方法名字: [ "+method.getName()+"() ]  目标类名字: [ "+target.getClass().getName()+" ]");
    }

    /**
     * 后置通知
     * @param returnValue the value returned by the method, if any
     * @param method method being invoked
     * @param args arguments to the method
     * @param target target of the method invocation. May be <code>null</code>.
     * @throws Throwable
     */
    /*public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        System.out.println("后置通知 在目标方法执行完之后执行 ");
    }*/

    /**
     * 环绕通知
     * @param methodInvocation
     * @return
     * @throws Throwable
     */
    /*public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        System.out.println("环绕通知 在方法执行之前 放行 才能执行余下的 方法 ...");
        Object obj=methodInvocation.proceed();
        return obj;
    }*/
}
