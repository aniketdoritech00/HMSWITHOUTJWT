/*-----------------------------IMPORT EMPLOYEE -: By Suyash -----------------------------------*/

function importEmployeeMaster() {
    var fileInput = document.getElementById('formFile');
    var file = fileInput.files[0];

    if (!file) {
        Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: 'Please choose a file.',
        });
        return;
    }

    var formData = new FormData();
    formData.append('employeeMasterFile', file);

    fetch('importEmployeeMaster', {
        method: 'POST',
        body: formData,
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.text();
    })
    .then(data => {
        if (data.includes('saved')) {
            Swal.fire({
                icon: 'success',
                title: 'Import Successful',
                text: data,
            });
        } else {
            Swal.fire({
                icon: 'error',
                title: 'Failed',
                text: 'Import Failed. Please choose correct file.',
            });
        }
    })
    .catch(error => {
        console.error('Error during import:', error);
        Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: 'Import failed. Please try again.',
        });
    });
}

/********** EXPORT EMPLOYEE MASTER DETAILS IN EXCEL SHEET ***********/
function exportEmployeeMaster() {
    
    fetch('downloadEmployeesData', {
        method: 'GET',
    })
    .then(response => {
      
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        
       
        return response.blob();
    })
    .then(blob => {
        
        const link = document.createElement('a');
        link.href = window.URL.createObjectURL(blob);
        link.download = 'EmployeeDetails.xlsx';
        
       
        document.body.appendChild(link);
        link.click();
        
     
        document.body.removeChild(link);
    })
    .catch(error => {
        console.error('Error fetching data:', error);
    });
}




/*----------------------------------------------IMS EMPLOYEE MASTER JS-------------------------------------------------*/

$(document).ready(function(){
    $('#groupId').change(function() {
        var opval = $(this).val();
        if(opval=="otherGroup"){
            $('#groupMasterModal').modal("show");
        }
    });
});
function closeModal(){
		       $('#groupMasterModal').modal("hide");
}

/*<<<====================================================================>>
Save new employee data @author Aniket mishra
<<<====================================================================>>*/
//Function to save branch data
function saveNewEmployee() {
	alert("Enter	")
    if (validateEmployeeFormData()) {
        const email = $("#email").val();

        // Check if the email exists
        $.ajax({
            url: "checkEmailExists",
            type: "GET",
            data: { email: email },
            success: function(exists) {
                if (exists) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Email Exists!',
                        text: 'The email address is already registered. Please choose another one.',
                    });
                } else {
                    // Proceed with saving the employee
                    var formData = {
                        empId: $("#empId").val(),
                        empName: $("#empName").val(),
                        empRole: $("#empRole").val(),
                        groupId: $("#groupId").val(),
                   
                        address: $("#address").val(),
                        city: $("#city").val(),
                        pin: $("#pin").val(),
                        state: $("#state").val(),
                        country: $("#country").val(),
                        mobile: $("#mobile").val(),
                        email: $("#email").val(),
                        branch: $("#branch").val()
                    };
alert(JSON.stringify(formData))
                    $.ajax({
                        url: "saveOrUpdateEmployee",
                        type: "POST",
                        dataType: "json",
                        contentType: "application/json",
                        data: JSON.stringify(formData),
                        success: function(data) {
                            Swal.fire({
                                icon: 'success',
                                title: 'Success!',
                                text: 'Employee saved successfully.',
                            }).then(function() {
                                location.reload();
                            });
                        },
                        error: function(error) {
                            Swal.fire({
                                icon: 'error',
                                title: 'Error!',
                                text: 'Failed to save Employee.',
                            });
                        }
                    });
                }
            },
            error: function(error) {
                console.error("Error checking email:", error);
                Swal.fire({
                    icon: 'error',
                    title: 'Error!',
                    text: 'An error occurred while checking the email.',
                });
            }
        });
    }
}

//<<<========== Function to validate employee form data before submission ==========>>>
function validateEmployeeFormData() {
try {
let errStr = "";
if ($('#empName').val() == '') {
Swal.fire({
	text: "Please enter Employee Name.",
	icon: "error",
});
return false;
}
if ($('#empRole').val() == '') {
Swal.fire({
	text: "Please Select Employee Role.",
	icon: "error",
});
return false;
}

if ($('#address').val() == '') {
Swal.fire({
	text: "Please enter Employee Address.",
	icon: "error",
});
return false;
}
if ($('#city').val() == '') {
Swal.fire({
	text: "Please enter City.",
	icon: "error",
});
return false;
}
if ($('#pin').val() == '') {
Swal.fire({
	text: "Please enter PinCode.",
	icon: "error",
});
return false;
}
if ($('#state').val() == '') {
Swal.fire({
	text: "Please enter State.",
	icon: "error",
});
return false;
}
if ($('#country').val() == '') {
Swal.fire({
	text: "Please enter Country.",
	icon: "error",
});
return false;
}
if ($('#mobile').val() == '') {
Swal.fire({
	text: "Please enter Mobile Number.",
	icon: "error",
});
return false;
}
if (!isValidPhoneNumber($('#mobile').val())) {
Swal.fire({
	text: "Please enter a valid Mobile Number.",
	icon: "error",
});
return false;
}
if ($('#email').val() == '') {
Swal.fire({
	text: "Please enter Email.",
	icon: "error",
});
return false;
} else if (!isValidEmail($('#email').val())) {
Swal.fire({
	text: "Please enter a valid Email.",
	icon: "error",
});
return false;
}
if ($('#branch').val() == '') {
Swal.fire({
	text: "Please enter Branch.",
	icon: "error",
});
return false;
}
return true;
} catch (err) {
alert(err.message);
return false;
}
}

//<<==== Function to validate email format ====>>>
function isValidEmail(email) {
var emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
return emailPattern.test(email);
}

//<<==== Function to validate phone number (assuming 10 or 13 digits)====>>
function isValidPhoneNumber(phoneNumber) {
return /^\d{10}$/.test(phoneNumber) || /^\d{13}$/.test(phoneNumber);
}

//<<==== Function to validate PAN (assuming 10 characters alphanumeric) ====>>>
/*function isValidPan(pan) {
var panPattern = /^[A-Za-z]{5}[0-9]{4}[A-Za-z]{1}$/;
return panPattern.test(pan);
}*/

//<<==== Document ready function to initialize event listeners====>>
$(document).ready(function() {
$('#mobile').on('blur', function() {
var mobileNumber = $(this).val();
if (mobileNumber && !isValidPhoneNumber(mobileNumber)) {
$('#mobileError').text('Invalid!!').show();
} else {
$('#mobileError').hide();
}
});

$('#email').on('blur', function() {
var email = $(this).val();
if (email && !isValidEmail(email)) {
$('#emailError').text('Invalid!!').show();
} else {
$('#emailError').hide();
}
});

// Add blur event for PAN validation
/*$('#empPan').on('blur', function() {
var pan = $(this).val();
if (pan && !isValidPan(pan)) {
$('#panError').text('Invalid!!').show();
} else {
$('#panError').hide();
}
});*/

// Add change event for pin code to fetch details
$('#pin').on('change', fetchPinCodeDetails);
});

//<<<==== Function to fetch pin code details using Axios (from your provided code)====>>>

function fetchPinCodeDetails() {
const pinCode = $('#pin').val();
if (pinCode.length === 6) {
axios.get(`https://api.postalpincode.in/pincode/${pinCode}`)
.then(response => {
	if (response.data && response.data.length > 0 && response.data[0].Status === "Success") {
		const postOffice = response.data[0].PostOffice[0];
		$('#city').val(postOffice.District);
		$('#state').val(postOffice.State);
		$('#country').val(postOffice.Country);
	} else {
		showValidationError('Invalid Pin Code.');
	}
})
.catch(error => {
	console.error(error);
	showValidationError('Failed to fetch details. Please try again.');
});
}
}


/*
function checkAllFieldsFilled() {
    var allFieldsFilled = true;
    $(".js-input").each(function () {
        if ($(this).val() === '') {
            allFieldsFilled = false;
            return false; // Exit the loop early if an empty field is found
        }
    });
    return allFieldsFilled;
}

*/
function resetForm() {
    try {
         $("#empForm")[0].reset();
    } catch (resetError) {
        console.error(resetError);
}
}

/*****************JS TO FETCH EMPLOYEE BY EMPLOYEE ID****************** */
var originalEmployeeData = {}; // To store fetched employee data

function fetchEmployee(empId) {
    $.ajax({
        type: "GET",
        url: "getEmployeeByEmpId",
        data: { empId: empId },
        success: function (response) {
            if (response.statusCode === 200) {
                var employee = response.payload;
                console.log(response.payload);

                let userRole = sessionStorage.getItem("userRole");

                if (userRole != "super admin") {
                    $("#editButton").css("display", "block");
                    $("#saveEmployeeButton").prop("disabled", true).css("display", 'none');
                }

                // Populate form fields
                $("#groupId").val(employee.groupId);
                $("#empId").val(employee.empId);
                $("#empName").val(employee.empName);
                $("#empRole").val(employee.empRole);
                $("#address").val(employee.address);
                $("#city").val(employee.city);
                $("#pin").val(employee.pin);
                $("#state").val(employee.state);
                $("#country").val(employee.country);
                $("#mobile").val(employee.mobile);
                $("#email").val(employee.email);
                $("#branch").val(employee.branch);

                // Store the original data for comparison
                 originalEmployeeData = {
                    groupId: employee.groupId,
                    empId: employee.empId,
                    empName: employee.empName,
                    empRole: employee.empRole,
                    address: employee.address,
                    city: employee.city,
                    pin: employee.pin,
                    state: employee.state,
                    country: employee.country,
                    mobile: employee.mobile,
                    email: employee.email,
                    branch: employee.branch
                };
            } else {
                console.error("Error fetching details:", response.message);
            }
        },
        error: function (error) {
            console.error("Error fetching details:", error);
        }
    });
}

function editEmployee() {
    var employeeId = $("#empId").val();
    var formData = getEmployeeFormData();

    // Compare formData with originalEmployeeData
    if (isDataUnchanged(formData, originalEmployeeData)) {
        Swal.fire({
            icon: "info",
            title: "No Changes Detected",
            text: "No changes were made. Please modify the details before updating."
        });
        return; // Stop the function execution
    }

    if (validateEmployeeFormData()) {
        Swal.fire({
            title: "Are you sure?",
            text: "Do you really want to update this employee?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Yes, update it!"
        }).then((result) => {
            if (result.isConfirmed) {
                try {
                    formData.empId = employeeId;

                    $.ajax({
                        url: "saveOrUpdateEmployee",
                        type: "POST",
                        dataType: "json",
                        contentType: "application/json",
                        data: JSON.stringify(formData),
                        success: function (response) {
                            try {
                                Swal.fire({
                                    icon: 'success',
                                    title: 'Success!',
                                    text: 'Employee updated successfully.',
                                }).then(function () {
                                    location.reload();
                                });
                            } catch (successError) {
                                console.error(successError);
                            }
                        },
                        error: function (error) {
                            try {
                                Swal.fire({
                                    icon: 'error',
                                    title: 'Error!',
                                    text: 'Failed to update Employee.',
                                });
                                console.error(error);
                            } catch (errorHandlingError) {
                                console.error(errorHandlingError);
                            }
                        }
                    });
                } catch (outerError) {
                    console.error(outerError);
                }
            }
        });
    }
}

// Function to compare form data with original data
function isDataUnchanged(newData, originalData) {
	console.log(JSON.stringify(newData))
	
		console.log(JSON.stringify(originalData))
    return JSON.stringify(newData) === JSON.stringify(originalData);
}

function getEmployeeFormData() {
    return {
		groupId: $("#groupId").val(),
        empId: $("#empId").val(),
        empName: $("#empName").val(),
        empRole: $("#empRole").val(),
        address: $("#address").val(),
        city: $("#city").val(),
        pin: $("#pin").val(),
        state: $("#state").val(),
        country: $("#country").val(),
        mobile: $("#mobile").val(),
        email: $("#email").val(),
        branch: $("#branch").val()
    };
}
