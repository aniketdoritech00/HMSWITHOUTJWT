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
<title>All  Acknowledged Reports</title>

<link rel="stylesheet" href="./css/deviceLogDetails.css" />
<link rel="stylesheet" href="./css/multilingual.css" />
<link rel="stylesheet" href="./externalCSS/bootstrap.min.css" />
<link href="./fonts/poppins.css" rel="stylesheet">
<script src="./externalJS/fontawesome.js"></script>

<!-- DATATABLES CSS Links -->
<link rel="stylesheet" href="./externalCSS/dataTables.bootstrap4.min.css">
<link rel="stylesheet" href="./externalCSS/buttons.dataTables.min.css">
<link rel="stylesheet" href="./externalCSS/responsive.bootstrap4.min.css">

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




<style>
.testing {
	background: rgba(87, 192, 255);
	color: #fff;
	border-radius: 5px;
	padding: 3px;
}


.falseAlarm {
	background: rgba(25, 192, 115);
	color: #fff;
	border-radius: 5px;
	padding: 3px;
}

.acknowledged {
	background: rgba(227, 50, 53);
	color: #fff;
	border-radius: 5px;
	padding: 3px;
}

.informedVendor {
	background: rgba(210, 235, 66);
	color: #fff;
	border-radius: 5px;
	padding: 3px;
}
</style>
</head>
<body style="font-size: 12px; overflow-y: hidden; font-family: Poppins;">
	<div class="row mx-1">
		<div class="col-md-12">
			<div class="row">
				<div class="col-md-12">
					<h5 class="heading">All  Acknowledged Reports</h5>
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<div class="m-2">
				<div class="card">
					<div class="input-group rounded mt-2 col-md-6">
						<input class="form-control form-control-sm rounded typeahead mx-1"
							type="text" id="startDate" placeholder="Start Date"
							style="height: 32px;" onfocus="(this.type='date')"> <input
							class="form-control form-control-sm rounded typeahead mx-1"
							type="text" id="endDate" placeholder="End Date"
							style="height: 32px;" onfocus="(this.type='date')">
						<button class="input-group-text border-0 ml-2" id="filterButton"
							style="background-color: #028bd7; color: #ffff; height: 32px;">
							<i class="fa fa-search" aria-hidden="true"></i>
						</button>
						<button class="input-group-text border-0 ml-2"
							style="background-color: #028bd7; color: #ffff; height: 32px;"
							id="clearButton">
							<i class="fa-solid fa-broom fa-flip"></i>
						</button>
					</div>
					<table id="example" class="table table-striped table-bordered">
						<thead>
							<tr>
								<th>Branch Name</th>
								<th>Vendor Name</th>
								<th>Contact No.</th>
								<th>Branch Address</th>
								<th>Active Zones</th>
								<th>Date Time</th>
								<th>User Name</th>
								<th>Remarks</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="Acknowledged" items="${allAcknowledgedList.payload}">
								<tr>
									<td>${Acknowledged.branchCode}</td>
									<td>${Acknowledged.vendorName}</td>
									<td>${Acknowledged.mobile}</td>
									<td>${Acknowledged.address}</td>
									<td>${Acknowledged.activeZone}</td>
									<td>${Acknowledged.dateTime}</td>
									<td>${Acknowledged.user}</td>
									<td class="remark-cell">${Acknowledged.remarks}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>

	<script>
	
		document.addEventListener('DOMContentLoaded', function() {
			var remarkCells = document.querySelectorAll('.remark-cell');
			remarkCells.forEach(function(cell) {
				var remarks = cell.innerHTML;
				switch (remarks) {
				case 'TESTING':
					cell.innerHTML = '<span class="testing">' + "Testing"
							+ '</span>';
					break;
				case 'FALSE ALARM':
					cell.innerHTML = '<span class="falseAlarm">' + "False Alarm"
							+ '</span>';
					break;
				case 'ACKNOWLEDGED':
					cell.innerHTML = '<span class="acknowledged">'
							+ "Acknowledged" + '</span>';
					break;
				case 'SECURITY VENDOR INFORMED':
					cell.innerHTML = '<span class="informedVendor">'
							+ "Vendor Informed" + '</span>';
					break;
				default:
					break;
				}
			});
		});

		$(document)
				.ready(
						function() {
							
							var table = $('#example').DataTable(
									{
										responsive : true,
										paging : false,
										scrollCollapse : true,
										scrollY : '45vh',
										dom : 'Bfrtip',
										buttons : [ 'copy', 'csv', 'excel',
												'pdf', 'print' ],
										order : [ [ 5, 'desc' ] ]
									});

							
							$('#filterButton')
									.click(
											function() {
												var startDate = $('#startDate')
														.val();
												var endDate = $('#endDate')
														.val();

												if (startDate !== ''
														&& endDate !== '') {
													$.fn.dataTable.ext.search
															.push(function(
																	settings,
																	data,
																	dataIndex) {
																var dateTime = data[5]
																		|| ""; // Use data for the date column
																if (dateTime >= startDate
																		&& dateTime <= endDate) {
																	return true;
																}
																return false;
															});
													table.draw();
												} else {
													alert('Please provide both start and end dates.');
												}
											});

							// Clear button click event
							$('#clearButton').click(function() {
								$('#startDate').val('');
								$('#endDate').val('');
								$.fn.dataTable.ext.search.pop();
								table.draw();
							});
						});
	</script>




	<script src="./js/navBarToggle.js"></script>
	<script src="./externalJS/bootstrap.bundle.min.js"></script>
</body>
</html>