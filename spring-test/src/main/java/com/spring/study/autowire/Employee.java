package com.spring.study.autowire;

/**
 * Created by free on 17-1-25.
 */
public class Employee {

    // 员工工号
    private String employeeNumber;

    private String name ;

    private Department department;

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
