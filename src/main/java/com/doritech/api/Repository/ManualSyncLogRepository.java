package com.doritech.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.ManualSyncLog;
@Repository
public interface ManualSyncLogRepository extends JpaRepository<ManualSyncLog, Long>{

}
