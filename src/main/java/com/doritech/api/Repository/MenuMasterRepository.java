package com.doritech.api.Repository;

import com.doritech.api.Entity.MenuAccessEntity;
import com.doritech.api.Entity.MenuMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuMasterRepository extends JpaRepository<MenuMasterEntity,Integer> {

}
