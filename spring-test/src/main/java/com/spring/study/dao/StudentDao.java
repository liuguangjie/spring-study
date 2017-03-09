package com.spring.study.dao;


import com.spring.study.beans.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by free on 17-2-7.
 */
public class StudentDao extends JdbcDaoSupport{


    public List<Student> getStudentList(){
        return getJdbcTemplate().query("select f_id as id ,f_name as name, f_age as age,f_birthday as birthday  from t_student",new BeanPropertyRowMapper<Student>(){
            @Override
            public Student mapRow(ResultSet rs, int rowNumber) throws SQLException {

                return convertObject(rs);
            }
        });
    }

    public Student convertObject(ResultSet rs)throws SQLException{
        Student stu=new Student();
        int id=rs.getInt("id");
        String name =rs.getString("name");
        int age=rs.getInt("age");
        Date date=rs.getDate("birthday");
        stu.setId(id);
        stu.setAge(age);
        stu.setBirthday(date);
        stu.setName(name);
        return stu;
    }

    public Student getStudentById(Integer id) {

        return getJdbcTemplate().queryForObject("select f_id as id ,f_name as name, f_age as age,f_birthday as birthday  from t_student where f_id=" +id ,new BeanPropertyRowMapper<Student>(){
            @Override
            public Student mapRow(ResultSet rs, int rowNumber) throws SQLException {
                return convertObject(rs);
            }
        });
    }

    public void updatestudent(final  Student student) {
        getJdbcTemplate().update("update t_student set f_name=?,f_age=?,f_birthday=? where f_id=?", new PreparedStatementSetter(){
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1,student.getName());
                ps.setInt(2,student.getAge());
                ps.setDate(3, new java.sql.Date(student.getBirthday().getTime()));
                ps.setInt(4,student.getId());
            }
        });
    }

    public void addStudent(final  Student student){


        getJdbcTemplate().execute("insert into t_student(f_name,f_age,f_birthday) values('"+student.getName()+"',"+student.getAge()+",now())");

    }
}