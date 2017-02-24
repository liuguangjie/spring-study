package com.spring.study.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by free on 16-12-8.
 * 开心的情绪
 */
/*@Service("com.happy")*/
public class Happy  extends Mood{

    @Autowired
    private ConnectionExample connectionExample;

    private String hi=null;


    public Happy(){}

    public Happy(String happy){
        this.hi=happy;
    }


    public String getHi() {
        return hi;
    }

    public void setHi(String hi) {
        this.hi = hi;
    }

    // 初始化 方法
    public void init(){
        System.out.println("com.spring.study.beans.Happy.init()....");
    }

    // 销毁方法
    public void destroy(){
        System.out.println("com.spring.study.beans.Happy.destroy()....");
    }


    @PostConstruct
    public void postConstruct(){
        System.out.println("com.spring.study.beans.Happy.postConstruct().... 注解@PostConstruct 执行");
    }


    @PreDestroy
    public void preDestroy(){
        System.out.println("com.spring.study.beans.Happy.postConstruct().... 注解@PreDestroy 执行");
    }



    public void setConnectionExample(ConnectionExample connectionExample) {
        this.connectionExample = connectionExample;
    }

    public void say(){
        System.out.println("com.spring.study.beans.Happy.say()...");
        connectionExample.execConnection();
    }

}
