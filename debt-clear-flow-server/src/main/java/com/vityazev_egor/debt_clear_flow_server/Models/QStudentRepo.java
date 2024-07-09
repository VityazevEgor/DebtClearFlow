package com.vityazev_egor.debt_clear_flow_server.Models;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
public interface QStudentRepo extends JpaRepository<QStudent, Integer>{
    // ищем студентов, который записаны на определённую очередь
    public List<QStudent> findByDebtRepaymentIdOrderByIdAsc(Integer debtRepaymentId);

    // ищем студентов которые сейчас находятся в очереди
    public List<QStudent> findByDebtRepaymentIdAndIsAcceptedFalseOrderByIdAsc(Integer debtRepaymentId);

    // найти всех студентов с опреедлённой почтой
    public List<QStudent> findByEmail(String email);

    // найди следующего студента в очереди который ещё не был принят и не находиться на приёме
    public QStudent findFirstByDebtRepaymentIdAndIsAcceptedFalseAndIsInProcessFalseOrderByIdAsc(Integer debtRepaymentId);

    // ищем стедунта который в данный момент находиться на приём
    public List<QStudent> findByDebtRepaymentIdAndTeacherLoginAndIsInProcessTrueAndIsAcceptedFalse(Integer debtRepaymentId, String teacherLogin);

    @Query("SELECT s FROM QStudent s WHERE s.debtRepaymentId = :debtRepaymentId AND s.teacherLogin = :teacherLogin AND s.isInProcess = true AND s.isAccepted = false")
    public QStudent findCurrentStudent(@Param("debtRepaymentId") Integer debtRepaymentId, @Param("teacherLogin") String teacherLogin);
}
