package com.doritech.api.Repository;

import com.doritech.api.Entity.MenuAccessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuAccessRepository extends JpaRepository<MenuAccessEntity, Integer> {

    MenuAccessEntity findByRoleId(int userRoleId);
    MenuAccessEntity findByRoleNameAndMenuId(String roleName, int menuId);
    List<MenuAccessEntity> findByRoleName(String empRole);
}
