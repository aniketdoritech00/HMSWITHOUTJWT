

/**----------------------------------------------------------JS For LiveStreaming------------------------------------------------------------------**/



function openVideoStream() {
	// alert("hi")
	window.open("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4", "_blank");
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

var branchCodeGlobal="";
var deviceIdGlobal ="";
var fetchedBranchGlobal=""; 
var branchDetailsGlobal ="";
var alarmedZonesListGlobal ="";

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

    $(modal).on('hidden.bs.modal', function () {
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
            preview.addEventListener('click', function () {
                localStorage.setItem('selectedModal', JSON.stringify({ modalId, branchCode }));
                window.location.href = 'target-page.html'; // Replace with your target page URL
            });

            dropdown.appendChild(preview);
        });

        previewContainer.appendChild(dropdown);

        bellIcon.addEventListener('click', function (event) {
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
										<th>Health Communicator</th>
										<th>Sync Time</th>
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
var panelSerialNum='';

function processMessage(message) {
/*	var messageContainer = document.getElementById("messageContainer");
	var messageElement = document.createElement("p");
	messageElement.textContent = message;
	messageContainer.appendChild(messageElement);*/
	var dataSplit = message.split(",");
	var branchCodeFromMessage = dataSplit[0];
	var dateTimeStamp=dataSplit[2];
	var deviceId = dataSplit[1];
	activeZone = dataSplit[8];
		var branchCodes = [];
		zone_desired_name=dataSplit[9];
		var vendorNumber='';
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
					//alert(JSON.stringify(response.payload))
					vendorName = response.payload.vendorName;
					panelSerialNum=response.payload.deviceSerialNo;
				$.ajax({
													url: './getVendorByName',
													type: 'GET',
													contentType: 'application/json',
													data: {
														vendorName: vendorName
													},
													success: function(response) {
														//alert("Enter in ss")
														//alert(JSON.stringify(response.payload));
														vendorNumber = response.payload.mobile;
														//alert(vendorNumber);
														//alert("VENDOR NUMBER :: "+vendorNumber);
																	sendSmsByBranchCode(branchCodeFromMessage, activeZone);
																	sendMailByBranchCode(branchCodeFromMessage, activeZone);

																	var zonesArray = activeZone.split(/[:,_]/);

																	zonesArray = zonesArray.filter(function(element) {
																		return element.trim() !== '';
																	});

																	var evenIndicesArray = zonesArray.filter(function(element, index) {
																		return index % 2 === 0;
																	});

																	var alarmedZonesList = [];

																	alarmedZonesList.push(evenIndicesArray);

																	

																	let matches = [...zone_desired_name.matchAll(/(.+?)_/g)]; 
																	var desiredNamedList = [];

																	let extractedValues = matches.map(match => match[1]); // Extract matched groups
																	desiredNamedList.push(extractedValues);
															//		alert(JSON.stringify(desiredNamedList));
																			
																	document.querySelectorAll('#branchValue').forEach(function(element) {
																		var branchCode = element.getAttribute('value');
																		branchCodes.push(branchCode);
																	});

																	var success = false;
																	branchCodes.forEach(function(branchCode) {
																		if (branchCode === branchCodeFromMessage && activeZone.length > 0) {
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

																		displayAlarmedZonesList(desiredNamedList,vendorNumber,alarmedZonesList, deviceId, fetchedBranch, vendorName, branchDetails);

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
																		activeZonesList.push(dataSplit[8]);
																		activeZonesList.push(dataSplit[10]);
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


function sendEmail(branchCode, branchName, bankName, vendorName,alertType, panelSerialNumber,dateTime) {
	
	 var formattedAlertType = alertType.flat().join(',');
	
	//alert(branchCode+"--->>"+branchName+"--->>"+bankName+"--->>"+vendorName+"--->>"+formattedAlertType+"--->>"+panelSerialNumber+"--->>"+dateTime)
	
	
	
	 var emailRequestDTO = {
        ticketId: "0",
        branchCode: branchCode,
        branchName: branchName,
        bankName: bankName,
        vendorName: vendorName,
        alertType: formattedAlertType,
        panelSerialNumber: panelSerialNumber,
        dateTime: dateTime,
        remarks: "",
        completeSMS: "",
        status: ""
    };

    $.ajax({
        url: './api/tickets/send-email',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(emailRequestDTO),
        success: function (response) {
          //  alert("Email sent successfully!");
        },
        error: function (xhr, status, error) {
            alert("Failed to send email: " + xhr.responseText);
        }
    });
}

function fetchData() {
	$.ajax({
		url: './fetchDataAndBroadcast',
		method: 'GET',
		success: function(response) {
			console.log(response);
			//alert("Enter");
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

	//alert(zone+"-"+mobile+"-"+deviceId+""+fetchedBranch)
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
			console.log('Response:', responseData);
			console.log('Call to branch ' + fetchedBranch + ' (' + fetchedBranch + ') has been initiated.');
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.error('Error:', textStatus, errorThrown);
			alert('Failed to initiate call to branch ' + fetchedBranch + ' (' + fetchedBranch + ').');
		}
	});
}

/*-----------------------------------------------------------------JS for Map and Geolocation------------------------------------------------------------------*/



/*----------------------------------------------------JS for Alert Sound-----------------------------------------------------------------------------*/

function playSoundAlert() {
	var audio = document.getElementById("audio-alert");
	audio.play();
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
			console.log("SMS sent successfully!");
			console.log(response);
		},
		error: function(xhr, status, error) {
			console.error("Error sending SMS:", error);
		}
	});
}


function sendMailByBranchCode(branchCode, activeZone) {
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
			console.log("Mail sent successfully!");
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

function displayAlarmedZonesList(desiredNamedList,vendorNumber, alarmedZonesList, deviceId, fetchedBranch, vendorName, branchDetails) {
    alert(vendorNumber)
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
        submitButton.textContent = 'Informed To Vendor';
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
		var vendorName = tableRow.children[1].textContent;
		var branchName = branchDetails.branchCode;
		var address = tableRow.children[4].textContent;
		var mobile = tableRow.children[3].textContent;
		var deviceId = tableRow.children[5].textContent;
		var alarmedZone = tableRow.children[6].textContent;

		var remarks = tableRow.children[7].textContent;
		       var remarkDropdown = tableRow.querySelector('select.form-control');
		       var selectedRemark = remarkDropdown.options[remarkDropdown.selectedIndex].textContent;
		      
		     
		       
		       var inputRemark = tableRow.children[9]; 
		       var inputRemarkField = inputRemark ? inputRemark.querySelector('input.form-control') : null;
		       var inputRemarkValue = inputRemarkField ? inputRemarkField.value : '';
		      

		var userId = $('#userId').val();

		var auditData = {
			deviceId: deviceId,
			activeZone: alarmedZone,
			remarks: selectedRemark,
			remrks: inputRemarkValue,
			user: userId,
			branchCode: branchName,
			mobile: mobile,
			address: address,
			vendorName: vendorName
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
			document.getElementById("total").textContent = "0";
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

		var total = dataValues.reduce((a, b) => a + b, 0);
		document.getElementById("total").textContent = total;

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



	function updateTable(data) {


		var tableBody = $('#auditTable tbody');
		tableBody.empty();

		if (!data.payload || data.payload.length === 0) {
			var noDataRow = $('<tr class="noDataRow"></tr>');
			noDataRow.append('<td colspan="4" style="text-align:center;">No record found</td>');
			tableBody.append(noDataRow);
			return;
		}

		// Sort the data by dateTime in descending order
		data.payload.sort(function(a, b) {
			return new Date(b.dateTime) - new Date(a.dateTime);
		});

		function toSentenceCase(str) {
			if (!str) return '';
			return str
				.toLowerCase() 
				.replace(/\b\w/g, function(char) { return char.toUpperCase(); });
		}

		data.payload.forEach(function(audit) {
			var row = $('<tr class="alarmedTable"></tr>');
			row.append('<td>' + toSentenceCase(audit.deviceId) + '</td>');
			row.append('<td>' + toSentenceCase(audit.alarmedDevice) + '</td>');
			row.append('<td>' + toSentenceCase(audit.activeZone) + '</td>');
			row.append('<td style="text-align:right;">' + toSentenceCase(formatDateTime(audit.dateTime)) + '</td>');


			var remarks = audit.remarks;
			switch (remarks) {
				case 'TESTING':
					remarks = '<span class="testing">' + toSentenceCase('Testing') + '</span>';
					break;
				case 'FALSE ALARM':
					remarks = '<span class="falseAlarm">' + toSentenceCase('False') + '</span>';
					break;
				case 'ACKNOWLEDGED':
					remarks = '<span class="acknowledged">' + toSentenceCase('Acknowledged') + '</span>';
					break;
				case 'SECURITY VENDOR INFORMED':
					remarks = '<span class="informedVendor">' + toSentenceCase('Vendor informed') + '</span>';
					break;
				default:
					remarks = toSentenceCase(remarks);  // Apply sentence case to default remarks
					break;
			}

			row.append('<td>' + remarks + '</td>');
			tableBody.append(row);
		});

	}

	$.ajax({
		url: './getAllRecordsByDateRange',
		type: 'GET',
		data: {
			startDate: $('#startDate').val(),
			endDate: $('#endDate').val()
		},
		success: function(response) {
			//alert("hi")
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


	$('#endDate').change(function() {
		//var selectedDate = $(this).val();

		$.ajax({
			url: './getAllRecordsByDateRange',
			type: 'GET',
			data: {
				startDate: $('#startDate').val(),
				endDate: $('#endDate').val()
			},
			success: function(response) {
				//alert("hi")
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
	});

	function fetchDataByMonth(month, year) {
		$.ajax({
			url: './getAllRecordsByMonth',
			type: 'GET',
			data: {
				month: month,
				year: year
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

	// Update data when month filter changes
	$('#monthFilter').change(function() {
		var selectedMonthYear = $(this).val().split('-');
		var selectedMonth = parseInt(selectedMonthYear[1]);
		var selectedYear = parseInt(selectedMonthYear[0]);
		fetchDataByMonth(selectedMonth, selectedYear);
	});

	// Function to fetch data by year
	function fetchDataByYear(year) {
		$.ajax({
			url: './getAllRecordsByYear',
			type: 'GET',
			data: {
				year: year
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

	// Update data when year filter changes
	dropdownMenu.addEventListener('click', function(event) {
		if (event.target.classList.contains('dropdown-item')) {
			var selectedYear = event.target.textContent;
			fetchDataByYear(selectedYear);
		}
	});

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
        success: function (response) {
            console.log('API Response:', response);

            if (response.statusCode === 200 && response.payload) {
                apiResponse = response.payload; // Assign the payload to apiResponse
                updateCharts(); // Call the function to update the charts
            } else {
                alert('Error fetching data: ' + (response.message || 'Invalid response'));
            }
        },
        error: function (xhr, status, error) {
            console.error('AJAX Error:', error);
            alert('Error: ' + error);
        }
    });
}


function updateHeading(section, status) {
    const headingElement = document.querySelector('.heading.deviceReports');
    if (headingElement&& section &&status) {
        headingElement.textContent = `Device Reports : ${section} ${status}`;
    }else{
		headingElement.textContent = `Device Reports`;
	}
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
                        label: function (tooltipItem) {
                            return total === 0
                                ? `No Data`
                                : `${tooltipItem.label}: ${tooltipItem.raw}`;
                        }
                    }
                }
            },
            cutout: '70%',
            onClick: function (e) {
                if (total === 0) {
                    alert(`No data available for ${label}`);
                    return;
                }

                const activePoints = chart.getElementsAtEventForMode(e, 'nearest', { intersect: true }, true);
                if (activePoints.length > 0) {
                    const index = activePoints[0].index;
                    const status = ['Working', 'Not Working', 'Partially Working'][index];
                    
                    updateHeading(label, status);
                    fetchZoneData(label, status);
                }
            }
        },
        plugins: [
            {
                id: 'centerText',
                beforeDraw: function (chart) {
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
	
	//alert(status)
    const branchCode = $('#loggedInBranch').val();

    $.ajax({
        url: './countingZonesByStatus', 
        type: 'GET',
        data: {
            branchCode: branchCode,
            zoneType: zoneType,
            status: status
        },
        success: function (response) {
			
            if (response.statusCode === 200 && response.payload) {
				var newResponse = JSON.stringify(response.payload);
				//alert(JSON.stringify(response.payload))
                populateTableForChart(response.payload);
            } else {
                alert('Error fetching zone data: ' + (response.message || 'Invalid response'));
            }
        },
        error: function (xhr, status, error) {
            console.error('AJAX Error:', error);
            alert('Error: ' + error);
        }
    });
}

function populateTableForChart(data) {
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

    data.forEach(function (item, index) {
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
            .on('click', function () {
				fetchLatestDataNew(item.branchCode, row);
                toggleZoneTable(row, item.zoneDTOList);
            });
        buttonCell.append(button);
        row.append(buttonCell);
        
         const manualSyncCell = $('<td></td>');

    // Status Button
    const statusButton = $('<button></button>')
        .addClass('btn btn-sm btn-info mx-1') // Bootstrap class
        .text('Status')
        .on('click', function () {
            statusManualSync(item.deviceSerialNo); // Call a function for status
        });
    manualSyncCell.append(statusButton);

    // Reset Button
    const resetButton = $('<button></button>')
        .addClass('btn btn-sm btn-warning') // Bootstrap class
        .text('Reset')
        .on('click', function () {
            resetManualSync(item.deviceSerialNo); // Call a function for reset
        });
    manualSyncCell.append(resetButton);

    row.append(manualSyncCell);
        

        tableBody.append(row);
    });
}


function resetManualSync(deviceSerialNumber){
	  $.ajax({
	            url: './hitUrlForReset?deviceSerialNumber=' + deviceSerialNumber,
	            type: 'POST',
	            contentType: 'application/json',
	            success: function(response) {
	                Swal.close(); 
	                Swal.fire({
	                    icon: 'success',
	                    title: 'Success',
	                    text: response.message
	                });
	            },
	            error: function(xhr, status, error) {
	                Swal.close(); 
	                alert('Error occurred: ' + error);
	            }
	        });
	
}

function  statusManualSync(deviceSerialNumber) {
	
	 $.ajax({
	            url: './hitUrlForStatus?deviceSerialNumber=' + deviceSerialNumber,
	            type: 'POST',
	            contentType: 'application/json',
	            success: function(response) {
	                Swal.close(); 
	                Swal.fire({
	                    icon: 'success',
	                    title: 'Success',
	                    text: response.message
	                });
	            },
	            error: function(xhr, status, error) {
	                Swal.close();
	                alert('Error occurred: ' + error);
	            }
	        });
}

function populateTableForReport(data) {
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

    data.forEach(function (item, index) {
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
            .on('click', function () {
				fetchLatestDataNew(item.branchCode, row);
                toggleZoneTableForReport(row, item.zoneDTOList);
            });
        buttonCell.append(button);
        row.append(buttonCell);
        
         const manualSyncCell = $('<td></td>');

    // Status Button
    const statusButton = $('<button></button>')
        .addClass('btn btn-sm btn-info mx-1') // Bootstrap class
        .text('Status')
        .on('click', function () {
            statusManualSync(item.deviceSerialNo); // Call a function for status
        });
    manualSyncCell.append(statusButton);

    // Reset Button
    const resetButton = $('<button></button>')
        .addClass('btn btn-sm btn-warning') // Bootstrap class
        .text('Reset')
        .on('click', function () {
            resetManualSync(item.deviceSerialNo); // Call a function for reset
        });
    manualSyncCell.append(resetButton);

    row.append(manualSyncCell);
        

        tableBody.append(row);
    });
}


function toggleZoneTable(parentRow, zones) {
    
    $('.zone-details-row').remove();
     $('.detail-row').remove();
   
    if ($(parentRow).next('.zone-details-row').length  && $(parentRow).next('.detail-row').length) {
       
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
    const zoneHeader = $('<thead class="thead-primary"><tr><th>Zone</th><th>Zone Type</th><th>Alert Type</th><th colspan="2">Status</th></tr></thead>');
    
    // Add close icon to the last column of the header row
    const closeIcon = $('<span></span>')
        .addClass('close-icon')
        .html('&#10005;')
        .css({
            'cursor': 'pointer',
            'font-size': '20px',
            'font-weight':'bold',
            'color': '#FFFFFF',
            'margin-left': '200px'
        })
        .on('click', function () {
            $(zoneRow).remove(); // Close the zone table when cross is clicked
        });

    // Add close icon to the last `<th>` element
    zoneHeader.find('th:last').append(closeIcon);

    zoneTable.append(zoneHeader);

    const zoneBody = $('<tbody class="text-dark" style="font-size:13px;"></tbody>');
    zones.forEach(function (zone) {
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

        zoneRow.append(zoneCell, zoneTypeCell, alertTypeCell, statusCell);
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
    const zoneHeader = $('<thead class="thead-primary"><tr><th>Zone</th><th>Zone Type</th><th>Alert Type</th><th colspan="2">Status</th></tr></thead>');

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
        .on('click', function () {
            $(zoneRow).remove(); // Close the zone table when cross is clicked
        });

    // Add close icon to the last `<th>` element
    zoneHeader.find('th:last').append(closeIcon);

    zoneTable.append(zoneHeader);

    const zoneBody = $('<tbody class="text-dark" style="font-size:13px;"></tbody>');

    zones.forEach(function (zone) {
        const zoneRow = $('<tr></tr>');
        const zoneCell = $('<td class="col-3"></td>').text(zone.zone);
        const zoneTypeCell = $('<td class="col-3"></td>').text(zone.zoneType);
        const alertTypeCell = $('<td class="col-3"></td>').text(zone.alertType);

        // Interpret status for "Partially Working (A)" and "Partially Working (N)"
        let interpretedStatus = zone.status;
        if (zone.status.includes('Partially Working (A)')||zone.status.includes('Partially Working (D)')) {
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

        zoneRow.append(zoneCell, zoneTypeCell, alertTypeCell, statusCell);
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
window.onload = function () {
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
        success: function (response) {
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
        error: function (xhr, status, error) {
            console.error('AJAX Error:', error);
            alert('An error occurred while fetching data. Please try again.');
        }
    });
}




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
            } else if (latestRecord.battery === 1) {
                batteryIcon = `<br/><i class="fa-solid fa-battery-three-quarters fa-2xl" style="color: #d9a13f;"></i><br/><b style="font-size:12px;">Charging</b>`;
            } else if (latestRecord.battery === 2) {
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
                    <td colspan="6">
                        <div class="table-responsive table-bordered dataTableNew">
                            <table id="dataTableNew" class="table mb-0">
                                <thead style="color: #fff; background: #028bd7;">
                                    <tr>
                                        <th>Branch Name</th>
                                        <th>Health Communicator</th>
                                        <th class="text-right">Sync Time</th>
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
                                <td class="text-dark text-right">${syncTimeFormatted}</td>
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

/*----------------------------------------------------------------JS for Filter Branch Details-----------------------------------------------------------*/


document.addEventListener("DOMContentLoaded", function() {
	var zoneDropdown = document.getElementById("branchZoneFilter2");
	var branchTableBody = document.getElementById("branchTableBody");
	var branchDropdown = document.getElementById("zoneReportFilter2");



	zoneDropdown.addEventListener("click", function(event) {
		var selectedZone = event.target.getAttribute("data-value");
		if (selectedZone) {
			getBranchesByParentCode(selectedZone);
		} else {

			resetTable();
		}
	});

	var branchDropdown = document.getElementById("zoneReportFilter2");

	branchDropdown.addEventListener("click", function(event) {
		var selectedBranch = event.target.getAttribute("data-value");
		if (selectedBranch) {
			filterTableByBranch(selectedBranch);
		} else {
			resetTable();
		}
	});

	var statusDropdown = document.getElementById("status");

	statusDropdown.addEventListener("click", function(event) {
		var selectedStatus = event.target.getAttribute("data-value");
		if (selectedStatus) {
			filterTableByStatus(selectedStatus);
		} else {
			resetTable();
		}
	});


	function getBranchesByParentCode(selectedZone) {
		$.ajax({
			url: './getBranchByParentCode',
			type: 'POST',
			data: {
				parentCode: selectedZone
			},
			success: function(response) {
				var branches = response.payload;
				var branchDropdown = document.getElementById("zoneReportFilter2");
				branchDropdown.innerHTML = ""; // Clear existing options

				branches.forEach(function(branch) {
					var option = document.createElement("a");
					option.className = "dropdown-item";
					option.setAttribute("data-value", branch.branchCode);
					option.innerText = branch.branchDesc + " -> " + branch.branchCode;
					branchDropdown.appendChild(option);
				});

				var branchCodes = branches.map(function(branch) {
					return branch.branchCode;
				});
				filterTableByBranch(branchCodes);
			},
			error: function(xhr, status, error) {
				console.error("Error fetching branches: ", error);
			}
		});
	}

	function filterTableByBranch(branchCodes) {
		var tableRows = branchTableBody.getElementsByTagName("tr");
		for (var i = 0; i < tableRows.length; i++) {
			var branchValue = tableRows[i].querySelector("[id='branchValue']").getAttribute("value");
			if (branchCodes.includes(branchValue)) {
				tableRows[i].style.display = "";
			} else {
				tableRows[i].style.display = "none";
			}
		}
	}
	var statusDropdown = document.getElementById("status");
	var branchTableBody = document.getElementById("branchTableBody");

	statusDropdown.addEventListener("click", function(event) {
		var selectedStatus = event.target.getAttribute("data-value");
		if (selectedStatus) {
			filterTableByStatus(selectedStatus);
		}
	});

	function filterTableByStatus(status) {
		resetTable();
		var tableRows = branchTableBody.getElementsByTagName("tr");
		for (var i = 0; i < tableRows.length; i++) {

			var branchStatus = tableRows[i].lastElementChild.innerText;

			if (branchStatus === status) {
				tableRows[i].style.display = "";
			} else {
				tableRows[i].style.display = "none";
			}
		}
	}



	function resetTable() {
		var tableRows = branchTableBody.getElementsByTagName("tr");
		for (var i = 0; i < tableRows.length; i++) {
			tableRows[i].style.display = "";
		}
	}

});


function scrollToTable() {

	var table = document.getElementById('dataTable');
	table.scrollIntoView({ behavior: 'smooth', block: 'start' });
}


function scrollToInputStatusDetails() {
	var table = document.getElementById('activeZonesBody');
	table.scrollIntoView({ behavior: 'smooth', block: 'start' });
}


function toggleCollapseAndScroll() {
	$('#collapseTwo').collapse('show');

	setTimeout(function() {
		$('html, body').animate({
			scrollTop: $('#collapseTwo').offset().top
		}, 800);
	}, 300);
}






function toggleScrolltoNotWorking() {
	$('#collapseOne').collapse('show');


	$('html, body').animate({
		scrollTop: $('#totalDevice').offset().top
	}, 800);
}



/**-------------------------------JS for All Alerts Report (Pagination)--------------*********************/

document.addEventListener('DOMContentLoaded', function() {
	fetchAlarms(0);
});

function fetchAlarms(page) {
	var branchCode = $('#loggedInBranch').val();
	$.ajax({
		url: `./getAllAlertsReport`,
		type: 'GET',
		data: {
			branchCode: branchCode,
			page: page,
			size: 100
		},
		success: function(data) {
			if (data.statusCode === 200) {
				populateTable(data.payload);
				renderPagination(data.currentPage, data.totalPages);
			} else {
				console.error('Failed to fetch alarms:', data.message);
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.error('Error:', textStatus, errorThrown);
		}
	});
}

function toTitleCase(str) {
	return str.toLowerCase().split(' ').map(word => word.charAt(0).toUpperCase() + word.slice(1)).join(' ');
}

function populateTable(alarms) {
	const tableBody = document.getElementById('logsTableBody');
	tableBody.innerHTML = '';
	let serialNumber = 1;
	
	
	alarms.forEach(alarm => {
		const row = document.createElement('tr');

		const serialNumberCell = document.createElement('td');
		serialNumberCell.innerText = serialNumber++;

		const branchCodeCell = document.createElement('td');
		branchCodeCell.innerText = toTitleCase(alarm.branchCode);

		const bankCell = document.createElement('td');
		bankCell.innerText = toTitleCase(alarm.bank);
		bankCell.style.display = 'none';

		const ifscCell = document.createElement('td');
		ifscCell.innerText = alarm.ifsc;
		ifscCell.classList.add('text-right');
		
		const deviceTypeCell = document.createElement('td');
		switch (alarm.deviceType) {
			case 'D':
				deviceTypeCell.innerText = 'Digitals';
				break;
			case 'O':
				deviceTypeCell.innerText = 'Others';
				break;
			case 'NG':
				deviceTypeCell.innerText = 'Guard NG';
				break;
			default:
				deviceTypeCell.innerText = 'Unknown';
		}

		const vendorNameCell = document.createElement('td');
		vendorNameCell.innerText = toTitleCase(alarm.vendorName);

		const addressCell = document.createElement('td');
		addressCell.innerText = toTitleCase(alarm.address);

		const timeStampCell = document.createElement('td');
		timeStampCell.classList.add('time-stamp', 'text-right','col-2');

		const formattedTimeStamp = formatDateTime(alarm.timeStamp);


		timeStampCell.textContent = formattedTimeStamp;


		timeStampCell.setAttribute('data-time-stamp', alarm.timeStamp);


		const zoneDesiredNameCell = document.createElement('td');
		zoneDesiredNameCell.innerText = toTitleCase(alarm.zoneDesiredName);

		const alertTypeCell = document.createElement('td');
		alertTypeCell.innerText = toTitleCase(alarm.alertType);


		row.appendChild(serialNumberCell);
		row.appendChild(bankCell);
		row.appendChild(ifscCell);
		row.appendChild(deviceTypeCell);
		row.appendChild(vendorNameCell);
		row.appendChild(addressCell);

		row.appendChild(timeStampCell);
		row.appendChild(zoneDesiredNameCell);
		row.appendChild(alertTypeCell);
		tableBody.appendChild(row);
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

