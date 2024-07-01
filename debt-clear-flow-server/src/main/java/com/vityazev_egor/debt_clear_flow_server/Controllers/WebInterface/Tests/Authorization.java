package com.vityazev_egor.debt_clear_flow_server.Controllers.WebInterface.Tests;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/authtest")
public class Authorization {
    
    @GetMapping("/")
    public ModelAndView main(){
        return new ModelAndView("layout");
    }

    @GetMapping("/login")
    public ModelAndView getLoginView(){
        return new ModelAndView("login");
    }

    @PostMapping("/login")
    public ModelAndView tryToLogin(){
        System.out.println("Got rs");
        return new ModelAndView("login");
    }


}
