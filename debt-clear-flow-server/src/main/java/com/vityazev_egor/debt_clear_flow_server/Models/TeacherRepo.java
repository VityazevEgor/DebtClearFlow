package com.vityazev_egor.debt_clear_flow_server.Models;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepo extends JpaRepository<Teacher, Integer> {
    public List<Teacher> findByLogin(String username);
    public List<Teacher> findByLoginAndPassword(String username, String password);
}
