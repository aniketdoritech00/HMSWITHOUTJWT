package com.doritech.api.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.doritech.api.DTO.BulkSmsRequestDTO;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.SmsLogEntity;
import com.doritech.api.Entity.SmsParamEntity;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.SmsLogRepo;
import com.doritech.api.Repository.SmsParamRepo;
import com.doritech.api.Repository.StaticDataRepo;

@Service
public class BulkSmsService {

    @Autowired
    SmsLogRepo smsLogRepo;
    @Autowired
    SmsParamRepo smsParamRepo;
    @Autowired
    BranchMasterRepository branchMasterRepository;
    @Autowired
    StaticDataRepo staticDataRepo;

    @Async
    public void sendBulkSms(BulkSmsRequestDTO request) {
        for (int i = 0; i < request.getCount(); i++) {
            try {
                System.out.println("==> Sending SMS #" + (i + 1) + " of " + request.getCount());
                processSingleSms(request, i + 1);

                if (i < request.getCount() - 1) {
                    System.out.println("Waiting " + request.getWaitMinutes() + " seconds before next SMS...");
                    TimeUnit.SECONDS.sleep(request.getWaitMinutes());
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("SMS sending interrupted at #" + (i + 1));
                break;
            } catch (Exception e) {
                System.out.println("Error at SMS #" + (i + 1));
                e.printStackTrace();
            }
        }
        System.out.println("✅ All " + request.getCount() + " SMS processed!");
    }

    private void processSingleSms(BulkSmsRequestDTO request, int attempt) throws Exception {

        // Branch details fetch karo
        BranchMasterEntity branchEntity =
                branchMasterRepository.findBybranchCode(request.getBranchCode());

        if (branchEntity == null) {
            System.out.println("Branch not found for code: " + request.getBranchCode());
            return;
        }

        // Static template fetch karo
        List<Object[]> staticData = staticDataRepo.getMessageTemplates("MESSAGE", "ALERT");

        if (staticData == null || staticData.isEmpty()) {
            System.out.println("No SMS template found!");
            return;
        }

        // Sirf pehla template use karo
        Object[] object = staticData.get(0);

        // Date/Time
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String currentDate = dateFormat.format(new Date());
        String currentTime = timeFormat.format(new Date());

        // SMS Param fetch karo
        SmsParamEntity smsParam = smsParamRepo.getById(1L);

        if (smsParam == null) {
            System.out.println("SMS Param not found!");
            return;
        }

        // Encode values
        String encodedDateTime   = URLEncoder.encode(currentDate + " " + currentTime,
                                    StandardCharsets.UTF_8.toString());
        String encodedBranchDesc = URLEncoder.encode(branchEntity.getBranchDesc(),
                                    StandardCharsets.UTF_8.toString());
        String encodedAlertTypes = URLEncoder.encode(request.getActiveZone(),
                                    StandardCharsets.UTF_8.toString());

        // URL build karo
        String smsURL = smsParam.getUrl()
                .replace("XAPIKEY",   smsParam.getPassword())
                .replace("XROUTE",    "transactional")
                .replace("XSENDERID", smsParam.getSenderid())
                .replace("XMOBILE",   request.getMobileNumber())
                .replace("XV1",       encodedDateTime)
                .replace("XV2",       encodedBranchDesc)
                .replace("XV3",       encodedAlertTypes);

        System.out.println("=== SMS URL (Attempt #" + attempt + ") ===");
        System.out.println(smsURL);
        System.out.println("==========================================");

        // Decoded text for log
        String decodedText = "";
        if (smsURL.contains("&text=")) {
            String textPart = smsURL.substring(smsURL.indexOf("&text=") + 6);
            decodedText = URLDecoder.decode(textPart, StandardCharsets.UTF_8.toString());
        }

        // Parent branch name
        BranchMasterEntity parentBranch =
                (branchEntity.getParentCode() != null)
                        ? branchMasterRepository.findBybranchCode(branchEntity.getParentCode())
                        : null;

        String branchName = (parentBranch != null && parentBranch.getBranchDesc() != null)
                ? branchEntity.getBranchDesc() + " - " + parentBranch.getBranchDesc()
                : branchEntity.getBranchDesc();

        // DB mein log save karo (pehle status N)
        SmsLogEntity smsEntity = new SmsLogEntity();
        smsEntity.setDatetime(new Date());
        smsEntity.setMessage(decodedText);
        smsEntity.setSentTo(request.getMobileNumber());
        smsEntity.setSmsId(1L);
        smsEntity.setStatus("N");
        smsEntity.setType("BULK_ALERT");
        smsEntity.setMessageRef(smsURL);
        smsEntity.setBranchCode(request.getBranchCode());
        smsEntity.setEscalationLogId(request.getEscalationLogId());
        smsEntity.setBranchName(branchName);
        smsLogRepo.save(smsEntity);

        // ✅ ACTUAL HTTP CALL
        try {
            URL url = new URL(smsURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();

            // Response body padho
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBody = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                responseBody.append(inputLine);
            }
            in.close();

            System.out.println("SMS API Response Code : " + responseCode);
            System.out.println("SMS API Response Body : " + responseBody.toString());

            // Agar success toh status Y kar do
            if (responseCode == 200) {
                smsEntity.setStatus("Y");
                smsLogRepo.save(smsEntity);
                System.out.println("SMS #" + attempt + " sent successfully to: "
                        + request.getMobileNumber());
            } else {
                System.out.println("SMS #" + attempt + " failed with response code: "
                        + responseCode);
            }

        } catch (Exception e) {
            System.out.println("SMS API Call Failed for attempt #" + attempt
                    + " : " + e.getMessage());
            e.printStackTrace();
        }
    }
}