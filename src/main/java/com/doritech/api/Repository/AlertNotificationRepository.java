package com.doritech.api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.doritech.api.Entity.AlertNotificationEntity;
import java.util.Optional;

@Repository
public interface AlertNotificationRepository extends JpaRepository<AlertNotificationEntity, Long> {
    
    // Check if notification already sent for this specific alarm
    Optional<AlertNotificationEntity> findByDeviceNameAndBranchNameAndAlertTypeAndAlarmTimeAndAlarmStatus(
            String deviceName, String branchName, String alertType, String alarmTime, String alarmStatus);
    
    // Find notification by device and alarm time (to update status when alarm closes)
    Optional<AlertNotificationEntity> findByDeviceNameAndAlarmTime(String deviceName, String alarmTime);

	Optional<AlertNotificationEntity> findByDeviceNameAndBranchNameAndAlertTypeAndAlarmStatus(String deviceName, String branchName,
			String alertType, String string);
}