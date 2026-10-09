package com.doritech.api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.DVR;

@Repository
public interface DVRRepository extends JpaRepository<DVR, Long> {
    
    @Query("SELECT DISTINCT d.panelSerial FROM DVR d")
    List<String> findDistinctPanelSerials();
}