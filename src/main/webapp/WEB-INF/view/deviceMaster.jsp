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
<title>DI-HMS</title>


<link rel="stylesheet" href="./css/deviceMaster.css" />
<link rel="stylesheet" href="./externalCSS/bootstrap.min.css" />
<script src="./externalJS/sweetalert2.all.min.js"></script>
<link href="./fonts/poppins.css" rel="stylesheet">
<script src="./externalJS/jquery-3.7.0.js"></script>
<script src="./externalJS/fontawesome.js"></script>

<!-- DATATABLES CSS Links -->

<link rel="stylesheet"
	href="./externalCSS/dataTables.bootstrap4.min.css">
<link rel="stylesheet" href="./externalCSS/buttons.dataTables.min.css">
<link rel="stylesheet"
	href="./externalCSS/responsive.bootstrap4.min.css">

<!-- DATATABLES JS Links -->
<script src="./externalJS/jquery.dataTables.min.js"></script>
<script src="./externalJS/dataTables.bootstrap4.min.js"></script>
<script src="./externalJS/responsive.bootstrap4.min.js"></script>
<script src="./externalJS/dataTables.responsive.min.js"></script>
<script src="./externalJS/dataTables.buttons.min.js"></script>
<script src="./externalJS/pdfmake.min.js"></script>
<script src="./externalJS/vfs_fonts.js"></script>
<script src="./externalJS/buttons.html5.min.js"></script>
<script src="./externalJS/buttons.print.min.js"></script>



<script src="./externalJS/bootstrap3-typeahead.min.js"></script>
<link rel="stylesheet" href="./externalCSS/bootstrap3.min.css" />
<script src="./externalJS/bootstrap3.min.js"></script>
<script src="./externalJS/bootstrap-multiselect.js"></script>
<link rel="stylesheet" href="./externalCSS/bootstrap-multiselect.css" />

<style>
.hidden {
	visibility: hidden;
}

.visible {
	visibility: visible;
}

.caret {
	display: none;
}

.multiselect-container {
	max-height: 50vh;
	overflow-y: auto;
	position: absolute;
	top: -45px;
	left: 150px;
	z-index: 99999;
	background-color: white;
	border: 1px solid #ccc;
	width: 100%;
}




.scrollable-tbody {
    display: block;
    max-height: 45vh; /* Set the maximum height */
    overflow-y: auto; /* Enable vertical scrolling */
}

.scrollable-tbody tr {
    display: table;
    width: 100%;
    table-layout: fixed; /* Ensure proper column alignment */
}

.tableStyle {
    display: table;
    width: 100%;
    table-layout: fixed;
}


</style>

</head>
<body style="font-size: 12px; font-family: Poppins; font-weight: 300">

	<div class="container-fluid">
		<div class="row mx-1">
			<div class="col-md-12">
				<div class="row">
					<div class="col-12 col-md-6">
						<h5 class="heading" style="font-size: 20px;">Device Master</h5>
					</div>
					<div class="col-12 col-md-3 mt-2 d-flex mb-2">
						<input class="form-control form-control-sm rounded typeahead me-2"
							type="text" id="searchInput" placeholder="Search by Device ID"
							style="max-height: 40px;" />
						<div id="suggestionDropdown"></div>
						<button class="input-group-text border-0 mx-2" id="searchButton"
							style="background-color: #028BD7; max-height: 32px;"
							onclick="searchByDeviceCode()">
							<i class="fas fa-search" style="color: #fff; font-size: 20px;"></i>
						</button>
					</div>
					<div
						class="col-12 col-md-3 mt-2 mt-md-0 d-flex align-items-center mb-2">
						<form id="uploadForm" enctype="multipart/form-data">
							<input class="form-control me-2" type="file" name="file"
								id="formFile"> <label for="formFile"
								class="btn btn-primary" style="background-color: #028bd7;">Upload
								File</label>
						</form>

						<div class="d-flex mx-2">
							<button type="button"
								class="btn btn-sm importButton me-2 mb-1 mx-4"
								data-toggle="tooltip" data-placement="top" title="Upload File"
								onclick="uploadFile()">
								<i class="fa-solid fa-upload"></i>
							</button>
							<button type="button" class="btn btn-sm importButton mb-1  mx-4"
								data-toggle="tooltip" data-placement="top" title="Download File">
								<i class="fa-solid fa-download" onclick="exportDeviceMaster();"></i>
							</button>
							
														<button type="button" class="btn btn-sm btn-primary mb-1 " 
        data-toggle="modal" data-target=".viewFullDataTable" 
        data-toggle="tooltip" title="View Full Data Table">
    <i class="fa-solid fa-table-list"></i>
</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>





	<div class="row  d-flex">

		<div class="col-md-12 ">


			<div class="col-md-12">

				<div class="card">


					<div class="mx-2">
						<div class="row">
							<div class="col-md-4 col-12">
								<div style="border: 1.75px solid #028bd7;" class="m-2">
									<div class="m-2">
										
										<table id="deviceListTable"
											class="table table-striped table-bordered">
				

											
											<thead>
												<tr>
													<th>Device Name</th>
													<th>Branch Name</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach items="${deviceList.payload}" var="device">
													<tr>
														<td><a href="#"
															onclick="searchDeviceFromTable('${device.deviceId}')"
															style="text-decoration: none;">${device.deviceName} </a></td>
														<td><a href="#"
															onclick="searchDeviceFromTable('${device.deviceId}')"
															style="text-decoration: none;">${device.branchName}</a></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
								</div>
							</div>

							<div class="col-md-8 col-12">
								<div class="mt-2"></div>
								<form id="deviceMasterForm" name="deviceMasterForm">

									<div style="border: 1.75px solid #028bd7;">
										<b style="margin-left: 1%; color: #1649A2;">Device
											Master Details</b>
										<div class="mb-2 row" style="margin: 1%;">
											<label class="custom-field one col-md-6"
												style="display: none;"> <input type="text"
												placeholder="" class="form-control form-control-sm"
												id="deviceId" value="${deviceMasterDTO.deviceId}" readonly />
												<span class="placeholder">Device ID</span>
											</label> <label class="custom-field one col-md-6"> <input
												type="text" placeholder=""
												class="form-control form-control-sm " id="deviceName" /> <span
												class="placeholder">Device Name</span>
											</label>
											<div class="custom-field one col-md-6">
												<div class="input-boxx active-greyy">
													<label class="input-labell">Device Type</label> <select
														class="input-11 form-control" id="deviceType"
														name="deviceType">
														<option id="deviceType" value="">--select--</option>
														<option id="deviceType" value="Smart Communicator">Smart
															Communicator</option>
															<option value="SIA">SIA Device</option>
														<option id="deviceType" value="Others">Others</option>
													</select>
												</div>
											</div>
											<label class="custom-field one col-md-6"> <input
												type="text" placeholder=""
												class="form-control form-control-sm " id="deviceSerialNo" />
												<span class="placeholder">Device Serial No.</span>
											</label>
											
											<label class="custom-field one col-md-6"> <input
												type="text" placeholder=""
												class="form-control form-control-sm " id="deviceLocation" />
												<span class="placeholder">Device Location</span>
											</label>

										</div>


										<div class="mb-2 row" style="margin: 1%;">

											 <label class="custom-field one col-md-6"> <input
												type="number" placeholder=""
												class="form-control form-control-sm" id="phoneNumber"
												maxlength="13" /> <span class="placeholder">
													<div id="phoneError"
														style="color: red; display: none; font-size: 14px; display: inline;"></div>
													Phone Number
											</span>

											</label>


										<div class="custom-field one col-md-6">
    <div class="input-boxx active-greyy">
        <label class="input-labell">Product Type</label>
        <select class="input-11 form-control" id="productType" name="productType">
            <option value="#">Select</option>
            <option value="D">Digitals</option>
            <option value="NG">Guard NG</option>
            <option value="O">Others</option>
        </select>
    </div>
</div>
											<label class="custom-field one col-md-6"> <input
												type="text" placeholder=""
												class="form-control form-control-sm " id="userNo" /> <span
												class="placeholder">User No</span>
												
											</label>
											
											
											<label class="custom-field one col-md-6"> <input
												type="text" placeholder=""
												class="form-control form-control-sm " id="supportNo" /> <span
												class="placeholder">Support No</span>
											</label>


										</div>

										<div class="mb-2 row" style="margin: 1%;">



											<div class="custom-field one col-md-6">
												<div class="input-boxx active-greyy">
													<label class="input-labell">Branch Code</label> <select
														class="input-11 form-control" id="branchCode"
														name="branchCode">
														<option id="branchCode" value="">--select--</option>
														<c:forEach var="branch" items="${branchList.payload}">
															<option id="branchCode" value="${branch.branchCode}">${branch.branchDesc}</option>
														</c:forEach>
													</select>
												</div>
											</div>


											<label class="custom-field one col-md-6"> <input
												type="email" placeholder=""
												class="form-control form-control-sm" id="email1" value="" />
												<span class="placeholder">
													<div id="email1Error"
														style="color: red; display: none; font-size: 14px; display: inline;"></div>
													Email

											</span>

											</label>
											<div class="custom-field one col-md-6">
												<div class="input-boxx active-greyy">
													<label class="input-labell">Protection Type</label> <select
														class="input-11 form-control" id="protectionType"
														name="protectionType">
														<option id="protectionType" value="#">Select</option>
														<option id="protectionType" value="passcodeId">Passcode
															+ ID</option>
														<option id="protectionType" value="callerId">Caller
															ID</option>
														<option id="protectionType" value="passcode">Passcode</option>

													</select>
												</div>
											</div>
											
											
													<div class="custom-field one col-md-6">
												<div class="input-boxx active-greyy">
													<label class="input-labell">Vendor Name</label> <select
														class="input-11 form-control" id="vendorName"
														name="vendorName">
														<option value="vendorTest">Vendor Test</option>
														<c:forEach items="${vendorList.payload}" var="vendor">
														<option value="${vendor.vendorId}">${vendor.vendorName}</option>
														</c:forEach>

													</select>
												</div>
											</div>

										</div>

										<div class="mb-2 row" style="margin: 1%;">
											<label class="custom-field one col-md-6"> <input
												type="text" placeholder=""
												class="form-control form-control-sm" id="passcode" value="" />
												<span class="placeholder"> Passcode </span>

											</label> <label class="custom-field one col-md-6"> <input
												type="text" placeholder=""
												class="form-control form-control-sm " id="address" /> <span
												class="placeholder">Address</span>
											</label> 
											
											
										
										</div>



										<div class="mb-2 row" style="margin: 1%;">
									
											<label class="custom-field one col-md-6"> <input
												type="email" placeholder=""
												class="form-control form-control-sm" id="vendorEmail"
												value="" /> <span class="placeholder">Vendor Email</span>
											</label>
										</div>
									</div>
								</form>
								<div class="mt-3"></div>
							</div>
						</div>
					</div>

				</div>

			</div>
			
		
			<div class="col-md-12 mt-2">
				<div class="card">

					<div class="m-1">

						<div class="table-container"
							style="min-height: 50vh;  overflow-y: auto;">
							<input type="hidden" id="zonesPayload"
								value="${zonesList.payload}"> <input type="hidden"
								id="inputsPayload" value="${zonesInputList.payload}">
									<b style="color: #1649A2;">Zone Details</b>
							<table id="example" class="table table-striped table-bordered">
    <thead>
        <tr>
            <th class="col-md-2">Input Desired Name</th>
            <th class="">Group Type</th>
            <th class="">Zone</th>
            <th class="hidden" id="messageOthers">Message</th>
            <th class="">Alert Type</th>
            <th class="col-md-1" id="addNew">Add New</th>
            <th class="hidden" id="alertAccess">Alert</th>
            <th class="hidden" id="flag">Flag</th>
            <th>Deletion</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td style="min-width: 100px;">
                <select class="form-control form-control-sm" name="inputType" id="inputType">
                    <option value="CCTV">CCTV</option>
                    <option value="SECURITY ALARM">Security Alarm</option>
                    <option value="FIRE ALARM">Fire Alarm</option>
                    <option value="ETL">ETL</option>
                    <option value="BACS">BACS</option>
                </select>
            </td>
            <td style="min-width: 100px;">
                <input type="text" placeholder="" class="form-control form-control-sm" id="" value="" />
            </td>
            <td>
                <div class="select-container">
                    <select id="framework" name="framework[]" multiple class="form-control custom-select"></select>
                </div>
            </td>
            <td style="min-width: 100px;">
                <input type="text" placeholder="" class="form-control form-control-sm" id="openModalInput" value="" />
            </td>
            <td>
                <button type="button" class="btn btn-sm ml-2" onclick="addNewItemIssue();" style="background: 
#028BD7; color: 
#ffff;">
                    <i class="fa-solid fa-square-plus fa-lg"></i>
                </button>
            </td>
            <td>
                <button type="button" class="btn btn-danger btn-sm ml-2" onclick="removeItemRow(this);">
                    <i class="fa-regular fa-trash-can fa-lg"></i>
                </button>
            </td>
        </tr>
    </tbody>
</table>

<!-- Container for the new table -->
<div id="dynamicContent"></div>
						</div>
						<div class="row">
							<div class="col-md-12">
								<div class="form-btn mt-2">
									<button type="button" class="btn btn-primary" id="saveButton"
										onclick="saveDeviceData();">Save</button>
									<button type="button" class="btn btn-info hidden"
										id="editButton" onclick="editDeviceData();">Confirm</button>
									<button style="border: 1px solid #fa0000;" type="button"
										class="btn btn-danger" id="deleteButton"
										onclick="deleteDevice();">
										<i class="fa-regular fa-trash-can fa-lg"></i>
									</button>
									<button type="button" class="btn btn-success hidden"
										id="siaSubmission" onclick="siaDeviceData();">Submit</button>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>




		</div>
	</div>


	<!-- View Full Table of Device Master Start -->

	<div class="modal fade bd-example-modal-lg viewFullDataTable"
		tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel"
		aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
			 <div class="modal-header">
        <button type="button" class="close btn btn-sm" data-bs-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
			<div class="modal-body">
				<table id="deviceMasterModalTable"
					class="table table-striped table-bordered">
					<thead>
						<tr>
							<th>Device Name</th>
							<th>Branch Name</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${deviceList.payload}" var="device">
							<tr>
								<td><a href="#"
									onclick="searchDeviceFromTable('${device.deviceId}')"
									style="text-decoration: none;">${device.deviceName} </a></td>
								<td><a href="#"
									onclick="searchDeviceFromTable('${device.deviceId}')"
									style="text-decoration: none;">${device.branchName}</a></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
				</div>
			</div>
		</div>
	</div>


	<!-- View Full Table of Device Master Ends -->

	<div class="modal bd-example-modal-lg" id="myModal">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<!-- Modal Header -->
				<div class="modal-header">
					<h4 class="modal-title">Alert Type Details</h4>
					<button type="button" class="close" data-bs-dismiss="modal">&times;</button>
				</div>

				<!-- Modal Body -->
				<div class="modal-body">
					<table class="table">
						<thead>
							<tr>
								<th style="width: 10%;">Zone</th>
								<th style="width: 25%;">Alert Type</th>
								<th style="width: 50%;">Message</th>
								<th style="width: 5%;">Primary Zone</th>
								<th style="width: 10%;" class="alert-header">Alert</th>
							</tr>
						</thead>
						<tbody id="selectedOptionsTable">
							<!-- Table content will be dynamically generated here -->
						</tbody>
					</table>
				</div>

				<div class="modal-footer">
					<button type="button" class="btn btn-primary" id="doneBtn">Done</button>
				</div>
			</div>
		</div>
	</div>

	
	
<script>
document.getElementById("deviceType").addEventListener("change", function () {
    const selectedValue = this.value;

    const existingTable = document.getElementById("example");
    const dynamicContent = document.getElementById("dynamicContent");
    const saveButton = document.getElementById("saveButton");
    const deleteButton = document.getElementById("deleteButton");
    const siaSubmission = document.getElementById("siaSubmission");

    if (selectedValue === "SIA") {
        existingTable.style.display = "none";
        saveButton.classList.add("d-none");
        deleteButton.classList.add("d-none");
        siaSubmission.classList.remove("hidden");

        $.ajax({
            url: 'getAllSiaCodes', // your backend endpoint
            method: 'GET',
            dataType: 'json',
            success: function(response) {
            	var siaCodesList= response.payload;
            	
                let tableHTML = `
                    <div class="table-responsive">
                        <table class="table table-bordered">
                            <thead class="tableStyle">
                                <tr>
                                    <th>S.No</th>
                                    <th>Zone Name</th>
                                    <th>Description</th>
                                    <th>Remarks</th>
                                </tr>
                            </thead>
                            <tbody class="scrollable-tbody tableStyle">`;

                for (let i = 0; i < siaCodesList.length; i++) {
                    tableHTML += '<tr>' +
                        '<td>' + (i + 1) + '</td>' +
                        '<td>' + siaCodesList[i].code + '</td>' +
                        '<td>' + siaCodesList[i].alertType + '</td>' +
                        '<td><input type="text" class="form-control form-control-sm" value="' + siaCodesList[i].alertDef + '" /></td>' +
                        '</tr>';
                }

                tableHTML += `</tbody></table><hr/></div>`;
                dynamicContent.innerHTML = tableHTML;
            },
            error: function(xhr, status, error) {
                console.error("Error fetching SIA codes:", error);
            }
        });

    } else {
        existingTable.style.display = "table";
        dynamicContent.innerHTML = "";
        saveButton.classList.remove("d-none");
        deleteButton.classList.remove("d-none");
        siaSubmission.classList.add("hidden");
    }
});



function siaDeviceData() {
    try {
        // Collect table data for SIA Protocol
        const tableRows = document.querySelectorAll("#dynamicContent table tbody tr");
        const siaDtos = [];

        tableRows.forEach((row) => {
            const command = row.cells[1].textContent.trim(); 
            const messageType = row.cells[2].textContent.trim(); 
            const message = row.cells[3].querySelector("input").value.trim();
            siaDtos.push({
                command: command,
                messageType: messageType,
                message: message,
                deviceId: $('#deviceId').val() 
            });
        });

        alert(JSON.stringify(siaDtos))
 
        const formData = {
            deviceId: $('#deviceId').val(),
            deviceName: $('#deviceName').val(),
            deviceType: $('#deviceType').val(),
            deviceLocation: $('#deviceLocation').val(),
            phoneNumber: $('#phoneNumber').val(),
            userNo: $('#userNo').val(),
            protectionType: $('#protectionType').val(),
            supportNo: $('#supportNo').val(),
            email1: $('#email1').val(),
            productType: $('#productType').val(),
            passcode: $('#passcode').val(),
            address: $('#address').val(),
            branchCode: $('#branchCode').val(),
            deviceSerialNo: $('#deviceSerialNo').val(),
            vendorName: $('#vendorName').val() || '', 
            vendorEmail: $('#vendorEmail').val(), 
            siaProtocol: siaDtos // Add SIA-specific data
        };

        
        alert(JSON.stringify(formData))
        // Send data to the backend
        $.ajax({
            url: 'addDeviceMaster',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function (data) {
                if (data.statusCode === 200) {
                    Swal.fire({
                        icon: 'success',
                        title: 'Success',
                        text: 'SIA Device added successfully!',
                    });
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: data.message || 'An error occurred while saving data.',
                    });
                }
            },
            error: function (error) {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'An error occurred while saving data.',
                });
            }
        });
    } catch (e) {
        Swal.fire({
            icon: 'error',
            title: 'Exception',
            text: 'An exception occurred. Please check your input and try again.',
        });
    }
}


</script>
	
<script>
document.addEventListener("DOMContentLoaded", function () {
    let userRole = sessionStorage.getItem("userRole");

    if (userRole === "super admin") {
        document.getElementById("saveButton").style.display = "none";
        document.getElementById("editButton").style.display = "none";
        document.getElementById("deleteButton").style.display = "none";
        
        
    }
});


</script>
	<script src="./js/navBarToggle.js"></script>
	<script src="./externalJS/bootstrap.bundle.min.js"></script>
	<script src="./js/deviceMaster.js"></script>

</body>
</html>