<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Controls</title>

<link rel="stylesheet" href="./css/notifications.css" />
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



</head>
<body
	style="font-size: 12px; overflow-y: hidden; font-family: Poppins; background-color:#F4F6FF">
	
	<input id="branchId" value="${loggedInUserBranchId}" style="display:none;">
	<div class="row mx-1">
		<div class="col-md-12">
			<div class="row">
				<div class="col-md-12">
					<h6 class="heading"> Notifications</h6>
				</div>
			</div>
		</div>
	</div>
	
	
	 <div class="container my-2">
    <h6 class="mx-2"><i class="fa-regular fa-bell fa-beat mx-1" style="color: #f42f60;"></i>Notifications</h6>
    <div class="row">
		<div class="col-md-12">
		
		
			<div class="m-2">
				<div class="card">
    <div id="notification-container" style="max-height:80vh; overflow-y:auto;"></div>
  </div>
  </div>
  
  
      <div class="bottom-nav">
    <div class="nav-item">
        <i class="fas fa-tablet-alt" style="color:#028bd7;"></i>
        <span><a href="/devices">Devices</a></span>
    </div>
    <div class="nav-item">
        <i class="fa-brands fa-superpowers" style="color:#028bd7;"></i>
        <span><a href="/zonesInfo">Zones</a></span>
    </div>
    <div class="nav-item">
        <i class="fas fa-bell" style="color:#028bd7;"></i>
        <span><a href="/notifications">Notifications</a></span>
    </div>
    <div class="nav-item">
        <i class="fas fa-gamepad" style="color:#028bd7;"></i>
        <span><a href="/controls">Controls</a></span>
    </div>
</div>
  </div>
  </div>
  </div>
 
	
	<!-- Other JavaScript files -->
	<script src="./externalJS/sweetalert2.all.min.js"></script>
	<script src="./externalJS/jquery.dataTables.min.js"></script>
	<script src="./externalJS/responsive.bootstrap4.min.js"></script>
	<script src="./js/navBarToggle.js"></script>
	<script src="./js/notifications.js"></script>
	
</body>
</html>
