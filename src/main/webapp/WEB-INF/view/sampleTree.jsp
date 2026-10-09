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

body {
    margin: 0;
    font-family: "Poppins", sans-serif;
    font-size: 0.875rem;
    font-weight: 400;
    line-height: 1.5;
    /* color: #BDBDC7; */
    text-align: left;
    overflow-x:hidden;
    background-color: rgba(88, 115, 254, 0.04);
}

    .branch-box {
     border: 1px solid #ccc;
    text-align: center;
  
    color:white;
    background:linear-gradient(222deg, rgba(251, 251, 251, 1) -12%, rgb(60 194 126) 45%);
    border-radius: 100% 0% 100% 98% / 2% 0% 100% 100%;
    margin: 5px;
    padding: 10px;
    box-shadow: 2px 0px 6px 0px rgb(60 194 126) !important;
    transition: transform 0.2s;
    width:150px;
    heighht:150px;
    }
    .l-bg-blue-dark {
    background: linear-gradient(to right, #373b44, #4286f4) !important;
    color: #fff;
}
    .branch-box .fa-university {
    position: absolute;
    top: 5px;
    right: 22px;
    font-size: 79px;
    color: #fff;
    opacity: 0.2;
}
    
    .branch-box:hover {
        transform: scale(1.05);
    }
    .modal-lg {
        max-width: 80%;
    }
    .modal-body {
        max-height: 60vh;
        overflow-y: auto;
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
    
</style>
</head>
<body>
<input id="loggedInBranch" value="${branchId}" style="display: none;">
<input id="userId" value="${loginUserId}" style="display:none;">
<input id="remarksList" value="${remarksListForBranchBoxes.payload}" style="display:none;">

<div class="m-2">
    <div class="card">
        <div class="card-body" style="padding:0;" >
         <div class="row">
        <div class="col-md-12">
            <h5 class="heading">Branch Live Status</h5>
        </div>
    </div>
            <div class="row">
              <c:forEach var="branch" items="${branchList.payload}">
    <div class="col-md-2">
        <div class="card branch-box" data-toggle="modal" data-target="#branchModal" data-id="${branch.branchCode}" data-desc="${branch.branchDesc}">
            <div class="card-body" style="padding:0; position:relative;" onclick="updateBranchCode('${branch.branchCode}');">
                <h6 class="card-title" style="color:white">${branch.branchDesc}</h6>
                <p class="card-text">ID: ${branch.branchCode}</p>
                <i class="fas fa-university"></i>
            </div>
        </div>
    </div>
</c:forEach>

            </div>
        </div>
    </div>
</div>


<div class="row mx-2">
    <div class="col-lg-12 col-md-12 col-sm-12 mt-0">
        <div class="card mx-0" id="branchDetailsCard" style="min-height: 422px;">
            <div class="row">
                <div class="col-12">
                    <h5 class="heading">Branch Details</h5>
                </div>
                <div class="col-md-1 mt-2">
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
                        <a class="dropdown-item changeTextofZones" data-value="">All Branches</a>
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
                        <a class="dropdown-item changeTextofZones" data-value="">All Status</a>
                        <a class="dropdown-item" data-value="Active">Active</a>
                        <a class="dropdown-item" data-value="InActive">InActive</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
            <div class="card-header" id="branchesDetailsTable">
              <div class="table-responsive mb-2" id="branchTableWrapper" style="max-height: 350px; overflow-y: auto;">
    <table class="table mb-0">
        <thead style="color: #fff; position: sticky; top: 0; z-index: 1; background: #028bd7;">
            <tr>
                <th style="display: none;">Branch Code</th>
                <th style="display:none;">Bank Name</th>
                <th>Serial No.</th>
                <th>Branch Name</th>
                <th>IFSC</th>
                <th>Contact No.</th>
                <th>Address</th>
                <th>Status</th>
            </tr>
        </thead>
        <tbody id="branchTableBody" class="" onclick="scrollToTable();">
            <c:choose>
                <c:when test="${empty childBranch.payload}">
                    <tr>
                        <td colspan="7" class="text-center" style="font-size: 15px; color: black;">No data present</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="childBranch"  varStatus="status" items="${childBranch.payload}">
                        <tr style="color: black; cursor: pointer" onclick="showDeviceHealth('${childBranch.branchCode}', this)">
                        <td>${status.index + 1}</td>
                            <td id="branchValue" value="${childBranch.branchCode}" style="display: none;">${childBranch.branchCode}</td>
                            <td id="branchValue" value="${childBranch.branchCode}"  style="display: none;">${childBranch.bank}</td>
                            <td id="branchValue" value="${childBranch.branchCode}"><i class="fa-solid fa-caret-right fa-lg mx-1"></i>${childBranch.branchDesc}</td>
                            <td id="branchValue" value="${childBranch.branchCode}">${childBranch.ifsc}</td>
                            <td style="text-align: right;" id="branchValue" value="${childBranch.branchCode}">${childBranch.mobile}</td>
                            <td id="branchValue" value="${childBranch.branchCode}">${childBranch.address1}</td>
                            <td id="branchValue" value="${childBranch.branchCode}">
                                <i class="fas fa-shield-alt fa-2x mr-2" style="color: ${childBranch.status == 'Active' ? '#2c832a' : '#C70039'};" title=""></i>${childBranch.status}
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
									<th>Branch Name</th>
									<th>Health Communicator</th>
									<th>Sync Time</th>
									<th>Main</th>
									<th>Solar</th>
									<th>Battery</th>
									<th>Signal</th>
									<th>Status</th>


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

								<th>Device Name</th>
								<th>Status</th>
							</tr>
						</thead>
						<tbody id="activeZonesBody"
							style="color: black; text-align: center; font-size: 14px;">

						</tbody>
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
								<table id="auditTable" class="table" style="color: #003d87;">
									<thead
										style="color: #fff; position: sticky; top: 0; z-index: 1; background: #028bd7;">
										<tr>
											<th>Service Provider</th>
											<th>Alarmed Zone</th>
											<th>Zone</th>
											<th style="text-align: right;">Alert Time Stamp</th>
											<th>Status</th>
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

				<div class="card-header" id="AlertDetailsTable">

					<div class="table-responsive mb-2"
						style="min-height: 250px; max-height: 350px; overflow-y: auto;">


						<table class="table table-striped table-bordered mb-0">
							<thead
								style="color: #fff; position: sticky; top: 0; z-index: 1; background: #028bd7;">
								<tr>
								
									<th style="display:none;">Bank Name</th>
									<th>Serial No.</th>
									<th class="text-left">IFSC</th>
									<th class="text-left">Service Provider</th>
									<th class="text-left">Branch Address</th>
									<th class="text-left">Zone Name</th>
									<th class="text-left">Alert Type</th>
									<th class="text-right">Time Stamp</th>
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
                <p><strong>Branch Name:</strong> <span id="modalBranchDesc"></span></p>
                <p><strong>Branch Code:</strong> <span id="modalBranchCode"></span></p>
                <table id="alarmZones" class="table table-striped table-bordered">
                    <thead style="background: #1c0f54; color: #ffff;">
                        <tr>
                            <th>Serial No.</th>
                            <th>Vendor Name</th>
                            <th>Branch Name</th>
                            <th>Address</th>
                            <th>Contact No.</th>
                            <th>Device ID</th>
                            <th>Active Zone</th>
                            <th>Remark</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
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
var clickedBranchCodeScreenTwo = '';
var branchDataScreenTwo = {};
var activeZoneScreenTwo = {};
function updateBranchCode(branchCode) {
	clickedBranchCodeScreenTwo = branchCode;
}

function fetchDataScreenTwo() {
    $.ajax({
        url: './fetchDataAndBroadcast',
        method: 'GET',
        success: function(response) {
            response.forEach(function(message) {
                processMessageScreenTwo(message);
            });
        },
        error: function(error) {
            console.error('Error fetching data:', error);
        }
    });
}

$(document).ready(function() {
	fetchDataScreenTwo();
    setInterval(fetchDataScreenTwo, 2000);
});

function processMessageScreenTwo(message) {
    var dataSplit = message.split(",");
    var branchCodeFromMessage = dataSplit[0];
    var deviceId = dataSplit[1];
    activeZoneScreenTwo = dataSplit[8];

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

                    branchDataScreenTwo[branchCodeFromMessage].alerts.push({
                        deviceId: deviceId,
                        zones: evenIndicesArray
                    });

                    updateBranchBoxColor(branchCodeFromMessage, "#FCAEAE", "#C70039");

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


function updateBranchBoxColor(branchCode, backgroundColor, textColor) {
	if(activeZoneScreenTwo.length > 0){
		$('.branch-box').each(function() {
	        var element = $(this);
	        var dataId = element.data('id');
	        if (dataId === branchCode) {
	            element.css({
	                'background': backgroundColor,
	                'color': textColor
	            });
	        }
	    });
	}else{
		
		$('.branch-box').each(function() {
            var element = $(this);
            var dataId = element.data('id');
            if (dataId === branchCode) {
                element.css({
                    'background': "#E0FBE2",
                    'color': "#000000"
                });
            }
        });
    
		
	}
	
    
}



function displayAlarmedZonesListScreenTwo(branchCode) {
    var branchInfo = branchDataScreenTwo[branchCode];
    if (!branchInfo) return;

    var tableBody = document.getElementById('alarmZones').getElementsByTagName('tbody')[0];
    tableBody.innerHTML = '';

    if (branchInfo.alerts.length === 0) {
        var noDataRow = document.createElement('tr');
        var noDataCell = document.createElement('td');
        noDataCell.colSpan = 9;
        noDataCell.textContent = 'No data found';
        noDataRow.appendChild(noDataCell);
        tableBody.appendChild(noDataRow);
        return;
    }

    if(activeZoneScreenTwo.length > 0){
    	var counter = 0;
        branchInfo.alerts.forEach(function(alert) {
            alert.zones.forEach(function(zone) {
                counter++;
                var newRow = document.createElement('tr');

                var serialNumberCell = document.createElement('td');
                serialNumberCell.textContent = counter;
                newRow.appendChild(serialNumberCell);

                var vendorCell = document.createElement('td');
                vendorCell.textContent = branchInfo.vendorName;
                newRow.appendChild(vendorCell);

                var branchCell = document.createElement('td');
                branchCell.textContent = branchInfo.fetchedBranch;
                newRow.appendChild(branchCell);

                var addressCell = document.createElement('td');
                addressCell.textContent = branchInfo.details.address1 + "," + branchInfo.details.address2 + "," + branchInfo.details.address3 + "," + branchInfo.details.address4;
                newRow.appendChild(addressCell);

                var mobileCell = document.createElement('td');
                mobileCell.textContent = branchInfo.details.mobile;
                newRow.appendChild(mobileCell);

                var deviceIDCell = document.createElement('td');
                deviceIDCell.textContent = alert.deviceId;
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
            });
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
        var address = tableRow.children[3].textContent;
        var mobile = tableRow.children[4].textContent;
        var deviceId = tableRow.children[5].textContent;
        var alarmedZone = tableRow.children[6].textContent;

        var remarkDropdown = tableRow.querySelector('select.form-control');
        var selectedRemark = remarkDropdown.options[remarkDropdown.selectedIndex].textContent;

        var userId = $('#userId').val();

        var auditData = {
            deviceId: deviceId,
            activeZone: alarmedZone,
            remarks: selectedRemark,
            user: userId,
            branchCode: branchName,
            mobile: mobile,
            address: address,
            vendorName: vendorName
        };

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

              
                updateBranchBoxColor(branchName, "linear-gradient(222deg, rgba(251, 251, 251, 1) -12%, rgb(60, 194, 126) 45%)", "#FFFFFF");
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
    }
});
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