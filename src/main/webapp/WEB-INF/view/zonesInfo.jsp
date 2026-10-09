<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="org.springframework.http.HttpStatus"%>
<%@ page import="com.doritech.api.Entity.*"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Controls</title>

<link rel="stylesheet" href="./css/zonesInfo.css" />
<link rel="stylesheet" href="./externalCSS/bootstrap.min.css">
<link href="./fonts/poppins.css" rel="stylesheet">
<script src="./externalJS/fontawesome.js"></script>

<!-- DATATABLES CSS Links -->
<link rel="stylesheet"
	href="./externalCSS/dataTables.bootstrap4.min.css">
<link rel="stylesheet" href="./externalCSS/buttons.dataTables.min.css">
<link rel="stylesheet"
	href="./externalCSS/responsive.bootstrap4.min.css">

<!-- jQuery -->
<script src="./externalJS/jquery-3.5.1.min.js"></script>
<!-- Bootstrap JS -->
<script src="./externalJS/bootstrap.min.js"></script>

<script src="./externalJS/bootstrap.bundle.min.js"></script>



</head>
<body
	style="font-size: 12px; overflow-y: hidden; font-family: Poppins; background-color: #F4F6FF;">
	<div class="row mx-1">
		<div class="col-md-12">
			<div class="row">
				<div class="col-md-12">
					<h6 class="heading">Digitals India Hub : Zones Info</h6>
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<div class="m-2">
				<div class="card mx-2">


					<div class="dropdown m-2">
    <button class="btn btn-primary btn-sm dropdown-toggle" type="button"
        id="dropdownMenuButton" data-bs-toggle="dropdown"
        aria-expanded="false" style="background-color: #028bd7; font-size:14px;">
        Select Device
    </button>
    <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton"
        style="max-height: 250px; overflow-y: auto;">
        <c:forEach var="device"
            items="${deviceByBranchCodeInZonesInfo.payload}">
            <li>
                <a class="dropdown-item" href="#"
                    data-serial-number="${device.deviceSerialNo}"
                    onclick="updateButtonText(this, '${device.branchCode}')" style="font-size:12px;">
                    ${device.deviceName}
                </a>
            </li>
        </c:forEach>
    </ul>
</div>

					<div id="zone-list" style="max-height: 90vh; overflow-y: auto;"></div>


					<div class="bottom-nav">
						<div class="nav-item">
							<i class="fas fa-tablet-alt" style="color: #028bd7;"></i> <span><a
								href="/devices">Devices</a></span>
						</div>
						<div class="nav-item">
							<i class="fa-brands fa-superpowers" style="color: #028bd7;"></i>
							<span><a href="/zonesInfo">Zones</a></span>
						</div>
						<div class="nav-item">
							<i class="fas fa-bell" style="color: #028bd7;"></i> <span><a
								href="/notifications">Notifications</a></span>
						</div>
						<div class="nav-item">
							<i class="fas fa-gamepad" style="color: #028bd7;"></i> <span><a
								href="/controls">Controls</a></span>
						</div>
					</div>


				</div>
			</div>
		</div>
	</div>

	<script>

</script>
	<!-- Other JavaScript files -->
	<script src="./externalJS/sweetalert2.all.min.js"></script>
	<script src="./externalJS/jquery.dataTables.min.js"></script>
	<script src="./externalJS/responsive.bootstrap4.min.js"></script>
	<script src="./js/navBarToggle.js"></script>
	<script src="./js/zonesInfo.js"></script>

</body>
</html>
