package com.vityazev_egor.debt_clear_flow_server.Controllers.Filters;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthFilter extends OncePerRequestFilter{
    
    private final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession();

        if (session != null && session.getAttribute("login") != null && session.getAttribute("id") != null){
            logger.info("User is logged in with name = "+session.getAttribute("login").toString());
            filterChain.doFilter(request, response);
        }
        else{
            logger.info("User is not logged in");
            
            response.sendRedirect("/authtest/login");
        }
    }
    
}
