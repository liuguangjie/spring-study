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

    public String printHelloWorld(String count){

        return count+"asdasd";
    }
}
