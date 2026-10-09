package com.doritech.api.Repository;

import com.doritech.api.Entity.EmailTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplateEntity, String> {

    // Exact match: level + reminder index
	@Query(value = "select * from email_templates where escalation_level = :escalationLevel and escalation_Reminder = :escalationReminder and is_active=1", nativeQuery = true)
    Optional<EmailTemplateEntity> findByEscalationLevelAndEscalationReminderAndIsActiveTrue(
            String escalationLevel, Integer escalationReminder);

    // Fallback: level only (reminder = 0 or any)
    Optional<EmailTemplateEntity> findByEscalationLevelAndIsActiveTrue(
            String escalationLevel);
}