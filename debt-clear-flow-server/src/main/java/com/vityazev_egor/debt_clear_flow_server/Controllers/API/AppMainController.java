package com.vityazev_egor.debt_clear_flow_server.Controllers.API;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.vityazev_egor.debt_clear_flow_server.Models.*;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@RestController
@RequestMapping("/api")
public class AppMainController {

    @Autowired
    private QStudentRepo studentRepo;

    @Autowired
    private DebtRepaymentRepo repaymentRepo;

    @Autowired
    private TeacherRepo teacherRepo;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(AppMainController.class);

    @Data
    @NoArgsConstructor
    private static class EmailRequest {
        private String email;
    }

    @PostMapping("/findReceptions")
    public ResponseEntity<?> findReceptions(@RequestBody EmailRequest emailRequest) {
        try {
            List<QStudent> students = studentRepo.findByEmail(emailRequest.getEmail());
            List<DebtRepayment> result = students.stream()
                .map(QStudent::getDebtRepayment)
                .filter(Objects::nonNull)
                .toList();

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error finding receptions for email: " + emailRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve receptions"));
        }
    }

    @Data
    @NoArgsConstructor
    private static class FindPositionRequest {
        private String email;
        private Integer repaymentId;
    }

    // TODO переделать методы ниже, чтобы они принимали JSON
    @GetMapping("/findPosition")
    public ResponseEntity<Map<String, Object>> findPosition(@RequestBody FindPositionRequest findPositionRequest) {
        try {
            DebtRepayment repayment = repaymentRepo.findById(findPositionRequest.getRepaymentId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid repayment ID"));

            List<QStudent> studentsInQueue = studentRepo.findAllUnacceptedInQueue(repayment);
            int position = studentsInQueue.stream()
                .filter(s -> s.getEmail().equals(findPositionRequest.getEmail()))
                .findFirst()
                .map(s -> studentsInQueue.indexOf(s) + 1)
                .orElse(-1);

            if (position == -1) {
                return ResponseEntity.ok(Map.of(
                    "position", -1
                ));
            }

            return ResponseEntity.ok(Map.of(
                "position", position
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error finding position for email: " + findPositionRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve queue position"));
        }
    }

    @GetMapping("/getTeacherInfo")
    public ResponseEntity<Map<String, Object>> getTeacherInfo(@RequestParam String email, @RequestParam String repaymentId) {
        try {
            DebtRepayment repayment = repaymentRepo.findById(Integer.parseInt(repaymentId))
                .orElseThrow(() -> new IllegalArgumentException("Invalid repayment ID"));

            return studentRepo.findByEmailAndDebtRepayment(email, repayment)
                .stream()
                .findFirst()
                .map(QStudent::getTeacherLogin)
                .map(login -> teacherRepo.findByLogin(login)
                    .stream()
                    .findFirst()
                    .map(teacher -> {
                        Teacher secureTeacher = teacher.clone();
                        secureTeacher.setPassword(null);
                        return ResponseEntity.ok(Map.of(
                            "message", "Teacher found successfully",
                            "data", secureTeacher
                        ));
                    })
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Teacher not found"))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Student not found")));
                    
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error getting teacher info for email: " + email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve teacher information"));
        }
    }
}