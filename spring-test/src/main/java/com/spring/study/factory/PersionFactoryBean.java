package com.spring.study.factory;

import com.spring.study.beans.Person;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by free on 17-1-30.
 */
public class PersionFactoryBean implements FactoryBean<Person>{


    public Person getObject() throws Exception {

        return new Person();
    }

    public Class<?> getObjectType() {
        return Person.class;
    }

    public boolean isSingleton() {
        return true;
    }
}
