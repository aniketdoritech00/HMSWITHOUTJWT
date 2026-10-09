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
<link href="./fonts/poppins.css" rel="stylesheet">
<script src="./externalJS/sweetalert2.all.min.js"></script>
<script src="./externalJS/fontawesome.js"></script>



<style>
body {
	font-family: 'Poppins', sans-serif;
}

iframe {
	border: 0;
	width: 100%;
	margin-top: -2%;
}

.profile-container {
	background: rgba(255, 255, 255, 0.8);
	box-shadow: 0 0 10px rgba(0, 0, 0, 0.4);
	padding: 20px;
	width: auto;
}

.table-borderless tr, .table-borderless td {
	padding: 5px 10px;
}

@media ( max-width :600px) {
	iframe {
		margin-top: -7%;
	}
	.truncate {
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		max-width:  250px;
		 
	}
}

@media ( max-width : 767.98px) {
	.dropdown {
		margin-left: 0 !important;
		text-align: center;
		width: 100%;
	}
	.dropdown-toggle {
		width: 100%;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		max-width: 250px;
	}
}

@media ( min-width :1900px) {
	iframe {
		margin-top: -1%;
	}
}

.dropdown {
	margin-right: 4%;
	margin-left: 70%;
}

[data-sidebar-position="fixed"][data-layout="vertical"] .menu-toggle .quixnav
	{
	position: fixed;
}

[data-sidebar-style="full"][data-layout="vertical"] .menu-toggle .quixnav footer
	{
	display: none;
}

.arrow {
	color: #028bd7;
	position: relative;
	transition: transform 0.3s ease;
	cursor: pointer;
}

.arrow::before, .hamburger::after {
	position: absolute;
	color: #028bd7;
	transition: transform 0.3s ease;
}

.arrow ::before {
	top: -10px;
}

.arrow::after {
	top: 10px;
}

.arrow.isactive {
	transform: rotate(180deg);
}

@media screen and (max-width: 768px) {
	.arrow {
		transform: rotate(180deg);
	}
}

.arrow.isactive {
	transform: rotate(180deg);
}

@media screen and (max-width: 768px) {
	.arrow.isactive {
		transform: rotate(0deg);
	}
}

:root {
	--color-text: #616161;
	--color-text-btn: #ffffff;
	--card3-gradient-color1: #028bd7;
	--card3-gradient-color2: #69caff;
}

.card-wrap {
	width: 220px;
	background: #fff;
	border-radius: 5px;
	border: 5px solid #fff;
	overflow: hidden;
	box-shadow: rgba(0, 0, 0, 0.19) 0px 10px 20px, rgba(0, 0, 0, 0.23) 0px
		6px 6px;
	cursor: pointer;
	transition: all .2s ease-in-out;
}

.card-wrap:hover {
	transform: scale(1.1);
}

.card-header2 {
	height: 200px;
	width: 100%;
	background: red;
	border-radius: 100% 0% 100% 0%/1% 0% 100% 100%;
	display: grid;
	place-items: center;
}

.card-header2 i {
	color: #fff;
	font-size: 72px;
}

.card-content {
	display: flex;
	flex-direction: column;
	align-items: center;
	margin: 0 auto;
}

.card-title {
	text-align: center;
	text-transform: uppercase;
	font-size: 16px;
	margin-top: 10px;
	margin-bottom: 20px;
}

.card-text {
	text-align: center;
	font-size: 12px;
	margin-bottom: 20px;
}

.card-btn {
	border: none;
	border-radius: 100px;
	padding: 5px 30px;
	color: #fff;
	margin-bottom: 15px;
	text-transform: uppercase;
}

.card-header2.three {
	background: linear-gradient(to bottom left, var(--card3-gradient-color1),
		var(--card3-gradient-color2));
}

.card-btn.three {
	background: linear-gradient(to left, var(--card3-gradient-color1),
		var(--card3-gradient-color2));
}

.table-borderless td {
	padding: 5px 10px;
	width: 70px;
}

#signalStrengthIcon i {
	transition: color 0.3s ease;
}
</style>
</head>

<body style="font-family: Poppins;">

	<input id="userId" value="${empId}" style="display: none;">

	<div id="main-wrapper">
		<div class="nav-header">
			<a href="./welcome" class="brand-logo"> <img class="logoHms mx-3"
				src="./logo/hmsLogo.png" alt="">
			</a>

			<div class="nav-control" style="padding: 0px;">
				<div class="arrow">
					<i class="fa-solid fa-arrow-left"></i>
				</div>
			</div>


		</div>



		<div class="header">

			<div class="header-content float-center">

				<nav class="navbar navbar-expand">
					<div class="collapse navbar-collapse">

						<div class="d-flex justify-content-end align-items-center w-100">
							<div class="d-flex align-items-center profile">
							
							
						<a href="#" class="nav-link mr-1" id="bellDropdown"
   role="button" data-bs-toggle="dropdown" aria-expanded="false"
   style="display: none;">
   <i class="fa-solid fa-bell" style="font-size: 20px; color: crimson;"></i>
</a>


								<ul class="dropdown-menu dropdown-menu-end"
									id="bellDropdownMenu" aria-labelledby="bellDropdown">

								</ul>

								<input id="remarksList" value="${remarksList.payload}"
									style="display: none;"> <a href="#"
									class="nav-link" id="signalStrengthIcon"
									style="font-size: 20px;"> <i class="fa-solid fa-signal" data-bs-toggle="tooltip"
									data-bs-placement="top" title="Heath Communicator's Signal"></i>

								</a> <span onclick="fetchSignalStrength();"> <i
									class="fa-solid fa-sync-alt" style="cursor: pointer;font-size: 10px; color: #028bd7; margin-left:-5px;"
									title="Refresh Signal Strength"></i>
								</span> 
								


								<img src="<c:url value='/logos/image/${logo.id}'/>" alt="Logo"
									class="mr-1 bank_logo" width="90" />
									 <a href="#"
									class="dropdown-toggle truncate d-flex align-items-center"
									id="profileDropdown" role="button" data-bs-toggle="dropdown"
									aria-expanded="false" style="font-size: 14px;"> <span
									class=" nameOfUser">
									<i class="fa-solid fa-user"></i> </span> <span
									id="loggedInUser" style="font-size: 14px;" class="nameOfUser">${loggedInUser}</span>


								</a>


								<div class="card-wrap dropdown-menu dropdown-menu-right"
									aria-labelledby="profileDropdown">
									<div class="card-header2 three">
										<img src="./img/download.jpg" alt="Profile Image"
											class="mx-auto d-block three"
											style="width: 100px; height: 100px; border-radius: 50%; margin-bottom: 20px;">
									</div>
									<div class="card-content">
										<h1 class="card-title">Profile</h1>
										<table class="table-borderless ps-1">
											<tr>
												<td><i class="fa-solid fa-code-branch"
													style="color: #74C0FC;" data-bs-toggle="tooltip"
													data-bs-placement="top" title="Branch ID"></i>Branch Code:</td>
												<td class="text-start">${branchId}</td>
											</tr>

											<tr>
												<td><i class="fa-solid fa-layer-group"
													style="color: #B197FC;" data-bs-toggle="tooltip"
													data-bs-placement="top" title="Group ID"></i>Group ID:</td>
												<td class="text-start">${groupId}</td>
											</tr>
											<tr>
												<td><i class="fa-regular fa-envelope"
													style="color: #FFD43B;" data-bs-toggle="tooltip"
													data-bs-placement="top" title="Email ID"></i>Email:</td>
												<td class="text-start">${email}</td>
											</tr>
											<tr>
												<td><i class="fa-solid fa-id-card"
													data-bs-toggle="tooltip" data-bs-placement="top"
													title="Pan Card No."></i>PAN:</td>
												<td class="text-start">${empPan}</td>
											</tr>
											<tr>
												<td><i class="fa-solid fa-phone"
													style="color: #63E6BE;" data-bs-toggle="tooltip"
													data-bs-placement="top" title="Phone No."></i>Phone:</td>
												<td class="text-start">${phone}</td>
											</tr>
											<tr>
												<td><i class="fa-solid fa-map-location"
													style="color: #63E6BE;" data-bs-toggle="tooltip"
													data-bs-placement="top" title="Address"></i>Address:</td>
												<td class="text-start">${address},${city},${state},
													${country}, ${pin}</td>
											</tr>
										</table>
										<button class="card-btn three" onclick="confirmLogout()">
											Logout&nbsp;&nbsp;<i class="fas fa-sign-out-alt"></i>
										</button>
									</div>
								</div>
							</div>
						</div>
					</div>
				</nav>
			</div>
		</div>









		<div class='dashboard'>
			<div class="dashboard-nav">

				<div class="nxn quixnav"></div>
				<div class="quixnav"
					style="background-color: #028bd7; border-radius: 100% 0% 100% 0%/0% 0% 100% 100%;">
					<div class="quixnav-scroll">
						<ul class="metismenu" id="menu">

							<c:forEach items="${menuList.payload}" var="menu">
								<nav class="dashboard-nav-list">
									<c:choose>
										<c:when test="${menu.parentId eq 0 and menu.childId eq 0}">
											<a href="${menu.menuHandlerName}" class="dashboard-nav-item"
												target="adminRight" onclick="showLoadingIndicator()"><i class="${menu.menuIcon}"></i>&nbsp;<span
												class="nav-text">${menu.menuName} </span> </a>
										</c:when>
										<c:when test="${menu.childId ne 0}">
											<div class="submenu-container">
												<a href="#" class="dashboard-nav-item with-submenu"
													style="font-size: 14px;"><i class="${menu.menuIcon}"></i>&nbsp;<span
													class="nav-text">${menu.menuName} </span> &nbsp;<i
													class="fa-sharp fa-solid fa-angle-down nav-text"
													style="color: #ffffff; position: absolute; right: 10%;"></i>
												</a>
												<div class="submenu" style="display: none;">
													<ul>
														<c:forEach items="${menuList.payload}" var="submenu">
															<c:if test="${submenu.parentId eq menu.childId}">
																<li><a href="${submenu.menuHandlerName}"
																	class="dashboard-nav-item submenu-item"
																	target="adminRight" onclick="showLoadingIndicator()"
																	style="font-size: 14px; color: #003d87;"> <i
																		class="${submenu.menuIcon}"></i>&nbsp;<span
																		class="nav-text">${submenu.menuName} </span>
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



<div class="help-icon">
        <a href="./helpPage" target="adminRight" onclick="showLoadingIndicator()">
            <i class="fa-solid fa-circle-question"></i> Help
        </a>
    </div>
						</ul>




						<div class="nav-item-divider"></div>
						<footer class="company-footer ">

							<img alt="doritech logo" src="/logo/digitals.png"
								class="logoCenter">     
						</footer>
					</div>

				</div>


			</div>
			<div class='content-body'>


				<iframe src="" name="adminRight" id="adminRight" frameborder="0"></iframe>

			</div>
		</div>
	</div>





	<!-- Modal -->
	<div class="modal bd-example-modal-lg" id="bellModal" tabindex="-1"
		aria-labelledby="bellModalLabel" aria-hidden="true">
		<div class="modal-dialog-centered modal-dialog modal-xl ">
			<div class="modal-content" style="border-radius: 20px;">
				<div class="modal-header"
					style="background: #C70039; border-radius: 20px;">
					<h4>
						<i class="fa-solid fa-bell fa-shake fa-2xl" style="color: #ffff;"></i>
					</h4>

					<div style="float: right;">


						<button type="button" class="btn" data-bs-dismiss="modal"
							aria-label="Close">
							<i class="fa-solid fa-xmark fa-xl" style="color: #ffff;"></i>
						</button>


					</div>
				</div>

				<div class="modal-body">
					<div class="card">
						<div class="card-body" style="border-radius: 20px;">

							<section class="get-in-touch">
								<form class="contact-form row" name="roleMaster"
									style="max-height: 280px; overflow-y: auto;">

									<table id="example" class="table table-striped table-bordered"
										style="color: #1d1d1d;">
										<thead
											style="border-radius: 20px; background: #1c0f54; color: #ffff;">
											<tr>
												<th>S.No</th>
												<th>Service Provider</th>
												<th>Branch Name</th>
												<th>Address</th>
												<th>Contact No.</th>
												<th>Device ID</th>
												<th>Active Zone</th>
												<th>Remark</th>
												<th>Action</th>
											</tr>
										</thead>
										<tbody>
											<!-- Dynamic rows go here -->
										</tbody>
									</table>

								</form>
							</section>

						</div>
					</div>
				</div>
			</div>
		</div>
	</div>


	<script>


</script>
	<script>
	
	document.addEventListener('DOMContentLoaded', function () {
	    const adminRightFrame = document.querySelector('iframe[name="adminRight"]');
	    if (adminRightFrame) {
	        adminRightFrame.addEventListener('load', hideLoadingIndicator);
	    }
	});

       
        function displaySessionCreationMessage() {
        	//Swal.fire("Success", "Session created successfully. You are logged in.", "success");
            console.log("Session created successfully. You are logged in.");
        }

       
        function displaySessionTimeoutMessage() {
        	Swal.fire("Session expired!", "Please log in again.", "warning").then(() => {
                 window.location.href = "/"; 
             });
         }

       
        function startSessionTimer() {
        	var sessionTimeoutInMilliseconds = 365 * 24 * 60 * 60 * 1000;
            setTimeout(displaySessionTimeoutMessage, sessionTimeoutInMilliseconds);
        }

       
        window.onload = function() {
            displaySessionCreationMessage(); 
            getUserDashboard();
        };

        function getUserDashboard() {
            var userId = $('#userId').val();
            
            showLoadingIndicator();

            $.ajax({
                type: 'GET',
                url: 'getDashboardByEmpId',
                data: { empId: userId },
                success: function(response) {
                    var iframe = document.getElementById('adminRight');

                    if (response.statusCode == 200) {
                        if (response.payload.dashboardNo === "Dashboard1") {
                            iframe.src = "./hmsDashboard";
                        } else if (response.payload.dashboardNo === "Dashboard2") {
                            iframe.src = "./dashboardTwo";
                        }else if (response.payload.dashboardNo === "Dashboard3") {
                            iframe.src = "./hmsDashboard3";
                        } else {
                            iframe.src = "./dashboardHomePage";
                        }
                    } else {
                        iframe.src = "./dashboardHomePage";
                    }

                    iframe.onload = function() {
                        hideLoadingIndicator();
                    };
                },
                error: function(error) {
                    console.error('Error fetching dashboard:', error);

                    hideLoadingIndicator();
                }
            });
        }

        
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

	<script>
     
     // Script to display minimized modals and populate branchCodes in dropdown.
    document.addEventListener("DOMContentLoaded", function () {
    	
    	localStorage.clear();
    	updateDropdown();
    	window.addEventListener('storage', function (event) {
            if (event.key === 'minimizedModals') {
                updateDropdown();
            }
        });

    	
    	
    	function updateDropdown() {
    	    const minimizedModals = JSON.parse(localStorage.getItem('minimizedModalsForWelcome')) || [];
    	    const branchCodeForminimizedModals = JSON.parse(localStorage.getItem('minimizedModals')) || [];
    	    const dropdownMenu = document.getElementById('bellDropdownMenu');
    	    const bellIcon = document.getElementById('bellDropdown');

    	    dropdownMenu.innerHTML = '';

    	    if (branchCodeForminimizedModals.length > 0) {
    	        branchCodeForminimizedModals.forEach((modalId) => {
    	            const branchCode = modalId.split('_')[1] || 'Unknown';

    	            const existingItem = Array.from(dropdownMenu.children).find(
    	                (item) => item.textContent === branchCode
    	            );
    	            if (existingItem) {
    	                dropdownMenu.removeChild(existingItem);
    	            }

    	            const listItem = document.createElement('li');
    	            const link = document.createElement('a');
    	            link.className = 'dropdown-item';
    	            link.href = '#';
    	            link.textContent = branchCode;

    	            link.addEventListener('click', function () {
    	                const modalData = JSON.parse(localStorage.getItem('minimizedModalsForWelcome')) || [];
    	                const branchData = modalData.find(item => item.branchDetails.branchCode === branchCode);
    	                if (branchData) {
    	                    populateModal(branchData);
    	                } else {
    	                    alert("No data available for this branch.");
    	                }
    	            });

    	            listItem.appendChild(link);
    	            dropdownMenu.appendChild(listItem);
    	        });

    	        // Show bell icon when minimized modals exist
    	        bellIcon.style.display = 'inline-block';
    	    } else {
    	        const noDataItem = document.createElement('li');
    	        noDataItem.innerHTML = '<span class="dropdown-item text-muted">No minimized modals available</span>';
    	        dropdownMenu.appendChild(noDataItem);

    	        // Hide bell icon if no minimized modals
    	        bellIcon.style.display = 'none';
    	    }
    	}

        
        
        function populateModal(data) {
            const alertMessage = data;

            console.log("DATA====" + JSON.stringify(data));

            const modal = document.getElementById('bellModal');
            const tableBody = modal.querySelector('#example tbody');

            tableBody.innerHTML = ''; // Clear existing rows

            let counter = 0;

            alertMessage.alarmedZonesList.forEach((subArray) => {
                subArray.forEach((zone) => {
                    counter++;

                    const tableBranch = alertMessage.fetchedBranch;
                    const tableVendor = alertMessage.branchDetails.vendorName;
                    const tableMobile = alertMessage.branchDetails.mobile;
                    const tableDevice = alertMessage.deviceId;
                    const tableZone = zone;

                    

                    const newRow = document.createElement('tr');

                    const serialCell = document.createElement('td');
                    serialCell.textContent = counter;
                    newRow.appendChild(serialCell);

                    const vendorCell = document.createElement('td');
                    vendorCell.textContent = tableVendor;
                    newRow.appendChild(vendorCell);

                    const branchCell = document.createElement('td');
                    branchCell.textContent = tableBranch;
                    newRow.appendChild(branchCell);

                    const addressCell = document.createElement('td');
                    addressCell.textContent = alertMessage.branchDetails.address1 + ', ' +
                                              alertMessage.branchDetails.address2 + ', ' +
                                              alertMessage.branchDetails.address3 + ', ' +
                                              alertMessage.branchDetails.address4;
                    newRow.appendChild(addressCell);

                    const mobileCell = document.createElement('td');
                    mobileCell.textContent = tableMobile;
                    newRow.appendChild(mobileCell);

                    const deviceCell = document.createElement('td');
                    deviceCell.textContent = tableDevice;
                    newRow.appendChild(deviceCell);

                    const zoneCell = document.createElement('td');
                    zoneCell.textContent = tableZone;
                    newRow.appendChild(zoneCell);

                    var remarkCell = document.createElement('td');
                    var selectDropdown = document.createElement('select');
                    selectDropdown.className = 'form-control';

                    // Parse remarks list value from input field
                    var remarksListInput = document.getElementById('remarksList');
                    if (remarksListInput && remarksListInput.value) {
                        var remarks = remarksListInput.value.slice(1, -1).split(',');
                        remarks.forEach(function(remark) {
                            var optionElement = document.createElement('option');
                            optionElement.textContent = remark.trim();
                            selectDropdown.appendChild(optionElement);
                        });
                    }

                    remarkCell.appendChild(selectDropdown);
                    newRow.appendChild(remarkCell);

                    const actionCell = document.createElement('td');
                    const actionButton = document.createElement('button');
                    actionButton.className = 'btn btn-primary';
                    actionButton.textContent = 'Submit';
                    actionCell.appendChild(actionButton);
                    newRow.appendChild(actionCell);

                    tableBody.appendChild(newRow);
                });
            });

            const bootstrapModal = new bootstrap.Modal(modal);
            bootstrapModal.show();
        }
    });

   
    
    // Function to fetch signal strength (defined globally)
       async function fetchSignalStrength() {
          // alert("hi");
           try {
               const signalIcon = document.querySelector("#signalStrengthIcon i");
               const response = await fetch("./hitUrlForHealthCommunicatorHealth", { method: "POST" });
               const result = await response.text();

               // Update the icon based on the API response
               let signalLevel = "gray";
               let tooltipText = "Unknown Signal";

               if (result.includes("POOR")) {
                   signalLevel = "red";
                   tooltipText = "Poor Signal";
               } else if (result.includes("WEAK")) {
                   signalLevel = "orange";
                   tooltipText = "Weak Signal";
               } else if (result.includes("MODERATE")) {
                   signalLevel = "yellow";
                   tooltipText = "Moderate Signal";
               } else if (result.includes("STRONG")) {
                   signalLevel = "green";
                   tooltipText = "Strong Signal";
               }

               // Apply changes to the signal icon
               signalIcon.style.color = signalLevel;
               signalIcon.setAttribute("data-bs-original-title", tooltipText); 
           } catch (error) {
               console.error("Error fetching signal strength:", error);
           }
       }

       // DOMContentLoaded to initialize page content
       document.addEventListener("DOMContentLoaded", function () {
           // Fetch signal strength on page load
           fetchSignalStrength();
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
	<script src="./externalJS/bootstrap.bundle.min.js"></script>
	<script src="./externalJS/jquery-3.7.0.js"></script>
	<script src="./js/welcomeToggle.js"></script>
</body>
</html>