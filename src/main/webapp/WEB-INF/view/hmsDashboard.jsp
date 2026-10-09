<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.util.List"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.time.LocalDate"%>
<%@ page import="org.springframework.http.HttpStatus"%>
<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>DI-HMS</title>
<!-- Favicon icon -->
<link rel='stylesheet'
	href='https://cdn-uicons.flaticon.com/2.1.0/uicons-solid-rounded/css/uicons-solid-rounded.css'>
<link rel="icon" type="image/png" sizes="16x16"
	href="./images/favicon.png">
<link rel="stylesheet"
	href="./vendor/owl-carousel/css/owl.carousel.min.css">
<link rel="stylesheet"
	href="./vendor/owl-carousel/css/owl.theme.default.min.css">
<link href="./vendor/jqvmap/css/jqvmap.min.css" rel="stylesheet">
<link href="./dashboardCSS/style.css" rel="stylesheet">
<script src="./externalJS/fontawesome.js"></script>
<link href="./fonts/poppins.css" rel="stylesheet">
<link href="./externalCSS/select2.min.css" rel="stylesheet" />
<link href="./css/hmsDashboard.css" rel="stylesheet" />
<link href="./css/hmsDashboardExtendedStyle.css" rel="stylesheet" />

<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.css" />




<!-- DATATABLES CSS Links -->

<link rel="stylesheet"
	href="./externalCSS/dataTables.bootstrap4.min.css">
<link rel="stylesheet"
	href="./externalCSS/responsive.bootstrap4.min.css">

<!-- DATATABLES JS Links -->

<script src="./externalJS/sweetalert2.all.min.js"></script>
<script src="./externalJS/jquery-3.7.0.js"></script>
<script src="./externalJS/jquery.dataTables.min.js"></script>
<script src="./externalJS/dataTables.bootstrap4.min.js"></script>
<script src="./externalJS/responsive.bootstrap4.min.js"></script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDMKndzFcfiysofJH9zTVJtOCeIH70nKcc"></script>


<!------------------------------------------------------------ AM CHARTS----------------------------------------------------------------->

<script src="https://cdn.amcharts.com/lib/5/index.js"></script>
<script src="https://cdn.amcharts.com/lib/5/percent.js"></script>
<script src="https://cdn.amcharts.com/lib/5/themes/Animated.js"></script>


</head>

<body style="font-family: Poppins;">


 <div id="fullPageLoader" class="full-page-loader" style="display: none;">
        <div class="loader-content">
            <div class="spinner"></div>
            <div class="loader-text">Loading...</div>
        </div>
    </div>

 <div class="ai-popup-overlay" id="aiPopupOverlay">
        <div class="ai-popup">
            <!-- Sparkle Effects -->
            <div class="sparkle"><i class="fas fa-star"></i></div>
            <div class="sparkle"><i class="fas fa-sparkles"></i></div>
            <div class="sparkle"><i class="fas fa-star"></i></div>
            <div class="sparkle"><i class="fas fa-sparkles"></i></div>
            
            <!-- Close Button -->
            <button class="ai-popup-close" id="closePopup">
                <i class="fas fa-times"></i>
            </button>
            
            <!-- AI Icon -->
            <div class="ai-icon">
                <i class="fas fa-robot"></i>
            </div>
            
            <!-- Title -->
            <h2>We're AI-Enabled!</h2>
            
          
            <div class="subtitle">
                Your HMS is now smarter than ever
            </div>
            
     
            <ul class="ai-features">
                <li>
                    <i class="fas fa-brain"></i>
                    <span>Intelligent Monitoring</span>
                </li>
                <li>
                    <i class="fas fa-comments"></i>
                    <span>AI Assistant Ready</span>
                </li>
                <li>
                    <i class="fas fa-chart-line"></i>
                    <span>Predictive Analytics</span>
                </li>
            </ul>
            
    
            <div class="progress-bar">
                <div class="progress-fill"></div>
            </div>
        </div>
    </div>

<img src="<c:url value='./logos/image/${logo.id}'/>" alt="Logo"
									class="mr-1 bank_logo" width="90"  style="display:none ;"/>




	<input id="userId" value="${userId}" style="display: none;">
	<input id="loggedInBranch" value="${branchId}" style="display: none;">
	<input id="remarksList" value="${remarksList.payload}"
		style="display: none;">
<div class="container-fluid " id="accordion">
    <!-- Row 1: Status of Sites and Map -->
 <div class="row mb-3">
    <div class="col-12 col-sm-12">
        <div class="col-12">
            <div class="card mb-3 flex-grow-1" style="background-color: #ffff;">
                <div class="row">
                    <div class="col-md-12">
                        <h5 class="heading">View Branch Wise Info</h5>
                    </div>
                </div>
                <div class="card-body">
                            <div class="row">
            <!-- Head-Office Branch Dropdown -->
            <div class="custom-field one col-md-3">
                <div class="input-boxx active-greyy">
                    <label class="input-labell">Select Head-Office Branch</label>
                    <select class="input-11 form-control form-control-sm" name="headIdDropdown" id="headIdDropdown">
                        <!-- Options will be populated via AJAX -->
                    </select>
                </div>
            </div>

            <!-- Zone Dropdown -->
            <div class="custom-field one col-md-3">
                <div class="input-boxx active-greyy">
                    <label class="input-labell">Select Zone</label>
                    <select class="input-11 form-control form-control-sm" name="zoneIdDropdown" id="zoneIdDropdown">
                        <!-- Options will be populated dynamically -->
                    </select>
                </div>
            </div>
            
             <!-- Branch Dropdown -->
            <div class="custom-field one col-md-3">
                            <div class="input-boxx active-greyy">
                                <label class="input-labell">Select Branch</label>
                                <select class="input-11 form-control form-control-sm" name="branchIdDropdown" id="branchIdDropdown">
                                    <!-- Options will be populated dynamically -->
                                </select>
                            </div>
                        </div>

           
     <div class="custom-field one col-md-1.5">
                            <div class="input-boxx active-greyy">
                                 <div class="col-md-12 text-center">
                                    <button type="button" class="btn btn-primary" id="searchButton">
                                        SEARCH
                                    </button>
                                 </div>
                            </div>
                        </div>

                        <!-- Reset Button -->
                        <div class="custom-field one col-md-1.5">
                            <div class="input-boxx active-greyy">
                                 <div class="col-md-12 text-center">
                                    <button type="button" class="btn btn-secondary" id="resetButton">
                                        RESET
                                    </button>
                                 </div>
                            </div>
                        </div>

          
        </div>
                </div>
				
				
				
				
				
            </div>
        </div>
    </div>
</div>

    
    
    
        <!-- Status of Sites -->
        <div class="col-12 col-sm-12 ">
	<div class="col-12">
    <div class="card mb-3 flex-grow-1" style="background-color: #ffff;">
        <div class="row">
            <div class="col-md-12">
                <h5 class="heading">Status of Sites</h5>
            </div>
        </div>
        <div class="card-body">
            <div class="row">
                <!-- Total Devices -->
<!-- Total Sites -->
<div class="col-md-3 col-sm-12">
    <div class="card text-white bg-white mb-2 position-relative" style="width: 100%; overflow: hidden; border: 3px solid royalblue; border-right: none; border-bottom: none;">
        <!-- Card Header -->
        <h4 class="card-header text-primary">Total Sites</h4>
        
        <!-- Background Icon -->
        <i class="fas fa-university position-absolute" 
           style="font-size: 6rem; color: rgba(2, 139, 215, 0.4); top: 20%; right: 10%;"></i>
        
        <!-- Card Body -->
        <div class="card-body py-2">
            <h4 class="stat-digit text-dark">${deviceCount.payload[0]}</h4>
        </div>
    </div>
</div>

<!-- Working Devices -->
<div class="col-md-3 col-sm-12">
    <div class="card text-white bg-white mb-2 position-relative" style="width: 100%; overflow: hidden; border: 3px solid green; border-right: none; border-bottom: none;">
        <!-- Card Header -->
        <h4 class="card-header" style="color: green;">Working</h4>
        
        <!-- Background Icon -->
        <i class="fas fa-university position-absolute" 
           style="font-size: 6rem; color: rgba(0, 128, 0, 0.4); top: 20%; right: 10%;"></i>
        
        <!-- Card Body -->
        <div class="card-body py-2">
            <h4 class="stat-digit text-dark">
                ${deviceStatusCount.payload.workingDeviceCount}/${deviceCount.payload[0]}
            </h4>
        </div>
    </div>
</div>

<!-- Not Working Devices -->
<div class="col-md-3 col-sm-12">
    <div class="card text-white bg-white mb-2 position-relative" style="width: 100%; overflow: hidden; border: 3px solid crimson; border-right: none; border-bottom: none;">
        <!-- Card Header -->
        <h4 class="card-header" style="color: crimson;">Not Working</h4>
        
        <!-- Background Icon -->
        <i class="fas fa-university position-absolute" 
           style="font-size: 6rem; color: rgba(220, 20, 60, 0.4); top: 20%; right: 10%;"></i>
        
        <!-- Card Body -->
        <div class="card-body py-2">
            <h4 class="stat-digit text-dark">
                ${deviceStatusCount.payload.notWorkingCount}/${deviceCount.payload[0]}
            </h4>
        </div>
    </div>
</div>


<!-- SIA Devices Card -->
<div class="col-md-3 col-sm-12">
    <div class="card text-white mb-2 position-relative" 
         style="width: 100%; overflow: hidden; border: 3px solid #4F1C51; border-right: none; border-bottom: none; cursor: pointer;"
         data-bs-toggle="modal" data-bs-target="#siaDevicesModal"
         onclick="loadSiaDeviceDetails()">
        
        <!-- Card Header -->
        <h4 class="card-header" style="color: #4F1C51;">Total SIA Devices</h4>
        <i class="fa-solid fa-wifi position-absolute" style="font-size: 5rem; color: rgba(178, 165, 255, 0.4); top: 20%; right: 10%;"></i>

        <div class="card-body py-2">
            <h4 class="stat-digit text-dark">
                ${siaCountList.payload.siaDeviceCount} 
            </h4>
        </div>
    </div>
</div>


            </div>
        </div>
    </div>
</div>

        </div>

        <!-- Map Card -->
        <div class="col-12 col-sm-12  mt-0 ">
            <div class="card mx-0 flex-grow-1 mapCard">
                <div class="row">
                    <div class="col-md-12">
                        <h5 class="heading">Branch Locations</h5>
                    </div>
                </div>
                <div class="m-3">
                    <div id="map"></div>
                </div>
               
            </div>
        </div>
    </div>

    <!-- Row 2: Status of Connecting Devices -->
    <div class="row d-flex align-items-stretch">
        <div class="card flex-grow-1 mx-4">
            <div class="row mt-0">
                <div class="col-12">
                    <h5 class="heading">Status of Connecting Devices</h5>
                </div>
            </div>
            <div class="card-body py-2">
                <!-- Legends -->
                <div class="legend-container">
                    <div class="legend-item">
                        <div class="legend-color" style="background-color: #5DB996;"></div>
                        <span class="lColor">Working</span>
                    </div>
                    <div class="legend-item">
                        <div class="legend-color" style="background-color: #ECE852;"></div>
                        <span class="lColor">Partially Working</span>
                    </div>
                    <div class="legend-item">
                        <div class="legend-color" style="background-color: #FB4141;"></div>
                        <span class="lColor">Not Working</span>
                    </div>
                </div>
                <!-- Doughnut Charts -->
                <div class="row">
                <div class="col-lg-1 mb-2"></div>
                    <div class="col-lg-2 mb-2" id="cctv-chart-container">
                        <canvas id="cctvChart" onclick="toggleCollapseAndScroll();"></canvas>
                    </div>
                    <div class="col-lg-2 mb-2" id="security-alarm-chart-container">
                        <canvas id="securityAlarmChart" onclick="toggleCollapseAndScroll();"></canvas>
                    </div>
                    <div class="col-lg-2 mb-2" id="fire-alarm-chart-container">
                        <canvas id="fireAlarmChart" onclick="toggleCollapseAndScroll();"></canvas>
                    </div>
                    <div class="col-lg-2 mb-2" id="bacs-chart-container">
                        <canvas id="bacsChart" onclick="toggleCollapseAndScroll();"></canvas>
                    </div>
                    <div class="col-lg-2 mb-2" id="etl-chart-container">
                        <canvas id="etlChart" onclick="toggleCollapseAndScroll();"></canvas>
                    </div>
                    <div class="col-lg-1 mb-2"></div>
                </div>
                 <div class="col-12" id="headingTwo">
                    <a href="#collapseTwo">
                        <button id="viewStatusButton" class="btn btn-sm btn-primary" data-toggle="collapse"
                            data-target="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo"
                            onclick="fetchZonesForReportTable();">Report</button>
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>


	<!---------------------------------------------------------------- BRANCH LOCATIONS--------------------------------------------------->






	<div class="col-lg-12 col-md-12">
		<div>
		
		
			<div class="card">

			<div id="collapseTwo" class="collapse" aria-labelledby="headingTwo" data-parent="#accordion">
    <div class="row">
        <div class="col-md-12 col-sm-12">
            <h5 class="heading deviceReports">Device Reports
          
                <button id="closeReportButton" class="btn btn-peace mr-1 float-right" data-toggle="collapse"
                    data-target="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
                    <i class="fa-solid fa-xmark fa-xl" style="color: white;"></i>
                </button>
                
            </h5>
            
        </div>
       
    </div>

    <div class="container ml-1">
        <div class="row drp float-left">
            <!-- Dropdown Filters Here -->
            <!-- (Same content as before for Zone, Branch, and Device Filters) -->
        </div>
    </div>

   <div class="card-body">
    <div class="table-responsive mb-2" style="min-height:300px; max-height:550px; overflow-y: auto;">
        <table class="table table-bordered mb-0" id="zoneReportTable">
            <thead style="color: #fff; position: sticky; top: 0; z-index: 1; background: #028bd7;">
                <tr>
                    <th style="display: none">Branch Code</th>
                    <th style="text-align: right">S.No</th>
                    <th class="text-left">Branch Name</th>
                    <th>Bank Name</th>
                    <th style="text-align: right">Contact No.</th>
                    <th colspan="5">View Zones & Health Details</th>
                    
                </tr>
            </thead>
            <tbody style="color: black; cursor:pointer;" id="reportTableBody">
                <!-- Rows will be populated dynamically -->
            </tbody>
        </table>
    </div>
</div>

</div>


			</div>
			<div class="card">

				<div id="collapseOne" class="collapse" aria-labelledby="headingOne"
					data-parent="#accordion">

					<div class="row">
						<div class="col-md-12 col-sm-12">
							<h5 class="heading">Not Working Devices<div class="row float-right mb-2">
								<button id="viewStatusButton" class="btn btn-peace mr-3"
									data-toggle="collapse" data-target="#collapseOne"
									aria-expanded="false" aria-controls="collapseOne">
									<i class="fa-solid fa-xmark fa-xl" style="color: white;"></i>
								</button>
							</div></h5>
						</div>
						
					</div>


					<div class="card-body">

						<div class="" style="">



							<div class="card-header" id="totalDevice">
								<div class="table-responsive  table-bordered mb-2"
									style="max-height: 250px; overflow-y: auto;">
									<table class="table mb-0" >
										<thead
											style="color: #fff; position: sticky; top: 0; z-index: 1; background: #028bd7;">
											<tr>
												<th>S.No</th>
												<th>Branch Name</th>
												<th style="display: none;">Bank Name</th>
												<th style="text-align: right;">Phone No.</th>
												<th>Not working devices</th>
												<th>Alert Type</th>
												<th style="text-align: right;">Date & Time</th>
											</tr>
										</thead>
										<tbody style="color: black; ">
											<c:set var="serialNumber" value="1" />
											<!-- Initialize serial number counter -->
											<c:forEach items="${notWorkingDevices.payload}" var="device"
												varStatus="loop">
												<c:if
													test="${not empty device.report and not empty device.report.zoneValues}">
													<c:set var="branchDisplayed" value="false" />
													<c:forEach items="${device.report.zoneValues}"
														var="zoneValue">
														<tr>
															<td>${serialNumber}</td>
															<c:if test="${!branchDisplayed}">
																<td rowspan="${fn:length(device.report.zoneValues)}">${device.branchName}</td>
																<c:set var="branchDisplayed" value="true" />
															</c:if>
															<td style="display: none;">${device.bankName}</td>
															<td style="text-align: right;">${device.mobile}</td>
															<c:set var="splitZoneValue"
																value="${fn:split(zoneValue, ':')}" />
															<td>${splitZoneValue[0]}</td>
															<td>${splitZoneValue[1]}</td>
															<td class="datetime"
																data-datetime="${device.report.DateTime}"
																style="text-align: right;"></td>
														</tr>
														<c:set var="serialNumber" value="${serialNumber + 1}" />
														<!-- Increment serial number -->
													</c:forEach>
												</c:if>
											</c:forEach>
										</tbody>

									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>


		</div>
	</div>


	<div class="row mx-2">
		<div class="col-lg-12 col-md-12 col-sm-12 mt-0">
			<div class="card mx-0" id="branchDetailsCard"
				style="min-height: 422px;">
				<div class="row">
					<div class="col-md-12">
						<h5 class="heading">Branch Details</h5>
					</div>
					<div class="col-md-1 mt-4">
						<div id="minimizedModalPreviews"></div>
					</div>
				</div>
				<div class="container ml-1">
					<div class="row drp float-left">
						<div class="filter-dropdown col-md-4 col-sm-4 mt-1">
							<div class="dropdown">
								<button class="btn setButton dropdown-toggle" type="button"
									data-toggle="dropdown" aria-haspopup="true"
									aria-expanded="false">Select Zone</button>
								<div class="dropdown-menu" aria-labelledby="branchZoneFilter2"
									id="branchZoneFilter2" style="cursor: pointer; color: black;">
									<a class="dropdown-item" data-value="">All Zones</a>
									<c:forEach var="zones" items="${zoneList.payload}">
										<a class="dropdown-item" data-value="${zones.branchCode}">${zones.branchDesc}->${zones.branchCode}</a>
									</c:forEach>
								</div>
							</div>
						</div>
						<div class="filter-dropdown col-md-4 col-sm-4 mt-1">
							<div class="dropdown">
								<button class="btn setButton dropdown-toggle" type="button"
									data-toggle="dropdown" aria-haspopup="true"
									aria-expanded="false">Select Branch</button>
								<div class="dropdown-menu" aria-labelledby="zoneReportFilter2"
									id="zoneReportFilter2" style="cursor: pointer; color: black;">
									<a class="dropdown-item changeTextofZones" data-value="">All
										Branches</a>
									<c:forEach var="branches" items="${childBranch.payload}">
										<a class="dropdown-item getOldBranches changeTextofZones"
											data-value="${branches.branchCode}">${branches.branchDesc}->${branches.branchCode}</a>
									</c:forEach>
								</div>
							</div>
						</div>
						<div class="filter-dropdown col-md-4 col-sm-12 mt-1">
							<div class="dropdown">
								<button class="btn setButton dropdown-toggle" type="button"
									data-toggle="dropdown" aria-haspopup="true"
									aria-expanded="false">Select Status</button>
								<div class="dropdown-menu" aria-labelledby="statusDropdown"
									id="status" style="cursor: pointer; color: black;">
									<a class="dropdown-item changeTextofZones" data-value="">All
										Status</a> <a class="dropdown-item" data-value="Active">Active</a>
									<a class="dropdown-item" data-value="InActive">InActive</a>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="card-header" id="branchesDetailsTable">
					<div class="table-responsive mb-2" id="branchTableWrapper"
						style="max-height: 350px; overflow-y: auto;">
						<table class="table table-bordered mb-0">
							<thead
								style="color: #fff; position: sticky; top: 0; z-index: 1; background: #028bd7;">
								<tr>
									<th style="display: none;">Branch Code</th>
									<th style="display: none;">Bank Name</th>
									<th>S.No</th>
									<th>Branch Name</th>
									<th class="">IFSC</th>
									<th class="text-right">Contact No.</th>
									<th>Address</th>
									<th>Status</th>
								</tr>
							</thead>
							<tbody id="branchTableBody" class="" onclick="scrollToTable();">
								<c:choose>
									<c:when test="${empty childBranch.payload}">
										<tr>
											<td colspan="7" class="text-center"
												style="font-size: 15px; color: black;">No data present</td>
										</tr>
									</c:when>
									<c:otherwise>
										<c:forEach var="childBranch" varStatus="status"
											items="${childBranch.payload}">
											<tr style="color: black; cursor: pointer"
												onclick="showDeviceHealth('${childBranch.branchCode}', this)">
												<td><i class="fa-solid fa-caret-right fa-lg mx-1"></i>${status.index + 1}</td>
												<td id="branchValue" value="${childBranch.branchCode}"
													style="display: none;">${childBranch.branchCode}</td>
												<td id="branchValue" value="${childBranch.branchCode}"
													style="display: none;">${childBranch.bank}</td>
												<td id="branchValue" value="${childBranch.branchCode}">${childBranch.branchDesc}</td>
												<td id="branchValue" value="${childBranch.branchCode}" class="">${childBranch.ifsc}</td>
												<td style="text-align: right;" id="branchValue"
													value="${childBranch.branchCode}" class="text-right">${childBranch.mobile}</td>
												<td id="branchValue" value="${childBranch.branchCode}">${childBranch.address1}</td>
												<td id="branchValue" value="${childBranch.branchCode}">
													<i class="fas fa-shield-alt fa-2x mr-2"
													style="color: ${childBranch.status == 'Active' ? '#2c832a' : '#C70039'};"
													title=""></i>${childBranch.status}
												</td>
											</tr>
										</c:forEach>
									</c:otherwise>
								</c:choose>
							</tbody>
						</table>
					</div>

				</div>
			</div>
		</div>
	</div>



<div class="col-lg-12 col-md-12" id="statusTable" style="display: none;">
    <div class="">
        <div class="row" id="inputFilter" onchange="filterInputs(this.value)">
       
        </div>
   
        <div class="card-header">
            <div class="table-responsive table-striped table-bordered mb-2" style="max-height: 250px; overflow-y: auto; font-size: 12px;">
                <table class="table mb-0">
                    <thead style="color: #fff; position: sticky; top: 0; z-index: 1; background: #028bd7; text-align: center;">
                        <tr>
                            <th>Device Name</th>
                            <th>Status</th>
                            <th>Alert Type</th>
                            <th>Status</th>
                            
                        </tr>
                    </thead>
                    <tbody id="activeZonesBodyOne" style="color: black; text-align: center; font-size: 14px;"></tbody>
                </table>
            </div>
        </div>
    </div>
</div>





	<div class="row mx-2 d-none">


		<div class=" col-lg-12 col-md-12 col-sm-12">
			<div class="card" style="">

				<div class="row">
					<div class="col-md-12">
						<h5 class="heading">Recent Activities</h5>
					</div>
				</div>
				<div class="container">
					<div class="col-lg-12">
						<div class="row">
							<div class="row col-lg-12">
								<div class="col-lg-2 col-md-2 col-sm-6 mt-1">
									<label for="dateFilter"
										style="font-weight: 600; color: #028bd7"> Start Date</label>
								</div>
								<div class="col-lg-2 col-md-2 col-sm-6 mt-1">
									<input
										class="form-control form-control-sm rounded typeahead stylish-input"
										type="date" id="startDate" value="">
								</div>

								<div class="col-lg-2 col-md-2 col-sm-6 mt-1">
									<label for="dateFilter"
										style="font-weight: 600; color: #028bd7"> End Date</label>
								</div>
								<div class="col-lg-2 col-md-2 col-sm-6 mt-1">
									<input
										class="form-control form-control-sm rounded typeahead stylish-input"
										type="date" id="endDate" value="<%=LocalDate.now()%>">
								</div>

								<div class="col-lg-2 col-sm-6 col-md-2 mt-1">
									<label style="font-weight: 600; color: #028bd7">Select
										Device</label>
								</div>
								<div class="col-lg-2 col-sm-6 col-md-2">
									<div class="btn-group">
										<button type="button"
											class="btn btn-primary dropdown-toggle active-device w-100 btn-block"
											data-toggle="dropdown" aria-haspopup="true"
											aria-expanded="false">Select Device</button>
										<div class="dropdown-menu deviceFilter"
											style="cursor: pointer; color: #1d1d1d;">
											<a class="dropdown-item activeZoneFilter">SECURITY ALARM</a>
											<a class="dropdown-item activeZoneFilter">FIRE ALARM</a> <a
												class="dropdown-item activeZoneFilter">CCTV</a>
										</div>
									</div>
								</div>

							</div>

							<div class="row col-lg-12 col-sm-12 col-md-12 mt-2">

								<div class="col-lg-2 col-sm-6 mt-1">
									<label for="monthFilter"
										style="font-weight: 600; color: #028bd7; display: none;">Select
										Month</label>
								</div>
								<div class="col-lg-2  col-md-2 col-sm-6 mt-1">
									<input
										class="form-control form-control-sm rounded typeahead stylish-input"
										type="month" id="monthFilter" style="display: none;">
								</div>

								<div class="col-lg-2 col-sm-2 col-md-2 mt-1">
									<label style="font-weight: 600; color: #028bd7; display: none;">Select
										Year</label>
								</div>
								<div class="col-lg-4 col-sm-4 col-md-4">
									<div class="btn-group">
										<button type="button"
											class="btn btn-primary dropdown-toggle year-dropdown w-100 btn-block"
											data-toggle="dropdown" aria-haspopup="true"
											aria-expanded="false" style="display: none;">Select
											Year</button>
										<div class="dropdown-menu yearFilter"
											style="cursor: pointer; color: #1d1d1d;"></div>
									</div>
								</div>



							</div>



						</div>
					</div>
				</div>

				<!-- Example single danger button -->

				<div class="card-body">
					
					<div class="row">
						<div class="col-lg-4 col-md-4 col-sm-12">

							<canvas id="sold-product"></canvas>

						</div>


						<div class="col-lg-8 col-md-8 col-sm-12 mx-0">
							<div class="table-responsive"
								style="max-height: 220px; overflow-y: auto;">
								<!-- <table id="auditTable" class="table table-bordered" style="color: #003d87;">
									<thead
										style="color: #fff; position: sticky; top: 0; z-index: 1; background: #028bd7;">
										<tr>
											<th class="text-left col-3">Service Provider</th>
											<th class="text-left col-2">Alert Zones</th>
											<th class="text-left col-3">Zone</th>
											<th style="text-align: right;" class="col-3">Alert Time Stamp</th>
											<th class="text-left">Status</th>
										</tr>
									</thead>
									<tbody>

									</tbody>
								</table> -->
							</div>
						</div>
					</div>

				</div>
			</div>
		</div>


	</div>





<div class="row mx-2">
    <div class="col-lg-12 col-md-12 col-sm-12">
        <div class="card">
            <div class="row">
                <div class="col-md-12">
                    <h5 class="heading">Recent Activities</h5>
                </div>
            </div>

         <div class="container">
					<div class="col-lg-12">
						<div class="row align-items-center">
						
							<div class="col-5">
								<p class="card-title mb-0" style="font-size: 18px;">
									<i class="fa-regular fa-bell fa-lg"></i> Recent Alerts: <span
										id="total"></span>
								</p>
							</div>

							<!-- Right-aligned date range picker, start time, and end time picker -->
							<div class="col-7 d-flex justify-content-end">
								<div id="reportrange"
									style="background: #fff; color: black; cursor: pointer; padding: 5px 10px; border: 1px solid #ccc;">
									<i class="fa fa-calendar"></i>&nbsp; <span id="dateRangeText"></span>
									<i class="fa fa-caret-down"></i>
								</div>

								<div id="startTimePicker" class="mx-2"
									style="background: #fff; color: black; cursor: pointer; padding: 5px 10px; border: 1px solid #ccc;">
									<i class="fa fa-clock"></i>&nbsp; <span id="startTimeText"></span>
									<i class="fa fa-caret-down"></i>
								</div>

								<div id="endTimePicker"
									style="background: #fff; color: black; cursor: pointer; padding: 5px 10px; border: 1px solid #ccc;">
									<i class="fa fa-clock"></i>&nbsp; <span id="endTimeText"></span>
									<i class="fa fa-caret-down"></i>
								</div>
							</div>
						</div>
					</div>
				</div>	

            <div class="card-body">
                <div class="row">
                    <div class="col-lg-3  col-sm-12">
                        <div id="chartdiv"></div>
                    </div>
                    
                    <div class="col-lg-9  col-sm-12">
                      <div class="table-responsive" style="max-height:350px; overflow-y:auto;">
                    <table id="auditTable" class="table table-bordered" style="color: #003d87;">
                        <thead style="color: #fff; position: sticky; top: 0; z-index: 1; background: #028bd7;">
                            <tr>
                                <th class="text-left col-3">Service Integrator</th>
                                <th class="text-left col-2">Alert Zones</th>
                                <th class="text-left col-3">Zone</th>
                                <th style="text-align: right;" class="col-3">Alert Time Stamp</th>
                                <th class="text-left">Status</th>
                            </tr>
                        </thead>
                        <tbody>
                        
                        </tbody>
                    </table>
                </div>
                </div>
                </div>
            </div>
        </div>
    </div>
</div>





<!--Alert Reports  -->
	<div class="row mx-2">
		<div class="col-lg-12 col-md-12 col-sm-12 mt-0">
			<div class="card mx-0">

				<div class="row">
					<div class="col-12">
						<h5 class="heading">Alert Reports</h5>
					</div>
				</div>
<div class="input-group rounded mt-2">
						<!-- <input class="form-control form-control-sm rounded typeahead mx-1"
							type="text" id="startDateForAlertReport" placeholder="Start Date"
							style="height: 32px;" onfocus="(this.type='date')"> <input
							class="form-control form-control-sm rounded typeahead mx-1"
							type="text" id="endDate" placeholder="End Date"
							style="height: 32px;" onfocus="(this.type='date')">
						<button class="input-group-text border-0 ml-2" id="filterButton"
							style="background-color: #028bd7; color: #ffff; height: 32px;">
							<i class="fa fa-search" aria-hidden="true" onclick="fetchAlarms(0);"></i>
						</button>
						
						<select class="form-control form-control-sm rounded mx-1" 
        id="deviceType" 
        style="height: 32px;">
    <option value="" disabled selected>Device Type</option>
    <option value="CCTV">CCTV</option>
    <option value="Security Alarm">Security Alarm</option>
    <option value="Fire Alarm">Fire Alarm</option>
    <option value="BACS">BACS</option>
    <option value="ETL">ETL</option>
</select>
 -->
 <div class="container">
	<div class="row gx-2 gy-2">
	  <div class="col-md-2 col-sm-6">
		<label for="branchFilter" class="form-label">Branch</label>
		<select id="branchFilter" class="form-control form-control-sm">
		  <option value="">All Branches</option>
		</select>
	  </div>
	  
	  
	  <div class="col-md-2 col-sm-6">
		<label for="deviceTypeFilter" class="form-label">Device Type</label>
		<select id="deviceTypeFilter" class="form-control form-control-sm">
		  <option value="">All Device Types</option>
		  <option value="Smart Communicator">Smart Communicator</option>
		  <option value="SIA">SIA</option>
		  
		</select>
	  </div>
  
	  <div class="col-md-2 col-sm-6">
		<label for="zoneFilter" class="form-label">Product</label>
		<select id="zoneFilter" class="form-control form-control-sm">
		  <option value="">Product Type</option>
		</select>
	  </div>
  
	  <div class="col-md-2 col-sm-6">
		<label for="alertTypeFilter" class="form-label">Alert Type</label>
		<select id="alertTypeFilter" class="form-control form-control-sm">
		  <option value="">All Alert Types</option>
		</select>
	  </div>
  
	  
  
	  <div class="col-md-2 col-sm-6">
		<label for="fromDate" class="form-label">From Date</label>
		<input type="date" id="fromDate" class="form-control form-control-sm" />
	  </div>
  
	  <div class="col-md-2 col-sm-6">
		<label for="toDate" class="form-label">To Date</label>
		<input type="date" id="toDate" class="form-control form-control-sm" />
	  </div>
	</div>
  
	<!-- Search button aligned to right under last filter -->
	<div class="row mt-2">
	  <div class="col-12 d-flex justify-content-end">
		<button id="searchButtonForAllAlerts" type="button" class="btn btn-sm btn-primary">Search</button>
	  </div>
	</div>
  </div>

					</div>
				<div class="card-header" id="AlertDetailsTable">
		<div id="loaderOverlay" style="display: none;">
        <div class="loader-spinner"></div>
    </div>

<div class="table-responsive mb-2 position-relative" style="min-height: 250px; max-height: 350px; overflow-y: auto;">
    <!-- Loader overlay -->
    

    <table class="table table-striped table-bordered mb-0">
        <thead style="color: #fff; position: sticky; top: 0; z-index: 1; background: #028bd7;">
            <tr>
                <th>S.No</th>
                <th class="text-right">Branch Code</th>
				<th class="text-left col-3">Branch Address</th>
                <th>Device Type</th>
                <th class="text-left col-2">Service Integrator</th>
                <th class="text-left">Product Name</th>
                <th class="text-right">Alert Type</th>
                
                <th class="text-left">Time Stamp</th>
            </tr>
        </thead>
        <tbody id="AlertTableBody"></tbody>
    </table>
</div>

				</div>

				<div class="pagination" id="paginationContainer"></div>
			</div>

		</div>

	</div>








<!---------------------------------------------------------  HMS CHATBOT START------------------------------------------------------------------>
<!-- Updated Chatbot HTML to match the image -->
<div class="chatbot-container">
    <!-- Chat Toggle Button - Styled to match the image with "ASK ILA" text and avatar -->
    <button class="chat-toggle-btn" id="chatToggleBtn">
        <div class="chat-bubble-text">Ask Digi</div>
        <div class="avatar-container">
            <img src="./img/digi_assistant.png" class="avatar-img" alt="Digi Assistant" width="50">
        </div>
        <div class="close-icon" id="chatCloseIcon" style="display: none;">
            <i class="fas fa-times"></i>
        </div>
    </button>

    <!-- Chatbot Interface -->
    <div class="chatbot-wrapper" id="chatbotWrapper">
        <div class="chatbot-header">
            <div class="chatbot-title">
                <img src="./img/digi_assistant.png" width="30">
                <span class="ml-2">Digi : HMS Assistant</span>
                
                
            </div>
     
            <button class="minimize-btn" id="minimizeChatBtn">
                <i class="fas fa-minus"></i>
            </button>
           
        </div>
        <div class="chatbot-body" id="chatbotBody">
            <div class="chat-messages" id="chatMessages">
                <!-- Messages will be populated here -->
                <div class="bot-message">
                    <div class="message-content">
                        <p>Hello! I'm Digi, your HMS assistant.</p>
                    </div>
                  <span class="message-time mb-1">Now</span>
                </div>
                
                
                 
<div class="bot-message">
    <div class="message-content">
        <p>How can I help you today? <b>Get Quick Status of:</b></p>
        
    </div>
</div>

					<div class="quick-actions"
						style="display: flex; flex-direction: column; align-items: flex-start; gap: 10px;">
						<button class="btn btn-sm btn-outline-chatbot">Fire Alarm System <b>(FAS)</b></button>
						<button class="btn btn-sm btn-outline-chatbot">Electronic Time Lock <b>(ETL)</b></button>
						<button class="btn btn-sm btn-outline-chatbot">Security Alarm System <b>(SAS)</b></button>
						<button class="btn btn-sm btn-outline-chatbot">Closed-Circuit Television <b>(CCTV)</b></button>	
						<button class="btn btn-sm btn-outline-chatbot">Bio-metric Access Control System <b>(BACS)</b></button>
						
										
					</div>

					
            </div>
        </div>
        <div class="chatbot-footer">
            <div class="input-group">
                <input type="text" class="form-control mr-1" placeholder="Type your message..." id="userMessageInput">
                <button class="btn btn-primary send-btn" id="sendMessageBtn">
                    <i class="fas fa-paper-plane"></i>
                </button>
            </div>
        </div>
    </div>
</div>
<!---------------------------------------------------------  HMS CHATBOT END------------------------------------------------------------------>



	<!--**********************************
            Content body end
        ***********************************-->


	<!--**********************************
            Footer start
        ***********************************-->
	<div class="footer">
		<div class="copyright">
			<p>
				Copyright © Designed &amp; Developed by <a href="#" target="_blank">Doritech</a>
				2024
			</p>
		</div>
	</div>
	<!--**********************************
            Footer end
        ***********************************-->

<div class="modal fade" id="siaDevicesModal" tabindex="-1" aria-labelledby="siaDevicesModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-xl modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">SIA Device Details</h5>
                <button type="button" class="btn btn-close btn-secondary" data-bs-dismiss="modal" aria-label="Close">Close</button>
            </div>

            <div class="modal-body" style="text-color:black;">
                <table class="table table-striped table-bordered " style="color:black;" id="siaDeviceTable">
                    <thead>
                        <tr>
                            <th>Branch Name</th>
                            <th>Branch Address</th>
                            <th>Branch IFSC</th>
                            <th>Mobile No.</th>
                            <th>Device Serial No</th>
                            <th>Device Type</th>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- AJAX data goes here -->
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>


	<div class="modal  bd-example-modal-xl " tabindex="-1" role="dialog"
		aria-labelledby="myLargeModalLabel" aria-hidden="true"
		id="totalDeviceModal">
		<div class="modal-dialog modal-xl card">
			<div class="modal-content" style="border-radius: 20px;">
				<div class="modal-header"
					style="background: #C70039; border-radius: 20px;">
					<h4>
						<i class="fa-solid fa-bell fa-shake fa-2xl" style="color: #ffff;"></i> 
					</h4>

					<div style="float: right;">
						<button type="button" class="btn minimize-btn"
							data-dismiss="modal" onclick="minimizeModal(this)">
							<i class="fa-solid fa-window-minimize fa-lg"
								style="color: #ffff;"></i>
						</button>
						<button type="button" class=" btn minimize-btn"
							data-bs-dismiss="modal" aria-label="Close" onclick="stopSoundAlert();">
							<span aria-hidden="true"><i
								class="fa-solid fa-xmark fa-xl" style="color: #ffff;"></i></span>
						</button>
					</div>
				</div>
				<div class="modal-body">
					<div class="modal-body">
						<div class='dashboard-content'>
							<div class='container'>
								<div id="icon-container">
									<i class="fa-solid fa-video" onclick="openVideoStream();"></i>
									View Live Stream
								</div>
								<div class='card'>
									<div class='card-body' style="border-radius: 20px;">

										<section class="get-in-touch">
											<form class="contact-form row" name="roleMaster"
												style="max-height: 210px; overflow-y: auto;">


												<table id="example"
													class="table table-striped table-bordered"
													style="color: #1d1d1d;">
													<thead
														style="border-radius: 20px; background: #1c0f54; color: #ffff;">
														<tr>
														    <th>S.No</th>
                                                            <th>Desired Name</th>
														  	<th>Service Integrator</th>
															<th>Service Integrator Number</th>
															<th>Branch Address</th>
															<th>Contact No.</th>
															<th>Branch Name</th>
															<th>Alert Zone</th>
															<th>Resolution Status</th>
															<th>Remark</th>
															<th>Action</th> 
															
														</tr>
													</thead>
													<tbody>
													</tbody>
												</table>

												<audio id="audio-alert" src="./audio/emergency_alert.mp3" preload="auto"></audio>

											</form>
										</section>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	
	<!-- FILTERED TABLE OPENED IN THE TABLE  -->
	
	
	
	
	<!-- Modal -->
<div class="modal fade" id="filteredTableModal" tabindex="-1" aria-labelledby="filteredTableModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="filteredTableModalLabel">Filtered Alerts</h5>
                <button type="button" class="close" data-bs-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div class="table-responsive">
                    <table id="filteredAuditTable" class="table table-bordered" style="color: #003d87;">
                        <thead style="color: #fff; position: sticky; top: 0; z-index: 1; background: #028bd7;">
                            <tr>
                                <th class="text-left col-3">Service Integrator</th>
                                <th class="text-left col-2">Branch Name</th>
                                <th class="text-left col-3"> Alert Zone</th>
                                <th style="text-align: right;" class="col-3">Alert Time Stamp</th>
                                <th class="text-left">Status</th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
	
	
		<!-- Bootstrap Modal for Showing Data on Map -->


<div class="modal fade" id="branchModal" tabindex="-1" aria-labelledby="modalTitle" aria-hidden="true">
    <div class="modal-dialog modal-xl">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalTitle">
                    <img src="<c:url value='./logos/image/${logo.id}'/>" alt="Logo" width="80" style="display:inline-block; margin-right:10px;">
                    
                </h5>
                <button type="button" class="close" data-bs-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body" style="max-height: 80vh; overflow-y: auto; cursor:pointer;">
                <div id="modalContent">Loading...</div>
            </div>
        </div>
    </div>
</div>




	<script type="text/javascript" src="https://cdn.jsdelivr.net/momentjs/latest/moment.min.js"></script>
<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.min.js"></script>


<script>

document.addEventListener("DOMContentLoaded", function () {
    var startDate = moment().subtract(30, 'days').startOf('day');
    var endDate = moment().endOf('day');
    var startTime = null;
    var endTime = null;
    var chart = null;
    var root = null;

    function updateDateRangeText(start, end) {
        document.getElementById("dateRangeText").innerHTML =
            start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY');
    }

    function updateTimeText(start, end) {
        document.getElementById("startTimeText").innerHTML = start ? start.format('HH:mm') : "--:--";
        document.getElementById("endTimeText").innerHTML = end ? end.format('HH:mm') : "--:--";
    }

    new daterangepicker(document.getElementById("reportrange"), {
        startDate: startDate,
        endDate: endDate,
        showDropdowns: true,
        ranges: {
            'Today': [moment().startOf('day'), moment().endOf('day')],
            'Yesterday': [moment().subtract(1, 'days').startOf('day'), moment().subtract(1, 'days').endOf('day')],
            'Last 7 Days': [moment().subtract(6, 'days').startOf('day'), moment().endOf('day')],
            'Last 30 Days': [moment().subtract(29, 'days').startOf('day'), moment().endOf('day')],
            'This Month': [moment().startOf('month'), moment().endOf('month')],
            'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
        }
    }, function (start, end) {
        if (start === null || end === null) {
            fetchStatusData(null, null);
            updateDateRangeText("All Data", "");
        } else {
            startDate = start;
            endDate = end;
            updateDateRangeText(start, end);
            fetchStatusData(start, end, startTime, endTime);
        }
    });

    new daterangepicker(document.getElementById("startTimePicker"), {
        singleDatePicker: true,
        timePicker: true,
        timePicker24Hour: true,
        timePickerIncrement: 1,
        locale: { format: 'HH:mm' },
        autoApply: true,
        isInvalidDate: function () { return true; }
    }, function (start) {
        startTime = start;
        updateTimeText(startTime, endTime);
        fetchStatusData(startDate, endDate, startTime, endTime);
    });

    new daterangepicker(document.getElementById("endTimePicker"), {
        singleDatePicker: true,
        timePicker: true,
        timePicker24Hour: true,
        timePickerIncrement: 1,
        locale: { format: 'HH:mm' },
        autoApply: true,
        isInvalidDate: function () { return true; }
    }, function (end) {
        endTime = end;
        updateTimeText(startTime, endTime);
        fetchStatusData(startDate, endDate, startTime, endTime);
    });

    function fetchStatusData(start = null, end = null, startTime = null, endTime = null, category = null) {
        let requestData = {};

        if (start && end) {
            requestData.startDate = start.format('YYYY-MM-DD');
            requestData.endDate = end.format('YYYY-MM-DD');
        }

        if (startTime && endTime) {
            requestData.startTime = startTime.format('HH:mm');
            requestData.endTime = endTime.format('HH:mm');
        }

        if (category) {
            requestData.category = category;
        }

        $.ajax({
            url: "./getStatusCountByDateRange",
            method: 'GET',
            data: requestData,
            success: function (data) {
                if (data.statusCode === 200) {
                    updateChart(data.payload, startTime, endTime);
                    updateTotalAlerts(data.payload);
                } else {
                    console.error("Failed to fetch data", data.message);
                    alert("Error fetching data");
                }
            },
            error: function (xhr, status, error) {
                console.error("Error fetching data:", error);
            }
        });

        $.ajax({
            url: "./getAllRecordsByDateRange",
            method: 'GET',
            data: requestData,
            success: function (data) {
                if (data.statusCode === 200) {
              
                    updateTable(data.payload, startTime, endTime);
                } else {
             
                    updateTable(data.payload, startTime, endTime);
                    Swal.fire({
                        icon: 'info',
                        title: 'No Alerts',
                        text: 'No Alerts Found'
                    });
                }
            },
            error: function (xhr, status, error) {
                console.error("Error fetching data:", error);
            }
        });
    }

    function updateChart(statusData, startTime, endTime) {
        if (root) {
            root.dispose();
            root = null;
        }

        root = am5.Root.new("chartdiv");

        root.setThemes([am5themes_Animated.new(root)]);

        chart = root.container.children.push(am5percent.PieChart.new(root, {
            layout: root.verticalLayout,
            innerRadius: am5.percent(50)
        }));

        var series = chart.series.push(am5percent.PieSeries.new(root, {
            valueField: "value",
            categoryField: "category",
            alignLabels: false
        }));

        series.labels.template.setAll({
            visible: false
        });

        const filteredData = statusData.filter(item => {
            let itemTime = moment(item.dateTime, "YYYY-MM-DD HH:mm");
            return (!startTime || itemTime.isSameOrAfter(startTime, 'minute')) &&
                   (!endTime || itemTime.isSameOrBefore(endTime, 'minute'));
        });

        const chartData = filteredData.map(item => ({
            value: item.count,
            category: item.remarks
        }));

        series.data.setAll(chartData);

        var legend = chart.children.push(am5.Legend.new(root, {
            centerX: am5.percent(50),
            x: am5.percent(50),
            marginTop: 15,
            marginBottom: 15,
        }));

        legend.data.setAll(series.dataItems);

        series.appear(1000, 100);

        series.dataItems.forEach(function (dataItem) {
            dataItem.get("slice").events.on("click", function () {
                var category = dataItem.get("category");

                var filteredCategory = category;
                if (category === "FALSE ALARM") filteredCategory = "False";
                else if (category === "SECURITY VENDOR INFORMED") filteredCategory = "Vendor Informed";

                filterTableByCategory(filteredCategory);
            });
        });

        document.getElementById("chartdiv").style.display = 'block';
    }

    function filterTableByCategory(category) {
        $("#auditTable tbody tr").each(function () {
            var rowCategory = $(this).find("td:nth-child(5)").text().trim();

            if (rowCategory.toLowerCase() === category.toLowerCase() || category === "All") {
                $(this).show();
            } else {
                $(this).hide();
            }
        });
    }

    function updateTotalAlerts(statusData) {
        let totalAlerts = statusData.reduce((total, item) => total + item.count, 0);
        document.getElementById("total").innerText = totalAlerts;
    }

    function formatDateTime(dateTime) {
        return moment(dateTime).format('MMMM D, YYYY HH:mm');
    }

    function updateTable(statusData, startTime, endTime) {
    	
    	//alert("entering updateTable");
    	
    	//alert(JSON.stringify(statusData));
        var tableBody = $('#auditTable tbody');
        tableBody.empty();

        if (!statusData || statusData.length === 0) {
            var noDataRow = $('<tr class="alarmedTable"></tr>');
            noDataRow.append('<td colspan="9" style="text-align:center;">No record found</td>');
            tableBody.append(noDataRow);
            return;
        }

        statusData.sort((a, b) => new Date(b.dateTime) - new Date(a.dateTime));

        function toSentenceCase(str) {
            if (!str) return '';
            return str.toLowerCase().replace(/\b\w/g, char => char.toUpperCase());
        }

        statusData.forEach(audit => {
            let auditTime = moment(audit.dateTime, "YYYY-MM-DD HH:mm");

            if ((startTime && auditTime.isBefore(startTime, 'minute')) ||
                (endTime && auditTime.isAfter(endTime, 'minute'))) {
                return;
            }

            var row = $('<tr class="alarmedTable"></tr>');
            row.append('<td>' + toSentenceCase(audit.vendorName) + '</td>');
            row.append('<td>' + toSentenceCase(audit.deviceId) + '</td>');
            row.append('<td>' + toSentenceCase(audit.activeZone) + '</td>');
            row.append('<td style="text-align:right;">' + formatDateTime(audit.dateTime) + '</td>');

            var remarks = audit.remarks;
            switch (remarks) {
                case 'TESTING':
                    remarks = '<span class="testing">' + toSentenceCase('Testing') + '</span>';
                    break;
                case 'FALSE ALARM':
                    remarks = '<span class="falseAlarm">' + toSentenceCase('False') + '</span>';
                    break;
                case 'ACKNOWLEDGED':
                    remarks = '<span class="acknowledged">' + toSentenceCase('Acknowledged') + '</span>';
                    break;
                case 'SECURITY VENDOR INFORMED':
                    remarks = '<span class="informedVendor">' + toSentenceCase('Vendor informed') + '</span>';
                    break;
                default:
                    remarks = toSentenceCase(remarks);
                    break;
            }

            row.append('<td>' + remarks + '</td>');
            tableBody.append(row);
        });
    }

    fetchStatusData(startDate, endDate, startTime, endTime);
});

document.addEventListener("DOMContentLoaded", function () {
    var startDate = moment().subtract(30, 'days').startOf('day');
    var endDate = moment().endOf('day');
    var startTime = null;
    var endTime = null;

    function updateTimeText(start, end) {
    	//alert(start+"------->>>>>>>"+end);
        document.getElementById("startTimeText").innerHTML = start ? start.format('HH:mm') : "--:--";
        document.getElementById("endTimeText").innerHTML = end ? end.format('HH:mm') : "--:--";
    }

    new daterangepicker(document.getElementById("startTimePicker"), {
        singleDatePicker: true,
        timePicker: true,
        timePicker24Hour: true,
        timePickerIncrement: 1,
        locale: { format: 'HH:mm' },
        autoApply: true,
    }, function (start) {
        startTime = start;
        updateTimeText(startTime, endTime);
        fetchRecordsByTimeRange(startTime, endTime);
    });

    new daterangepicker(document.getElementById("endTimePicker"), {
        singleDatePicker: true,
        timePicker: true,
        timePicker24Hour: true,
        timePickerIncrement: 1,
        locale: { format: 'HH:mm' },
        autoApply: true,
    }, function (end) {
        endTime = end;
        updateTimeText(startTime, endTime);
        fetchRecordsByTimeRange(startTime, endTime);
        fetchStatusData(startDate, endDate, startTime, endTime);
    });

    function fetchStatusData(start, end, startTime, endTime) {
        let requestData = {
            startDate: start.format('YYYY-MM-DD'),
            endDate: end.format('YYYY-MM-DD'),
            startTime: startTime ? startTime.format('HH:mm') : null,
            endTime: endTime ? endTime.format('HH:mm') : null
        };

        $.ajax({
            url: "./getStatusCountByDateRange",
            method: 'GET',
            data: requestData,
            success: function (data) {
                if (data.statusCode === 200) {
                    updateChart(data.payload);
                    updateTotalAlerts(data.payload);
                }
            },
            error: function (xhr, status, error) {
                console.error("Error fetching data:", error);
            }
        });

        $.ajax({
            url: "./getAllRecordsByDateRange",
            method: 'GET',
            data: requestData,
            success: function (data) {
                if (data.statusCode === 200) {
                    updateTable(data.payload);
                }
            },
            error: function (xhr, status, error) {
                console.error("Error fetching data:", error);
            }
        });
    }
    
    
    
 function updateTableForTimeFilter(statusData, startTime, endTime) {
    	
    
        var tableBody = $('#auditTable tbody');
        tableBody.empty();

        if (!statusData || statusData.length === 0) {
            var noDataRow = $('<tr class="alarmedTable"></tr>');
            noDataRow.append('<td colspan="9" style="text-align:center;">No record found</td>');
            tableBody.append(noDataRow);
            return;
        }

        statusData.sort((a, b) => new Date(b.dateTime) - new Date(a.dateTime));

        function toSentenceCase(str) {
            if (!str) return '';
            return str.toLowerCase().replace(/\b\w/g, char => char.toUpperCase());
        }

        statusData.forEach(audit => {
            let auditTime = moment(audit.dateTime, "YYYY-MM-DD HH:mm");

            if ((startTime && auditTime.isBefore(startTime, 'minute')) ||
                (endTime && auditTime.isAfter(endTime, 'minute'))) {
                return;
            }

            var row = $('<tr class="alarmedTable"></tr>');
            row.append('<td>' + toSentenceCase(audit.vendorName) + '</td>');
            row.append('<td>' + toSentenceCase(audit.deviceId) + '</td>');
            row.append('<td>' + toSentenceCase(audit.activeZone) + '</td>');
            row.append('<td style="text-align:right;">' + formatDateTime(audit.dateTime) + '</td>');
            var remarks = audit.remarks;
            switch (remarks) {
                case 'TESTING':
                    remarks = '<span class="testing">' + toSentenceCase('Testing') + '</span>';
                    break;
                case 'FALSE ALARM':
                    remarks = '<span class="falseAlarm">' + toSentenceCase('False') + '</span>';
                    break;
                case 'ACKNOWLEDGED':
                    remarks = '<span class="acknowledged">' + toSentenceCase('Acknowledged') + '</span>';
                    break;
                case 'SECURITY VENDOR INFORMED':
                    remarks = '<span class="informedVendor">' + toSentenceCase('Vendor informed') + '</span>';
                    break;
                default:
                    remarks = toSentenceCase(remarks);
                    break;
            }

            row.append('<td>' + remarks + '</td>');
            tableBody.append(row);
        });
    }

 
 
 function updateTableForModal(statusData, startTime, endTime) {
	    var tableBody = $('#filteredAuditTable tbody');
	    tableBody.empty();

	    if (!statusData || statusData.length === 0) {
	        Swal.fire({
	            icon: 'info',
	            title: 'No Records Found',
	            text: 'No data available for the selected time range.',
	            confirmButtonText: 'OK'
	        });
	        return;
	    }

	    statusData.sort((a, b) => new Date(b.dateTime) - new Date(a.dateTime));

	    function toSentenceCase(str) {
	        if (!str) return '';
	        return str.toLowerCase().replace(/\b\w/g, char => char.toUpperCase());
	    }

	    statusData.forEach(audit => {
	        let auditTime = moment(audit.dateTime, "YYYY-MM-DD HH:mm");

	        if ((startTime && auditTime.isBefore(startTime, 'minute')) ||
	            (endTime && auditTime.isAfter(endTime, 'minute'))) {
	            return;
	        }

	        var row = $('<tr class="alarmedTable"></tr>');
            row.append('<td>' + toSentenceCase(audit.vendorName) + '</td>');
            row.append('<td>' + toSentenceCase(audit.deviceId) + '</td>');
            row.append('<td>' + toSentenceCase(audit.activeZone) + '</td>');
            row.append('<td style="text-align:right;">' + formatDateTime(audit.dateTime) + '</td>');

	        var remarks = audit.remarks;
	        switch (remarks) {
	            case 'TESTING':
	                remarks = '<span class="testing">' + toSentenceCase('Testing') + '</span>';
	                break;
	            case 'FALSE ALARM':
	                remarks = '<span class="falseAlarm">' + toSentenceCase('False') + '</span>';
	                break;
	            case 'ACKNOWLEDGED':
	                remarks = '<span class="acknowledged">' + toSentenceCase('Acknowledged') + '</span>';
	                break;
	            case 'SECURITY VENDOR INFORMED':
	                remarks = '<span class="informedVendor">' + toSentenceCase('Vendor informed') + '</span>';
	                break;
	            default:
	                remarks = toSentenceCase(remarks);
	                break;
	        }

	        row.append('<td>' + remarks + '</td>');
	        tableBody.append(row);
	    });

	   
	    $('#filteredTableModal').modal('show');
	}

    
    
 function fetchRecordsByTimeRange(startTime, endTime) {
	    if (!endTime) return; 

	    let requestData = {
	        startDate: startDate.format('YYYY-MM-DD'),
	        endDate: endDate.format('YYYY-MM-DD'),
	        startTime: startTime ? startTime.format('HH:mm') : null,
	        endTime: endTime ? endTime.format('HH:mm') : null
	    };

	    $.ajax({
	        url: "./getAllRecordsOfTodayByTimeRange",
	        method: 'GET',
	        data: requestData,
	        success: function (data) {
	        	
	            if (data.statusCode === 200) {
	                updateTableForModal(data.payload, startTime, endTime);
	                $('#filteredTableModal').modal('show'); 
	            } else if(data.statusCode === 404) {
	                Swal.fire({
	                    icon: 'info',
	                    title: 'No Alerts',
	                    text: 'No Alerts Found'
	                });
	            }
	        },
	        error: function (xhr, status, error) {
	            console.error("Error fetching data:", error);
	        }
	    });
	}




});


	 
	 
		document.addEventListener('DOMContentLoaded',
		function() {
							// Function to truncate text and update dropdown button
		function updateDropdownText(dropdownMenu) {
								dropdownMenu.querySelectorAll('a').forEach(
								 function(item) {
									 item.addEventListener('click',function() {
										let selectedText = this.textContent;
										let displayText = selectedText.length > 8 ? selectedText.substring(0,8)+ '...': selectedText;
										this.closest('.dropdown').querySelector('.dropdown-toggle').textContent = displayText;
											
									 });
											});
							}

							document.querySelectorAll('.dropdown-menu')
									.forEach(function(dropdownMenu) {
										updateDropdownText(dropdownMenu);
									});
						});
		</script>
		<script>
		var markers = [];

		$(document).ready(function() {
			fetchBranchData();
		});

		function fetchBranchData() {
			var branchId = $('#loggedInBranch').val();

			$.ajax({
				url : './getAllChildren?branchCode=' + branchId,
				type : 'GET',
				success : function(response) {
					if (response.message === "Branches fetched successfully") {
						var branches = response.payload;
						var coordinates = [];

						branches.forEach(function(branch) {

							coordinates.push({
								lat : parseFloat(branch.latitude),
								lng : parseFloat(branch.longitude),
								branch: branch.branchCode,
		                        bank: branch.bank,
		                        branchDesc: branch.branchDesc,
		                        address: branch.address1 + ', ' + branch.address2 + ', ' + branch.address3 + ', ' + branch.address4,
		                        phone: branch.mobile || branch.telephone
						});

						});
						initMap(coordinates);

					} else {
						console.error('Error: ' + response.message);
					}
				},
				error : function(xhr, status, error) {
					console.error('Error: ' + error);
				}
			});
		}

		function initMap(coordinates) {
		    var map = new google.maps.Map(document.getElementById('map'), {
		        zoom: 8, 
		        center: coordinates[0]
		    });

		    var bounds = new google.maps.LatLngBounds(); 

		    var mainMarker = new google.maps.Marker({
		        position: coordinates[0],
		        map: map,
		        title: 'Head Branch'
		    });

		    bounds.extend(mainMarker.getPosition()); 

		    var logoUrl = "<c:url value='./logos/image/${logo.id}'/>"; 

		    var infoWindow = new google.maps.InfoWindow({
		        content: '<div style="color:black; text-align:center;width:90px; height:45px; padding:0; display:flex; align-items:center; justify-content:center;">' +
		            '<img src="' + logoUrl + '" alt="Logo" width="90px" style="display:block;"><br>' +
		            '</div>'
		    });

		    google.maps.event.addListener(infoWindow, 'domready', function () {
		        document.querySelector('.gm-style-iw').parentNode.querySelector('button').style.display = 'none';
		        document.querySelector('.gm-style-iw-ch').style.paddingTop = '0px';
		    });

		    mainMarker.addListener('click', function () {
		        infoWindow.open(map, mainMarker);
		    });

		    markers.push(mainMarker);
		    
		    addClickListener(mainMarker, coordinates[0].branch);

		    function getInfoContent(branchCode) {
		        var activeZoneValue = activeZone.substring(activeZone.indexOf('_') + 1, activeZone.indexOf(':'));
		        return '<div><strong>Digitals Alert:</strong> ' + branchCode + '<br>Having the issue in "' + activeZoneValue + '"<br>Thank you for visiting!</div>';
		    }

		    function addMouseOverListener(marker, branchCode) {
		        marker.addListener('mouseover', function () {
		            if (braodcastBranchCode.includes(marker.branch)) {
		                var infoWindow = new google.maps.InfoWindow({
		                    content: getInfoContent(branchCode)
		                });
		                infoWindow.open(map, marker);
		            }
		        });
		    }

		    infoWindow.open(map, mainMarker);
		    addMouseOverListener(mainMarker, coordinates[0].branch);

		    for (var i = 1; i < coordinates.length; i++) {
	            var childCoordinate = coordinates[i];
	            
	            var details = "Bank      : " + childCoordinate.bank + "\n" +
                              "Address : " + childCoordinate.address + "\n" +
                              "Phone    : " + childCoordinate.phone;
	           // alert(JSON.stringify(childCoordinate.address))
	            var childMarker = new google.maps.Marker({
	                position: childCoordinate,
	                map: map,
	                title:  details,
	                branch: childCoordinate.branch,
	                
	            });

	            markers.push(childMarker);
	            addClickListener(childMarker, childCoordinate);
	            bounds.extend(childMarker.getPosition());

	            var line = new google.maps.Polyline({
	                path: [coordinates[0], childCoordinate],
	                geodesic: true,
	                strokeColor: '#FF0000',
	                strokeOpacity: 0,
	                icons: [{
	                    icon: {
	                        path: google.maps.SymbolPath.CIRCLE,
	                        fillOpacity: 1,
	                        scale: 3
	                    },
	                    offset: '0%',
	                    repeat: '20px'
	                }],
	                map: map
	            });
	        }

	        map.fitBounds(bounds, -50);
			
			var listener = google.maps.event.addListenerOnce(map, "bounds_changed", function () {
    if (map.getZoom() < 8) {
        map.setZoom(8);  
    }
});
			
			

	        setInterval(function () {
	            toggleMarkers();
	        }, 100);
	    }

		 


function getHealthCommunicatorContentForMap(branchCode, communicatorRow) {
    // Remove existing detail rows
    $('.detail-row').remove();
    

    $.ajax({
        url: './getLatestRecordWithAlertType',
        type: 'GET',
        data: { branchCode: branchCode }, // or use panelSerialNo if your API requires it
        success: function(latestRecordResponse) {
            if (!latestRecordResponse || !latestRecordResponse.payload) {
                console.error("No latest record found.");
                return;
            }

            const latestRecord = latestRecordResponse.payload.latestRecord;
            

            // Now get the branch details
            $.ajax({
                url: './getBranchByBranchCode',
                type: 'GET',
                contentType: 'application/json',
                data: { branchCode: branchCode },
                success: function(response) {
                    if (!response || !response.payload || !response.payload[0]) {
                        console.error("Invalid branch response.");
                        return;
                    }

                    const fetchedBranchCodeName = response.payload[0].branchDesc;
                    const dateObj = new Date(latestRecord.dateTime);
                    const datePart = dateObj.toISOString().split('T')[0];
                    const timePart = dateObj.toISOString().split('T')[1].split('.')[0];
                    const syncTimeFormatted = datePart + " " + timePart;

                    const mainIcon = latestRecord.main == 0
                        ? `<br/><i class="fa-solid fa-plug-circle-xmark fa-2xl" style="color: #902c3b;"></i><br/><b style="font-size:12px;">Power Off</b>`
                        : `<br/><i class="fa-solid fa-plug-circle-bolt fa-2xl" style="color: #1d6e1c;"></i><br/><b style="font-size:12px;">Power On</b>`;

                    const solarIcon = latestRecord.solar == 0
                        ? `<br/><i class="fa-solid fa-sun fa-2xl" style="color: #808080;"></i><br/><b style="font-size:12px;">Not Connected</b>`
                        : `<br/><i class="fa-solid fa-sun fa-2xl" style="color: #FFAB52;"></i><br/><b style="font-size:12px;">Connected</b>`;

                    let batteryIcon;
                    if (latestRecord.battery == 0) {
                        batteryIcon = `<br/><i class="fa-solid fa-battery-empty fa-2xl" style="color: #c25419;"></i><br/><b style="font-size:12px;">Not Installed</b>`;
                    } else if (latestRecord.battery == 1) {
                        batteryIcon = `<br/><i class="fa-solid fa-battery-three-quarters fa-2xl" style="color: #d9a13f;"></i><br/><b style="font-size:12px;">Charging</b>`;
                    } else if (latestRecord.battery == 2) {
                        batteryIcon = `<br/><i class="fa-solid fa-battery-quarter fa-2xl" style="color: #FFD43B;"></i><br/><b style="font-size:12px;">Battery Low</b>`;
                    } else {
                        batteryIcon = `<br/><i class="fa-solid fa-battery-full fa-2xl" style="color: #4a8519;"></i><br/><b style="font-size:12px;">Battery Full</b>`;
                    }

                    let signalIcon;
                    if (latestRecord.signalFlag == 0) {
                        signalIcon = `<br/><img src="./images/nonSignal.png" style="width:40px;"><br/><b style="font-size:12px;">No Signal</b>`;
                    } else if (latestRecord.signalFlag == 1) {
                        signalIcon = `<img src="./images/signalUnknown.png" style="width:70px;"><br/><b style="font-size:12px;">Unknown Signal</b>`;
                    } else if (latestRecord.signalFlag == 2) {
                        signalIcon = `<img src="./images/veryPoorSignal.png" style="width:40px;"><br/><b style="font-size:12px;">Very Poor Signal</b>`;
                    } else if (latestRecord.signalFlag == 3) {
                        signalIcon = `<img src="./images/poorSignal.png" style="width:40px;"><br/><b style="font-size:12px;">Poor Signal</b>`;
                    } else if (latestRecord.signalFlag == 4) {
                        signalIcon = `<img src="./images/fairSignal.png" style="width:40px;"><br/><b style="font-size:12px;">Fair Signal</b>`;
                    } else {
                        signalIcon = `<img src="./images/goodSignal.png" style="width:40px;"><br/><b style="font-size:12px;">Good Signal</b>`;
                    }

                    let statusIcon;
                    if (latestRecord.armed == 0) {
                        statusIcon = `<br/><i class="fa-solid fa-shield-virus fa-2xl" style="color:#418529;"></i><br/><b style="font-size:12px;">Active</b>`;
                    } else {
                        statusIcon = `<br/><i class="fa-solid fa-shield-virus fa-2xl" style="color: #ba2626;"></i><br/><b style="font-size:12px;">Disarmed</b>`;
                    }

                    var htmlContent = '<tr class="detail-row">' +
                        '<td colspan="5">' +
                        '<div class="table-responsive table-bordered dataTableNew">' +
                        '<table id="dataTableNew" class="table mb-0">' +
                        '<thead style="color: #fff; background: #028bd7;">' +
                        '<tr>' +
                       
                        '<th class="text-right">Sync Time</th>' +
                        '<th>Main</th>' +
                        '<th>Solar</th>' +
                        '<th>Battery</th>' +
                        '<th>Signal</th>' +
                        '<th>Status</th>' +
                        '</tr>' +
                        '</thead>' +
                        '<tbody>' +
                        '<tr>' +
                      
                        '<td class="text-dark text-right">' + syncTimeFormatted + '</td>' +
                        '<td class="text-dark text-center">' + mainIcon + '</td>' +
                        '<td class="text-dark text-center">' + solarIcon + '</td>' +
                        '<td class="text-dark text-center">' + batteryIcon + '</td>' +
                        '<td class="text-dark text-center">' + signalIcon + '</td>' +
                        '<td class="text-dark text-center">' + statusIcon + '</td>' +
                        '</tr>' +
                        '</tbody>' +
                        '</table>' +
                        '</div>' +
                        '</td>' +
                        '</tr>';

                    $(communicatorRow).after(htmlContent);
                },
                error: function(xhr) {
                    console.error("Branch AJAX error:", xhr.responseText);
                }
            });
        },
        error: function(xhr) {
            console.error("Latest record AJAX error:", xhr.responseText);
        }
    });
}


		
		function fetchMainBranchData(branchCode) {
		    $('#modalContent').html("Loading…");

		    $.ajax({
		        url: './countingZonesForReportTable?branchCode=' + branchCode,
		        type: 'GET',
		        success: function(response) {
		            if (response.message === "Data fetched successfully") {
		                var branches = response.payload;
		                var tableContent = '<div style="overflow-x: auto; max-width: 100%;">' +
		                    '<table class="table table-bordered black-text" id="branchTable">' +
		                    '<thead class="thead-primary text-white">' +
		                    '<tr>' +
		                    '<th class="d-none">Branch Code</th>' +
		                    '<th class="d-none">Bank</th>' +
		                    '<th>Branch Name</th>' +
		                    '<th style="text-align:right">Phone</th>' +
		                    '<th style="text-align:right">Device Serial No</th>' +
		                    '</tr>' +
		                    '</thead>' +
		                    '<tbody>';

		                branches.forEach(function(branch) {
		                    tableContent += "<tr class='branch-row' data-branch-code='" + branch.branchCode + "'>" +
		                        "<td class='d-none'>" + branch.branchCode + "</td>" +
		                        "<td class='d-none'>" + branch.bank + "</td>" +
		                        "<td>" + branch.branchName + "</td>" +
		                        "<td style='text-align:right'>" + branch.mobile + "</td>" +
		                        "<td style='text-align:right'>" + branch.deviceSerialNo + "</td>" +
		                        "</tr>";
		                });

		                tableContent += '</tbody></table></div>';
		                $('#modalContent').html(tableContent);
		            } else {
		                $('#modalContent').html('<p class="text-danger black-text">Error: ' + response.message + '</p>');
		            }
		        },
		        error: function(xhr, status, error) {
		            $('#modalContent').html('<p class="text-danger black-text">Failed to fetch data.</p>');
		            console.error('Error: ' + error);
		        }
		    });

		    $('#branchModal').modal('show');
		}

		// Click event to fetch zone details when clicking a branch row
		$(document).on('click', '.branch-row', function() {
		    var branchCode = $(this).data('branch-code');
		    var currentRow = $(this);

		    $.ajax({
		        url: './countingZonesForReportTable?branchCode=' + branchCode,
		        type: 'GET',
		        success: function(response) {
		            if (response.message === "Data fetched successfully") {
		                var branchData = response.payload.find(b => b.branchCode === branchCode);
		                
		              
		                if (!branchData || !branchData.zoneDTOList || branchData.zoneDTOList.length === 0) {
		                    return;
		                }

		                var zoneDetails = formatZoneDetails(branchData.zoneDTOList);
		                var detailsRow = "<tr class='details-row'><td colspan='5'>" + zoneDetails + "</td></tr>";

		                // Remove existing details row if already present
		                $('.details-row').remove();

		                // Insert details row after the clicked branch row
		                currentRow.after(detailsRow);

		                // Call getHealthCommunicatorContent to show the table in the modal
		                getHealthCommunicatorContentForMap(branchCode, currentRow);
		                
		            } else {
		                alert("Error: " + response.message);
		            }
		        },
		        error: function(xhr, status, error) {
		            alert("Failed to fetch counts.");
		            console.error('Error:', error);
		        }
		    });
		});

		// Function to format zone details into a list
		function formatZoneDetails(zones) {
		    var content = '<div style="overflow-x: auto; max-width: 100%; background-color:#f8f8f8;">' +
		        '<table class="table table-bordered black-text">' +
		        '<thead style="background-color:#1d1d1d; color:#f8f8f8;">' +
		        '<tr>' +
		        '<th>Zone</th>' +
		        '<th>Zone Type</th>' +
		        '<th>Alert Type</th>' +
		        '<th>Status</th>' +
		        '</tr>' +
		        '</thead>' +
		        '<tbody>';

		    zones.forEach(function(zone) {
		        var statusColor = getStatusColor(zone.status);
		        content += '<tr>' +
		            '<td>' + zone.zone + '</td>' +
		            '<td>' + zone.zoneType + '</td>' +
		            '<td>' + zone.alertType + '</td>' +
		            '<td><span style="background-color: ' + statusColor + '; color: white; padding: 2px 5px; border-radius: 3px;">' + zone.status + '</span></td>' +
		            '</tr>';
		    });

		    content += '</tbody></table></div>';
		    return content;
		}

		// Function to get status color
		function getStatusColor(status) {
		    if (status.includes("Partially Working")) return "#FCC737";
		    if (status.includes("Not Working")) return "crimson";
		    if (status.includes("Working")) return "#118B50";
		    return "#000"; // Default Black
		}


// Click event for markers
function addClickListener(marker, branchData) {
    marker.addListener('click', function () {
        if (marker === markers[0]) { // Main branch marker
            fetchMainBranchData(branchData);
        } else {
            openFullscreen(); // Open map in full screen for other markers
            setTimeout(function () {
                fetchBranchDetails(branchData);
                $('#branchModal').appendTo('#map').modal('show');
            }, 500);
        }
    });
}


		// Function to open the map in fullscreen mode
		function openFullscreen() {
		    var mapElement = document.getElementById('map');
		    if (mapElement.requestFullscreen) {
		        mapElement.requestFullscreen();
		    } else if (mapElement.mozRequestFullScreen) { // Firefox
		        mapElement.mozRequestFullScreen();
		    } else if (mapElement.webkitRequestFullscreen) { // Chrome, Safari and Opera
		        mapElement.webkitRequestFullscreen();
		    } else if (mapElement.msRequestFullscreen) { // IE/Edge
		        mapElement.msRequestFullscreen();
		    }
		}

		

		function fetchBranchDetails(branchData) {
		    var branchDetails = `
		        <div style="background-color: #D4EBF8; color: #1F509A; border: 1px solid black; padding: 10px; display: block; width: 100%; border-radius: 10px;">
		            <div>
		                <strong>Branch:</strong> ` + branchData.branchDesc + ` &nbsp;&nbsp;
		            </div>
		            <div>
		                <strong>Address:</strong> ` + branchData.address + ` &nbsp;&nbsp;
		                <strong>Phone:</strong> ` + branchData.phone + `
		            </div>
		        </div>
		    `;

		    $('#modalContent').html("Loading...");

		    $.ajax({
		        url: './getCountsByZoneStatus?branchCode=' + branchData.branch,
		        type: 'GET',
		        success: function(response) {
		            if (response.message === "Counts fetched successfully") {
		                var data = response.payload;
		                var content = branchDetails + '<div style="color: black; font-size: 14px;">';
		                var hasValidData = false;

		                for (var system in data) {
		                    var working = data[system]["Working"];
		                    var notWorking = data[system]["Not Working"];
		                    var partiallyWorking = data[system]["Partially Working"];
		                    var total = working + notWorking + partiallyWorking;

		                    if (total === 0) {
		                        continue;
		                    }

		                    hasValidData = true;
		                    content += '<div class="mb-2" style="padding: 8px; border-bottom: 1px solid #ccc; font-size:13px;">' +
		                        '<div style="display: flex; justify-content: space-between; align-items: center;">' +
		                            '<strong style="font-size: 15px; color: black;">' + system + '</strong>' +
		                            '<span class="my-2 p-2" style="background-color:#F1EAFF; color:#7743DB; font-size: 15px; border-radius:15px;">Total: ' + total + '</span>' +
		                        '</div>' +
		                        '<div style="display: flex; gap: 10px; margin-top: 5px;">' +
		                            '<span style="background-color: #E0FBE2; color: #169976; padding: 8px; border-radius: 15px;">' +
		                                '<i class="fas fa-check-circle"></i> Working: <strong class="ml-3">' + working + '</strong>' +
		                            '</span>' +
		                            '<span style="background-color: #FFFECE; color: #ab8c02; padding: 8px; border-radius: 15px;">' +
		                                '<i class="fas fa-exclamation-triangle"></i> Partially Working: <strong class="ml-3">' + partiallyWorking + '</strong>' +
		                            '</span>' +
		                            '<span style="background-color: #FF9F9F; color: #A62C2C; padding: 8px; border-radius: 15px;">' +
		                                '<i class="fas fa-times-circle"></i> Not Working: <strong class="ml-3">' + notWorking + '</strong>' +
		                            '</span>' +
		                        '</div>' +
		                        '</div>';
		                }

		                if (!hasValidData) {
		                    content += '<p style="color: black;">No data available.</p>';
		                }

		                // Fetch and append device health information
		                $.ajax({
		                    url: './getLatestRecordWithAlertType',
		                    type: 'GET',
		                    data: { branchCode: branchData.branch },
		                    success: function(latestRecordResponse) {
		                        if (latestRecordResponse && latestRecordResponse.payload) {
		                            const latestRecord = latestRecordResponse.payload.latestRecord;

		                            const syncTimeFormatted = new Date(latestRecord.dateTime).toLocaleString();
		                            const mainStatus = latestRecord.main == 0 ? "Power Off" : "Power On";
		                            const solarStatus = latestRecord.solar == 0 ? "Not Connected" : "Connected";
		                            const batteryStatus = latestRecord.battery == 0 ? "Not Installed" :
		                                latestRecord.battery == 1 ? "Charging" :
		                                latestRecord.battery == 2 ? "Battery Low" : "Battery Full";
		                            const signalStatus = latestRecord.signalFlag == 0 ? "No Signal" :
		                                latestRecord.signalFlag == 1 ? "Unknown Signal" :
		                                latestRecord.signalFlag == 2 ? "Very Poor Signal" :
		                                latestRecord.signalFlag == 3 ? "Poor Signal" :
		                                latestRecord.signalFlag == 4 ? "Fair Signal" : "Good Signal";
		                            const armedStatus = latestRecord.armed == 0 ? "Active" : "Disarmed";


		                            

		                            content += 

		                            	'<div style="margin-top: 20px; padding: 10px; background-color: #FBFBFB; border: 1px solid #ccc; border-radius: 10px;">' +
		                            	    '<h5 style="color: #1F509A; margin-bottom: 10px;">Device Health</h5>' +
		                            	    '<div style="display: flex; flex-wrap: nowrap; gap: 10px; overflow-x: auto; font-size:13px;">' +
		                            	        '<div style="flex: 0 0 auto; background-color:#B1F0F7; color:#1d1d1d; padding:5px; border-radius:10px;"><strong> <i class="fa fa-refresh mx-1" aria-hidden="true"></i>Sync Time:</strong> ' + syncTimeFormatted + '</div>' +
		                            	        '<div style="flex: 0 0 auto; background-color:#BFECFF; color:#1d1d1d; padding:5px; border-radius:10px;""><strong><i class="fa fa-plug mx-1" aria-hidden="true"></i>Main:</strong>  ' + mainStatus + '</div>' +
		                            	        '<div style="flex: 0 0 auto; background-color:#CDFCF6; color:#1d1d1d; padding:5px; border-radius:10px;""><strong><i class="fa fa-sun-o mx-1" aria-hidden="true"></i>Solar:</strong> ' + solarStatus + '</div>' +
		                            	        '<div style="flex: 0 0 auto; background-color:#B9F3E4; color:#1d1d1d; padding:5px; border-radius:10px;""><strong> <i class="fa fa-battery-full mx-1" aria-hidden="true"></i>Battery:</strong> ' + batteryStatus + '</div>' +
		                            	        '<div style="flex: 0 0 auto; background-color:#C6E7FF; color:#1d1d1d; padding:5px; border-radius:10px;""><strong> <i class="fa fa-signal mx-1" aria-hidden="true"></i>Signal:</strong> ' + signalStatus + '</div>' +
		                            	        '<div style="flex: 0 0 auto; background-color:#D9EDBF; color:#1d1d1d; padding:5px; border-radius:10px;""><strong><i class="fa fa-shield mx-1" aria-hidden="true"></i>Status:</strong> ' + armedStatus + '</div>' +
		                            	    '</div>' +
		                            	'</div>';



		                        }
		                        $('#modalContent').html(content);
		                    },
		                    error: function(xhr) {
		                        console.error("Device health AJAX error:", xhr.responseText);
		                        $('#modalContent').html(content);
		                    }
		                });
		            } else {
		                $('#modalContent').html('<p class="text-danger" style="color:black;">Error: ' + response.message + '</p>');
		            }
		        },
		        error: function(xhr, status, error) {
		            $('#modalContent').html('<p class="text-danger" style="color:black;">Failed to fetch data.</p>');
		            console.error('Error: ' + error);
		        }
		    });
		}

		function toggleMarkers() {
			markers.forEach(function(marker) {
				if (braodcastBranchCode.includes(marker.branch)) {
					marker.setVisible(!marker.getVisible());
				} else {

				}
			});
			if (braodcastBranchCode.includes(markers[0].branch)) {
				marker.setVisible(!marker.getVisible());
			}
		}
	</script>
<!-- 	
<script>
    document.getElementById("branchIdDropdown").addEventListener("change", function () {
        const selectedBranchId = this.value;

        fetch("/updateBranchId", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                "branchId": selectedBranchId
            })
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error("Network response was not ok.");
        })
        .then(data => {
            if (data.statusCode === 200) {
                // Success, reload the page
                location.reload();
            } else {
                alert("Failed to update branch ID: " + data.message);
            }
        })
        .catch(error => {
            console.error("Error updating branch ID:", error);
            alert("An error occurred while updating branch ID.");
        });
    });
</script> -->
<script>
function displayLoader(text = 'Loading...') {
    $('#fullPageLoader .loader-text').text(text);
    $('#fullPageLoader').fadeIn(200);
}

// Function to hide full page loader
function dismissLoader() {
    $('#fullPageLoader').fadeOut(200);
}

$(document).ready(function() {
	
	var loggedInbranchCode = sessionStorage.getItem("branch");
    $('#zoneIdDropdown').prop('disabled', true);
    $('#branchIdDropdown').prop('disabled', true);

    $.ajax({
        url: './getAllChildren',
        method: 'GET',
		data:{branchCode:loggedInbranchCode},
        success: function(data) {
            var selectElement = $('#headIdDropdown');
            selectElement.empty();
            selectElement.append('<option value="">Select Head-Office Branch</option>');

            if (data.payload && data.payload.length > 0) {
    $.each(data.payload, function(index, branch) {
        if (branch.branchType === "HEAD OFFICE") {
            selectElement.append('<option value="' + branch.branchCode + '">' + branch.branchDesc + '</option>');
        }
    });
    selectElement.prop('disabled', false);
}else {
                selectElement.prop('disabled', true);
            }
        },
        error: function(xhr, status, error) {
            console.error("AJAX error:", status, error);
            alert("Error loading head office branches. Please try again.");
        }
    });

    $('#headIdDropdown').change(function() {
        var branchCode = $(this).val();
        if (branchCode) {
            $('#zoneIdDropdown').prop('disabled', false);

            $.ajax({
                url: './getAllZonesByBranchCode',
                method: 'GET',
                data: { branchCode: branchCode },
                success: function(data) {
                    var selectElement = $('#zoneIdDropdown');
                    selectElement.empty();
                    selectElement.append('<option value="">Select Zone</option>');

                    if (data.payload && data.payload.length > 0) {
                        $.each(data.payload, function(index, zone) {
                            selectElement.append('<option value="' + zone.branchCode + '">' + zone.branchDesc + '</option>');
                        });
                        selectElement.prop('disabled', false);
                    } else {
                        selectElement.prop('disabled', true);
                    }
                },
                error: function(xhr, status, error) {
                    console.error("AJAX error:", status, error);
                    alert("Error loading zones. Please try again.");
                }
            });
        } else {
            $('#zoneIdDropdown').empty().append('<option value="">Select Zone</option>');
            $('#zoneIdDropdown').prop('disabled', true);
            $('#branchIdDropdown').empty().append('<option value="">Select Branch</option>');
            $('#branchIdDropdown').prop('disabled', true);
        }
    });

    $('#zoneIdDropdown').change(function() {
        var zoneCode = $(this).val();
        if (zoneCode) {
            $('#branchIdDropdown').prop('disabled', false);

            $.ajax({
                url: './getAllBranchesAndDevicesbyBranchCode',
                method: 'GET',
                data: { branchCode: zoneCode },
                success: function(data) {
                    var selectElement = $('#branchIdDropdown');
                    selectElement.empty();
                    selectElement.append('<option value="">Select Branch</option>');

                    if (data.payload && data.payload.length > 0) {
                        $.each(data.payload, function(index, branch) {
                            selectElement.append('<option value="' + branch.branchCode + '">' + branch.branchName + '</option>');
                        });
                        selectElement.prop('disabled', false);
                    } else {
                        selectElement.prop('disabled', true);
                    }
                },
                error: function(xhr, status, error) {
                    console.error("AJAX error:", status, error);
                    alert("Error loading branches. Please try again.");
                }
            });
        } else {
            $('#branchIdDropdown').empty().append('<option value="">Select Branch</option>');
            $('#branchIdDropdown').prop('disabled', true);
        }
    });

    // Updated Search Button with Loader
    $('#searchButton').click(function() {
        var headOfficeCode = $('#headIdDropdown').val();
        var zoneCode = $('#zoneIdDropdown').val();
        var branchCode = $('#branchIdDropdown').val();

        var selectedCode = "";

        if (branchCode) {
            selectedCode = branchCode;
        }
        else if (zoneCode) {
            selectedCode = zoneCode;
        }
        else if (headOfficeCode) {
            selectedCode = headOfficeCode;
        }

        if (selectedCode) {
            // Show loader with search message
            displayLoader('Searching...');

            fetch("./updateBranchId", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: new URLSearchParams({
                    "branchId": selectedCode
                })
            })
            .then(response => {
                if (response.ok) {
                    return response.json();
                }
                throw new Error("Network response was not ok.");
            })
            .then(data => {
                if (data.statusCode === 200) {
                    // Keep loader visible during page reload
                    displayLoader('Refreshing page... It wil take few seconds..');
                    location.reload();
                } else {
                    dismissLoader();
                    alert("Failed to update branch ID: " + data.message);
                }
            })
            .catch(error => {
                dismissLoader();
                console.error("Error updating branch ID:", error);
                alert("An error occurred while updating branch ID.");
            });
        } else {
            alert("Please select Head-Office, Zone, or Branch before searching.");
        }
    });

    // Updated Reset Button with Loader
    $('#resetButton').click(function() {
        // Show loader with reset message
        displayLoader('Resetting...');

        $('#headIdDropdown').val('').prop('disabled', false);
        $('#zoneIdDropdown').val('').prop('disabled', true).empty().append('<option value="">Select Zone</option>');
        $('#branchIdDropdown').val('').prop('disabled', true).empty().append('<option value="">Select Branch</option>');

        var loggedInBranchCode = sessionStorage.getItem("firstLoggedInBranchCode");

        fetch("./updateBranchId", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                "branchId": loggedInBranchCode
            })
        })
        .then(response => {
            if (response.ok) {
                // Keep loader visible during page reload
                displayLoader('Refreshing page... It wil take few seconds..');
                window.location.reload();
                return response.json();
            }
            throw new Error("Network response was not ok.");
        })
        .then(data => {
            if (data.statusCode === 200) {
                console.log("Branch reset successfully.");
            } else {
                dismissLoader();
                alert("Failed to reset branch ID: " + data.message);
            }
        })
        .catch(error => {
            dismissLoader();
            console.error("Error resetting branch ID:", error);
            alert("An error occurred while resetting branch ID.");
        });
    });
});

</script>

	<script async defer
		src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDMKndzFcfiysofJH9zTVJtOCeIH70nKcc&callback=initMap"></script>

<script src="./js/hmsDashboardExtendedScript.js"></script>
	<script src="./js/hmsDashboard.js"></script>



	<!--**********************************
        Scripts
    ***********************************-->

<script src="./externalJS/chart.js"></script>
	<script src="./js/dateAndTime.js"></script>
	<script src="./vendor/global/global.min.js"></script>
	<script src="./dashboardJS/quixnav-init.js"></script>
	<script src="./dashboardJS/custom.min.js"></script>
	<script src="./vendor/chart.js/chart.min.js"></script>
	<script src="./vendor/chartist/chartist.min.js"></script>
	<script src="./externalJS/bootstrap.bundle.min.js"></script>
	<script src="./vendor/easy-pie-chart/jquery.easypiechart.min.js"></script>
	<script src="./vendor/raphael/raphael.min.js"></script>
	<script src="./vendor/morris/morris.min.js"></script>
	<script src="./dashboardJS/dashboard/dashboard-1.js"></script>
	<script src="./externalJS/select2.min.js"></script>
</body>

</html>