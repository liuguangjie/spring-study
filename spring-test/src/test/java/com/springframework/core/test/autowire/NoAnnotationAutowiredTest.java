package com.springframework.core.test.autowire;

import com.spring.study.autowire.Employee;
import com.springframework.core.test.BaseApplicationContext;
import org.junit.Test;

/**
 * Created by free on 17-1-25.
 */
public class NoAnnotationAutowiredTest  extends BaseApplicationContext{

    public String[] loadConfig() {
        return new String[]{"classpath:autowire/spring-autowire.xml"};
    }

    /** 测试 根据类型 自动注入  */
    @Test
    public void testAutowiredbyType(){
        Employee employee=applicationContext.getBean(Employee.class);
        System.out.println(employee.getDepartment());
    }


}
