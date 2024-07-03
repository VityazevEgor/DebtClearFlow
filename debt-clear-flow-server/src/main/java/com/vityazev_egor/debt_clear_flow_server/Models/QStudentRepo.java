package com.vityazev_egor.debt_clear_flow_server.Models;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface QStudentRepo extends JpaRepository<QStudent, Integer>{
    // ищем студентов, который записаны на определённую очередь
    public List<QStudent> findByDebtRepaymentIdOrderByIdAsc(Integer debtRepaymentId);
}
