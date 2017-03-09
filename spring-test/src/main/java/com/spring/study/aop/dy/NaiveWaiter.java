package com.spring.study.aop.dy;

/**
 * Created by free on 17-3-5.
 */
public class NaiveWaiter implements  Waiter {
    @Override
    public void greetTo(String name) {
        System.out.println("greet to "+name+"...");
    }

    @Override
    public void serveTo(String name) {
        System.out.println("serving "+name+"...");
    }
}
