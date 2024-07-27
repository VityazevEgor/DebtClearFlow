package com.vityazev_egor.debt_clear_flow_server.Controllers.WebInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.vityazev_egor.debt_clear_flow_server.Models.Teacher;
import com.vityazev_egor.debt_clear_flow_server.Models.TeacherRepo;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(path = "/panel/account/")
public class AccountController {

    @Autowired
    private TeacherRepo teacherRepo;

    private final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private Teacher getTeacherFromSession(HttpSession session){
        Integer id = (Integer) session.getAttribute("id");
        return teacherRepo.findById(id).orElse(null);
    }

    @GetMapping("/")
    public ModelAndView getAccountView(HttpSession session){
        Teacher currentUser = getTeacherFromSession(session);
        
        logger.info("Got teacher with id = "+ currentUser.id);
        return new ModelAndView("account", "currentUser", currentUser);
    }

    @PostMapping("/changePassword")
    public ModelAndView changePassword(HttpSession session, @RequestParam String currentPassword, @RequestParam String newPassword){
        Teacher currentUser = getTeacherFromSession(session);
        ModelAndView modelAndView = new ModelAndView("account", "currentUser", currentUser);

        logger.info("Got rs in 'changePassword' - " + currentPassword + ":" + newPassword);
        if(currentUser.password.equals(currentPassword)){
            currentUser.password = newPassword;
            teacherRepo.save(currentUser);
            logger.info("Password changed for user - " + currentUser.login);
            modelAndView.addObject("message", "Пароль был успешно изменён!");
            return modelAndView;
        }
        else{
            logger.warn("Current password is not correct for user - "+ currentUser.login);
            modelAndView.addObject("errorMessage", "Введеённый пароль не совпадает с текущим");
            return modelAndView;
        }
    }
}
