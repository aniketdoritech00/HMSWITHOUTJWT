$(document).ready(function() {
	$('#excelFile').on('change', function() {
		var fileName = $(this).val().split('\\').pop();
		$(this).next('.custom-file-label').html(fileName);
	});

	$('#filterItemIssued').on('submit', function(event) {
		event.preventDefault();

		var formData = new FormData(this);
		$('#loader-wrapper').show();
        alert("data getting");
		$.ajax({
			
			url: 'http://192.168.1.50:8082/IMS-DI/importBhavCopy',
			type: 'POST',
			data: formData,
			contentType: false,
			processData: false,
			success: function(response) {
				
				alert("data save");
				
				$('#loader-wrapper').hide();
				Swal.fire({
					title: 'Success!',
					text: 'File uploaded and data imported successfully.',
					icon: 'success',
					confirmButtonText: 'OK'
				}).then((result) => {
					if (result.isConfirmed) {
						location.reload();
					}
				});
			},
			error: function(error) {
				alert("data not getting	");
				$('#loader-wrapper').hide();
				Swal.fire({
					title: 'Error!',
					text: 'Failed to upload file. Please try again.',
					icon: 'error',
					confirmButtonText: 'OK'
				});
			}
		});
	});

	$('#example').DataTable({
		"paging": true,
		"lengthMenu": [10, 25, 50, 100]
	});
});


/*----------------------------------------------Pagination------------------------------------------------------*/


document.addEventListener('DOMContentLoaded', function() {
	fetchAlarms(0);
});

function fetchAlarms(page) {
	$.ajax({
		url: `http://192.168.1.50:8082/IMS-DI/getAllDataMined`,
		type: 'GET',
		data: {

			page: page,
			size: 200
		},
		success: function(data) {
			if (data.statusCode === 200) {
				console.log(JSON.stringify(data.payload))
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

var globalPayload = {};

function triggerApiAndStorePayload(code) {
	const apiUrl = `http://192.168.1.50:8082/IMS-DI/getAllExceptions?code=${encodeURIComponent(code)}`;

	return fetch(apiUrl)
		.then(response => response.json())
		.then(data => {
			if (data.statusCode === 200) {
				globalPayload[code] = data.payload;
			} else {
				console.log("Error:", data.message);
			}
		})
		.catch(error => {
			console.error("API call failed:", error);
		});
}

function createTableCell(text, classes = [], bgColor = null, textColor = null) {
	const cell = document.createElement('td');
	cell.innerText = text;
	cell.classList.add(...classes);
	if (bgColor) cell.style.backgroundColor = bgColor;
	if (textColor) cell.style.color = textColor;
	return cell;
}

async function populateTable(alarms) {
	const tableBody = document.getElementById('saleMining');
	tableBody.innerHTML = '';

	for (const alarm of alarms) {
		const row = document.createElement('tr');

		await Promise.all([
			triggerApiAndStorePayload('ALLOWED_BRANCHES'),
			triggerApiAndStorePayload('ALLOWED_PRODUCT'),
			triggerApiAndStorePayload('ALLOWED_BANKS'),
			triggerApiAndStorePayload('ALLOWED_DVR_MAKER'),
			triggerApiAndStorePayload('ALLOWED_CHANNEL_TYPE'),
			triggerApiAndStorePayload('ALLOWED_CAMERA_QUALITY'),
			triggerApiAndStorePayload('ALLOWED_DISK_SIZE'),
			triggerApiAndStorePayload('ALLOWED_VISIT_COMPLAINT'),
			triggerApiAndStorePayload('ALLOWED_DESIGNATION')
		]);

		row.appendChild(createTableCell(alarm.diBranch, ['fixed-height-cell'],
			!globalPayload['ALLOWED_BRANCHES']?.includes(alarm.diBranch) ? 'red' : null,
			!globalPayload['ALLOWED_BRANCHES']?.includes(alarm.diBranch) ? 'white' : null));

		row.appendChild(createTableCell(alarm.cctv, ['fixed-height-cell'],
			!globalPayload['ALLOWED_PRODUCT']?.includes(alarm.cctv) ? 'red' : null,
			!globalPayload['ALLOWED_PRODUCT']?.includes(alarm.cctv) ? 'white' : null));

		row.appendChild(createTableCell(alarm.srNo, ['fixed-height-cell']));

		row.appendChild(createTableCell(alarm.bank, ['fixed-height-cell'],
			!globalPayload['ALLOWED_BANKS']?.includes(alarm.bank) ? 'red' : null,
			!globalPayload['ALLOWED_BANKS']?.includes(alarm.bank) ? 'white' : null));

		row.appendChild(createTableCell(alarm.aoName, ['fixed-height-cell']));

		row.appendChild(createTableCell(alarm.regionalOffice, ['fixed-height-cell']));

		row.appendChild(createTableCell(alarm.branch, ['fixed-height-cell']));

		row.appendChild(createTableCell(alarm.dvrMaker, ['fixed-height-cell'],
			!globalPayload['ALLOWED_DVR_MAKER']?.includes(alarm.dvrMaker) ? 'red' : null,
			!globalPayload['ALLOWED_DVR_MAKER']?.includes(alarm.dvrMaker) ? 'white' : null));

		row.appendChild(createTableCell(alarm.modelNo, ['fixed-height-cell']));

		row.appendChild(createTableCell(alarm.typeOfChannels, ['fixed-height-cell'],
			!globalPayload['ALLOWED_CHANNEL_TYPE']?.includes(alarm.typeOfChannels) ? 'red' : null,
			!globalPayload['ALLOWED_CHANNEL_TYPE']?.includes(alarm.typeOfChannels) ? 'white' : null));

		row.appendChild(createTableCell(alarm.noOfCameras, ['fixed-height-cell'],
			!Array.from({ length: 32 }, (_, i) => (i + 1).toString()).includes(alarm.noOfCameras) ? 'red' : null,
			!Array.from({ length: 32 }, (_, i) => (i + 1).toString()).includes(alarm.noOfCameras) ? 'white' : null));

		row.appendChild(createTableCell(alarm.cameraQuality, ['fixed-height-cell'],
			!globalPayload['ALLOWED_CAMERA_QUALITY']?.includes(alarm.cameraQuality) ? 'red' : null,
			!globalPayload['ALLOWED_CAMERA_QUALITY']?.includes(alarm.cameraQuality) ? 'white' : null));

		row.appendChild(createTableCell(alarm.hardDisksize, ['fixed-height-cell'],
			!globalPayload['ALLOWED_DISK_SIZE']?.includes(alarm.hardDisksize) ? 'red' : null,
			!globalPayload['ALLOWED_DISK_SIZE']?.includes(alarm.hardDisksize) ? 'white' : null));

		row.appendChild(createTableCell(alarm.hddQty, ['fixed-height-cell']));

		row.appendChild(createTableCell(alarm.recordAbilityFromDate, ['fixed-height-cell']));

		row.appendChild(createTableCell(alarm.recordAbilityToDate, ['fixed-height-cell']));

		row.appendChild(createTableCell(alarm.recordingDays, ['fixed-height-cell']));

		row.appendChild(createTableCell(alarm.visitComplaint, ['fixed-height-cell'],
			!globalPayload['ALLOWED_VISIT_COMPLAINT']?.includes(alarm.visitComplaint) ? 'red' : null,
			!globalPayload['ALLOWED_VISIT_COMPLAINT']?.includes(alarm.visitComplaint) ? 'white' : null));

		row.appendChild(createTableCell(alarm.remarks, ['fixed-height-cell']));

		row.appendChild(createTableCell(alarm.dateOfVisit, ['fixed-height-cell']));

		row.appendChild(createTableCell(alarm.reportNo, ['fixed-height-cell']));

		row.appendChild(createTableCell(alarm.faName, ['fixed-height-cell']));

		row.appendChild(createTableCell(alarm.yearsOfInstatllion, ['fixed-height-cell']));

		row.appendChild(createTableCell(alarm.isLockerPresent, ['fixed-height-cell']));

		row.appendChild(createTableCell(alarm.lockerEntryExit, ['fixed-height-cell']));

		row.appendChild(createTableCell(alarm.bmDesignation, ['fixed-height-cell'],
			!globalPayload['ALLOWED_DESIGNATION']?.includes(alarm.designation) ? 'red' : null,
			!globalPayload['ALLOWED_DESIGNATION']?.includes(alarm.designation) ? 'white' : null));

		row.appendChild(createTableCell(alarm.bmName, ['fixed-height-cell']));

		row.appendChild(createTableCell(alarm.mobileNumber, ['fixed-height-cell']));

		tableBody.appendChild(row);
	}
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