package com.spring.study.aop;


/**
 * Created by free on 17-1-8.
 */
public class HelloWorldImpl1 implements HelloWorld {

    public void printHelloWorld()
    {
        System.out.println("Enter HelloWorldImpl1.printHelloWorld()");
    }

    public void doPrint()
    {
        System.out.println("Enter HelloWorldImpl1.doPrint()");
        return ;
    }

    public void printHelloWorld(String count){
        System.out.println("无返回值 ...");
    }
}
