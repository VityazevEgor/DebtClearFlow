package com.vityazev_egor.debt_clear_flow_server.Models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// модель отработки задолжности
@Entity
public class DebtRepayment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotBlank(message = "Name is blank")
    @Size(min = 1, max = 100, message = "Name is too long")
    private String name;
    // кабинет где проводится отработка
    @NotBlank(message  = "Closet is blank")
    private String closet;
    
    @NotNull
    private LocalDateTime starTime;
    @NotNull
    private LocalDateTime endTime;

    // логины преподавателей (разделённые через запятую), которые принимают участие в отработке
    @NotBlank(message = "Teachers is blank")
    @Size(min  =  1, max  =  255, message  =  "Teachers is too long")
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
