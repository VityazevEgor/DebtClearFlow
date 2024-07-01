package com.vityazev_egor.debt_clear_flow_server.Controllers.WebInterface.Tests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.vityazev_egor.debt_clear_flow_server.Models.Teacher;
import com.vityazev_egor.debt_clear_flow_server.Models.TeacherRepo;

@Component
public class Init implements CommandLineRunner {

    @Autowired
    private TeacherRepo teacherRepo;

    @Override
    public void run(String... args) throws Exception {
        if (teacherRepo.findByLogin("test").isEmpty()){
            var t = new Teacher();
            t.fullname = "test";
            t.login  =  "test";
            t.password   =  "test";
            t.imageName = "test";

            teacherRepo.save(t);
            System.out.println("Added test account");
        }
        else{
            System.out.println("Test account already exists");
        }
    }
    
}
