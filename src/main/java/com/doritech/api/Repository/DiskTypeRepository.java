package com.doritech.api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.api.Entity.DiskType;

public interface DiskTypeRepository extends JpaRepository<DiskType, Long> {

    List<DiskType> findByStatus(String status);
}