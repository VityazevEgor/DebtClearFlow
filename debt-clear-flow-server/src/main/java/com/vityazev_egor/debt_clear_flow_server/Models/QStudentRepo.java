package com.vityazev_egor.debt_clear_flow_server.Models;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
public interface QStudentRepo extends JpaRepository<QStudent, Integer>{
    // ищем студентов, который записаны на определённую очередь
    public List<QStudent> findByDebtRepaymentOrderByIdAsc(DebtRepayment debtRepayment);

    // ищем студентов которые сейчас находятся в очереди
    @Query("SELECT qs FROM QStudent qs WHERE qs.debtRepayment = :debtRepayment AND qs.isAccepted = false AND qs.isInProcess = false AND qs.isRejected = false ORDER BY qs.id ASC")
    public List<QStudent> findAllUnacceptedInQueue(DebtRepayment debtRepayment);

    // найти всех записи студента с опреедлённой почтой
    public List<QStudent> findByEmail(String email);

    // найти всех студентов с оепределённой почтой записанные на определённую отработку
    public List<QStudent> findByEmailAndDebtRepayment(String email, DebtRepayment debtRepayment);

    @Query("SELECT COUNT(qs) FROM QStudent qs WHERE qs.debtRepayment = :debtRepayment AND qs.isAccepted = false AND qs.isInProcess = false AND qs.isRejected = false")
    public Integer countStudenstLeft(DebtRepayment debtRepayment);

    // найди следующего студента в очереди который ещё не был принят и не находиться на приёме
    @Query("SELECT qs FROM QStudent qs WHERE qs.debtRepayment = :debtRepayment AND qs.isAccepted = false AND qs.isInProcess = false AND qs.isRejected = false ORDER BY qs.id ASC LIMIT 1")
    public Optional<QStudent> findNextUnacceptedInQueue(@Param("debtRepayment") DebtRepayment debtRepayment);

    // ищем студента который в данный момент находиться на приёме у определённого учителя
    @Query("SELECT s FROM QStudent s WHERE s.debtRepayment = :debtRepayment AND s.teacherLogin = :teacherLogin AND s.isInProcess = true AND s.isAccepted = false")
    public Optional<QStudent> findCurrentStudent(@Param("debtRepayment") DebtRepayment debtRepayment, @Param("teacherLogin") String teacherLogin);
}
