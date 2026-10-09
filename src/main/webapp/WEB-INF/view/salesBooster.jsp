<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>IMS - Upload Excel</title>
<!-- CSS Links -->
<link rel="stylesheet" href="./css/fileImport.css" />
<link rel="stylesheet" href="./externalCSS/bootstrap.min.css">
<link rel="stylesheet" href="./externalCSS/googleFonts.css">

<!-- DataTables CSS -->
<link rel="stylesheet" type="text/css"
	href="https://cdn.datatables.net/1.11.5/css/jquery.dataTables.css">

<!-- JavaScript Libraries -->
<script src="./externalJS/sweetalert2.all.min.js"></script>
<script src="./externalJS/fontawesome.js"></script>

<!-- jQuery -->
<script type="text/javascript" charset="utf8"
	src="https://code.jquery.com/jquery-3.5.1.js"></script>

<!-- DataTables JS -->
<script type="text/javascript" charset="utf8"
	src="https://cdn.datatables.net/1.11.5/js/jquery.dataTables.js"></script>

<!-- Custom Styles -->
<style>
iframe {
	border: 0;
	width: 100%;
	margin-top: -7px;
}

.loader-wrapper {
	display: none;
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(255, 255, 255, 0.8);
	z-index: 1000;
}

.loader {
	border: 16px solid #f3f3f3;
	border-radius: 50%;
	border-top: 16px solid #3498db;
	width: 120px;
	height: 120px;
	animation: spin 2s linear infinite;
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
}

.loader-text {
	position: absolute;
	top: 60%;
	left: 50%;
	transform: translateX(-50%);
	font-size: 18px;
	color: #333;
}

@keyframes spin { 0%{
	transform: rotate(0deg);
} 100%{
	transform: rotate(360deg);
 }
}

/* Add custom height for the table container */
.table-container {
    min-height: 300px; /* Set desired height */
    overflow-y: auto; /* Enable vertical scrolling */
}
</style>
</head>
<body>
	<div class="loader-wrapper" id="loader-wrapper" style="display: none;">
		<div class="loader" id="loader"></div>
		<div class="loader-text">Data is importing...</div>
	</div>

	<div class="row mx-1">
		<div class="col-md-9">
			<h5 class="heading">Sales Booster (Import File)</h5>
		</div>
		<div class="col-md-3">
			<form id="filterItemIssued" name="filterItemIssue"
				enctype="multipart/form-data">
				<div class="input-group">
					<div class="custom-file">
						<input type="file" class="custom-file-input" id="excelFile"
							name="file" accept=".xls,.xlsx"
							aria-describedby="inputGroupFileAddon"> <label
							class="custom-file-label" for="excelFile">Choose file</label>
					</div>
					<button class="btn btn-sm importButton ml-1" type="submit"
						onclick="uploadFile()">
						<i class="fa-solid fa-upload"></i>
					</button>
				</div>
			</form>
		</div>
	</div>

	<div class="row mx-1">
		<div class="col-md-12">
			<div class="mt-1"></div>
			<div class="">
				<div class="card">
					<div class="m-1">
						<div class="table-container">
							<div class="mb-2 row" style="max-height: 280px; margin: 1%; overflow-y: auto;"></div>
							<table id="example"
								class="table table-striped table-bordered table-wrapped">
								<thead style="color: #fff; position: sticky; top: 0; z-index: 1; background: #028bd7;">
									<tr>
										<th class="col-md-1">DI Branch</th>
										<th>CCTV</th>
										<th>Serial No.</th>
										<th>Bank</th>
										<th>AO Name</th>
										<th>Regional Office</th>
										<th>Branch</th>
										<th>DVR Maker</th>
										<th>Model No.</th>
										<th>Channels</th>
										<th>Cameras</th>
										<th>Camera Quality</th>
										<th>Hard Disk Size</th>
										<th>HDD Qty.</th>
										<th>Record Ability From Date</th>
										<th>Record Ability To Date</th>
										<th>Recording</th>
										<th>Visit Complaint</th>
										<th>Remarks</th>
										<th>Date of Visit</th>
										<th>Report No.</th>
										<th>FA Name</th>
										<th>Year of Installation</th>
										<th>Locker Status (present or not)</th>
										<th>Locker Entry Exit</th>
										<th>Officer</th>
										<th>BM Name</th>
										<th>Number</th>
									</tr>
								</thead>
								<tbody id="saleMining">

								</tbody>
							</table>
						</div>
						<div class="pagination" id="paginationContainer"></div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- JavaScript Links -->
	<script src="./externalJS/bootstrap.bundle.min.js"></script>
	<script src="./externalJS/jquery-3.7.0.js"></script>
	<script src="./js/welcomeToggle.js"></script>
	<script src="./js/fileImport.js"></script>

</body>
</html>
