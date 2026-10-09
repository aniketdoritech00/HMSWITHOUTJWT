package com.doritech.api.Repository;

import com.doritech.api.Entity.AlarmEmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmEmailLogRepository extends JpaRepository<AlarmEmailLog, Long> {
    boolean existsByBranchNameAndDeviceNameAndAlarmTime(String branchName, String deviceName, String alarmTime);
    
    boolean existsByAlarmKey(String alarmKey);

}
