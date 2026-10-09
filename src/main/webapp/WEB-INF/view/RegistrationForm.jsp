<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Visitor Registration</title>
<!-- Bootstrap CSS -->
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
<!-- Font Awesome Icons -->
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
<!-- SweetAlert2 CSS -->
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/sweetalert2@10">

<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Montserrat:400,500&display=swap">
<style>
.upload__img-wrap {
	display: flex; 
	flex-wrap: nowrap; 
	overflow-x: auto;
}

.upload__img-box {
	width: 75px; 
	height: 100px; 
	margin-right: 10px; 
}

.img-bg {
	background-repeat: no-repeat;
	background-position: center;
	background-size: cover;
	position: relative;
	padding-bottom: 100%;
}
label{
margin-bottom:0;
display:block;
}

html * {
	box-sizing: border-box;
}

p {
	margin: 5px;
	margin-left: 0;
}

.upload { &__box { padding:40px;
	
}

&
__inputfile {
	width: .1px;
	height: .1px;
	opacity: 0;
	overflow: hidden;
	position: absolute;
	z-index: -1;
}

&
__btn {
	display: inline-block;
	font-weight: 600;
	color: #fff;
	text-align: center;
	min-width: 116px;
	padding: 5px;
	transition: all .3s ease;
	cursor: pointer;
	border: 2px solid;
	background-color: #4045ba;
	border-color: #4045ba;
	border-radius: 10px;
	line-height: 26px;
	font-size: 14px; &: hover { background-color : unset;
	color: #4045ba;
	transition: all .3s ease;
}

&
-box {
	margin-bottom: 10px;
}

}
&
__img { &-wrap { display:flex;
	flex-wrap: wrap;
	margin: 0 -10px;
}

&
-box {
	width: 200px;
	padding: 0 10px;
	margin-bottom: 12px;
}

&
-close {
	width: 24px;
	height: 24px;
	border-radius: 50%;
	background-color: rgba(0, 0, 0, 0.5);
	position: absolute;
	top: 10px;
	right: 10px;
	text-align: center;
	line-height: 24px;
	z-index: 1;
	cursor: pointer; &: after { content : '\2716';
	font-size: 14px;
	color: white;
}

}
}
}
.img-bg {
	background-repeat: no-repeat;
	background-position: center;
	background-size: cover;
	position: relative;
	padding-bottom: 100%;
}

  .btn-success:disabled {
            background-color: grey !important;
            border-color: grey !important;
            cursor: not-allowed;
        }
</style>
<style>
body {
	font-family: Montserrat, Arial, Helvetica, sans-serif;
	background-color: #f7f7f7;
	
}

.container {
	width: 90%;
	max-width: 500px;
	margin: 50px auto;
	padding: 20px;
	border: 1px solid #ccc;
	border-radius: 10px;
	background: white;
	box-shadow: 2px 1px 21px -9px rgba(0, 0, 0, 0.38);
	
}

input[type="file"]{
            width: 70%;
            margin: 5px 0 22px 0;
            border: none;
            background: #f7f7f7;
            font-family: Montserrat, Arial, Helvetica, sans-serif;
        }

input[type="text"],
        input[type="password"],
        input[type="phone"],
        
        input[type="date"],
        select {
            width: 100%;
            padding: 15px;
            margin: 5px 0 22px 0;
            border: none;
            background: #f7f7f7;
            font-family: Montserrat, Arial, Helvetica, sans-serif;
            border-radius: 8px;
        }
        select {
            appearance: none;
            -webkit-appearance: none;
            -moz-appearance: none;
        }
        input[type="text"]:focus,
        input[type="password"]:focus,
        input[type="phone"]:focus,
        input[type="date"]:focus,
        select:focus {
            background-color: #ddd;
            outline: none;
        }

.btn-success {
	padding: 10px 20px;
	margin: 8px 0;
	border: none;
	cursor: pointer;
	width: 100%;
	opacity: 0.9;
	font-size: 16px;
	font-family: Montserrat, Arial, Helvetica, sans-serif;
	border-radius: 10px;
}
.form-control-sm{
    padding-bottom: 30px;
}

button:hover {
	opacity: 1;
}
</style></head>
<body>

	<div class="container">
		<h2 style="margin-bottom:40px;" >
			<i class="fas fa-solid fa-file"></i> Complaint Registration
		</h2>
		<form>
			<input type="hidden" id="branchCode" name="branchCode" value="BR-31">

			<label for="name"><b><i class="fas fa-user"></i> Branch
					Name</b></label> <input type="text" id="branchName" name="name"
				placeholder="Enter Name" required> <label for="date" readonly><b><i
					class="fas fa-calendar-alt"></i> Date</b></label> <input type="date" id="date"
				placeholder="Select Date" name="date" required>



		<!-- 	<div class="upload__box">
				<div class="upload__btn-box">
					<label class="upload__btn">
						<p>Upload images</p> <input type="file" multiple="" id="imageFile"
						data-max_length="20" class="upload__inputfile form-control "
						capture="camera"><span>
						<button type="button" class="btn-sm btn-primary ml-2" id="uploadBtn" onclick="saveImages();" ><i class="fas fa-upload"></i> </button></span>
					</label>
				</div>
				<div class="upload__img-wrap"></div>
			</div> -->
			
			
			<div class="upload__box">
				<div class="upload__btn-box" >
					<label class="upload__btn "></label>
						<p><i class="fas fa-regular fa-image"></i><b> Upload images</b></p> <input type="file" multiple="" id="imageFile"
						data-max_length="20" class="upload__inputfile form-control-sm " style="background-color: #f7f7f7; border: 1px #f7f7f7; "
						capture="camera">
						<span><button type="button" class="btn-sm btn-primary  ml-2" id="addMore"  data-toggle="tooltip" title="Add more images"><i class="fas fa-plus"></i> </button></span>
						<span><button type="button" class="btn-sm btn-primary  ml-2" id="uploadBtn" onclick="saveImages();" data-toggle="tooltip" title="Upload images"><i class="fas fa-upload"></i> </button></span>
					</label>
				</div>
				<div class="upload__img-wrap"></div>
			</div>
	
		



			<label for="person" style="margin-top:9px;"><b><i class="fa-brands fa-product-hunt"></i>
					Product</b></label> <select id="product" name="person" class="form-control" style="background-color:#f7f7f7; border:1px #f7f7f7;"
				required>
				<c:forEach var="product" items="${productList.payload}">
					<option id="" value="${product}">${product}</option>
				</c:forEach>
			</select> <label for="phone" style="margin-top:9px;"><b><i class="fa-solid fa-pen"></i> Remarks</b></label>
			<input type="text" id="remarks" placeholder="Enter remarks"
				name="remarks" required>

			<div class="clearfix">
				<button type="button" id="submitBtn" class="btn btn-success"
					onclick="" disabled>Submit</button>
			</div>
		</form>
	</div>

	<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/js/all.min.js"></script>

	<script>


	 function getUrlParameter(name) {
		    name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
		    var regex = new RegExp('[\\?&]' + name + '=([^&#]*)');
		    var results = regex.exec(location.search);
		    return results === null ? '' : decodeURIComponent(results[1]);
		}

	// Function to decrypt asynchronously using EncryptionDecryption class
	function decryptWithEncryptionDecryption(encryptedText, secretKey, callback) {
	    // Prepare the data to be sent
	    var requestData = {
	        encryptedData: encryptedText,
	        secretKey: secretKey
	    };
	//alert(JSON.stringify(requestData));
	    // Make the AJAX request
	    $.ajax({
	        
	        type: "GET",
			contentType: "application/json",
			url: "decrypt",
	        data: requestData,
	        success: function(response) {
	        	//alert(JSON.stringify(response))
				
				var decryptedData = response.payload;
				//alert(decryptedData);
				
				 callback(decryptedData);
	        	//alert(JSON.stringify(response.payload))
	           
				
				document.getElementById('branchCode').value =decryptedBranchCode;
	        },
	        error: function(xhr, textStatus, errorThrown) {
	            console.error("Error decrypting data:", errorThrown);
	            // Call the callback function with null in case of error
	            callback(null);
	        }
	    });
	}

	// Main function to decrypt branchCode
	function decryptBranchCode() {
		//alert("hi")
	    var branchCodeEncrypted = getUrlParameter('data');
	    var secretKey = "doritech_consultancy@12"; 

	    decryptWithEncryptionDecryption(branchCodeEncrypted, secretKey, function(decryptedText) {
	        if (decryptedText) {
				//alert(decryptedText)
	        	//alert("decryptedText :"+decryptedText)
	            document.getElementById('branchCode').value = decryptedText;
				
				 getBranchByBranchCode(decryptedText);
	        } else {
	            console.error("Decryption failed or returned empty.");
	        }
	    });
	}

	// Call the main function


	decryptBranchCode(); 
	 function getBranchByBranchCode(branchCode) {
		
		
		 //var branchCode = $("#branchCode").val();
		// alert(branchCode)
	     // AJAX request
	     $.ajax({
	         type: "GET",
	         url: "getBranchByBranchCode",
	         data: {
	             branchCode: branchCode
	         },
	         success: function(response) {
	             console.log("Branch details:", response.payload);
	             document.getElementById('branchName').value = response.payload[0].branchDesc;
	         },
	         error: function(xhr, status, error) {
	             // Handle error
	             console.error("Error fetching branch details:", error);
	         }
	     });
	 }

 
 $(document).ready(function() {
	 
	// Define a function to fetch branch details by branch code
	
	//getBranchByBranchCode();
	 
	

	 
	 
	 
	 
	 
    var branch = $("#branchCode").val();
   //alert(branch)

  $("#submitBtn").click(function() {
    	showSavedLoadingIndicator();
          
            	//alert(id)
                var branchComplaint = {
                    id: id,
                    branchCode: $('#branchCode').val(),
                    product: $("#product").val(),
                    remarks: $("#remarks").val(),
                    dateTime: $("#date").val()
                };

            	
    //alert(JSON.stringify(branchComplaint));       	
                $.ajax({
                    type: "POST",
                    url: "saveBranchComplaint",
                    contentType: 'application/json',
                    data: JSON.stringify(branchComplaint),
                    success: function(response) {
                        hideLoadingIndicator();
                        Swal.fire({
                            icon: 'success',
                            title: 'Success',
                            text: 'Complaint registered successfully with complaint id :'+response.payload.id,
                            confirmButtonText: 'OK'
                        }).then((result) => {
                            if (result.isConfirmed) {
                            	location.reload();
                            	sendEmail(response.payload, imagesList); // Call sendEmail function with personDetails
                            }
                        });
                    },
                    error: function(xhr, status, error) {
                        Swal.fire({
                            icon: 'error',
                            title: 'Error',
                            text: 'Error occurred while saving data',
                            confirmButtonText: 'OK'
                        });
                        console.error(xhr.responseText);
                    }
                });

   
    });
   
   
  function sendEmail(branchComplaint, images) {
      var formData = new FormData();
      formData.append('branchComplaint', JSON.stringify(branchComplaint));

      for (var i = 0; i < images.length; i++) {
          formData.append('files', images[i]);
      }

      $.ajax({
          type: "POST",
          url: "sendComplaintMail",
          data: formData,
          contentType: false,
          processData: false,
          success: function(response) {
          	alert("Email sent")
          	 //location.reload();
            /*   Swal.fire({
                  icon: 'success',
                  title: 'Success',
                  text: 'Complaint registered successfully with complaint id :'+response.payload.id,
                  confirmButtonText: 'OK'
              }).then((result) => {
                  if (result.isConfirmed) {
                      location.reload();
                  }
              }); */
          },
          error: function(xhr, status, error) {
              Swal.fire({
                  icon: 'error',
                  title: 'Email Error',
                  text: 'Error occurred while sending email',
                  confirmButtonText: 'OK'
              });
              console.error(xhr.responseText);
          }
      });
  }
  }); 

</script>
<script >
var id = ''; 
function saveImages(){
	
	showLoadingIndicator();
	   
	   var formData = new FormData();
    formData.append('branchCode', $("#branchCode").val());
    formData.append('product', $("#product").val());
    formData.append('remarks', $("#remarks").val());
    formData.append('date', $("#date").val());

  // alert(JSON.strigify(formData))

    for (var i = 0; i < imagesList.length; i++) {
        formData.append('files', imagesList[i]); // Append as array to support multiple files
    }

    
    
    $.ajax({
        type: "POST",
        url: "savePersonDetails",
        data: formData,
        contentType: false,
        processData: false,
        success: function(response) {
        	$('#submitBtn').prop('disabled', false);
        	
        	console.log(JSON.stringify(response.payload))
        	 hideLoadingIndicator();
        	 Swal.fire({
                 icon: 'success',
                 title: 'Success',
                 text: 'Images uploaded successfully',
                 confirmButtonText: 'OK'
             });
        	 response.payload.forEach(item => {
                 //ids.push(item.complaintId);
                 id=item.complaintId;
             });
        },
        error: function(xhr, status, error) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Error occurred while saving data',
                confirmButtonText: 'OK'
            });
            console.error(xhr.responseText);
        }
    });
}
</script>
	<script>

jQuery(document).ready(function () {
	  ImgUpload();
	  
	  $('#addMore').on('click', function () {
	        $('#imageFile').click(); // Programmatically trigger the file input click
	    });
	  var today = new Date();
	    var dateString = today.toISOString().split('T')[0]; // Get date in YYYY-MM-DD format
	    $('#date').val(dateString);
	  
	  
	  $('[data-toggle="tooltip"]').tooltip();
	});
var imagesList=[];

function ImgUpload() {
	  var imgWrap = "";
	  var imgArray = [];
	 // var imagesList = [];

	  $('.upload__inputfile').each(function () {
	    $(this).on('change', function (e) {
	      imgWrap = $(this).closest('.upload__box').find('.upload__img-wrap');
	      var maxLength = $(this).attr('data-max_length');

	      var files = e.target.files;
	      var filesArr = Array.prototype.slice.call(files);
	      var iterator = 0;
	      filesArr.forEach(function (f, index) {

	        if (!f.type.match('image.*')) {
	          return;
	        }

	        if (imgArray.length > maxLength) {
	          return false;
	        } else {
	          var len = 0;
	          for (var i = 0; i < imgArray.length; i++) {
	            if (imgArray[i] !== undefined) {
	              len++;
	            }
	          }
	          if (len > maxLength) {
	            return false;
	          } else {
	            imgArray.push(f);
	            imagesList.push(f); 

	            var reader = new FileReader();
	            reader.onload = function (e) {
	              var html = "<div class='upload__img-box'><div style='background-image: url(" + e.target.result + ")' data-number='" + $(".upload__img-close").length + "' data-file='" + f.name + "' class='img-bg'><div class='upload__img-close'></div></div></div>";
	              imgWrap.append(html);
	              iterator++;
	            };
	            reader.readAsDataURL(f);
	          }
	        }
	      });
	    });
	  });

	  $('body').on('click', ".upload__img-close", function (e) {
	    var file = $(this).parent().data("file");
	    for (var i = 0; i < imgArray.length; i++) {
	      if (imgArray[i].name === file) {
	        imgArray.splice(i, 1);
	        break;
	      }
	    }
	    // Remove the image from the imagesList array
	    imagesList = imagesList.filter(function (img) {
	      return img.name !== file;
	    });
	    $(this).parent().parent().remove();
	  });
	}

</script>
<script >

function showLoadingIndicator() {
    Swal.fire({
        title: 'Uploading images...',
        html: '<div class="progress"><div class="progress-bar progress-bar-striped progress-bar-animated bg-success" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div></div>',
        allowOutsideClick: false,
        showCancelButton: false,
        showConfirmButton: false,
        onBeforeOpen: () => {
            Swal.showLoading();
        },
    });
}
function showSavedLoadingIndicator() {
    Swal.fire({
        title: 'Please wait...',
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
</body>
</html>
