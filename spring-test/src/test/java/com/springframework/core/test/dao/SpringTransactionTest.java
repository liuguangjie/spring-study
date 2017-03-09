package com.springframework.core.test.dao;

import com.spring.study.beans.Student;
import com.spring.study.dao.StudentDao;
import com.spring.study.dao.StudentService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.util.Date;
import java.util.List;

/**
 * Created by free on 17-3-8.
 */
public class SpringTransactionTest {


    @Test
    public void testLocal(){

    }

    @Test
    public void transactionProxyFactoryBean(){
        ClassPathXmlApplicationContext ac=new ClassPathXmlApplicationContext("dao/spring-dao.xml");
        StudentService studentService=ac.getBean("studentService",StudentService.class);
        //StudentDao studentDao=ac.getBean("studentService",StudentDao.class);
        Student student=new Student();
        student.setId(7);
        student.setAge(4);
        student.setBirthday(new Date());
        student.setName("啊哈测试");
        //studentService.addStudent(student);
        //studentDao.updatestudent(student);
        List<Student> students=studentService.getStudents();
        System.out.println(students);

        ac.close();
    }


}
