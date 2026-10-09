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
<script
	src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDMKndzFcfiysofJH9zTVJtOCeIH70nKcc"></script>

<style>
iframe {
	border: 0;
	width: 100%;
	height: 400px;
}

.zone-table {
	max-height: 150px;
	over-flow-y: auto;
}

.ticket {
	justify-content: center;
	align-items: center;
	background: #ffffff;
	border: 2px dashed #028bd7;
	border-radius: 10px;
	width: 400px;
	padding: 20px;
	box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.ticket-header {
	text-align: center;
	margin-bottom: 20px;
}

.ticket-header h1 {
	font-size: 1.5em;
	color: #333;
	margin: 0;
}

.ticket-header p {
	font-size: 0.9em;
	color: #777;
	margin: 5px 0 0;
}

.ticket-body {
	margin-top: 10px;
}

.ticket-body ul {
	list-style: none;
	padding: 0;
}

.ticket-body ul li {
	padding: 8px 0;
	border-bottom: 1px solid #ddd;
	font-size: 1em;
}

.ticket-body ul li span {
	font-weight: bold;
	color: #333;
}

.ticket-footer {
	text-align: center;
	margin-top: 20px;
}

.ticket-footer a {
	display: inline-block;
	text-decoration: none;
	background: #4caf50;
	color: #fff;
	padding: 10px 20px;
	border-radius: 5px;
	font-size: 1em;
}

.ticket-footer a:hover {
	background: #45a049;
}

.card-bodyPie {
	display: flex;
	justify-content: space-around;
	align-items: center;
	padding: 10px;
}

.chart-container {
	flex: 1;
	padding: 5px;
}

canvas {
	max-width: 100%;
	max-height: 100%;
}

.chart-title {
	margin-top: 5px;
	text-align: center;
	font-size: 14px;
	color: black;
}

.legend-container {
	display: flex;
	margin-botton: 5px;
}

.legend-item {
	display: flex;
	align-items: center;
	margin: 0 5px;
}

.legend-color {
	width: 15px;
	height: 15px;
	margin-right: 5px;
	border-radius: 3px;
}

.lColor {
	color: black;
}

#activeZonesBodyOne tr:first-child, #activeZonesBodyTwo tr:first-child {
	display: none;
}

.tooltip-working .tooltip-inner {
	background-color: #118B50;
	color: white;
}

.tooltip-partially .tooltip-inner {
	background-color: #FCC737;
	color: black;
}

.tooltip-not-working .tooltip-inner {
	background-color: crimson;
	color: white;
}

.form-control-sm {
    font-size: 0.875rem;
}

select.form-control-sm,
input.form-control-sm {
    background-color: #f8f9fa;
    border: 1px solid #ced4da;
    color: #495057;
    padding: 0.375rem 0.75rem;
    border-radius: 0.25rem;
    transition: border-color 0.2s, box-shadow 0.2s;
}

select.form-control-sm:focus,
input.form-control-sm:focus {
    border-color: #80bdff;
    box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}

.input-datalist {
    background-color: #f8f9fa;
}

input::placeholder {
    font-size: 0.8rem;
    color: #6c757d;
}


.row .col-auto {
	margin-bottom: 0.5rem;
}

#filterButton, #downloadExcel {
	text-align: center;
}

@media ( max-width : 768px) {
	.button-legend-wrapper {
		flex-direction: column; /* Stack legends and button vertically */
		align-items: flex-start; /* Align items to the left */
		gap: 10px;
	}
	.legend-container {
		flex-direction: row; /* Align legends horizontally */
		flex-wrap: wrap;
	}
}
</style>

</head>

<body style="font-family: Poppins;">


	<input id="userId" value="${userId}" style="display: none;">
	<input id="loggedInBranch" value="${branchId}" style="display: none;">
	<input id="remarksList" value="${remarksList.payload}"
		style="display: none;">
	<div class="container-fluid " id="accordion">
		<div class="row d-flex align-items-stretch">

			<div class="col-12">
				<div class="card mb-3 flex-grow-1" style="background-color: #ffff;">
					<div class="row">
						<div class="col-md-12">
							<h5 class="heading">Status of Sites</h5>
						</div>
					</div>
					<div class="card-body py-2">
						<div class="row mx-2">
							<!-- Total Devices -->
							<!-- Total Sites -->
							<div class="col-lg-4 col-sm-12">
								<div class="card text-white bg-white mb-2 position-relative"
									style="width: 100%; overflow: hidden; border: 3px solid royalblue; border-right: none; border-bottom: none;">
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
							<div class="col-lg-4 col-sm-12">
								<div class="card text-white bg-white mb-2 position-relative"
									style="width: 100%; overflow: hidden; border: 3px solid green; border-right: none; border-bottom: none;">
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
							<div class="col-lg-4 col-sm-12">
								<div class="card text-white bg-white mb-2 position-relative"
									style="width: 100%; overflow: hidden; border: 3px solid crimson; border-right: none; border-bottom: none;">
									<!-- Card Header -->
									<h4 class="card-header" style="color: crimson;">Not
										Working</h4>

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


						</div>
					</div>
				</div>
			</div>


			<div class="col-12">
				<div class="card flex-grow-1">
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
						<div class="row row-cols-5">
							<div class="col" id="cctv-chart-container">
								<canvas id="cctvChart" onclick="toggleCollapseAndScroll();"></canvas>
							</div>
							<div class="col" id="security-alarm-chart-container">
								<canvas id="securityAlarmChart"
									onclick="toggleCollapseAndScroll();"></canvas>
							</div>
							<div class="col" id="fire-alarm-chart-container">
								<canvas id="fireAlarmChart" onclick="toggleCollapseAndScroll();"></canvas>
							</div>
							<div class="col" id="bacs-chart-container">
								<canvas id="bacsChart" onclick="toggleCollapseAndScroll();"></canvas>
							</div>
							<div class="col" id="etl-chart-container">
								<canvas id="etlChart" onclick="toggleCollapseAndScroll();"></canvas>
							</div>
						</div>

					</div>
				</div>
			</div>

		</div>
	</div>




	<div class="row mx-2 d-none">
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
				<div class="card-header " id="branchesDetailsTable" >
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





	<div class="col-lg-12 col-md-12">



		<div class="card">

			<div>
				<div class="row">
					<div class="col-md-12 col-sm-12">
						<h5 class="heading deviceReports"
							onclick="fetchZonesForReportTable();">Device Reports</h5>

					</div>

				</div>



				<div class="card-body" style="font-size: 12px;">


					<div class="card-body" style="font-size: 12px;">
						<div class="button-legend-wrapper"
							style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
							<!-- Legend -->
							<div class="legend-container"
								style="display: flex; align-items: center; gap: 10px;">
								<div class="legend-item"
									style="display: flex; align-items: center; gap: 5px; cursor: pointer;"
									data-bs-toggle="tooltip" data-bs-placement="top"
									title="All zones are fully operational and functioning as expected."
									data-bs-custom-class="tooltip-working">
									<div class="legend-color"
										style="width: 15px; height: 15px; background-color: #118B50; border-radius: 3px;"></div>
									<span style="font-size: 14px; color: black;">Working</span>
								</div>
								<div class="legend-item"
									style="display: flex; align-items: center; gap: 5px; cursor: pointer;"
									data-bs-toggle="tooltip" data-bs-placement="top"
									title="The primary zone is functioning, but at least one other zone is not working."
									data-bs-custom-class="tooltip-partially">
									<div class="legend-color"
										style="width: 15px; height: 15px; background-color: #FCC737; border-radius: 3px;"></div>
									<span style="font-size: 14px; color: black;">Partially
										Working</span>
								</div>
								<div class="legend-item"
									style="display: flex; align-items: center; gap: 5px; cursor: pointer;"
									data-bs-toggle="tooltip" data-bs-placement="top"
									title="The primary zone has an issue or is malfunctioning, but the other zones are unaffected."
									data-bs-custom-class="tooltip-not-working">
									<div class="legend-color"
										style="width: 15px; height: 15px; background-color: crimson; border-radius: 3px;"></div>
									<span style="font-size: 14px; color: black;">Not Working</span>
								</div>
							</div>

							<!-- Button -->
							<button id="viewStatusButton" class="btn btn-sm btn-primary"
								data-toggle="collapse" data-target="#collapseTwo"
								aria-expanded="false" aria-controls="collapseTwo"
								onclick="fetchZonesForReportTable();updateHeading('', '')">View
								All</button>
						</div>



						<div class="table-responsive mb-2"
							style="min-height: 300px; max-height: 550px; overflow-y: auto;">
							<table class="table table-bordered mb-0" id="zoneReportTable">
								<thead
									style="color: #fff; position: sticky; top: 0; z-index: 1; background: #028bd7;">
									<tr>
										<th style="display: none">Branch Code</th>
										<th style="text-align: right">S.No</th>
										<th class="text-left">Branch Name</th>
										<th>Bank Name</th>
										<th style="text-align: right">Contact No.</th>
										<th>View Zones & Health Details</th>
										<th>Manual Sync</th>
									</tr>
								</thead>
								<tbody style="color: black; cursor: pointer;"
									id="reportTableBody">
									<!-- Rows will be populated dynamically -->
								</tbody>
							</table>
						</div>
					</div>

				</div>




			</div>



		</div>
	</div>











	<div class="col-lg-12 col-md-12" id="statusTable"
		style="display: none;">
		<div class="">
			<div class="row" id="inputFilter" onchange="filterInputs(this.value)">

			</div>

		</div>
	</div>




	<!--Alert Reports  -->
	<div class="row mx-2">
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
						data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"
						style="display: none;">Select Year</button>
					<div class="dropdown-menu yearFilter"
						style="cursor: pointer; color: #1d1d1d;"></div>
				</div>
			</div>



		</div>
		<div class="col-lg-12 col-md-12 col-sm-12 mt-0">
			<div class="card mx-0" style="font-size: 12px;">

				<div class="row">
					<div class="col-12">
						<h5 class="heading">Alert Reports</h5>
					</div>
				</div>
				<div class="row mt-3 mx-2 align-items-center">

					<!-- Start Date -->


					<!-- Device Type -->
					<div class="col-md-2 col-sm-6 mb-2">
						<select class="form-control form-control-sm rounded"
							id="filterAlertType" onchange="filterTableByAlertType()">
							<option value="All">All Alert Types</option>
							<c:forEach var="alertType" items="${allAlertList.payload}">
								<option value="${alertType}">${alertType}</option>
							</c:forEach>
						</select>
					</div>

					<!-- Vendor Name -->
					<div class="col-md-2 col-sm-6 mb-2">
						<select class="form-control form-control-sm rounded"
							id="filterVendorName" onchange="filterTableByVendorName()">
							<option value="All">All Vendors</option>
							<c:forEach var="vendor" items="${vendorList.payload}">
								<option value="${vendor}">${vendor}</option>
							</c:forEach>
						</select>
					</div>

					<!-- Device Type -->
					<div class="col-md-2 col-sm-6 mb-2">
						<select class="form-control form-control-sm rounded"
							id="filterDeviceType" onchange="filterTableByDeviceName()">
							<option value="All">Device Type</option>
							<option value="CCTV">CCTV</option>
							<option value="SECURITY ALARM">Security Alarm</option>
							<option value="FIRE ALARM">Fire Alarm</option>
							<option value="BACS">BACS</option>
							<option value="ETL">ETL</option>
						</select>
					</div>

					<!-- Time Filter -->
					<div class="col-md-2 col-sm-6 mb-2">
						<select class="form-control form-control-sm rounded"
							id="filterOfTime" onchange="filterTableByResolutionTime()">
							<option value="All">Select Duration</option>
							<option value="24">24 hours</option>
							<option value="48">48 hours</option>
							<option value="72">72 hours</option>
							<option value="more">More than 72 hours</option>
						</select>
					</div>

					<!-- No of Logs -->
					<div class="col-md-2 col-sm-6 mb-2">
						<select class="form-control form-control-sm rounded"
							id="filterNoOfLogs" onchange="filterTableByNoOfLogs()">
							<option value="All">No. of Logs</option>
							<option value="100">100</option>
							<option value="500">500</option>
							<option value="1000">1000</option>
						</select>
					</div>

					<!-- Ticket ID -->
					
					
					<div class="col-md-2 col-sm-6 mb-">
						<select class="form-control form-control-sm rounded"
							id="filterBranch" style="height: 32px;"
							onchange="filterTableByBranch()">
							<option value="All">Select Branch</option>
							<c:forEach var="branch" items="${branchListForFilter.payload}">
								<option value="${branch.branchDesc}">${branch.branchDesc}</option>
							</c:forEach>

						</select>
					</div>
					
					
				<div class="col-md-2 col-sm-6 mb-2">
    <input list="ticketIdList"
           class="form-control form-control-sm rounded input-datalist" 
           id="filterTicketId"
           placeholder="Select or type Ticket ID" 
           style="height: 32px;"
           oninput="filterTableByTicketId()" />
    <datalist id="ticketIdList">
        <c:forEach var="ticket" items="${ticketIdList}">
            <option value="${ticket.ticketId}"></option>
        </c:forEach>
    </datalist>
</div>

					
					<div class="col-md-2 col-sm-6 mb-2">
						<input class="form-control form-control-sm rounded" type="text"
							id="startDateForAlertReport" placeholder="Start Date"
							onfocus="(this.type='date')">
					</div>

					<!-- End Date -->
					<div class="col-md-2 col-sm-6 mb-2">
						<input class="form-control form-control-sm rounded" type="text"
							id="endDate" placeholder="End Date" onfocus="(this.type='date')">
					</div>

					<!-- Filter Button -->
					<div class="col-md-3 col-sm-6 mb-2 d-flex">
						<button class="btn btn-primary ticketButton mr-2"
							id="filterButton">
							<i class="fa fa-search"></i>
						</button>

						<button id="downloadExcel" class="btn btn-sm btn-primary"
							data-bs-toggle="tooltip" data-bs-placement="top"
							title="Download Alert Reports">
							<i class="fa-solid fa-file-csv fa-lg text-center"></i>
						</button>
					</div>





				</div>


				<div class="card-header" id="AlertDetailsTable">



					<div class="table-responsive mb-2"
						style="min-height: 250px; max-height: 350px; overflow-y: auto;">


						<table class="table table-striped table-bordered mb-0"
							id="alertTableExcel">
							<thead
								style="color: #fff; position: sticky; top: 0; z-index: 1; background: #028bd7;">
								<tr>

									<th class="col-1 text-left">Branch</th>
									<th class="col-1 text-right">Contact</th>
									<th class="col-1 text-left">Vendor</th>
									<th class="col-3 text-left">Alarmed Zone</th>
									<th class="col-2 text-left">Alert Type</th>
									<th class="col-2 text-right">Alert Time Stamp</th>
									<th class="col-2 text-right">Resolved Times Stamp</th>
									<th class="col-2 text-right">Downtime</th>
									<th class="text-left">Ticket</th>
									<th class="text-left">View</th>

								</tr>
							</thead>
							<tbody id="logsTableBody">

								<%-- <c:forEach var="input" items="${inputDetailsList.payload}">
									<tr style="color: black;">
										<td class="text-left branch-name">${input.branchName}</td>

										<td class="text-right">${input.mobileNo}</td>
										<td class="vendor-name text-left">${input.vendorName}</td>
										<td class="device-type text-left">${input.alarmtype}</td>
										<td class="alert-type text-left">${input.alertType}</td>
										<td class="alarm-time text-right"
											data-alarm-time="${input.alarmTime}" style="color: black;">
											${input.alarmTime}</td>

										<td class="issue-resolved-time text-right"
											data-issue-resolved-time="${input.issueResolvedTime}"
											style="color: black;"></td>
										<td class="time-difference text-right">${input.timeDifference}</td>

										<td class="ticket-id text-left">${input.ticketId}</td>
										<td><button class="btn btn-sm btn-primary"
												onclick="getComplaintsByPanelSerialNumberAndDateTime('${input.deviceSerialNo}', '${input.alarmTime}');">
												Ticket</button></td>
									</tr>
								</c:forEach> --%>
							</tbody>
						</table>

					</div>



				</div>

				<div class="pagination" id="paginationContainer"></div>
			</div>

		</div>

		<iframe src="http://192.168.1.27:8080/pendingComplaint"
			name="adminRightHMS" id="adminRightHMS" frameborder="0"></iframe>



	</div>








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

	<!--**********************************
           Support ticket button start
        ***********************************-->

	<!--**********************************
           Support ticket button end
        ***********************************-->
	<!--**********************************
        Main wrapper end
    ***********************************-->

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
							data-dismiss="modal" aria-label="Close"
							onclick="stopSoundAlert();">
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
															<th>Input Desired Name</th>
															<th>Vendor Name</th>
															<th>Vendor Number</th>
															<th>Branch Address</th>
															<th>Contact No.</th>
															<th>Device ID</th>
															<th>Alert Zone</th>
															<th>Resolution Status</th> 
															<th>Remark</th>
															<th>Action</th>
															
														</tr>
													</thead>
													<tbody>
													</tbody>
												</table>

												<audio id="audio-alert" src="./audio/emergency_alert.mp3"
													preload="auto"></audio>

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







	<!-- Modal Structure -->
	<!-- Modal Structure -->
	<div class="modal fade" id="complaintsModal" tabindex="-1"
		aria-labelledby="complaintsModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="complaintsModalLabel">Complaints
						Details</h5>
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal" aria-label="Close">Close</button>
				</div>
				<div class="modal-body d-flex justify-content-center">
					<div class="ticket">
						<div class="ticket-header">
							<h1>Alert Ticket</h1>
							<p>Details for your generated ticket</p>
						</div>
						<div class="ticket-body">
							<ul>
								<li><span>Ticket ID:</span>&nbsp; <span id="ticketId"
									class="text-primary"></span></li>
								<li><span>Assigned To:</span> &nbsp;<span id="assignedTo"
									class="text-primary"></span></li>
								<li><span>Vendor:</span> &nbsp;<span id="vendorName"
									class="text-primary"></span></li>
								<li><span>Remarks:</span> &nbsp;<span id="remarks"
									class="text-primary"></span></li>
								<li><span>Status:</span> &nbsp;<span id="status"
									class="text-primary"></span></li>
								<li><span>DateTime:</span> &nbsp;<span id="dateTime"
									class="text-primary"></span></li>
								<li><span>Complete SMS:</span>&nbsp; <span
									id="completeSMS" class="text-primary"></span></li>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>


	<script src="./js/xlxs.full.min.js"></script>

	<script>
	document.getElementById('downloadExcel').addEventListener('click', function () {
	   
	    const table = document.getElementById('alertTableExcel');

	    // Convert the table to a worksheet
	    const worksheet = XLSX.utils.table_to_sheet(table);

	    // Style the headers
	    const range = XLSX.utils.decode_range(worksheet['!ref']);
	    for (let col = range.s.c; col <= range.e.c; col++) { // Loop through all columns in the header row
	        const cellAddress = XLSX.utils.encode_cell({ r: 0, c: col }); // Header row is the first row (index 0)
	        const cell = worksheet[cellAddress];
	        if (cell) {
	           
	            cell.s = {
	                font: { bold: true }, 
	                fill: { fgColor: { rgb: "028BD7" } }, // Blue background
	                alignment: { horizontal: "center" } // Center alignment
	            };
	        }
	    }

	    // Fix mobile numbers (assume column "Contact No." is the 3rd column, index 2)
	    for (let row = range.s.r + 1; row <= range.e.r; row++) { // Start from row 1 (skip headers)
	        const cellAddress = XLSX.utils.encode_cell({ r: row, c: 2 }); // Column index for "Contact No."
	        const cell = worksheet[cellAddress];
	        if (cell && typeof cell.v === 'number') {
	            // Convert the number to a string to avoid scientific notation
	            cell.v = cell.v.toString();
	            cell.t = 's'; // Set cell type to string
	        }
	    }

	    // Create a workbook and add the updated worksheet
	    const workbook = XLSX.utils.book_new();
	    XLSX.utils.book_append_sheet(workbook, worksheet, 'AlertDetails');

	    // Export the workbook to an Excel file
	    XLSX.writeFile(workbook, 'AlertDetailsTable.xlsx');
	});



	
	document.addEventListener('DOMContentLoaded', function () {
	    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
	    tooltipTriggerList.forEach(function (tooltipTriggerEl) {
	      new bootstrap.Tooltip(tooltipTriggerEl);
	    });
	  });
	
	
	
				 $(document).ready(function () {
		    fetchZonesForReportTable(); 
		});
				 
				
				
				
				
				/*****************************JS to show alert time stamps in alert resolving time report *******************************/


				document.addEventListener('DOMContentLoaded', function () {
		    const formatDateTimeWithSeconds = (dateTimeString) => {
		        const date = new Date(dateTimeString);
		        return date.toLocaleString('en-US', {
		            year: 'numeric',
		            month: '2-digit',
		            day: '2-digit',
		            hour: '2-digit',
		            minute: '2-digit',
		            second: '2-digit',
		            hour12: false
		        });
		    };

		    // Populate table cells with formatted date values
		    const alarmTimeCells = document.querySelectorAll('.alarm-time');
		    alarmTimeCells.forEach(cell => {
		        const alarmTime = cell.getAttribute('data-alarm-time');
		        if (alarmTime) {
		            cell.textContent = formatDateTimeWithSeconds(alarmTime);
		        }
		    });

		    const issueResolvedTimeCells = document.querySelectorAll('.issue-resolved-time');
		    issueResolvedTimeCells.forEach(cell => {
		        const issueResolvedTime = cell.getAttribute('data-issue-resolved-time');
		        if (issueResolvedTime) {
		            cell.textContent = formatDateTimeWithSeconds(issueResolvedTime);
		        }
		    });

		    // Filter functionality
		    const filterButton = document.getElementById('filterButton');
		    filterButton.addEventListener('click', function () {
		        const startDateInput = document.getElementById('startDateForAlertReport').value;
		        const endDateInput = document.getElementById('endDate').value;

		        if (!startDateInput || !endDateInput) {
		            alert('Please select both start and end dates.');
		            return;
		        }

		        const startDate = new Date(startDateInput).setHours(0, 0, 0, 0);
		        const endDate = new Date(endDateInput).setHours(23, 59, 59, 999);

		        const tableRows = document.querySelectorAll('#AlertDetailsTable tbody tr');
		        tableRows.forEach(row => {
		            const alarmTimeCell = row.querySelector('.alarm-time');
		            const alarmTimeString = alarmTimeCell.getAttribute('data-alarm-time');
		            
		            if (!alarmTimeString) {
		                row.style.display = 'none';
		                return;
		            }

		            const alarmTime = new Date(alarmTimeString).getTime();

		            // Check if alarmTime is within the range
		            if (alarmTime > startDate && alarmTime < endDate) {
		                row.style.display = ''; // Show row
		            } else {
		                row.style.display = 'none'; // Hide row
		            }
		        });
		    });
		});

				 function filterTableByAlertType() {
				        const filter = document.getElementById("filterAlertType").value;
				        const table = document.getElementById("AlertDetailsTable");
				        const rows = table.getElementsByTagName("tbody")[0].getElementsByTagName("tr");

				        for (const row of rows) {
				            const alertTypeCell = row.querySelector(".alert-type");
				            if (filter === "All" || (alertTypeCell && alertTypeCell.textContent.trim() === filter)) {
				                row.style.display = ""; // Show row
				            } else {
				                row.style.display = "none"; // Hide row
				            }
				        }
				    }
				 
				 function filterTableByVendorName() {
				        const filter = document.getElementById("filterVendorName").value;
				        const table = document.getElementById("AlertDetailsTable");
				        const rows = table.getElementsByTagName("tbody")[0].getElementsByTagName("tr");

				        for (const row of rows) {
				            const vendorNameCell = row.querySelector(".vendor-name");
				            if (filter === "All" || (vendorNameCell && vendorNameCell.textContent.trim() === filter)) {
				                row.style.display = ""; 
				            } else {
				                row.style.display = "none";
				            }
				        }
				    }
				 
				 function filterTableByDeviceName() {
				        const filter = document.getElementById("filterDeviceType").value;
				        const table = document.getElementById("AlertDetailsTable");
				        const rows = table.getElementsByTagName("tbody")[0].getElementsByTagName("tr");

				        for (const row of rows) {
				            const deviceTypeCell = row.querySelector(".device-type");
				            if (filter === "All" || (deviceTypeCell && deviceTypeCell.textContent.trim() === filter)) {
				                row.style.display = ""; // Show row
				            } else {
				                row.style.display = "none"; // Hide row
				            }
				        }
				    }
				 
				 function filterTableByTicketId(){
				        const filter = document.getElementById("filterTicketId").value;
				        const table = document.getElementById("AlertDetailsTable");
				        const rows = table.getElementsByTagName("tbody")[0].getElementsByTagName("tr");

				        for (const row of rows) {
				            const ticketIdCell = row.querySelector(".ticket-id");
				            if (filter === "All" || (ticketIdCell && ticketIdCell.textContent.trim() === filter)) {
				                row.style.display = ""; // Show row
				            } else {
				                row.style.display = "none"; // Hide row
				            }
				        }
				    }
				 
				 function filterTableByBranch(){
				        const filter = document.getElementById("filterBranch").value;
				        const table = document.getElementById("AlertDetailsTable");
				        const rows = table.getElementsByTagName("tbody")[0].getElementsByTagName("tr");

				        for (const row of rows) {
				            const branchNameCell = row.querySelector(".branch-name");
				            if (filter === "All" || (branchNameCell && branchNameCell.textContent.trim() === filter)) {
				                row.style.display = ""; // Show row
				            } else {
				                row.style.display = "none"; // Hide row
				            }
				        }
				    }
				 
				 
				 
				 function getComplaintsByPanelSerialNumberAndDateTime(serialNumber, dateTime) {
					    serialNumber = "SN1234567890";
					    dateTime = "2025-01-02T12:30:00Z";
					   // alert(serialNumber + "--->>" + dateTime);

					    $.ajax({
					        url: "./api/tickets/getComplaintsByPanelSerialNumberAndDateTime", 
					        type: 'GET', 
					        data: {
					            panelSerialNumber: serialNumber,
					            dateTime: dateTime
					        },
					        dataType: 'json',
					        success: function(response) {
					            alert(JSON.stringify(response.payload)); 

					           
					            let complaint = response.payload[0];

					            // Fill modal with the fetched data
					            $('#ticketId').text(complaint.ticketId);
					            $('#bankName').text(complaint.bankName);
					            $('#branchName').text(complaint.branchName);
					            $('#vendorName').text(complaint.vendorName);
					            $('#remarks').text(complaint.remarks);
					            $('#status').text(complaint.status);
					            $('#assignedTo').text(complaint.assignedTo)
					            $('#dateTime').text(complaint.dateTime);
					            $('#completeSMS').text(complaint.completeSMS);

					            // Show the modal
					            $('#complaintsModal').modal('show');
					        },
					        error: function(xhr, status, error) {
					            console.error('Error fetching complaints:', status, error);
					        }
					    });
					}


	
		
	</script>

	<script>
    
     function filterTableByResolutionTime() {
    	    const filterValue = document.getElementById("filterOfTime").value;
    	    const rows = document.querySelectorAll("#logsTableBody tr");

    	    rows.forEach(row => {
    	        const timeDifferenceCell = row.querySelector(".time-difference");
    	        const timeText = timeDifferenceCell ? timeDifferenceCell.textContent.trim() : "";

    	        // Parse time difference into total hours
    	        let totalHours = 0;

    	        // Extract days, hours, and minutes using regex
    	        const daysMatch = timeText.match(/(\d+)\s*days/);
    	        const hoursMatch = timeText.match(/(\d+)\s*hours/);
    	        const minutesMatch = timeText.match(/(\d+)\s*minutes/);

    	        if (daysMatch) totalHours += parseInt(daysMatch[1]) * 24; // Convert days to hours
    	        if (hoursMatch) totalHours += parseInt(hoursMatch[1]);
    	        if (minutesMatch) totalHours += parseInt(minutesMatch[1]) / 60; // Convert minutes to hours

    	        let shouldShow = false;

    	        if (filterValue === "All") {
    	            shouldShow = true; // Show all rows for "All"
    	        } else if (filterValue === "24" && totalHours <= 24) {
    	            shouldShow = true;
    	        } else if (filterValue === "48" && totalHours > 24 && totalHours <= 48) {
    	            shouldShow = true;
    	        } else if (filterValue === "72" && totalHours > 48 && totalHours <= 72) {
    	            shouldShow = true;
    	        } else if (filterValue === "more" && totalHours > 72) {
    	            shouldShow = true;
    	        }

    	        row.style.display = shouldShow ? "" : "none";
    	    });
    	}

    
     function filterTableByNoOfLogs() {
 	    const filterValue = document.getElementById("filterNoOfLogs").value;
 	    const tableBody = document.getElementById("logsTableBody");
 	    const rows = Array.from(tableBody.getElementsByTagName("tr"));

 	    // Sort rows by alert time (latest first)
 	    rows.sort((a, b) => {
 	        const alertTimeA = new Date(a.querySelector(".alarm-time").dataset.alarmTime);
 	        const alertTimeB = new Date(b.querySelector(".alarm-time").dataset.alarmTime);
 	        return alertTimeB - alertTimeA; // Descending order
 	    });

 	    // Filter the rows based on the selected value
 	    const numberOfLogs = filterValue === "All" ? rows.length : parseInt(filterValue);
 	    rows.forEach((row, index) => {
 	        row.style.display = index < numberOfLogs ? "" : "none";
 	    });
 	}
    
</script>


	<script src="./js/hmsDashboard3.js"></script>



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