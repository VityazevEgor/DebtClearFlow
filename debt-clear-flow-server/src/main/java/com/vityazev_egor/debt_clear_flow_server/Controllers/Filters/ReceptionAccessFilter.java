package com.vityazev_egor.debt_clear_flow_server.Controllers.Filters;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.vityazev_egor.debt_clear_flow_server.Models.DebtRepaymentRepo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// фильтр который блокирует доступ к отработкам преподавателям, которым не был выдан доступ
@Component
public class ReceptionAccessFilter extends OncePerRequestFilter{

    @Autowired
    private DebtRepaymentRepo repaymentRepo;

    private final Logger logger = LoggerFactory.getLogger(ReceptionAccessFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String teacherLogin = (String) request.getSession().getAttribute("login");
        
        if (!path.matches("/panel/reception/\\d+/\\w+") && !path.matches("/panel/reception/\\d+")) {
            filterChain.doFilter(request, response);
            return;
        }

        String[] pathSegments = path.split("/");
        Integer rpid = Integer.parseInt(pathSegments[3]); // Извлекаем {rpid}

        repaymentRepo.findById(rpid).ifPresentOrElse(
            repayment -> {
                try {
                    if (repayment.getTeachersLogins().stream().anyMatch(teacherLogin::equals)) {
                        filterChain.doFilter(request, response);
                    } else {
                        logger.warn(String.format("User with login = %s tried to access wrong reception", teacherLogin));
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    }
                } catch (IOException | ServletException e) {
                    throw new RuntimeException(e); // Так надо, а иначе не компилируется :/ 
                }
            },
            () -> {
                try {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                } catch (IOException e) {
                    throw new RuntimeException(e); // Так надо, а иначе не компилируется :/ 
                }
            }
        );
    }

    
}
