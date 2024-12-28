package com.vityazev_egor.debt_clear_flow_server.Models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

// модель отработки задолжности
@Entity
@Data
public class DebtRepayment {
    public enum RepaymentStatus{
        OPEN,
        CLOSED,
        WAITING
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotBlank(message = "Название отработки не может быть пустым")
    @Size(min = 1, max = 100, message = "Длина названия отроботки должна быть от 1 до 100 символов")
    private String name;
    // кабинет где проводится отработка
    @NotBlank(message  = "Указание кабинета отработки обязательно")
    private String closet;
    
    @NotNull(message = "Начало отработки должно быть указано")
    private LocalDateTime starTime;
    @NotNull(message = "Конец отработки должен быть указан")
    private LocalDateTime endTime;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "debt_repayment_teachers", joinColumns = @JoinColumn(name = "debt_repayment_id"))
    @Column(name = "teacher_login")
    private List<String> teachersLogins = new ArrayList<>();

    private RepaymentStatus status = RepaymentStatus.WAITING;
}
