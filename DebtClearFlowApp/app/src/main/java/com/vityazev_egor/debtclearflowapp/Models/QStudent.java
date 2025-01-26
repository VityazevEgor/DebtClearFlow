package com.vityazev_egor.debtclearflowapp.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QStudent {
    // по id мы будем определяеть положение в очереди этого студента
    private Integer id;

    private String fullName;
    private String email;
    private String workDeskription;

    // id очереди в которую добавлен этот студент
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWorkDeskription() {
        return workDeskription;
    }

    public void setWorkDeskription(String workDeskription) {
        this.workDeskription = workDeskription;
    }

    public DebtRepayment getDebtRepayment() {
        return debtRepayment;
    }

    public void setDebtRepayment(DebtRepayment debtRepayment) {
        this.debtRepayment = debtRepayment;
    }

    @JsonProperty("isAccepted")
    public Boolean getAccepted() {
        return isAccepted;
    }

    public void setAccepted(Boolean accepted) {
        isAccepted = accepted;
    }

    @JsonProperty("isRejected")
    public Boolean getRejected() {
        return isRejected;
    }

    public void setRejected(Boolean rejected) {
        isRejected = rejected;
    }

    @JsonProperty("isInProcess")
    public Boolean getInProcess() {
        return isInProcess;
    }

    public void setInProcess(Boolean inProcess) {
        isInProcess = inProcess;
    }

    public String getTeacherLogin() {
        return teacherLogin;
    }

    public void setTeacherLogin(String teacherLogin) {
        this.teacherLogin = teacherLogin;
    }
}
