<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="ISO-8859-1"%>
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
<title>Active Device Log Details</title>
 
 
<link rel="stylesheet" href="./css/deviceLogDetails.css" />
<link rel="stylesheet" href="./css/multilingual.css" />
<link rel="stylesheet" href="./externalCSS/bootstrap.min.css" />
<script src="./externalJS/fontawesome.js"></script>
 
<!-- DATATABLES CSS Links -->
 
<link rel="stylesheet" href="./externalCSS/dataTables.bootstrap4.min.css">
<link rel="stylesheet" href="./externalCSS/buttons.dataTables.min.css">
<link href="./fonts/poppins.css" rel="stylesheet">
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
<style type="text/css">
td a {
	color: black;
	text-decoration: none !important;
}
 
.mt-2 {
	margin-top: 10px;
}
 
.mb-1 {
	margin-bottom: 5px;
}
 
#example {
	margin-top: 10px;
}
</style>
 
</head>
<body style="font-size: 12px; overflow-y: hidden; font-family:Poppins;">
	<div class="row mx-1">
		<div class="col-md-12">
			<div class="row">
				<div class="col-md-12">
					<h5 class="heading">Active Device Master Log Details</h5>
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<div class=" m-2">
				<div class="card">
					
					<div class="my-2" style="margin-top:-2%;">
						<table id="example" class="table table-striped table-bordered">
							<thead>
								<tr>
									<th>Branch Name</th>
									<th>Bank Name</th>
									<th>Vendor</th>
									<th>Device Name</th>
									<th>Device Type</th>
									<th>Alarm Time</th>
									<th>Device Location</th>
									<th>Mobile No</th>
									<th>Alert Type</th>
									
								</tr>
							</thead>
							<tbody style="color: black;" id="activeLogsTableBody">
    <c:forEach items="${activeLogs.payload}" var="activeLog">
        <tr>
            <td>${activeLog.branchName}</td>
            <td>${activeLog.bankName}</td>
            <td>${activeLog.vendorName}</td>
            <td>${activeLog.deviceName}</td>
            <td>${activeLog.deviceType}</td>
            <td class="alarm-time" data-alarm-time="${activeLog.alarmTime}" style="text-align:right;"></td>
            <td>${activeLog.deviceLocation}</td>
            <td style="text-align:right;">${activeLog.mobileNo}</td>
            <td>${activeLog.alertType}</td>
        </tr>
    </c:forEach>
</tbody>

						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<script>
	document.addEventListener('DOMContentLoaded', function() {
	    const alarmTimeCells = document.querySelectorAll('.alarm-time');

	    alarmTimeCells.forEach(cell => {
	        const alarmTime = cell.getAttribute('data-alarm-time');
	        if (alarmTime) {
	            const date = new Date(alarmTime);
	            const formattedDateTime = date.toLocaleString('en-US', {
	                year: 'numeric',
	                month: '2-digit',
	                day: '2-digit',
	                hour: '2-digit',
	                minute: '2-digit',
	                second: '2-digit',
	                hour12: false
	            });
	            cell.textContent = formattedDateTime;
	        }
	    });
	});

	</script>
	<script src="./js/navBarToggle.js"></script>
	<script src="./externalJS/bootstrap.bundle.min.js"></script>
	<script src="./js/dateAndTime.js"></script>

</body>
</html>