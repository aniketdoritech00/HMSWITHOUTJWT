
function loadSiaDeviceDetails() {
		//alert("Enter")
		const branchCode = $('#loggedInBranch').val(); 

        const apiUrl = "./getAllDeviceOfSia?branchCode=" + branchCode;

        $.ajax({
            url: apiUrl,
            method: "GET",
            success: function (data) {
                const tbody = $("#siaDeviceTable tbody");
                tbody.empty();

                if (data.payload && Array.isArray(data.payload)) {
                    data.payload.forEach(function (device) {
                        const row = "<tr>" +
                            "<td>" + device.branchName + "</td>" +
                            "<td>" + device.branchAddress + "</td>" +
                            "<td>" + device.branchIfsc + "</td>" +
                            "<td>" + device.branchNumber + "</td>" +
                            "<td>" + device.devicePanelSerialNo + "</td>" +
                            "<td>" + device.deviceType + "</td>" +
                            "</tr>";
                        tbody.append(row);
                    });
                } else {
                    const noDataRow = "<tr><td colspan='6' class='text-center'>No data found</td></tr>";
                    tbody.append(noDataRow);
                }
            },
            error: function (xhr, status, error) {
                console.error("Error fetching SIA devices:", error);
                const tbody = $("#siaDeviceTable tbody");
                const errorRow = "<tr><td colspan='6' class='text-center text-danger'>Failed to load data</td></tr>";
                tbody.html(errorRow);
            }
        });
    }
/**----------------------------------------------------------JS For LiveStreaming------------------------------------------------------------------**/


function openVideoStream() {

	window.open("rtsp://192.168.1.22:554", "_blank");
}




function formatDateTime(dateTimeString) {
	var dateTime = new Date(dateTimeString); // Convert to Date object
	var formattedDate = [
		dateTime.getDate().toString().padStart(2, '0'), // Day
		(dateTime.getMonth() + 1).toString().padStart(2, '0'), // Month
		dateTime.getFullYear() // Year
	].join('-'); // Join date components

	var formattedTime = [
		dateTime.getHours().toString().padStart(2, '0'), // Hours
		dateTime.getMinutes().toString().padStart(2, '0'), // Minutes
		dateTime.getSeconds().toString().padStart(2, '0') // Seconds
	].join(':');

	return formattedDate + ' ' + formattedTime; // Combine date and time
}


/*-------------------------------------------------- Current Month function for PieChart----------------------------------------------------- */


var currentDate = new Date();


var currentMonth = currentDate.toISOString().slice(0, 7);


document.getElementById("monthFilter").value = currentMonth;


var dropdownItems = document.querySelectorAll('.changeTextofZones');


dropdownItems.forEach(function(item) {
	item.addEventListener('click', function() {

		var newText = item.textContent.trim();

		document.querySelector('.btn.dropdown-toggle.zoneButtonText').textContent = newText;
	});
});


/*---------------------------------------------JS for Minimizing Modal with Alert--------------------------------------------------------------*/

var branchCodeGlobal = "";
var deviceIdGlobal = "";
var fetchedBranchGlobal = "";
var branchDetailsGlobal = "";
var alarmedZonesListGlobal = "";
var desiredNameGlobal = "";
function storeModalData(branchCode, deviceId, fetchedBranch, branchDetails, alarmedZonesList) {

	//alert(branchCode+"-->>"+fetchedBranch)
	const minimizedModalsNew = JSON.parse(localStorage.getItem('minimizedModalsForWelcome')) || [];

	// Check if this modal is already stored
	const existingModalIndex = minimizedModalsNew.findIndex(modal => modal.branchCode === branchCode);

	const modalData = {
		branchCode,
		deviceId,
		fetchedBranch,
		branchDetails,
		alarmedZonesList
	};


	//alert(JSON.stringify(modalData))
	if (existingModalIndex === -1) {
		minimizedModalsNew.push(modalData);
	} else {
		minimizedModalsNew[existingModalIndex] = modalData;
	}

	// Save back to localStorage

	//localStorage.setItem('minimizedModals', JSON.stringify(minimizedModals));

	localStorage.setItem('minimizedModalsForWelcome', JSON.stringify(minimizedModalsNew));

	// alert("inside storemodal data -->>"+JSON.stringify(localStorage.getItem('minimizedModalsForWelcome')))
}

var minimizedModals = [];

function minimizeModal(button) {
	const modal = button.closest('.modal');
	const modalId = modal.getAttribute('id');
	// alert("-->>>>>>>.modalId"+modalId)
	if (modal) {
		// Minimize or toggle the modal
		$(modal).modal('toggle');

		if (!modal.classList.contains('minimized') && !minimizedModals.includes(modalId)) {

			//alert("minimizedModals.push")
			minimizedModals.push(modalId);  // Add to minimized modals if not already present
			//alert("minimizedModals.push  done"+JSON.stringify(minimizedModals))
			//alert("Success");
			storeModalData(branchCodeGlobal, deviceIdGlobal, fetchedBranchGlobal, branchDetailsGlobal, alarmedZonesListGlobal);
		} else {
			//alert("else")
			const index = minimizedModals.indexOf(modalId);
			if (index !== -1) {
				minimizedModals.splice(index, 1);  // Remove if already minimized
			}
		}

		// Save minimized modals to localStorage
		// alert("setting data in localstorage ")
		localStorage.setItem('minimizedModals', JSON.stringify(minimizedModals));
		//alert("setting data in localstorage done"+localStorage.setItem(JSON.stringify(minimizedModals)) )

		displayMinimizedPreviews();
	} else {
		console.error('Modal not found:', modalId);
	}

	$(modal).on('hidden.bs.modal', function() {
		const index = minimizedModals.indexOf(modalId);
		if (index !== -1) {
			minimizedModals.splice(index, 1);
			localStorage.setItem('minimizedModals', JSON.stringify(minimizedModals));
			displayMinimizedPreviews();
		}
	});
}


function displayMinimizedPreviews() {
	const previewContainer = document.getElementById('minimizedModalPreviews');
	const uniqueModalIds = new Set(minimizedModals);
	previewContainer.innerHTML = '';

	if (uniqueModalIds.size > 0) {
		const bellIcon = document.createElement('div');
		bellIcon.classList.add('minimized-modal-preview');
		bellIcon.style.cursor = 'pointer';
		bellIcon.innerHTML = '<i class="fa fa-bell fa-lg bell-icon" style="color: #c70039;"></i> <span>+' + uniqueModalIds.size + '</span>';
		previewContainer.appendChild(bellIcon);

		const dropdown = document.createElement('div');
		dropdown.id = 'minimizedModalDropdown';
		dropdown.style.display = 'none';
		dropdown.classList.add('dropdown-menu');
		dropdown.style.position = 'absolute';

		uniqueModalIds.forEach((modalId) => {
			const branchCode = modalId.split('_')[1] || 'Unknown';
			const preview = document.createElement('div');
			preview.classList.add('dropdown-item');
			preview.style.cursor = 'pointer';
			preview.innerHTML = '<i class="fa fa-bell fa-lg bell-icon" style="color: #c70039;"></i> <span>' + branchCode + '</span>';

			// Click event to navigate and pass data
			preview.addEventListener('click', function() {
				localStorage.setItem('selectedModal', JSON.stringify({ modalId, branchCode }));
				window.location.href = './hmsDashboard';
			});

			dropdown.appendChild(preview);
		});

		previewContainer.appendChild(dropdown);

		bellIcon.addEventListener('click', function(event) {
			const dropdown = document.getElementById('minimizedModalDropdown');
			dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';
			event.stopPropagation();
		});
	}
}


document.addEventListener("DOMContentLoaded", function() {
	const dateTimeCells = document.querySelectorAll('.datetime');
	dateTimeCells.forEach(cell => {
		const dateTimeStr = cell.getAttribute('data-datetime');
		if (dateTimeStr) {
			const dateTime = new Date(dateTimeStr);
			const formattedDateTime = dateTime.toLocaleString('en-US', {
				year: 'numeric',
				month: '2-digit',
				day: '2-digit',
				hour: '2-digit',
				minute: '2-digit',
				second: '2-digit',
				hour12: false
			});
			cell.textContent = formattedDateTime;
		}
	});
});
/*----------------------------------------------JS to display Broadcasting  data in branch table-------------------------------------------------------*/


var currentModalId;
var branchCodes = {};
var isBroadcastingRunning = false;


document.querySelectorAll('#branchValue').forEach(function(element) {
	var branchCode = element.getAttribute('value');
	branchCodes[branchCode] = true;
});


for (var code in branchCodes) {
	if (branchCodes.hasOwnProperty(code)) {

	}
}

var clickedBranchCode;
var alarmedZonesList = [];

let currentlyOpenRow = null;
let originalCardHeight = null; // To store the original height of the card

function showDeviceHealth(branchCode, row) {
	let existingHealthTable = document.querySelector('#deviceHealthTable');
	const branchDetailsCard = document.getElementById('branchDetailsCard');
	const branchTableWrapper = document.getElementById('branchTableWrapper');

	if (currentlyOpenRow === row) {
		// Remove the health table
		if (existingHealthTable) {
			existingHealthTable.remove();
		}

		// Reset the dimensions
		branchDetailsCard.style.width = "100%";
		branchTableWrapper.style.width = "100%";

		// Decrease the card height
		if (originalCardHeight !== null) {
			branchDetailsCard.style.minHeight = originalCardHeight + "px";
		}

		currentlyOpenRow = null;
		return;
	}

	if (existingHealthTable) {
		existingHealthTable.remove();
	}

	// Store the original height of the card if not already done
	if (originalCardHeight === null) {
		originalCardHeight = parseFloat(getComputedStyle(branchDetailsCard).minHeight);
	}

	// Create a new row for the health communicator table
	const newRow = document.createElement('tr');
	newRow.id = 'deviceHealthTable';
	newRow.style.display = 'table-row';

	const newCell = document.createElement('td');
	newCell.colSpan = 7;
	newCell.innerHTML = `
		<div class="table-wrapper" style="width: 100%;">
			<div class="row mx-1">
				<div class="col-md-12">
					<div class="card">
						<div class="table-responsive table-bordered" style="max-height: 250px; overflow-y: auto; font-size: 14px;">
							<div id="messageContainer" style="display: none;"></div>
							<table id="dataTable" class="table mb-0">
								<thead style="color: #fff; position: sticky; top: 0; background: #028bd7;">
									<tr>
										<th>Branch Name</th>
										<th>Smart Communicator</th>
										<th>Sync Time</th>
										<th>Battery Voltage</th>
										<th>GSM</th>
										<th>Main</th>
										<th>Solar</th>
										<th>Battery</th>
										<th>Signal</th>
										<th>Status</th>
										
									</tr>
								</thead>
								<tbody id="dataBody" style="color: #028bd7; cursor: pointer; color: black;">
								</tbody>
							</table>
						</div>
					</div>
					<div class="card">
						<!-- Input Status Table -->
						<div id="statusTable" style="display: none;">
							<!-- Filter Dropdown -->
							<div class="">
							   
								<div class="table-responsive table-striped table-bordered mb-2" style="max-height: 230px; overflow-y: auto; font-size: 12px">
									<table class="table mb-0">
										<thead style="color: #fff; position: sticky; top: 0; background: #028bd7; text-align: center;">
											<tr>
												<th class="text-left">SNo.</th>
												<th class="text-left">Device Name</th>
											    
												<th class="text-left">Alert Type</th>
												<th class="text-left">Status</th>
											</tr>
										</thead>
										<tbody id="activeZonesBodyOne" style="color: black; text-align: left; font-size: 14px;">
										</tbody>
									    
										<tbody id="activeZonesBodyTwo" style="color: black; text-align: left; font-size: 14px;">
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	`;

	newRow.appendChild(newCell);
	row.parentNode.insertBefore(newRow, row.nextSibling);

	currentlyOpenRow = row;

	if (isBroadcastingRunning) {
		displayData();
	} else {
		fetchLatestData(branchCode);
	}

	// Add click event listener to the table body
	document.getElementById('dataBody').addEventListener('click', function(event) {
		if (event.target.tagName === 'TD') {
			toggleStatusTable(row);
		}
	});
}


function toggleStatusTable() {
	const statusTable = document.getElementById('statusTable');
	statusTable.style.display = statusTable.style.display === 'block' ? 'none' : 'block';

	if (statusTable.style.display === 'block') {
		openStatusTable();
	}
}

/*-----------------------------------------JS for getting Latest record in Branch Table----------------------------------------------------------*/


var zonesString = "";
function fetchLatestData(branchCode) {

	$.ajax({
		url: './getLatestRecordWithAlertType',
		type: 'GET',
		data: { branchCode: branchCode },
		dataType: 'json',
		success: function(data) {
			if (data.statusCode == 200) {


				var latestRecord = data.payload.latestRecord;

				zonesString = "_";

				smsResponse = data.payload.zoneValues;

				smsResponse.forEach(function(zoneValue, index) {
					var zoneParts = zoneValue.split(":");
					var zoneName = zoneParts[0].trim();
					var zoneAlert = zoneParts[1].trim();
					var zoneStatus = zoneParts[2].trim();




					zonesString += zoneName + " : " + zoneAlert + " : " + zoneStatus;

					if (index < smsResponse.length - 1) {
						zonesString += "_";
					}
				});
				console.log("zonesString--->>>>>>" + zonesString)


				displayDataForLatestRecord(latestRecord, branchCode);
			} else {
				alert("No data found for the branch code: " + branchCode);
			}
		},
		error: function(xhr, status, error) {
			console.error('Error fetching latest data:', error);
			alert("Failed to fetch latest data for the branch code: " + branchCode);
		}
	});
}


/*--------------------------------JS for Opening Table with Latest Active Zone Record---------------------------------------------------------------*/

function openStatusTableForLatestRecord(zones, event) {
	// Clear any existing status tables to prevent duplicates
	$('.status-table-row').remove();

	const activeZonesData = zones.split("_");

	const statusTable = $('#statusTable').clone().show();
	statusTable.attr('id', '');

	const activeZonesTableBody = statusTable.find("#activeZonesBodyOne");
	activeZonesTableBody.empty(); // Clear any previous data

	// Populate the cloned table with zone data
	activeZonesData.forEach((zone, index) => {
		const zoneData = zone.split(":");
		const newRow = $("<tr></tr>");

		// Add a serial number cell
		$("<td></td>").text(index).appendTo(newRow); // Index starts from 1

		// Add device name and status cells
		zoneData.forEach((data, dataIndex) => {
			const trimmedData = data.trim();
			const newCell = $("<td></td>");

			// Check if it's the status field to render an icon
			if (dataIndex === zoneData.length - 1) {
				renderStatusIconAndAlert(newCell, trimmedData); // Render status icon
			} else {
				newCell.text(trimmedData); // Add normal text for other fields
			}

			newRow.append(newCell);
		});

		activeZonesTableBody.append(newRow);
	});

	// Create a new row to hold the status table
	const newTableRow = $('<tr class="status-table-row"></tr>');
	const newTableCell = $('<td colspan="8"></td>').append(statusTable);
	newTableRow.append(newTableCell);

	// Insert the status table row right after the clicked DataTable row
	$(event.currentTarget).after(newTableRow);
}

// Function to render status icons and alert types
function renderStatusIconAndAlert(cell, status) {
	switch (status) {
		case 'A':
			cell.html('<i class="fa-solid fa-circle-exclamation fa-2xl" style="color:crimson;" data-toggle="tooltip" title="Alarm"></i> ');
			break;
		case 'N':
			cell.html('<i class="fa-solid fa-circle-check fa-2xl" style="color: #07974d;" data-toggle="tooltip" title="Normal"></i> ');
			break;
		case 'I':
			cell.html('<i class="fa-solid fa-circle-exclamation fa-2xl" style="color: #e19b23;" data-toggle="tooltip" title="Isolated"></i> ');
			break;
		case 'D':
			cell.html('<i class="fa-solid fa-circle-exclamation fa-2xl" style="color: #9e9a94;" data-toggle="tooltip" title="Deactive"></i> ');
			break;
		default:
			cell.html('<i class="fa-solid fa-question-circle fa-2xl" style="color: grey;" data-toggle="tooltip" title="Unknown"></i> Unknown');
			break;
	}
}

// Initialize tooltips
$(document).ready(function() {
	$('[data-toggle="tooltip"]').tooltip();
});


function closeStatusTable() {
	const statusTable = document.getElementById('statusTable');
	const branchDetailsCard = document.getElementById('branchDetailsCard');
	const branchTableWrapper = document.getElementById('branchTableWrapper');

	// Hide the input status table
	statusTable.style.display = "none";

	// Reset the card height
	branchDetailsCard.style.minHeight = "422px";
	branchTableWrapper.style.maxHeight = "350px";
}


/*----------------------------------------JS for display latest record according to branch code when broadcasting is false--------------------------------------*/


function displayDataForLatestRecord(latestRecord, branchCode) {
	function toSentenceCase(str) {
		return str.toLowerCase().replace(/(^\w{1})|(\s+\w{1})/g, letter => letter.toUpperCase());
	}

	$.ajax({
		url: './getBranchByBranchCode',
		type: 'GET',
		contentType: 'application/json',
		data: {
			branchCode: branchCode
		},
		success: function(response) {
			var fetchedBranchCodeName = response.payload[0].branchDesc;

			var tableBody = document.getElementById("dataBody");
			var newRow = document.createElement("tr");

			// Branch Code Cell
			var branchCodeCell = document.createElement("td");
			var icon = document.createElement("i");
			icon.className = "fa-solid fa-caret-right fa-lg mx-1";
			branchCodeCell.appendChild(icon);
			branchCodeCell.appendChild(document.createTextNode(toSentenceCase(fetchedBranchCodeName)));
			newRow.appendChild(branchCodeCell);

			// Toggle the status table and caret direction on icon click
			icon.addEventListener('click', function() {
				toggleStatusTable(newRow);
			});

			// Device Name Cell
			var deviceNameCell = document.createElement("td");
			deviceNameCell.textContent = toSentenceCase(latestRecord.panelSerialNo);
			newRow.appendChild(deviceNameCell);

			// Sync Time Cell
			var syncTimeCell = document.createElement("td");

			var dateTimeString = latestRecord.dateTime;
			var dateObj = new Date(dateTimeString);

			// Format the date as dd-mm-yyyy and time as hh:mm:ss
			var datePart = ("0" + dateObj.getDate()).slice(-2) + "-" + ("0" + (dateObj.getMonth() + 1)).slice(-2) + "-" + dateObj.getFullYear();
			var timePart = ("0" + dateObj.getHours()).slice(-2) + ":" + ("0" + dateObj.getMinutes()).slice(-2) + ":" + ("0" + dateObj.getSeconds()).slice(-2);

			syncTimeCell.textContent = datePart + ' ' + timePart;
			newRow.appendChild(syncTimeCell);
             
              var batteryVoltage = document.createElement("td");
    batteryVoltage.style.textAlign = "center";
    batteryVoltage.innerHTML = latestRecord.battery_VOLTAGE;
    newRow.appendChild(batteryVoltage);
    
       var gsmSignal = document.createElement("td");
      gsmSignal.style.textAlign = "center";
    gsmSignal.innerHTML = latestRecord.gsm_SIGNAL;
    newRow.appendChild(gsmSignal);

			// Main Cell
			var mainCell = document.createElement("td");
			mainCell.style.textAlign = "center";
			mainCell.innerHTML = latestRecord.main == 0 ?
				`<br/><i class="fa-solid fa-plug-circle-xmark fa-2xl" style="color: #902c3b;"></i><br/><b style="font-size:12px;">Power Off</b>` :
				`<br/><i class="fa-solid fa-plug-circle-bolt fa-2xl" style="color: #1d6e1c;"></i><br/><b style="font-size:12px;">Power On</b>`;
			newRow.appendChild(mainCell);

			// Solar Cell
			var solarCell = document.createElement("td");
			solarCell.style.textAlign = "center";
			solarCell.innerHTML = latestRecord.solar == 0 ?
				`<br/><i class="fa-solid fa-sun fa-2xl" style="color: #808080;"></i><br/><b style="font-size:12px;">Not Connected</b>` :
				`<br/><i class="fa-solid fa-sun fa-2xl" style="color: #FFAB52;"></i><br/><b style="font-size:12px;">Connected</b>`;
			newRow.appendChild(solarCell);

			// Battery Cell
			var batteryCell = document.createElement("td");
			batteryCell.style.textAlign = "center";
			if (latestRecord.battery == 0) {
				batteryCell.innerHTML =
					`<br/><i class=" text-center fa-solid fa-battery-empty fa-2xl" style="color: #c25419;"></i><br/><b style="font-size:12px;">Not Installed</b>`;
			} else if (latestRecord.battery == 1) {
				batteryCell.innerHTML =
					`<br><i class="fa-solid fa-battery-three-quarters fa-2xl" style="color: #d9a13f;"></i><br/><b style="font-size:12px;">Charging</b>`;
			} else if (latestRecord.battery == 2) {
				batteryCell.innerHTML =
					`<br><i class="fa-solid fa-battery-quarter fa-2xl" style="color: #FFD43B;"></i><br/><b style="font-size:12px;">Battery Low</b>`;
			} else {
				batteryCell.innerHTML =
					`<br><i class="fa-solid fa-battery-full fa-2xl" style="color: #4a8519;"></i><br/><b style="font-size:12px;">Battery Full</b>`;
			}
			newRow.appendChild(batteryCell);

			// Signal Cell
			var signalCell = document.createElement("td");
			signalCell.style.textAlign = "center";
			if (latestRecord.signalFlag == 0) {
				signalCell.innerHTML =
					`<br/><img src="./images/nonSignal.png" alt="nonSignal" style="width:40px;"><br/><b style="font-size:12px;">No Signal</b>`;
			} else if (latestRecord.signalFlag == 1) {
				signalCell.innerHTML =
					`<img src="./images/signalUnknown.png" alt="unknownSignal" style="width:70px;"><br/><b style="font-size:12px;">Unknown Signal</b>`;
			} else if (latestRecord.signalFlag == 2) {
				signalCell.innerHTML =
					`<img src="./images/veryPoorSignal.png" alt="veryPoorSignal" style="width:40px;"><br/><b style="font-size:12px;">Very Poor Signal</b>`;
			} else if (latestRecord.signalFlag == 3) {
				signalCell.innerHTML =
					`<img src="./images/poorSignal.png" alt="PoorSignal" style="width:40px;"><br/><b style="font-size:12px;">Poor Signal</b>`;
			} else if (latestRecord.signalFlag == 4) {
				signalCell.innerHTML =
					`<img src="./images/fairSignal.png" alt="fairSignal" style="width:40px;"><br/><b style="font-size:12px;">Fair Signal</b>`;
			} else {
				signalCell.innerHTML =
					`<img src="./images/goodSignal.png" alt="goodSignal" style="width:40px;"><br/><b style="font-size:12px;">Good Signal</b>`;
			}
			newRow.appendChild(signalCell);

			// Status Cell
			var statusCell = document.createElement("td");
			statusCell.style.textAlign = "center";
			statusCell.innerHTML = latestRecord.armed == 0 ?
				`<br/><i class="fa-solid fa-shield-virus fa-2xl" style="color:#418529;"></i><br/><b style="font-size:12px;">Active</b>` :
				`<i class="fa-solid fa-shield-virus fa-2xl" style="color: #ba2626 ;"></i><br/><b style="font-size:12px;">Disarmed</b>`;
			newRow.appendChild(statusCell);

   
    
			tableBody.appendChild(newRow);

			newRow.querySelectorAll('td').forEach(cell => {
				cell.addEventListener('click', function() {
					openStatusTable(zonesString);
				});
			});

		},
		error: function(xhr, status, error) {

			console.error(xhr.responseText);
		}
	});
}

function toggleStatusTable(row, zonesString) {
	const statusTable = document.getElementById('statusTable');
	const icon = row.querySelector('i');

	if (statusTable.style.display === "none" || statusTable.style.display === "") {
		// Ensure zonesString is defined and available
		if (zonesString) {
			openStatusTableForLatestRecord(zonesString); // Opens and populates the status table
		}
		statusTable.style.display = "block";
		icon.className = "fa-solid fa-caret-down fa-lg mx-1";
	} else {
		closeStatusTable(); // Hides the status table
		statusTable.style.display = "none";
		icon.className = "fa-solid fa-caret-right fa-lg mx-1";
	}
}


/*----------------------------------------------------------------JS for open Active Zone Table----------------------------------------------------------*/
/*
function openStatusTable(zones) {
	alert(zones)
	document.getElementById('statusTable').style.display = "block";

	var activeZonesData = getActiveZonesData();
	var activeZonesTableBody = document.getElementById("activeZonesBody");
	activeZonesTableBody.innerHTML = "";

	var displayedRows = [];

	for (var i = 0; i < activeZonesData.length; i++) {
		var zoneData = activeZonesData[i].split("_");
		for (var j = 0; j < zoneData.length; j++) {
			var newRow = document.createElement("tr");
			var zoneStatus = zoneData[j].split(":");
			var isRowDisplayed = false;

			for (var k = 0; k < zoneStatus.length; k++) {
				var newCell = document.createElement("td");
				newCell.textContent = zoneStatus[k];

				if (document.getElementById("inputFilter").value === 'alarm') {
					if (zoneStatus[k].trim() === 'A') {
						renderActiveStatus(newCell, zoneStatus[k]);
						isRowDisplayed = true;
					}
				} else if (document.getElementById("inputFilter").value === 'disable') {
					isRowDisplayed = false;
					if (zoneStatus[k].trim() === 'D') {
						renderInactiveStatus(newCell, zoneStatus[k]);
						isRowDisplayed = true;
					}
				} else if (document.getElementById("inputFilter").value === 'normal') {
					isRowDisplayed = false;
					if (zoneStatus[k].trim() === 'N') {
						renderNormalStatus(newCell, zoneStatus[k]);
						isRowDisplayed = true;
					}
				} else {
					renderAllStatus(newCell, zoneStatus[k]);
					isRowDisplayed = true;
				}

				newRow.appendChild(newCell);
			}

			if (isRowDisplayed && !displayedRows.includes(newRow.innerHTML)) {
				activeZonesTableBody.appendChild(newRow);
				displayedRows.push(newRow.innerHTML);
			}
		}
	}
}
*/




function openStatusTable(zones) {



	var activeZonesData = zones.split("_");
	var activeZonesTableBody = document.getElementById("activeZonesBodyTwo");




	activeZonesTableBody.innerHTML = "";

	for (var i = 0; i < activeZonesData.length; i++) {
		var zoneData = activeZonesData[i].split(":");
		var newRow = document.createElement("tr");
		var isRowDisplayed = false;


		var serialNumberCell = document.createElement("td");
		serialNumberCell.textContent = i;
		newRow.appendChild(serialNumberCell);


		for (var j = 0; j < zoneData.length; j++) {
			var newCell = document.createElement("td");
			newCell.textContent = zoneData[j].trim();

			renderAllStatus(newCell, zoneData[j].trim());
			isRowDisplayed = true;



			newRow.appendChild(newCell);
		}

		if (isRowDisplayed) {
			activeZonesTableBody.appendChild(newRow);
		}
	}
}

function renderAllStatus(cell, status) {
	switch (status.trim()) {
		case 'A':
			cell.innerHTML = '<i class="fa-solid fa-circle-exclamation fa-2xl" style="color:crimson;" data-toggle="tooltip" title="Normal"></i>';
			break;
		case 'N':
			cell.innerHTML = '<i class="fa-solid fa-circle-check fa-2xl" style="color: #07974d;" data-toggle="tooltip" title="Normal"></i>';
			break;
		case 'I':
			cell.innerHTML = '<i class="fa-solid fa-circle-exclamation fa-2xl" style="color: #e19b23;" data-toggle="tooltip" title="Isolated"></i>';
			break;
		case 'D':
			cell.innerHTML = '<i class="fa-solid fa-circle-exclamation fa-2xl" style="color: #9e9a94;" data-toggle="tooltip" title="Deactive"></i>';
			break;
		default:
			break;
	}
}

function renderActiveStatus(newCell, status) {


	if (status.trim() == 'A') {

		newCell.innerHTML = '<i class="fa-solid fa-circle-exclamation fa-2xl" style="color:crimson;" data-toggle="tooltip" title="Normal"></i>';
	}
}

function renderNormalStatus(newCell, status) {


	if (status.trim() == 'N') {

		newCell.innerHTML = '<i class="fa-solid fa-circle-check fa-2xl" style="color: #07974d;" data-toggle="tooltip" title="Normal"></i>';
	}
}
function renderInactiveStatus(cell, status) {
	switch (status.trim()) {

		case 'D':
			cell.innerHTML = '<i class="fa-solid fa-circle-exclamation fa-2xl" style="color: #9e9a94;" data-toggle="tooltip" title="Deactive"></i>';
			break;
		default:
			break;
	}
}

document.getElementById('inputFilter').addEventListener('change', function() {
	openStatusTable();
});

document.getElementById('inputFilter').addEventListener('change', function() {
	openStatusTableForLatestRecord(zonesString);
});


/*----------------------------------------------------JS for getting data by alert flag------------------------------------------------------------------*/
function fetchAlertFlags(branchCode, activeZoneScreenTwo, callback) {
	$.ajax({
		url: './getAlertFlag',
		type: 'GET',
		data: { branchCode: branchCode },
		success: function(response) {
			if (response.statusCode === 200) {
				let alertFlags = response.payload;
				//alert(JSON.stringify(alertFlags))
				// Process activeZone to extract values before the colon and store them in an array
				let processedZones = activeZoneScreenTwo.split('_').filter(Boolean).map(zone => zone.split(':')[0]);

				// Filter processed zones to only include those with an alert flag of "Y"
				let zonesWithAlertFlagY = processedZones.filter(function(zone) {

					return alertFlags.some(function(flag) {

						return zone === flag.alertType && flag.alertFlag === "Y";
					});
				});

				//alert("zonesWithAlertFlagY--"+zonesWithAlertFlagY);
				let matchFound = zonesWithAlertFlagY.length > 0;

				callback(matchFound, zonesWithAlertFlagY);  // Pass filtered zones to callback
			} else {
				console.log("Failed to fetch alert flags. Status Code:", response.statusCode);
				callback(false, []);  // Pass empty array if fetch fails
			}
		},
		error: function(xhr, status, error) {
			console.error("Error fetching alert flags:", error);
			callback(false, []);  // Pass empty array if error occurs
		}
	});
}

/*----------------------------------------------------JS for getting data from websocket------------------------------------------------------------------*/
var activeZonesList = [];
var inActiveZonesList = [];
var deviceData = {};

var fetchedBranch = "";
var vendorName = '';

var branchDetails = '';
var braodcastBranchCode = [];
var activeZone = '';
var zone_desired_name = '';
var completeMessage = '';
// Modify the SIA device handling in processMessage function
function processMessage(message) {
	alert("Message : " + message);

	var dataSplit = message.split(",");
	//alert(dataSplit);
	var branchCodeFromMessage = dataSplit[0];
	
	var deviceId = dataSplit[1];
	activeZone = dataSplit[8];
	var branchCodes = [];
	zone_desired_name = dataSplit[9];

	var completeMessage = dataSplit[10];
	//alert("CompleteMessage ::" + activeZone);

	// Extract panel serial number
	var panelSerialMatch = completeMessage.match(/\[(\d+)\|/);
	var panelSerialNumber = panelSerialMatch ? panelSerialMatch[1] : null;

	//alert("Panel Serial Number :: " + panelSerialNumber);

	// Extract alert code
	var match = completeMessage.match(/\[(\d+)\|([^\]]+)\]/);
	var alertCode = match ? match[2] : null;

	var siaalarmedZonesList = [];
	var desiredNamedList = [];

	if (panelSerialNumber) {
		$.ajax({
			url: './getDeviceForSiaByPanelSerialNo',
			type: 'GET',
			data: { deviceSerialNo: panelSerialNumber },
			success: function(response) {
				if (response && response.payload && response.payload.deviceId) {
					var fetchedDeviceId = response.payload.deviceId;

					if (alertCode) {
						var rawList = alertCode.split('_');
						var ajaxPromises = [];

						for (var i = 0; i < rawList.length; i++) {
							var item = rawList[i];
							if (item.length >= 6) {
								var command = item.substring(1, 3);

								var promise = $.ajax({
									url: './getAllDeviceSiaMessageType',
									method: 'GET',
									data: {
										command: command,
										deviceId: fetchedDeviceId
									}
								}).then(function(response) {
									if (response && response.payload && response.payload.message) {
										siaalarmedZonesList.push(response.payload.message);
										desiredNamedList.push(response.payload.messageType);
									} else {
										siaalarmedZonesList.push('N/A');
									}
								}).catch(function() {
									siaalarmedZonesList.push('Error');
								});

								ajaxPromises.push(promise);
							}
						}

						Promise.all(ajaxPromises).then(function() {
							//alert("Final SIA Alarmed Zones List: " + siaalarmedZonesList.join(', '));
						});
					}
				} else {
					//alert("Device ID not found for panel serial number: " + panelSerialNumber);
				}
			},
			error: function(xhr, status, error) {
				console.error("Error fetching device by panel serial:", xhr.responseText);
			}
		});
	}

	var vendorNumber = '';

	// LOGIC WILL Write here.....
	$.ajax({
		url: './getDeviceDetailsByBranchCode',
		type: 'GET',
		contentType: 'application/json',
		data: {
			branchCode: branchCodeFromMessage
		},
		success: function(response) {
			//alert("Enter" + JSON.stringify(response.payload.deviceType));
			if ("SIA" === response.payload.deviceType) {
				//alert("SIA DEVICE :: " + siaalarmedZonesList);

				braodcastBranchCode.push(branchCodeFromMessage);

				$.ajax({
					url: './getBranchByBranchCode',
					type: 'GET',
					contentType: 'application/json',
					data: {
						branchCode: branchCodeFromMessage
					},
					success: function(response) {
						fetchedBranch = response.payload[0].branchDesc;
						dataSplit[0] = fetchedBranch;
						branchDetails = response.payload[0];

						$.ajax({
							url: 'getDeviceDetailsByBranchCode',
							type: 'GET',
							contentType: 'application/json',
							data: {
								branchCode: branchCodeFromMessage
							},
							success: function(response) {
								vendorName = response.payload.vendorName;
								//alert("VENDOR NAME :: " + vendorName);

								$.ajax({
									url: './getVendorByName',
									type: 'GET',
									contentType: 'application/json',
									data: {
										vendorName: vendorName
									},
									success: function(response) {
										//alert("Enter in ss");
										vendorNumber = response.payload.mobile;
										//alert("VENDOR NUMBER :: " + vendorNumber);

										sendSmsByBranchCode(branchCodeFromMessage, alertCode);
										sendMailByBranchCode(branchCodeFromMessage, alertCode);

										var alarmedZonesList = [];
										alarmedZonesList.push(siaalarmedZonesList);

										document.querySelectorAll('#branchValue').forEach(function(element) {
											var branchCode = element.getAttribute('value');
											branchCodes.push(branchCode);
										});

										var success = false;
										branchCodes.forEach(function(branchCode) {
											if (branchCode === branchCodeFromMessage && alertCode) {
												success = true;
											}
										});

										if (success) {
											document.querySelectorAll('#branchValue').forEach(function(element) {
												var branchCode = element.getAttribute('value');
												if (branchCode === branchCodeFromMessage) {
													element.style.backgroundColor = "#FCAEAE";
													element.style.color = "#C70039";
												}
											});

											displayAlarmedZonesList(desiredNamedList, vendorNumber, alarmedZonesList, deviceId, fetchedBranch, vendorName, branchDetails, true);

											var modalId = 'totalDeviceModal_' + branchCodeFromMessage;
											var modal = document.getElementById(modalId);

											if (!modal) {
												var modalTemplate = document.getElementById('totalDeviceModal').cloneNode(true);
												modalTemplate.id = modalId;
												document.body.appendChild(modalTemplate);
												modal = modalTemplate;

												attachCloseModalListener(modalId);
											} else {
												var randomNumber = Math.floor(Math.random() * 1000);
												modalId = modalId + '_' + randomNumber;
												var modalTemplate = document.getElementById('totalDeviceModal').cloneNode(true);
												modalTemplate.id = modalId;
												document.body.appendChild(modalTemplate);
												modal = modalTemplate;

												attachCloseModalListener(modalId);
											}

											playSoundAlert();
											$(modal).modal('show');
											currentModalId = modalId;
										} else {
											document.querySelectorAll('#branchValue').forEach(function(element) {
												var branchCode = element.getAttribute('value');
												if (branchCode === branchCodeFromMessage) {
													element.style.backgroundColor = "#CDFADB";
													element.style.color = "#0d9276";
												}
											});
										}

										if (branchCodeFromMessage === clickedBranchCode) {
											deviceData[deviceId] = dataSplit;
											displayData();
											activeZonesList.push(alertCode);
											activeZonesList.push(dataSplit[11]);
										}
									},
									error: function(xhr, status, error) {
										console.error(xhr.responseText);
									}
								});
							},
							error: function(xhr, status, error) {
								console.error(xhr.responseText);
							}
						});
					},
					error: function(xhr, status, error) {
						console.error(xhr.responseText);
					}
				});

				fetchBranchCodeFromBroadCast(branchCodeFromMessage);
			} else {			
			//alert("Enter")
				braodcastBranchCode.push(branchCodeFromMessage);

				$.ajax({
					url: './getBranchByBranchCode',
					type: 'GET',
					contentType: 'application/json',
					data: {
						branchCode: branchCodeFromMessage
					},
					success: function(response) {
						//alert("Enter2")
						fetchedBranch = response.payload[0].branchDesc;
						dataSplit[0] = fetchedBranch;
						branchDetails = response.payload[0];

						$.ajax({
							url: './getDeviceDetailsByBranchCode',
							type: 'GET',
							contentType: 'application/json',
							data: {
								branchCode: branchCodeFromMessage
							},
							success: function(response) {
								//alert("Enter3")
								vendorName = response.payload.vendorName;
								//alert(vendorName);
								$.ajax({
									url: './getVendorByName',
									type: 'GET',
									contentType: 'application/json',
									data: {
										vendorName: vendorName
									},
									success: function(response) {
										//alert("Enter4")
										vendorNumber = response.payload.mobile;
										//alert("Enter4"+vendorNumber+"-"+branchCodeFromMessage+"-"+activeZone);
										
										sendSmsByBranchCode(branchCodeFromMessage, activeZone);
										sendMailByBranchCode(branchCodeFromMessage, activeZone);

										var zonesArray = activeZone.split(/[:,_]/);
										//alert("ZONES ARRAY :: "+zonesArray);
										zonesArray = zonesArray.filter(function(element) {
											return element.trim() !== '';
										});

										var evenIndicesArray = zonesArray.filter(function(element, index) {
											return index % 2 === 0;
										});

										var alarmedZonesList = [];

										alarmedZonesList.push(evenIndicesArray);



										let matches = [...zone_desired_name.matchAll(/(.+?)_/g)];
										//alert(matches)
										var desiredNamedList = [];

										let extractedValues = matches.map(match => match[1]); // Extract matched groups
										desiredNamedList.push(extractedValues);
										//alert(JSON.stringify(desiredNamedList));

										document.querySelectorAll('#branchValue').forEach(function(element) {
											var branchCode = element.getAttribute('value');
											branchCodes.push(branchCode);
										//	alert(branchCodes);
										});
										//alert("Before alert 1")

										var success = false;
										branchCodes.forEach(function(branchCode) {
											//alert(branchCode);
											if (branchCode === branchCodeFromMessage && activeZone.length > 0) {
											//alert("if for true success")
													success = true;
											}
										});
//alert("Before success :: " + success)
										if (success == true) {
											//alert("true")
											document.querySelectorAll('#branchValue').forEach(function(element) {
												var branchCode = element.getAttribute('value');
												if (branchCode === branchCodeFromMessage) {
													element.style.backgroundColor = "#FCAEAE";
													element.style.color = "#C70039";
												}
											});
											displayAlarmedZonesList(desiredNamedList, vendorNumber, alarmedZonesList, deviceId, fetchedBranch, vendorName, branchDetails);

											var modalId = 'totalDeviceModal_' + branchCodeFromMessage;

											var modal = document.getElementById(modalId);
											if (!modal) {
												var modalTemplate = document.getElementById('totalDeviceModal').cloneNode(true);
												modalTemplate.id = modalId;
												document.body.appendChild(modalTemplate);
												modal = modalTemplate;

												attachCloseModalListener(modalId);
											} else {
												//alert("Before alert2")

												var randomNumber = Math.floor(Math.random() * 1000);
												modalId = modalId + '_' + randomNumber;
												var modalTemplate = document.getElementById('totalDeviceModal').cloneNode(true);
												modalTemplate.id = modalId;
												document.body.appendChild(modalTemplate);
												modal = modalTemplate;

												attachCloseModalListener(modalId);
											}

											playSoundAlert();

											$(modal).modal('show');
											currentModalId = modalId;

										} else {
											//alert("enter in false condition")
											document.querySelectorAll('#branchValue').forEach(function(element) {
												var branchCode = element.getAttribute('value');
												if (branchCode === branchCodeFromMessage) {
													element.style.backgroundColor = "#CDFADB";
													element.style.color = "#0d9276";
												}
											});
										}

										if (branchCodeFromMessage === clickedBranchCode) {
											deviceData[deviceId] = dataSplit;
											displayData();
											activeZonesList.push(dataSplit[8]);
											activeZonesList.push(dataSplit[11]);
										}
									},
									error: function(xhr, status, error) {
										console.error(xhr.responseText);
									}
								});
							},
							error: function(xhr, status, error) {
								console.error(xhr.responseText);
							}
						});
					},
					error: function(xhr, status, error) {
						console.error(xhr.responseText);
					}
				});

				fetchBranchCodeFromBroadCast(branchCodeFromMessage);
			}
		},
		error: function(xhr, status, error) {
			console.error(xhr.responseText);
		}
	});
}

function fetchData() {
	$.ajax({
		url: './fetchDataAndBroadcast',
		method: 'GET',
		success: function(response) {
			console.log(response);
		//alert("Enter"+JSON.stringify(response));
			response.forEach(function(message) {
				processMessage(message);
			});
		},
		error: function(error) {
			console.error('Error fetching data:', error);
		}
	});
}

$(document).ready(function() {
	fetchData();
	setInterval(fetchData, 200);
});


function makeCall(zone, mobile, deviceId, fetchedBranch) {

	//alert("test--------->>"+zone+"-"+mobile+"-"+deviceId+"-"+fetchedBranch);
	const data = {
		sourcetype: "0",
		customivr: true,
		campaigntype: "4",
		filetype: "2",

		msisdnlist: [{
			phoneno: mobile,
			BranchName: fetchedBranch,
			ProductType: deviceId,
			Reason: zone,
			ContactNo: "9027095493"
		}]
	};

	$.ajax({
		url: './makeCall',
		type: 'POST',
		contentType: 'application/json',
		data: JSON.stringify(data),
		success: function(responseData) {
			
			//alert("call success");
			//alert("Response:"+JSON.stringify(responseData));
			console.log('Response:', responseData);
			console.log('Call to branch ' + fetchedBranch + ' (' + fetchedBranch + ') has been initiated.');
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.error('Error:', textStatus, errorThrown);
			//alert('Failed to initiate call to branch ' + fetchedBranch + ' (' + fetchedBranch + ').');
		}
	});
}

/*-----------------------------------------------------------------JS for Map and Geolocation------------------------------------------------------------------*/



/*----------------------------------------------------JS for Alert Sound-----------------------------------------------------------------------------*/

function playSoundAlert() {
	//var audio = document.getElementById("audio-alert");
	//	audio.play();
}

function stopSoundAlert() {
	var audio = document.getElementById("audio-alert");

	if (audio) {
		audio.pause();
		audio.currentTime = 0;
		audio.volume = 1;

	}
}
/*-----------------------------------JS for Send SMS and Mail for Broadcasting Alert---------------------------------------------------------------*/


function sendSmsByBranchCode(branchCode, activeZone) {
//alert("enter in sms");
	if (!branchCode) {
		console.error("Branch code is required.");
		return;
	}
	if (!activeZone) {
		console.error("Active zone is required.");
		return;
	}

	$.ajax({
		url: "./sendSmsByBranchCode",
		type: "GET",
		data: {
			branchCode: branchCode,
			activeZone: activeZone
		},
		success: function(response) {
			//   	alert(activeZone)
			//alert("SMS sent successfully!");
			console.log(response);
		},
		error: function(xhr, status, error) {
			console.error("Error sending SMS:", error);
		}
	});
}


function sendMailByBranchCode(branchCode, activeZone) {
//alert("enter in mail");

	if (!branchCode) {
		console.error("Branch code is required.");
		return;
	}
	if (!activeZone) {
		console.error("Active zone is required.");
		return;
	}

	$.ajax({
		url: "./sendMailByBranchCode",
		type: "GET",
		data: {
			branchCode: branchCode,
			activeZone: activeZone
		},
		success: function(response) {
			//alert("Mail sent successfully!");
			console.log(response);
		},
		error: function(xhr, status, error) {
			console.error("Error sending Mail:", error);
		}
	});
}

/*-----------------------------------------------------JS for Close the Modal Appearing on Alert-----------------------------------------------------*/


function attachCloseModalListener(modalId) {



	var closeModalButton = document.getElementById(modalId).querySelector('.close');
	if (closeModalButton) {
		closeModalButton.addEventListener('click', function() {
			var tableRows = document.getElementById(modalId).querySelector('tbody').getElementsByTagName('tr');
			Array.from(tableRows).forEach(function(row) {
				var deviceId = row.children[1].textContent;
				var alarmedZone = row.children[2].textContent;
				var remarkDropdown = row.querySelector('select.form-control');
				var defaultRemark = remarkDropdown.options[remarkDropdown.selectedIndex].textContent;
				var userId = $('#userId').val();
				var auditData = {
					deviceId: deviceId,
					activeZone: alarmedZone,
					remarks: defaultRemark,
					user: userId
				};
				fetch('./saveAudit', {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json'
					},
					body: JSON.stringify(auditData)
				})
					.then(response => response.json())
					.then(data => {
						stopSoundAlert();
						console.log(data);
					})
					.catch(error => {
						console.error('Error:', error);
					});
			});


			//stopSoundAlert();
		});
	}
}


/*---------------------------------------------------JS for Display Broadcasting Data in Table--------------------------------------------------------*/

var fetchedBranchName = '';
function displayData() {
	var tableBody = document.getElementById("dataBody");
	tableBody.innerHTML = "";

	/*  $.ajax({
		 url: 'getBranchByBranchCode',
		 type: 'GET',
		 contentType: 'application/json',
		 data: {
			 branchCode: clickedBranchCode
		 },
		 success: function(response) {
			 fetchedBranchName = response.payload[0].branchDesc;
			 //alert(fetchedBranchName)
			  },
error: function(xhr, status, error) {
	// Handle error here
	console.error(xhr.responseText);
}
});  */

	if (Object.keys(deviceData).length === 0) {

		fetchLatestData(clickedBranchCode);
	} else {
		for (var deviceId in deviceData) {




			if (deviceData.hasOwnProperty(deviceId)) {
				var dataSplit = deviceData[deviceId];
				var newRow = document.createElement("tr");

				/* var branchCell = document.createElement("td");
				branchCell.textContent = fetchedBranchName; // Set fetchedBranchName as content
				newRow.appendChild(branchCell); */
				//dataSplit[0] = fetchedBranchName;

				for (var i = 0; i < dataSplit.length - 2; i++) {


					var newCell = document.createElement("td");
					var icon = document.createElement("i");



					newCell.appendChild(icon);

					newCell.textContent = dataSplit[i];
					newCell.onclick = openStatusTable;

					console.log(dataSplit[i]);
					newCell.classList.add("email-column");
					newRow.appendChild(newCell)




					if (i == 3 && dataSplit[i] == 0) {

						newCell.innerHTML = `
                                     	
                                     	<td><br/><i class="fa-solid fa-plug-circle-xmark fa-2xl" style="color: #902c3b;"></i><br/><b style="font-size:12px;">Power Off</b></td>`;




					}
					else if (i == 3 && dataSplit[i] == 1) {
						newCell.innerHTML = `
                                      	
                                      	<td><br/> <i class="fa-solid fa-plug-circle-bolt fa-2xl" style="color: #1d6e1c;"></i><br/><b style="font-size:12px;">Power On</b></td>`

					}

					else if (i == 4 && dataSplit[i] == 1) {
						newCell.innerHTML = `
                                     	
                                     	<td><br/> <i class="fa-solid fa-sun fa-2xl" style="color:#ffab52;"></i><br/><b style="font-size:12px;">Charging On</b></td>`

					}
					else if (i == 4 && dataSplit[i] == 0) {
						newCell.innerHTML = `
                                     	
                                     	<td><br/> <i class="fa-solid fa-sun fa-2xl" style="color: #0c0d0d;"></i><br/><b style="font-size:12px;">Charging Off</b></td>`

					}
					else if (i == 5 && dataSplit[i] == 0) {
						newCell.innerHTML = `
                                        	
                                        	<td><br><i class="fa-solid fa-battery-empty fa-2xl" style="color: #c25419;"></i><br/><b style="font-size:12px;">Battery Low</b></td>`

					}
					else if (i == 5 && dataSplit[i] == 1) {
						newCell.innerHTML = `
                                        	
                                        	<td><br><i class="fa-solid fa-battery-three-quarters fa-2xl" style="color: #d9a13f;"></i><br/><b style="font-size:12px;">Good</b></td>`

					}
					else if (i == 5 && dataSplit[i] == 2) {
						newCell.innerHTML = `
                                        	
                                        	<td><br><i class="fa-solid fa-battery-quarter fa-2xl" style="color: #FFD43B;"></i><br/><b style="font-size:12px;">Average</b></td>`

					}
					else if (i == 5 && dataSplit[i] == 3) {
						newCell.innerHTML = `
                                        	
                                        	<td><br><i class="fa-solid fa-battery-full fa-2xl" style="color: #4a8519;"></i><br/><b style="font-size:12px;">Battery Full</b></td>`

					} else if (i == 6 && dataSplit[i] == 0) {
						newCell.innerHTML = `
                                     	
                                  		<td><br><img src="./images/nonSignal.png" alt"nonSignal" style="width:40px;"><br/><b style="font-size:12px;">No Signal</b></td>`

					}
					else if (i == 6 && dataSplit[i] == 1) {
						newCell.innerHTML = `
                                         	
                                         	<td><br><img src="./images/signalUnknown.png" alt"unknownSignal" style="width:70px;"><br/><b style="font-size:12px;">Unknown Signal</b></td>`

					}
					else if (i == 6 && dataSplit[i] == 2) {
						newCell.innerHTML = `
                                         	
                                  		<td><br><img src="./images/veryPoorSignal.png" alt"veryPoorSignal" style="width:40px;"><br/><b style="font-size:12px;">Very Poor Signal</b></td>`

					}
					else if (i == 6 && dataSplit[i] == 3) {
						newCell.innerHTML = `
                                         	
                                         	<td><img src="./images/poorSignal.png" alt"PoorSignal" style="width:40px;"><br/><b style="font-size:12px;">Poor Signal</b></td>`

					}
					else if (i == 6 && dataSplit[i] == 4) {
						newCell.innerHTML = `
                                         	
                                  		<td><br><img src="./images/fairSignal.png" alt"fairSignal" style="width:40px;"><br/><b style="font-size:12px;">Fair Signal</b></td>`

					}
					else if (i == 6 && dataSplit[i] == 5) {
						newCell.innerHTML = `
                                          	
                                   		<td><br><img src="./images/goodSignal.png" alt"goodSignal" style="width:40px;"><br/><b style="font-size:12px;">Good Signal</b></td>`

					} else if (i == 6 && dataSplit[i] == 6) {
						newCell.innerHTML = `
                                       	
                                		<td><br><img src="./images/strongSignal.png" alt"strongSignal" style="width:30px;"><br/><b style="font-size:12px;">Strong Signal</b></td>`

					}

					else if (i == 7 && dataSplit[i] == 0) {
						newCell.innerHTML = `
                                          	
                                   		<td><br><i class="fa-solid fa-shield-virus fa-2xl" style="color:#418529;"></i><br/><b style="font-size:12px;">Active</b></td>`

					} else if (i == 7 && dataSplit[i] == 1) {
						newCell.innerHTML = `
                                        	
                                     		<td><br><i class="fa-solid fa-shield-virus fa-2xl" style="color: #ba2626 ;"></i><br/><b style="font-size:12px;">Disarmed</b></td>`

					}





				}


				tableBody.appendChild(newRow);
			}
		}
	}


}


function getActiveZonesData() {



	return activeZonesList;
}

function getInActiveZonesData() {

	return inActiveZonesList;
}



/*----------------------------------------------JS for appending table in Modal for submiting the Alert---------------------------------------------------*/


function displayAlarmedZonesList(desiredNamedList, vendorNumber, alarmedZonesList, deviceId, fetchedBranch, vendorName, branchDetails) {
	//alert(vendorNumber)
	var tableBody = document.getElementById('example').getElementsByTagName('tbody')[0];
	tableBody.innerHTML = '';

	// Flatten desiredNamedList if it's nested
	let desiredValues = Array.isArray(desiredNamedList[0]) ? desiredNamedList.flat() : desiredNamedList;
	console.log("Processed desiredNames:", desiredValues);

	var uniqueZones = [...new Set(alarmedZonesList.flat())];
	var maxLength = Math.max(desiredValues.length, uniqueZones.length);

	for (let i = 0; i < maxLength; i++) {
		var newRow = document.createElement('tr');
		var serialNumberCell = document.createElement('td');
		serialNumberCell.textContent = i + 1;
		newRow.appendChild(serialNumberCell);
		var desiredNameCell = document.createElement('td');
		desiredNameCell.textContent = desiredValues[i] || ""; // Avoid undefined values
		newRow.appendChild(desiredNameCell);

		var vendorCell = document.createElement('td');
		vendorCell.textContent = branchDetails.vendorName;
		newRow.appendChild(vendorCell);
		/*<th>Vendor Number</th>*/

		var branchCell = document.createElement('td');
		branchCell.textContent = vendorNumber;
		newRow.appendChild(branchCell);

		var addressCell = document.createElement('td');
		addressCell.textContent = `${branchDetails.address1}, ${branchDetails.address2}, ${branchDetails.address3}, ${branchDetails.address4}`;
		newRow.appendChild(addressCell);

		var mobileCell = document.createElement('td');
		mobileCell.textContent = branchDetails.mobile;
		newRow.appendChild(mobileCell);
		var deviceIDCell = document.createElement('td');
		deviceIDCell.textContent = deviceId;
		newRow.appendChild(deviceIDCell);


		var alarmedZoneCell = document.createElement('td');
		alarmedZoneCell.textContent = uniqueZones[i] || "";
		newRow.appendChild(alarmedZoneCell);

		var remarkCell = document.createElement('td');
		var selectDropdown = document.createElement('select');
		selectDropdown.className = 'form-control';

		// If you want to prefill the input with a default remark, you can do:
		var remarksListInput = document.getElementById('remarksList');
		if (remarksListInput && remarksListInput.value) {
			var remarks = remarksListInput.value.replace(/[\[\]"]/g, '').split(',');

			remarks.forEach(function(remark) {
				var option = document.createElement('option');
				option.value = remark.trim();
				option.textContent = remark.trim();
				selectDropdown.appendChild(option);
			});
		}


		remarkCell.appendChild(selectDropdown);
		newRow.appendChild(remarkCell);


		var remarkInput = document.createElement('td');
		var remarkInputField = document.createElement('input');

		remarkInputField.type = 'text';
		remarkInputField.className = 'form-control';
		remarkInputField.style.width = '250px';
		remarkInputField.placeholder = 'Enter Remarks';

		remarkInput.appendChild(remarkInputField);
		newRow.appendChild(remarkInput);

		var actionCell = document.createElement('td');
		var submitButton = document.createElement('button');
		submitButton.type = 'button';
		submitButton.className = 'btn btn-primary';
		submitButton.textContent = 'Submit';
		actionCell.appendChild(submitButton);
		newRow.appendChild(actionCell);

		makeCall(uniqueZones[i], branchDetails.mobile, deviceId, fetchedBranch);

		tableBody.appendChild(newRow);
	}

	// Store global variables
	branchCodeGlobal = fetchedBranch;
	deviceIdGlobal = deviceId;
	fetchedBranchGlobal = fetchedBranch;
	branchDetailsGlobal = branchDetails;
	alarmedZonesListGlobal = alarmedZonesList;
	desiredNameGlobal = desiredNamedList;
}



document.addEventListener('click', function(event) {
	if (event.target.classList.contains('btn-primary')) {

		stopSoundAlert();
		var submitButton = event.target;
		var tableRow = submitButton.closest('tr');
				var zoneName = tableRow.children[1].textContent;
		var vendorName = tableRow.children[2].textContent;
		var branchName = tableRow.children[6].textContent;
		var branchCode = branchDetails.branchCode;
		var address = tableRow.children[4].textContent;
		var mobile = tableRow.children[3].textContent;
		var deviceId = tableRow.children[5].textContent;
		var alarmedZone = tableRow.children[7].textContent;

		var remarks = tableRow.children[8].textContent;
		var remarkDropdown = tableRow.querySelector('select.form-control');
		var selectedRemark = remarkDropdown.options[remarkDropdown.selectedIndex].textContent;

		var inputRemark = tableRow.children[9];
		var inputRemarkField = inputRemark ? inputRemark.querySelector('input.form-control') : null;
		var inputRemarkValue = inputRemarkField ? inputRemarkField.value : '';
		
   

		var userId = $('#userId').val();

		var auditData = {
			deviceId: deviceId,
			activeZone: alarmedZone,
			branchName: branchName,
			remarks: selectedRemark,
			remarks2: inputRemarkValue,
			user: userId,
			branchCode: branchCode,
			mobile: mobile,
			address: address,
			vendorName: vendorName,
			zoneName:zoneName
		};

		fetch('./saveAudit', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(auditData)
		})
			.then(response => response.json())
			.then(data => {
				console.log(data);


				// Once the submission is successful, remove the row from the modal
				tableRow.remove();

				// Find the modal by its ID
				var modal = document.getElementById(currentModalId);

				// Check if there are any remaining rows with data in the modal
				var remainingRows = modal.querySelectorAll('tbody tr:not(.header-row)').length;


				// If no remaining rows, remove the entire table
				if (remainingRows === 0) {


					// Remove the entire table
					modal.querySelector('table').remove();

					// Close the modal
					$(modal).modal('hide');
				}
			})
			.catch(error => {
				console.error('Error:', error);
			});
	}
});


/*-----------------------------------JS for Chart and functions for filter the chart by year month and date------------------------------------------------------*/
var currentYear = new Date().getFullYear();
var numberOfYears = 100;

var dropdownMenu = document.querySelector('.yearFilter');
for (var i = 0; i < numberOfYears; i++) {
	var yearLink = document.createElement('a');
	yearLink.classList.add('dropdown-item');

	yearLink.textContent = currentYear - i;
	dropdownMenu.appendChild(yearLink);
}




$(document).ready(function() {
	var chartInstance;

	function updatePieChart(data) {

		var nk = document.getElementById("sold-product");

		if (!data.payload || data.payload.length === 0) {
			var ctx = nk.getContext("2d");
			ctx.clearRect(0, 0, nk.width, nk.height);

			ctx.font = "20px Arial";
			ctx.textAlign = "center";
			ctx.fillText("No data found", nk.width / 2, nk.height / 2);
			/*document.getElementById("total").textContent = "0";*/
			return;
		}


		var dataValues = [
			0,
			0,
			0,
			0
		];

		data.payload.forEach(function(item) {
			switch (item.remarks) {
				case "ACKNOWLEDGED":
					dataValues[0] = item.count;
					break;
				case "FALSE ALARM":
					dataValues[1] = item.count;
					break;
				case "TESTING":
					dataValues[2] = item.count;
					break;
				case "SECURITY VENDOR INFORMED":
					dataValues[3] = item.count;
					break;
			}
		});

		/*var total = dataValues.reduce((a, b) => a + b, 0);
		document.getElementById("total").textContent = total;*/

		if (chartInstance) {
			chartInstance.destroy();
		}

		chartInstance = new Chart(nk, {
			type: 'doughnut',
			data: {
				defaultFontFamily: 'Poppins',
				datasets: [{
					data: dataValues,
					borderWidth: 0,
					backgroundColor: [
						"rgba(227, 50, 53, 0.8)",
						"rgba(25, 192, 115, 0.55)",
						"rgba(87, 192, 255, 0.57)",
						"rgba(210, 235, 66, 0.7)"
					],
					hoverBackgroundColor: [
						"rgba(227, 50, 53, 0.98)",
						"rgba(25, 192, 115, 0.87)",
						"rgba(87, 192, 255, 0.88)",
						"rgba(219, 250, 44, 0.98)"
					]
				}],
				labels: ["Acknowledged", "False ", "Testing", "Security Vendor Informed"]
			},
			options: {
				responsive: true,
				legend: false,
				maintainAspectRatio: false,

			}
		});



	}

	$.ajax({
		url: './getStatusCountByDateRange',
		type: 'GET',
		data: {
			startDate: $('#startDate').val(),
			endDate: $('#endDate').val()
		},
		success: function(response) {
			if (response && response.statusCode === 200) {
				updatePieChart(response);
			} else {
				updatePieChart(response);
				console.error('Error fetching data:', response.message);
			}
		},
		error: function(xhr, status, error) {
			console.error('AJAX error:', error);
		}
	});

	$('#endDate').change(function() {
		var selectedDate = $(this).val();

		$.ajax({
			url: './getStatusCountByDateRange',
			type: 'GET',
			data: {
				startDate: $('#startDate').val(),
				endDate: $('#endDate').val()
			},
			success: function(response) {
				if (response && response.statusCode === 200) {
					updatePieChart(response);
				} else {
					updatePieChart(response);
					console.error('Error fetching data:', response.message);
				}
			},
			error: function(xhr, status, error) {
				console.error('AJAX error:', error);
			}
		});
	});

	function fetchDataByYear(year) {
		$.ajax({
			url: './getStatusCountByYear',
			type: 'GET',
			data: {
				year: year
			},
			success: function(response) {
				if (response && response.statusCode === 200) {
					updatePieChart(response);
				} else {
					updatePieChart(response);
					console.error('Error fetching data:', response.message);
				}
			},
			error: function(xhr, status, error) {
				console.error('AJAX error:', error);
			}
		});
	}


	$('.dropdown-menu.yearFilter').on('click', '.dropdown-item', function(event) {
		var selectedYear = $(this).text();
		$('.btn.btn-primary.dropdown-toggle.year-dropdown').text(selectedYear);
		fetchDataByYear(selectedYear);
	});


	$(document).ready(function() {
		$('.activeZoneFilter').click(function() {
			var selectedDevice = $(this).text();
			$('.btn.btn-primary.dropdown-toggle.active-device').text(selectedDevice);
			fetchDataByDevice(selectedDevice);
		});



		function fetchDataByDevice(device) {
			$.ajax({
				url: './getStatusCountByActiveZones',
				type: 'GET',
				data: {
					activeZone: device
				},
				success: function(response) {
					if (response && response.statusCode === 200) {
						updatePieChart(response);
					} else {
						updatePieChart(response);
						console.error('Error fetching data:', response.message);
					}
				},
				error: function(xhr, status, error) {
					console.error('AJAX error:', error);
				}
			});
		}
	});


	$('#monthFilter').change(function() {
		var selectedMonthYear = $(this).val().split('-');
		var selectedMonth = parseInt(selectedMonthYear[1]);
		var selectedYear = parseInt(selectedMonthYear[0]);

		fetchDataByMonthAndYear(selectedMonth, selectedYear);
	});



	function fetchDataByMonthAndYear(month, year) {
		$.ajax({
			url: './getStatusCountByMonth',
			type: 'GET',
			data: {
				month: month,
				year: year
			},
			success: function(response) {
				if (response && response.statusCode === 200) {
					updatePieChart(response);
				} else {
					console.error('Error fetching data:', response.message);
				}
			},
			error: function(xhr, status, error) {
				console.error('AJAX error:', error);
			}
		});
	}

	var currentMonthNumber = currentDate.getMonth() + 1;
	var currentYear = currentDate.getFullYear();

	fetchDataByMonthAndYear(currentMonthNumber, currentYear);
});




/*----------------------------------------JS for update table list in piechart and filter data on change of date--------------------------------------*/

$(document).ready(function() {


	function fetchDataByActiveZones(activeZone) {
		$.ajax({
			url: './getAllRecordsByActiveZone',
			type: 'GET',
			data: {
				activeZone: activeZone
			},
			success: function(response) {
				if (response && response.statusCode === 200) {
					updateTable(response);
				} else {
					updateTable(response);
					console.error('Error fetching data:', response.message);
				}
			},
			error: function(xhr, status, error) {
				console.error('AJAX error:', error);
			}
		});
	}

	// Update data when active zone filter changes
	$('.activeZoneFilter').click(function() {
		var selectedDevice = $(this).text();
		fetchDataByActiveZones(selectedDevice);
	});

	function fetchDataByDevice(deviceId) {
		$.ajax({
			url: './getAllRecordsByDevice',
			type: 'GET',
			data: {
				deviceId: deviceId
			},
			success: function(response) {
				if (response && response.statusCode === 200) {
					updateTable(response);
				} else {
					updateTable(response);
					console.error('Error fetching data:', response.message);
				}
			},
			error: function(xhr, status, error) {
				console.error('AJAX error:', error);
			}
		});
	}

	// Update data when device filter changes
	$('.deviceFilter .dropdown-item').click(function() {
		var selectedDevice = $(this).text();
		fetchDataByDevice(selectedDevice);
		$('.active-device').text(selectedDevice);
	});
});



document.addEventListener('DOMContentLoaded', function() {
	document.querySelectorAll('#branchTableBody tr').forEach(function(row) {
		row.addEventListener('click', function(event) {

			const caretIcon = row.querySelector('.fa-caret-right');
			if (caretIcon) {
				caretIcon.classList.toggle('fa-caret-down');
			}


			highlightFunction(event);
		});
	});
});

function highlightFunction(event) {

	var highlightedRows = document.querySelectorAll('#branchTableBody .highlightedClicked-tr');
	highlightedRows.forEach(function(row) {
		row.classList.remove('highlightedClicked-tr');
	});


	var clickedRow = event.currentTarget;
	clickedRow.classList.add('highlightedClicked-tr');
}




//--------------------------------------------------- JavaScript for generating Zone Status Report----------------------------------------------------//

let apiResponse = {}; // Initialize apiResponse as an object

function getZoneStatusCounts() {
	const branchCode = $('#loggedInBranch').val(); // Get branchCode from input element

	$.ajax({
		url: './getCountsByZoneStatus', // URL of the API endpoint
		type: 'GET', // HTTP method
		data: { branchCode: branchCode }, // Send the branchCode as a query parameter
		success: function(response) {
			console.log('API Response:', response);

			if (response.statusCode === 200 && response.payload) {
				apiResponse = response.payload; // Assign the payload to apiResponse
				updateCharts(); // Call the function to update the charts
			} else {
				alert('Error fetching data: ' + (response.message || 'Invalid response'));
			}
		},
		error: function(xhr, status, error) {
			console.error('AJAX Error:', error);
			alert('Error: ' + error);
		}
	});
}

function createDoughnutChart(ctx, data, label) {
	// Calculate the sum of statuses
	const total = (data.Working || 0) + (data['Not Working'] || 0) + (data['Partially Working'] || 0);

	const chartData = total === 0
		? {
			labels: ['No Data'],
			datasets: [{
				label: `${label} Status`,
				data: [1], // Single slice
				backgroundColor: ['#D9EAFD'],
				borderColor: ['#B0BEC5']
			}]
		}
		: {
			labels: ['Working', 'Not Working', 'Partially Working'],
			datasets: [{
				label: `${label} Status`,
				data: [
					data.Working || 0,
					data['Not Working'] || 0,
					data['Partially Working'] || 0
				],
				backgroundColor: ['#5DB996', '#FB4141', '#ECE852'],
				borderColor: ['#118B50', 'CRIMSON', '#FCC737']
			}]
		};

	const chart = new Chart(ctx, {
		type: 'doughnut',
		data: chartData,
		options: {
			responsive: true,
			plugins: {
				legend: {
					display: false // Hides the legend
				},
				tooltip: {
					callbacks: {
						label: function(tooltipItem) {
							return total === 0
								? `No Data`
								: `${tooltipItem.label}: ${tooltipItem.raw}`;
						}
					}
				}
			},
			cutout: '70%',
			onClick: function(e) {
				if (total === 0) {
					alert(`No data available for ${label}`);
					return;
				}

				const activePoints = chart.getElementsAtEventForMode(e, 'nearest', { intersect: true }, true);
				if (activePoints.length > 0) {
					const index = activePoints[0].index;
					const status = ['Working', 'Not Working', 'Partially Working'][index];
					fetchZoneData(label, status);
				}
			}
		},
		plugins: [
			{
				id: 'centerText',
				beforeDraw: function(chart) {
					const { width } = chart;
					const { height } = chart;
					const { ctx } = chart;
					ctx.save();
					ctx.font = 'Normal 20px Poppins';
					ctx.fillStyle = '#000'; // Text color
					ctx.textAlign = 'center';
					ctx.textBaseline = 'middle';
					ctx.fillText(total || '0', width / 2, height / 2); // Draw total in the center
					ctx.restore();
				}
			}
		]
	});

	// Add a custom label below the chart (device name)
	const labelContainer = document.createElement('div');
	labelContainer.classList.add('device-name-label', 'text-dark', 'text-center');
	labelContainer.innerHTML = `${label}`;
	document.getElementById(`${label.toLowerCase().replace(' ', '-')}-chart-container`).append(labelContainer);
}


function updateCharts() {
	if (!Object.keys(apiResponse).length) {
		console.error('API response is empty.');
		return;
	}

	const charts = {
		cctv: 'CCTV',
		securityAlarm: 'Security Alarm',
		fireAlarm: 'Fire Alarm',
		bacs: 'BACS',
		etl: 'ETL'
	};

	Object.keys(charts).forEach(chartId => {
		const ctx = document.getElementById(`${chartId}Chart`);
		if (ctx) {
			createDoughnutChart(ctx.getContext('2d'), apiResponse[charts[chartId]] || {}, charts[chartId]);
		} else {
			console.warn(`Canvas element for ${chartId}Chart is missing.`);
		}
	});
}

function fetchZoneData(zoneType, status) {
	const branchCode = $('#loggedInBranch').val();

	$.ajax({
		url: './countingZonesByStatus',
		type: 'GET',
		data: {
			branchCode: branchCode,
			zoneType: zoneType,
			status: status
		},
		success: function(response) {
			if (response.statusCode === 200 && response.payload) {
				populateTableForChart(response.payload, zoneType, status); // Pass zoneType and status
			} else {
				alert('Error fetching zone data: ' + (response.message || 'Invalid response'));
			}
		},
		error: function(xhr, status, error) {
			console.error('AJAX Error:', error);
			alert('Error: ' + error);
		}
	});
}


function populateTableForReport(data) {
	const tableBody = $('#reportTableBody');
	tableBody.empty();

	if (!data || !data.length) {
		const noDataRow = $('<tr></tr>');
		const noDataCell = $('<td></td>').attr('colspan', 5).text('No data available');
		noDataRow.append(noDataCell);
		tableBody.append(noDataRow);
		return;
	}

	data.forEach(function(item, index) {
		const row = $('<tr></tr>');

		// Hidden branchCode cell
		const branchCodeCell = $('<td></td>').css('display', 'none').text(item.branchCode);
		row.append(branchCodeCell);

		// Index cell
		const indexCell = $('<td></td>').css('text-align', 'right').text(index + 1);
		row.append(indexCell);

		// Branch name cell
		const branchNameCell = $('<td></td>').addClass('text-left').text(item.branchName);
		row.append(branchNameCell);

		// Bank name cell
		const bankCell = $('<td></td>').text(item.bank);
		row.append(bankCell);

		// Mobile number cell
		const mobileCell = $('<td></td>').css('text-align', 'right').text(item.mobile);
		row.append(mobileCell);

		// Action button cell
		const buttonCell = $('<td></td>');
		const button = $('<button></button>')
			.addClass('btn btn-sm btn-primary')
			.text('View Zones')
			.on('click', function() {
				fetchLatestDataNew(item.branchCode, row);
				toggleZoneTableForReport(row, item.zoneDTOList);
			});
		buttonCell.append(button);
		row.append(buttonCell);

		tableBody.append(row);
	});
}



function toggleCollapseAndScroll() {
	$('#collapseTwo').collapse('show');

	setTimeout(function() {
		$('html, body').animate({
			scrollTop: $('#collapseTwo').offset().top
		}, 800);
	}, 300);
}



function populateTableForChart(data, zoneType, status) {
	//alert(JSON.stringify(data))
	const tableBody = $('#reportTableBody');
	tableBody.empty();

	if (!data || !data.length) {
		const noDataRow = $('<tr></tr>');
		const noDataCell = $('<td></td>').attr('colspan', 5).text('No data available');
		noDataRow.append(noDataCell);
		tableBody.append(noDataRow);
		return;
	}

	data.forEach(function(item, index) {
		const row = $('<tr></tr>');

		// Hidden branchCode cell
		const branchCodeCell = $('<td></td>').css('display', 'none').text(item.branchCode);
		row.append(branchCodeCell);

		// Index cell
		const indexCell = $('<td></td>').css('text-align', 'right').text(index + 1);
		row.append(indexCell);

		// Branch name cell
		const branchNameCell = $('<td></td>').addClass('text-left').text(item.branchName);
		row.append(branchNameCell);

		// Bank name cell
		const bankCell = $('<td></td>').text(item.bank);
		row.append(bankCell);

		// Mobile number cell
		const mobileCell = $('<td></td>').css('text-align', 'right').text(item.mobile);
		row.append(mobileCell);

		// Action button cell
		const buttonCell = $('<td></td>');

		// View Zones button
		const viewZonesButton = $('<button></button>')
			.addClass('btn btn-sm btn-primary')
			.text('View Zones')
			.on('click', function() {

				$('.zone-details-row').remove();
				$('.detail-row').remove();


				if (row.next('.zone-details-row').length && row.next('.detail-row').length) {
					row.next('.zone-details-row').remove();
					row.next('.detail-row').remove();
				} else {
					fetchLatestDataNew(item.branchCode, row);
					toggleZoneTable(row, item.zoneDTOList);
				}
			});

		buttonCell.append(viewZonesButton);



		if (zoneType === 'CCTV' && ['Working', 'Not Working', 'Partially Working'].includes(status)) {
  	const dvrDetailsButton = $('<button></button>')
	.addClass('btn btn-sm btn-secondary ml-2')
	.text('DVR Details')
	.prop('disabled', item.branchCode !== 'BR-16') // disable if not BR-16
	.on('click', function() {
		if (item.branchCode !== 'BR-16') return;
            const url = "http://192.168.1.50:8025/dahua/api/dhauha/getCameraInfo?panelSerialNo=1";

            // Encode the URL
            const encodedUrl = encodeURIComponent(url);

            $.ajax({
                url: `./proxy/proxy?url=${encodedUrl}`,
                type: 'GET',
                success: function(response) {
					
                   if (response.valid && response.details) {
                        const dvrDetails = response.details[0];

								const modal = $(`
<div class="modal fade" id="dvrDetailsModal" tabindex="-1" aria-labelledby="dvrDetailsModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="dvrDetailsModalLabel">DVR Details</h5>
                <button type="button" class="btn btn-secondary btn-sm" id="viewSnapshotButton">View Snapshot</button>
           
            </div>
            
            <div class="modal-body" style="max-height: 70vh; overflow-y: auto; cursor:pointer;">
                <!-- DVR Details Content -->
                <div class="mb-2" style="padding: 2px; border-bottom: 1px solid #ccc; font-size:13px;">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <strong style="font-size: 15px; color: black;">Branch Name</strong>
                        <span class="my-2 p-2" style="background-color:#F1EAFF; color:#7743DB; font-size: 15px; border-radius:7px;">
                            ${item.branchName}
                        </span>
                        <strong style="font-size: 15px; color: black;">Device Serial No</strong>
                        <span class="p-2" style=" background-color:#F1EAFF; color:#7743DB; font-size: 15px; border-radius:7px;">
                            ${item.deviceSerialNo}
                        </span>
                  
                    </div>
                </div>
                
                <div class="mt-1" style="padding: 3px; border-bottom: 1px solid #ccc; font-size:13px;">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <strong style="font-size: 15px; color: black;">NVR Name</strong>
                        <span class="my-2 p-2" style=" background-color:#F6F5F2; padding:4px; color:#028bd7; font-size: 15px; border-radius:7px;">
                            ${dvrDetails.nvrName || 'N/A'}
                        </span>
                    </div>
                    
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <strong style="font-size: 15px; color: black;">Device Type</strong>
                        <span class=" my-2 p-2" style=" background-color:#F6F5F2; padding:4px; color:#028bd7; font-size: 15px; border-radius:7px;">
                            ${dvrDetails.deviceType || 'N/A'}
                        </span>
                    </div>
                    
                  
                </div>
                
                <div class="mt-1" style="padding: 3px; border-bottom: 1px solid #ccc; font-size:13px;">
                
                  <div style="display: flex; justify-content: space-between; align-items: center;">
                        <strong style="font-size: 15px; color: black;">Vendor</strong>
                        <span class="my-2 p-2" style="background-color:#F6F5F2; padding:4px; color:#028bd7; font-size: 15px; border-radius:7px;">
                            ${dvrDetails.vendorInfo || 'N/A'}
                        </span>
                    </div> 
                    
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <strong style="font-size: 15px; color: black;">NVR Time</strong>
                        <span class="my-2 p-2" style="background-color:#F6F5F2; padding:4px; color:#028bd7; font-size: 15px; border-radius:7px;">
                            ${dvrDetails.nvrTime || 'N/A'}
                        </span>
                    </div>
             
                </div>
                
                
                   <div class="mt-1" style="padding: 3px; border-bottom: 1px solid #ccc; font-size:13px;">
                     <div style="display: flex; justify-content: space-between; align-items: center;">
                        <strong style="font-size: 15px; color: black;">NVR Type</strong>
                        <span class="my-2 p-2" style="background-color:#F6F5F2; padding:4px;  color:#028bd7; font-size: 15px;">
                            ${dvrDetails.nvrType || 'N/A'}
                        </span>
                    </div>
                    
                     <div style="display: flex; justify-content: space-between; align-items: center;">
                        <strong style="font-size: 15px; color: black;">Update Serial</strong>
                        <span class="my-2 p-2" style="background-color:#F6F5F2; padding:4px;  color:#028bd7; font-size: 15px;">
                            ${dvrDetails.updateSerial || 'N/A'}
                        </span>
                    </div>
                </div>
                
                
                
                <div class="mt-1" style="padding: 3px; border-bottom: 1px solid #ccc; font-size:13px;">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <strong style="font-size: 15px; color: black;">Processor</strong>
                        <span class="my-2 p-2" style="background-color:#F6F5F2; padding:4px; color:#028bd7; font-size: 15px; border-radius:7px;">
                            ${dvrDetails.processor || 'N/A'}
                        </span>
                    </div>
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <strong style="font-size: 15px; color: black;">Software Info</strong>
                        <span class="my-2 p-2" style=" background-color:#F6F5F2; padding:4px; color:#028bd7; font-size: 15px; border-radius:7px;">
                            ${dvrDetails.softwareInfo || 'N/A'}
                        </span>
                    </div>
                </div>
                
                <div class="mt-1" style="padding: 3px; border-bottom: 1px solid #ccc; font-size:13px;">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <strong style="font-size: 15px; color: black;">Hardware Version</strong>
                        <span class="my-2 p-2" style=" background-color:#F6F5F2; padding:4px; color:#028bd7; font-size: 15px; border-radius:7px;">
                            ${dvrDetails.hardwareVersion || 'N/A'}
                        </span>
                    </div>
                </div>
            </div>

     
            <div class="modal-footer">
             <button type="button" class="btn btn-secondary" id="openChannelInfo">Channel Info</button>
            <button type="button" class="btn btn-secondary" id="openStorageInfo">Storage Info</button>
            <button type="button" class="btn btn-danger" data-bs-dismiss="modal">Close</button>
                 
            </div>

        </div>
    </div>
</div>

`);

								$('body').append(modal);
								
								
								
						

$(document).on('click', '#openChannelInfo', function () {
    // Create and display the Channel Info modal

const channelInfoModal = $(`
    <div class="modal fade" id="channelInfoModal" tabindex="-1" aria-labelledby="channelInfoModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-xl">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="channelInfoModalLabel">Channel Info</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body" style="min-height: 72vh; max-height: 72vh; overflow-y: auto; cursor:pointer;">
                    <div id="channelSummary" class="mb-3"></div>
                    <div class="table-responsive">
                        <table class="table table-bordered">
                            <thead class="thead-primary">
                                <tr>
                                    <th>#</th>
                                    <th>Name</th>
                                    <th>IP Address</th>
                                    <th>MAC</th>
                                    <th>Channel Name</th>
                                    
                                    <th>Vendor</th>
                                    <th>Enabled</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody id="cameraDetailsTable"></tbody>
                        </table>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-danger" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
`);

// Append the modal to the body
$('body').append(channelInfoModal);

// Show the modal
$('#channelInfoModal').modal('show');
const url = "http://localhost:8025/dahua/api/dhauha/getChannelInfo?panelSerialNo=1";

            // Encode the URL
            const encodedUrl = encodeURIComponent(url);

            
// Fetch data from the API
$.ajax({
    url: `./proxy/proxy?url=${encodedUrl}`,
    type: 'GET',
    data: { panelSerialNo: item.deviceSerialNo },
    success: function (response) {
        if (response.valid) {
            const details = response.details[0];
            const totalCameras = details.totalCamera || 0;
            const connectedCameras = details.connectCamera || 0;
            const disconnectedCameras = details.disconnectCamera || 0;

            // Update the summary section

			$('#channelSummary').html(`
    <div class="row">
        <div class="col p-2 mx-2" style="background-color: #AFDDFF; color: #27548A; border-radius:10px;">
            <strong>Total Cameras: ${totalCameras}</strong> 
        </div>
        <div class="col p-2 mx-2" style="background-color: #B3D8A8; color: #3D8D7A; border-radius:10px;">
            <strong>Connected Cameras: ${connectedCameras}</strong> 
        </div>
        <div class="col p-2 mx-2" style="background-color: #F7A4A4; color: #A31D1D; border-radius:10px;">
            <strong>Disconnected Cameras: ${disconnectedCameras}</strong> 
        </div>
    </div>
`);


            // Populate the table with camera details
            const cameraDetails = details.cameraChannelDetails || [];
            const tableBody = $('#cameraDetailsTable');
            tableBody.empty(); // Clear any existing rows

            cameraDetails.forEach((camera, index) => {
                const statusBadge = camera.connected
                    ? '<span class="badge bg-success text-light">Connected</span>'
                    : '<span class="badge bg-danger text-light">Disconnected</span>';

                const enabledBadge = camera.enabled
                    ? '<span class="badge bg-success text-light">Yes</span>'
                    : '<span class="badge bg-danger text-light">No</span>';

                tableBody.append(`
                    <tr class="text-dark">
                        <td>${index + 1}</td>
                        <td>${camera.name || 'N/A'}</td>
                        <td>${camera.ipAddress || 'N/A'}</td>
                        <td>${camera.mac || 'N/A'}</td>
                        <td>${camera.channelName || 'N/A'}</td>
                        <td>${camera.vendor || 'N/A'}</td>
                        <td>${enabledBadge}</td>
                        <td>${statusBadge}</td>
                    </tr>
                `);
            });
        } else {
            $('#channelSummary').html('<p class="text-danger">Invalid response from the server.</p>');
        }
    },
    error: function (xhr, status, error) {
        console.error('Error fetching channel info:', error);
        $('#channelSummary').html('<p class="text-danger">Failed to load channel info.</p>');
    }
});

// Remove the modal from the DOM after it is hidden
$('#channelInfoModal').on('hidden.bs.modal', function () {
    $(this).remove();
});

});
	



$('#openStorageInfo').on('click', function() {
    const storageUrl = "http://localhost:8025/dahua/api/dhauha/getStorageInfo?panelSerialNo=1";
    const daysUrl = "http://localhost:8025/dahua/api/dhauha/getTotalDaysOfRecordingByStorage?panelSerialNo=1";

    // Encode the URL
    const encodedStorageUrl = encodeURIComponent(storageUrl);
    const encodedDaysUrl = encodeURIComponent(daysUrl);

    // Fetch data from the storage API
    $.ajax({
        url: `./proxy/proxy?url=${encodedStorageUrl}`,
        type: 'GET',
        data: { panelSerialNo: item.deviceSerialNo },
        success: function(storageResponse) {
            if (storageResponse.valid && storageResponse.details && storageResponse.details.length > 0) {
                const storageDetails = storageResponse.details[0]; // Assuming the first array contains the storage details
                
                // Fetch data from the total days API
                $.ajax({
                    url: `./proxy/proxy?url=${encodedDaysUrl}`,
                    type: 'GET',
                    data: { panelSerialNo: item.deviceSerialNo },
                    success: function(daysResponse) {
                        if (daysResponse.valid && daysResponse.details && daysResponse.details.length > 0) {
                            const totalDays = daysResponse.details[0]; // Get total days from the API response

                            // Create the new modal with the additional days information
                            const newModal = $(`
                                <div class="modal fade" id="newModal" tabindex="-1" aria-labelledby="newModalLabel" aria-hidden="true">
                                    <div class="modal-dialog modal-xl">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <h5 class="modal-title" id="newModalLabel">Storage Info</h5>
                                                <div style="position: absolute; right: 20px; top: 20px; font-size: 14px; color:black;">
                                                    Total Days of Recording: ${totalDays} Days
                                                </div>
                                            </div>
                                            <div class="modal-body" style="max-height: 72vh; min-height:72vh; overflow-y: auto; cursor:pointer;">
                                                <div class="table-responsive">
                                                    <table class="table table-bordered">
                                                        <thead class="thead-primary">
                                                            <tr>
                                                                <th>S No.</th>
                                                                <th>SATA Name</th>
                                                                <th>Total Bytes</th>
                                                                <th>Bytes Used</th>
                                                                <th>Type</th>
                                                                <th>Usage (%)</th>
                                                                <th>Status</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            ${Object.entries(storageDetails)
                                                                .sort((a, b) => {
                                                                    const aIndex = parseInt(a[0].match(/\d+/));
                                                                    const bIndex = parseInt(b[0].match(/\d+/));
                                                                    return aIndex - bIndex;
                                                                })
                                                                .map(([key, item], index) => {
                                                                    const totalBytes = (parseFloat(item.totalBytes) || 0).toFixed(2);
                                                                    const usedBytes = (parseFloat(item.usedBytes) || 0).toFixed(2);
                                                                    const isHddError = parseFloat(totalBytes) <= 0;
                                                                    return `
                                                                        <tr class="text-dark">
                                                                            <td>${index + 1}</td>
                                                                            <td>${item.name || 'N/A'}</td>
                                                                            <td>${totalBytes} GB</td>
                                                                            <td>${usedBytes} GB</td>
                                                                            <td>ReadWrite</td>
                                                                            <td>${item.usagePercentage || '0'}</td>
                                                                            <td>
                                                                                <span class="badge" style="background-color: ${isHddError ? '#A31D1D' : '#48A6A7'}; color: white;">
                                                                                    ${isHddError ? 'HDD Error' : 'Working'}
                                                                                </span>
                                                                            </td>
                                                                        </tr>
                                                                    `;
                                                                }).join('')}
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                            <div class="modal-footer">
                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            `);

                            // Append the new modal to the body
                            $('body').append(newModal);

                            // Show the new modal
                            $('#newModal').modal('show');
                        } else {
                            alert('No total days of recording data found.');
                        }
                    },
                    error: function(xhr, status, error) {
                        console.error('Error fetching total days of recording:', error);
                        alert('Failed to fetch total days of recording.');
                    }
                });
            } else {
                alert('No storage details found.');
            }
        },
        error: function(xhr, status, error) {
            console.error('Error fetching storage details:', error);
            alert('Failed to fetch storage details.');
        }
    });
});





								// Add event listener for the "View Snapshot" button
								$('#viewSnapshotButton').on('click', function() {
									const snapshotModal = $(`
    <div class="modal fade" id="snapshotModal" tabindex="-1" aria-labelledby="snapshotModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-xl">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="snapshotModalLabel">DVR Snapshot</h5>
                    
                </div>
                <div class="modal-body text-center" style="max-height: 75vh; overflow-y: auto; cursor:pointer;">
                   
<img id="dvrSnapshotImage" src="" alt="DVR Snapshot" style="max-width: 100%;" />

                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
    `);
    
    

									// Append the snapshot modal to the body
									$('body').append(snapshotModal);

const url = "http://localhost:8025/dahua/api/dhauha/getCameraLiveSnapShoot?pannelSerialNo=1&channel=1";
const encodedUrl = encodeURIComponent(url);

// Fetch data from the API
$.ajax({
    url: `./proxy/image?url=${encodedUrl}`,  // Make sure this hits the correct proxy path
    type: 'GET',
    dataType: 'json', // Expecting JSON response
    success: function(response) {
        if (response.imageBase64) {
            const imageSrc = `data:image/png;base64,${response.imageBase64}`;
            $('#dvrSnapshotImage').attr('src', imageSrc);
            $('#snapshotModal').modal('show');
        } else {
            console.error('Image data not found in response');
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Snapshot data is missing.',
                confirmButtonColor: '#d33'
            });
        }
    },
    error: function(xhr, status, error) {
        console.error('Error fetching snapshot:', error);
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'Failed to load snapshot.',
            confirmButtonColor: '#d33'
        });
    }
});

  
								});

								// Show the DVR Details modal
								$('#dvrDetailsModal').modal('show');
							}
						},
						error: function(xhr, status, error) {
							console.error('Error fetching DVR details:', error);
							Swal.fire({
								icon: 'error',
								title: 'Error',
								text: 'No NVR Found.',
								confirmButtonColor: '#d33'
							});
						}

					});
				});

			buttonCell.append(dvrDetailsButton);
		}



		row.append(buttonCell);
		tableBody.append(row);
	});
}


function toggleZoneTable(parentRow, zones) {

	$('.zone-details-row').remove();
	$('.detail-row').remove();

	if ($(parentRow).next('.zone-details-row').length && $(parentRow).next('.detail-row').length) {

		$(parentRow).next('.zone-details-row').remove();
		$(parentRow).next('.detail-row').remove();
		return;
	}

	// Create a new row for the sub-table
	const zoneRow = $('<tr></tr>').addClass('zone-details-row');
	const zoneCell = $('<td></td>').attr('colspan', 6);

	// Create the sub-table
	const zoneTable = $('<table></table>').addClass('table table-bordered zone-table');

	// Header with close icon in the last column
	const zoneHeader = $('<thead class="thead-primary"><tr><th>Alert Type</th><th>Zone Type</th><th>Zone</th><th colspan="2">Status</th></tr></thead>');

	// Add close icon to the last column of the header row
	const closeIcon = $('<span></span>')
		.addClass('close-icon')
		.html('&#10005;')
		.css({
			'cursor': 'pointer',
			'font-size': '20px',
			'font-weight': 'bold',
			'color': '#FFFFFF',
			'margin-left': '200px'
		})
		.on('click', function() {
			$(zoneRow).remove(); // Close the zone table when cross is clicked
		});

	// Add close icon to the last `<th>` element
	zoneHeader.find('th:last').append(closeIcon);

	zoneTable.append(zoneHeader);

	const zoneBody = $('<tbody class="text-dark" style="font-size:13px;"></tbody>');
	zones.forEach(function(zone) {
		const zoneRow = $('<tr></tr>');
		const zoneCell = $('<td class="col-3"></td>').text(zone.zone);
		const zoneTypeCell = $('<td class="col-3"></td>').text(zone.zoneType);
		const alertTypeCell = $('<td class="col-3"></td>').text(zone.alertType);
		const statusText = zone.status === 'N' ? 'Working' : 'Not Working'; // Determine display text
		const statusCell = $('<td class="col-3" colspan="2"></td>')
			.css("padding", "8px")
			.append(
				$("<span>")
					.text(statusText) // Use display text
					.css(getStatusStyles(statusText))
			);

		zoneRow.append(alertTypeCell, zoneTypeCell, zoneCell, statusCell);
		zoneBody.append(zoneRow);
	});

	zoneTable.append(zoneBody);
	zoneCell.append(zoneTable);
	zoneRow.append(zoneCell);

	// Insert the sub-table row after the parent row
	$(parentRow).after(zoneRow);
}



function toggleZoneTableForReport(parentRow, zones) {
	//alert(JSON.stringify(zones));

	$('.zone-details-row').remove();
	$('.detail-row').remove();

	if ($(parentRow).next('.zone-details-row').length && $(parentRow).next('.detail-row').length) {
		$(parentRow).next('.zone-details-row').remove();
		$(parentRow).next('.detail-row').remove();
		return;
	}

	// Create a new row for the sub-table
	const zoneRow = $('<tr></tr>').addClass('zone-details-row');
	const zoneCell = $('<td></td>').attr('colspan', 6);

	// Create the sub-table
	const zoneTable = $('<table></table>').addClass('table table-bordered zone-table');

	// Header with close icon in the last column
	const zoneHeader = $('<thead class="thead-primary"><tr><th>Alert Type</th><th>Zone Type</th><th>Zone</th><th colspan="2">Status</th></tr></thead>');

	// Add close icon to the last column of the header row
	const closeIcon = $('<span></span>')
		.addClass('close-icon')
		.html('&#10005;')
		.css({
			'cursor': 'pointer',
			'font-size': '20px',
			'font-weight': 'bold',
			'color': '#FFFFFF',
			'margin-left': '200px'
		})
		.on('click', function() {
			$(zoneRow).remove(); // Close the zone table when cross is clicked
		});

	// Add close icon to the last `<th>` element
	zoneHeader.find('th:last').append(closeIcon);

	zoneTable.append(zoneHeader);

	const zoneBody = $('<tbody class="text-dark" style="font-size:13px;"></tbody>');

	zones.forEach(function(zone) {
		const zoneRow = $('<tr></tr>');
		const zoneCell = $('<td class="col-3"></td>').text(zone.zone);
		const zoneTypeCell = $('<td class="col-3"></td>').text(zone.zoneType);
		const alertTypeCell = $('<td class="col-3"></td>').text(zone.alertType);

		// Interpret status for "Partially Working (A)" and "Partially Working (N)"
		let interpretedStatus = zone.status;
		if (zone.status.includes('Partially Working (A)') || zone.status.includes('Partially Working (D)')) {
			interpretedStatus = 'Partially Working (Not Working)';
		} else if (zone.status.includes('Partially Working (N)')) {
			interpretedStatus = 'Partially Working (Working)';
		}

		const statusCell = $('<td class="col-3" colspan="2"></td>')
			.css("padding", "8px")
			.append(
				$("<span>")
					.text(interpretedStatus)
					.css(getStatusStyles(interpretedStatus))
			);

		zoneRow.append(alertTypeCell, zoneTypeCell, zoneCell, statusCell);
		zoneBody.append(zoneRow);
	});

	zoneTable.append(zoneBody);
	zoneCell.append(zoneTable);
	zoneRow.append(zoneCell);

	// Insert the sub-table row after the parent row
	$(parentRow).after(zoneRow);
}




function getStatusStyles(status) {
	switch (status.toLowerCase()) {
		case 'working':
			return {
				"color": "white",
				"background-color": "#118B50",
				"text-align": "center",
				"padding": "2px 6px",
				"border-radius": "4px",
				"display": "inline-block"
			};
		case 'not working':
			return {
				"color": "white",
				"background-color": "crimson",
				"text-align": "center",
				"padding": "2px 6px",
				"border-radius": "4px",
				"display": "inline-block"
			};
		case 'partially working':
			return {
				"color": "black",
				"background-color": "#FCC737",
				"text-align": "center",
				"padding": "2px 6px",
				"border-radius": "4px",
				"display": "inline-block"
			};
		case 'partially working (not working)':
			return {
				"color": "black",
				"background-color": "#FCC737",
				"text-align": "center",
				"padding": "2px 6px",
				"border-radius": "4px",
				"display": "inline-block"
			};
		case 'partially working (working)':
			return {
				"color": "black",
				"background-color": "#FCC737",
				"text-align": "center",
				"padding": "2px 6px",
				"border-radius": "4px",
				"display": "inline-block"
			};
		default:
			return {
				"color": "black",
				"background-color": "gray",
				"text-align": "center",
				"padding": "2px 6px",
				"border-radius": "4px",
				"display": "inline-block"
			};
	}
}


// Initialize everything when the window is loaded
window.onload = function() {
	getZoneStatusCounts();
};





// Function to call the API
function fetchZonesForReportTable() {
	const branchCode = $('#loggedInBranch').val();
	var zoneType = null;
	var status = null;

	$.ajax({
		url: './countingZonesForReportTable', // API endpoint
		type: 'GET',
		data: {
			branchCode: branchCode,
			zoneType: zoneType, // Optional
			status: status // Optional
		},
		success: function(response) {
			if (response.statusCode === 200) {
				// console.log('Data fetched successfully:', response.payload);

				//alert(JSON.stringify(response.payload))
				populateTableForReport(response.payload);

				// renderZonesData(response.payload); // Call a function to process/display data
			} else {
				console.error('Error:', response.message);
				alert('Failed to fetch data: ' + response.message);
			}
		},
		error: function(xhr, status, error) {
			console.error('AJAX Error:', error);
			alert('An error occurred while fetching data. Please try again.');
		}
	});
}













/*$(document).ready(function () {
	fetchStatusReport();

	$('#viewStatusButton').click(function () {
		// Clear filters and fetch all data
		fetchStatusReport('', '', false);
	});

	$('.stat-text-working, .stat-text-not-working, .stat-text-not-connected, .stat-text-partially-working').click(function () {
		// Highlight and filter based on clicked status
		$('.highlighted').removeClass('highlighted');
		var inputName = $(this).data('input-name');
		var status = $(this).text().split(":")[0].trim();
		fetchStatusReport(inputName, status);

		$(this).addClass('highlighted');
	});
});

function fetchStatusReport(inputName = '', status = '', initializeChart = true) {
	
	//alert(inputName+"   success  "+status)
	var branchId = $('#loggedInBranch').val();

	$.ajax({
		type: 'GET',
		url: 'getCountsByStatusOfAllDevices',
		data: { branchCode: branchId },
		success: function (data) {
			//alert("function success")
			displayStatusReport(data, inputName, status, initializeChart);
		},
		error: function (xhr, status, error) {
			console.error("Error fetching status report:", error);
		}
	});
}

function displayStatusReport(data, inputName = '', status = '', initializeChart = true) {
	
	//alert(inputName+"-->>"+status)
	var tableBody = $("#zoneReportTable tbody");
	tableBody.empty();

	var statusMap = {
		'A': 'Not Working',
		'D': 'Not Connected',
		'N': 'Working',
		'P': 'Partially Working'
	};

	var statusCounts = {
		'A': { 'CCTV': 0, 'SECURITY ALARM': 0, 'FIRE ALARM': 0, 'ETL': 0, 'BACS': 0 },
		'N': { 'CCTV': 0, 'SECURITY ALARM': 0, 'FIRE ALARM': 0, 'ETL': 0, 'BACS': 0 },
		'D': { 'CCTV': 0, 'SECURITY ALARM': 0, 'FIRE ALARM': 0, 'ETL': 0, 'BACS': 0 },
		'P': { 'CCTV': 0, 'SECURITY ALARM': 0, 'FIRE ALARM': 0, 'ETL': 0, 'BACS': 0 }
	};

	var groupedData = {};

	data.payload.forEach(function (item) {
		if (item.zoneValues) {
			var branchCode = item.branchCode;
			if (!groupedData[branchCode]) {
				groupedData[branchCode] = {
					branchName: item.branchName,
					bank: item.bank,
					mobile: item.mobile,
					zones: []
				};
			}

			var zoneGroups = {};

			item.zoneValues.forEach(function (zoneValue) {
				var parts = zoneValue.split(":");
				var zoneId = parts[0];
				var inputNameValue = parts[1];
				var description = parts[3];
				var zoneFlag = parts[4];
				var zoneStatusValue = parts[5];
				var statusText = statusMap[zoneStatusValue];

				if (!zoneGroups[inputNameValue]) {
					zoneGroups[inputNameValue] = { primary: null, nonPrimary: [] };
				}

				if (zoneFlag === 'Y') {
					zoneGroups[inputNameValue].primary = zoneStatusValue;
				} else {
					zoneGroups[inputNameValue].nonPrimary.push(zoneStatusValue);
				}

				groupedData[branchCode].zones.push({
					zoneId: zoneId,
					inputName: inputNameValue,
					description: description,
					status: statusText,
					zoneFlag: zoneFlag,
					zoneStatusValue: zoneStatusValue
				});
			});

			for (var inputNameValue in zoneGroups) {
				var group = zoneGroups[inputNameValue];
				var primaryStatus = group.primary;
				var nonPrimaryStatuses = group.nonPrimary;

				if (primaryStatus === 'N' && nonPrimaryStatuses.includes('A')) {
					statusCounts['P'][inputNameValue]++;
					groupedData[branchCode].zones.forEach(function (zone) {
						if (zone.inputName === inputNameValue) {
							zone.status = 'Partially Working';
						}
					});
				} else if (primaryStatus === 'A') {
					statusCounts['A'][inputNameValue]++;
				} else if (primaryStatus === 'N') {
					statusCounts['N'][inputNameValue]++;
				}
			}
		}
	});

	var serialNumber = 1;

	for (var branchCode in groupedData) {
		if (groupedData.hasOwnProperty(branchCode) && groupedData[branchCode].zones.length > 0) {
			var branchData = groupedData[branchCode];

			// Display rows only if no filters or they match the filter
			if ((inputName && !branchData.zones.some(zone => zone.inputName === inputName)) ||
				(status && !branchData.zones.some(zone => zone.status === status))) {
				continue;
			}

			var row = $("<tr>").addClass("branch-row").attr("data-branch-code", branchCode);
			row.append(
				$("<td class='text-right'>").html(
					`<span>${serialNumber++}</span>`
				),
				$("<td>").text(branchData.branchName),
				$("<td>").text(branchData.bank),
				$("<td class='text-right'>").text(branchData.mobile),
				$("<td colspan='5'>").append(
					$("<button>")
						.addClass("btn btn-primary btn-sm")
						.text("View Details")
						.on("click", function () {
							const latestRecord = {}; // Replace this with the actual latest record for the branch
							getHealthCommunicatorContent(latestRecord, branchCode, $(this).closest(".branch-row").next(".health-row"));
						})
				)
			);

			tableBody.append(row);

			(function (currentBranchData, currentBranchCode) {
				row.on("click", function () {
					if ($(this).next(".zone-row").length) {
						$(this).nextUntil(".branch-row").remove();
						$(this).find("i.fa-caret-down").removeClass("fa-caret-down").addClass("fa-caret-right");
					} else {
						$(this).find("i.fa-caret-right").removeClass("fa-caret-right").addClass("fa-caret-down");
						addZoneAndHealthRows(currentBranchData, currentBranchCode, $(this));
					}
				});
			})(branchData, branchCode);
		}
	}

	if (!inputName && !status && initializeChart) {
		initializeDoughnutCharts(statusCounts);
	}


}

*/
/*function addZoneAndHealthRows(branchData, branchCode, branchRow) {
	var tableBody = $("#zoneReportTable tbody");

	// Remove any previously opened rows
	$(".zone-row, .health-row").remove();
	$(".branch-row").find("i.fa-caret-down").removeClass("fa-caret-down").addClass("fa-caret-right");

	// Create a table structure for zones
	var zoneTable = $("<table style='border:1px solid #ddd; width:100%; margin-top:10px;'>").addClass("zone-table");

	// Add <thead> with column headers
	var thead = $("<thead>").css({ "background-color": "#e0e0e0", "padding": "10px" });
	thead.append(
		$("<tr>").append(
			$("<th>").text("Zone Name").css("padding", "8px"),
			$("<th>").text("Device").css("padding", "8px"),
			$("<th>").text("Alert Description").css("padding", "8px"),
			$("<th>").text("Status").css("padding", "8px")
		)
	);
	zoneTable.append(thead);

	// Add <tbody> to hold the zone rows
	var zoneTableBody = $("<tbody>");
	zoneTable.append(zoneTableBody);

	// Add zone rows
	branchData.zones.forEach(function(zone) {
		var statusColor = ""; // Default color
		if (zone.status === "Working") {
			statusColor = "green";
		} else if (zone.status === "Not Working") {
			statusColor = "red";
		} else if (zone.status === "Partially Working") {
			statusColor = "orange";
		}

		var zoneRow = $("<tr>").addClass("zone-row").css("background-color", "#F5F5F5");
		zoneRow.append(
			$("<td>").text(zone.zoneId).css("padding", "8px"),
			$("<td>").text(zone.inputName).css("padding", "8px"),
			$("<td>").text(zone.description).css("padding", "8px"),
			$("<td>").css("padding", "8px").append(
				$("<span>")
					.text(zone.status)
					.css({
						"color": "white",
						"background-color": statusColor,
						"text-align": "center",
						"padding": "2px 6px",
						"border-radius": "4px",
						"display": "inline-block"
					})
			)
		);
		zoneTableBody.append(zoneRow);
	});

	// Insert the zone table row
	var zoneRow = $("<tr>").addClass("zone-row");
	zoneRow.append($("<td colspan='10'>").append(zoneTable));
	branchRow.after(zoneRow);

	// Add branch health communicator row above the zone table
	var healthRow = $("<tr>").addClass("health-row");
	healthRow.append($("<td colspan='10' style='color:red;'>").text("No Data Available"));
	zoneRow.before(healthRow);
	fetchLatestDataNew(branchCode, healthRow);
}*/



function fetchLatestDataNew(branchCode, communicatorRow) {
	$.ajax({
		url: './getLatestRecordWithAlertType',
		type: 'GET',
		data: { branchCode: branchCode },
		dataType: 'json',
		success: function(data) {
			if (data.statusCode === 200) {
				const latestRecord = data.payload.latestRecord;
				getHealthCommunicatorContent(latestRecord, branchCode, communicatorRow);
			} else {
				alert("No data found for the branch code: " + branchCode);
			}
		},
		error: function(xhr, status, error) {
			console.error('Error fetching latest data:', error);
			alert("Failed to fetch latest data for the branch code: " + branchCode);
		}
	});
}


function getHealthCommunicatorContent(latestRecord, branchCode, communicatorRow) {
	$.ajax({
		url: './getBranchByBranchCode',
		type: 'GET',
		contentType: 'application/json',
		data: { branchCode: branchCode },
		success: function(response) {
			if (!response || !response.payload || !response.payload[0]) {
				console.error("Invalid response format or missing data");
				return;
			}

			const fetchedBranchCodeName = response.payload[0].branchDesc;
			const datefinal=latestRecord.dateTimes;
			const dateObj = new Date(latestRecord.dateTime);
			const datePart = dateObj.toISOString().split('T')[0];
			const timePart = dateObj.toISOString().split('T')[1].split('.')[0];
			const syncTimeFormatted = `${datePart} ${timePart}`;

			const mainIcon = latestRecord.main == 0
				? `<br/><i class="fa-solid fa-plug-circle-xmark fa-2xl" style="color: #902c3b;"></i><br/><b style="font-size:12px;">Power Off</b>`
				: `<br/><i class="fa-solid fa-plug-circle-bolt fa-2xl" style="color: #1d6e1c;"></i><br/><b style="font-size:12px;">Power On</b>`;

			const solarIcon = latestRecord.solar == 0
				? `<br/><i class="fa-solid fa-sun fa-2xl" style="color: #808080;"></i><br/><b style="font-size:12px;">Not Connected</b>`
				: `<br/><i class="fa-solid fa-sun fa-2xl" style="color: #FFAB52;"></i><br/><b style="font-size:12px;">Connected</b>`;

			let batteryIcon;
			if (latestRecord.battery == 0) {
				batteryIcon = `<br/><i class="fa-solid fa-battery-empty fa-2xl" style="color: #c25419;"></i><br/><b style="font-size:12px;">Not Installed</b>`;
			} else if (latestRecord.battery == 1) {
				batteryIcon = `<br/><i class="fa-solid fa-battery-three-quarters fa-2xl" style="color: #d9a13f;"></i><br/><b style="font-size:12px;">Charging</b>`;
			} else if (latestRecord.battery == 2) {
				batteryIcon = `<br/><i class="fa-solid fa-battery-quarter fa-2xl" style="color: #FFD43B;"></i><br/><b style="font-size:12px;">Battery Low</b>`;
			} else {
				batteryIcon = `<br/><i class="fa-solid fa-battery-full fa-2xl" style="color: #4a8519;"></i><br/><b style="font-size:12px;">Battery Full</b>`;
			}


			// Signal Icon
			let signalIcon;
			if (latestRecord.signalFlag == 0) {
				signalIcon = `<br/><img src="./images/nonSignal.png" alt="noSignal" style="width:40px;"><br/><b style="font-size:12px;">No Signal</b>`;
			} else if (latestRecord.signalFlag == 1) {
				signalIcon = `<img src="./images/signalUnknown.png" alt="unknownSignal" style="width:70px;"><br/><b style="font-size:12px;">Unknown Signal</b>`;
			} else if (latestRecord.signalFlag == 2) {
				signalIcon = `<img src="./images/veryPoorSignal.png" alt="veryPoorSignal" style="width:40px;"><br/><b style="font-size:12px;">Very Poor Signal</b>`;
			} else if (latestRecord.signalFlag == 3) {
				signalIcon = `<img src="./images/poorSignal.png" alt="PoorSignal" style="width:40px;"><br/><b style="font-size:12px;">Poor Signal</b>`;
			} else if (latestRecord.signalFlag == 4) {
				signalIcon = `<img src="./images/fairSignal.png" alt="fairSignal" style="width:40px;"><br/><b style="font-size:12px;">Fair Signal</b>`;
			} else {
				signalIcon = `<img src="./images/goodSignal.png" alt="goodSignal" style="width:40px;"><br/><b style="font-size:12px;">Good Signal</b>`;
			}


			// Status Icon
			let statusIcon;
			if (latestRecord.armed == 0) {
				statusIcon = `<br/><i class="fa-solid fa-shield-virus fa-2xl" style="color:#418529;"></i><br/><b style="font-size:12px;">Active</b>`;
			} else {
				statusIcon = `<br/><i class="fa-solid fa-shield-virus fa-2xl" style="color: #ba2626 ;"></i><br/><b style="font-size:12px;">Disarmed</b>`;
			}

			const htmlContent = `
                <tr class="detail-row">
                    <td colspan="5">
                        <div class="table-responsive table-bordered dataTableNew">
                            <table id="dataTableNew" class="table mb-0">
                                <thead style="color: #fff; background: #028bd7;">
                                    <tr>
                                        <th>Branch Name</th>
                                        <th>Smart Communicator</th>
                                        <th class="text-right">Sync Time</th>
                                        <th>Battery Voltage</th>
                                         <th>GSM</th>
                                        <th>Main</th>
                                        <th>Solar</th>
                                        <th>Battery</th>
                                        <th>Signal</th>
                                         <th>Status</th>
                                         
                                         
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                      <td class="text-dark">${fetchedBranchCodeName}</td>
                                <td class="text-dark">${latestRecord.panelSerialNo}</td>
                                <td class="text-dark text-right">${datefinal}</td>
                                   <td class="text-dark text-center">${latestRecord.battery_VOLTAGE}</td>
                                <td class="text-dark text-center">${latestRecord.gsm_SIGNAL}</td>
                                <td class="text-dark text-center">${mainIcon}</td>
                                <td class="text-dark text-center">${solarIcon}</td>
                                <td class="text-dark text-center">${batteryIcon}</td>
                                <td class="text-dark text-center">${signalIcon}</td>
                                <td class="text-dark text-center">${statusIcon}</td>
                             
                                
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </td>
                </tr>
            `;

			$(communicatorRow).after(htmlContent);
		},
		error: function(xhr, status, error) {
			console.error("AJAX error:", xhr.responseText);
		}
	});
}


function toSentenceCase(str) {
	return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}


/*--------------------------------------------------JS For Filtering Branch Details By Zone, Branch, Desmired Name--------------------------------------------- */


$(document).ready(function() {

	$('#branchZoneFilter .dropdown-item').on('click', function(event) {
		var selectedZoneDesiredName = $('#zoneDesiredName').val();
		var selectedBranchCode = $(this).attr('data-value');

		filterTable(selectedBranchCode, selectedZoneDesiredName);
	});


	$('#zoneReportFilter .dropdown-item').on('click', function(event) {
		var selectedZoneDesiredName = $('#zoneDesiredName').val();
		var selectedBranchCode = $(this).attr('data-value');

		filterTable(selectedBranchCode, selectedZoneDesiredName);
	});

	$('#zoneDesiredName .dropdown-item').on('click', function(event) {


		var selectedZoneDesiredName = $(this).attr('data-value');


		var selectedBranchCode = $('#zoneReportFilter .dropdown-item').attr('data-value');

		filterTable(selectedBranchCode, selectedZoneDesiredName);
	});


	var initialBranchOptions = $('#zoneReportFilter').html();


	$('#branchZoneFilter').change(function() {
		var selectedZone = $(this).val();
		if (selectedZone !== "") {
			$.ajax({
				url: './getBranchByParentCode',
				type: 'POST',
				data: {
					parentCode: selectedZone
				},
				success: function(response) {
					if (response.statusCode == 200 && response.payload.length > 0) {
						$('#zoneReportFilter').empty();
						$.each(response.payload, function(index, branch) {
							$('#zoneReportFilter').append($('<a>', {
								class: 'dropdown-item',
								href: '#',
								text: branch.branchDesc + '->' + branch.branchCode,
								'data-value': branch.branchCode
							}));
						});
					} else {
						$('#zoneReportFilter').empty().html($('<a>', {
							class: 'dropdown-item',
							href: '#',
							text: 'No Branches Found',
							'data-value': ''
						}));
					}
				},
				error: function(xhr, status, error) {
					console.error('Error:', error);
				}
			});
		} else {
			$('#zoneReportFilter').html(initialBranchOptions);
		}
	});

	function filterTable(selectedBranchCode, selectedZoneDesiredName) {
		var tableRows = $('#zoneReportTable tbody tr');
		var rowsDisplayed = false;

		$('#zoneReportTable tbody').find('.no-data-row').remove();

		tableRows.each(function() {
			var branchCodeColumn = $(this).find('td:eq(1)').text().trim();
			var zoneDesiredNameColumn = $(this).find('td:eq(6)').text().trim();
			//alert(zoneDesiredNameColumn)
			var branchCodeMatch = selectedBranchCode === '' || branchCodeColumn === selectedBranchCode;
			var zoneDesiredNameMatch = selectedZoneDesiredName === '' || zoneDesiredNameColumn === selectedZoneDesiredName;
			if (branchCodeMatch && zoneDesiredNameMatch) {
				$(this).show();
				rowsDisplayed = true;
			} else {
				$(this).hide();
			}
		});

		if (!rowsDisplayed) {
			var noDataRow = $("<tr>", { class: 'no-data-row' }).append($("<td>", {
				colspan: 8,
				text: "No data found for selected input"
			}));
			$("#zoneReportTable tbody").append(noDataRow);
		}
	}
});

/**-------------------------------JS for All Alerts Report (Pagination)--------------*********************/


/**-------------------------------JS for All Alerts Report (Pagination)--------------*********************/
function showLoader() {
    document.getElementById('loaderOverlay').style.display = 'flex';
}

function hideLoader() {
    document.getElementById('loaderOverlay').style.display = 'none';
}

document.addEventListener('DOMContentLoaded', function() {
    const loggedInBranch = $('#loggedInBranch').val();

    fetchBranches(); // Populate branch filter
    fetchAlarms(0, loggedInBranch); // Load alarms for logged-in branch
    fetchZones(loggedInBranch); // Load zones for logged-in branch

    // On branch change: only fetch zones, don't filter alarms
    document.getElementById('branchFilter').addEventListener('change', function() {
        const selectedBranch = this.value || loggedInBranch;
        fetchZones(selectedBranch);
    });

    // Add search button click handler - this will handle ALL filters
    document.getElementById('searchButtonForAllAlerts').addEventListener('click', function() {
        const selectedBranch = document.getElementById('branchFilter').value || loggedInBranch;
        fetchAlarms(0, selectedBranch);
    });
});

// Fetch all child branches and populate branchFilter dropdown
function fetchBranches() {
    const branchCode = $('#loggedInBranch').val();

    if (!branchCode) {
        console.error("Logged-in branch code not found!");
        return;
    }

    $.ajax({
        url: './getAllChildren?branchCode=' + branchCode,
        type: 'GET',
        success: function(data) {
            if (data.payload && data.payload.length > 0) {
                const branchSelect = document.getElementById('branchFilter');
                branchSelect.innerHTML = '<option value="">All Branches</option>';

                data.payload.forEach(branch => {
                    const option = document.createElement('option');
                    option.value = branch.branchCode;
                    option.innerText = branch.branchDesc;
                    branchSelect.appendChild(option);
                });
            } else {
                console.error('No branches found in response');
            }
        },
        error: function() {
            console.error('Failed to fetch branch list');
        }
    });
}

// Fetch alarms for a given branch and apply all filters
function fetchAlarms(page, branchCode = '') {
    branchCode = branchCode || $('#loggedInBranch').val();

    showLoader();

    $.ajax({
        url: `./getAllAlertsReport`,
        type: 'GET',
        data: {
            branchCode: branchCode,
            page: page,
            size: 100
        },
        success: function(data) {
            hideLoader();
            if (data.statusCode === 200) {
                const selectedZone = document.getElementById('zoneFilter').value;
                const selectedAlertType = document.getElementById('alertTypeFilter').value;
                const selectedDeviceType = document.getElementById('deviceTypeFilter').value;
                const fromDate = document.getElementById('fromDate').value;
                const toDate = document.getElementById('toDate').value;

                let filteredData = data.payload;

                if (selectedZone) {
                    filteredData = filteredData.filter(item => item.zoneDesiredName === selectedZone);
                }

                if (selectedAlertType) {
                    filteredData = filteredData.filter(item => item.alertType === selectedAlertType);
                }

                if (selectedDeviceType) {
                    filteredData = filteredData.filter(item => item.deviceType === selectedDeviceType);
                }

                if (fromDate) {
                    const fromTimestamp = new Date(fromDate).getTime();
                    filteredData = filteredData.filter(item => new Date(item.timeStamp).getTime() >= fromTimestamp);
                }

                if (toDate) {
                    const toTimestamp = new Date(toDate).getTime() + 86400000; // Include entire day
                    filteredData = filteredData.filter(item => new Date(item.timeStamp).getTime() <= toTimestamp);
                }
//alert("FIlter Data"+filteredData);
                populateTable(filteredData);
                renderPagination(data.currentPage, data.totalPages);
            } else {
                console.error('Failed to fetch alarms:', data.message);
            }
        },
        error: function(jqXHR, textStatus, errorThrown) {
            hideLoader();
            console.error('Error fetching alarms:', textStatus, errorThrown);
        }
    });
}

function fetchZones(branchCode) {
    if (!branchCode) {
        console.error("Branch code is missing for fetching zones.");
        return;
    }

    $.ajax({
        url: './getAllDeviceZoneNameByBranchCode?branchCode=' + branchCode,
        type: 'GET',
        success: function(data) {
            if (data.statusCode === 200 && data.payload && data.payload.zoneList) {
                const zoneSet = new Set();
                const alertTypeSet = new Set();

                data.payload.zoneList.forEach(zone => {
                    if (zone.zoneDesiredName) {
                        zoneSet.add(zone.zoneDesiredName);
                    }
                    if (zone.alertType) {
                        alertTypeSet.add(zone.alertType);
                    }
                });

                // Populate zone dropdown
                const zoneFilter = document.getElementById('zoneFilter');
                zoneFilter.innerHTML = '<option value="">All Zones</option>';
                zoneSet.forEach(zone => {
                    const option = document.createElement('option');
                    option.value = zone;
                    option.innerText = zone;
                    zoneFilter.appendChild(option);
                });

                // Populate alert type dropdown
                const alertTypeFilter = document.getElementById('alertTypeFilter');
                alertTypeFilter.innerHTML = '<option value="">All Alert Types</option>';
                alertTypeSet.forEach(alertType => {
                    const option = document.createElement('option');
                    option.value = alertType;
                    option.innerText = alertType;
                    alertTypeFilter.appendChild(option);
                });

            } else {
                console.warn("No zone/alert data found for branch", branchCode);
            }
        },
        error: function() {
            console.error("Failed to fetch zone names");
        }
    });
}




function toTitleCase(str) {
	if (!str) return '';
	return str.toLowerCase().split(' ').map(word => 
		word.charAt(0).toUpperCase() + word.slice(1)
	).join(' ');
}

function populateTable(alarms) {

	const tableBody = document.getElementById('AlertTableBody');
	tableBody.innerHTML = '';
	let serialNumber = 1;

	alarms.forEach(alarm => {
		try {
			
			const row = document.createElement('tr');

			const serialNumberCell = document.createElement('td');
			serialNumberCell.innerText = serialNumber++;


			const branchCodeCell = document.createElement('td');
			branchCodeCell.innerText = toTitleCase(alarm.branchCode || 'Unknown');
			branchCodeCell.style.display='none';
			

			const bankCell = document.createElement('td');
			bankCell.innerText = toTitleCase(alarm.bank || 'Unknown');
			bankCell.style.display = 'none';


			const ifscCell = document.createElement('td');
			ifscCell.innerText = alarm.ifsc || 'N/A';
			ifscCell.classList.add('text-right');
			

			const deviceTypeCell = document.createElement('td');
			deviceTypeCell.innerText = toTitleCase(alarm.deviceType|| 'Unknown');
			

			const vendorNameCell = document.createElement('td');
			vendorNameCell.innerText = toTitleCase(alarm.vendorName || 'Unknown');

			const addressCell = document.createElement('td');
			addressCell.innerText = toTitleCase(alarm.address || 'N/A');

			const timeStampCell = document.createElement('td');
			timeStampCell.classList.add('time-stamp', 'text-right', 'col-2');
			const formattedTimeStamp = formatDateTime(alarm.timeStamp || '');
			
			timeStampCell.textContent = formattedTimeStamp;
			timeStampCell.setAttribute('data-time-stamp', alarm.timeStamp);

			const zoneDesiredNameCell = document.createElement('td');
			zoneDesiredNameCell.innerText = toTitleCase(alarm.zoneDesiredName || 'Unknown');

			const alertTypeCell = document.createElement('td');
			alertTypeCell.innerText = toTitleCase(alarm.alertType || 'Unknown');
	

			// Append all cells
			row.appendChild(serialNumberCell);
			row.appendChild(branchCodeCell);
			row.appendChild(bankCell);
			row.appendChild(ifscCell);
			row.appendChild(addressCell);
			row.appendChild(deviceTypeCell);
			row.appendChild(vendorNameCell);
			row.appendChild(zoneDesiredNameCell);
			row.appendChild(alertTypeCell);
			row.appendChild(timeStampCell);

			tableBody.appendChild(row);
		} catch (error) {
			console.error("Error while processing alarm row:", alarm, error);
		}
	});
}




function renderPagination(currentPage, totalPages) {
	const paginationContainer = document.getElementById('paginationContainer');
	paginationContainer.innerHTML = '';
	paginationContainer.style = 'color:black;';

	const prevButton = document.createElement('button');
	prevButton.innerText = '<';
	prevButton.style = 'color:white; background-color:#028bd7;';
	prevButton.disabled = currentPage === 0;
	prevButton.addEventListener('click', function() {
		if (currentPage > 0) {
			fetchAlarms(currentPage - 1);
		}
	});
	paginationContainer.appendChild(prevButton);

	const pageInfo = document.createElement('span');
	pageInfo.style = 'margin-top:6px;';
	pageInfo.innerText = ` Page ${currentPage + 1} of ${totalPages + 1} `;
	paginationContainer.appendChild(pageInfo);

	const nextButton = document.createElement('button');
	nextButton.innerText = '>';
	nextButton.style = 'color:white; background-color:#028bd7;';
	nextButton.disabled = currentPage === totalPages;
	nextButton.addEventListener('click', function() {
		if (currentPage < totalPages) {
			fetchAlarms(currentPage + 1);
		}
	});
	paginationContainer.appendChild(nextButton);
}





/*------------------------------------------------------SENTENCE CASE CODE--------------------------------------------------------------------*/



document.addEventListener("DOMContentLoaded", function() {
	function toSentenceCase(str) {
		return str.toLowerCase().replace(/(^\w{1})|(\s+\w{1})/g, letter => letter.toUpperCase());
	}

	function transformTextNodes(element) {
		// Recursively process text nodes
		element.childNodes.forEach(node => {
			if (node.nodeType === Node.TEXT_NODE) {
				// Check if parent element has the 'no-transform' attribute or class
				if (node.parentNode.getAttribute('data-no-transform') !== null) {
					return; // Skip transformation
				}
				node.textContent = toSentenceCase(node.textContent);
			} else if (node.nodeType === Node.ELEMENT_NODE) {
				transformTextNodes(node); // Recursive call for child elements
			}
		});
	}

	function applySentenceCaseToTable(tableId) {
		const table = document.getElementById(tableId);

		if (table) {

			table.querySelectorAll('thead th').forEach(th => {
				transformTextNodes(th);
			});


			table.querySelectorAll('tbody td').forEach(td => {
				transformTextNodes(td);
			});
		}
	}

	const tableIds = ['notWorkingDeviceTable'];
	tableIds.forEach(applySentenceCaseToTable);
});

