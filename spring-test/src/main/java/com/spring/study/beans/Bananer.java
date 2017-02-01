package com.spring.study.beans;

/**
 * Created by free on 17-1-3.
 */
public class Bananer extends Fruit {
    private String bananers=null;
    public Bananer () {
        System.out.println("I got a  fresh bananer");
    }

    public String getBananers() {
        return bananers;
    }

    public void setBananers(String bananers) {
        this.bananers = bananers;
    }

}
