package com.vityazev_egor.debt_clear_flow_server.Controllers.WebInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.vityazev_egor.debt_clear_flow_server.Models.DebtRepaymentRepo;
import com.vityazev_egor.debt_clear_flow_server.Models.QStudent;
import com.vityazev_egor.debt_clear_flow_server.Models.QStudentRepo;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(path = "/panel/")
public class ReceptionController {

    @Autowired
    DebtRepaymentRepo debtRepaymentRepo;

    @Autowired
    QStudentRepo qStudentRepo;

    @RequestMapping(value = "/reception/{rpid}", method = RequestMethod.GET)
    public ModelAndView getReception(@PathVariable Integer rpid, HttpSession session){
        String teacherLogin = (String)session.getAttribute("login");
        var mav = new ModelAndView("reception");

        // в начале мы берём текущего студента на приеме и добовляем его к представлению
        QStudent currentStudent = qStudentRepo.findCurrentStudent(rpid, teacherLogin);
        mav.addObject("currentStudent", currentStudent);

        // ну и инфу о самой отработке лишней не будет
        mav.addObject("repayment", debtRepaymentRepo.findById(rpid).get());
        return mav;
    }

    @RequestMapping(value = "/reception/{rpid}/next", method = RequestMethod.GET)
    public ModelAndView getReceptionNext(@PathVariable Integer rpid, HttpSession session){
        String teacherLogin = (String)session.getAttribute("login");
        // если текущий студент был то мы обновляем инфу о том что он был принят
        QStudent currentStudent = qStudentRepo.findCurrentStudent(rpid, teacherLogin);
        if (currentStudent != null){
            currentStudent.setIsAccepted(true);
            currentStudent.setIsInProcess(false);
            qStudentRepo.save(currentStudent);
        }

        QStudent nextStudent = qStudentRepo.findFirstByDebtRepaymentIdAndIsAcceptedFalseAndIsInProcessFalseOrderByIdAsc(rpid);
        if (nextStudent != null){
            nextStudent.setIsInProcess(true);
            nextStudent.setTeacherLogin(teacherLogin);
            qStudentRepo.save(nextStudent);
        }

        return new ModelAndView("redirect:/panel/reception/" + rpid.toString());
    }

}
