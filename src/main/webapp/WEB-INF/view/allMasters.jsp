<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="org.springframework.http.HttpStatus"%>
<%@ page import="com.doritech.api.Entity.MenuMasterEntity"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>DI-HMS</title>
<!-------------------------- CSS Links---------------------------- -->
<link rel="icon" type="image/png" sizes="16x16"
	href="./images/favicon.png">
<link rel="stylesheet"
	href="./vendor/owl-carousel/css/owl.carousel.min.css">
<link rel="stylesheet"
	href="./vendor/owl-carousel/css/owl.theme.default.min.css">
<link href="./vendor/jqvmap/css/jqvmap.min.css" rel="stylesheet">
<link href="./dashboardCSS/style.css" rel="stylesheet">
<link href="./css/welcomePage.css" rel="stylesheet">

<script src="/externalJS/fontawesome.js"></script>

<style>
iframe {
	border: 0;
	width: 100%;
	margin-top: -2%;
}
</style>
</head>
<body>
	<div id="main-wrapper">
		<div class="nav-header">
			<a href="/welcome" class="brand-logo"> <img class="logo-abbr"
				src="./images/hmsLogo.png" alt=""> 

			</a>

			<div class="nav-control">
				<div class="hamburger">

					<span class="line"></span><span class="line"></span><span
						class="line"></span>

				</div>

			</div>
		</div>

		<div class="header">
			<div class="header-content">
				<nav class="navbar navbar-expand">
					<div class="collapse navbar-collapse ">
						<div class="dropdown" style="margin-right: 4%; margin-left: 70%;">
							<a href="#" class="dropdown-toggle" id="profileDropdown"
								role="button" data-bs-toggle="dropdown" aria-expanded="false"
								style="font-size: 17px;"> Welcome, ${loggedInUser} </a>
							<ul class="dropdown-menu dropdown-menu-right"
								aria-labelledby="profileDropdown">
								<h3 class="dropdownHead">PROFILE</h3>

								<table>
									<tr>
										<td><strong>Id:</strong></td>
										<td>${empId}</td>
									</tr>
									<tr>
										<td><strong>Name:</strong></td>
										<td>${loggedInUser}</td>
									</tr>
									<tr>
										<td><strong>PAN:</strong></td>
										<td>${empPan}</td>
									</tr>
									<tr>
										<td><strong>Email:</strong></td>
										<td>${email}</td>
									</tr>
									<tr>
										<td><strong>Phone:</strong></td>
										<td>${phone}</td>
									</tr>
									<tr>
										<td><strong>Branch:</strong></td>
										<td>${branchId}</td>
									</tr>
									<tr>
										<td><strong>Address:</strong></td>
										<td>${address},${city},${state}, ${country}, ${pin}</td>
									</tr>
									<tr>
										<td><strong>Group Id:</strong></td>
										<td>${groupId}</td>
									</tr>
								</table>


								<b><a href="#" class=" dropdownfooter"
									onclick="confirmLogout()"> Logout&nbsp;<i
										class="fas fa-sign-out-alt"></i>
								</a></b>


							</ul>
						</div>


					</div>
				</nav>
			</div>
		</div>

		<div class='dashboard'>
			<div class="dashboard-nav">


				<div class="quixnav">
					<div class="quixnav-scroll">
						<ul class="metismenu" id="menu">

							<c:forEach items="${menuList.payload}" var="menu">
								<nav class="dashboard-nav-list">
									<c:choose>
										<c:when test="${menu.parentId eq 0 and menu.childId eq 0}">
											<a href="${menu.menuHandlerName}" class="dashboard-nav-item"
												target="adminRight"><i class="${menu.menuIcon}"></i>&nbsp;<span class="nav-text">${menu.menuName} </span>
												</a>
										</c:when>
										<c:when test="${menu.childId ne 0}">
											<div class="submenu-container">
												<a href="#" class="dashboard-nav-item with-submenu"
													style="font-size: 14px;"><i class="${menu.menuIcon}"></i>&nbsp;<span class="nav-text">${menu.menuName} </span>
													&nbsp;<i class="fa-sharp fa-solid fa-angle-down nav-text"
													style="color: #ffffff; position: absolute; right: 10%;"></i>
												</a>
												<div class="submenu" style="display: none;">
													<ul>
														<c:forEach items="${menuList.payload}" var="submenu">
															<c:if test="${submenu.parentId eq menu.childId}">
																<li><a href="${submenu.menuHandlerName}"
																	class="dashboard-nav-item submenu-item"
																	target="adminRight" style="font-size: 14px;"> <i
																		class="${submenu.menuIcon}"></i>&nbsp;<span class="nav-text">${submenu.menuName} </span>
																</a></li>
															</c:if>
														</c:forEach>
													</ul>
												</div>
											</div>
										</c:when>
									</c:choose>
								</nav>
							</c:forEach>

						</ul>




						<div class="nav-item-divider"></div>
						<footer class="company-footer">
							<p>Powered By:</p>
							<img alt="doritech logo" src="${paramData.payload.descp2}"
								class="logoCenter">     
						</footer>
					</div>

				</div>


			</div>
			<div class='content-body '>


				<iframe src="./hmsDashboard" name="adminRight" id="adminRight"
					frameborder="0"></iframe>

			</div>
		</div>
	</div>

	<script>
 document.addEventListener("DOMContentLoaded", function() {
    var dropdownToggle = document.getElementById("profileDropdown");
    var dropdownMenu = dropdownToggle.nextElementSibling;

    dropdownToggle.addEventListener("click", function() {
        dropdownMenu.classList.toggle("show");
    });

    window.addEventListener("click", function(event) {
        if (!dropdownToggle.contains(event.target) && !dropdownMenu.contains(event.target)) {
            dropdownMenu.classList.remove("show");
        }
    });
});

</script>

	<script src="./vendor/global/global.min.js"></script>
	<script src="./dashboardJS/quixnav-init.js"></script>
	<script src="./dashboardJS/custom.min.js"></script>


	<!-- Vectormap -->
	<script src="./vendor/raphael/raphael.min.js"></script>
	<script src="./vendor/morris/morris.min.js"></script>


	<script src="./vendor/circle-progress/circle-progress.min.js"></script>
	<script src="./vendor/chart.js/Chart.bundle.min.js"></script>

	<script src="./vendor/gaugeJS/dist/gauge.min.js"></script>

	<!--  flot-chart js -->
	<script src="./vendor/flot/jquery.flot.js"></script>
	<script src="./vendor/flot/jquery.flot.resize.js"></script>

	<!-- Owl Carousel -->
	<script src="./vendor/owl-carousel/js/owl.carousel.min.js"></script>

	<!-- Counter Up -->
	<script src="./vendor/jqvmap/js/jquery.vmap.min.js"></script>
	<script src="./vendor/jqvmap/js/jquery.vmap.usa.js"></script>
	<script src="./vendor/jquery.counterup/jquery.counterup.min.js"></script>


	<script src="./dashboardJS/dashboard/dashboard-1.js"></script>
	<script src="/externalJS/bootstrap.bundle.min.js"></script>
	<script src="/externalJS/jquery-3.7.0.js"></script>
	<script src="/js/welcomeToggle.js"></script>
</body>
</html>