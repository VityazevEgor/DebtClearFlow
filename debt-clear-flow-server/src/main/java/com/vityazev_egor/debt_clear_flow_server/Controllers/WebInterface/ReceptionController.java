package com.vityazev_egor.debt_clear_flow_server.Controllers.WebInterface;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.vityazev_egor.debt_clear_flow_server.Models.DebtRepayment;
import com.vityazev_egor.debt_clear_flow_server.Models.DebtRepaymentRepo;
import com.vityazev_egor.debt_clear_flow_server.Models.QStudentRepo;
import com.vityazev_egor.debt_clear_flow_server.Models.DebtRepayment.RepaymentStatus;

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
        var repayment = debtRepaymentRepo.findById(rpid).get();
        var mav = new ModelAndView("reception");
        long minutesLeft = Duration.between(LocalDateTime.now(), repayment.getEndTime()).toMinutes();
        if (minutesLeft < 0) {
            minutesLeft = 0;
        }
        mav.addObject("timeLeft", minutesLeft);
        mav.addObject("queueSize", qStudentRepo.countStudenstLeft(repayment));

        // в начале мы берём текущего студента на приеме и добовляем его к представлению
        qStudentRepo.findCurrentStudent(repayment, teacherLogin).ifPresentOrElse(
            student ->{
                mav.addObject("currentStudent", student);
            },
            () -> {
                if (repayment.getStatus() == RepaymentStatus.CLOSED){
                    mav.addObject("errorMessage", "Приём студентов завершён");
                    mav.addObject("isClosed", true);
                }
                else if (qStudentRepo.findNextUnacceptedInQueue(repayment).isEmpty()){
                    mav.addObject("errorMessage", "Все студенты были приняты");
                    mav.addObject("isClosed", true);
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

    private ModelAndView getNextStudent(DebtRepayment repayment, String teacherLogin){
        // если препродаватель начал принимать студентов, то мы открываем отработку
        if (repayment.getStatus() == RepaymentStatus.WAITING){
            repayment.setStatus(RepaymentStatus.OPEN);
            debtRepaymentRepo.save(repayment);
        }
        qStudentRepo.findNextUnacceptedInQueue(repayment).ifPresent(student ->{
            student.setIsInProcess(true);
            student.setTeacherLogin(teacherLogin);
            qStudentRepo.save(student);
        });
        return new ModelAndView("redirect:/panel/reception/" + repayment.getId().toString());
    }

    @RequestMapping(value = "/reception/{rpid}/next", method = RequestMethod.GET)
    public ModelAndView getReceptionNext(@PathVariable Integer rpid, HttpSession session){
        String teacherLogin = (String)session.getAttribute("login");
        var repayment = debtRepaymentRepo.findById(rpid).get();

        return getNextStudent(repayment, teacherLogin);
    }

    @RequestMapping(value = "/reception/{rpid}/accept", method = RequestMethod.GET)
    public ModelAndView acceptStudent(@PathVariable Integer rpid, HttpSession session) {
        String teacherLogin = (String)session.getAttribute("login");
        var repayment = debtRepaymentRepo.findById(rpid).get();

        qStudentRepo.findCurrentStudent(repayment, teacherLogin).ifPresent(student ->{
            student.setIsAccepted(true);
            student.setIsInProcess(false);
            qStudentRepo.save(student);
        });

        return getNextStudent(repayment, teacherLogin);
    }

    @RequestMapping(value = "/reception/{rpid}/reject", method = RequestMethod.GET)
    public ModelAndView rejectStudent(@PathVariable Integer rpid, HttpSession session) {
        String teacherLogin = (String)session.getAttribute("login");
        var repayment = debtRepaymentRepo.findById(rpid).get();

        qStudentRepo.findCurrentStudent(repayment, teacherLogin).ifPresent(student ->{
            student.setIsRejected(true);
            student.setIsInProcess(false);
            qStudentRepo.save(student);
        });

        return getNextStudent(repayment, teacherLogin);
    }

    @RequestMapping(value = "/reception/{rpid}/sendToEnd", method = RequestMethod.GET)
    public ModelAndView sendToEndStudent(@PathVariable Integer rpid, HttpSession session) {
        String teacherLogin = (String)session.getAttribute("login");
        var repayment = debtRepaymentRepo.findById(rpid).get();

        qStudentRepo.findCurrentStudent(repayment, teacherLogin).ifPresent(student ->{
            var copy = student.createCleanCopy();
            qStudentRepo.delete(student);
            qStudentRepo.save(copy);
        });

        return getNextStudent(repayment, teacherLogin);
    }

    // продливаем время приёма студентов
    @RequestMapping(value = "/reception/{rpid}/extendTime", method = RequestMethod.GET)
    public ModelAndView extendReceptionTime(@PathVariable Integer rpid, HttpSession session) {
        var repayment = debtRepaymentRepo.findById(rpid).get();
        repayment.setEndTime(LocalDateTime.now().plusMinutes(20));
        debtRepaymentRepo.save(repayment);
        return new ModelAndView("redirect:/panel/reception/" + repayment.getId().toString());
    }

    @RequestMapping(value = "/reception/{rpid}/endReception", method = RequestMethod.GET)
    public ModelAndView endReception(@PathVariable Integer rpid, HttpSession session) {
        var repayment = debtRepaymentRepo.findById(rpid).get();
        repayment.setEndTime(LocalDateTime.now());
        repayment.setStatus(RepaymentStatus.CLOSED);
        debtRepaymentRepo.save(repayment);
        return new ModelAndView("redirect:/panel/myRepayments");
    }

}
