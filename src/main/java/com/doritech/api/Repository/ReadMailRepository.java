package com.doritech.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.ReadMailEntity;

@Repository
public interface ReadMailRepository extends JpaRepository<ReadMailEntity, Integer>{

}
