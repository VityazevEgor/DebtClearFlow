package com.vityazev_egor.debt_clear_flow_server.Misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.vityazev_egor.debt_clear_flow_server.Models.Teacher;
import com.vityazev_egor.debt_clear_flow_server.Models.TeacherRepo;

import java.io.IOException;
import java.nio.file.*;

// выполнение необходимых операций перед запуском сервера
@Component
public class InitMisc implements CommandLineRunner{

    @Autowired
    private TeacherRepo teacherRepo;

    private final Logger logger = LoggerFactory.getLogger(InitMisc.class);

    private void createTestAccount(){
        if (teacherRepo.findByLogin("test").isEmpty()){
            var t = new Teacher();
            t.fullname = "test";
            t.login  =  "test";
            t.password   =  "test";
            t.imageName = "test";

            teacherRepo.save(t);
            logger.info("Added test account");
        }
        else{
            logger.info("Test account already exists");
        }
    }

    private void createAllFolders(){
        if (!Files.exists(Shared.csvFilesDirectory)){
            try {
                Files.createDirectory(Shared.csvFilesDirectory);
                logger.info("Created csvFiles directory");
            } catch (IOException e) {
                logger.error("Can't create csvDirectory", e);
            }
        }

        if (!Files.exists(Shared.imagesDirectory)){
            try {
                Files.createDirectory(Shared.imagesDirectory);
                logger.info("Created images directory");
            } catch (IOException e) {
                logger.error("Can't create imagesDirectory", e);
            }
        }
    }

    @Override
    public void run(String... args) throws Exception {
        createTestAccount();
        createAllFolders();
    }
    
}
