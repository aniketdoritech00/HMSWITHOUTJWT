package com.doritech.api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.api.DTO.SiaProtocalDTO;
import com.doritech.api.Entity.SiaProtocalEntity;
@Repository
public interface SiaProtocalRepository extends JpaRepository<SiaProtocalEntity, Long>{

	List<SiaProtocalEntity> findByDeviceId(String deviceId);

	SiaProtocalEntity findByCommand(String command);

	SiaProtocalEntity findByCommandAndDeviceId(String command, String deviceId);

}
