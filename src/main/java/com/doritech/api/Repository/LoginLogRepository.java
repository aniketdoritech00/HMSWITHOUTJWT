package com.doritech.api.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.api.Entity.LoginLog;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
    Optional<LoginLog> findTopByEmailOrderByLoginTimeDesc(String email);
}
