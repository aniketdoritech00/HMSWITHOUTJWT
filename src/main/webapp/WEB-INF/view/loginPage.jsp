<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

	<!DOCTYPE html>
	<html lang="en">

	<head>
		<meta charset="UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<title>DI-HMS</title>
		<!-------------------------- CSS Links------------------------------>
		<link rel="stylesheet" href="./externalCSS/fontawesome.css">
		<link rel="stylesheet" href="./externalCSS/bootstrap.min.css">
		<link href="./fonts/poppins.css" rel="stylesheet">
		<link rel="stylesheet" href="./externalCSS/googleFonts.css">
		<link rel="stylesheet" href="./css/loginStyle.css" />
		<script src="./externalJS/sweetalert2.all.min.js"></script>

	</head>

	<body style="font-family:Poppins">
		<nav class="navbar navbar-expand-md navbar-light nav-above fixed-top" style="height: 45px;">
			<div class="container">
				<h6 class="navbar-brand" style="color: #fff;" href="#">DI-HMS</h6>
		

				<div id="nav-bar">
					<ul class="navbar-nav ml-auto">
						<li class="nav-item"><label class="nav-link" href="#"
								style="color: #fff;">${paramClient.payload.descp1}</label></li>
					</ul>
				</div>
			</div>
		</nav>
		
		<div class="ai-enabled-container">
        <div class="scrolling-wrapper">
            <span class="ai-enabled-text">🤖 Now, HMS is AI-Enabled ✨ Next-gen monitoring for modern infrastructure.</span>
            <span class="ai-enabled-text">🚀 Advanced Analytics & Smart Insights Available Now!</span>
            <span class="ai-enabled-text">⚡ Real-time AI Processing for Better Performance!</span>
            <span class="ai-enabled-text">💡 Smart Monitoring Solutions for Tomorrow!</span>
            <span class="ai-enabled-text">🔥 Experience the Future of Infrastructure Management!</span>
            <span class="ai-enabled-text">🤖 Now, HMS is AI-Enabled ✨ Next-gen monitoring for modern infrastructure.</span>
        </div>
    </div>
		


		<div class="login-container">
			<div class="login-form">
				<div class="login-form-inner">
					<h4>Login to DI-HMS</h4>
					<div class="login-form-group">
						<div class="form-group">


							<!-- <div class="col-sm-16" style="font-size: 13px;">
								<b>User Type:&nbsp; &nbsp;</b>
								<select id="userTypeDropdown" onchange="userChange(this.value);"
									style="padding: 5px; border: 1px solid 65; border-radius: 4px;">
									<option value="admin" selected="selected">Admin</option>
									<option value="superAdmin">Super Admin</option>
									<option value="employee">Employee</option>
								</select>
							</div> -->
						</div>
						<div style="height: 10px;"></div>
						<label for="adminEmail" style="display: block;" id="labelAdmin">Admin<span class="required-star"
								style="color: red;">*</span></label> <input type="text" placeholder="Enter email"
							id="adminEmail">

						<!-- <label for="superAdminEmail" style="display: none;" id="labelSuperAdmin">Super Admin<span
								class="required-star" style="color: red;">*</span></label> <input type="text"
							placeholder="Enter email" id="superAdminEmail" style="display: none;">

						<label for="employeeEmail" style="display: none;" id="labelEmployee">Employee<span
								class="required-star" style="color: red;">*</span></label> <input type="text"
							placeholder="Enter email" style="display: none;" id="employeeEmail">
 -->

						<div class="login-form-group mt-2">
							<label for="pwd">Password <span class="required-star" style="color: red;">*</span></label>
							<input autocomplete="off" type="password" placeholder="Enter Password" id="pwd">
						</div>

						<div class="login-form-group single-row">
							<div class="custom-check">
								<input autocomplete="off" type="checkbox" checked id="remember"><label
									for="remember">Remember me</label>
							</div>
							<a href="#" class="link forgot-link" id="forgotPasswordLink"
								onclick="openForgotPasswordModal()">Forgot Password ?</a>
						</div>
						<button type="button" class="rounded-button login-cta" id="loginButton">Login</button>
					</div>
				</div>
			</div>

			<div class="onboarding">
				<div class="swiper-container">
					<div class="swiper-wrapper">
						<img alt="company's logo" src="${paramClient.payload.descp2}" style="width:20%; height:40px; position:relative; float:right; top:5px; right:5px;">
						<div class="swiper-slide color-1">
							<img alt="pictureLogin" src="./img/hmsPicture.png" class="loginimg">
		
						</div>
					</div>

					<div class="swiper-pagination"></div>
		
				</div>
			</div>
		
		</div>
	
       

		<nav class="navbar fixed-bottom navbar-light background">
			<div class="container-fluid d-flex justify-content-center">
				<h4 class="navbar-brand text-center custom-font-size" style="color: #fff;">${paramData.payload.descp1}
				</h4>
			</div>
		</nav>


		<!---------------------------MODAL FOR FORGOT PASSWORD---------------------------------------------->
		<div class="modal" id="forgotPasswordModal">
			<div class="modal-dialog">
				<div class="modal-content">

					<div class="modal-header">
						<b class="modal-title"><span style="color: #343957;">Verify
								OTP</span></b>
						<button type="button" class="close" data-dismiss="modal" onclick="closeModal()">&times;</button>
					</div>
					<div class="modal-body modal-email">
						<div class='dashboard-content'>
							<div class='container'>
								<div class='card'>
									<div class='card-header'>
										<h6 style="color: #fff;">To Get OTP Enter Email ID</h6>
									</div>
									<div class='card-body'>
										<section class="get-in-touch">

											<form class="contact-form row" name="dialogBox">
												<div class="form-field col-md-10">
													<label class="label">E-mail:</label>
													<input id="email" name="email" class="input-text js-input"
														type="email" placeholder="Enter Your E-mail" required>
												</div>

												<div class="form-field col-lg-12">

													<button class="submit-btn" type="button"
														id="otpModalButton">Submit</button>

												</div>

											</form>
										</section>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>


		<!---------------------- MODAL FOR ENTER OTP FOR CHANGING PASSWORD --------------------------->

		<div class="modal" id="otpgenerationModal" style="overflow-y: hidden;">
			<!-- backdrop-filter: blur(2px); -->
			<div class="modal-dialog">
				<div class="modal-content">

					<div class="modal-header">
						<b class="modal-title"><span style="color: #343957;">Verify
								OTP</span></b>
						<button type="button" class="close" data-dismiss="modal" onclick="closeModal()">&times;</button>
					</div>
					<div class="modal-body modal-otp">
						<div class='dashboard-content'>
							<div class='container'>
								<div class='card'>
									<div class='card-header'>
										<h6 style="color: #fff;">VERIFY YOUR OTP</h6>
									</div>
<!-- 									
								<input id="emailId" name="email" class="input-text js-input"
									type="email" readonly> -->

									<div class='card-body'>
										<section class="get-in-touch">

											<form class="contact-form row" name="dialogBox">

												<div class="form-field col-md-12">

													<div class="otp-field">
														<input type="text" maxlength="1" /> <input type="text"
															maxlength="1" /> <input type="text" maxlength="1" /> <input
															type="text" maxlength="1" />
														<label class="label" for="enterOtp" id="enterOtp">Enter
															OTP:</label>

													</div>


												</div>

												<div class="form-field col-lg-12">

													<button class="submit-btn" type="button"
														id="submitOtpButton">Submit</button>

												</div>
											</form>


										</section>

									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
<script>
       
        function displaySessionCreationMessage() {
            console.log("Session created successfully. You are logged in.");
        }

       
        function displaySessionTimeoutMessage() {
        	 swal("Session expired!", "Please log in again.", "warning").then(() => {
                 window.location.href = "/"; 
             });
         }

       
        function startSessionTimer() {
        	var sessionTimeoutInMilliseconds = 1000 * 24 * 60 * 60 * 1000;  
            setTimeout(displaySessionTimeoutMessage, sessionTimeoutInMilliseconds);
        }

       
        window.onload = function() {
            displaySessionCreationMessage(); 
            startSessionTimer(); 
        };
        
        document.addEventListener('DOMContentLoaded', function() {
            const aiTexts = document.querySelectorAll('.ai-enabled-text');

            aiTexts.forEach(text => {
                // Auto-show celebration on page load
                setTimeout(() => {
                    createCelebrationIcons(text);
                }, Math.random() * 2000 + 500);

                // Trigger celebration on hover
                text.addEventListener('mouseenter', function() {
                    createCelebrationIcons(this);
                });
            });

            function createCelebrationIcons(element) {
                const celebrationIcons = ['✨', '🎈', '🌟', '💫', '⭐'];

                for (let i = 0; i < 4; i++) { 
                    setTimeout(() => {
                        const celebration = document.createElement('span');
                        celebration.innerHTML = celebrationIcons[Math.floor(Math.random() * celebrationIcons.length)];
                        celebration.style.position = 'absolute';
                        celebration.style.top = Math.random() * 60 + '%';
                        celebration.style.left = Math.random() * 80 + '%';
                        celebration.style.fontSize = '0.8rem'; 
                        celebration.style.pointerEvents = 'none';
                        celebration.style.animation = 'celebrationFloat 2.5s ease-out forwards';
                        celebration.style.zIndex = '10';

                        element.appendChild(celebration);

                        setTimeout(() => {
                            if (celebration.parentNode) {
                                celebration.parentNode.removeChild(celebration);
                            }
                        }, 2500);
                    }, i * 150);
                }
            }

            // Add periodic celebration bursts
            setInterval(() => {
                const randomText = aiTexts[Math.floor(Math.random() * aiTexts.length)];
                createCelebrationIcons(randomText);
            }, 5000);
        });

    </script> 
    
    	<script>
			// Run when the page loads
			window.onload = function () {
				// Check if data is stored in localStorage
				if (localStorage.getItem("rememberMe") === "true") {
					document.getElementById("adminEmail").value = localStorage.getItem("adminEmail") || "";
					document.getElementById("pwd").value = localStorage.getItem("password") || "";
					document.getElementById("remember").checked = true;
				}
			};

			// Add event listener to store/remove data when checkbox is toggled
			document.getElementById("remember").addEventListener("change", function () {
				if (this.checked) {
					// Save to localStorage
					localStorage.setItem("adminEmail", document.getElementById("adminEmail").value);
					localStorage.setItem("password", document.getElementById("pwd").value);
					localStorage.setItem("rememberMe", "true");
				} else {
					// Clear stored data
					localStorage.removeItem("adminEmail");
					localStorage.removeItem("password");
					localStorage.setItem("rememberMe", "false");
				}
			});

			// Optional: Also save data on input change
			document.getElementById("adminEmail").addEventListener("input", function () {
				if (document.getElementById("remember").checked) {
					localStorage.setItem("adminEmail", this.value);
				}
			});

			document.getElementById("pwd").addEventListener("input", function () {
				if (document.getElementById("remember").checked) {
					localStorage.setItem("password", this.value);
				}
			});
		</script>
		<script src="./externalJS/jquery-3.7.0.js"></script>
		<script src="./externalJS/bootstrap.min.js"></script>
		<script src="./js/loginPageScript.js"></script>
		<script src="./js/forgotPassword.js"></script>



	</body>

	</html>