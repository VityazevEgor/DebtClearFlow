package com.vityazev_egor.debt_clear_flow_server.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class QStudent {
    // по id мы будем определяеть положение в очереди этого студента
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String fullName;
    private String email;
    private String workDeskription;

    // id очереди в которую добавлен этот студент
    @ManyToOne
    @JoinColumn(name = "repayment_id", nullable = false)
    private DebtRepayment debtRepayment;

    // была ли работа студента принята
    private Boolean isAccepted = false;
    // была ли работа студента отклонена
    private Boolean isRejected = false;

    // находится ли студент сейчас на приёме у преподователя
    private Boolean isInProcess = false;

    // логин преподователя, который принимает данного студента
    private String teacherLogin;

    public Boolean checkEmailAndFullName(){
        return fullName != null && email != null;
    }

    public QStudent createCleanCopy(){
        var copy = new QStudent();
        copy.setFullName(fullName);
        copy.setEmail(email);
        copy.setWorkDeskription(workDeskription);
        copy.setDebtRepayment(debtRepayment);

        return copy;
    }
}
