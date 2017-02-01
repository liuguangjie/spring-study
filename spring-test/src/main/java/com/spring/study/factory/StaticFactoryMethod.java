package com.spring.study.factory;

import com.spring.study.beans.Person;

/**
 * Created by free on 17-1-30.
 */
public class StaticFactoryMethod {


    public static Person getPerson(){
        return new Person();
    }

}
