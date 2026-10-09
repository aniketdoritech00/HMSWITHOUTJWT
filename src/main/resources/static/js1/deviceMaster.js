/*---------------------------------------DataTable for devicde master--------------------*/
$(document).ready(function() {
	$('#deviceListTable').DataTable({
		responsive: true,
		paging: false,
		scrollCollapse: true,
		scrollY: '46vh',
		dom: 'Bfrtip',
		buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
		],

		// order: [[5, 'desc']]
	});
});


$(document).ready(function() {
	$('#deviceMasterModalTable').DataTable({
		responsive: true,
		paging: true,
		scrollY: '50vh',
		dom: 'Bfrtip',
		buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
		],

		
	});
});



    // Initialize tooltips
    $(function () {
        $('[data-toggle="tooltip"]').tooltip();
    });


function searchDeviceFromTable(deviceId) {
    $.ajax({
        type: "GET",
        url: "getDeviceAndZoneDetailsByDeviceId",
        data: { deviceId: deviceId },
        success: function(response) {
            if (response && response.statusCode === 200 && response.payload) {
                var device = response.payload;

                // Set common form values
                $("#editButton").addClass("visible").removeClass("hidden");
                $("#saveButton").prop("disabled", true).addClass("hidden").removeClass("visible");
                $("#flag").addClass("visible").removeClass("hidden");
                $("#messageOthers").addClass("visible").removeClass("hidden");
                $("#addNew").prop("disabled", true).addClass("hidden").removeClass("visible");
                $("#alertAccess").addClass("visible").removeClass("hidden");

                $("#deviceId").val(device.deviceId);
                $("#deviceName").val(device.deviceName);
                $("#deviceType").val(device.deviceType);
                $("#deviceSerialNo").val(device.deviceSerialNo);
                $("#deviceLocation").val(device.deviceLocation);
                $("#phoneNumber").val(device.phoneNumber);
                $("#protectionType").val(device.protectionType);
                $("#userNo").val(device.userNo);
                $("#supportNo").val(device.supportNo);
                $("#branchCode").val(device.branchCode);
                $("#status").val(device.status);
                $("#email1").val(device.email1);
                $("#productType").val(device.productType);
                $("#passcode").val(device.passcode);
                $("#address").val(device.address);
                $("#vendorName").val(device.vendorName);
                $("#vendorEmail").val(device.vendorEmail);

                var existingTable = document.getElementById("example");
                var dynamicContent = document.getElementById("dynamicContent");
                var saveButton = document.getElementById("saveButton");
                var deleteButton = document.getElementById("deleteButton");
                var siaSubmission = document.getElementById("siaSubmission");


                // Handle SIA device case
                if (device.deviceType === "SIA") {
                    existingTable.style.display = "none";
                    saveButton.classList.add("d-none");
                    deleteButton.classList.add("d-none");
                    siaSubmission.classList.remove("hidden");

                   if (device.siaProtocol && device.siaProtocol.length > 0) {
    var tableHTML = `
        <div class="table-responsive">
            <table class="table table-bordered">
                <thead class="tableStyle">
                    <tr>
                        <th>S.No</th>
                        <th>Zone Name</th>
                        <th>Description</th>
                        <th>Remarks</th>
                    </tr>
                </thead>
                <tbody class="scrollable-tbody tableStyle">`;

    for (var i = 0; i < device.siaProtocol.length; i++) {
        var item = device.siaProtocol[i];
        tableHTML += '<tr>' +
            '<td>' + (i + 1) + '<input type="hidden" class="sia-id" value="' + item.id + '" /></td>' +
    		'<td>' + item.command + '</td>' +
            '<td>' + item.message + '</td>' +
            '<td><input type="text" class="form-control form-control-sm" value="' + item.messageType + '" /></td>' +
            '</tr>';
    }

    tableHTML += `</tbody></table><hr/></div>`;
    dynamicContent.innerHTML = tableHTML;
} else {
    dynamicContent.innerHTML = "<p>No SIA data available.</p>";
}

                } else {
                    // For NON-SIA devices
                    existingTable.style.display = "table";
                    dynamicContent.innerHTML = "";
                    saveButton.classList.remove("d-none");
                    deleteButton.classList.remove("d-none");
                    siaSubmission.classList.add("hidden");

                    $("#zoneDetailsContainer").empty();
                    $("#example tbody").empty();

                    var zonesList = $("#zonesPayload").val().replace(/[\[\]]/g, '').split(',').map(item => item.trim());
                    var inputsList = $("#inputsPayload").val().replace(/[\[\]]/g, '').split(',').map(item => item.trim());

                    if (device.zoneList && device.zoneList.length > 0) {
                        device.zoneList.forEach(function(zone) {
                            var zoneDesiredNameOptions = zonesList.map(function(zoneName) {
                                return '<option value="' + zoneName + '"' + (zoneName === zone.zoneDesiredName ? ' selected' : '') + '>' + zoneName + '</option>';
                            }).join('');
                            var textColor = zone.primaryFlag === 'Y' ? 'red' : 'green';

                            var dynamicMessage = '';
                            var alertFlag = '';
                            if (device.dynamicConfigurationList) {
                                var dynamicConfig = device.dynamicConfigurationList.find(function(config) {
                                    return config.alertType === zone.alertType;
                                });
                                if (dynamicConfig) {
                                    dynamicMessage = dynamicConfig.message;
                                    alertFlag = dynamicConfig.alertFlag;
                                }
                            }

                            var newRow = '<tr>' +
                                '<td><select class="form-control form-control-sm">' + zoneDesiredNameOptions + '</select></td>' +
                                '<td><input type="text" class="form-control form-control-sm" value="' + zone.inputType + '" readonly/></td>' +
                                '<td><input type="text" class="form-control form-control-sm" value="' + zone.zoneOriginalName + '" readonly/></td>' +
                                '<td><input type="text" class="form-control form-control-sm" value="' + dynamicMessage + '" readonly/></td>' +
                                '<td><input type="text" class="form-control form-control-sm" value="' + zone.alertType + '" /></td>' +
                                '<td style="display:none;"><input type="text" class="form-control form-control-sm" value="' + zone.id + '" readonly/></td>' +
                                '<td><input type="text" class="form-control form-control-sm" value="' + (zone.primaryFlag === 'Y' ? 'Yes' : 'No') + '" style="color: ' + textColor + ';" readonly/></td>' +
                                '<td><input type="text" class="form-control form-control-sm" value="' + (alertFlag === 'Y' ? 'Yes' : 'No') + '" style="color: ' + textColor + ';" readonly/></td>' +
                                '<td><button type="button" class="btn btn-danger btn-sm ml-2" onclick="removeItemRow(this);"><i class="fa-regular fa-trash-can fa-lg"></i></button></td>' +
                                '</tr>';
                            $("#example tbody").append(newRow);
                        });
                    } else {
                        Swal.fire('Zone details are undefined.');
                    }
                }
            } else {
                Swal.fire('Device Not Found.');
            }
        },
        error: function(error) {
            console.error("Error fetching data:", error);
        }
    });
}



function searchByDeviceCode() {
	var deviceId = $("#searchInput").val();
	$.ajax({
		type: "GET",
		url: "getDeviceAndZoneDetailsByDeviceId",
		data: { deviceId: deviceId },
		success: function(response) {

			if (response && response.statusCode === 200 && response.payload) {
				var device = response.payload;

				$("#editButton").css("display", "block");
				$("#saveButton").prop("disabled", true);
				$("#saveButton").css("display", 'none');
				$("#flag").css("display", "block");
				$("#messageOthers").css("display", "block");
				$("#addNew").prop("disabled", true);
				$("#addNew").css("display", 'none');

				$("#deviceId").val(device.deviceId);
				$("#deviceName").val(device.deviceName);
				$("#deviceType").val(device.deviceType);
				$("#deviceSerialNo").val(device.deviceSerialNo);
				$("#deviceLocation").val(device.deviceLocation);
				$("#phoneNumber").val(device.phoneNumber);
				$("#protectionType").val(device.protectionType);
				$("#userNo").val(device.userNo);
				$("#supportNo").val(device.supportNo);
				$("#branchCode").val(device.branchCode);
				$("#status").val(device.status);
				$("#email1").val(device.email1);
				$("#email2").val(device.email2);
				$("#email3").val(device.email3);
				$("#address").val(device.address);
				$("#vendorName").val(device.vendorName);
				$("#vendorEmail").val(device.vendorEmail);
				$("#zoneDetailsContainer").empty();
				$("#example tbody").empty();

				var zonesList = $("#zonesPayload").val().replace(/[\[\]]/g, '').split(',').map(item => item.trim());
				var inputsList = $("#inputsPayload").val().replace(/[\[\]]/g, '').split(',').map(item => item.trim());

				if (device.zoneList && device.zoneList.length > 0) {
					device.zoneList.forEach(function(zone) {
						var zoneDesiredNameOptions = zonesList.map(function(zoneName) {
							return `<option value="${zoneName}" ${zoneName === zone.zoneDesiredName ? 'selected' : ''}>${zoneName}</option>`;
						}).join('');
						var textColor = zone.primaryFlag === 'Y' ? 'red' : 'green';

						var dynamicMessage = '';
						if (device.dynamicConfigurationList) {
							var dynamicConfig = device.dynamicConfigurationList.find(function(config) {
								return config.alertType === zone.alertType;
							});
							if (dynamicConfig) {
								dynamicMessage = dynamicConfig.message;
							}
						}

						var newRow = `
                            <tr>
                              <td>
                                    <select class="form-control form-control-sm">
                                        ${zoneDesiredNameOptions}
                                    </select>
                                </td>
                                <td><input type="text" class="form-control form-control-sm" value="${zone.inputType}" readonly/></td>
                                <td><input type="text" class="form-control form-control-sm" value="${zone.zoneOriginalName}" readonly/></td>
                                <td><input type="text" class="form-control form-control-sm" value="${dynamicMessage}" readonly/></td>
                                <td><input type="text" class="form-control form-control-sm" value="${zone.alertType}" /></td>
                                
                                <td style="display:none;"><input type="text" class="form-control form-control-sm" value="${zone.id}" readonly/></td>
                                <td><input type="text" class="form-control form-control-sm" value="${zone.primaryFlag === 'Y' ? 'Yes' : 'No'}" style="color: ${textColor};" readonly/></td>
                                <td><button type="button" class="btn btn-danger btn-sm ml-2" onclick="removeItemRow(this);"><i class="fa-regular fa-trash-can fa-lg"></i></button></td>
                             </tr>`;
						$("#example tbody").append(newRow);
					});
				} else {
					Swal.fire('Zone details are undefined.');
				}
			} else {
				Swal.fire('Device Not Found.');
			}
		},
		error: function(error) {
			console.error("Error fetching data:", error);
		}
	});
}


// function addNewItemIssueForEdit() {
// var rowCount = $("#example tbody tr").length;
//
// var tableBody = document.querySelector('#example tbody');
// var newRow = tableBody.insertRow();
//
// var zoneOriginalName = newRow.insertCell();
// var inputTypeCell = newRow.insertCell();
// var zoneDesiredName = newRow.insertCell();
// var alertTypeCell = newRow.insertCell();
// var actionsCell = newRow.insertCell();
// var addNewCell = newRow.insertCell();
//
// rowCount++;
//
// zoneOriginalName.innerHTML = `<input type="text" class="form-control
// form-control-sm" value="ZONE${rowCount}" />`;
//
// var inputsPayloadInput = document.getElementById('inputsPayload');
// var inputsPayload = inputsPayloadInput.value;
//
// var inputsPayloadList = inputsPayload.substring(1, inputsPayload.length -
// 1).split(', ');
//
// var selectOptionsInput = '';
// for (var i = 0; i < inputsPayloadList.length; i++) {
// selectOptionsInput += `<option
// value="${inputsPayloadList[i]}">${inputsPayloadList[i]}</option>`;
// }
// inputTypeCell.innerHTML = `<select class="form-control
// form-control-sm">${selectOptionsInput}</select>`;
//
// var zonesPayloadInput = document.getElementById('zonesPayload');
// var zonesPayload = zonesPayloadInput.value;
//
// var zonesList = zonesPayload.substring(1, zonesPayload.length - 1).split(',
// ');
//
// var selectOptions = '';
// for (var i = 0; i < zonesList.length; i++) {
// selectOptions += `<option value="${zonesList[i]}">${zonesList[i]}</option>`;
// }
// zoneDesiredName.innerHTML = `<select class="form-control
// form-control-sm">${selectOptions}</select>`;
//
// alertTypeCell.innerHTML = `
// <input type="text" placeholder="Alert Type" class="form-control
// form-control-sm" id="alertType${rowCount}" value="" />`;
//
// actionsCell.innerHTML = `
// <button type="button" class="btn btn-danger btn-sm ml-2"
// onclick="removeItemRow(this);">
// <i class="fa-regular fa-trash-can fa-lg"></i>
// </button>`;
//
// var addNewButton = document.createElement('button');
// addNewButton.setAttribute('type', 'button');
// addNewButton.classList.add('btn', 'btn-sm', 'ml-2');
// addNewButton.style.background = '#028BD7';
// addNewButton.style.color = '#ffff';
// addNewButton.setAttribute('onclick', 'addNewItemIssueForEdit()();');
// addNewButton.innerHTML = '<i class="fa-solid fa-square-plus fa-lg"></i>';
//
// addNewCell.appendChild(addNewButton);
// }




/** ************************************************************************************************* */
/* SEARCH BAR FOR DEVICE CODE */
/** ************************************************************************************************* */
const searchInput = document.getElementById('searchInput');
const suggestionDropdown = document.getElementById('suggestionDropdown');
let lastSearchTerm = '';
let lastFetchedData = [];

searchInput.addEventListener('input', function() {
	const searchTerm = searchInput.value.toLowerCase();
	if (searchTerm !== lastSearchTerm) {
		fetch(`getDeviceMasterByDeviceId?deviceId=${searchTerm}`)
			.then(response => response.json())
			.then(data => {
				console.log('Fetched Data:', data);
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
	console.log('Rendering Suggestions:', lastFetchedData);
	if (searchTerm.trim() === '') {
		suggestionDropdown.style.display = 'none';
		return;
	}
	const matchingSuggestions = lastFetchedData.filter(item => item.deviceId.toLowerCase().includes(searchTerm));
	if (matchingSuggestions.length > 0) {
		suggestionDropdown.innerHTML = '';
		matchingSuggestions.forEach(suggestion => {
			const suggestionItem = document.createElement('div');
			suggestionItem.classList.add('suggestionItem');
			suggestionItem.innerHTML = `<strong>${suggestion.deviceId}</strong> - ${suggestion.deviceName}`;
			suggestionItem.addEventListener('click', () => {
				searchInput.value = suggestion.deviceId;
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
 * ********--------------------------------------------- Dynamic Table
 * Creation------------------------------------- **********
 */
const radioValuesForRow = [];
const messageValuesForOtherRow = [];
const alertFlagsForOther=[];
$(document).ready(function() {
	initializeMultiSelect($('#framework'));
	populateOptions('framework');
	initializeMultiSelect($('#framework'));
	$('#framework').change(updateOtherSelects);
});

let rowCount = 0;

function initializeMultiSelect(element) {
	element.multiselect({
		nonSelectedText: 'Select Zone',
		enableFiltering: true,
		enableCaseInsensitiveFiltering: true,
		buttonWidth: '100%'
	});
}
function addNewItemIssue() {
	var tableBody = document.querySelector('#example tbody');
	var newRow = tableBody.insertRow();

	var inputDesiredNameCell = newRow.insertCell();
	var groupTypeCell = newRow.insertCell();
	var zoneCell = newRow.insertCell();
	var alertTypeCell = newRow.insertCell();
	var addNewCell = newRow.insertCell();
	var deletionCell = newRow.insertCell();

	rowCount++;

	inputDesiredNameCell.innerHTML = `<select name="inputDesiredName" class="form-control form-control-sm">
        <option value="CCTV">CCTV</option>
        <option value="SECURITY ALARM">Security Alarm</option>
        <option value="FIRE ALARM">Fire Alarm</option>
        <option value="ETL">ETL</option>
        <option value="BACS">BACS</option>
    </select>`;

	groupTypeCell.innerHTML = `<input type="text" class="form-control form-control-sm" value="" />`;

	var selectId = `framework${rowCount}`;
	zoneCell.innerHTML = `<select id="${selectId}" name="framework[]" multiple class="form-control custom-select"></select>`;

	populateOptions(selectId);
	initializeMultiSelect($(`#${selectId}`));

	var alertInputId = `alertType${rowCount}`;
	alertTypeCell.innerHTML = `<input type="text" id="${alertInputId}" placeholder="" class="form-control form-control-sm open-modal-input" value=""  />`;

	alertTypeCell.addEventListener('click', function() {
		const modalIds = generateDynamicModal(`myModal${rowCount}`, $(`#${selectId}`).val(), alertInputId);
		if (!$(`#${modalIds.modalId}`).hasClass('show')) {
			$(`#${modalIds.modalId}`).modal('show');
		}
	});

	var addNewButton = document.createElement('button');
	addNewButton.setAttribute('type', 'button');
	addNewButton.classList.add('btn', 'btn-sm', 'ml-2');
	addNewButton.style.background = '#028BD7';
	addNewButton.style.color = '#ffffff';
	addNewButton.setAttribute('onclick', 'addNewItemIssue();');
	addNewButton.innerHTML = '<i class="fa-solid fa-square-plus fa-lg"></i>';
	addNewCell.appendChild(addNewButton);

	deletionCell.innerHTML = `
        <button type="button" class="btn btn-danger btn-sm ml-2" onclick="removeItemRow(this);">
            <i class="fa-regular fa-trash-can fa-lg"></i>
        </button>`;

	$(`#${selectId}`).change(updateOtherSelects);

	updateOtherSelects();
}

function populateOptions(selectId) {
	const newSelectElement = document.getElementById(selectId);
	for (let i = 1; i <= 40; i++) {
		const option = document.createElement('option');
		option.value = `ZONE${i}`;
		option.textContent = `ZONE ${i}`;
		newSelectElement.appendChild(option);
	}
}

function updateOtherSelects() {
	const selectedZones = [];
	$('select[name="framework[]"]').each(function() {
		const selectedOptions = $(this).val();
		if (selectedOptions) {
			selectedZones.push(...selectedOptions);
		}
	});

	$('select[name="framework[]"]').each(function() {
		const currentSelect = $(this);
		const currentSelected = currentSelect.val() || [];

		currentSelect.find('option').each(function() {
			const optionValue = $(this).val();
			if (selectedZones.includes(optionValue) && !currentSelected.includes(optionValue)) {
				$(this).prop('disabled', true).prop('selected', false);
			} else {
				$(this).prop('disabled', false);
			}
		});

		currentSelect.multiselect('rebuild');
	});
}

function generateDynamicModal(modalId, selectedZones, alertInputId) {
	let existingModal = document.getElementById(modalId);
	if (existingModal) {
		return { modalId };
	}

	const tableId = `${modalId}_table`;
	const doneBtnId = `${modalId}_doneBtn`;
	const selectedOptionsTableId = `${modalId}_selectedOptionsTable`;

	let modalHTML = `
        <div class="modal bd-example-modal-lg" id="${modalId}">
            <div class="modal-dialog  modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title">Alert Details</h4>
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                    </div>
                    <div class="modal-body">
                        <table class="table" id="${tableId}">
                            <thead>
                                <tr>
                                    <th style="width: 10%;">Zone</th>
                                    <th style="width: 25%;">Alert Type</th>
                                    <th style="width: 45%;">Message</th>
                                    <th style="width: 10%;">Primary Zone</th>
                                    <th style="width: 10%;" class="alert-header">Alert</th>
                                </tr>
                            </thead>
                            <tbody id="${selectedOptionsTableId}">
                            </tbody>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" id="${doneBtnId}">Done</button>
                    </div>
                </div>
            </div>
        </div>
    `;

	document.body.insertAdjacentHTML('beforeend', modalHTML);

	const selectedOptionsTable = document.getElementById(selectedOptionsTableId);
	selectedZones.forEach(zone => {
		const row = document.createElement('tr');
		row.innerHTML = `
            <td>${zone}</td>
            <td><input type="text" class="form-control form-control-sm alert-type-input" value="" /></td>
            <td><input type="text" class="form-control form-control-sm message-input" value="" /></td>
            <td><input type="radio" name="primaryZone_${modalId}" value="${zone}" class="primary-zone-radio" /></td>
            <td class="alert-input"><div class="form-check"><input class="form-check-input" type="checkbox" value="" ></div></td>
        `;
		selectedOptionsTable.appendChild(row);
	});

	updateDoneButton(modalId, alertInputId);
	addRadioEventListeners(modalId);
	disableMessageFields();

	return {
		modalId: modalId,
		tableId: tableId,
		doneBtnId: doneBtnId,
		selectedOptionsTableId: selectedOptionsTableId
	};
}

function updateDoneButton(modalId, alertInputId) {
	const doneBtnId = `${modalId}_doneBtn`;
	const selectedOptionsTableId = `${modalId}_selectedOptionsTable`;

	$(`#${doneBtnId}`).off('click').on('click', function() {
		var cell2Values = [];
		var messageValues = [];
		var radioSelections = [];
		var zoneData = [];
        var alertFlags = []; 
		
		$(`#${selectedOptionsTableId} tr`).each(function(index) {
			var zone = $(this).find('td:first').text();
			var alertType = $(this).find('.alert-type-input').val();
			var message = $(this).find('.message-input').val();
			var isPrimaryZone = $(this).find('.primary-zone-radio:checked').val() ? 'Y' : 'N';
			var alertFlag = $(this).find('.form-check-input').is(':checked') ? 'Y' : 'N';

			//alert("Alert flag: " + alertFlag); // Debugging line

			// Push values to arrays
			cell2Values.push(alertType);
			messageValues.push(message);
			radioSelections.push(isPrimaryZone);
			alertFlags.push(alertFlag);
			
			// Create the zone data object
			zoneData.push({
				zone: zone,
				alertType: alertType,
				message: message,
				isPrimaryZone: isPrimaryZone,
				alertFlag: alertFlag
			});
		});

		console.log('Zone Data: ', zoneData);

		// Additional code for updating the input field and closing the modal
		$(`#${alertInputId}`).val(cell2Values.join(", "));

		radioValuesForRow[rowCount - 1] = radioSelections;
		messageValuesForOtherRow[rowCount - 1] = messageValues;
		alertFlagsForOther[rowCount - 1] = alertFlags;

				$(`#${modalId}`).modal('hide');
	});
}

function addRadioEventListeners(modalId) {
	const radios = document.querySelectorAll(`#${modalId} .primary-zone-radio`);
	radios.forEach(radio => {
		radio.addEventListener('change', function() {
			if (this.checked) {
				radios.forEach(r => {
					r.checked = false;
					r.value = 'N';
				});
				this.checked = true;
				this.value = 'Y';
			}
		});
	});
}

function disableMessageFields() {
    const productType = document.getElementById("productType").value;
    const messageFields = document.querySelectorAll('.message-input');
    const alertFields = document.querySelectorAll('.alert-input');
    const protectionType = document.querySelectorAll('#protectionType');
    const alertHeader = document.querySelector('.alert-header'); 

    if (productType === 'D') {
       
        messageFields.forEach(field => {
            field.disabled = true; 
            field.style.pointerEvents = 'none'; 
            field.style.backgroundColor = '#f9f9f9'; 
            field.style.color = '#a1a1a1'; 
        });

        
        protectionType.forEach(field => {
            field.style.pointerEvents = 'none'; 
            field.style.backgroundColor = '#f9f9f9'; 
        });

   
        alertFields.forEach(field => {
            field.style.visibility = 'hidden'; 
            field.style.pointerEvents = 'none'; 
        });

       
        if (alertHeader) alertHeader.style.display = 'none'; 
    } else if (productType === 'O') {
 
        protectionType.forEach(field => {
            field.style.pointerEvents = 'none'; 
            field.style.backgroundColor = '#f9f9f9'; 
        });

        alertFields.forEach(field => {
            field.style.visibility = ''; 
            field.style.pointerEvents = ''; 
        });

       
        if (alertHeader) alertHeader.style.display = ''; 
    } else {
        
        messageFields.forEach(field => {
            field.style.visibility = ''; 
            field.style.pointerEvents = '';
            field.style.backgroundColor = ''; 
        });

        protectionType.forEach(field => {
            field.style.visibility = ''; 
            field.style.pointerEvents = ''; 
            field.style.backgroundColor = ''; 
        });

        alertFields.forEach(field => {
            field.style.visibility = ''; 
            field.style.pointerEvents = ''; 
        });

      
        if (alertHeader) alertHeader.style.display = ''; 
    }
}



document.getElementById("productType").addEventListener("change", function() {
	disableMessageFields();
});


function removeItemRow(button) {
	var row = button.closest('tr');
	row.parentNode.removeChild(row);
	updateOtherSelects();
}

/** ***** EDIT DEVICE MASTER ******* */
/** ***** EDIT DEVICE MASTER ******* */
function editDeviceMaster(callback) {
	try {
		let formData = {
			deviceId: $('#deviceId').val(),
			deviceName: $('#deviceName').val(),
			deviceType: $('#deviceType').val(),
			deviceLocation: $('#deviceLocation').val(),
			phoneNumber: $('#phoneNumber').val(),
			userNo: $('#userNo').val(),
			protectionType: $('#protectionType').val() || '#',
			supportNo: $('#supportNo').val(),
			email1: $('#email1').val(),
			productType: $('#productType').val(),
			passcode: $('#passcode').val() || '*',
			address: $('#address').val(),
			branchCode: $('#branchCode').val(),
			deviceSerialNo: $('#deviceSerialNo').val(),
			vendorName: $('#vendorName').val(),
			vendorEmail: $('#vendorEmail').val()
		};


alert("EDIT-->"+JSON.stringify(formData))
		Swal.fire({
			title: "Are you sure?",
			text: "Do you want to update this device information?",
			icon: "warning",
			showCancelButton: true,
			confirmButtonColor: "#3085d6",
			cancelButtonColor: "#d33",
			confirmButtonText: "Yes, update it!"
		}).then((result) => {
			if (result.isConfirmed) {
				$.ajax({
					url: 'editDeviceMaster',
					type: 'POST',
					contentType: 'application/json',
					data: JSON.stringify(formData),
					success: function (data) {
						if (data.statusCode === 200) {
							if (callback && typeof callback === 'function') {
								callback(true);
							}
						} else {
							let errorMessage = "Error saving device master data!";
							if (data.statusCode === 302) {
								errorMessage = "Device already present in the database!";
							} else if (data.statusCode === 409) {
								errorMessage = "Device with the provided Device Serial Number is already paired with branch!";
							} else if (data.statusCode === 400) {
								errorMessage = "Please enter valid entries!";
							}
							Swal.fire({
								icon: 'error',
								title: errorMessage
							});
							if (callback && typeof callback === 'function') {
								callback(false);
							}
						}
					},
					error: function () {
						Swal.fire({
							icon: 'error',
							title: 'Error',
							text: 'An error occurred while saving device master data.'
						});
						if (callback && typeof callback === 'function') {
							callback(false);
						}
					}
				});
			}
		});
	} catch (e) {
		console.error("Exception occurred: ", e);
		Swal.fire({
			icon: 'error',
			title: 'Exception',
			text: 'An exception occurred while saving device master data. Please check your input and try again.'
		});
		if (callback && typeof callback === 'function') {
			callback(false);
		}
	}
}

function editDeviceZoneDetailsData(callback) {
	var tableBody = document.querySelector('#example tbody');
	var rows = tableBody.getElementsByTagName('tr');
	var itemList = [];

	for (var i = 0; i < rows.length; i++) {
		var row = rows[i];

		var deviceId = $('#deviceId').val();
	//	alert("Device ID: " + deviceId);

		var zoneDesiredNameInput = row.cells[0].querySelector('select');
		var zoneDesiredName = zoneDesiredNameInput ? zoneDesiredNameInput.value : '';
		//alert("Zone Desired Name: " + zoneDesiredName);

		var inputTypeInput = row.cells[1].querySelector('input[type="text"]');
		var inputType = inputTypeInput ? inputTypeInput.value.trim() : '';
		//alert("Input Type: " + inputType);

		var zoneOriginalNameInput = row.cells[2].querySelector('input[type="text"]');
		var zoneOriginalName = zoneOriginalNameInput ? zoneOriginalNameInput.value : '';
//		alert("Zone Original Name: " + zoneOriginalName);

		var alertTypeInput = row.cells[4].querySelector('input[type="text"]');
		var alertType = alertTypeInput ? alertTypeInput.value : '';
	//	alert("Alert Type: " + alertType);

		var zoneIdInput = row.cells[5].querySelector('input[type="text"]');
		var zoneId = zoneIdInput ? zoneIdInput.value : '';
		//alert("Zone ID: " + zoneId);

		var messageInput = row.cells[3].querySelector('input[type="text"]');
		var message = messageInput ? messageInput.value : '';
//		alert("Message: " + message);

		var alertFlagInput = row.cells[7].querySelector('input[type="text"]');
		var alertFlag = alertFlagInput ? alertFlagInput.value.trim() : '';
		//alert("Alert Flag: " + alertFlag);

		if (zoneId || zoneOriginalName || zoneDesiredName) {
			var item = {
				id: zoneId,
				deviceId: deviceId,
				zoneOriginalName: zoneOriginalName,
				inputType: inputType,
				zoneDesiredName: zoneDesiredName,
				alertType: alertType,
				alertFlag: alertFlag,
				message: message
				 // Add the alertFlag here
			};

			//alert("Saving Item: " + JSON.stringify(item));
			itemList.push(item);
		}
	}

	if (itemList.length > 0) {
		//alert("Final Item List: " + JSON.stringify(itemList));

		var apiUrl = 'editMultipleDeviceZoneDetails';
		$.ajax({
			url: apiUrl,
			type: 'POST',
			contentType: 'application/json',
			data: JSON.stringify(itemList),
			success: function(data) {
				if (data.statusCode == 200) {
					//alert("Save Success!");
					if (callback && typeof callback === 'function') {
						callback(true);
					}
				} else {
					//alert("Error saving device zone details data!");
					Swal.fire({
						icon: 'error',
						title: 'Error saving device zone details data!',
					});
					if (callback && typeof callback === 'function') {
						callback(false);
					}
				}
			},
			error: function(error) {
				alert("Error occurred while saving data.");
				Swal.fire({
					icon: 'error',
					title: 'Error',
					text: 'An error occurred while saving device zone details data.',
				});
				if (callback && typeof callback === 'function') {
					callback(false);
				}
			}
		});
	} else {
		alert("No items to save for device zone details.");
		Swal.fire({
			icon: 'warning',
			title: 'Warning',
			text: 'No items to save for device zone details.',
		});
		if (callback && typeof callback === 'function') {
			callback(false);
		}
	}
}

function editDeviceData() {
    try {
        if (validateFormFields()) {
            const deviceType = $('#deviceType').val();

            editDeviceMaster(function(successDeviceMaster) {
                if (successDeviceMaster) {
                    if (deviceType === 'SIA') {
                        editSIAProtocolData(function(successSIA) {
                            if (successSIA) {
                                Swal.fire({
                                    icon: 'success',
                                    title: 'SIA device updated successfully!',
                                }).then(() => {
                                    location.reload(true);
                                });
                            }
                        });
                    } else {
                        editDeviceZoneDetailsData(function(successDeviceZoneDetails) {
                            if (successDeviceZoneDetails) {
                                Swal.fire({
                                    icon: 'success',
                                    title: 'Data edit successfully!',
                                }).then(() => {
                                    location.reload(true);
                                });
                            } else {
                                Swal.fire({
                                    icon: 'error',
                                    title: 'Error saving deviceZoneDetails data!',
                                });
                            }
                        });
                    }
                }
            });
        }
    } catch (e) {
        Swal.fire({
            icon: 'error',
            title: 'Exception',
            text: 'An exception occurred. Please check your input and try again.',
        });
    }
}


function editSIAProtocolData(callback) {
    const tableRows = document.querySelectorAll('#dynamicContent table tbody tr');
    const deviceId = $('#deviceId').val();
    const siaList = [];

     tableRows.forEach((row) => {
        // Get the ID from the hidden input inside the first column
        const idInput = row.cells[0].querySelector('input[type="hidden"]');
        const id = idInput ? idInput.value : null;

        const command = row.cells[1].textContent.trim();
        const message = row.cells[2].textContent.trim();
        const messageTypeInput = row.cells[3].querySelector('input[type="text"]');
        const messageType = messageTypeInput ? messageTypeInput.value.trim() : '';

        if (id && command && message) {
            siaList.push({
                id: id,
                deviceId: deviceId,
                command: command,
                message: message,
                messageType: messageType
            });
        }
    });


alert(JSON.stringify(siaList))
    if (siaList.length > 0) {
        $.ajax({
            url: 'UpdateSiaProtocols', // Make sure your backend supports this endpoint
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(siaList),
            success: function (data) {
                if (data.statusCode === 200) {
                    if (callback && typeof callback === 'function') {
                        callback(true);
                    }
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: 'Error saving SIA data!',
                    });
                    if (callback && typeof callback === 'function') {
                        callback(false);
                    }
                }
            },
            error: function () {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'An error occurred while saving SIA protocol data.',
                });
                if (callback && typeof callback === 'function') {
                    callback(false);
                }
            }
        });
    } else {
        Swal.fire({
            icon: 'warning',
            title: 'Warning',
            text: 'No SIA data to save.',
        });
        if (callback && typeof callback === 'function') {
            callback(false);
        }
    }
}





/** ***** SAVE DEVICE MASTER ******* */
$(document).ready(function() {
	$('#branchCode').change(function() {
		var branchCode = $(this).val();
		$.ajax({
			url: 'checkBranch',
			type: 'GET',
			data: { branchCode: branchCode },
			success: function(data) {
				if (data.statusCode == 200) {
					
					
					 
               // $("#editButton").prop("disabled", true).addClass("hidden").removeClass("visible");
					Swal.fire({
						icon: 'error',
						title: 'Error',
						text: 'Device already exists for this branch. Please select another branch.',
					}).then(() => {
                    // Reset the dropdown selection after the user clicks "OK"
                    $('#branchCode').val('');
                });
				}
				//else{
					//$("#editButton").addClass("visible").removeClass("hidden");
					
				//}
			},
			error: function(error) {
				Swal.fire({
					icon: 'error',
					title: 'Error',
					text: 'An error occurred while checking the branch.',
				});
			}
		});
	});
});

function saveDeviceMaster(callback) {
	try {
		if (!validateFormFields()) {
			return;
		}

		var formData = {
			deviceId: $('#deviceId').val(),
			deviceName: $('#deviceName').val(),
			deviceType: $('#deviceType').val(),
			deviceLocation: $('#deviceLocation').val(),
			phoneNumber: $('#phoneNumber').val(),
			userNo: $('#userNo').val(),
			protectionType: $('#protectionType').val(),
			supportNo: $('#supportNo').val(),
			email1: $('#email1').val(),
			productType: $('#productType').val(),
			passcode: $('#passcode').val(),
			address: $('#address').val(),
			branchCode: $('#branchCode').val(),
			deviceSerialNo: $('#deviceSerialNo').val(),
			vendorName: $('#vendorName').val(),
			vendorEmail: $('#vendorEmail').val()
		};

alert(JSON.stringify(deviceZoneDetails))
		$.ajax({
			url: 'addDeviceMaster',
			type: 'POST',
			contentType: 'application/json',
			data: JSON.stringify(formData),
			success: function(data) {
				if (data.statusCode == 200) {
					Swal.fire({
						icon: 'success',
						title: 'Success',
						text: 'Device added successfully!',
					});
					if (callback && typeof callback === 'function') {
						callback(true);
					}
				} else if (data.statusCode == 302) {
					Swal.fire({
						icon: 'error',
						title: 'Device already present in the database!',
					});
					if (callback && typeof callback === 'function') {
						callback(false);
					}
				} else if (data.statusCode == 409) {
					Swal.fire({
						icon: 'error',
						title: 'Device with the provided Device Serial Number is already paired with a branch!',
					});
					if (callback && typeof callback === 'function') {
						callback(false);
					}
				} else if (data.statusCode == 400) {
					Swal.fire({
						icon: 'error',
						title: 'Please enter valid entries!',
					});
					if (callback && typeof callback === 'function') {
						callback(false);
					}
				} else {
					Swal.fire({
						icon: 'error',
						title: 'Error saving Device Master data!',
					});
					if (callback && typeof callback === 'function') {
						callback(false);
					}
				}
			},
			error: function(error) {
				Swal.fire({
					icon: 'error',
					title: 'Error',
					text: 'An error occurred while saving device data.',
				});
				if (callback && typeof callback === 'function') {
					callback(false);
				}
			}
		});
	} catch (e) {
		Swal.fire({
			icon: 'error',
			title: 'Exception',
			text: 'An exception occurred while saving device data. Please check your input and try again.',
		});
		if (callback && typeof callback === 'function') {
			callback(false);
		}
	}
}

function validateFormFields() {

	return true;
}

const deviceZoneDetails = [];
function saveDeviceZoneDetailsData(callback) {
	
	const deviceId = $('#deviceId').val();
	const uniqueCombinations = new Set();

	function sendRequest(data) {
		// alert("hlo-------->>>>>>>>"+JSON.stringify(data));

		console.log(JSON.stringify(data));
		$.ajax({
			type: 'POST',
			url: 'addMultipleDeviceZoneDetails',
			contentType: 'application/json',
			data: JSON.stringify(data),
			success: function(data) {
				if (data.statusCode == 200) {
					if (callback && typeof callback === 'function') {
						callback(true);
					}
				} else {
					Swal.fire({
						icon: 'error',
						title: 'Error saving deviceZoneDetails data!',
					});
					if (callback && typeof callback === 'function') {
						callback(false);
					}
				}
			},
			error: function(error) {
				Swal.fire({
					icon: 'error',
					title: 'Error',
					text: 'An error occurred while saving deviceZoneDetails data.',
				});
				if (callback && typeof callback === 'function') {
					callback(false);
				}
			}
		});
	}

	function processDetails(zone, desiredName, alertType, inputType, primaryFlag, messageFieldValue,alertFlag) {
		// alert("messageValuesForOtherRow-->>"+JSON.stringify(messageValuesForOtherRow))
		const combinationKey = `${deviceId}_${zone}_${desiredName}_${alertType}_${inputType}_${primaryFlag}_${alertFlag}`;

		if (!uniqueCombinations.has(combinationKey)) {
			uniqueCombinations.add(combinationKey);
			const detail = {
				id: '',
				deviceId: deviceId,
				zoneOriginalName: zone,
				zoneDesiredName: desiredName,
				alertType: alertType,
				inputType: inputType,
				primaryFlag: primaryFlag,
				message: messageFieldValue,
				alertFlag: alertFlag 
			};
			deviceZoneDetails.push(detail);
			 //alert(JSON.stringify(deviceZoneDetails));
		}
	}


	const radioValues = [];
	const alertFlags =[];
	$('#selectedOptionsTable tr').each(function() {
		var isPrimaryZone = $(this).find('input.primary-zone-radio').is(':checked') ? 'Y' : 'N';
		radioValues.push(isPrimaryZone);
		var isChecked = $(this).find('input.checkbox-alert:checked').length > 1 ? 'Y' : 'N';
		       alertFlags.push(isChecked); 
	});

	const firstRow = $('#example tbody tr:first');
	const firstRowDesiredName = firstRow.find('select[name="inputType"]').val();
	const firstRowAlertTypes = inputFieldValues;
	const selectedZonesFirstRow = firstRow.find('select[name="framework[]"]').val();
	const firstRowInputType = firstRow.find('input[type="text"]').val();

	if (selectedZonesFirstRow && firstRowAlertTypes) {
		for (let i = 0; i < Math.min(selectedZonesFirstRow.length, firstRowAlertTypes.length); i++) {
			const zone = selectedZonesFirstRow[i];
			const alertType = firstRowAlertTypes[i];
			const primaryFlagValue = radioValues[i] === 'Y' ? 'Y' : 'N';
			const messageFieldValue = messageValues[i]; // Fetch message field value
			const alertFlag = alertCheckbox[i];
			 //alert("Alert value "+alertFlag);
			processDetails(zone, firstRowDesiredName, alertType, firstRowInputType, primaryFlagValue, messageFieldValue,alertFlag);
		}
	}
	// alert("radioValues"+JSON.stringify(radioValues))

	// Process details for subsequent rows
	$('#example tbody tr').not(':first').each(function(index) {
		const row = $(this);
		const selectedZones = row.find('select[name="framework[]"]').val();
		const desiredName = row.find('select[name="inputDesiredName"]').val();
		const alertTypes = row.find('.open-modal-input').val().split(',').map(a => a.trim());
		const inputType = row.find('input[type="text"]').val();

		if (selectedZones && alertTypes) {
			for (let i = 0; i < Math.min(selectedZones.length, alertTypes.length); i++) {
				const zone = selectedZones[i];
				const alertType = alertTypes[i];
				const primaryFlagValue = radioValuesForRow[index][i] === 'Y' ? 'Y' : 'N'; // Adjust
			
				const messageFieldValue = messageValuesForOtherRow[index][i]; 
				const alertFlagForOther = alertFlagsForOther[index][i] === 'Y' ? 'Y' : 'N';;
				
			//	alert("alertFlagForOther-->>"+alertFlagForOther)
				

				processDetails(zone, desiredName, alertType, inputType, primaryFlagValue, messageFieldValue,alertFlagForOther);
			}
		}
	});


	console.log(JSON.stringify(deviceZoneDetails));

	sendRequest(deviceZoneDetails);
}

function validateZones() {
	// Check if at least one zone is selected
	let isZoneSelected = false;
	$('#example tbody tr').each(function() {
		const selectedZones = $(this).find('select[name="framework[]"]').val();
		if (selectedZones && selectedZones.length > 0) {
			isZoneSelected = true;
			return false; 
		}
	});
	
	return isZoneSelected;
}

function saveDeviceData() {
	try {
		if (validateFormFields()) {
			if (!validateZones()) {
				Swal.fire({
					icon: 'error',
					title: 'Please add zones',
					text: 'At least one zone must be selected to save the device.'
				});
				return; // Stop execution if no zones are selected
			}
			
			// Save device master and zone details if zones are selected
			saveDeviceMaster(function(successDeviceMaster) {
				if (successDeviceMaster) {
					saveDeviceZoneDetailsData(function(successDeviceZoneDetails) {
						if (successDeviceZoneDetails) {
							Swal.fire({
								icon: 'success',
								title: 'Data saved successfully!',
							}).then(() => {
								location.reload(true);
							});
						} else {
							Swal.fire({
								icon: 'error',
								title: 'Error saving deviceZoneDetails data!',
							});
						}
					});
				}
			});
		}
	} catch (e) {
		Swal.fire({
			icon: 'error',
			title: 'Exception',
			text: 'An exception occurred. Please check your input and try again.',
		});
	}
}


// <<<==================Validate all the data 
function validateFormFields() {
	var deviceId = $('#deviceId').val();
	var deviceName = $('#deviceName').val();
	var deviceType = $('#deviceType').val();
	var deviceLocation = $('#deviceLocation').val();
	var phoneNumber = $('#phoneNumber').val();
	var userNo = $('#userNo').val();
	var protectionType = $('#protectionType').val();
	var supportNo = $('#supportNo').val();
	var email1 = $('#email1').val();
	var productType = $('#productType').val();
	var passcode = $('#passcode').val();
	var address = $('#address').val();
	var branchCode = $('#branchCode').val();

	var blankFields = [];

	if (!deviceId) {
		blankFields.push('Device ID');
	}
	if (!deviceName) {
		blankFields.push('Device Name');
	}
	if (!deviceType) {
		blankFields.push('Device Type');
	}
	if (!deviceLocation) {
		blankFields.push('Device Location');
	}
	if (!isValidPhoneNumber(phoneNumber)) {
		blankFields.push('Phone Number (10 or 13 digits)');
	}
	if (!isValidEmail(email1)) {
		blankFields.push('Email 1');
	}
	
	if (!address) {
		blankFields.push('Address');
	}
	if (!branchCode) {
		blankFields.push('Branch Code');
	}
	if (!userNo) {
		blankFields.push('User No');
	}
	if (!protectionType) {
		blankFields.push('Protection Type');
	}

	/*if (blankFields.length > 0) {
		var warningMessage = 'Please fill or correct the following fields: ' + blankFields.join(', ');

		Swal.fire({
			icon: 'warning',
			title: 'Warning',
			text: warningMessage,
		});

		return false;
	}*/

	return true;
}

// <<<==========Check that phone number is valid or invalid @Author Aniket
// mishra ============>>>
function isValidPhoneNumber(phoneNumber) {
	return /^\d{10}$/.test(phoneNumber) || /^\d{13}$/.test(phoneNumber);
}
// <<<==========Check that email is valid or invalid @Authod Aniket
// mishra============>>>
function isValidEmail(email) {
	var emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
	return emailPattern.test(email);
}

// <<<========== Document ready function to initialize event
// listeners==========>>>
$(document).ready(function() {

	$('#phoneNumber').on('blur', function() {
		var phoneNumber = $(this).val();
		if (phoneNumber && !isValidPhoneNumber(phoneNumber)) {
			$('#phoneError').text('Invalid!!').show();
		} else {
			$('#phoneError').hide();
		}
	});

	$('#email1').on('blur', function() {
		var email = $(this).val();
		var emailErrorId = $(this).attr('id') + 'Error';
		if (email && !isValidEmail(email)) {
			$('#' + emailErrorId).text('Invalid!!').show();
		} else {
			$('#' + emailErrorId).hide();
		}
	});
});


/** *** DELETE DEVICE MASTER BY DEVICE ID **** */
function deleteDevice() {
    var deviceId = $("#deviceId").val();
    var deviceName = $("#deviceName").val();
    
    //alert(deviceId)
    
    
    if (!deviceName) {
        Swal.fire({
            icon: "warning",
            title: "No Device Selected",
            text: "Please select a device to delete before proceeding.",
        });
        return;
    }

    Swal.fire({
        title: "Are you sure?",
        text: "This action cannot be undone!",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#d33",
        cancelButtonColor: "#3085d6",
        confirmButtonText: "Yes, delete it!"
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                type: "DELETE",
                url: "deleteDeviceMasterByDeviceId?deviceId=" + deviceId,
                success: function(response) {
                    if (response.statusCode === 200) {
                        Swal.fire({
                            icon: "success",
                            title: "Device Deleted Successfully.",
                            text: response.message
                        }).then(() => {
                            window.location.reload(true);
                        });
                    } else {
                        Swal.fire({
                            icon: "error",
                            title: "Error",
                            text: response.message
                        });
                    }
                },
                error: function(error) {
                    console.error("Error deleting device:", error);
                    Swal.fire({
                        icon: "error",
                        title: "Error",
                        text: "An error occurred while deleting the device."
                    });
                }
            });
        }
    });
}



/*------------------------------------------Multiple Dropdown Select from Option-------------------------------------------------------------*/



$(document).ready(function() {
	$('#framework').multiselect({
		nonSelectedText: 'Select Zone',
		enableFiltering: true,
		enableCaseInsensitiveFiltering: true,
		buttonWidth: '250px'
	});

});


const selectElement = document.getElementById('framework');

for (let i = 1; i <= 40; i++) {
	const option = document.createElement('option');
	option.value = `ZONE${i}`;
	option.textContent = `Zone ${i}`;
	selectElement.appendChild(option);
}


/*-----------------------------------------------Modal Open for Zone-----------------------------------------------------------------*/

var inputFieldValues = [];
var radioValues = [];
var messageValues = [];
var alertCheckbox = [];

function checkPrimaryZoneSelection() {
    var anySelected = $('#selectedOptionsTable input.primary-zone-radio:checked').length > 0;
    $('#doneBtn').prop('disabled', !anySelected);
}

function populateModalTable(inputValues, radioValues, messageValues, checkboxValues) {
    var selectElement = document.getElementById("framework");
    var selectedOptions = [];

    for (var i = 0; i < selectElement.options.length; i++) {
        if (selectElement.options[i].selected) {
            selectedOptions.push(selectElement.options[i].textContent);
        }
    }

    var tableBody = document.querySelector("#selectedOptionsTable");
    tableBody.innerHTML = "";

    for (var i = 0; i < selectedOptions.length; i++) {
        var row = tableBody.insertRow(i);

        var cell1 = row.insertCell(0);
        cell1.textContent = selectedOptions[i];

        var cell2 = row.insertCell(1);
        var inputField = document.createElement("input");
        inputField.type = "text";
        inputField.className = "form-control form-control-sm alert-type-input";
        inputField.value = inputValues[i] || '';
        cell2.appendChild(inputField);

        var cell3 = row.insertCell(2);
        var messageField = document.createElement("input");
        messageField.type = "text";
        messageField.className = "form-control form-control-sm message-input";
        messageField.value = messageValues[i] || '';
        cell3.appendChild(messageField);

        var productType = document.getElementById("productType").value;
        messageField.disabled = productType === 'D';

        var cell4 = row.insertCell(3);
        var primaryCell = document.createElement("div");
        primaryCell.className = "form-check";

        var yesRadio = document.createElement("input");
        yesRadio.type = "radio";
        yesRadio.name = "primaryZone";
        yesRadio.value = selectedOptions[i];
        yesRadio.className = "form-check-input primary-zone-radio";

        if (radioValues[i] === "Y") {
            yesRadio.checked = true;
        }

        primaryCell.appendChild(yesRadio);
        cell4.appendChild(primaryCell);

        var cell5 = row.insertCell(4);
        var checkBoxCell = document.createElement("div");
        checkBoxCell.className = "form-check alert-input";

        var checkBox = document.createElement("input");
        checkBox.type = "checkbox";
        checkBox.className = "form-check-input checkbox-alert";

        if (checkboxValues[i]) {
            checkBox.checked = true;
        }

        checkBoxCell.appendChild(checkBox);
        cell5.appendChild(checkBoxCell);
    }

    checkPrimaryZoneSelection();

    // Hides the "Alert" column when protectionType is 'D'
    var alertHeader = document.querySelector('th.alert-header');
    var alertCells = document.querySelectorAll('.alert-input');

    if (productType === 'D') {
        alertHeader.style.display = 'none';
        alertCells.forEach(cell => {
            cell.style.display = 'none';
        });
    } else {
        alertHeader.style.display = '';
        alertCells.forEach(cell => {
            cell.style.display = '';
        });
    }

    $('#myModal').modal('show');
}


document.getElementById("openModalInput").addEventListener("click", function () {
    populateModalTable(inputFieldValues, radioValues, messageValues, alertCheckbox);
});

$('#doneBtn').click(function () {
    var cell2Values = [];
    var radioSelections = [];
    var messageFieldValues = [];
    var checkboxSelections = [];

    $('#selectedOptionsTable tr').each(function (index) {
        var cell2Input = $(this).find('input.alert-type-input').val();
        cell2Values.push(cell2Input);

        var messageFieldInput = $(this).find('input.message-input').val();
        messageFieldValues.push(messageFieldInput);

        var isPrimaryZone = $(this).find('input.primary-zone-radio:checked').length > 0 ? 'Y' : 'N';
        radioSelections.push(isPrimaryZone);

        var isChecked = $(this).find('input.checkbox-alert:checked').length > 0 ? 'Y' : 'N';
        checkboxSelections.push(isChecked);
    });

    inputFieldValues = cell2Values;
    $('#openModalInput').val(cell2Values.join(", "));
    radioValues = radioSelections;
    messageValues = messageFieldValues;
    alertCheckbox = checkboxSelections;
    //alert(alertCheckbox)
    $('#myModal').modal('hide');
});

$('#myModal').on('hidden.bs.modal', function () {
    $('#selectedOptionsTable input:not(.primary-zone-radio)').val('');
    $('#selectedOptionsTable input.message-input').prop('disabled', false);
    $('#selectedOptionsTable input[type="radio"]').prop('disabled', false);
    $('#selectedOptionsTable input[type="checkbox"]').prop('checked', false);
    $('#doneBtn').prop('disabled', true);
});

$('#selectedOptionsTable').on('click', 'input.primary-zone-radio', function () {
    var row = $(this).closest('tr');
    $('#selectedOptionsTable input.primary-zone-radio').prop('disabled', false);
    //$('#selectedOptionsTable input.primary-zone-radio').not(row.find('input.primary-zone-radio')).prop('disabled', true);
    checkPrimaryZoneSelection();
});




//
/* Save Data From Excel To Database - @Author = Aniket Mishra            */
//

function uploadFile() {
	var form = document.getElementById('uploadForm');
	var formData = new FormData(form);

	fetch('/importExcel', {
		method: 'POST',
		body: formData
	})
	.then(response => {
		if (response.status === 204) {
			alert("All rows imported successfully. No failed data.");
			return null;
		} else if (response.ok) {
			return response.blob();
		} else {
			throw new Error("Import failed");
		}
	})
	.then(blob => {
		if (blob) {
			const url = window.URL.createObjectURL(blob);
			const a = document.createElement('a');
			a.href = url;
			a.download = "failed_rows.xlsx";
			document.body.appendChild(a);
			a.click();
			a.remove();
			window.URL.revokeObjectURL(url);
		}
	})
	.catch(error => {
		console.error('Error:', error);
		alert("Something went wrong during import.");
	});
}

//
/* Export Data from Data base to Excel - @Author = Aniket Mishra            */
//
function exportDeviceMaster() {
	const xhr = new XMLHttpRequest();
	xhr.open("GET", "./exportExcel", true);
	xhr.responseType = "blob";

	xhr.onload = function() {
		if (xhr.status === 200) {
			const blob = new Blob([xhr.response], { type: "application/vnd.ms-excel" });
			const url = window.URL.createObjectURL(blob);
			const a = document.createElement("a");
			a.href = url;
			a.download = "DemoExcelFile.xlsx";
			document.body.appendChild(a);
			a.click();
			window.URL.revokeObjectURL(url);
			document.body.removeChild(a);
		} else {
			console.error('Error downloading file:', xhr.statusText);
		}
	};

	xhr.onerror = function() {
		console.error('Request failed');
	};

	xhr.send();
}








$('#deviceSerialNo').on('input', function() {
    var deviceSerialNo = $(this).val().trim();
    if (deviceSerialNo !== '') {
        $.ajax({
            url: 'checkDevice', // Replace with your actual endpoint
            type: 'GET',
            data: { serialNo: deviceSerialNo },
            success: function(data) {
                if (data.statusCode == 200) {
					
                    Swal.fire({
                        icon: 'error',
                        title: 'Duplicate Serial Number',
                        text: 'This device serial number already exists. Please enter a different one.',
                    }).then(() => {
                        $('#deviceSerialNo').val(''); // Clear the input after alert
                    });
                }
            },
            error: function() {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'An error occurred while checking the serial number. Please try again.',
                });
            }
        });
    }
});
