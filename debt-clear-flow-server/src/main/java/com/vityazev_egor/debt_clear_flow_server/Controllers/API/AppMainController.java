package com.vityazev_egor.debt_clear_flow_server.Controllers.API;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import com.vityazev_egor.debt_clear_flow_server.Models.*;
import java.util.*;


@RestController
@RequestMapping(path = "/api/")
public class AppMainController {

    @Autowired
    private QStudentRepo studentRepo;

    @Autowired
    private DebtRepaymentRepo repaymentRepo;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(AppMainController.class);

    // получить список отработок на которые записан ученик
    @PostMapping("findReceptions")
    public List<DebtRepayment> findReceptions(@RequestPart String email) {
        List<QStudent> students = studentRepo.findByEmail(email);
        List<DebtRepayment> result = new ArrayList<>();

        for (QStudent student : students) {
            DebtRepayment r = repaymentRepo.findById(student.getDebtRepaymentId()).orElse(null);
            if (r != null){
                result.add(r);
            }
        }

        return result;
    }

    @PostMapping("findPosition")
    public Integer findPosition(@RequestPart String email, @RequestPart String repaymentId){
        Integer position = -1;
        // текущая очередь
        List<QStudent> que = studentRepo.findByDebtRepaymentIdAndIsAcceptedFalseOrderByIdAsc(Integer.parseInt(repaymentId));
        QStudent student = que.stream().filter(s-> s.getEmail().equals(email)).findFirst().orElse(null);
        if (student == null){
            // если студент прошёл очередь то возвращаем -1
            return position;
        }
        
        return que.indexOf(student) + 1;
    }
    
}
