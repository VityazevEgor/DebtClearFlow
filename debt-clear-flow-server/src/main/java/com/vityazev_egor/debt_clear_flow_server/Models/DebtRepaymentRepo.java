package com.vityazev_egor.debt_clear_flow_server.Models;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface DebtRepaymentRepo extends JpaRepository<DebtRepayment, Integer>{
    // найти отработки, к которым определённый преподаватель имеет доступ
    List<DebtRepayment> findByTeachersLoginsContaining(String teachersLogins);
}
