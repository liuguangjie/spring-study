package com.spring.study.aop;


import org.aspectj.lang.JoinPoint;


/**
 * Created by free on 17-1-8.
 */
public class TimeHandler {

    public void printTime() {
        System.out.println("CurrentTime = " + System.currentTimeMillis());
    }

    public void aopAfter(JoinPoint point){

        System.out.println("TimeHandler.aopAfter()...");
    }

    public void aopBefore(JoinPoint point){
        System.out.println("目标 方法 "+point.getSignature().getName());
        System.out.println("TimeHandler.aopBefore()...");
    }

    public Object aopAround(){

        return null;
    }

}
