package com.vityazev_egor.debt_clear_flow_server.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String fullname;
    private String login;
    private String password;
    // названия файла с аватаркой преподавателя
    private String imageName;

    public Teacher clone(){
        return new Teacher(id, fullname, login, password, imageName);
    }
}
