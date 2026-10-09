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
<title>Manual Sync</title>

<link rel="stylesheet" href="./css/deviceLogDetails.css" />
<link rel="stylesheet" href="./css/multilingual.css" />
<link href="./fonts/poppins.css" rel="stylesheet">
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
<style>
#loader {
	border: 8px solid #f3f3f3;
	border-radius: 50%;
	border-top: 8px solid #3498db;
	width: 50px;
	height: 50px;
	animation: spin 2s linear infinite;
	margin: 20px auto;
}

@
keyframes spin { 0% {
	transform: rotate(0deg);
}
100
%
{
transform
:
rotate
(
360
deg
);
}
}
</style>
</head>
<body style="font-size: 12px; overflow-y: hidden; font-family:Poppins;">
	<div class="row mx-1">
		<div class="col-md-12">
			<div class="row">
				<div class="col-md-12">
					<h5 class="heading">Manual Sync</h5>
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<div class="m-2">
		
				<div class="card">



					<table id="example" class="table table-bordered">
						<thead>
							<tr>
								<th>Serial No.</th>
								<th>Branch Name</th>
								<th>Panel Name</th>
								<th>Bank Name</th>
								<th>Phone Number</th>
								<th>Last 5 Sync Time</th>
								<th>Action</th>

							</tr>
						</thead>
					<tbody id="deviceMasterTableBody">
    <c:forEach var="device" items="${deviceMaster.payload}">
        <tr>
            <td style="text-align:right">${device.deviceSerialNo}</td>
            <td>${device.branchName}</td>
            <td>${device.deviceName}</td>
            <td>${device.bankName}</td>
            <td style="text-align:right">${device.phoneNumber}</td>
			<td class="last-synced-time text-nowrap" style="text-align:right;">
			    <ul style="padding-left: 0; list-style-type: none;">
			       <c:forEach var="syncedTime" items="${device.lastSyncedTimes}">
         			   <li class="formatted-date" data-datetime="${syncedTime}"></li>
     			   </c:forEach>
			    </ul>
			</td>
            <td style="text-align:center;">
                <button class="btn btn-sm btn-danger reset-btn" data-device-serial="${device.deviceSerialNo}">Reset</button>
                <button class="btn btn-sm btn-info status-btn" data-device-serial="${device.deviceSerialNo}">Status</button>
            </td>
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
        const dateElements = document.querySelectorAll('.formatted-date');

        dateElements.forEach(el => {
            const rawDateTime = el.getAttribute('data-datetime');
            if (rawDateTime) {
                const dateObj = new Date(rawDateTime);
                const formattedDate = dateObj.toLocaleString('en-GB', {
                    day: '2-digit',
                    month: '2-digit',
                    year: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit',
                    second: '2-digit',
                    hour12: false
                }).replace(',', ''); // Remove the comma

                el.textContent = formattedDate;
            }
        });
    });
</script>
	
	<script>
	$(document).ready(function() {
	    $('.reset-btn').click(function() {
	        var deviceSerialNumber = $(this).data('device-serial');
	        Swal.fire({
	            title: 'Loading',
	            allowOutsideClick: false,
	            onBeforeOpen: () => {
	                Swal.showLoading();
	            }
	        });
	        
	        $.ajax({
	            url: './hitUrlForReset?deviceSerialNumber=' + deviceSerialNumber,
	            type: 'POST',
	            contentType: 'application/json',
	            success: function(response) {
	                Swal.close(); // Hide the loading SweetAlert
	                Swal.fire({
	                    icon: 'success',
	                    title: 'Success',
	                    text: response.message
	                });
	            },
	            error: function(xhr, status, error) {
	                Swal.close(); // Hide the loading SweetAlert
	                alert('Error occurred: ' + error);
	            }
	        });
	    });

	    $('.status-btn').click(function() {
	        var deviceSerialNumber = $(this).data('device-serial');
	        Swal.fire({
		        title: 'Loading',
		        html: '<div class="progress"><div class="progress-bar progress-bar-striped progress-bar-animated" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div></div>',
		        showCancelButton: false,
		        showConfirmButton: false,
		        allowOutsideClick: false,
		        allowEscapeKey: false,
		        allowEnterKey: false
		    });
	        
	        $.ajax({
	            url: '/hitUrlForStatus?deviceSerialNumber=' + deviceSerialNumber,
	            type: 'POST',
	            contentType: 'application/json',
	            success: function(response) {
	                Swal.close(); // Hide the loading SweetAlert
	                Swal.fire({
	                    icon: 'success',
	                    title: 'Success',
	                    text: response.message
	                });
	            },
	            error: function(xhr, status, error) {
	                Swal.close();
	                alert('Error occurred: ' + error);
	            }
	        });
	    });
	});

	
	
	document.addEventListener('DOMContentLoaded', function() {
	    const syncedTimeCells = document.querySelectorAll('.last-synced-time');

	    syncedTimeCells.forEach(cell => {
	        const lastSyncedTime = cell.getAttribute('data-last-synced-time');
	        if (lastSyncedTime) {
	            const date = new Date(lastSyncedTime);
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
	<script src="./js/activeDeviceLogDetails.js"></script>
	<script src="./js/tmsDataTables.js"></script>

</body>
</html>