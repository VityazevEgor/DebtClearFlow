package com.vityazev_egor.debtclearflowapp.Models;

public class QStudent {
    private Integer id;

    private String fullName;
    private String email;
    private String workDeskription;

    // id очереди в которую добавлен этот студент
    private Integer debtRepaymentId;

    // был ли студент уже принят преподователем
    private Boolean isAccepted = false;

    // находится ли студент сейчас на приёме у преподователя
    private Boolean isInProcess = false;

    // логин преподователя, который принимает данного студента
    private String teacherLogin;

    public Boolean checkEmailAndFullName(){
        return fullName != null && email != null;
    }

    @Override
    public String toString(){
        return "id: " + id + "\n" +
                "fullName: " + fullName + "\n" +
                "email: " + email + "\n" +
                "workDeskription: " + workDeskription + "\n" +
                "debtRepaymentId: " + debtRepaymentId + "\n" +
                "isAccepted: " + isAccepted + "\n" +
                "isInProcess: " + isInProcess + "\n" +
                "teacherLogin: " + teacherLogin + "\n";
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getId() {
        return id;
    }

    public void setFullName(String fullName){
        this.fullName = fullName;
    }
    public String getFullName(){
        return fullName;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getEmail(){
        return email;
    }
    public void setWorkDeskription(String workDeskription){
        this.workDeskription = workDeskription;
    }
    public String getWorkDeskription(){
        return workDeskription;
    }

    public void setDebtRepaymentId(Integer debtRepaymentId){
        this.debtRepaymentId = debtRepaymentId;
    }
    public Integer getDebtRepaymentId(){
        return debtRepaymentId;
    }
    public void setIsAccepted(Boolean isAccepted){
        this.isAccepted = isAccepted;
    }
    public Boolean getIsAccepted(){
        return isAccepted;
    }
    public void setIsInProcess(Boolean isInProcess){
        this.isInProcess = isInProcess;
    }
    public Boolean getIsInProcess(){
        return isInProcess;
    }
    public void setTeacherLogin(String teacherLogin){
        this.teacherLogin = teacherLogin;
    }
    public String getTeacherLogin(){
        return teacherLogin;
    }
}
