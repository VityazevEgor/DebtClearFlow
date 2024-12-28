package com.vityazev_egor.debt_clear_flow_server.Controllers.WebInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.vityazev_egor.debt_clear_flow_server.Models.DebtRepaymentRepo;
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
        var repayment = debtRepaymentRepo.findById(rpid).get();

        // в начале мы берём текущего студента на приеме и добовляем его к представлению
        qStudentRepo.findCurrentStudent(repayment, teacherLogin).ifPresentOrElse(
            student ->{
                mav.addObject("currentStudent", student);
            },
            () -> {
                if (qStudentRepo.findByDebtRepaymentAndIsAcceptedFalseOrderByIdAsc(repayment).size() == 0){
                    mav.addObject("errorMessage", "Все студенты были приняты");
                }
                else {
                    mav.addObject("errorMessage", "Вы ещё не начали приём студентов");
                }
            }
        );

        // ну и инфу о самой отработке лишней не будет
        mav.addObject("repayment", repayment);
        return mav;
    }

    @RequestMapping(value = "/reception/{rpid}/next", method = RequestMethod.GET)
    public ModelAndView getReceptionNext(@PathVariable Integer rpid, HttpSession session){
        String teacherLogin = (String)session.getAttribute("login");
        var repayment = debtRepaymentRepo.findById(rpid).get();
        // если текущий студент был то мы обновляем инфу о том что он был принят
        qStudentRepo.findCurrentStudent(repayment, teacherLogin).ifPresent(student ->{
            student.setIsAccepted(true);
            student.setIsInProcess(false);
            qStudentRepo.save(student);
        });

        qStudentRepo.findNextUnacceptedInQueue(repayment).ifPresent(student ->{
            student.setIsInProcess(true);
            student.setTeacherLogin(teacherLogin);
            qStudentRepo.save(student);
        });
        return new ModelAndView("redirect:/panel/reception/" + rpid.toString());
    }

}
