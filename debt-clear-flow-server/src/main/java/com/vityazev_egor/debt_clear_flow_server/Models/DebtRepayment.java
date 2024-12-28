package com.vityazev_egor.debt_clear_flow_server.Models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
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

    // Используем @ManyToMany так как один учитель может участвовать в нескольких отработках,
    // и в одной отработке может участвовать несколько учителей
    @ElementCollection
    @CollectionTable(name = "debt_repayment_teachers", joinColumns = @JoinColumn(name = "debt_repayment_id"))
    @Column(name = "teacher_login")
    private List<String> teachersLogins = new ArrayList<>();

    private Boolean isOpen = false;
}
