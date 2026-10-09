

/**----------------------------------------------------------JS For LiveStreaming------------------------------------------------------------------**/

/*  document.addEventListener("DOMContentLoaded", function() {
	var iconContainer = document.getElementById("icon-container");
	iconContainer.addEventListener("click", function() {
		// Open a new window or tab with the live stream URL
		alert("HI")
		//window.open("https://yourlivestreamurl.com", "_blank");
	});
});
 */

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
	].join(':'); // Join time components

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


var minimizedModals = [];

function minimizeModal(button) {
	var modal = button.closest('.modal');
	var modalId = modal.getAttribute('id');
	if (modal) {
		$(modal).modal('toggle');
		if (!modal.classList.contains('minimized')) {
			minimizedModals.push(modalId);
		} else {
			var index = minimizedModals.indexOf(modalId);
			if (index !== -1) {
				minimizedModals.splice(index, 1);
			}
		}
		displayMinimizedPreviews();
	} else {
		console.error('Modal not found:', modalId);
	}

	// Remove modalId from minimizedModals when modal is closed
	$(modal).on('hidden.bs.modal', function() {
		var index = minimizedModals.indexOf(modalId);
		if (index !== -1) {
			minimizedModals.splice(index, 1);
			displayMinimizedPreviews();
		}
	});
}

function displayMinimizedPreviews() {
	var previewContainer = document.getElementById('minimizedModalPreviews');
	var uniqueModalIds = new Set(minimizedModals);
	previewContainer.innerHTML = '';

	if (uniqueModalIds.size > 0) {
		var bellIcon = document.createElement('div');
		bellIcon.classList.add('minimized-modal-preview');
		bellIcon.style.cursor = 'pointer';
		bellIcon.innerHTML = '<i class="fa fa-bell fa-lg bell-icon" style="color: #c70039;"></i> <span>+' + uniqueModalIds.size + '</span>';
		previewContainer.appendChild(bellIcon);

		var dropdown = document.createElement('div');
		dropdown.id = 'minimizedModalDropdown';
		dropdown.style.display = 'none';
		dropdown.classList.add('dropdown-menu');
		dropdown.style.position = 'absolute';
		dropdown.style.top = bellIcon.offsetTop + bellIcon.offsetHeight + 'px';
		dropdown.style.left = (bellIcon.offsetLeft - 50) + 'px';

		uniqueModalIds.forEach(function(modalId) {
			var modal = document.getElementById(modalId);
			if (modal) {
				var branchCode = modalId.split('_')[1];
				var preview = document.createElement('div');
				preview.classList.add('dropdown-item');
				preview.style.cursor = 'pointer';
				preview.innerHTML = '<i class="fa fa-bell fa-lg bell-icon" style="color: #c70039;"></i> <span>' + branchCode + '</span>';
				preview.addEventListener('click', function() {
					$(modal).modal('show');
				});
				dropdown.appendChild(preview);
			}
		});

		previewContainer.appendChild(dropdown);


		bellIcon.addEventListener('click', function(event) {
			var dropdown = document.getElementById('minimizedModalDropdown');
			if (dropdown.style.display === 'block') {
				dropdown.style.display = 'none';
			} else {
				dropdown.style.display = 'block';
			}
			event.stopPropagation();
		});
	}
}



/*document.addEventListener("DOMContentLoaded", function() {
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
*/

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
                                        <th class="text-center">Branch Name</th>
                                        <th class="text-center">Health Communicator</th>
                                        <th class="text-center">Sync Time</th>
                                        <th class="text-center">Main</th>
                                        <th class="text-center">Solar</th>
                                        <th class="text-center">Battery</th>
                                        <th class="text-center">Signal</th>
                                        <th class="text-center">Status</th>
                                    </tr>
                                </thead>
                                <tbody id="dataBody" onclick="scrollToInputStatusDetails()" style="color: #028bd7; cursor: pointer; color: black;">
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="card">
                        <!-- Input Status Table -->
                        <div id="statusTable" style="display: none;">
                            <!-- Filter Dropdown -->
                            <div class="">
                                <div class="filter-dropdown ml-3 mb-3 col-md-2 col-sm-2" style="position: absolute; right: 0;">
                                    <select class="form-control" id="inputFilter" onchange="filterInputs(this.value)">
                                        <option value="all">All Devices</option>
                                        <option value="alarm">Alerts Devices</option>
                                        <option value="disable">Disable Devices</option>
                                        <option value="normal">Normal Devices</option>
                                    </select>
                                </div>
                                <div class="table-responsive table-striped table-bordered mb-2" style="max-height: 230px; overflow-y: auto; font-size: 12px">
                                    <table class="table mb-0">
                                        <thead style="color: #fff; position: sticky; top: 0; background: #028bd7; text-align: center;">
                                            <tr>
                                            <th class="text-center">SNo.</th>
                                                <th class="text-center">Device Name</th>
                                                <th  class="text-center">Alert Type</th>
                                                <th  class="text-center">Status</th>
                                            </tr>
                                        </thead>
                                        <tbody id="activeZonesBody" style="color: black; text-align: center; font-size: 14px;">
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
    
    
}


/*-----------------------------------------JS for getting Latest record in Branch Table----------------------------------------------------------*/


var zonesString = "";
function fetchLatestData(branchCode) {

	$.ajax({
		url: 'getLatestRecordWithAlertType',
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
					

					

					zonesString += zoneName + " : " + zoneAlert+ " : " + zoneStatus;

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

function openStatusTableForLatestRecord(zones) {
    const statusTable = document.getElementById('statusTable');
    const branchDetailsCard = document.getElementById('branchDetailsCard');
    const branchTableWrapper = document.getElementById('branchTableWrapper');

    // Show the input status table
    statusTable.style.display = "block";

    // Calculate the additional height required
    const additionalHeight = statusTable.offsetHeight + 150;

    // Adjust the card height
    branchDetailsCard.style.minHeight = (422 + additionalHeight) + "px";
    branchTableWrapper.style.maxHeight = (350 + additionalHeight) + "px";

    var activeZonesData = zones.split("_");
    var activeZonesTableBody = document.getElementById("activeZonesBody");

    activeZonesTableBody.innerHTML = "";

    var serialNumber = 1; // Counter for the serial number, starting from 1
    var rowAdded = false; // Flag to check if any row is added

    for (var i = 0; i < activeZonesData.length; i++) {
        var zoneData = activeZonesData[i].split(":");
        var newRow = document.createElement("tr");
        var isRowDisplayed = false;

        // Iterate over zone data and check for alarm status
        for (var j = 0; j < zoneData.length; j++) {
            var newCell = document.createElement("td");
            newCell.style.textAlign = "center";
            newCell.textContent = zoneData[j].trim();

            const filterValue = document.getElementById("inputFilter").value;
            if (filterValue === 'all' && zoneData[j].trim() === 'A') {
                renderActiveStatus(newCell, zoneData[j].trim());
                isRowDisplayed = true;
            }

            newRow.appendChild(newCell);
        }

        // If the row has an active status and is displayed, add the serial number
        if (isRowDisplayed) {
            var serialNumberCell = document.createElement("td");
            serialNumberCell.style.textAlign = "center";
            serialNumberCell.textContent = serialNumber++; // Increment the serial number after assigning
            newRow.insertBefore(serialNumberCell, newRow.firstChild); // Insert serial number at the start of the row

            activeZonesTableBody.appendChild(newRow); // Append the new row to the table body
            rowAdded = true; // Set flag to true as a row is added
        }
    }

    // If no rows were added, show "No data found"
    if (!rowAdded) {
        var noDataRow = document.createElement("tr");
        var noDataCell = document.createElement("td");
        noDataCell.colSpan = zoneData.length + 1; // Span across all columns
        noDataCell.style.textAlign = "center";
        noDataCell.textContent = "No alert zones found";
        noDataRow.appendChild(noDataCell);
        activeZonesTableBody.appendChild(noDataRow);
    }
}


function closeStatusTable() {
    const statusTable = document.getElementById('statusTable');
    const branchDetailsCard = document.getElementById('branchDetailsCard');
    const branchTableWrapper = document.getElementById('branchTableWrapper');

    // Hide the input status table
    statusTable.style.display = "none";

    // Reset the card height
    branchDetailsCard.style.minHeight = "422px";

    // Reset the table wrapper height
    branchTableWrapper.style.maxHeight = "350px";
}


/*----------------------------------------JS for display latest record according to branch code when broadcasting is false--------------------------------------*/


function displayDataForLatestRecord(latestRecord, branchCode) {
	function toSentenceCase(str) {
		return str.toLowerCase().replace(/(^\w{1})|(\s+\w{1})/g, letter => letter.toUpperCase());
	}

	$.ajax({
		url: 'getBranchByBranchCode',
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

/*function openStatusTable() {
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
					renderActiveStatus(newCell, zoneStatus[k]);
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
}*/

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


/*----------------------------------------------------JS for getting data from websocket------------------------------------------------------------------*/
/*var activeZonesList = [];
var inActiveZonesList = [];
var deviceData = {};

var fetchedBranch = "";
var vendorName = '';
var branchDetails = '';
var braodcastBranchCode = [];
var activeZone = '';

function processMessage(message) {
	alert("Message : " + message);
	var messageContainer = document.getElementById("messageContainer");
	var messageElement = document.createElement("p");
	messageElement.textContent = message;
	messageContainer.appendChild(messageElement);
	var dataSplit = message.split(",");
	var branchCodeFromMessage = dataSplit[0];
	var deviceId = dataSplit[1];
	activeZone = dataSplit[8];
	var branchCodes = [];

	braodcastBranchCode.push(branchCodeFromMessage);

	$.ajax({
		url: 'getBranchByBranchCode',
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
				},
				error: function(xhr, status, error) {
					console.error(xhr.responseText);
				}
			});

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

				displayAlarmedZonesList(alarmedZonesList, deviceId, fetchedBranch, vendorName, branchDetails);

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
				activeZonesList.push(dataSplit[9]);
			}
		},
		error: function(xhr, status, error) {
			console.error(xhr.responseText);
		}
	});

	fetchBranchCodeFromBroadCast(branchCodeFromMessage);
}

function fetchData() {
	$.ajax({
		url: '/fetchDataAndBroadcast',
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
	setInterval(fetchData, 20000);
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
		url: 'makeCall',
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
*/
/*-----------------------------------------------------------------JS for Map and Geolocation------------------------------------------------------------------*/



/*----------------------------------------------------JS for Alert Sound-----------------------------------------------------------------------------*/

function playSoundAlert() {
	var audio = new Audio('./audio/emergency_alert.mp3');
	audio.play();
}

function stopSoundAlert() {
	var audio = document.getElementById("audio-alert");
	if (audio) {
		audio.pause();
		audio.currentTime = 0;
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
				fetch('saveAudit', {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json'
					},
					body: JSON.stringify(auditData)
				})
					.then(response => response.json())
					.then(data => {
						console.log(data);
					})
					.catch(error => {
						console.error('Error:', error);
					});
			});

			stopSoundAlert();
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


function displayAlarmedZonesList(alarmedZonesList, deviceId, fetchedBranch, vendorName, branchDetails) {
	// alert(JSON.stringify(branchDetails))
	var tableBody = document.getElementById('example').getElementsByTagName('tbody')[0];

	tableBody.innerHTML = '';

	var uniqueZones = new Set();


	alarmedZonesList.forEach(function(subArray) {
		subArray.forEach(function(zone) {
			uniqueZones.add(zone);
		});
	});

	var counter = 0;

	uniqueZones.forEach(function(zone) {


		counter++;

		var newRow = document.createElement('tr');

		var serialNumberCell = document.createElement('td');
		serialNumberCell.textContent = counter;
		newRow.appendChild(serialNumberCell);

		var vendorCell = document.createElement('td');
		vendorCell.textContent = branchDetails.vendorName;
		newRow.appendChild(vendorCell);

		var branchCell = document.createElement('td');
		branchCell.textContent = fetchedBranch;
		newRow.appendChild(branchCell);

		var addressCell = document.createElement('td');
		addressCell.textContent = branchDetails.address1 + "," + branchDetails.address2 + "," + branchDetails.address3 + "," + branchDetails.address4;
		newRow.appendChild(addressCell);

		var mobileCell = document.createElement('td');
		mobileCell.textContent = branchDetails.mobile;
		newRow.appendChild(mobileCell);

		var deviceIDCell = document.createElement('td');
		deviceIDCell.textContent = deviceId;
		newRow.appendChild(deviceIDCell);

		var alarmedZoneCell = document.createElement('td');
		alarmedZoneCell.textContent = zone;
		newRow.appendChild(alarmedZoneCell);

		var remarkCell = document.createElement('td');
		var selectDropdown = document.createElement('select');
		selectDropdown.className = 'form-control';

		// Parse remarksList value and use it as options
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

		var actionCell = document.createElement('td');
		var submitButton = document.createElement('button');
		submitButton.type = 'button';
		submitButton.className = 'btn btn-primary';
		submitButton.textContent = 'Submit';
		actionCell.appendChild(submitButton);
		newRow.appendChild(actionCell);


		makeCall(zone, branchDetails.mobile, deviceId, fetchedBranch);

		tableBody.appendChild(newRow);




	});


}



document.addEventListener('click', function(event) {
	if (event.target.classList.contains('btn-primary')) {
		var submitButton = event.target;
		var tableRow = submitButton.closest('tr');
		var vendorName = tableRow.children[1].textContent;
		var branchName = branchDetails.branchCode;
		var address = tableRow.children[3].textContent;
		var mobile = tableRow.children[4].textContent;
		var deviceId = tableRow.children[5].textContent;
		var alarmedZone = tableRow.children[6].textContent;

		// Get the selected option from the dropdown
		var remarkDropdown = tableRow.querySelector('select.form-control');
		var selectedRemark = remarkDropdown.options[remarkDropdown.selectedIndex].textContent;

		var userId = $('#userId').val();

		var auditData = {
			deviceId: deviceId,
			activeZone: alarmedZone,
			remarks: selectedRemark,
			user: userId,
			branchCode: branchName,
			mobile: mobile,
			address: address,
			vendorName: vendorName
		};

		fetch('saveAudit', {
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
		url: 'getStatusCountByDateRange',
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
			url: 'getStatusCountByDateRange',
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
			url: 'getStatusCountByYear',
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
				url: 'getStatusCountByActiveZones',
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
			url: 'getStatusCountByMonth',
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
        .toLowerCase()  // Convert the entire string to lowercase
        .replace(/\b\w/g, function(char) { return char.toUpperCase(); });  // Capitalize the first letter of each word
}

data.payload.forEach(function(audit) {
    var row = $('<tr class="alarmedTable"></tr>');
    row.append('<td class="text-left">' + toSentenceCase(audit.deviceId) + '</td>');
    row.append('<td class="text-left">' + toSentenceCase(audit.alarmedDevice) + '</td>');
    row.append('<td class="text-left">' + toSentenceCase(audit.activeZone) + '</td>');
    row.append('<td class="text-right"">' + toSentenceCase(formatDateTime(audit.dateTime)) + '</td>');

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

    row.append('<td class="text-center">' + remarks + '</td>');
    tableBody.append(row);
});

	}
	
	$.ajax({
			url: 'getAllRecordsByDateRange',
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
			url: 'getAllRecordsByDateRange',
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
			url: 'getAllRecordsByMonth',
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
			url: 'getAllRecordsByYear',
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
			url: 'getAllRecordsByActiveZone',
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
			url: 'getAllRecordsByDevice',
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
$(document).ready(function() {
	fetchStatusReport();

	$('#viewStatusButton').click(function() {
		fetchStatusReport('', '', false);
	});

	$('.stat-text-working, .stat-text-not-working, .stat-text-not-connected, .stat-text-partially-working').click(function() {
		$('.highlighted').removeClass('highlighted');
		var inputName = $(this).data('input-name');
		var status = $(this).text().split(":")[0].trim();
		fetchStatusReport(inputName, status);

		$(this).addClass('highlighted');
	});
});

function fetchStatusReport(inputName = '', status = '', initializeChart = true) {
	var branchId = $('#loggedInBranch').val();

	$.ajax({
		type: 'GET',
		url: 'getCountsByStatusOfAllDevices',
		data: { branchCode: branchId },
		success: function(data) {
			displayStatusReport(data, inputName, status, initializeChart);
		},
		error: function(xhr, status, error) {
			console.error("Error fetching status report:", error);
		}
	});
}

function displayStatusReport(data, inputName = '', status = '', initializeChart = true) {
	var tableBody = $("#zoneReportTable tbody");

	tableBody.empty();
	var statusMap = {
		'A': 'Not Working',
		'D': 'Not Connected',
		'N': 'Working',
		'P': 'Partially Working'
	};

	var statusCounts = {
		'A': {
			'CCTV': 0,
			'SECURITY ALARM': 0,
			'FIRE ALARM': 0,
			'ETL': 0,
			'BACS': 0
		},
		'N': {
			'CCTV': 0,
			'SECURITY ALARM': 0,
			'FIRE ALARM': 0,
			'ETL': 0,
			'BACS': 0
		},
		'D': {
			'CCTV': 0,
			'SECURITY ALARM': 0,
			'FIRE ALARM': 0,
			'ETL': 0,
			'BACS': 0
		},
		'P': {
			'CCTV': 0,
			'SECURITY ALARM': 0,
			'FIRE ALARM': 0,
			'ETL': 0,
			'BACS': 0
		}
	};

	if (data.statusCode == 404) {
		var noDataRow = $("<tr>").append($("<td>", {
			colspan: 7,
			text: "No data found"
		}));
		tableBody.append(noDataRow);
		return;
	}

	var groupedData = {};
	var lastBranchName = null;
	var branchRowNumbers = {};

	data.payload.forEach(function(item) {
		if (item.zoneValues) {
			var branchCode = item.branchCode;
			var branchName = item.branchName;
			var mobile = item.mobile;
			var bank = item.bank;
			var dateTime = item.dateTime;

			var formattedDateTime = new Date(dateTime).toLocaleString('en-US', {
				year: 'numeric',
				month: '2-digit',
				day: '2-digit',
				hour: '2-digit',
				minute: '2-digit',
				second: '2-digit',
				hour12: false
			});

			var zoneGroups = {};

			item.zoneValues.forEach(function(zoneValue) {
				var parts = zoneValue.split(":");
				var zoneId = parts[0];
				var zoneInputName = parts[1];
				var zoneDescription = parts[3];
				var zoneFlag = parts[4];
				var zoneStatusValue = parts[5];
				var statusText = statusMap[zoneStatusValue];

				if (!groupedData[branchCode]) {
					groupedData[branchCode] = {
						branchName: branchName,
						bank: bank,
						mobile: mobile,
						dateTime: formattedDateTime,
						zones: []
					};
				}

				if (!zoneGroups[zoneInputName]) {
					zoneGroups[zoneInputName] = { primary: null, nonPrimary: [] };
				}

				if (zoneFlag === 'Y') {
					zoneGroups[zoneInputName].primary = zoneStatusValue;
				} else {
					zoneGroups[zoneInputName].nonPrimary.push(zoneStatusValue);
				}

				groupedData[branchCode].zones.push({
					zoneId: zoneId,
					inputName: zoneInputName,
					status: statusText,
					description: zoneDescription,
					zoneFlag: zoneFlag,
					zoneStatusValue: zoneStatusValue
				});
			});

			for (var zoneInputName in zoneGroups) {
				var group = zoneGroups[zoneInputName];
				var primaryStatus = group.primary;
				var nonPrimaryStatuses = group.nonPrimary;

				if (primaryStatus === 'N' && nonPrimaryStatuses.includes('A')) {
					statusCounts['P'][zoneInputName]++;
					groupedData[branchCode].zones.forEach(function(zone) {
						if (zone.inputName === zoneInputName) {
							zone.status = 'Partially Working';
						}
					});
				} else if (primaryStatus === 'A') {
					statusCounts['A'][zoneInputName]++;
				} else if (primaryStatus === 'N') {
					statusCounts['N'][zoneInputName]++;
				}
			}
		}
	});

	// Initialize variables to track the last branch name and its color
	var lastBranchName = null;
	var lastBranchColor = '';
var serialNumber = 1;
	for (var branchCode in groupedData) {
		function toSentenceCase(str) {
			if (!str) return '';
			return str
				.toLowerCase()  // Convert the entire string to lowercase
				.replace(/\b\w/g, function(char) { return char.toUpperCase(); });  // Capitalize the first letter of each word
		}

		if (groupedData.hasOwnProperty(branchCode)) {
			var branchData = groupedData[branchCode];
			var rowSpan = branchData.zones.length;

			branchData.zones.forEach(function(zone, index) {
				if ((inputName && inputName !== zone.inputName) || (status && status !== zone.status)) {
					return;
				}

				var row = $("<tr>");
				  var serialNumberCell = $("<td>").text(serialNumber++);
				var branchCodeCell = $("<td style='display:none;'>").text(branchCode);
				var branchNameCell = $("<td style='display:none;'>").text(toSentenceCase(branchData.branchName));
				var bankNameCell = $("<td>").text(toSentenceCase(branchData.bank));
				var deviceNameCell = $("<td>").text(toSentenceCase(branchData.mobile));

				var zoneIdCell = $("<td>").text(toSentenceCase(zone.zoneId));
				var inputNameCell = $("<td>").text(toSentenceCase(zone.inputName));
				var statusCell = $("<td class='status-cell' style='text-align:left'>").append($("<span>").text(toSentenceCase(zone.status)));
				var alertTypeCell = $("<td>").text(toSentenceCase(zone.description)).addClass('alert-type');
				var dateTimeCell = $("<td>").text(toSentenceCase(branchData.dateTime));

				if (zone.status === 'Partially Working') {
					if (zone.zoneStatusValue === 'N') {
						statusCell.find('span').addClass('partiallyWorking');
					} else if (zone.zoneStatusValue === 'A') {
						statusCell.find('span').addClass('partiallyWorking');
					}
				} else {
					switch (zone.status) {
						case 'Not Working':
							statusCell.find('span').addClass('acknowledged');
							break;
						case 'Working':
							statusCell.find('span').addClass('falseAlarm');
							break;
						case 'Not Connected':
							statusCell.find('span').addClass('testing');
							break;
						default:
							statusCell.find('span').addClass('defaultStatus');
					}
				}

				if (lastBranchName !== branchData.branchName) {
					lastBranchColor = (lastBranchColor === '#EEEEEE') ? '#FBF6E2' : '#EEEEEE';
					lastBranchName = branchData.branchName;
				}
serialNumberCell.css('background-color', lastBranchColor);
				branchCodeCell.css('background-color', lastBranchColor);
				branchNameCell.css('background-color', lastBranchColor);
				bankNameCell.css('background-color', lastBranchColor);
				deviceNameCell.css('background-color', lastBranchColor);
				zoneIdCell.css('background-color', lastBranchColor);
				inputNameCell.css('background-color', lastBranchColor);
				statusCell.css('background-color', lastBranchColor);
				alertTypeCell.css('background-color', lastBranchColor);
				dateTimeCell.css('background-color', lastBranchColor);
 row.append(serialNumberCell);
				row.append(branchCodeCell);
				row.append(branchNameCell);
				row.append(bankNameCell);
				row.append(deviceNameCell);
				row.append(zoneIdCell);
				row.append(inputNameCell);
				row.append(statusCell);
				row.append(alertTypeCell);
				row.append(dateTimeCell);
				tableBody.append(row);
			});
		}

	}



	if (status === 'Working') {
		$(".alert-type-header").hide();
		$(".alert-type").hide();
	} else {
		$(".alert-type-header").show();
		$(".alert-type").show();
	}

	if (inputName && status) {
		$("#" + inputName.toLowerCase() + status).text(status + ": " + statusCounts[status][inputName]);
	}

	// Handle visibility of alert types
	if (status === 'Working') {
		$(".alert-type-header").hide();
		$(".alert-type").hide();
	} else {
		$(".alert-type-header").show();
		$(".alert-type").show();
	}



	if (!inputName && !status && initializeChart) {
		initializeMorrisBarChart(statusCounts);
	}
}

let cctvWorking, cctvNotWorking, cctvPartiallyWorking;

let bacsWorking, bacsNotWorking, bacsPartiallyWorking;

let fireAlarmWorking, fireAlarmNotWorking, fireAlarmPartiallyWorking;

let etlWorking, etlNotWorking, etlPartiallyWorking;

let securityAlarmWorking, securityAlarmNotWorking, securityAlarmPartiallyWorking;

function initializeMorrisBarChart(statusCounts) {
	var chartData = [
		{
			y: 'CCTV',
			working: statusCounts['N']['CCTV'],
			notWorking: statusCounts['A']['CCTV'],
			partiallyWorking: statusCounts['P']['CCTV']
		},
		{
			y: 'BACS',
			working: statusCounts['N']['BACS'],
			notWorking: statusCounts['A']['BACS'],
			partiallyWorking: statusCounts['P']['BACS']
		},
		{
			y: 'FIRE ALARM',
			working: statusCounts['N']['FIRE ALARM'],
			notWorking: statusCounts['A']['FIRE ALARM'],
			partiallyWorking: statusCounts['P']['FIRE ALARM']
		},
		{
			y: 'ETL',
			working: statusCounts['N']['ETL'],
			notWorking: statusCounts['A']['ETL'],
			partiallyWorking: statusCounts['P']['ETL']
		},
		{
			y: 'SECURITY ALARM',
			working: statusCounts['N']['SECURITY ALARM'],
			notWorking: statusCounts['A']['SECURITY ALARM'],
			partiallyWorking: statusCounts['P']['SECURITY ALARM']
		}
	];


	// Store values for CCTV
	cctvWorking = chartData[0].working;
	cctvNotWorking = chartData[0].notWorking;
	cctvPartiallyWorking = chartData[0].partiallyWorking;

	// Store values for BACS
	bacsWorking = chartData[1].working;
	bacsNotWorking = chartData[1].notWorking;
	bacsPartiallyWorking = chartData[1].partiallyWorking;

	// Store values for FIRE ALARM
	fireAlarmWorking = chartData[2].working;
	fireAlarmNotWorking = chartData[2].notWorking;
	fireAlarmPartiallyWorking = chartData[2].partiallyWorking;

	// Store values for ETL
	etlWorking = chartData[3].working;
	etlNotWorking = chartData[3].notWorking;
	etlPartiallyWorking = chartData[3].partiallyWorking;

	// Store values for SECURITY ALARM
	securityAlarmWorking = chartData[4].working;
	securityAlarmNotWorking = chartData[4].notWorking;
	securityAlarmPartiallyWorking = chartData[4].partiallyWorking;

	console.log("CCTV - Working:", cctvWorking, "Not Working:", cctvNotWorking, "Partially Working:", cctvPartiallyWorking);
	console.log("BACS - Working:", bacsWorking, "Not Working:", bacsNotWorking, "Partially Working:", bacsPartiallyWorking);
	console.log("FIRE ALARM - Working:", fireAlarmWorking, "Not Working:", fireAlarmNotWorking, "Partially Working:", fireAlarmPartiallyWorking);
	console.log("ETL - Working:", etlWorking, "Not Working:", etlNotWorking, "Partially Working:", etlPartiallyWorking);
	console.log("SECURITY ALARM - Working:", securityAlarmWorking, "Not Working:", securityAlarmNotWorking, "Partially Working:", securityAlarmPartiallyWorking);



	var chart = Morris.Bar({
		element: 'morris-bar-chart',
		data: chartData,
		xkey: 'y',
		ykeys: ['working', 'notWorking', 'partiallyWorking'],
		labels: ['Working', 'Not Working', 'Partially Working'],
		barColors: ['#9CDBA6', '#FF7777', '#F6FB7A'],
		hideHover: 'auto',
		gridLineColor: '#eef0f2',
		resize: true


	});




	$('#morris-bar-chart svg rect').each(function(index, element) {
		var deviceType = chartData[Math.floor(index / 3)].y;
		var status = (index % 3 === 0) ? 'working' : (index % 3 === 1) ? 'notWorking' : 'partiallyWorking';
		$(element).attr('data-device-type', deviceType).attr('data-status', status);
	});

	$('#morris-bar-chart').on('click', 'rect', function() {
		var deviceType = $(this).attr('data-device-type');
		var status = $(this).attr('data-status') === 'working' ? 'Working' : $(this).attr('data-status') === 'notWorking' ? 'Not Working' : 'Partially Working';
		//alert(deviceType + "===>>>" + status);
		fetchStatusReport(deviceType, status, false);
	});


	initializeDoughnutCharts()
}

function initializeDoughnutCharts() {
	const commonColors = ['#9CDBA6', '#FF7777', '#F6FB7A'];

	const pieData1 = {
		datasets: [{
			data: [cctvWorking, cctvNotWorking, cctvPartiallyWorking],
			backgroundColor: commonColors
		}]
	};

	const pieData2 = {
		datasets: [{
			data: [securityAlarmWorking, securityAlarmNotWorking, securityAlarmPartiallyWorking],
			backgroundColor: commonColors
		}]
	};

	const pieData3 = {
		datasets: [{
			data: [fireAlarmWorking, fireAlarmNotWorking, fireAlarmPartiallyWorking],
			backgroundColor: commonColors
		}]
	};

	const pieData4 = {
		datasets: [{
			data: [etlWorking, etlNotWorking, etlPartiallyWorking],
			backgroundColor: commonColors
		}]
	};

	const pieData5 = {
		datasets: [{
			data: [bacsWorking, bacsNotWorking, bacsPartiallyWorking],
			backgroundColor: commonColors
		}]
	};

	// Initialize charts with the custom plugin
	new Chart(document.getElementById('pieChart1'), {
		type: 'doughnut',
		data: pieData1,
		options: {
			plugins: {
				centerTextPlugin: {} // Apply custom plugin
			},
			cutout: '50%' // Adjust the percentage to control the thickness
		},
		plugins: [centerTextPlugin]
	});

	new Chart(document.getElementById('pieChart2'), {
		type: 'doughnut',
		data: pieData2,
		options: {
			plugins: {
				centerTextPlugin: {} // Apply custom plugin
			},
			cutout: '50%'
		},
		plugins: [centerTextPlugin]
	});

	new Chart(document.getElementById('pieChart3'), {
		type: 'doughnut',
		data: pieData3,
		options: {
			plugins: {
				centerTextPlugin: {} // Apply custom plugin
			},
			cutout: '50%'
		},
		plugins: [centerTextPlugin]
	});

	new Chart(document.getElementById('pieChart4'), {
		type: 'doughnut',
		data: pieData4,
		options: {
			plugins: {
				centerTextPlugin: {} // Apply custom plugin
			},
			cutout: '50%'
		},
		plugins: [centerTextPlugin]
	});

	new Chart(document.getElementById('pieChart5'), {
		type: 'doughnut',
		data: pieData5,
		options: {
			plugins: {
				centerTextPlugin: {} // Apply custom plugin
			},
			cutout: '50%'
		},
		plugins: [centerTextPlugin]
	});


}



/*************************************Doughnut charts JS*********************************************/

const commonColors = ['#9CDBA6', '#FF7777', '#F6FB7A'];
// Define a custom plugin for Chart.js
const centerTextPlugin = {
	id: 'centerTextPlugin',
	beforeDraw: function(chart) {
		const ctx = chart.ctx;
		const width = chart.width;
		const height = chart.height;
		const fontSize = (height / 114).toFixed(2);
		const dataset = chart.data.datasets[0].data;
		const sum = dataset.reduce((a, b) => a + b, 0); // Calculate sum of data
		const text = sum.toString(); // Convert sum to string

		ctx.restore();
		ctx.font = fontSize + 'em Arial';
		ctx.textBaseline = 'middle';
		ctx.textAlign = 'center';
		ctx.fillStyle = '#000'; // Text color

		// Calculate position
		const centerX = width / 2;
		const centerY = height / 2;

		// Draw text in the center
		ctx.fillText(text, centerX, centerY);
		ctx.save();
	}
};




/*--------------------------------------------------JS For Filtering Branch Details By Zone, Branch, Dedired Name--------------------------------------------- */


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
				url: 'getBranchByParentCode',
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
			var branchCodeColumn = $(this).find('td:eq(0)').text().trim();
			var zoneDesiredNameColumn = $(this).find('td:eq(4)').text().trim();
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
				colspan: 5,
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
			url: 'getBranchByParentCode',
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
		url: `getAllAlertsReport`,
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
	const tableBody = document.getElementById('AlertTableBody');
	tableBody.innerHTML = '';
 let serialNumber = 1;
	alarms.forEach(alarm => {
		const row = document.createElement('tr');
		
		 const serialNumberCell = document.createElement('td');
		 serialNumberCell.classList.add('text-left');
        serialNumberCell.innerText = serialNumber++;

		const branchCodeCell = document.createElement('td');
		branchCodeCell.classList.add('text-left');
		branchCodeCell.innerText = toTitleCase(alarm.branchCode);

		const bankCell = document.createElement('td');
		bankCell.classList.add('text-center');
		bankCell.innerText = toTitleCase(alarm.bank);
		bankCell.style.display = 'none';

		const ifscCell = document.createElement('td');
		ifscCell.classList.add('text-right');
		ifscCell.innerText = alarm.ifsc;

		const vendorNameCell = document.createElement('td');
		vendorNameCell.classList.add('text-left');
		vendorNameCell.innerText = toTitleCase(alarm.vendorName);

		const addressCell = document.createElement('td');
		addressCell.classList.add('text-left');
		addressCell.innerText = toTitleCase(alarm.address);

		const zoneDesiredNameCell = document.createElement('td');
		zoneDesiredNameCell.classList.add('text-left');
		zoneDesiredNameCell.innerText = toTitleCase(alarm.zoneDesiredName);

		const alertTypeCell = document.createElement('td');
		alertTypeCell.classList.add('text-left');
		alertTypeCell.innerText = toTitleCase(alarm.alertType);

const timeStampCell = document.createElement('td');
		timeStampCell.classList.add('time-stamp', 'text-right');

		const formattedTimeStamp = formatDateTime(alarm.timeStamp);


		timeStampCell.textContent = formattedTimeStamp;


		timeStampCell.setAttribute('data-time-stamp', alarm.timeStamp);


		//row.appendChild(branchCodeCell);
		 row.appendChild(serialNumberCell); 
		row.appendChild(bankCell);
		row.appendChild(ifscCell);
		row.appendChild(vendorNameCell);
		row.appendChild(addressCell);
		row.appendChild(zoneDesiredNameCell);
		row.appendChild(alertTypeCell);
		row.appendChild(timeStampCell);
		tableBody.appendChild(row);
	});

	formatTimestamps();
}


function formatTimestamps() {
	const timestampCells = document.querySelectorAll('.time-stamp');
	timestampCells.forEach(cell => {
		const timeStamp = cell.getAttribute('data-time-stamp');
		if (timeStamp) {
			const date = new Date(timeStamp);
			const formattedDateTime = date.toLocaleString('en-US', {
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

	const tableIds = ['notWorkingDeviceTable', 'branchesDetailsTable', 'auditTable'];
	tableIds.forEach(applySentenceCaseToTable);
});

