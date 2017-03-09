package com.spring.study.dao;

import com.spring.study.beans.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by free on 17-2-7.
 */
public class StudentService {

    private StudentDao studentDao;

    public List<Student> getStudents(){

        return studentDao.getStudentList();
    }

    public Student getStudentById(Integer id) {

        return  studentDao.getStudentById(id);
    }

    public void updatestudent(Student student) {
        studentDao.updatestudent(student);
    }

    public void setStudentDao(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    public void addStudent(Student student){
        studentDao.addStudent(student);
    }
}
