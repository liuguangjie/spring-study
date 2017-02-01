package com.spring.study.beans;

/**
 * Created by free on 17-1-3.
 */
public class Apple extends Fruit {
    public Apple() {
        System.out.println("I got a fresh apple");
    }

    private Fruit fruit=null;

    public Apple(String iDCode,Fruit fruit){
        this.iDCode=iDCode;
        this.fruit=fruit;
    }

    private String iDCode;

    public String getIDCode() {
        return iDCode;
    }

    public void setIDCode(String iDCode) {
        this.iDCode = iDCode;
    }

    public Fruit getFruit() {
        return fruit;
    }

    public void setFruit(Fruit fruit) {
        this.fruit = fruit;
    }

}
