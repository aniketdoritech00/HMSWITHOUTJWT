/*function importBranchMaster() {
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
	formData.append('branchMasterFile', file);

	fetch('importBranchMaster', {
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
*/

/*----------------------------------JS TO SAVE BRANCH-----------------------------------*/

/*
 * function saveBranch() { if (validateBranchFormData()) { try { var branchData = {
 * branchCode: $("#branchCode").val(), bank: $("#bank").val(), branchDesc:
 * $("#branchDesc").val(), branchType: $("#branchType").val(), contractPerson:
 * $("#contractPerson").val(), mobile: $("#mobile").val(), telephone:
 * $("#telephoneNo").val(), parentCode: $("#parentCode").val(), faxNo:
 * $("#faxNo").val(), address1: $("#address1").val(), address2:
 * $("#address2").val(), address3: $("#address3").val(), address4:
 * $("#address4").val(), email: $("#email").val(), status: $("#status").val(),
 * ifsc: $("#ifsc").val(), longitude: $("#longitude").val(), latitude:
 * $("#latitude").val() };
 * 
 * 
 * $.ajax({ url: "addBranchMaster", type: "POST", dataType: "json", contentType:
 * "application/json", data: JSON.stringify(branchData), success: function
 * (data) { try { Swal.fire({ icon: 'success', title: 'Success!', text: 'Branch
 * saved successfully.', }).then(function () { location.reload(); });
 *  } catch (successError) { console.error(successError); } }, error: function
 * (error) { alert("error"); try { Swal.fire({ icon: 'error', title: 'Error!',
 * text: 'Failed to save Branch.', }); console.error(error); } catch
 * (errorHandlingError) { console.error(errorHandlingError); } } }); } catch
 * (outerError) { console.error(outerError); } } }
 */

/*
/*<<<====================================================================>>
Save new Branch data @author Aniket mishra
<<<====================================================================>>*/

function saveBranch() {
	if (validateBranchFormData()) {
		try {
			var branchData = {
				branchCode: $("#branchCode").val(),
				bank: $("#bank").val(),
				branchDesc: $("#branchDesc").val(),
				branchType: $("#branchType").val(),
				contractPerson: $("#contractPerson").val(),
				mobile: $("#mobile").val(),
				telephone: $("#telephoneNo").val(),
				parentCode: $("#parentCode").val(),
				faxNo: $("#faxNo").val(),
				address1: $("#address1").val(),
				address2: $("#address2").val(),
				address3: $("#address3").val(),
				address4: $("#address4").val(),
				email: $("#email").val(),
				status: $("#status").val(),
				ifsc: $("#ifsc").val(),
				longitude: $("#longitude").val(),
				latitude: $("#latitude").val(),
				mobileSecondary: $("#mob2").val(),
				mobileTertiary: $("#mob3").val(),
				mobileQuaternary: $("#mob4").val(),
				mobileQuinary: $("#mob5").val(),
				mobileSenary: $("#mob6").val(),
				email2: $("#email2").val(),
				email3: $("#email3").val(),
				email4: $("#email4").val(),
				email5: $("#email5").val(),
				email6: $("#email6").val()
			};

			$.ajax({
				url: "addBranchMaster",
				type: "POST",
				dataType: "json",
				contentType: "application/json",
				data: JSON.stringify(branchData),
				success: function(data) {
					try {

						if (data.statusCode == 200) {
							Swal.fire({
								icon: 'success',
								title: 'Success!',
								text: 'Branch saved successfully.',
							}).then(function() {
								location.reload();
							});
						} else if (data.statusCode == 400) {

							Swal.fire({
								icon: 'error',
								title: 'Error!',
								html: 'A branch with the following details already exists:<br>' +
									'<strong>IFSC:</strong> ' + data.payload.ifsc + '<br>' +
									'<strong>Branch:</strong> ' + data.payload.branchDesc + '<br>' +
									'Please check the details and try again.'
							});


						}





					} catch (successError) {
						console.error(successError);
					}
				},
				error: function(error) {
					alert("error");
					try {
						Swal.fire({
							icon: 'error',
							title: 'Error!',
							text: 'Failed to save Branch.',
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
}


/*------------------------------------VALIDATIONS FOR BRANCH MASTER-------------------------------------*/

//Function to validate phone number (assuming 10 or 13 digits)
function isValidPhoneNumber(phoneNumber) {
	return /^\d{10}$/.test(phoneNumber) || /^\d{12}$/.test(phoneNumber) || /^\d{13}$/.test(phoneNumber);
}

//Function to validate email format
function isValidEmail(email) {
	var emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
	return emailPattern.test(email);
}

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

	const mobileFields = ['#mob2', '#mob3', '#mob4', '#mob5', '#mob6'];
	const emailFields = ['#email2', '#email3', '#email4', '#email5', '#email6'];

	mobileFields.forEach((field, index) => {
		$(field).on('blur', function() {
			var mobile = $(this).val();
			if (mobile && !isValidPhoneNumber(mobile)) {
				$(`#phoneError${index + 2}`).text('Invalid!!').show();
			} else {
				$(`#phoneError${index + 2}`).hide();
			}
		});
	});

	emailFields.forEach((field, index) => {
		$(field).on('blur', function() {
			var email = $(this).val();
			if (email && !isValidEmail(email)) {
				$(`#emailError${index + 2}`).text('Invalid!!').show();
			} else {
				$(`#emailError${index + 2}`).hide();
			}
		});
	});

	// Add change event for pin code to fetch details
	$('#address3').on('change', fetchPinCodeDetails);
});

//Function to validate branch form data
function validateBranchFormData() {
	try {
		let errStr = "";
		if ($('#branchDesc').val() === '') {
			Swal.fire({
				text: "Please enter Branch Name",
				icon: "error",
			});
			return false;
		}
		if ($('#branchType').val() === '') {
			Swal.fire({
				text: "Please Select Branch Type.",
				icon: "error",
			});
			return false;
		}
		if ($('#contractPerson').val() === '') {
			Swal.fire({
				text: "Please enter Contract person.",
				icon: "error",
			});
			return false;
		}
		if ($('#telephoneNo').val() === undefined || $('#telephoneNo').val().length === 0) {
			Swal.fire({
				text: "Please enter telephone no.",
				icon: "error",
			});
			return false;
		}
		if ($('#parentCode').val() === '') {
			Swal.fire({
				text: "Please select a parent branch.",
				icon: "error",
			});
			return false;
		}

		if ($('#address1').val() === '') {
			Swal.fire({
				text: "Please enter Address.",
				icon: "error",
			});
			return false;
		}
		if ($('#mobile').val() === undefined || $('#mobile').val().length === 0) {
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
		if ($('#telephoneNo').val() === undefined || $('#telephoneNo').val().length === 0) {
			Swal.fire({
				text: "Please enter Mobile Number.",
				icon: "error",
			});
			return false;
		}
		if (!isValidPhoneNumber($('#telephoneNo').val())) {
			Swal.fire({
				text: "Please enter a valid Mobile Number.",
				icon: "error",
			});
			return false;
		}


		/*for (let i = 2; i <= 6; i++) {
		const mobileField = $(`#mob${i}`);
		//if (mobileField.val() === undefined || mobileField.val().length === 0) {
		//	Swal.fire({
		//		text: `Please enter Mobile Number ${i}.`,
		//		icon: "error",
		//	});
		//	return false;
		//}
		if (!isValidPhoneNumber(mobileField.val())) {
			Swal.fire({
				text: `Please enter a valid Mobile Number ${i}.`,
				icon: "error",
			});
			return false;
		}
		}*/

		/*for (let i = 2; i <= 6; i++) {
		const emailField = $(`#email${i}`);
		//if (emailField.val() === undefined || emailField.val() === '') {
		//	Swal.fire({
		//		text: `Please enter Email ${i}.`,
		//		icon: "error",
		//	});
		//	return false;
		//}
		if (!isValidEmail(emailField.val())) {
			Swal.fire({
				text: `Please enter a valid Email ${i}.`,
				icon: "error",
			});
			return false;
		}
		}
		*/
		if ($('#email').val() == '') {
			Swal.fire({
				text: "Please enter Email.",
				icon: "error",
			});
			return false;
		}
		if (!isValidEmail($('#email').val())) {
			Swal.fire({
				text: "Please enter a valid Email.",
				icon: "error",
			});
			return false;
		}

		if ($('#status').val() === '') {
			Swal.fire({
				text: "Please select status.",
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

//Function to fetch pin code details using Axios
function fetchPinCodeDetails() {
	const pinCode = $('#address3').val();
	if (pinCode.length === 6) {
		axios.get(`https://api.postalpincode.in/pincode/${pinCode}`)
			.then(response => {
				if (response.data && response.data.length > 0 && response.data[0].Status === "Success") {
					const postOffice = response.data[0].PostOffice[0];
					$('#address2').val(postOffice.District);
					$('#address4').val(postOffice.State);
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

//Function to show validation error
function showValidationError(message) {
	Swal.fire({
		icon: 'error',
		title: 'Validation Error',
		text: message,
	});
}


$(function() {
	var branchCode = $("#branchCode").val();

	// Fetch data from API and generate branch tree
	$.ajax({
		type: "GET",
		url: "getAllBranchForHierarchy",
		data: { loggedInBranch: branchCode },
		success: function(response) {
			generateBranchTree(response.payload, $("#branchTree"));
		},
		error: function(xhr, status, error) {
			console.error("Error fetching data from API:", error);
		}
	});

	function generateBranchTree(branches, container) {
		var ul = $("<ul>");
		branches.forEach(function(branch) {
			var li = $("<li>");
			var a = $("<a>").text(branch.branchDesc).attr('data-branch-code', branch.branchCode); // Add branch code as data attribute
			li.append(a);

			// Calculate and display the count of children
			if (branch.children && branch.children.length > 0) {
				var childCount = branch.children.length;
				var countSpan = $("<span>").addClass('child-count').text(" [" + childCount + "]");
				a.append(countSpan);
				li.addClass('parent');
				generateBranchTree(branch.children, li);
			} else {
				li.addClass('child');
			}
			ul.append(li);
		});
		container.append(ul);
	}

	// Click event handler for clicking on a branch in the tree
	$('#branchTree').on('click', 'a', function(e) {
		e.preventDefault();
		var branchCode = $(this).attr('data-branch-code');
		if (branchCode) {
			searchByBranch(branchCode); // Call searchByBranchCode function with branchCode
		}
	});

	$('.vertical-tree ul > li.child').hide();
});
var originalBranchData = {}; // Store original branch data

function searchByBranch(branchCode) {
	let userRole = sessionStorage.getItem("userRole");

	if (userRole === "super admin") {
		document.getElementById("saveButton").style.display = "none";
		document.getElementById("editButton").style.display = "none";
		document.getElementById("deleteButton").style.display = "none";
	} else {
		document.getElementById("saveButton").style.display = "none";
		document.getElementById("deleteButton").style.display = "inline-block";
	}

	$.ajax({
		type: "GET",
		url: "getBranchMasterByBranchCode",
		data: { branchCode: branchCode },
		success: function(response) {
			if (response && response.statusCode === 200 && response.payload && response.payload.length > 0) {
				var branch = response.payload[0];

				// Populate form fields with branch details
				$("#branchCode").val(branch.branchCode);
				$("#branchDesc").val(branch.branchDesc);
				$("#bank").val(branch.bank);
				$("#branchType").val(branch.branchType);
				$("#contractPerson").val(branch.contractPerson);
				$("#mobile").val(branch.mobile);
				$("#telephoneNo").val(branch.telephone);
				$("#faxNo").val(branch.faxNo);
				$("#address1").val(branch.address1);
				$("#address2").val(branch.address2);
				$("#address3").val(branch.address3);
				$("#address4").val(branch.address4);
				$("#email").val(branch.email);
				$("#status").val(branch.status);
				$("#ifsc").val(branch.ifsc);
				$("#longitude").val(branch.longitude);
				$("#latitude").val(branch.latitude);
				$("#mob2").val(branch.mobileSecondary);
				$("#mob3").val(branch.mobileTertiary);
				$("#mob4").val(branch.mobileQuaternary);
				$("#mob5").val(branch.mobileQuinary);
				$("#mob6").val(branch.mobileSenary);
				$("#email2").val(branch.email2);
				$("#email3").val(branch.email3);
				$("#email4").val(branch.email4);
				$("#email5").val(branch.email5);
				$("#email6").val(branch.email6);

				$("#parentCode").removeAttr("disabled");

				if (userRole !== "super admin") {
					$("#editButton").css("display", "block");
					$("#saveButton").prop("disabled", true).css("display", 'none');
				}

				// Store only relevant fields for comparison
				originalBranchData = {
					branchCode: branch.branchCode,
					bank: branch.bank,
					branchDesc: branch.branchDesc,
					branchType: branch.branchType,
					contractPerson: branch.contractPerson,
					mobile: branch.mobile,
					telephone: branch.telephone,
					parentCode: branch.parentCode,
					faxNo: branch.faxNo,
					address1: branch.address1,
					address2: branch.address2,
					address3: branch.address3,
					address4: branch.address4,
					email: branch.email,
					status: branch.status,
					ifsc: branch.ifsc,
					longitude: branch.longitude,
					latitude: branch.latitude,
					mobileSecondary: branch.mobileSecondary,
					mobileTertiary: branch.mobileTertiary,
					mobileQuaternary: branch.mobileQuaternary,
					mobileQuinary: branch.mobileQuinary,
					mobileSenary: branch.mobileSenary,
					email2: branch.email2,
					email3: branch.email3,
					email4: branch.email4,
					email5: branch.email5,
					email6: branch.email6
				};

				setParentCodeDropdown(branch.branchType, branch.parentCode);
			} else {
				console.error("No Branch found or invalid response.");
			}
		},
		error: function(error) {
			console.error("Error fetching data:", error);
		}
	});
}


/**
 * -----------------------------------JS TO EDIT
 * BRANCH-----------------------------------
 */
const searchInput = document.getElementById('searchInput');
const suggestionDropdown = document.getElementById('suggestionDropdown');
let lastSearchTerm = '';
let lastFetchedData = [];

searchInput.addEventListener('input', function() {
	const searchTerm = searchInput.value.toLowerCase();
	if (searchTerm !== lastSearchTerm) {
		fetch(`findByBranchCodeContainingPattern?branchCode=${searchTerm}`)
			.then(response => response.json())
			.then(data => {
				lastFetchedData = (data && data.payload && Array.isArray(data.payload)) ? data.payload : [];
				renderSuggestions(searchTerm);
			})
			.catch(error => console.error('Error fetching suggestions:', error))
			.finally(() => {
				lastSearchTerm = searchTerm;
			});
	} else {
		renderSuggestions(searchTerm);
	}
});

function renderSuggestions(searchTerm) {
	if (searchTerm.trim() === '') {
		suggestionDropdown.style.display = 'none';
		return;
	}
	const matchingSuggestions = lastFetchedData.filter(item => item.branchCode.toLowerCase().includes(searchTerm));
	if (matchingSuggestions.length > 0) {
		suggestionDropdown.innerHTML = '';
		matchingSuggestions.forEach(suggestion => {
			const suggestionItem = document.createElement('div');
			suggestionItem.classList.add('suggestionItem');
			suggestionItem.innerHTML = `<strong>${suggestion.branchCode}</strong> - ${suggestion.branchDesc}`;
			suggestionItem.addEventListener('click', () => {
				searchInput.value = suggestion.branchCode;
				suggestionDropdown.style.display = 'none';
			});
			suggestionDropdown.appendChild(suggestionItem);
		});
		suggestionDropdown.style.display = 'block';
	} else {
		suggestionDropdown.style.display = 'none';
	}
}

document.addEventListener('click', function(event) {
	if (!searchInput.contains(event.target) && !suggestionDropdown.contains(event.target)) {
		suggestionDropdown.style.display = 'none';
	}
});



/**
 * ---------------------------------------JS TO FETCH BRANCH
 * DETAILS-----------------------------------------*
 */

function searchByBranchCode() {
	var branchCode = $("#searchInput").val();
	$.ajax({
		type: "GET",
		url: "getBranchMasterByBranchCode",
		data: { branchCode: branchCode },
		success: function(response) {
			if (response && response.statusCode === 200 && response.payload && response.payload.length > 0) {
				var branch = response.payload[0];
				$("#branchCode").val(branch.branchCode);
				$("#branchDesc").val(branch.branchDesc);
				$("#bank").val(branch.bank);
				$("#branchType").val(branch.branchType);
				$("#contractPerson").val(branch.contractPerson);
				$("#mobile").val(branch.mobile);
				$("#telephoneNo").val(branch.telephone);
				$("#faxNo").val(branch.faxNo);
				$("#address1").val(branch.address1);
				$("#address2").val(branch.address2);
				$("#address3").val(branch.address3);
				$("#address4").val(branch.address4);
				$("#email").val(branch.email);
				$("#status").val(branch.status);
				$("#parentCode").removeAttr("disabled");
				setParentCodeDropdown(branch.branchType, branch.parentCode);
				$("#ifsc").val(branch.ifsc);
				$("#longitude").val(branch.longitude);
				$("#latitude").val(branch.latitude);
				$("#mob2").val(branch.mobileSecondary);
				$("#mob3").val(branch.mobileTertiary);
				$("#mob4").val(branch.mobileQuaternary);
				$("#mob5").val(branch.mobileQuinary);
				$("#mob6").val(branch.mobileSenary);
				$("#email2").val(branch.email2);
				$("#email3").val(branch.email3);
				$("#email4").val(branch.email4);
				$("#email5").val(branch.email5);
				$("#email6").val(branch.email6);

				$("#editButton").css("display", "block");
				$("#saveButton").prop("disabled", true);
				$("#saveButton").css("display", 'none');

				// Set parentCode dropdown based on branchType
				setParentCodeDropdown(branch.branchType, branch.parentCode);
			} else {

				Swal.fire({
					icon: "warning",
					title: "No branch found for selected branch code",
					text: "Please select correct branch code",
				});

				console.error("No Branch found or invalid response.");
			}
		},
		error: function(error) {
			console.error("Error fetching data:", error);
		}
	});
}

function setParentCodeDropdown(branchType, parentCode) {
	if (branchType === 'HEAD OFFICE') {
		// If branchType is HEAD OFFICE, disable and reset parentCode dropdown
		$("#parentCode").empty().append('<option value="0">--select--</option>').prop('disabled', true);
	} else {
		// Fetch parent branches based on branchType
		var url = "";
		if (branchType === 'ZONE') {
			url = 'getAllHeadOffice';
		} else if (branchType === 'BRANCH') {
			url = 'getAllHeadOfficeAndZones';
		}

		$.ajax({
			url: url,
			type: 'GET',
			success: function(response) {
				var branchList = response.payload;
				$("#parentCode").empty().append('<option value="0">--select--</option>');
				$.each(branchList, function(i, item) {
					$("#parentCode").append($('<option>', {
						value: item.ifsc,
						text: item.ifsc
					}));
				});
				$("#parentCode").prop('disabled', false).val(parentCode);
			},
			error: function(xhr, status, error) {
				console.error('Error fetching data from API:', error);
			}
		});
	}
}


/**
 * ---------------------------------------JS TO EDIT BRANCH
 * DETAILS-----------------------------------------*
 */


function editBranch() {
	var branchData = {
		branchCode: $("#branchCode").val(),
		bank: $("#bank").val(),
		branchDesc: $("#branchDesc").val(),
		branchType: $("#branchType").val(),
		contractPerson: $("#contractPerson").val(),
		mobile: $("#mobile").val(),
		telephone: $("#telephoneNo").val(),
		parentCode: $("#parentCode").val(),
		faxNo: $("#faxNo").val(),
		address1: $("#address1").val(),
		address2: $("#address2").val(),
		address3: $("#address3").val(),
		address4: $("#address4").val(),
		email: $("#email").val(),
		status: $("#status").val(),
		ifsc: $("#ifsc").val(),
		longitude: $("#longitude").val(),
		latitude: $("#latitude").val(),
		mobileSecondary: $("#mob2").val(),
		mobileTertiary: $("#mob3").val(),
		mobileQuaternary: $("#mob4").val(),
		mobileQuinary: $("#mob5").val(),
		mobileSenary: $("#mob6").val(),
		email2: $("#email2").val(),
		email3: $("#email3").val(),
		email4: $("#email4").val(),
		email5: $("#email5").val(),
		email6: $("#email6").val()
	};

	// Check if any changes were made
	if (JSON.stringify(originalBranchData) === JSON.stringify(branchData)) {
		Swal.fire({
			icon: 'info',
			title: 'No Changes Detected',
			text: 'You have not made any changes to the branch details.',
		});
		return; // Exit function if no changes
	}

	if (validateBranchFormData()) {
		Swal.fire({
			title: "Are you sure?",
			text: "Do you really want to update this branch?",
			icon: "warning",
			showCancelButton: true,
			confirmButtonColor: "#3085d6",
			cancelButtonColor: "#d33",
			confirmButtonText: "Yes, update it!"
		}).then((result) => {
			if (result.isConfirmed) {
				try {
					$.ajax({
						url: "editBranch",
						type: "POST",
						dataType: "json",
						contentType: "application/json",
						data: JSON.stringify(branchData),
						success: function(data) {
							try {
								Swal.fire({
									icon: 'success',
									title: 'Success!',
									text: 'Branch edited successfully.',
								}).then(function() {
									location.reload();
								});
							} catch (successError) {
								console.error(successError);
							}
						},
						error: function(error) {
							try {
								Swal.fire({
									icon: 'error',
									title: 'Error!',
									text: 'Failed to save Branch.',
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

/*-------------------------For Delete the BranchCode-------------------------------*/

function deleteBranch() {

	var branchCode = $("#branchCode").val();

	Swal.fire({
		title: 'Delete Confirmation',
		text: 'Are you sure you want to delete branch?',
		icon: 'warning',
		showCancelButton: true,
		confirmButtonColor: '#d33',
		cancleButtonColor: '3085d6',
		confirmButtonText: 'confirm',

	}).then((result) => {
		if (result.isConfirmed) {
			$.ajax({
				type: "DELETE",
				url: "deleteBranchesbyBranchCode",
				data: { branchCode: branchCode },
				success: function(data) {
					try {
						Swal.fire({
							icon: 'success',
							title: 'Success!',
							text: 'BranchCode Delete successfully.',
						}).then(function() {
							location.reload();
						});

					} catch (successError) {
						console.error(successError);
					}
				},
				error: function(error) {
					alert("error");
					try {
						Swal.fire({
							icon: 'error',
							title: 'Error!',
							text: 'Failed to Delete BranchCode.',
						});
						console.error(error);
					} catch (errorHandlingError) {
						console.error(errorHandlingError);
					}
				}
			});
		}

	});




}

/*--------------------------------------------------------JS FOR ENABLE BRANCHES----------------------------------------------------------------*/



$(document)
	.ready(
		function() {
			$('#branchType')
				.change(
					function() {
						var selectedBranchType = $(this)
							.val();
						if (selectedBranchType === 'HEAD OFFICE') {
							$('#parentCode').prop(
								'disabled', true)
								.val('0');
						} else if (selectedBranchType === 'ZONE') {
							$
								.ajax({
									url: 'getAllHeadOffice',
									type: 'GET',
									success: function(
										response) {

										console
											.log(response.payload)

										var branchList = response.payload;
										$(
											'#parentCode')
											.empty();
										$(
											'#parentCode')
											.append(
												$(
													'<option>',
													{
														value: '0',
														text: '--select--'
													}));
										$
											.each(
												branchList,
												function(
													i,
													item) {
													$(
														'#parentCode')
														.append(
															$(
																'<option>',
																{
																	value: item.ifsc,
																	text: item.ifsc

																}));
												});
										$(
											'#parentCode')
											.prop(
												'disabled',
												false);
									},
									error: function(
										xhr,
										status,
										error) {
										console
											.error(
												'Error fetching data from API:',
												error);
									}
								});
						} else if (selectedBranchType === 'BRANCH') {
							$
								.ajax({
									url: 'getAllHeadOfficeAndZones',
									type: 'GET',
									success: function(
										response) {
										console
											.log(response.payload);
										var branchList = response.payload;
										$(
											'#parentCode')
											.empty();
										$(
											'#parentCode')
											.append(
												$(
													'<option>',
													{
														value: '0',
														text: '--select--'
													}));
										$
											.each(
												branchList,
												function(
													i,
													item) {
													$(
														'#parentCode')
														.append(
															$(
																'<option>',
																{
																	value: item.ifsc,
																	text: item.ifsc
																		
																}));
												});
										$(
											'#parentCode')
											.prop(
												'disabled',
												false);
									},
									error: function(
										xhr,
										status,
										error) {
										console
											.error(
												'Error fetching data from API:',
												error);
									}
								});
						} else {

							$('#parentCode').prop(
								'disabled', false);
						}
					});
		});




$(document).ready(function () {
    // Initialize Select2
    $('#ifsc').select2({
        placeholder: "Branch Code",
        allowClear: true
    });

    // On branchType change
    $('#branchType').on('change', function () {
        var selectedBranchType = $(this).val();
        $('#ifsc').empty(); // Clear previous options

        if (selectedBranchType === "HEAD OFFICE") {
            fetchFgmo();
        } else if (selectedBranchType === "ZONE") {
            fetchFgmoForZone(); // No parent code at this point
        } else if (selectedBranchType === "BRANCH") {
            // Wait for parentCode selection, handled below
            $('#ifsc').empty().append('<option value="">Select</option>').val('');
        }
    });

    // Add change event for parentCode dropdown
    $('#parentCode').on('change', function () {
        var parentCode = $(this).val();
        var selectedBranchType = $('#branchType').val();
        if (parentCode && parentCode !== "0") {
            if (selectedBranchType === "ZONE") {
                fetchFgmoForZone(parentCode); // Pass selected parent code
            } else if (selectedBranchType === "BRANCH") {
                fetchBranchByZone(parentCode); // Pass selected parent code for BRANCH
            }
        } else {
            $('#ifsc').empty().append('<option value="">Select</option>').val('');
        }
    });

    function populateIfscOptions(dataArray) {
        var $dropdown = $('#ifsc');
        $dropdown.empty(); // Clear previous options before adding new ones
        $dropdown.append('<option value="">Select</option>');
        for (var i = 0; i < dataArray.length; i++) {
            var option = '<option value="' + dataArray[i] + '">' + dataArray[i] + '</option>';
            $dropdown.append(option);
        }
        $dropdown.val(''); // Reset selection
        $dropdown.trigger('change');
    }

    function fetchFgmo() {
        // Fetch FGMO data
        $.ajax({
            url: '/findAllFgmo',
            method: 'GET',
            success: function (res) {
                if (res.statusCode === 200 && res.payload) {
                    populateIfscOptions(res.payload);
                } else {
                    alert("Failed to fetch FGMO data.");
                }
            },
            error: function () {
                alert("Error fetching FGMO.");
            }
        });
    }

    function fetchFgmoForZone(parentCode) {
        if (parentCode) {
            // Fetch zones by parentCode (assumed to be IFSC or similar)
            $.ajax({
                url: 'findAllZoneNameByFgmo?fgmo=' + encodeURIComponent(parentCode),
                method: 'GET',
                success: function (res) {
                    if (res.statusCode === 200 && res.payload) {
                        populateIfscOptions(res.payload);
                    } else {
                        $('#ifsc').empty().append('<option value="">Select</option>').val('');
                        alert("Failed to fetch Zones.");
                    }
                },
                error: function () {
                    $('#ifsc').empty().append('<option value="">Select</option>').val('');
                    alert("Error fetching Zones.");
                }
            });
        } else {
            // Default: fetch first FGMO and then fetch zones for it
            $.ajax({
                url: '/findAllFgmo',
                method: 'GET',
                success: function (res) {
                    if (res.statusCode === 200 && res.payload && res.payload.length > 0) {
                        var fgmo = res.payload[0];
                        fetchZoneByFgmo(fgmo);
                    } else {
                        $('#ifsc').empty().append('<option value="">Select</option>').val('');
                        alert("Failed to fetch FGMO data.");
                    }
                },
                error: function () {
                    $('#ifsc').empty().append('<option value="">Select</option>').val('');
                    alert("Error fetching FGMO.");
                }
            });
        }
    }

    function fetchZoneByFgmo(fgmo) {
        $.ajax({
            url: 'findAllZoneNameByFgmo?fgmo=' + encodeURIComponent(fgmo),
            method: 'GET',
            success: function (res) {
                if (res.statusCode === 200 && res.payload) {
                    populateIfscOptions(res.payload);
                } else {
                    alert("Failed to fetch Zones.");
                }
            },
            error: function () {
                alert("Error fetching Zones.");
            }
        });
    }

    function fetchBranchByZone(zone) {
        $.ajax({
            url: '/findAllBranchByZoneName?branchCode=' + encodeURIComponent(zone),
            method: 'GET',
            success: function (res) {
                if (res.statusCode === 200 && res.payload) {
                    populateIfscOptions(res.payload);
                } else {
                    $('#ifsc').empty().append('<option value="">Select</option>').val('');
                    alert("Failed to fetch Branches.");
                }
            },
            error: function () {
                $('#ifsc').empty().append('<option value="">Select</option>').val('');
                alert("Error fetching Branches.");
            }
        });
    }
});
document.addEventListener("DOMContentLoaded", function () {
    let userRole = sessionStorage.getItem("userRole");

    if (userRole === "super admin") {
        document.getElementById("saveButton").style.display = "none";
        document.getElementById("editButton").style.display = "none";
        document.getElementById("deleteButton").style.display = "none";
        
        
    }
});