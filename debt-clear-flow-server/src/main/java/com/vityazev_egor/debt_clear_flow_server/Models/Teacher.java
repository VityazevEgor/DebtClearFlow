package com.vityazev_egor.debt_clear_flow_server.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer id;

    public String fullname;
    public String login;
    public String password;
    // названия файла с аватаркой преподавателя
    public String imageName;
}
