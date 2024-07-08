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
    public List<DebtRepayment> postMethodName(@RequestPart String email) {
        logger.info("Gor request to 'findReceptions'");
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
    
}
