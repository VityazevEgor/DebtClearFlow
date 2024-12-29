package com.vityazev_egor.debtclearflowapp.Models;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DebtRepayment {
    public enum RepaymentStatus{
        OPEN,
        CLOSED,
        WAITING
    }
    private Integer id;
    private String name;
    private String closet;
    private LocalDateTime starTime;
    private LocalDateTime endTime;
    private List<String> teachersLogins = new ArrayList<>();
    private RepaymentStatus status = RepaymentStatus.WAITING;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCloset() {
        return closet;
    }

    public void setCloset(String closet) {
        this.closet = closet;
    }

    public LocalDateTime getStarTime() {
        return starTime;
    }

    public void setStarTime(LocalDateTime starTime) {
        this.starTime = starTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<String> getTeachersLogins() {
        return teachersLogins;
    }

    public void setTeachersLogins(List<String> teachersLogins) {
        this.teachersLogins = teachersLogins;
    }

    public RepaymentStatus getStatus() {
        return status;
    }

    public void setStatus(RepaymentStatus status) {
        this.status = status;
    }
}
