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
<link rel="icon" type="image/png" sizes="16x16"
	href="./images/favicon.png">
<link rel="stylesheet"
	href="./vendor/owl-carousel/css/owl.carousel.min.css">
<link rel="stylesheet"
	href="./vendor/owl-carousel/css/owl.theme.default.min.css">
<link href="./vendor/jqvmap/css/jqvmap.min.css" rel="stylesheet">
<link href="./dashboardCSS/style.css" rel="stylesheet">
<script src="./externalJS/fontawesome.js"></script>
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
	src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAb57-UMhOIm1ug8nmviXUnIbCH2GUjrlE"></script>

<style>
.card-container {
	display: flex;
	gap: 10px;
	justify-content: center;
	align-items: center;
	margin-top: 20px;
}

.card {
	border: 1px solid #ccc;
	border-radius: 10px;
	overflow: hidden;
	box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
	transition: transform 0.2s;
	text-align: center;
}

.card:hover {
	transform: scale(1.05);
}

.card img {
	width: 200px;
	height: 450px;
	border-bottom: 1px solid #ccc;
}

.card h2 {
	margin: 10px 0;
	font-size: 18px;
	color: #007bff;
}

.heading {
	color: #ffffff;
	background-color: #003d87;
	height: 30px;
	border-bottom: 2px solid #028bd7;
	border-left: 20px solid #028bd7;
	padding-left: 20px;
	border-radius: 5px;
}

h1 {
	text-align: center;
	font-size: 24px;
	margin-top: 20px;
	color: #333;
}

.card img {
	width: 100%;
	height: auto;
	object-fit: cover;
	border-bottom: 1px solid #ccc;
}

#map {
	height: 300px;
	width: 100%;
}
</style>

</head>

<body>

	<input id="userId" value="${loginUserId}" style="display: none;">

	<!-- Main Container -->
	<div class="container-fluid " id="accordion">

		<!-- Heading -->
		<h5 class="heading">Choose Your Dashboard Here</h5>

		<!-- Dashboard Cards -->


		<!-- Other Contents -->
		<div class="row d-flex align-items-stretch mt-4">

			<div class="col-md-6 ">
				<div class="card mx-0 flex-grow-1">
					<div class="row"></div>
					<div class="m-2">
						<div id="map">

							<a id="dashboard1" href="./hmsDashboard">
								<h2 class="heading" style="color: white;">Dashboard 1</h2> <img
								src="./img/dashboard1.jpg" alt="Dashboard 1 Image">

							</a>


						</div>
					</div>
				</div>
			</div>
			<div class=" col-md-6  ">
				<div class="card mx-0 flex-grow-1">
					<div class="row"></div>
					<div class="m-2">
						<div id="map">

							<a id="dashboard2" href="./BranchBoxes">
								<h2 class="heading" style="color: white;">Dashboard 2</h2> <img
								src="./img/dashboard2.png" alt="Dashboard 2 Image">
							</a>

						</div>
					</div>
				</div>
			</div>
			
			<div class=" col-md-6  ">
				<div class="card mx-0 flex-grow-1">
					<div class="row"></div>
					<div class="m-2">
						<div id="map">

							<a id="dashboard3" href="./hmsDashboard3">
								<h2 class="heading" style="color: white;">Dashboard 3</h2> <img
								src="./img/dashboard2.png" alt="Dashboard 2 Image">
							</a>

						</div>
					</div>
				</div>
			</div>
			
			
		</div>
	</div>


	<script src="./js/hmsDashboard.js"></script>

	<!--**********************************
    Scripts
***********************************-->
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

	<script>
	$(document).ready(function() {
		
		
		$('#dashboard1').click(function(e) {
			e.preventDefault();
			saveDashboard('Dashboard1', './hmsDashboard');
		});

		$('#dashboard2').click(function(e) {
			e.preventDefault();
			saveDashboard('Dashboard2', './dashboardTwo');
		});
		$('#dashboard3').click(function(e) {
			e.preventDefault();
			saveDashboard('Dashboard3', './hmsDashboard3');
		});

		 function saveDashboard(dashboardName, redirectUrl) {
		        var userId = $('#userId').val();
		        var formData = {
		            dashboardNo: dashboardName,
		            empId: userId
		        };

		        showLoadingIndicator();

		        $.ajax({
		            type: 'POST',
		            url: 'saveDashboaord',
		            contentType: 'application/json',
		            data: JSON.stringify(formData),
		            success: function(response) {

		                window.location.href = redirectUrl;
		            },
		            error: function(error) {
		                console.error('Error saving dashboard:', error);
		            }
		        });
		    }
		});
		
		 /*------------------------Loader-------------*/
		 
		 
	    function showLoadingIndicator() {
	        Swal.fire({
	            title: 'Please wait...',
	            html: '<div class="progress"><div class="progress-bar progress-bar-striped progress-bar-animated bg-primary" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div></div>',
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

</body>
</html>