package com.doritech.api.Service;

import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SmsArchivalService {

    private static final Logger logger = LoggerFactory.getLogger(SmsArchivalService.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Scheduled task that runs daily at 2:00 AM to archive old SMS records
     * Moves records older than 2 days from sms_details to sms_details_history
     */
    @Scheduled(cron = "0 0 2 * * ?") // Runs at 2:00 AM every day
    @Transactional
    public void archiveOldSmsRecords() {
        try {
            logger.info("Starting SMS archival process...");
            
            int archivedCount = archiveSmsRecordsInBatches();
            
            logger.info("SMS archival completed. Total records archived: {}", archivedCount);
        } catch (Exception e) {
            logger.error("Error during SMS archival process", e);
        }
    }

    /**
     * Archives SMS records in batches to handle large volumes efficiently
     * This prevents memory issues when dealing with 4-5 lakh records
     */
    @Transactional
    public int archiveSmsRecordsInBatches() {
        int totalArchived = 0;
        int batchSize = 5000;
        boolean hasMoreRecords = true;

        while (hasMoreRecords) {
            // Insert old records into history table (older than 2 days)
            int insertedCount = entityManager.createNativeQuery(
                "INSERT INTO sms_details_history " +
                "(ORIGINAL_ID, SENDERMOBILENO, PANELSERIALNO, DATETIME, UNIQUEID, " +
                "main, solar, battery, SIGNALFLAG, armed, " +
                "zone1, zone2, zone3, zone4, zone5, zone6, zone7, zone8, zone9, zone10, " +
                "zone11, zone12, zone13, zone14, zone15, zone16, zone17, zone18, zone19, zone20, " +
                "zone21, zone22, zone23, zone24, zone25, zone26, zone27, zone28, zone29, zone30, " +
                "zone31, zone32, zone33, zone34, zone35, zone36, zone37, zone38, zone39, zone40, " +
                "COMPLETEMESSAGE, ENTRYDATETIME, BROADCAST_FLAG, EVENT_TYPE, EVENT_INFO, " +
                "ADC1, ADC2, ADC3, GSM_SIGNAL, BATTERY_VOLTAGE, ARCHIVED_DATE) " +
                "SELECT id, SENDERMOBILENO, PANELSERIALNO, DATETIME, UNIQUEID, " +
                "main, solar, battery, SIGNALFLAG, armed, " +
                "zone1, zone2, zone3, zone4, zone5, zone6, zone7, zone8, zone9, zone10, " +
                "zone11, zone12, zone13, zone14, zone15, zone16, zone17, zone18, zone19, zone20, " +
                "zone21, zone22, zone23, zone24, zone25, zone26, zone27, zone28, zone29, zone30, " +
                "zone31, zone32, zone33, zone34, zone35, zone36, zone37, zone38, zone39, zone40, " +
                "COMPLETEMESSAGE, ENTRYDATETIME, BROADCAST_FLAG, EVENT_TYPE, EVENT_INFO, " +
                "ADC1, ADC2, ADC3, GSM_SIGNAL, BATTERY_VOLTAGE, NOW() " +
                "FROM sms_details " +
                "WHERE ENTRYDATETIME < DATE_SUB(NOW(), INTERVAL 2 DAY) " +
                "LIMIT :batchSize"
            ).setParameter("batchSize", batchSize)
             .executeUpdate();

            if (insertedCount > 0) {
                // Delete the archived records from main table
                int deletedCount = entityManager.createNativeQuery(
                    "DELETE FROM sms_details " +
                    "WHERE ENTRYDATETIME < DATE_SUB(NOW(), INTERVAL 2 DAY) " +
                    "LIMIT :batchSize"
                ).setParameter("batchSize", batchSize)
                 .executeUpdate();

                totalArchived += deletedCount;
                logger.info("Archived batch: {} records", deletedCount);
                
                // Commit after each batch
                entityManager.flush();
                entityManager.clear();
            } else {
                hasMoreRecords = false;
            }
        }

        return totalArchived;
    }

    /**
     * Manual trigger method for archiving (can be called from a REST endpoint)
     */
    @Transactional
    public String manualArchive() {
        try {
            logger.info("Manual SMS archival triggered");
            int count = archiveSmsRecordsInBatches();
            return "Successfully archived " + count + " records";
        } catch (Exception e) {
            logger.error("Error during manual archival", e);
            return "Error during archival: " + e.getMessage();
        }
    }
    
    /**
     * Get count of records to be archived
     */
    public Long getRecordsToArchiveCount() {
        return entityManager.createQuery(
            "SELECT COUNT(s) FROM SmsDetailsEntity s " +
            "WHERE s.entryDateTime < :twoDaysAgo", Long.class)
            .setParameter("twoDaysAgo", new Date(System.currentTimeMillis() - 2L * 24 * 60 * 60 * 1000))
            .getSingleResult();
    }
}