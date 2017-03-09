package com.spring.study.beans;

import java.util.Date;

/**
 * Created by free on 17-2-2.
 */

public class Student {
    private int id;
    private int age;
    private String name;
    private Date birthday;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Student { " +
                "id=" + id +
                ", age=" + age +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                " }";
    }
}
