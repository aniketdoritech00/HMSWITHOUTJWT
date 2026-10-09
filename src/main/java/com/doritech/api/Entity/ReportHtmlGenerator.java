package com.doritech.api.Entity;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.doritech.api.DTO.TATReportDTO;
import com.doritech.api.DTO.ZoneReportDTO;
import com.doritech.api.DTO.ZonesDTO;

@Service
public class ReportHtmlGenerator {

    public String generateBranchReportHtml(String branchCode, Object branchReport,
                                           Object uptimeReport, Object cctvTatReport, Object fasTatReport,
                                           Object sasTatReport, Object bacsTatReport, Object etlTatReport) {

        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html lang='en'><head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta http-equiv='X-UA-Compatible' content='IE=edge'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>DI-HMS Reports - ").append(branchCode).append("</title>");

        // CSS styles
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; font-size: 12px; margin: 0; padding: 20px; }");
        html.append("table { border-collapse: collapse; width: 100%; margin-bottom: 20px; min-width: 600px; }");
        html.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: center; white-space: nowrap; }");
        html.append("th { background-color: #f2f2f2; font-weight: bold; }");
        html.append("tbody tr:nth-child(even) { background-color: #f9f9f9; }");
        html.append(".status-label { display: inline-block; padding: 2px 10px; border-radius: 5px; font-weight: bold; font-size: 12px; }");
        html.append(".status-online { background-color: green; color: white; }");
        html.append(".status-offline { background-color: red; color: white; }");
        html.append(".status-fault { background-color: yellow; color: black; }");
        html.append(".report-heading { background-color: #0056b3; color: white; padding: 10px; border-radius: 5px 5px 0 0; font-weight: bold; font-size: 16px; }");
        html.append(".report-body { padding: 10px; border: 1px solid #ddd; border-top: none; background-color: #f9f9f9; }");
        html.append(".table-responsive { width: 100%; overflow-x: auto; -webkit-overflow-scrolling: touch; }");
        html.append("@media (max-width: 768px) { body { font-size: 11px; } th, td { font-size: 11px; padding: 6px; } }");
        html.append("</style>");
        html.append("</head><body>");

        html.append("<div class='container-fluid'>");

        // Sections
        html.append(generateBranchReportSection(branchReport));
        html.append(generateUptimeReportSection(uptimeReport));
        html.append(generateTATReportSection("CCTV TAT Report", cctvTatReport));
        html.append(generateTATReportSection("FAS TAT Report", fasTatReport));
        html.append(generateTATReportSection("SAS/IAS TAT Report", sasTatReport));
        html.append(generateTATReportSection("BACS TAT Report", bacsTatReport));
        html.append(generateTATReportSection("ETL TAT Report", etlTatReport));

        html.append("</div></body></html>");

        return html.toString();
    }

    // ----------- Branch Report -----------
    private String generateBranchReportSection(Object branchReport) {
        StringBuilder section = new StringBuilder();
        section.append("<div class='report-container'>");
        section.append("<div class='report-heading'>Branch Report")
        .append(getDefaultTimeFrame())
        .append("</div>");


        section.append("<div class='report-body'>");

        if (isValidResponse(branchReport)) {
            ResponseEntity response = (ResponseEntity) branchReport;
            List<?> payload = (List<?>) response.getPayload();

            if (payload != null && !payload.isEmpty()) {
                section.append("<div class='table-responsive'>");
                section.append("<table>");
                section.append("<thead><tr>");
                section.append("<th>S.No</th><th>Branch Name</th><th>Site Code</th>");
                section.append("<th>CCTV</th><th>SAS/IAS</th><th>FAS</th><th>ETL</th><th>BACS</th>");
                section.append("</tr></thead><tbody>");

                int sno = 1;
                for (Object item : payload) {
                    if (item instanceof ZoneReportDTO) {
                        section.append(processBranchDataRow((ZoneReportDTO) item, sno++));
                    }
                }

                section.append("</tbody></table>");
                section.append("</div>");
            } else {
                section.append("<p class='text-center'>No Branch Report data available</p>");
            }
        } else {
            section.append("<p class='text-center'>No Branch Report data available</p>");
        }

        section.append("</div></div>");
        return section.toString();
    }

    private String processBranchDataRow(ZoneReportDTO dto, int sno) {
        try {
            String cctv = "-", sas = "-", fas = "-", etl = "-", bacs = "-";

            if (dto.getZoneDTOList() != null) {
                for (ZonesDTO zone : dto.getZoneDTOList()) {
                    String span = generateStatusSpan(zone.getStatus());
                    switch (zone.getZoneType().toUpperCase()) {
                        case "CCTV": cctv = span; break;
                        case "SECURITY ALARM": sas = span; break;
                        case "FIRE ALARM": fas = span; break;
                        case "ETL": etl = span; break;
                        case "BACS": bacs = span; break;
                    }
                }
            }

            return "<tr><td>" + sno + "</td>" +
                    "<td>" + safe(dto.getBranchName()) + "</td>" +
                    "<td>" + safe(dto.getBank()) + "</td>" +
                    "<td>" + cctv + "</td><td>" + sas + "</td><td>" + fas + "</td>" +
                    "<td>" + etl + "</td><td>" + bacs + "</td></tr>";
        } catch (Exception e) {
            return "<tr><td colspan='8'>Error: " + e.getMessage() + "</td></tr>";
        }
    }

    // ----------- Uptime Report -----------
    private String generateUptimeReportSection(Object uptimeReport) {
        StringBuilder section = new StringBuilder();

        section.append("<div class='report-container'>");
        section.append("<div class='report-heading'>Uptime Report")
        .append(getDefaultTimeFrame())
        .append("</div>");


        section.append("<div class='report-body'>");

        if (isValidResponse(uptimeReport)) {
            ResponseEntity response = (ResponseEntity) uptimeReport;
            List<?> payload = (List<?>) response.getPayload();

            if (payload != null && !payload.isEmpty()) {
                Map<String, Map<String, TATReportDTO>> branchDeviceMap = new LinkedHashMap<>();

                for (Object item : payload) {
                    if (item instanceof TATReportDTO) {
                        TATReportDTO dto = (TATReportDTO) item;
                        branchDeviceMap
                            .computeIfAbsent(dto.getBranchCode(), k -> new HashMap<>())
                            .put(dto.getDeviceType().toUpperCase(), dto);
                    }
                }

                section.append("<div class='table-responsive'>");
                section.append("<table class='table table-striped table-bordered'>");
                section.append("<thead><tr>");
                section.append("<th>S.No</th><th>Branch Name</th><th>Site Code</th>");
                section.append("<th>CCTV</th><th>SAS/IAS</th><th>FIRE ALARM</th><th>ETL</th><th>BACS</th>");
                section.append("</tr></thead><tbody>");

                int sno = 1;
                for (Map.Entry<String, Map<String, TATReportDTO>> entry : branchDeviceMap.entrySet()) {
                    Map<String, TATReportDTO> deviceMap = entry.getValue();
                    TATReportDTO anyDto = deviceMap.values().iterator().next();

                    section.append("<tr>");
                    section.append("<td>").append(sno++).append("</td>");
                    section.append("<td>").append(safe(anyDto.getBranchName())).append("</td>");
                    section.append("<td>").append(safe(anyDto.getIfsc())).append("</td>");
                    section.append("<td>").append(formatUptimeStr(deviceMap.get("CCTV"))).append("</td>");
                    section.append("<td>").append(formatUptimeStr(deviceMap.get("SECURITY ALARM"))).append("</td>");
                    section.append("<td>").append(formatUptimeStr(deviceMap.get("FIRE ALARM"))).append("</td>");
                    section.append("<td>").append(formatUptimeStr(deviceMap.get("ETL"))).append("</td>");
                    section.append("<td>").append(formatUptimeStr(deviceMap.get("BACS"))).append("</td>");
                    section.append("</tr>");
                }

                section.append("</tbody></table>");
                section.append("</div>");
            } else {
                section.append("<p class='text-center text-muted'>No Uptime Report data available</p>");
            }
        } else {
            section.append("<p class='text-center text-muted'>No Uptime Report data available</p>");
        }

        section.append("</div></div>");
        return section.toString();
    }

    private String formatUptimeStr(TATReportDTO dto) {
        if (dto == null) return "-";
        try {
            int uptime = Integer.parseInt(dto.getUptime());
            return formatUptime(uptime);
        } catch (Exception e) {
            // If parsing fails, treat it as a status string (like "Out of Sync")
            String status = safe(dto.getStatus());
            if (status.equalsIgnoreCase("OUT OF SYNC") || status.equalsIgnoreCase("OUTOF SYNC")) {
                return "<span class='status-label status-fault'>" + status + "</span>";
            }
            return "<span class='status-label status-fault'>" + status + "</span>";
        }
    }

    // ----------- TAT Reports -----------
    private String generateTATReportSection(String reportTitle, Object tatReport) {
        StringBuilder section = new StringBuilder();
        section.append("<div class='report-container'>");
        section.append("<div class='report-heading'>")
        .append(reportTitle)
        .append(getDefaultTimeFrame())
        .append("</div>");
 section.append("<div class='report-body'>");

        if (isValidResponse(tatReport)) {
            ResponseEntity response = (ResponseEntity) tatReport;
            List<?> payload = (List<?>) response.getPayload();

            section.append("<div class='table-responsive'>");
            section.append("<table>");
            section.append("<thead><tr>");
            section.append("<th>S.No</th><th>Branch Name</th><th>IFSC</th><th>Alert Type</th><th>TAT</th>");
            section.append("</tr></thead><tbody>");

            if (payload != null && !payload.isEmpty()) {
                int sno = 1;
                for (Object item : payload) {
                    section.append(processTATDataRow(item, sno++));
                }
            } else {
                section.append("<tr><td colspan='6'>No Alarms Found</td></tr>");
            }

            section.append("</tbody></table>");
            section.append("</div>");
        } else {
            section.append("<p class='text-center'>No TAT data available</p>");
        }

        section.append("</div></div>");
        return section.toString();
    }

    private String processTATDataRow(Object item, int sno) {
        try {
            if (item instanceof TATReportDTO) {
                TATReportDTO dto = (TATReportDTO) item;
                return "<tr><td>" + sno + "</td>" +
                        "<td>" + safe(dto.getBranchName()) + "</td>" +
                        "<td>" + safe(dto.getIfsc()) + "</td>" +
                        "<td>" + safe(dto.getZone()) + "</td>" +
                        
                        "<td>" + safe(dto.getTat()) + "</td></tr>";
            }
            return "<tr><td colspan='6'>Unsupported data format</td></tr>";
        } catch (Exception e) {
            return "<tr><td colspan='6'>Error: " + e.getMessage() + "</td></tr>";
        }
    }

    // ----------- Helpers -----------
    private boolean isValidResponse(Object response) {
        if (response instanceof ResponseEntity) {
            ResponseEntity resp = (ResponseEntity) response;
            Object statusCode = resp.getStatusCode();
            return (("200".equals(statusCode)) ||
                    (statusCode instanceof Integer && (Integer) statusCode == 200))
                    && resp.getPayload() != null;
        }
        return false;
    }

    private String generateStatusSpan(String status) {
        if (status == null) return "-";
        switch (status.toUpperCase()) {
            case "WORKING": return "<span class='status-label status-online'>WORKING</span>";
            case "NOT WORKING": return "<span class='status-label status-offline'>NOT WORKING</span>";
            default: return "<span class='status-label status-fault'>" + status + "</span>";
        }
    }

    private String formatUptime(Object uptimeObj) {
        if (uptimeObj == null) return "-";

        Integer uptimeInt = null;
        if (uptimeObj instanceof Number) {
            uptimeInt = ((Number) uptimeObj).intValue();
        } else {
            String s = uptimeObj.toString().trim();
            if (s.endsWith("%")) {
                s = s.substring(0, s.length() - 1).trim();
            }
            try {
                uptimeInt = Integer.parseInt(s);
            } catch (NumberFormatException ex) {
                return "<span class='status-label status-fault'>" + safe(s) + "</span>";
            }
        }

        // ✅ Apply new ranges
        if (uptimeInt >= 50) {
            return "<span class='status-label status-online'>" + uptimeInt + "%</span>"; // Green
        } else if (uptimeInt >= 20) {
            return "<span class='status-label status-fault'>" + uptimeInt + "%</span>"; // Yellow
        } else {
            return "<span class='status-label status-offline'>" + uptimeInt + "%</span>"; // Red
        }
    }


    private String safe(Object obj) {
        return obj == null ? "-" : obj.toString();
    }
    private String getDefaultTimeFrame() {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        String today = java.time.LocalDate.now().format(formatter);
        return " - As on " + today + " (00:01 AM to 23:59 PM)";
    }

}
