package com.spring.study.dao;

import com.spring.study.beans.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by free on 17-2-7.
 */
@Transactional(readOnly = true)
public class StudentService {

    private StudentDao studentDao;
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Student> getStudents(){

        return studentDao.getStudentList();
    }

    public Student getStudentById(Integer id) {

        return  studentDao.getStudentById(id);
    }
    @Transactional(readOnly = false,propagation = Propagation.REQUIRED)
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
