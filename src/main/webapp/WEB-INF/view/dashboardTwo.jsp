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

<style>
.hideScroll::-webkit-scrollbar {
  display: none;
}

    
  .heading{
color: #ffffff;
background-color: #003d87; 
height:30px; 
border-bottom: 2px solid #028bd7;  
border-left: 20px solid #028bd7;
padding-left: 20px;
border-radius: 5px;
}
.modal-lg {
        max-width: 80%;
    }
    .modal-body {
        max-height: 60vh;
        overflow-y: auto;
    }
    
 .tooltip-inner {
    background-color: rgba(255, 255, 255, 0.9);
    color: black;
    border: 1px solid black;
    padding: 10px;
    font-weight: bold;
    white-space: pre-wrap;
    max-width: none;
    text-align: left; 
}


.tooltip-arrow {
    border: 1px solid black; 
}

.tooltip {
    display: inline-block;
}

table th,td{
 white-space: nowrap;
}
   
    
</style>
</head>
<body>
<input id="loggedInBranch" value="${branchId}" style="display: none;">
<input id="userId" value="${loginUserId}" style="display:none;">
<input id="remarksList" value="${remarksListForBranchBoxes.payload}" style="display:none;">

<input id="panelSerialNumList" value="${panelSerialNumList.payload}" style="display:none;">

<div class="row m-2">
    <div class="col-12">
        <div class="">
            <div class="card">
                <div class="row">
                    <div class="col-md-12">
                        <h5 class="heading">Branch Live Status</h5>
                    </div>

                    
                    <div class="d-flex flex-wrap">
                        <!-- Status Indicators -->
                        <div class="d-flex flex-wrap">
                            <div class="p-2 ml-4 text-dark text-bold"><i class="fa-regular fa-circle-dot mx-1"></i> Armed</div>
                            <div class="p-2 text-dark text-bold"><i class="fa-solid fa-circle mx-1" style="color:#58d68d"></i> Working</div>
                            <div class="p-2 text-dark text-bold"><i class="fa-solid fa-circle" style="color: #df6262;"></i> Alarmed</div>
                        </div>
                        <div class="d-flex justify-content-end flex-wrap">
                            <div class="p-2 ml-4 text-dark text-bold"><i class="fa-solid fa-circle mx-1" style="color:#FFF100;"></i> Isolated</div>
                            <div class="p-2 text-dark text-bold"><i class="fa-solid fa-circle mx-1" style="color:orange;"></i> Health Not Updated (6 hours)</div>
                            
                            <!-- Search Box -->
                    <div class="col-md-4 my-2">
                        <input type="text" id="branchSearch" class="form-control form-control-sm" placeholder="Search Branches..." onkeyup="filterBranches()">
                    </div>
                           
                            <div class="d-flex justify-content-end flex-wrap">
    <div class="p-2 text-dark text-bold text-primary ml-4"><b class="text-primary">Devices: ${deviceCount.payload[0]}</b></div>
    <div class="p-2 text-dark text-bold text-success"><b style="color:#58d68d">Working: ${deviceStatusCount.payload.workingDeviceCount}/${deviceCount.payload[0]}</b></div>
    <div class="p-2 text-dark text-bold text-danger"><b style="color: #df6262;">Not Working: ${deviceStatusCount.payload.notWorkingCount}/${deviceCount.payload[0]}</b></div>
  </div>
                            
                        </div>
                    </div>
                </div>

                <div class="card-body" style="background-color: #ffff; min-height:550px; max-height:700px; overflow-y:scroll;">
                    <div class="row hideScroll" id="branchListContainer">
                        <c:forEach var="branch" items="${branchList.payload}">
                            <div class="col-md-2 mb-1 branch-box-container">
                                <div class="hideScroll branch-box" style="border-radius:5px; min-height:50px; max-height:50px; overflow-y:scroll;" 
                                    data-toggle="modal" data-target="#branchModal"
                                    data-id="${branch.branchCode}" data-desc="${branch.branchName}">
                                    <h6 class="text-dark m-2 text-center branch-name" style="font-size:11px;">
                                        <c:if test="${branch.armedFlag == 'Armed'}">
                                            <i class="fa-regular fa-circle-dot mx-1"></i>
                                        </c:if>
                                        ${branch.branchName}
                                    </h6>
                                    <div class="" onclick="updateBranchCode('${branch.branchCode}');">
                                        <p class="text-dark m-0 text-center" style="font-size:10px;"> <b>${branch.lastSyncedTime}</b></p>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
	<div class="col-12">
		<div class="card mb-3" style="background-color: #ffff; max-height:310px;">
			<div class="row">
				<div class="col-md-12">
					<h5 class="heading">Status of Sites</h5>
				</div>
			</div>
			<div class="card-body py-3">
				<div class="row mx-2">
					<div class="col-md-4 mt-1">
						<div class="stat-widget-two">
							<div class="stat-content d-flex totalDevices">
								<a href="#branchesDetailsTable" class="align-items-center"
									style="color: #003d87; border-left: solid 5px #003d87;">
									<h6 style="color: #003d87;" class="m-2 mb-0">Total Devices</h6>
									<div class="branchId" style="display: none">${branchId}</div>
									<div class="stat-digit text-dark mx-3">${deviceCount.payload[0]}</div>
								</a>
							</div>
						</div>
					</div>
					<div class="col-md-4 mt-1">
						<div class="stat-widget-two">
							<div class="stat-content d-flex flex-column workingDevices"
								style="color: #337357; border-left: solid 5px #337357;">
								<h6 style="color: #337357;" class="m-2 mb-0 text-left">Working</h6>
								<div class="stat-digit text-dark text-left mx-3">${deviceStatusCount.payload.workingDeviceCount}/${deviceCount.payload[0]}</div>
							</div>
						</div>
					</div>

					<div class="col-md-4 mt-1">
						<div class="stat-widget-two">
							<div class="stat-content d-flex notWorkingDevices"
								onclick="toggleScrolltoNotWorking()">
								<a href="#totalDevice" data-toggle="collapse"
									data-target="#collapseOne" aria-expanded="false"
									aria-controls="collapseOne"
									style="color: #B31312; border-left: solid 5px #B31312;">
									<h6 style="color: #B31312;" class="m-2 mb-0">Not Working</h6>
									<div class="stat-digit text-dark mx-3">${deviceStatusCount.payload.notWorkingCount}/${deviceCount.payload[0]}</div>
								</a>
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
									<th class="text-right">IFSC</th>
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
												<td id="branchValue" value="${childBranch.branchCode}" class="text-right">${childBranch.ifsc}</td>
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


	<div class="row mx-2" id="deviceHealthTable" style="display: none;">
		<div class="col-md-12">
			<div class="card" style="">
				<div class="row">
					<div class="col-md-12">
						<h5 class="heading">Smart Communication Health</h5>
					</div>
				</div>
				<div class="card-body">
					<div class="table-responsive table-bordered"
						style="max-height: 250px; overflow-y: auto; font-size: 14px">
						<div id="messageContainer" style="display: none;"></div>

						<!-- Table to display fetched data -->
						<table id="dataTable" class="table mb-0">
							<thead
								style="color: #fff; position: sticky; top: 0; z-index: 1; background: #028bd7;">
								<tr>
									<th style="text-align:center">Branch Name</th>
									<th class="text-center">Health Communicator</th>
									<th class="text-center">Sync Time</th>
									<th class="text-center"> Main</th>
									<th class="text-center">Solar</th>
									<th class="text-center">Battery</th>
									<th class="text-center">Signal</th>
									<th class="text-center">Status</th>


								</tr>
							</thead>
							<tbody id="dataBody" onclick="scrollToInputStatusDetails()"
								style="color: #028bd7; cursor: pointer; color: black;">
							</tbody>
						</table>

					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="col-lg-12 col-md-12" id="statusTable"
		style="display: none;">
		<div class="card" style="">
			<div class="row">
				<div class="col-md-10 col-sm-10">
					<h5 class="heading">Input Status Details</h5>
				</div>
			</div>
			<!-- Filter Dropdown -->
			<div class="filter-dropdown ml-3 mb-3 col-md-2 col-sm-2"
				style="position: absolute; right: 0;">

				<select class="form-control" id="inputFilter"
					onchange="filterInputs(this.value)">
					<option value="all">All Devices</option>
					<option value="alarm">Alerts Devices</option>
					<option value="disable">Disable Devices</option>
					<option value="normal">Normal Devices</option>
				</select>
			</div>

			<div class="card-header">
				<div class="table-responsive table-striped table-bordered mb-2"
					style="max-height: 250px; overflow-y: auto; font-size: 12px">
	     			<table class="table mb-0">
						<thead
							style="color: #fff; position: sticky; top: 0; z-index: 1; background: #028bd7; text-align: center;">
							<tr>

								<th class="text-center">S No.</th>
								<th class="text-center">Device Name</th>
								<th class="text-center">Alert Type</th>
								<th class="text-center">Status</th>
							</tr>
						</thead>
						<!-- <tbody id="activeZonesBody"
							style="color: black; text-align: center; font-size: 14px;">

						</tbody> -->
					</table>
				</div>
			</div>
		</div>
	</div>




	<div class="row mx-2">


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
										style="font-weight: 600; color: #028bd7; display:none;">Select Month</label>
								</div>
								<div class="col-lg-2  col-md-2 col-sm-6 mt-1">
									<input
										class="form-control form-control-sm rounded typeahead stylish-input"
										type="month" id="monthFilter" style="display:none;">
								</div>
								
								<div class="col-lg-2 col-sm-2 col-md-2 mt-1">
									<label style="font-weight: 600; color: #028bd7; display:none;">Select
										Year</label>
								</div>
								<div class="col-lg-4 col-sm-4 col-md-4">
									<div class="btn-group">
										<button type="button"
											class="btn btn-primary dropdown-toggle year-dropdown w-100 btn-block"
											data-toggle="dropdown" aria-haspopup="true"
											aria-expanded="false" style="display:none;">Select Year</button>
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
					<p class="card-title ml-3">
						<i class="fa-regular fa-bell fa-bounce fa-lg"></i> <span
							id="total"></span>
					</p>
					<div class="row">
						<div class="col-lg-4 col-md-4 col-sm-12">

							<canvas id="sold-product"></canvas>

						</div>


						<div class="col-lg-8 col-md-8 col-sm-12 mx-0">
							<div class="table-responsive"
								style="max-height: 220px; overflow-y: auto;">
								<table id="auditTable" class="table table-bordered" style="color: #003d87;">
									<thead
										style="color: #fff; position: sticky; top: 0; z-index: 1; background: #028bd7;">
										<tr>
											<th class="text-left col-3">Service Provider</th>
											<th class="text-left col-3">Alarmed Zone</th>
											<th class="text-left">Zone</th>
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



<div class="row mx-2">
		<div class="col-lg-12 col-md-12 col-sm-12 mt-0">
			<div class="card mx-0">

				<div class="row">
					<div class="col-12">
						<h5 class="heading">Alert Reports</h5>
					</div>
				</div>

				<div class="card-header" id="AlertDetailsTable">

					<div class="table-responsive mb-2"
						style="min-height: 250px; max-height: 350px; overflow-y: auto;">


						<table class="table table-striped table-bordered mb-0">
							<thead
								style="color: #fff; position: sticky; top: 0; z-index: 1; background: #028bd7;">
								<tr>

									<th style="display: none;">Bank Name</th>
									<th>S.No</th>
									<th class="text-right">IFSC</th>
									<th class="text-left col-2">Service Provider</th>
									<th class="text-left col-4">Branch Address</th>
									<th class="text-right">Time Stamp</th>
									<th class="text-left">Zone Name</th>
									<th class="text-left">Alert Type</th>
									
								</tr>
							</thead>
							<tbody id="AlertTableBody">
							</tbody>
						</table>


					</div>



				</div>

				<div class="pagination" id="paginationContainer"></div>
			</div>

		</div>

	</div>



<!-- Modal with Table -->
<div class="modal fade" id="branchModal" tabindex="-1" role="dialog" aria-labelledby="tableModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="tableModalLabel">Branch Details</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <p><strong class="text-dark">Branch Name:</strong> <span id="modalBranchDesc"></span></p>
                <p><strong class="text-dark">Branch Code:</strong> <span id="modalBranchCode"></span></p>
                <table id="alarmZones" class="table table-striped table-bordered">
                    <thead style="background: #1c0f54; color: #ffff;">
                        <tr>
                        <th>S.No</th>
														    <th>Input Desired Name</th>
														  	<th>Vendor Name</th>
															<th>Vendor Number</th>
															<th>Branch Address</th>
															<th>Contact No.</th>
															<th>Device ID</th>
															<th>Alert Zone</th>
															<th>Resolution Status</th>
															<th>Remarks</th>
															<th>Action</th> 
                        </tr>
                    </thead>
                    <tbody class="text-dark">
                        <!-- Data rows will be added dynamically -->
                    </tbody>
                </table>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>



<script>

function fetchAlertFlags(branchCode, activeZone, callback) {
    $.ajax({
        url: './getAlertFlag',
        type: 'GET',
        data: { branchCode: branchCode },
        success: function(response) {
            if (response.statusCode === 200) {
                let alertFlags = response.payload;

                // Process activeZone to extract values before the colon and store them in an array
                let processedZones = activeZone.split('_').filter(Boolean).map(zone => zone.split(':')[0]);

                let matchFound = false;
                processedZones.forEach(function(zone) {
                    alertFlags.forEach(function(flag) {
                        if (zone === flag.alertType && flag.alertFlag === "Y") {
                            matchFound = true;
                        }
                    });
                });

                callback(matchFound);
            } else {
                console.log("Failed to fetch alert flags. Status Code:", response.statusCode);
                callback(false);
            }
        },
        error: function(xhr, status, error) {
            console.error("Error fetching alert flags:", error);
            callback(false);
        }
    });
}



</script>

	<script>
var clickedBranchCodeScreenTwo = '';
var branchDataScreenTwo = {};
var activeZoneScreenTwo = {};
function updateBranchCode(branchCode) {
	
	clickedBranchCodeScreenTwo = branchCode;
	
	//alert(clickedBranchCodeScreenTwo)
}



function fetchDataOnLoad() {
	
	//alert("hi")
	
	var panelSerialNumbers= $("#panelSerialNumList").val();
    // Get the value of the input field and parse it into an array
    //const panelSerialNumbers = JSON.parse(document.getElementById("panelSerialNumList").value);
    panelSerialNumbers = panelSerialNumbers.replace(/[\[\]]/g, ''); // Removes square brackets

    // Debug: Log the parsed list
    //alert("Panel Serial Numbers:"+ panelSerialNumbers);

    $.ajax({
        url: 'fetchDataAndBroadcastByLatestTime',
        method: 'GET',
        data: { panelSerialNumbers: panelSerialNumbers }, // Pass the list as a query parameter
        traditional: true, // Ensures proper serialization of array parameters
        success: function(response) {
        	
        	//alert(JSON.stringify(response))
        	
        	console.log(JSON.stringify(response))
            if (Array.isArray(response)) {
                response.forEach(function(message) {
                    console.log(JSON.stringify(message));
                    // Call any function or process the received data here
                    fetchDeviceForScreenTwo(message);
                });
            } else {
                console.warn('Unexpected response format:', response);
            }
        },
        error: function(error) {
            console.error('Error fetching data:', error);
        }
    });
}

// Trigger the function on load
$(document).ready(function () {
    fetchDataOnLoad();
});






function fetchDataScreenTwo() {
    $.ajax({
        url: './fetchDataAndBroadcast',
        method: 'GET',
        success: function(response) {
            response.forEach(function(message) {
            	console.log(JSON.stringify(message))
            	
            	fetchDeviceForScreenTwo(message);
            	//alert(JSON.stringify(message))
                //processMessageScreenTwo(message);
            });
        },
        error: function(error) {
            console.error('Error fetching data:', error);
        }
    });
}
function fetchCompleteMessage(branchCode) {
    $.ajax({
        url: './getLatestRecord?branchCode=' + branchCode,
        method: 'GET',
        success: function(response) {
            var completeMessage = response.payload.latestRecord.completeMessage;
            fetchExtractCodes(completeMessage,branchCode);
        },
        error: function(error) {
            console.error('Error fetching data:', error);
        }
    });
}

function fetchExtractCodes(completeMessage, branchCode) {
    var encodedMessage = encodeURIComponent(completeMessage);

    $.ajax({
        url: 'extractCodesFromMessage?message=' + encodedMessage,
        method: 'GET',
        success: function(response) {
            var extractedMessageCodes = response;  
            var messageCodesArray = extractedMessageCodes.replace(/'/g, '').split(',');  

            fetchLeoStaticDataCodes(messageCodesArray, branchCode);
        },
        error: function(error) {
            console.error('Error fetching data:', error);
        }
    });
}


function fetchLeoStaticDataCodes(messageCodes, branchCode) {
    $.ajax({
        url: './getLeoStaticDataCodes',
        method: 'GET',
        traditional: true,  
        data: { messageCode: messageCodes },  
        success: function(response) {
            console.log("Data fetched successfully:", response);

            // Store the fetched data for this branch
            branchDataScreenTwo[branchCode].messageData = response.payload;

            // Update the tooltip content
            updateBranchTooltip(branchCode);
        },
        error: function(error) {
            console.error("Error fetching data:", error);
        }
    });
}
function updateBranchTooltip(branchCode) {
    const branchInfo = branchDataScreenTwo[branchCode];

    if (branchInfo && branchInfo.messageData) {
        let tooltipContent = '';

        branchInfo.messageData.forEach(function(data, index) {
            tooltipContent += (data.messageCode ? data.messageCode : '') + " : " + (data.messageDesc ? data.messageDesc : '');

            if (index < branchInfo.messageData.length - 1) {
                tooltipContent += '\n';
            }
        });

        const branchElement = document.querySelector('.branch-box[data-id="' + branchCode + '"]');

        if (branchElement) {
            branchElement.setAttribute('title', tooltipContent);

            $(branchElement).tooltip('dispose').tooltip({
                title: tooltipContent,
                html: true
            }).tooltip('show');
        }
    }
}



 $(document).ready(function() {
	 
	 
	 setInterval(getLatestSyncedTime, 200);
	 //localStorage.clear();
	 var storedBranchData = localStorage.getItem('branchDataScreenTwo');
	    if (storedBranchData) {
	        branchDataScreenTwo = JSON.parse(storedBranchData);
	    }
    $('[data-toggle="tooltip"]').tooltip(); 
    fetchDataScreenTwo();
    restoreBranchBoxColors();
   
    setInterval(fetchDataScreenTwo, 2000);
}); 

function restoreBranchBoxColors() {
    $('.branch-box').each(function() {
        var element = $(this);
        var branchCode = element.data('id');
        var savedState = JSON.parse(localStorage.getItem(branchCode));

        if (savedState) {
            element.css({
                'background': savedState.backgroundColor,
                'color': savedState.textColor
            });
        }
    });
}

/* // Call this function once the DOM is ready
$(document).ready(function() {
    restoreBranchBoxColors();
    fetchDataScreenTwo(); // Fetch data after restoring the saved colors
}); */


var flaggedZones = [];
function processMessageScreenTwo(message) {
	
	//alert("message "+message)
    var dataSplit = message.split(",");
    var branchCodeFromMessage = dataSplit[0];
    var deviceId = dataSplit[1];
    activeZoneScreenTwo = dataSplit[8];
    var zoneString = dataSplit[9];
    
    // Process zones and flags as before
    var zonesArray = zoneString.split(/[:,_]/).filter(function(element) {
        return element.trim() !== '';
    });

    for (var i = 0; i < zonesArray.length; i += 2) {
        var zone = zonesArray[i];
        var flag = zonesArray[i + 1];
        if (flag === "I") {
            flaggedZones.push(zone);
        }
    }
    
    
    fetchCompleteMessage(branchCodeFromMessage);
    
    if (activeZoneScreenTwo.length > 0) {
        fetchAlertFlags(branchCodeFromMessage, activeZoneScreenTwo, function(isAlertFlagY) {
            if (isAlertFlagY) {
                if (!branchDataScreenTwo[branchCodeFromMessage]) {
                    branchDataScreenTwo[branchCodeFromMessage] = {
                        alerts: [],
                        details: null,
                        vendorName: '',
                        fetchedBranch: ''
                    };
                }

                $.ajax({
                    url: 'getBranchByBranchCode',
                    type: 'GET',
                    contentType: 'application/json',
                    data: { branchCode: branchCodeFromMessage },
                    success: function(response) {
                        var fetchedBranch = response.payload[0].branchDesc;
                        var branchDetails = response.payload[0];
                        branchDataScreenTwo[branchCodeFromMessage].details = branchDetails;
                        branchDataScreenTwo[branchCodeFromMessage].fetchedBranch = fetchedBranch;

                        $.ajax({
                            url: 'getDeviceDetailsByBranchCode',
                            type: 'GET',
                            contentType: 'application/json',
                            data: { branchCode: branchCodeFromMessage },
                            success: function(response) {
                                var vendorName = response.payload.vendorName;
                                branchDataScreenTwo[branchCodeFromMessage].vendorName = vendorName;

                                var zonesArray = activeZoneScreenTwo.split(/[:,_]/).filter(function(element) {
                                    return element.trim() !== '';
                                });
                                var evenIndicesArray = zonesArray.filter(function(element, index) {
                                    return index % 2 === 0;
                                });

                                // Check if alert already exists to prevent duplicates
                                var alertExists = branchDataScreenTwo[branchCodeFromMessage].alerts.some(function(alert) {
                                    return alert.deviceId === deviceId &&
                                        JSON.stringify(alert.zones) === JSON.stringify(evenIndicesArray);
                                });

                                if (!alertExists) {
                                	//alert("hey")
                                    branchDataScreenTwo[branchCodeFromMessage].alerts.push({
                                        deviceId: deviceId,
                                        zones: evenIndicesArray
                                    });

                                    // Save the updated branch data to localStorage
                                    localStorage.setItem('branchDataScreenTwo', JSON.stringify(branchDataScreenTwo));
                                }

                                updateBranchBoxColor(branchCodeFromMessage, "#f58685", "#C70039");
                                if (branchCodeFromMessage === clickedBranchCodeScreenTwo) {
                                    displayAlarmedZonesListScreenTwo(branchCodeFromMessage);
                                }
                            },
                            error: function(xhr, status, error) {
                                console.error(xhr.responseText);
                            }
                        });
                    },
                    error: function(xhr, status, error) {
                        console.error(xhr.responseText);
                    }
                });
            }
        });
    } else {
    	//alert("hlo")
    	localStorage.clear();
        updateBranchBoxColor(branchCodeFromMessage, "#E0FBE2", "#000000");
    }
}


function fetchDeviceForScreenTwo(message) {
	
	 var dataSplit = message.split(",");
	var branchCodeFromMessage = dataSplit[0];
	
	//alert(branchCodeFromMessage)
    $.ajax({
        url: 'getDeviceDetailsByBranchCode',
        method: 'GET',
        traditional: true,  
        data: { branchCode: branchCodeFromMessage },  
        success: function(response) {
            console.log("Data fetched successfully:", response);
            //alert(JSON.stringify(response.payload));

            // Check the productType value and execute code accordingly
            if (response.payload.productType === "O") {
                // Code to execute if productType is 'O'
                console.log("Executing first code block for productType 'O'");
                
                processMessageScreenTwo(message);
                
            } else if (response.payload.productType === "D") {
                // Code to execute if productType is 'D'
                console.log("Executing second code block for productType 'D'");
                
                processMessageScreenTwoDigitals(message);
                // Add your specific logic for 'D' here
            } else {
                console.log("No matching productType found.");
            }
        },
        error: function(error) {
            console.error("Error fetching data:", error);
        }
    });
}


function updateBranchBoxColor(branchCode, backgroundColor, textColor) {
    if (activeZoneScreenTwo.length === 0) {
        // Apply green background color if there are no active zones
        $('.branch-box').each(function() {
            var element = $(this);
            var dataId = element.data('id');
            if (dataId === branchCode) {
                element.css({
                    'background': '#58d68d',
                    'color': '#FFFFFF'
                }).removeClass('alert-active'); // Remove alert-active class
                updateBranchTooltip(branchCode);

                // Remove the branch color from localStorage if no active zones
                localStorage.removeItem(branchCode);
            }
        });
        return;
    }

    fetchAlertFlags(branchCode, activeZoneScreenTwo, function(isAlertFlagY) {
        var hasFlaggedZones = flaggedZones.length > 0;
        $('.branch-box').each(function() {
            var element = $(this);
            var dataId = element.data('id');
            if (dataId === branchCode) {
                if (isAlertFlagY) {
                    element.css({
                    	 'background': hasFlaggedZones 
                         ? "linear-gradient(to right, #f58685 85%, yellow 15%)"
                         : "#f58685",
                     'color': "#C70039"
                    }).addClass('alert-active'); // Add alert-active class
                } else {
                    element.css({
                        'background': hasFlaggedZones 
                            ? "linear-gradient(to right, rgba(251, 251, 251, 1) -12%, rgb(60, 194, 126) 45%, yellow 50%)"
                            : "linear-gradient(222deg, rgba(251, 251, 251, 1) -12%, rgb(60, 194, 126) 45%)",
                        'color': "#FFFFFF"
                    }).addClass('alert-active'); 
                }
                
                updateBranchTooltip(branchCode);

                localStorage.setItem(branchCode, JSON.stringify({
                    backgroundColor: element.css('background'),
                    textColor: element.css('color')
                }));
            }
        });
    });
}

function displayAlarmedZonesListScreenTwo(branchCode) {
    var branchInfo = branchDataScreenTwo[branchCode];
//alert("==>>"+JSON.stringify(branchInfo))
    // Check if there are active zones or flagged zones
    if (!branchInfo || (branchInfo.alerts.length === 0 && flaggedZones.length === 0)) {
    	
        return;  // Exit function if no active or flagged zones
    }

    var tableBody = document.getElementById('alarmZones').getElementsByTagName('tbody')[0];
    tableBody.innerHTML = '';  // Clear existing rows

    var counter = 0;

    // Function to add rows for zones
    function addRowForZone(zone, vendorName, fetchedBranch, details, deviceId, isFlagged) {
        counter++;
        var newRow = document.createElement('tr');
        if (isFlagged) {
            newRow.style.backgroundColor = 'yellow';  // Highlight flagged zones
        }

        var serialNumberCell = document.createElement('td');
        serialNumberCell.textContent = counter;
        newRow.appendChild(serialNumberCell);

        var vendorCell = document.createElement('td');
        vendorCell.textContent = vendorName;
        newRow.appendChild(vendorCell);

        var branchCell = document.createElement('td');
        branchCell.textContent = fetchedBranch;
        newRow.appendChild(branchCell);

        var addressCell = document.createElement('td');
        addressCell.textContent = details.address1 + "," + details.address2 + "," + details.address3 + "," + details.address4;
        newRow.appendChild(addressCell);

        var mobileCell = document.createElement('td');
        mobileCell.textContent = details.mobile;
        newRow.appendChild(mobileCell);

        var deviceIDCell = document.createElement('td');
        deviceIDCell.textContent = deviceId;
        newRow.appendChild(deviceIDCell);

        var alarmedZoneCell = document.createElement('td');
        alarmedZoneCell.textContent = zone;
        newRow.appendChild(alarmedZoneCell);

        var remarkCell = document.createElement('td');
        var selectDropdown = document.createElement('select');
        selectDropdown.className = 'form-control';

        var remarksListInput = document.getElementById('remarksList');
        if (remarksListInput && remarksListInput.value) {
            var remarks = remarksListInput.value.slice(1, -1).split(',');
            remarks.forEach(function(remark) {
                var optionElement = document.createElement('option');
                optionElement.textContent = remark.trim();
                selectDropdown.appendChild(optionElement);
            });
        }

        remarkCell.appendChild(selectDropdown);
        newRow.appendChild(remarkCell);

        var actionCell = document.createElement('td');
        var submitButton = document.createElement('button');
        submitButton.type = 'button';
        submitButton.className = 'btn btn-primary submitButton';
        submitButton.textContent = 'Submit';
        actionCell.appendChild(submitButton);
        newRow.appendChild(actionCell);

        tableBody.appendChild(newRow);
    }

    // Process active zones
    branchInfo.alerts.forEach(function(alert) {
        alert.zones.forEach(function(zone) {
            fetchAlertFlags(branchCode, zone, function(isAlertFlagY) {
                if (isAlertFlagY) {
                    addRowForZone(zone, branchInfo.vendorName, branchInfo.fetchedBranch, branchInfo.details, alert.deviceId, false);
                }
            });
        });
    });

    // Process flagged zones
    flaggedZones.forEach(function(zone) {
        addRowForZone(zone, branchInfo.vendorName, branchInfo.fetchedBranch, branchInfo.details, branchInfo.alerts[0].deviceId, true);
    });
}



$('#branchModal').on('show.bs.modal', function(event) {
    var button = $(event.relatedTarget);
    var branchDesc = button.data('desc');
    var branchCode = button.data('id');

    var modal = $(this);
    modal.find('#modalBranchDesc').text(branchDesc);
    modal.find('#modalBranchCode').text(branchCode);

    displayAlarmedZonesListScreenTwo(branchCode);
});

$('#branchModal').on('hidden.bs.modal', function() {
    var tableBody = document.getElementById('alarmZones').getElementsByTagName('tbody')[0];
    tableBody.innerHTML = '';
});

document.addEventListener('click', function(event) {
    if (event.target.classList.contains('submitButton')) {
    	 
        var submitButton = event.target;
        var tableRow = submitButton.closest('tr');
         
        var vendorName = tableRow.children[1].textContent;
        var branchName = $('#modalBranchCode').text(); 
        var address = tableRow.children[4].textContent;
        var mobile = tableRow.children[3].textContent;
        var deviceId = tableRow.children[5].textContent;
        var alarmedZone = tableRow.children[6].textContent;
        
        var remarks = tableRow.children[7].textContent;
        var remarkDropdown = tableRow.querySelector('select.form-control');
        var selectedRemark = remarkDropdown.options[remarkDropdown.selectedIndex].textContent;
       
      
        
        var inputRemark = tableRow.children[9]; 
        var inputRemarkField = inputRemark ? inputRemark.querySelector('input.form-control') : null;
        var inputRemarkValue = inputRemarkField ? inputRemarkField.value : '';
       
        var userId = $('#userId').val();
        
        var auditData = {
            deviceId: deviceId,
            activeZone: alarmedZone,
            remarks: selectedRemark,
            user: userId,
            branchCode: branchName,
            mobile: mobile,
            remarks2: inputRemarkValue,
            address: address,
            vendorName: vendorName
        };
        
        alert("auditData:" +JSON.stringify(auditData));
        
        fetch('saveAudit', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(auditData)
        })
        .then(response => response.json())
        .then(data => {
            console.log(data);

            // Remove the row from the table
            tableRow.remove();

            // Check if there are any remaining rows
            var remainingRows = document.querySelectorAll('#alarmZones tbody tr').length;

            // If no rows remain, close the modal and reset branch alerts
            if (remainingRows === 0) {
                $('#branchModal').modal('hide');
                
                $('#branchModal').on('hidden.bs.modal', function() {
                    // Manually remove any remaining backdrop
                    $('.modal-backdrop').remove();

                    var tableBody = document.getElementById('alarmZones').getElementsByTagName('tbody')[0];
                    tableBody.innerHTML = '';
                });
                
                // Clear alerts for the branch
                if (branchDataScreenTwo[branchName]) {
                    branchDataScreenTwo[branchName].alerts = [];
                }

              
                //updateBranchBoxColor(branchName, "linear-gradient(222deg, rgba(251, 251, 251, 1) -12%, rgb(60, 194, 126) 45%)", "#FFFFFF");
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
    }
});
</script>

<script>
/********************************JS for highlighting not synced branches***********************************************/
function getLatestSyncedTime() {
    $.ajax({
        url: 'getLastSyncScheduler', 
        type: 'GET',
        success: function (response) {
            const syncedBranchCodes = response.payload.map(item => item.branchCode); 

            $('.branch-box').each(function() {
                const branchCode = $(this).data('id'); 

                // Only change the color if the branch-box is not under alert
                if (!$(this).hasClass('alert-active')) {
                    if (syncedBranchCodes.includes(branchCode)) {
                        $(this).css("background-color", "orange"); 
                    } 
                }else if ($(this).hasClass('alert-active')) {
                	//alert("red")
                    $(this).css({
                        'background': '#f58685', // Red alert color
                        'color': '#C70039'
                    });
                } else {
                	//alert("green")
                    $(this).css({
                        'background': '#58d68d', // Default green color
                        'color': '#FFFFFF'
                    });
                }
            });
        },
        error: function (xhr, status, error) {
            console.log(error);
        }
    });
}

</script>
<script>

var clickedBranchCodeScreenTwoDigitals = '';
var branchDataScreenTwoDigitals = {};
var activeZoneScreenTwoDigitals = {};
var flaggedZonesDigitals = [];
var zone_desired_name = '';

function updateBranchCodeDigitals(branchCode) {
    clickedBranchCodeScreenTwoDigitals = branchCode;
}

function fetchDataScreenTwoDigitals() {
    $.ajax({
        url: './fetchDataAndBroadcast',
        method: 'GET',
        success: function(response) {
            response.forEach(function(message) {
                console.log(JSON.stringify(message));
                processMessageScreenTwoDigitals(message);
            });
        },
        error: function(error) {
            console.error('Error fetching data:', error);
        }
    });
}

$(document).ready(function() {
    //setInterval(getLatestSyncedTimeDigitals, 200);

    var storedBranchDataDigitals = localStorage.getItem('branchDataScreenTwoDigitals');
    if (storedBranchDataDigitals) {
        branchDataScreenTwoDigitals = JSON.parse(storedBranchDataDigitals);
    }

    $('[data-toggle="tooltip"]').tooltip();
   // fetchDataScreenTwoDigitals();
    restoreBranchBoxColorsDigitals();

    setInterval(fetchDataScreenTwoDigitals, 200);
});

function restoreBranchBoxColorsDigitals() {
    $('.branch-box').each(function() {
        var element = $(this);
        var branchCode = element.data('id');
        var savedState = JSON.parse(localStorage.getItem(branchCode));

        if (savedState) {
            element.css({
                'background': savedState.backgroundColor,
                'color': savedState.textColor
            });
        }
    });
}

function processMessageScreenTwoDigitals(message) {
    var dataSplit = message.split(",");
    var branchCodeFromMessageDigitals = dataSplit[0];
    var deviceIdDigitals = dataSplit[1];
    activeZoneScreenTwoDigitals = dataSplit[8];
    var zoneStringDigitals = dataSplit[10];
	var branchCodes = [];
	zone_desired_name=dataSplit[9];
	var vendorNumber='';
    var zonesArrayDigitals = zoneStringDigitals.split(/[:,_]/).filter(function(element) {
        return element.trim() !== '';
    });

    for (var i = 0; i < zonesArrayDigitals.length; i += 2) {
        var zone = zonesArrayDigitals[i];
        var flag = zonesArrayDigitals[i + 1];
        if (flag === "I") {
            flaggedZonesDigitals.push(zone);
        }
    }

    if (activeZoneScreenTwoDigitals.length > 0) {
        if (!branchDataScreenTwoDigitals[branchCodeFromMessageDigitals]) {
            branchDataScreenTwoDigitals[branchCodeFromMessageDigitals] = {
                alerts: [],
                details: null,
                vendorName: '',
                fetchedBranch: ''
            };
        }

        $.ajax({
            url: 'getBranchByBranchCode',
            type: 'GET',
            contentType: 'application/json',
            data: { branchCode: branchCodeFromMessageDigitals },
            success: function(response) {
                var fetchedBranchDigitals = response.payload[0].branchDesc;
                var branchDetailsDigitals = response.payload[0];
                branchDataScreenTwoDigitals[branchCodeFromMessageDigitals].details = branchDetailsDigitals;
                branchDataScreenTwoDigitals[branchCodeFromMessageDigitals].fetchedBranch = fetchedBranchDigitals;

                $.ajax({
                    url: 'getDeviceDetailsByBranchCode',
                    type: 'GET',
                    contentType: 'application/json',
                    data: { branchCode: branchCodeFromMessageDigitals },
                    success: function(response) {
                        var vendorNameDigitals = response.payload.vendorName;
                        
                        branchDataScreenTwoDigitals[branchCodeFromMessageDigitals].vendorName = vendorNameDigitals;

                        var zonesArrayDigitals = activeZoneScreenTwoDigitals.split(/[:,_]/).filter(function(element) {
                            return element.trim() !== '';
                        });
                        var evenIndicesArrayDigitals = zonesArrayDigitals.filter(function(element, index) {
                            return index % 2 === 0;
                        });

                        var alertExistsDigitals = branchDataScreenTwoDigitals[branchCodeFromMessageDigitals].alerts.some(function(alert) {
                            return alert.deviceId === deviceIdDigitals &&
                                JSON.stringify(alert.zones) === JSON.stringify(evenIndicesArrayDigitals);
                        });

                        if (!alertExistsDigitals) {
                            branchDataScreenTwoDigitals[branchCodeFromMessageDigitals].alerts.push({
                                deviceId: deviceIdDigitals,
                                zones: evenIndicesArrayDigitals
                            });

                            localStorage.setItem('branchDataScreenTwoDigitals', JSON.stringify(branchDataScreenTwoDigitals));
                        }

                        updateBranchBoxColorDigitals(branchCodeFromMessageDigitals, "#f58685", "#C70039");
                        if (branchCodeFromMessageDigitals === clickedBranchCodeScreenTwoDigitals) {
                            displayAlarmedZonesListScreenTwoDigitals(branchCodeFromMessageDigitals);
                        }
                    },
                    error: function(xhr, status, error) {
                        console.error(xhr.responseText);
                    }
                });
            },
            error: function(xhr, status, error) {
                console.error(xhr.responseText);
            }
        });
    } else {
        localStorage.clear();
        updateBranchBoxColorDigitals(branchCodeFromMessageDigitals, "#E0FBE2", "#000000");
    }
}

function updateBranchBoxColorDigitals(branchCode, backgroundColor, textColor) {
    if (activeZoneScreenTwoDigitals.length === 0) {
        $('.branch-box').each(function() {
            var element = $(this);
            var dataId = element.data('id');
            if (dataId === branchCode) {
                element.css({
                    'background': '#58d68d',
                    'color': '#FFFFFF'
                }).removeClass('alert-active');
                localStorage.removeItem(branchCode);
            }
        });
        return;
    }

    $('.branch-box').each(function() {
        var element = $(this);
        var dataId = element.data('id');
        if (dataId === branchCode) {
            var hasFlaggedZonesDigitals = flaggedZonesDigitals.length > 0;
            element.css({
                'background': hasFlaggedZonesDigitals 
                    ? "linear-gradient(to right, #f58685 85%, yellow 15%)"
                    : "#f58685",
                'color': "#C70039"
            }).addClass('alert-active');

            localStorage.setItem(branchCode, JSON.stringify({
                backgroundColor: element.css('background'),
                textColor: element.css('color')
            }));
        }
    });
}

function displayAlarmedZonesListScreenTwoDigitals(branchCode) {
	let matches = [...zone_desired_name.matchAll(/(.+?)_/g)]; 
	var desiredNamedList = [];

	let extractedValues = matches.map(match => match[1]); // Extract matched groups
	desiredNamedList.push(extractedValues);
	alert(JSON.stringify(desiredNamedList));
			
    var branchInfoDigitals = branchDataScreenTwoDigitals[branchCode];

    if (!branchInfoDigitals || (branchInfoDigitals.alerts.length === 0 && flaggedZonesDigitals.length === 0)) {
        return;
    }

    var tableBodyDigitals = document.getElementById('alarmZones').getElementsByTagName('tbody')[0];
    tableBodyDigitals.innerHTML = '';

    var counter = 0;

    branchInfoDigitals.alerts.forEach(function(alert) {
        alert.zones.forEach(function(zone) {
            addRowForZoneDigitals(desiredNamedList,zone, branchInfoDigitals.vendorName, branchInfoDigitals.fetchedBranch, branchInfoDigitals.details, alert.deviceId, false);
        });
    });

    flaggedZonesDigitals.forEach(function(zone) {
        addRowForZoneDigitals(desiredNamedList,zone, branchInfoDigitals.vendorName, branchInfoDigitals.fetchedBranch, branchInfoDigitals.details, branchInfoDigitals.alerts[0].deviceId, true);
    });

    function addRowForZoneDigitals(desiredNamedList,zone, vendorName, fetchedBranch, details, deviceId, isFlagged) {
    	$.ajax({
    		url: 'getVendorByName',
    		type: 'GET',
    		contentType: 'application/json',
    		data: {
    			vendorName: vendorName
    		},
    		success: function(response) {
    			alert("Enter in ss")
    			
    			alert("VENDOR NUMBER :: "+JSON.stringify(response));
    			//alert(JSON.stringify(response.payload));
    			var vendorNumber = response.payload.mobile;
    			alert(vendorNumber);
    			alert("VENDOR NUMBER :: "+vendorNumber);
    			alert(JSON.stringify(desiredNamedList));

    	counter++;
        var newRow = document.createElement('tr');
        if (isFlagged) {
            newRow.style.backgroundColor = 'yellow';
        }

        var serialNumberCell = document.createElement('td');
        serialNumberCell.textContent = counter;
        newRow.appendChild(serialNumberCell);
        
        var inputNameCell = document.createElement('td');
        inputNameCell.textContent = desiredNamedList;
        newRow.appendChild(inputNameCell);
        
        var vendorCell = document.createElement('td');
        vendorCell.textContent = vendorName;
        newRow.appendChild(vendorCell);

        var branchCell = document.createElement('td');
        branchCell.textContent = vendorNumber;
        newRow.appendChild(branchCell);

        var addressCell = document.createElement('td');
        addressCell.textContent = details.address1 + "," + details.address2 + "," + details.address3 + "," + details.address4;
        newRow.appendChild(addressCell);

        var mobileCell = document.createElement('td');
        mobileCell.textContent = details.mobile;
        newRow.appendChild(mobileCell);

        var deviceIDCell = document.createElement('td');
        deviceIDCell.textContent = deviceId;
        newRow.appendChild(deviceIDCell);

        var alarmedZoneCell = document.createElement('td');
        alarmedZoneCell.textContent = zone;
        newRow.appendChild(alarmedZoneCell);

        
        
        

        var remarkCell = document.createElement('td');
        var selectDropdown = document.createElement('select');
        selectDropdown.className = 'form-control';

		// If you want to prefill the input with a default remark, you can do:
		  var remarksListInput = document.getElementById('remarksList');
            if (remarksListInput && remarksListInput.value) {
                var remarks = remarksListInput.value.replace(/[\[\]"]/g, '').split(',');
                
                remarks.forEach(function(remark) {
                    var option = document.createElement('option');
                    option.value = remark.trim();
                    option.textContent = remark.trim();
                    selectDropdown.appendChild(option);
                });
            }


		remarkCell.appendChild(selectDropdown);
        newRow.appendChild(remarkCell);
        
       /*  var remarkInput = document.createElement('td');
		var inputField = document.createElement('input');
		inputField.type = 'text';
		inputField.className = 'form-control';
		inputField.placeholder = 'Enter remark';
        remarkInput.appendchild(inputField);
        newRow.appendChild(remarkInput); */
    
        
        var remarkInput = document.createElement('td');
        var remarkInputField = document.createElement('input');

        remarkInputField.type = 'text';
        remarkInputField.className = 'form-control';
        remarkInputField.style.width = '250px';
        remarkInputField.placeholder = 'Enter Remarks';

        remarkInput.appendChild(remarkInputField);
        newRow.appendChild(remarkInput);


        
        var actionCell = document.createElement('td');
        var submitButton = document.createElement('button');
        submitButton.type = 'button';
        submitButton.className = 'btn btn-primary submitButton';
        submitButton.textContent = 'Submit';
        actionCell.appendChild(submitButton);
        newRow.appendChild(actionCell);

        tableBodyDigitals.appendChild(newRow);
    		}
    	});
    }
}



$('#branchModal').on('show.bs.modal', function(event) {
    var button = $(event.relatedTarget);
    var branchDesc = button.data('desc');
    var branchCode = button.data('id');

    var modal = $(this);
    modal.find('#modalBranchDesc').text(branchDesc);
    modal.find('#modalBranchCode').text(branchCode);

    displayAlarmedZonesListScreenTwoDigitals(branchCode);
});

$('#branchModal').on('hidden.bs.modal', function() {
    var tableBody = document.getElementById('alarmZones').getElementsByTagName('tbody')[0];
    tableBody.innerHTML = '';
});



</script>

<script>
function filterBranches() {
    let input = document.getElementById("branchSearch").value.toLowerCase();
    let branches = document.querySelectorAll(".branch-box-container");

    branches.forEach(branch => {
        let branchName = branch.querySelector(".branch-name").textContent.toLowerCase();
        if (branchName.includes(input)) {
            branch.style.display = "block";
        } else {
            branch.style.display = "none";
        }
    });
}
</script>




	<script src="./js/dashboardTwo.js"></script>


	<!--**********************************
        Scripts
    ***********************************-->

<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

	
	<script src="./vendor/chart.js/chart.min.js"></script>
	
	
	<script src="./dashboardJS/dashboard/dashboard-1.js"></script>
	<script src="./vendor/global/global.min.js"></script>
	
</body>

</html>