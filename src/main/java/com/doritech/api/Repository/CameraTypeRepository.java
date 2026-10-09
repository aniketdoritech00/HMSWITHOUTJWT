package com.doritech.api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.CameraMaster;
import com.doritech.api.Entity.CameraType;

@Repository
public interface CameraTypeRepository extends JpaRepository<CameraType, Long> {

    List<CameraType> findByStatus(String status);

}