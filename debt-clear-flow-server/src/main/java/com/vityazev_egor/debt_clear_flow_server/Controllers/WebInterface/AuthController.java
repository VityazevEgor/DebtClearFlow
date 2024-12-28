package com.vityazev_egor.debt_clear_flow_server.Controllers.WebInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.vityazev_egor.debt_clear_flow_server.Models.TeacherRepo;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {
    @Autowired
    private TeacherRepo teacherRepo;

    @GetMapping("/")
    public ModelAndView getGatePage(){
        return new ModelAndView("gate");
    }

    @GetMapping("/login")
    public ModelAndView getLoginView(){
        return new ModelAndView("login");
    }

    @GetMapping("/logout")
    public ModelAndView getLogoutView(HttpSession session){
        session.invalidate();
        return new ModelAndView("redirect:/");
    }

    @PostMapping("/login")
    public ModelAndView tryToLogin(@RequestParam(required = true) String username, @RequestParam(required = true) String password, HttpSession session){
        return teacherRepo.findByLoginAndPassword(username, password).map(teacher -> {
            session.setAttribute("login", username);
            session.setAttribute("id", teacher.getId());
            return new ModelAndView("redirect:/");
        }).orElse(new ModelAndView("login"));
    }
    
}
