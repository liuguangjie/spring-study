package com.spring.study.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by free on 17-1-28.
 */
public class DynamicAop implements InvocationHandler {

    private HelloWorld helloWorld;

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        TimeHandler timeHandler = new TimeHandler();
        timeHandler.printTime();
        Object o = method.invoke(helloWorld, args);
        timeHandler.printTime();
        return o;
    }

    public Object createProxy() {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),
                this.helloWorld.getClass().getInterfaces(),
                this);
    }

    public HelloWorld getHelloWorld() {
        return helloWorld;
    }

    public void setHelloWorld(HelloWorld helloWorld) {
        this.helloWorld = helloWorld;
    }

}
