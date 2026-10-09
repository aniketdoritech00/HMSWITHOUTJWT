<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="org.springframework.http.HttpStatus"%>
<%@ page import="com.doritech.api.Entity.*"%>
<%@ page import="com.doritech.api.DTO.EmployeeMasterDTO"%>
<%@ page import="com.doritech.api.Entity.ParamEntity"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>DI-HMS Reports</title>

<link rel="stylesheet" href="./css/deviceLogDetails.css" />
<link rel="stylesheet" href="./css/multilingual.css" />
<link rel="stylesheet" href="./externalCSS/bootstrap.min.css" />
<script src="./externalJS/fontawesome.js"></script>

<!-- DATATABLES CSS Links -->
<link rel="stylesheet"
	href="./externalCSS/dataTables.bootstrap4.min.css">
<link rel="stylesheet" href="./externalCSS/buttons.dataTables.min.css">
<link rel="stylesheet"
	href="./externalCSS/responsive.bootstrap4.min.css">

<!-- DATATABLES JS Links -->
<script src="./externalJS/sweetalert2.all.min.js"></script>
<script src="./externalJS/jquery-3.7.0.js"></script>
<script src="./externalJS/jquery.dataTables.min.js"></script>
<script src="./externalJS/dataTables.bootstrap4.min.js"></script>
<script src="./externalJS/responsive.bootstrap4.min.js"></script>
<script src="./externalJS/dataTables.responsive.min.js"></script>
<script src="./externalJS/dataTables.buttons.min.js"></script>
<script src="./externalJS/pdfmake.min.js"></script>
<script src="./externalJS/vfs_fonts.js"></script>
<script src="./externalJS/buttons.html5.min.js"></script>
<script src="./externalJS/buttons.print.min.js"></script>
<script src="./externalJS/jszip.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/html2canvas/1.4.1/html2canvas.min.js"></script>

</head>
<style>
    .dt-buttons {
        display: none !important;
    }
    
    .status-label {
        display: inline-block;
        padding: 2px 10px;
        border-radius: 5px;
        font-weight: bold;
        font-size: 12px;
        background-color: white;
        border: 1px solid;
    }

    .status-online {
        border-color: #28a745; /* green */
        color: #28a745;
    }

    .status-offline {
        border-color: #dc3545; /* red */
        color: #dc3545;
    }

    .status-fault {
        border-color: #ffc107; /* yellow */
        color: #ffc107;
    }

    .report-container {
        margin-bottom: 20px;
    }

    .report-heading {
        background-color: #0056b3;
        color: white;
        padding: 10px 15px;
        border-radius: 5px 5px 0 0;
        font-weight: bold;
        font-size: 18px;
        margin-bottom: 0;
    }

    .report-body {
        padding: 15px;
        border: 1px solid #ddd;
        border-top: none;
        border-radius: 0 0 5px 5px;
        background-color: #f9f9f9;
    }

    .report-actions {
        margin-bottom: 15px;
    }

    .btn-export {
        margin-right: 5px;
    }

    .tab-content {
        height: calc(100vh - 250px);
        overflow-y: auto;
    }
    
    @media print {
      .report-container {
        page-break-after: always;
      }
    }
</style>

<body style="font-size: 12px;">
	<div class="container-fluid">
	<div class="report-actions text-right">
  <!--   <button class="btn btn-danger btn-export" onclick="generatePDF()">
        <i class="fas fa-file-pdf"></i> Export All Reports to PDF
    </button> -->
</div>
	
		<!-- Branch Report Section -->
		<div class="report-container" id="branchReportSection">
			<div class="report-heading">
				Branch Report
			</div>
			<div class="report-body">
				<div class="card">
					<div id="branchReportContent">
						<c:choose>
							<c:when test="${branchReport.statusCode == 200 and not empty branchReport.payload}">
								<table id="branchTable" class="table table-striped table-bordered" style="width:100%">
									<thead>
										<tr style="text-align: left;">
											<th>Branch Name</th>
											<th>Ifsc Code</th>
											<th>CCTV</th>
											<th>SAS</th>
											<th>FAS</th>
											<th>ETL</th>
											<th>BACS</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="branch" items="${branchReport.payload}">
											<tr style="text-align:left;">
												<td>${branch.branchName}</td>
												<td>${branch.bank}</td>
												<c:set var="cctv" value="-" />
												<c:set var="sas" value="-" />
												<c:set var="fas" value="-" />
												<c:set var="etl" value="-" />
												<c:set var="bacs" value="-" />
												
												<c:forEach var="zone" items="${branch.zoneDTOList}">
													<c:choose>
														<c:when test="${zone.zoneType.toUpperCase() == 'CCTV'}">
															<c:set var="cctv">
																<c:choose>
																	<c:when test="${zone.status.toLowerCase() == 'working'}">
																		<span class='status-label status-online'>Online</span>
																	</c:when>
																	<c:when test="${zone.status.toLowerCase() == 'not working'}">
																		<span class='status-label status-offline'>Offline</span>
																	</c:when>
																	<c:when test="${zone.status.toLowerCase() == 'partially working'}">
																		<span class='status-label status-fault'>Fault</span>
																	</c:when>
																	<c:otherwise>-</c:otherwise>
																</c:choose>
															</c:set>
														</c:when>
														<c:when test="${zone.zoneType.toUpperCase() == 'SECURITY ALARM'}">
															<c:set var="sas">
																<c:choose>
																	<c:when test="${zone.status.toLowerCase() == 'working'}">
																		<span class='status-label status-online'>Online</span>
																	</c:when>
																	<c:when test="${zone.status.toLowerCase() == 'not working'}">
																		<span class='status-label status-offline'>Offline</span>
																	</c:when>
																	<c:when test="${zone.status.toLowerCase() == 'partially working'}">
																		<span class='status-label status-fault'>Fault</span>
																	</c:when>
																	<c:otherwise>-</c:otherwise>
																</c:choose>
															</c:set>
														</c:when>
														<c:when test="${zone.zoneType.toUpperCase() == 'FIRE ALARM'}">
															<c:set var="fas">
																<c:choose>
																	<c:when test="${zone.status.toLowerCase() == 'working'}">
																		<span class='status-label status-online'>Online</span>
																	</c:when>
																	<c:when test="${zone.status.toLowerCase() == 'not working'}">
																		<span class='status-label status-offline'>Offline</span>
																	</c:when>
																	<c:when test="${zone.status.toLowerCase() == 'partially working'}">
																		<span class='status-label status-fault'>Fault</span>
																	</c:when>
																	<c:otherwise>-</c:otherwise>
																</c:choose>
															</c:set>
														</c:when>
														<c:when test="${zone.zoneType.toUpperCase() == 'ETL'}">
															<c:set var="etl">
																<c:choose>
																	<c:when test="${zone.status.toLowerCase() == 'working'}">
																		<span class='status-label status-online'>Online</span>
																	</c:when>
																	<c:when test="${zone.status.toLowerCase() == 'not working'}">
																		<span class='status-label status-offline'>Offline</span>
																	</c:when>
																	<c:when test="${zone.status.toLowerCase() == 'partially working'}">
																		<span class='status-label status-fault'>Fault</span>
																	</c:when>
																	<c:otherwise>-</c:otherwise>
																</c:choose>
															</c:set>
														</c:when>
														<c:when test="${zone.zoneType.toUpperCase() == 'BACS'}">
															<c:set var="bacs">
																<c:choose>
																	<c:when test="${zone.status.toLowerCase() == 'working'}">
																		<span class='status-label status-online'>Online</span>
																	</c:when>
																	<c:when test="${zone.status.toLowerCase() == 'not working'}">
																		<span class='status-label status-offline'>Offline</span>
																	</c:when>
																	<c:when test="${zone.status.toLowerCase() == 'partially working'}">
																		<span class='status-label status-fault'>Fault</span>
																	</c:when>
																	<c:otherwise>-</c:otherwise>
																</c:choose>
															</c:set>
														</c:when>
													</c:choose>
												</c:forEach>
												
												<td>${cctv}</td>
												<td>${sas}</td>
												<td>${fas}</td>
												<td>${etl}</td>
												<td>${bacs}</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</c:when>
							<c:otherwise>
								<p class="text-center text-muted">No Branch Report data available</p>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</div>
		</div>

		<!-- Uptime Report Section -->
		<div class="report-container" id="uptimeReportSection">
    <div class="report-heading">Uptime Report</div>
    <div class="report-body">
        <div class="card">
            <div id="uptimeReportContent">
                <c:choose>
                    <c:when test="${uptimeReport.statusCode == 200 and not empty uptimeReport.payload}">
                        <table id="uptimeTable" class="table table-striped table-bordered" style="width:100%">
                            <thead>
                                <tr style="text-align:left;">
                                    <th>Branch Name</th>
                                    <th>IFSC</th>
                                    <th>Device Type</th>
                                    <th>Uptime (%)</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${uptimeReport.payload}">
                                    <tr style="text-align:left;">
                                        <td>${item.branchName}</td>
                                        <td>${item.ifsc}</td>
                                        <td>${item.deviceType}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${item.uptime >= 90}">
                                                    <span class="status-label status-online">${item.uptime}%</span>
                                                </c:when>
                                                <c:when test="${item.uptime >= 70}">
                                                    <span class="status-label status-fault">${item.uptime}%</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status-label status-offline">${item.uptime}%</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <p class="text-center text-muted">No Uptime Report data available</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
		
		
		<!-- CCTV TAT Report Section -->
		<div class="report-container" id="cctvTatReportSection">
			<div class="report-heading">
				CCTV TAT Report
			</div>
			<div class="report-body">
				<div class="card">
					<div id="cctvTatReportContent">
						<c:choose>
							<c:when test="${cctvTatReport.statusCode == 200 and not empty cctvTatReport.payload}">
								<table id="cctvTatTable" class="table table-striped table-bordered" style="width:100%">
									<thead>
										<tr style="text-align:left;">
											<th>Branch Name</th>
											<th>Ifsc Code</th>
											<th>Alert Type</th>
											<th>Created Time</th>
											<th>TAT</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="item" items="${cctvTatReport.payload}">
											<tr style="text-align:left;">
												<td>${item.branchName}</td>
												<td>${item.ifsc}</td>
												<td>${item.zone}</td>
												<td>Faulty since ${item.tat}</td>
												<td>${item.downtime != null ? item.downtime : '-'}</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</c:when>
							<c:otherwise>
								<table id="cctvTatTable" class="table table-striped table-bordered" style="width:100%">
									<thead>
										<tr style="text-align:left;">
											<th>Branch Name</th>
											<th>Ifsc Code</th>
											<th>Alert Type</th>
											<th>Created Time</th>
											<th>TAT</th>
										</tr>
									</thead>
									<tbody>
										<tr><td colspan="5" class="text-center">No Alarms Found</td></tr>
									</tbody>
								</table>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</div>
		</div>
		
		<!-- FAS TAT Report Section -->
		<div class="report-container" id="fasTatReportSection">
			<div class="report-heading">
				FAS TAT Report
			</div>
			<div class="report-body">
				<div class="card">
					<div id="fasTatReportContent">
						<c:choose>
							<c:when test="${fasTatReport.statusCode == 200 and not empty fasTatReport.payload}">
								<table id="fasTatTable" class="table table-striped table-bordered" style="width:100%">
									<thead>
										<tr style="text-align:left;">
											<th>Branch Name</th>
											<th>Ifsc Code</th>
											<th>Alert Type</th>
											<th>Created Time</th>
											<th>TAT</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="item" items="${fasTatReport.payload}">
											<tr style="text-align:left;">
												<td>${item.branchName}</td>
												<td>${item.ifsc}</td>
												<td>${item.zone}</td>
												<td>Faulty since ${item.tat}</td>
												<td>${item.downtime != null ? item.downtime : '-'}</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</c:when>
							<c:otherwise>
								<table id="fasTatTable" class="table table-striped table-bordered" style="width:100%">
									<thead>
										<tr style="text-align:left;">
											<th>Branch Name</th>
											<th>Ifsc Code</th>
											<th>Alert Type</th>
											<th>Created Time</th>
											<th>TAT</th>
										</tr>
									</thead>
									<tbody>
										<tr><td colspan="5" class="text-center">No Alarms Found</td></tr>
									</tbody>
								</table>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</div>
		</div>
		
		<!-- SAS TAT Report Section -->
		<div class="report-container" id="sasTatReportSection">
		    <div class="report-heading">
		        SAS TAT Report
		    </div>
		    <div class="report-body">
		        <div class="card">
		            <div id="sasTatReportContent">
		                <c:choose>
							<c:when test="${sasTatReport.statusCode == 200 and not empty sasTatReport.payload}">
								<table id="sasTatTable" class="table table-striped table-bordered" style="width:100%">
									<thead>
										<tr style="text-align:left;">
											<th>Branch Name</th>
											<th>Ifsc Code</th>
											<th>Alert Type</th>
											<th>Created Time</th>
											<th>TAT</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="item" items="${sasTatReport.payload}">
											<tr style="text-align:left;">
												<td>${item.branchName}</td>
												<td>${item.ifsc}</td>
												<td>${item.zone}</td>
												<td>Faulty since ${item.tat}</td>
												<td>${item.downtime != null ? item.downtime : '-'}</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</c:when>
							<c:otherwise>
								<table id="sasTatTable" class="table table-striped table-bordered" style="width:100%">
									<thead>
										<tr style="text-align:left;">
											<th>Branch Name</th>
											<th>Ifsc Code</th>
											<th>Alert Type</th>
											<th>Created Time</th>
											<th>TAT</th>
										</tr>
									</thead>
									<tbody>
										<tr><td colspan="5" class="text-center">No Alarms Found</td></tr>
									</tbody>
								</table>
							</c:otherwise>
						</c:choose>
		            </div>
		        </div>
		    </div>
		</div>

		<!-- BACS TAT Report Section -->
		<div class="report-container" id="bacsTatReportSection">
		    <div class="report-heading">
		        BACS TAT Report
		    </div>
		    <div class="report-body">
		        <div class="card">
		            <div id="bacsTatReportContent">
		                <c:choose>
							<c:when test="${bacsTatReport.statusCode == 200 and not empty bacsTatReport.payload}">
								<table id="bacsTatTable" class="table table-striped table-bordered" style="width:100%">
									<thead>
										<tr style="text-align:left;">
											<th>Branch Name</th>
											<th>Ifsc Code</th>
											<th>Alert Type</th>
											<th>Created Time</th>
											<th>TAT</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="item" items="${bacsTatReport.payload}">
											<tr style="text-align:left;">
												<td>${item.branchName}</td>
												<td>${item.ifsc}</td>
												<td>${item.zone}</td>
												<td>Faulty since ${item.tat}</td>
												<td>${item.downtime != null ? item.downtime : '-'}</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</c:when>
							<c:otherwise>
								<table id="bacsTatTable" class="table table-striped table-bordered" style="width:100%">
									<thead>
										<tr style="text-align:left;">
											<th>Branch Name</th>
											<th>Ifsc Code</th>
											<th>Alert Type</th>
											<th>Created Time</th>
											<th>TAT</th>
										</tr>
									</thead>
									<tbody>
										<tr><td colspan="5" class="text-center">No Alarms Found</td></tr>
									</tbody>
								</table>
							</c:otherwise>
						</c:choose>
		            </div>
		        </div>
		    </div>
		</div>

		<!-- ETL TAT Report Section -->
		<div class="report-container" id="etlTatReportSection">
		    <div class="report-heading">
		        ETL TAT Report
		    </div>
		    <div class="report-body">
		        <div class="card">
		            <div id="etlTatReportContent">
		                <c:choose>
							<c:when test="${etlTatReport.statusCode == 200 and not empty etlTatReport.payload}">
								<table id="etlTatTable" class="table table-striped table-bordered" style="width:100%">
									<thead>
										<tr style="text-align:left;">
											<th>Branch Name</th>
											<th>Ifsc Code</th>
											<th>Alert Type</th>
											<th>Created Time</th>
											<th>TAT</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="item" items="${etlTatReport.payload}">
											<tr style="text-align:left;">
												<td>${item.branchName}</td>
												<td>${item.ifsc}</td>
												<td>${item.zone}</td>
												<td>Faulty since ${item.tat}</td>
												<td>${item.downtime != null ? item.downtime : '-'}</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</c:when>
							<c:otherwise>
								<table id="etlTatTable" class="table table-striped table-bordered" style="width:100%">
									<thead>
										<tr style="text-align:left;">
											<th>Branch Name</th>
											<th>Ifsc Code</th>
											<th>Alert Type</th>
											<th>Created Time</th>
											<th>TAT</th>
										</tr>
									</thead>
									<tbody>
										<tr><td colspan="5" class="text-center">No Alarms Found</td></tr>
									</tbody>
								</table>
							</c:otherwise>
						</c:choose>
		            </div>
		        </div>
		    </div>
		</div>

	</div>

<script>
// Global variables
var branchReportTitle = "Branch Report";
var branchAboutText = "ABOUT: Branch Report provides an overview of the status of various systems across different branches. It includes information on CCTV, SAS, FAS, ETL, and BACS systems, helping to ensure that all security measures are functioning properly.";

var uptimeReportTitle = "Uptime Report";
var uptimeAboutText = "ABOUT: Uptime Report provides clear insights into system performance by showing how consistently the system remains operational.";

var cctvTatReportTitle = "CCTV TAT Report";
var cctvTatAboutText = "ABOUT: CCTV TAT Report provides clear insights into system performance by showing how consistently the system remains operational.";

var fasTatReportTitle = "FAS TAT Report";
var fasTatAboutText = "ABOUT: FAS TAT Report provides clear insights into system performance by showing how consistently the system remains operational.";

var sasTatReportTitle = "SAS TAT Report";
var sasTatAboutText = "ABOUT: SAS TAT Report provides clear insights into system performance by showing how consistently the system remains operational.";

var bacsTatReportTitle = "BACS TAT Report";
var bacsTatAboutText = "ABOUT: BACS TAT Report provides clear insights into system performance by showing how consistently the system remains operational.";

var etlTatReportTitle = "ETL TAT Report";
var etlTatAboutText = "ABOUT: ETL TAT Report provides clear insights into system performance by showing how consistently the system remains operational.";

function convertImageToBase64(url, callback) {
    var img = new Image();
    img.crossOrigin = 'Anonymous';
    img.onload = function () {
        var canvas = document.createElement('canvas');
        canvas.width = img.width;
        canvas.height = img.height;
        var ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0);
        var dataURL = canvas.toDataURL('image/png');
        callback(dataURL);
    };
    img.src = url;
}

function processUptimeData(data) {
    var grouped = {};
    
    // Group data by branchCode
    data.forEach(function(item) {
        if (!grouped[item.branchCode]) {
            grouped[item.branchCode] = {
                branchName: item.branchName,
                ifsc: item.ifsc,
                devices: {}
            };
        }
        grouped[item.branchCode].devices[item.deviceType] = item;
    });

    // Build table rows
    var tableBody = '';
    Object.keys(grouped).forEach(function(branchCode) {
        var branch = grouped[branchCode];
        tableBody += "<tr>";
        tableBody += "<td>" + branch.branchName + "</td>";
        tableBody += "<td>" + branch.ifsc + "</td>";
        tableBody += generateDeviceCell(branch.devices["CCTV"]);
        tableBody += generateDeviceCell(branch.devices["SECURITY ALARM"]);
        tableBody += generateDeviceCell(branch.devices["FIRE ALARM"]);
        tableBody += generateDeviceCell(branch.devices["ETL"]);
        tableBody += generateDeviceCell(branch.devices["BACS"]);
        tableBody += "</tr>";
    });
    
    $('#uptimeTable tbody').html(tableBody);
    
    // Initialize DataTable
    initializeUptimeDataTable();
}

function generateDeviceCell(deviceData) {
    if (!deviceData) {
        return "<td>-</td>";
    }

    let uptime = parseInt(deviceData.uptime || "0");
    let statusClass = "";

    if (uptime === 0) {
        if (deviceData.status === "No logs found for today") {
            statusClass = "status-label status-online"; 
            return "<td><span class='" + statusClass + "'>" + "100" + "%</span></td>";
        } else {
            statusClass = "status-label status-fault"; 
            return "<td><span class='" + statusClass + "'>" + uptime + "%</span></td>";
        }
    } else if (uptime >= 75) {
        statusClass = "status-label status-online"; 
        return "<td><span class='" + statusClass + "'>" + uptime + "%</span></td>";
    } else {
        statusClass = "status-label status-fault"; 
        return "<td><span class='" + statusClass + "'>" + uptime + "%</span></td>";
    }
}

$(document).ready(function() {
    // Initialize DataTables for all tables that don't need special processing
    initializeBranchDataTable();
    initializeTATDataTable("cctvTatTable", cctvTatReportTitle, cctvTatAboutText);
    initializeTATDataTable("fasTatTable", fasTatReportTitle, fasTatAboutText);
    initializeTATDataTable("sasTatTable", sasTatReportTitle, sasTatAboutText);
    initializeTATDataTable("bacsTatTable", bacsTatReportTitle, bacsTatAboutText);
    initializeTATDataTable("etlTatTable", etlTatReportTitle, etlTatAboutText);
    
    // Export button handlers
    $("#branchExcelBtn").click(function() {
        $("#branchTable").DataTable().button('.buttons-excel').trigger();
    });
    
    $("#branchPdfBtn").click(function() {
        $("#branchTable").DataTable().button('.buttons-pdf').trigger();
    });
    
    $("#branchPrintBtn").click(function() {
        $("#branchTable").DataTable().button('.buttons-print').trigger();
    });
    
    // Similar handlers for other reports...
});

function initializeBranchDataTable() {
    convertImageToBase64('./logo/digitals.png', function (base64Logo) {
        $('#branchTable').DataTable({
            responsive: true,
            paging: false,
            info: false,
            searching: false,
            dom: 'Bfrtip',
            buttons: [
                {
                    extend: 'excelHtml5',
                    text: 'Excel',
                    title: function() {
                        var now = new Date();
                        var dateTime = now.toLocaleString();