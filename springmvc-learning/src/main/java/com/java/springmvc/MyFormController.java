/*package com.java.springmvc;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

// SimpleFormController 没了

public class MyFormController extends SimpleFormController {

    public RegisterStudentController() {
        this.setCommandClass(Student.class);
    }

    protected ModelAndView onSubmit(Object object, BindException arg1)
            throws Exception {
        Student stu = (Student) object;
        return new ModelAndView(getSuccessView(), "student", stu);
    }*/