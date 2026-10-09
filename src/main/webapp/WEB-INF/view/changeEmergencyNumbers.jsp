<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="org.springframework.http.HttpStatus"%>
<%@ page import="com.doritech.api.Entity.*"%>
<%@ page import="com.doritech.api.DTO.EmployeeMasterDTO"%>
<%@ page import="com.doritech.api.Entity.ParamEntity"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Change Emergency Numbers</title>

<link rel="stylesheet" href="./css/deviceLogDetails.css" />
<link rel="stylesheet" href="./css/multilingual.css" />
<link rel="stylesheet" href="./externalCSS/bootstrap.min.css">
<script src="./externalJS/fontawesome.js"></script>

<!-- DATATABLES CSS Links -->
<link rel="stylesheet" href="./externalCSS/dataTables.bootstrap4.min.css">
<link rel="stylesheet" href="./externalCSS/buttons.dataTables.min.css">
<link rel="stylesheet" href="./externalCSS/responsive.bootstrap4.min.css">
 


</head>
<body style="font-size: 12px; overflow-y: hidden;">
	<div class="row mx-1">
		<div class="col-md-12">
			<div class="row">
				<div class="col-md-12">
					<h5 class="heading">Change Emergency Numbers</h5>
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<div class="m-2">
				<div class="card">
					<div class="input-group rounded mt-2 col-md-6">
						
					</div>
					<table id="example" class="table table-striped table-bordered">
						<thead>
							<tr>
								<th>S.No</th>
								<th>Branch Name</th>
								<th>Device Id</th>
								<th>Action</th>
								
							</tr>
						</thead>
						<tbody>
							<c:forEach var="branches" items="${BranchDetails.payload}" varStatus="status">
								<tr>
									<td>${status.index + 1}</td>
									<td>${branches.branchName}</td>
									<td>${branches.deviceId}</td>
									<td><button class="btn btn-sm btn-info status-btn"
                                            onclick="getDetails('${branches.deviceId}','${branches.branchName}')">Get Status</button></td>
                                </tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>

<!-- Modal -->
<div class="modal fade" id="branchDetailsModal" tabindex="-1" role="dialog" aria-labelledby="qrCodeModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="qrCodeModalLabel">Emergency Contact Details</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body" id="emergencyDetailsBody">
                <!-- Dynamic content will be inserted here -->
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" onclick="saveDetails();">Save</button>
            </div>
        </div>
    </div>
</div>

<script>

function getDetails(deviceId, branchName) {
	
	showLoadingIndicator();
	//$('#branchDetailsModal').modal('show');
	//alert("Device ID: " + deviceId + "\nBranch Name: " + branchName);
        var BranchEmergencyNoDetails = [
            {
                "Name":"Vishal Bhardwaj",
                "MobileNo":"9027095493",
                "UserGroup":"1",
                "ReceiverGroup":"2"
            },
            {
                "Name":"Akansha",
                "MobileNo":"9027095493",
                "UserGroup":"1",
                "ReceiverGroup":"2"
            },
            {
                "Name":"Swati",
                "MobileNo":"9027095493",
                "UserGroup":"1",
                "ReceiverGroup":"2"
            },
            {
                "Name":"Abhay",
                "MobileNo":"9027095493",
                "UserGroup":"1",
                "ReceiverGroup":"2"
            },
            {
                "Name":"Prafull",
                "MobileNo":"9027095493",
                "UserGroup":"1",
                "ReceiverGroup":"2"
            }
        ];


            /* $.ajax({
                url: "getDetails",
                type: "POST",
                dataType: "json",
                contentType: "application/json",
                data: JSON.stringify(formData),
                success: function (data) {
                    try {
                        Swal.fire({
                            icon: 'success',
                            title: 'Success!',
                            text: 'Employee saved successfully.',
                        }).then(function () {
                                              location.reload();
                           });

                    } catch (successError) {
                        console.error(successError);
                    }
                },
                error: function (error) {
                    alert("error");
                    try {
                        Swal.fire({
                            icon: 'error',
                            title: 'Error!',
                            text: 'Failed to save Employee.',
                        });
                        console.error(error);
                    } catch (errorHandlingError) {
                        console.error(errorHandlingError);
                    }
                }
            }); */
            
        $("#emergencyDetailsBody").empty();

        BranchEmergencyNoDetails.forEach(function (detail, index) {
            
            var nameInput = $('<input>').attr({
                type: 'text',
                class: 'form-control',
                id: 'name_' + index,
                value: detail.Name,
                readonly: false
            });
            var mobileNoInput = $('<input>').attr({
                type: 'text',
                class: 'form-control',
                id: 'mobileNo_' + index,
                value: detail.MobileNo,
                readonly: false
            });
            var userGroupSelect = $('<select>').attr({
                class: 'form-control',
                id: 'userGroup_' + index
            });
            var receiverGroupSelect = $('<select>').attr({
                class: 'form-control',
                id: 'receiverGroup_' + index
            });

            var userGroupOptions = [
                { value: '1', text: 'Group 1' },
                { value: '2', text: 'Group 2' },
                { value: '3', text: 'Group 3' }
            ];
            userGroupOptions.forEach(function (option) {
                var optionElem = $('<option>').attr('value', option.value).text(option.text);
                if (option.value === detail.UserGroup) {
                    optionElem.attr('selected', true);
                }
                userGroupSelect.append(optionElem);
            });

            var receiverGroupOptions = [
                { value: '1', text: 'Receiver Group 1' },
                { value: '2', text: 'Receiver Group 2' },
                { value: '3', text: 'Receiver Group 3' }
            ];
            receiverGroupOptions.forEach(function (option) {
                var optionElem = $('<option>').attr('value', option.value).text(option.text);
                if (option.value === detail.ReceiverGroup) {
                    optionElem.attr('selected', true);
                }
                receiverGroupSelect.append(optionElem);
            });

            // Create form groups and append input fields
            var formGroup = $('<div>').addClass('form-group row');
            formGroup.append($('<label>').addClass('col-sm-4 col-form-label').attr('for', 'name_' + index).text('Name:'));
            formGroup.append($('<div>').addClass('col-sm-8').append(nameInput));
            $("#emergencyDetailsBody").append(formGroup);

            formGroup = $('<div>').addClass('form-group row');
            formGroup.append($('<label>').addClass('col-sm-4 col-form-label').attr('for', 'mobileNo_' + index).text('Mobile No:'));
            formGroup.append($('<div>').addClass('col-sm-8').append(mobileNoInput));
            $("#emergencyDetailsBody").append(formGroup);

            formGroup = $('<div>').addClass('form-group row');
            formGroup.append($('<label>').addClass('col-sm-4 col-form-label').attr('for', 'userGroup_' + index).text('User Group:'));
            formGroup.append($('<div>').addClass('col-sm-8').append(userGroupSelect));
            $("#emergencyDetailsBody").append(formGroup);

            formGroup = $('<div>').addClass('form-group row');
            formGroup.append($('<label>').addClass('col-sm-4 col-form-label').attr('for', 'receiverGroup_' + index).text('Receiver Group:'));
            formGroup.append($('<div>').addClass('col-sm-8').append(receiverGroupSelect));
            $("#emergencyDetailsBody").append(formGroup);

            $("#emergencyDetailsBody").append('<hr>');
        });

        hideLoadingIndicator()
        $('#branchDetailsModal').modal('show');
        
}

function showLoadingIndicator() {
    Swal.fire({
        title: 'Please wait!...',
        html: '<div class="progress"><div class="progress-bar progress-bar-striped progress-bar-animated bg-success" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div></div>',
        allowOutsideClick: false,
        showCancelButton: false,
        showConfirmButton: false,
        onBeforeOpen: () => {
            Swal.showLoading();
        },
    });
}


function hideLoadingIndicator() {
    Swal.close();
}

</script>




	  <!-- jQuery -->
   <script src="./externalJS/jquery-3.7.0.js"></script>
    <!-- Bootstrap JS -->
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.bundle.min.js"></script>

    <!-- Other JavaScript files -->
    <script src="./externalJS/sweetalert2.all.min.js"></script>
    <script src="./externalJS/jquery.dataTables.min.js"></script>
    <script src="./externalJS/dataTables.bootstrap4.min.js"></script>
    <script src="./externalJS/responsive.bootstrap4.min.js"></script>
    <script src="./externalJS/dataTables.responsive.min.js"></script>
    <script src="./externalJS/dataTables.buttons.min.js"></script>
    <script src="./externalJS/pdfmake.min.js"></script>
    <script src="./externalJS/vfs_fonts.js"></script>
    <script src="./externalJS/buttons.html5.min.js"></script>
    <script src="./externalJS/buttons.print.min.js"></script>
    <script src="./js/navBarToggle.js"></script>
    <script src="./js/activeDeviceLogDetails.js"></script>
    <script src="./js/tmsDataTables.js"></script>
</body>
</html>