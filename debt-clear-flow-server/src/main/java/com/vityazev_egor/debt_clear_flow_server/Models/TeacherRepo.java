package com.vityazev_egor.debt_clear_flow_server.Models;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepo extends JpaRepository<Teacher, Integer> {
    public Optional<Teacher> findByLogin(String username);
    public Optional<Teacher> findByLoginAndPassword(String username, String password);
}
