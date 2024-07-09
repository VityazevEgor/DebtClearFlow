package com.vityazev_egor.debtclearflowapp.Models;
import java.time.LocalDateTime;

public class DebtRepayment {
    private Integer id;
    private String name;
    private String closet;
    private LocalDateTime starTime;
    private LocalDateTime endTime;
    private String teachersLogins;

    private Boolean isOpen = false;
    // Геттеры и сеттеры

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean open) {
        isOpen = open;
    }

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

    public String getTeachersLogins() {
        return teachersLogins;
    }

    public void setTeachersLogins(String teachersLogins) {
        this.teachersLogins = teachersLogins;
    }

    // create to string method
    @Override
    public String toString() {
        return "DebtRepayment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", closet='" + closet + '\'' +
                ", starTime=" + starTime +
                ", endTime=" + endTime +
                ", teachersLogins='" + teachersLogins + '\'' +
                '}';
    }
}
