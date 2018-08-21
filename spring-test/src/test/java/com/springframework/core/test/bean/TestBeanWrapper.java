package com.springframework.core.test.bean;

import com.spring.study.autowire.Department;
import com.spring.study.autowire.Employee;
import com.spring.study.beans.Student;
import org.junit.Test;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author ms.liu
 * ~~Email 18310693623@163.com
 * @Date 2018-06-09 下午3:41
 */
public class TestBeanWrapper {

    @Test
    public void beanWrapper() {

        System.out.println("ddddddddddddddd");


        /**
         * 关键的不是赋值流程 而是类似于  age[0].xxx赋值的问题
         * map['key'].name =
         */
        Student student = new Student();
        BeanWrapper beanWrapper = new BeanWrapperImpl(student);

        Map<String, Object> propertyMap = new HashMap<String, Object>();
        propertyMap.put("name", "zhangsan");
        propertyMap.put("age", "23");
        propertyMap.put("birthday", "1992-07-08 12:44:66");
        beanWrapper.registerCustomEditor(Date.class, new DateFormatTest());

        beanWrapper.setPropertyValues(propertyMap);


        System.out.println(student.getAge());
        System.out.println(student.getBirthday());


    }

    private static class DateFormatTest extends PropertyEditorSupport{

        private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        @Override
        public void setAsText(String text) throws IllegalArgumentException {

            try {
                super.setValue(dateFormat.parse(text));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void test1() {
        Employee employee = new Employee();

        BeanWrapper beanWrapper = new BeanWrapperImpl(employee);
        beanWrapper.setAutoGrowNestedPaths(true);
        Map<String, Object> propertyMap = new HashMap<String, Object>();

        // propertyMap.put("department.departmentName", "hhhhhhh");
         //propertyMap.put("depMap['test'].departmentName", "hhhhhhh");
        propertyMap.put("departments[0]", new Department());





        //beanWrapper.setAutoGrowCollectionLimit(Integer.MAX_VALUE);

        beanWrapper.setPropertyValues(propertyMap);


        //System.out.println(employee.getDepMap().get("test").getDepartmentName());

        System.out.println(employee.getDepartments().get(0));
    }
}
