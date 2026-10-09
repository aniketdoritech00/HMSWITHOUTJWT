<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="ISO-8859-1"%>
<%@ page import="org.springframework.http.HttpStatus"%>
<%@ page import="com.doritech.api.Entity.*"%>
<%-- <%@ page import="com.doritech.api.DTO.EmployeeMasterDTO"%>
<%@ page import="com.doritech.api.Entity.ParamEntity"%> --%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Social Media Report Details</title>


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

.alert-row {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 10px;
}

.checkbox-group {
	display: flex;
	gap: 10px;
}

.checkbox-group label {
	display: flex;
	align-items: center;
	gap: 5px;
}

/* Style icons with colors */
.whatsapp-icon {
	color: #25D366;
	font-size: 24px;
}

.sms-icon {
	color: #007BFF;
	font-size: 24px;
}

.call-icon {
	color: #34B7F1;
	font-size: 22px;
}

.email-icon {
	color: red;
	font-size: 24px;
}

.btn-primary {
	color: #fff;
	background-color: #337ab7;
	border-color: #2e6da4;
}

/* Custom style for disabled Save buttons */
.save-btn:disabled {
    background-color: #d6d6d6;  /* Light grey background */
    border-color: #d6d6d6;      /* Grey border */
    color: #999999;             /* Light grey text color */
    cursor: not-allowed;       /* Change cursor to not-allowed */
}
.sms-image {
    width: 20px;    /* Chhoti image ke liye adjust karein */
    height: 20px;
    vertical-align: middle; /* Checkbox ke sath properly align karne ke liye */
    margin-left: 5px; /* Checkbox ke baad thoda gap */
}
.mail-image {
    width: 20px;    /* Chhoti image ke liye adjust karein */
    height: 20px;
    vertical-align: middle; /* Checkbox ke sath properly align karne ke liye */
    margin-left: 5px; /* Checkbox ke baad thoda gap */
}

</style>

</head>
<body style="overflow-y: hidden; font-family:poppins; font-size:12px;">
	<div class="row mx-1">
		<div class="col-md-12">
			<div class="row">
				<div class="col-md-12">
					<h5 class="heading">Social Media Report</h5>
				</div>
			</div>
		</div>
	</div>
	<div style="display: none;" id="branchCodeContainer">${branchId}</div>
	<div class="row">
		<div class="col-md-12">
			<div class=" m-2">
				<div class="card">

					<div class="m-2" style="margin-top: -2%;">
						<table id="example" class="table table-striped table-bordered">
							<thead>
								<tr>
									<th>Branch Name</th>
									<th>Branch Type</th>
									<th>Bank Name</th>
									<th>Branch Code</th>
									<th>Alerts</th>
									<th>Action</th>
								</tr>
							</thead>
							<tbody style="color: black;" id="">
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- Modal HTML -->
	<div class="modal fade" id="alertModal" tabindex="-1" role="dialog"
     aria-labelledby="mobileModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header" style="padding: 5px; border-bottom: 0px;">
                <h5 class="heading" style="width: 100%" id="mobileModalLabel">Alerts</h5>
                <button type="button" class="close modal-close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body" id="alertModalBody">
                <!-- Mobile numbers will be shown here -->
            </div>
            <div class="modal-footer" style="padding: 5px">
                <button type="button" class="btn btn-sm btn-primary" id="submitAlertButton">Submit</button>
            </div>
        </div>
    </div>
</div>


	<script src="./js/socialMediaReport.js"></script>
	<script src="./js/navBarToggle.js"></script>
	<script src="./externalJS/bootstrap.bundle.min.js"></script>



	<script src="./externalJS/jquery.dataTables.min.js"></script>

	<script
		src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
</body>
</html>