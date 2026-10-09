$(document).ready(function () {
    var branchCode = sessionStorage.getItem("branch");

    // Delay API calls by 200-500ms so UI can load smoothly
    setTimeout(function () {
        // 1. Total Devices
        $.ajax({
            url: './getDeviceMaster',
            method: 'GET',
            data: { branchCode: branchCode },
            success: function (response) {
                $('#totalDevices').text(response.payload[0]);
                $('#workingTotalDevices').text(response.payload[0]);
                $('#notWorkingTotalDevices').text(response.payload[0]);
            }
        });
    }, 200);

    setTimeout(function () {
        // 2. Working/Not Working Devices
        $.ajax({
            url: '/getCountOfWorkingDevices',
            method: 'GET',
            data: { branchCode: branchCode },
            success: function (response) {
                $('#workingDevices').text(response.payload.workingDeviceCount);
                $('#notWorkingDevices').text(response.payload.notWorkingCount);
            }
        });
    }, 300);

    setTimeout(function () {
        // 3. SIA Device Count
        $.ajax({
            url: '/getDeviceCountOfSiaByBranchCode',
            method: 'GET',
            data: { branchCode: branchCode },
            success: function (response) {
                $('#siaDevices').text(response.payload.siaCount);
            }
        });
    }, 400);

    setTimeout(function () {
        // 4. Branch Table
        $.ajax({
            url: '/getAllChildren',
            method: 'GET',
            data: { branchCode: branchCode },
            success: function (response) {
                var branches = response.payload;
                var tbody = $('#branchTableBody');
                tbody.empty();

                if (!branches || branches.length === 0) {
                    tbody.append(
                        '<tr><td colspan="7" class="text-center" style="font-size: 15px; color: black;">No data present</td></tr>'
                    );
                    return;
                }

                for (var i = 0; i < branches.length; i++) {
                    var branch = branches[i];
                    var statusColor = branch.status === 'Active' ? '#2c832a' : '#C70039';
                    var row = '<tr style="color: black; cursor: pointer" onclick="showDeviceHealth(\'' + branch.branchCode + '\', this)">' +
                        '<td><i class="fa-solid fa-caret-right fa-lg mx-1"></i>' + (i + 1) + '</td>' +
                        '<td style="display: none;">' + branch.branchCode + '</td>' +
                        '<td style="display: none;">' + branch.bank + '</td>' +
                        '<td>' + branch.branchDesc + '</td>' +
                        '<td>' + branch.ifsc + '</td>' +
                        '<td class="text-right">' + branch.mobile + '</td>' +
                        '<td>' + branch.address1 + '</td>' +
                        '<td><i class="fas fa-shield-alt fa-2x mr-2" style="color: ' + statusColor + ';"></i>' + branch.status + '</td>' +
                        '</tr>';
                    tbody.append(row);
                }
            },
            error: function () {
                $('#branchTableBody').html(
                    '<tr><td colspan="7" class="text-center" style="font-size: 15px; color: black;">Error loading data</td></tr>'
                );
            }
        });
    }, 500);

    setTimeout(function () {
        // 5. All Zones Dropdown
        $.ajax({
            url: '/getAllZones',
            type: 'GET',
            success: function (response) {
                let dropdownHtml = '<a class="dropdown-item" data-value="">All Zones</a>';
                $.each(response.payload, function (index, zone) {
                    dropdownHtml += '<a class="dropdown-item" data-value="' + zone.branchCode + '">' +
                        zone.branchDesc + '->' + zone.branchCode + '</a>';
                });
                $('#branchZoneFilter2').html(dropdownHtml);
            },
            error: function (xhr) {
                console.error("Error fetching zones:", xhr);
            }
        });
    }, 600);

    setTimeout(function () {
        // 6. Remarks
        $.ajax({
            url: '/getRemarksList',
            method: 'GET',
            success: function (response) {
                if (response && response.payload) {
                    $('#remarksList').val(response.payload);
                } else {
                    $('#remarksList').val("No remarks found");
                }
            },
            error: function () {
                $('#remarksList').val("Error loading remarks");
            }
        });
    }, 700);
	
	
	setTimeout(function () {
	$.ajax({
	            url: "/getAllNotWorkingDevices",
	            type: "GET",
	            data: { branchCode: branchCode },
	            success: function (response) {
	                var data = response.payload;
	                var serialNumber = 1;
	                var html = "";

	                for (var i = 0; i < data.length; i++) {
	                    var device = data[i];

	                    if (device.report && device.report.zoneValues && device.report.zoneValues.length > 0) {
	                        var zoneValues = device.report.zoneValues;
	                        var rowspan = zoneValues.length;
	                        var branchDisplayed = false;

	                        for (var j = 0; j < zoneValues.length; j++) {
	                            var zoneSplit = zoneValues[j].split(":");
	                            var zone = zoneSplit[0];
	                            var alertType = zoneSplit.length > 1 ? zoneSplit[1] : "";

	                            html += "<tr>";
	                            html += "<td>" + serialNumber + "</td>";

	                            if (!branchDisplayed) {
	                                html += "<td rowspan='" + rowspan + "'>" + device.branchName + "</td>";
	                                branchDisplayed = true;
	                            }

	                            html += "<td style='display: none;'>" + device.bankName + "</td>";
	                            html += "<td style='text-align: right;'>" + device.mobile + "</td>";
	                            html += "<td>" + zone + "</td>";
	                            html += "<td>" + alertType + "</td>";
	                            html += "<td class='datetime' style='text-align: right;'>" + formatDateTime(device.report.DateTime) + "</td>";
	                            html += "</tr>";

	                            serialNumber++;
	                        }
	                    }
	                }

	                $("#notWorkingDeviceTableBody").html(html);
	            },
	            error: function () {
	                alert("Error fetching Not Working Devices.");
	            }
	        });

	        function formatDateTime(dateTimeStr) {
	            try {
	                var dt = new Date(dateTimeStr);
	                return dt.toLocaleString();
	            } catch (e) {
	                return dateTimeStr;
	            }
	        }
			}, 800);
});
//code writtten by suuyash


document.addEventListener("DOMContentLoaded", function() {
	var startDate = moment().subtract(30, 'days').startOf('day');
	var endDate = moment().endOf('day');
	var startTime = null;
	var endTime = null;
	var chart = null;
	var root = null;

	function updateDateRangeText(start, end) {
		document.getElementById("dateRangeText").innerHTML =
			start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY');
	}

	function updateTimeText(start, end) {
		document.getElementById("startTimeText").innerHTML = start ? start.format('HH:mm') : "--:--";
		document.getElementById("endTimeText").innerHTML = end ? end.format('HH:mm') : "--:--";
	}

	new daterangepicker(document.getElementById("reportrange"), {
		startDate: startDate,
		endDate: endDate,
		showDropdowns: true,
		ranges: {
			'Today': [moment().startOf('day'), moment().endOf('day')],
			'Yesterday': [moment().subtract(1, 'days').startOf('day'), moment().subtract(1, 'days').endOf('day')],
			'Last 7 Days': [moment().subtract(6, 'days').startOf('day'), moment().endOf('day')],
			'Last 30 Days': [moment().subtract(29, 'days').startOf('day'), moment().endOf('day')],
			'This Month': [moment().startOf('month'), moment().endOf('month')],
			'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
		}
	}, function(start, end) {
		if (start === null || end === null) {
			fetchStatusData(null, null);
			updateDateRangeText("All Data", "");
		} else {
			startDate = start;
			endDate = end;
			updateDateRangeText(start, end);
			fetchStatusData(start, end, startTime, endTime);
		}
	});

	new daterangepicker(document.getElementById("startTimePicker"), {
		singleDatePicker: true,
		timePicker: true,
		timePicker24Hour: true,
		timePickerIncrement: 1,
		locale: { format: 'HH:mm' },
		autoApply: true,
		isInvalidDate: function() { return true; }
	}, function(start) {
		startTime = start;
		updateTimeText(startTime, endTime);
		fetchStatusData(startDate, endDate, startTime, endTime);
	});

	new daterangepicker(document.getElementById("endTimePicker"), {
		singleDatePicker: true,
		timePicker: true,
		timePicker24Hour: true,
		timePickerIncrement: 1,
		locale: { format: 'HH:mm' },
		autoApply: true,
		isInvalidDate: function() { return true; }
	}, function(end) {
		endTime = end;
		updateTimeText(startTime, endTime);
		fetchStatusData(startDate, endDate, startTime, endTime);
	});

	function fetchStatusData(start = null, end = null, startTime = null, endTime = null, category = null) {
		let requestData = {};

		if (start && end) {
			requestData.startDate = start.format('YYYY-MM-DD');
			requestData.endDate = end.format('YYYY-MM-DD');
		}

		if (startTime && endTime) {
			requestData.startTime = startTime.format('HH:mm');
			requestData.endTime = endTime.format('HH:mm');
		}

		if (category) {
			requestData.category = category;
		}

		$.ajax({
			url: "./getStatusCountByDateRange",
			method: 'GET',
			data: requestData,
			success: function(data) {
				if (data.statusCode === 200) {
					updateChart(data.payload, startTime, endTime);
					updateTotalAlerts(data.payload);
				} else {
					console.error("Failed to fetch data", data.message);
					alert("Error fetching data");
				}
			},
			error: function(xhr, status, error) {
				console.error("Error fetching data:", error);
			}
		});

		$.ajax({
			url: "./getAllRecordsByDateRange",
			method: 'GET',
			data: requestData,
			success: function(data) {
				if (data.statusCode === 200) {

					updateTable(data.payload, startTime, endTime);
				} else {

					updateTable(data.payload, startTime, endTime);
					Swal.fire({
						icon: 'info',
						title: 'No Alerts',
						text: 'No Alerts Found'
					});
				}
			},
			error: function(xhr, status, error) {
				console.error("Error fetching data:", error);
			}
		});
	}

	function updateChart(statusData, startTime, endTime) {
		if (root) {
			root.dispose();
			root = null;
		}

		root = am5.Root.new("chartdiv");

		root.setThemes([am5themes_Animated.new(root)]);

		chart = root.container.children.push(am5percent.PieChart.new(root, {
			layout: root.verticalLayout,
			innerRadius: am5.percent(50)
		}));

		var series = chart.series.push(am5percent.PieSeries.new(root, {
			valueField: "value",
			categoryField: "category",
			alignLabels: false
		}));

		series.labels.template.setAll({
			visible: false
		});

		const filteredData = statusData.filter(item => {
			let itemTime = moment(item.dateTime, "YYYY-MM-DD HH:mm");
			return (!startTime || itemTime.isSameOrAfter(startTime, 'minute')) &&
				(!endTime || itemTime.isSameOrBefore(endTime, 'minute'));
		});

		const chartData = filteredData.map(item => ({
			value: item.count,
			category: item.remarks
		}));

		series.data.setAll(chartData);

		var legend = chart.children.push(am5.Legend.new(root, {
			centerX: am5.percent(50),
			x: am5.percent(50),
			marginTop: 15,
			marginBottom: 15,
		}));

		legend.data.setAll(series.dataItems);

		series.appear(1000, 100);

		series.dataItems.forEach(function(dataItem) {
			dataItem.get("slice").events.on("click", function() {
				var category = dataItem.get("category");

				var filteredCategory = category;
				if (category === "FALSE ALARM") filteredCategory = "False";
				else if (category === "SECURITY VENDOR INFORMED") filteredCategory = "Vendor Informed";

				filterTableByCategory(filteredCategory);
			});
		});

		document.getElementById("chartdiv").style.display = 'block';
	}

	function filterTableByCategory(category) {
		$("#auditTable tbody tr").each(function() {
			var rowCategory = $(this).find("td:nth-child(5)").text().trim();

			if (rowCategory.toLowerCase() === category.toLowerCase() || category === "All") {
				$(this).show();
			} else {
				$(this).hide();
			}
		});
	}

	function updateTotalAlerts(statusData) {
		let totalAlerts = statusData.reduce((total, item) => total + item.count, 0);
		document.getElementById("total").innerText = totalAlerts;
	}

	function formatDateTime(dateTime) {
		return moment(dateTime).format('MMMM D, YYYY HH:mm');
	}

	function updateTable(statusData, startTime, endTime) {

		//alert("entering updateTable");

		//alert(JSON.stringify(statusData));
		var tableBody = $('#auditTable tbody');
		tableBody.empty();

		if (!statusData || statusData.length === 0) {
			var noDataRow = $('<tr class="alarmedTable"></tr>');
			noDataRow.append('<td colspan="9" style="text-align:center;">No record found</td>');
			tableBody.append(noDataRow);
			return;
		}

		statusData.sort((a, b) => new Date(b.dateTime) - new Date(a.dateTime));

		function toSentenceCase(str) {
			if (!str) return '';
			return str.toLowerCase().replace(/\b\w/g, char => char.toUpperCase());
		}

		statusData.forEach(audit => {
			let auditTime = moment(audit.dateTime, "YYYY-MM-DD HH:mm");

			if ((startTime && auditTime.isBefore(startTime, 'minute')) ||
				(endTime && auditTime.isAfter(endTime, 'minute'))) {
				return;
			}

			var row = $('<tr class="alarmedTable"></tr>');
			row.append('<td>' + toSentenceCase(audit.vendorName) + '</td>');
			row.append('<td>' + toSentenceCase(audit.deviceId) + '</td>');
			row.append('<td>' + toSentenceCase(audit.activeZone) + '</td>');
			row.append('<td style="text-align:right;">' + formatDateTime(audit.dateTime) + '</td>');

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
					remarks = toSentenceCase(remarks);
					break;
			}

			row.append('<td>' + remarks + '</td>');
			tableBody.append(row);
		});
	}

	fetchStatusData(startDate, endDate, startTime, endTime);
});

document.addEventListener("DOMContentLoaded", function() {
	var startDate = moment().subtract(30, 'days').startOf('day');
	var endDate = moment().endOf('day');
	var startTime = null;
	var endTime = null;

	function updateTimeText(start, end) {
		//alert(start+"------->>>>>>>"+end);
		document.getElementById("startTimeText").innerHTML = start ? start.format('HH:mm') : "--:--";
		document.getElementById("endTimeText").innerHTML = end ? end.format('HH:mm') : "--:--";
	}

	new daterangepicker(document.getElementById("startTimePicker"), {
		singleDatePicker: true,
		timePicker: true,
		timePicker24Hour: true,
		timePickerIncrement: 1,
		locale: { format: 'HH:mm' },
		autoApply: true,
	}, function(start) {
		startTime = start;
		updateTimeText(startTime, endTime);
		fetchRecordsByTimeRange(startTime, endTime);
	});

	new daterangepicker(document.getElementById("endTimePicker"), {
		singleDatePicker: true,
		timePicker: true,
		timePicker24Hour: true,
		timePickerIncrement: 1,
		locale: { format: 'HH:mm' },
		autoApply: true,
	}, function(end) {
		endTime = end;
		updateTimeText(startTime, endTime);
		fetchRecordsByTimeRange(startTime, endTime);
		fetchStatusData(startDate, endDate, startTime, endTime);
	});

	function fetchStatusData(start, end, startTime, endTime) {
		let requestData = {
			startDate: start.format('YYYY-MM-DD'),
			endDate: end.format('YYYY-MM-DD'),
			startTime: startTime ? startTime.format('HH:mm') : null,
			endTime: endTime ? endTime.format('HH:mm') : null
		};

		$.ajax({
			url: "./getStatusCountByDateRange",
			method: 'GET',
			data: requestData,
			success: function(data) {
				if (data.statusCode === 200) {
					updateChart(data.payload);
					updateTotalAlerts(data.payload);
				}
			},
			error: function(xhr, status, error) {
				console.error("Error fetching data:", error);
			}
		});

		$.ajax({
			url: "./getAllRecordsByDateRange",
			method: 'GET',
			data: requestData,
			success: function(data) {
				if (data.statusCode === 200) {
					updateTable(data.payload);
				}
			},
			error: function(xhr, status, error) {
				console.error("Error fetching data:", error);
			}
		});
	}



	function updateTableForTimeFilter(statusData, startTime, endTime) {


		var tableBody = $('#auditTable tbody');
		tableBody.empty();

		if (!statusData || statusData.length === 0) {
			var noDataRow = $('<tr class="alarmedTable"></tr>');
			noDataRow.append('<td colspan="9" style="text-align:center;">No record found</td>');
			tableBody.append(noDataRow);
			return;
		}

		statusData.sort((a, b) => new Date(b.dateTime) - new Date(a.dateTime));

		function toSentenceCase(str) {
			if (!str) return '';
			return str.toLowerCase().replace(/\b\w/g, char => char.toUpperCase());
		}

		statusData.forEach(audit => {
			let auditTime = moment(audit.dateTime, "YYYY-MM-DD HH:mm");

			if ((startTime && auditTime.isBefore(startTime, 'minute')) ||
				(endTime && auditTime.isAfter(endTime, 'minute'))) {
				return;
			}

			var row = $('<tr class="alarmedTable"></tr>');
			row.append('<td>' + toSentenceCase(audit.vendorName) + '</td>');
			row.append('<td>' + toSentenceCase(audit.deviceId) + '</td>');
			row.append('<td>' + toSentenceCase(audit.activeZone) + '</td>');
			row.append('<td style="text-align:right;">' + formatDateTime(audit.dateTime) + '</td>');
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
					remarks = toSentenceCase(remarks);
					break;
			}

			row.append('<td>' + remarks + '</td>');
			tableBody.append(row);
		});
	}



	function updateTableForModal(statusData, startTime, endTime) {
		var tableBody = $('#filteredAuditTable tbody');
		tableBody.empty();

		if (!statusData || statusData.length === 0) {
			Swal.fire({
				icon: 'info',
				title: 'No Records Found',
				text: 'No data available for the selected time range.',
				confirmButtonText: 'OK'
			});
			return;
		}

		statusData.sort((a, b) => new Date(b.dateTime) - new Date(a.dateTime));

		function toSentenceCase(str) {
			if (!str) return '';
			return str.toLowerCase().replace(/\b\w/g, char => char.toUpperCase());
		}

		statusData.forEach(audit => {
			let auditTime = moment(audit.dateTime, "YYYY-MM-DD HH:mm");

			if ((startTime && auditTime.isBefore(startTime, 'minute')) ||
				(endTime && auditTime.isAfter(endTime, 'minute'))) {
				return;
			}

			var row = $('<tr class="alarmedTable"></tr>');
			row.append('<td>' + toSentenceCase(audit.vendorName) + '</td>');
			row.append('<td>' + toSentenceCase(audit.deviceId) + '</td>');
			row.append('<td>' + toSentenceCase(audit.activeZone) + '</td>');
			row.append('<td style="text-align:right;">' + formatDateTime(audit.dateTime) + '</td>');

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
					remarks = toSentenceCase(remarks);
					break;
			}

			row.append('<td>' + remarks + '</td>');
			tableBody.append(row);
		});


		$('#filteredTableModal').modal('show');
	}



	function fetchRecordsByTimeRange(startTime, endTime) {
		if (!endTime) return;

		let requestData = {
			startDate: startDate.format('YYYY-MM-DD'),
			endDate: endDate.format('YYYY-MM-DD'),
			startTime: startTime ? startTime.format('HH:mm') : null,
			endTime: endTime ? endTime.format('HH:mm') : null
		};

		$.ajax({
			url: "./getAllRecordsOfTodayByTimeRange",
			method: 'GET',
			data: requestData,
			success: function(data) {

				if (data.statusCode === 200) {
					updateTableForModal(data.payload, startTime, endTime);
					$('#filteredTableModal').modal('show');
				} else if (data.statusCode === 404) {
					Swal.fire({
						icon: 'info',
						title: 'No Alerts',
						text: 'No Alerts Found'
					});
				}
			},
			error: function(xhr, status, error) {
				console.error("Error fetching data:", error);
			}
		});
	}




});




document.addEventListener('DOMContentLoaded',
	function() {
		// Function to truncate text and update dropdown button
		function updateDropdownText(dropdownMenu) {
			dropdownMenu.querySelectorAll('a').forEach(
				function(item) {
					item.addEventListener('click', function() {
						let selectedText = this.textContent;
						let displayText = selectedText.length > 8 ? selectedText.substring(0, 8) + '...' : selectedText;
						this.closest('.dropdown').querySelector('.dropdown-toggle').textContent = displayText;

					});
				});
		}

		document.querySelectorAll('.dropdown-menu')
			.forEach(function(dropdownMenu) {
				updateDropdownText(dropdownMenu);
			});
	});

var markers = [];

$(document).ready(function() {
	fetchBranchData();
});

function fetchBranchData() {
	var branchId = $('#loggedInBranch').val();

	$.ajax({
		url: './getAllChildren?branchCode=' + branchId,
		type: 'GET',
		success: function(response) {
			if (response.message === "Branches fetched successfully") {
				var branches = response.payload;
				var coordinates = [];

				branches.forEach(function(branch) {

					coordinates.push({
						lat: parseFloat(branch.latitude),
						lng: parseFloat(branch.longitude),
						branch: branch.branchCode,
						bank: branch.bank,
						branchDesc: branch.branchDesc,
						address: branch.address1 + ', ' + branch.address2 + ', ' + branch.address3 + ', ' + branch.address4,
						phone: branch.mobile || branch.telephone
					});

				});
				initMap(coordinates);

			} else {
				console.error('Error: ' + response.message);
			}
		},
		error: function(xhr, status, error) {
			console.error('Error: ' + error);
		}
	});
}

function initMap(coordinates) {
	var map = new google.maps.Map(document.getElementById('map'), {
		zoom: 8,
		center: coordinates[0]
	});

	var bounds = new google.maps.LatLngBounds();

	var mainMarker = new google.maps.Marker({
		position: coordinates[0],
		map: map,
		title: 'Head Branch'
	});

	bounds.extend(mainMarker.getPosition());

	var logoUrl = "<c:url value='/logos/image/${logo.id}'/>";

	var infoWindow = new google.maps.InfoWindow({
		content: '<div style="color:black; text-align:center;width:90px; height:45px; padding:0; display:flex; align-items:center; justify-content:center;">' +
			'<img src="' + logoUrl + '" alt="Logo" width="90px" style="display:block;"><br>' +
			'</div>'
	});

	google.maps.event.addListener(infoWindow, 'domready', function() {
		document.querySelector('.gm-style-iw').parentNode.querySelector('button').style.display = 'none';
		document.querySelector('.gm-style-iw-ch').style.paddingTop = '0px';
	});

	mainMarker.addListener('click', function() {
		infoWindow.open(map, mainMarker);
	});

	markers.push(mainMarker);

	addClickListener(mainMarker, coordinates[0].branch);

	function getInfoContent(branchCode) {
		var activeZoneValue = activeZone.substring(activeZone.indexOf('_') + 1, activeZone.indexOf(':'));
		return '<div><strong>Digitals Alert:</strong> ' + branchCode + '<br>Having the issue in "' + activeZoneValue + '"<br>Thank you for visiting!</div>';
	}

	function addMouseOverListener(marker, branchCode) {
		marker.addListener('mouseover', function() {
			if (braodcastBranchCode.includes(marker.branch)) {
				var infoWindow = new google.maps.InfoWindow({
					content: getInfoContent(branchCode)
				});
				infoWindow.open(map, marker);
			}
		});
	}

	infoWindow.open(map, mainMarker);
	addMouseOverListener(mainMarker, coordinates[0].branch);

	for (var i = 1; i < coordinates.length; i++) {
		var childCoordinate = coordinates[i];

		var details = "Bank      : " + childCoordinate.bank + "\n" +
			"Address : " + childCoordinate.address + "\n" +
			"Phone    : " + childCoordinate.phone;
		// alert(JSON.stringify(childCoordinate.address))
		var childMarker = new google.maps.Marker({
			position: childCoordinate,
			map: map,
			title: details,
			branch: childCoordinate.branch,

		});

		markers.push(childMarker);
		addClickListener(childMarker, childCoordinate);
		bounds.extend(childMarker.getPosition());

		var line = new google.maps.Polyline({
			path: [coordinates[0], childCoordinate],
			geodesic: true,
			strokeColor: '#FF0000',
			strokeOpacity: 0,
			icons: [{
				icon: {
					path: google.maps.SymbolPath.CIRCLE,
					fillOpacity: 1,
					scale: 3
				},
				offset: '0%',
				repeat: '20px'
			}],
			map: map
		});
	}

	map.fitBounds(bounds, -50);

	var listener = google.maps.event.addListenerOnce(map, "bounds_changed", function() {
		if (map.getZoom() < 8) {
			map.setZoom(8);
		}
	});



	setInterval(function() {
		toggleMarkers();
	}, 100);
}




function getHealthCommunicatorContentForMap(branchCode, communicatorRow) {
	// Remove existing detail rows
	$('.detail-row').remove();


	$.ajax({
		url: './getLatestRecordWithAlertType',
		type: 'GET',
		data: { branchCode: branchCode }, // or use panelSerialNo if your API requires it
		success: function(latestRecordResponse) {
			if (!latestRecordResponse || !latestRecordResponse.payload) {
				console.error("No latest record found.");
				return;
			}

			const latestRecord = latestRecordResponse.payload.latestRecord;


			// Now get the branch details
			$.ajax({
				url: './getBranchByBranchCode',
				type: 'GET',
				contentType: 'application/json',
				data: { branchCode: branchCode },
				success: function(response) {
					if (!response || !response.payload || !response.payload[0]) {
						console.error("Invalid branch response.");
						return;
					}

					const fetchedBranchCodeName = response.payload[0].branchDesc;
					const dateObj = new Date(latestRecord.dateTime);
					const datePart = dateObj.toISOString().split('T')[0];
					const timePart = dateObj.toISOString().split('T')[1].split('.')[0];
					const syncTimeFormatted = datePart + " " + timePart;

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

					let signalIcon;
					if (latestRecord.signalFlag == 0) {
						signalIcon = `<br/><img src="./images/nonSignal.png" style="width:40px;"><br/><b style="font-size:12px;">No Signal</b>`;
					} else if (latestRecord.signalFlag == 1) {
						signalIcon = `<img src="./images/signalUnknown.png" style="width:70px;"><br/><b style="font-size:12px;">Unknown Signal</b>`;
					} else if (latestRecord.signalFlag == 2) {
						signalIcon = `<img src="./images/veryPoorSignal.png" style="width:40px;"><br/><b style="font-size:12px;">Very Poor Signal</b>`;
					} else if (latestRecord.signalFlag == 3) {
						signalIcon = `<img src="./images/poorSignal.png" style="width:40px;"><br/><b style="font-size:12px;">Poor Signal</b>`;
					} else if (latestRecord.signalFlag == 4) {
						signalIcon = `<img src="./images/fairSignal.png" style="width:40px;"><br/><b style="font-size:12px;">Fair Signal</b>`;
					} else {
						signalIcon = `<img src="./images/goodSignal.png" style="width:40px;"><br/><b style="font-size:12px;">Good Signal</b>`;
					}

					let statusIcon;
					if (latestRecord.armed == 0) {
						statusIcon = `<br/><i class="fa-solid fa-shield-virus fa-2xl" style="color:#418529;"></i><br/><b style="font-size:12px;">Active</b>`;
					} else {
						statusIcon = `<br/><i class="fa-solid fa-shield-virus fa-2xl" style="color: #ba2626;"></i><br/><b style="font-size:12px;">Disarmed</b>`;
					}

					var htmlContent = '<tr class="detail-row">' +
						'<td colspan="5">' +
						'<div class="table-responsive table-bordered dataTableNew">' +
						'<table id="dataTableNew" class="table mb-0">' +
						'<thead style="color: #fff; background: #028bd7;">' +
						'<tr>' +

						'<th class="text-right">Sync Time</th>' +
						'<th>Main</th>' +
						'<th>Solar</th>' +
						'<th>Battery</th>' +
						'<th>Signal</th>' +
						'<th>Status</th>' +
						'</tr>' +
						'</thead>' +
						'<tbody>' +
						'<tr>' +

						'<td class="text-dark text-right">' + syncTimeFormatted + '</td>' +
						'<td class="text-dark text-center">' + mainIcon + '</td>' +
						'<td class="text-dark text-center">' + solarIcon + '</td>' +
						'<td class="text-dark text-center">' + batteryIcon + '</td>' +
						'<td class="text-dark text-center">' + signalIcon + '</td>' +
						'<td class="text-dark text-center">' + statusIcon + '</td>' +
						'</tr>' +
						'</tbody>' +
						'</table>' +
						'</div>' +
						'</td>' +
						'</tr>';

					$(communicatorRow).after(htmlContent);
				},
				error: function(xhr) {
					console.error("Branch AJAX error:", xhr.responseText);
				}
			});
		},
		error: function(xhr) {
			console.error("Latest record AJAX error:", xhr.responseText);
		}
	});
}



function fetchMainBranchData(branchCode) {
	$('#modalContent').html("Loading…");

	$.ajax({
		url: './countingZonesForReportTable?branchCode=' + branchCode,
		type: 'GET',
		success: function(response) {
			if (response.message === "Data fetched successfully") {
				var branches = response.payload;
				var tableContent = '<div style="overflow-x: auto; max-width: 100%;">' +
					'<table class="table table-bordered black-text" id="branchTable">' +
					'<thead class="thead-primary text-white">' +
					'<tr>' +
					'<th class="d-none">Branch Code</th>' +
					'<th class="d-none">Bank</th>' +
					'<th>Branch Name</th>' +
					'<th style="text-align:right">Phone</th>' +
					'<th style="text-align:right">Device Serial No</th>' +
					'</tr>' +
					'</thead>' +
					'<tbody>';

				branches.forEach(function(branch) {
					tableContent += "<tr class='branch-row' data-branch-code='" + branch.branchCode + "'>" +
						"<td class='d-none'>" + branch.branchCode + "</td>" +
						"<td class='d-none'>" + branch.bank + "</td>" +
						"<td>" + branch.branchName + "</td>" +
						"<td style='text-align:right'>" + branch.mobile + "</td>" +
						"<td style='text-align:right'>" + branch.deviceSerialNo + "</td>" +
						"</tr>";
				});

				tableContent += '</tbody></table></div>';
				$('#modalContent').html(tableContent);
			} else {
				$('#modalContent').html('<p class="text-danger black-text">Error: ' + response.message + '</p>');
			}
		},
		error: function(xhr, status, error) {
			$('#modalContent').html('<p class="text-danger black-text">Failed to fetch data.</p>');
			console.error('Error: ' + error);
		}
	});

	$('#branchModal').modal('show');
}

// Click event to fetch zone details when clicking a branch row
$(document).on('click', '.branch-row', function() {
	var branchCode = $(this).data('branch-code');
	var currentRow = $(this);

	$.ajax({
		url: './countingZonesForReportTable?branchCode=' + branchCode,
		type: 'GET',
		success: function(response) {
			if (response.message === "Data fetched successfully") {
				var branchData = response.payload.find(b => b.branchCode === branchCode);


				if (!branchData || !branchData.zoneDTOList || branchData.zoneDTOList.length === 0) {
					return;
				}

				var zoneDetails = formatZoneDetails(branchData.zoneDTOList);
				var detailsRow = "<tr class='details-row'><td colspan='5'>" + zoneDetails + "</td></tr>";

				// Remove existing details row if already present
				$('.details-row').remove();

				// Insert details row after the clicked branch row
				currentRow.after(detailsRow);

				// Call getHealthCommunicatorContent to show the table in the modal
				getHealthCommunicatorContentForMap(branchCode, currentRow);

			} else {
				alert("Error: " + response.message);
			}
		},
		error: function(xhr, status, error) {
			alert("Failed to fetch counts.");
			console.error('Error:', error);
		}
	});
});

// Function to format zone details into a list
function formatZoneDetails(zones) {
	var content = '<div style="overflow-x: auto; max-width: 100%; background-color:#f8f8f8;">' +
		'<table class="table table-bordered black-text">' +
		'<thead style="background-color:#1d1d1d; color:#f8f8f8;">' +
		'<tr>' +
		'<th>Zone</th>' +
		'<th>Zone Type</th>' +
		'<th>Alert Type</th>' +
		'<th>Status</th>' +
		'</tr>' +
		'</thead>' +
		'<tbody>';

	zones.forEach(function(zone) {
		var statusColor = getStatusColor(zone.status);
		content += '<tr>' +
			'<td>' + zone.zone + '</td>' +
			'<td>' + zone.zoneType + '</td>' +
			'<td>' + zone.alertType + '</td>' +
			'<td><span style="background-color: ' + statusColor + '; color: white; padding: 2px 5px; border-radius: 3px;">' + zone.status + '</span></td>' +
			'</tr>';
	});

	content += '</tbody></table></div>';
	return content;
}

// Function to get status color
function getStatusColor(status) {
	if (status.includes("Partially Working")) return "#FCC737";
	if (status.includes("Not Working")) return "crimson";
	if (status.includes("Working")) return "#118B50";
	return "#000"; // Default Black
}


// Click event for markers
function addClickListener(marker, branchData) {
	marker.addListener('click', function() {
		if (marker === markers[0]) { // Main branch marker
			fetchMainBranchData(branchData);
		} else {
			openFullscreen(); // Open map in full screen for other markers
			setTimeout(function() {
				fetchBranchDetails(branchData);
				$('#branchModal').appendTo('#map').modal('show');
			}, 500);
		}
	});
}


// Function to open the map in fullscreen mode
function openFullscreen() {
	var mapElement = document.getElementById('map');
	if (mapElement.requestFullscreen) {
		mapElement.requestFullscreen();
	} else if (mapElement.mozRequestFullScreen) { // Firefox
		mapElement.mozRequestFullScreen();
	} else if (mapElement.webkitRequestFullscreen) { // Chrome, Safari and Opera
		mapElement.webkitRequestFullscreen();
	} else if (mapElement.msRequestFullscreen) { // IE/Edge
		mapElement.msRequestFullscreen();
	}
}



function fetchBranchDetails(branchData) {
	var branchDetails = `
		        <div style="background-color: #D4EBF8; color: #1F509A; border: 1px solid black; padding: 10px; display: block; width: 100%; border-radius: 10px;">
		            <div>
		                <strong>Branch:</strong> ` + branchData.branchDesc + ` &nbsp;&nbsp;
		            </div>
		            <div>
		                <strong>Address:</strong> ` + branchData.address + ` &nbsp;&nbsp;
		                <strong>Phone:</strong> ` + branchData.phone + `
		            </div>
		        </div>
		    `;

	$('#modalContent').html("Loading...");

	$.ajax({
		url: './getCountsByZoneStatus?branchCode=' + branchData.branch,
		type: 'GET',
		success: function(response) {
			if (response.message === "Counts fetched successfully") {
				var data = response.payload;
				var content = branchDetails + '<div style="color: black; font-size: 14px;">';
				var hasValidData = false;

				for (var system in data) {
					var working = data[system]["Working"];
					var notWorking = data[system]["Not Working"];
					var partiallyWorking = data[system]["Partially Working"];
					var total = working + notWorking + partiallyWorking;

					if (total === 0) {
						continue;
					}

					hasValidData = true;
					content += '<div class="mb-2" style="padding: 8px; border-bottom: 1px solid #ccc; font-size:13px;">' +
						'<div style="display: flex; justify-content: space-between; align-items: center;">' +
						'<strong style="font-size: 15px; color: black;">' + system + '</strong>' +
						'<span class="my-2 p-2" style="background-color:#F1EAFF; color:#7743DB; font-size: 15px; border-radius:15px;">Total: ' + total + '</span>' +
						'</div>' +
						'<div style="display: flex; gap: 10px; margin-top: 5px;">' +
						'<span style="background-color: #E0FBE2; color: #169976; padding: 8px; border-radius: 15px;">' +
						'<i class="fas fa-check-circle"></i> Working: <strong class="ml-3">' + working + '</strong>' +
						'</span>' +
						'<span style="background-color: #FFFECE; color: #ab8c02; padding: 8px; border-radius: 15px;">' +
						'<i class="fas fa-exclamation-triangle"></i> Partially Working: <strong class="ml-3">' + partiallyWorking + '</strong>' +
						'</span>' +
						'<span style="background-color: #FF9F9F; color: #A62C2C; padding: 8px; border-radius: 15px;">' +
						'<i class="fas fa-times-circle"></i> Not Working: <strong class="ml-3">' + notWorking + '</strong>' +
						'</span>' +
						'</div>' +
						'</div>';
				}

				if (!hasValidData) {
					content += '<p style="color: black;">No data available.</p>';
				}

				// Fetch and append device health information
				$.ajax({
					url: './getLatestRecordWithAlertType',
					type: 'GET',
					data: { branchCode: branchData.branch },
					success: function(latestRecordResponse) {
						if (latestRecordResponse && latestRecordResponse.payload) {
							const latestRecord = latestRecordResponse.payload.latestRecord;

							const syncTimeFormatted = new Date(latestRecord.dateTime).toLocaleString();
							const mainStatus = latestRecord.main == 0 ? "Power Off" : "Power On";
							const solarStatus = latestRecord.solar == 0 ? "Not Connected" : "Connected";
							const batteryStatus = latestRecord.battery == 0 ? "Not Installed" :
								latestRecord.battery == 1 ? "Charging" :
									latestRecord.battery == 2 ? "Battery Low" : "Battery Full";
							const signalStatus = latestRecord.signalFlag == 0 ? "No Signal" :
								latestRecord.signalFlag == 1 ? "Unknown Signal" :
									latestRecord.signalFlag == 2 ? "Very Poor Signal" :
										latestRecord.signalFlag == 3 ? "Poor Signal" :
											latestRecord.signalFlag == 4 ? "Fair Signal" : "Good Signal";
							const armedStatus = latestRecord.armed == 0 ? "Active" : "Disarmed";




							content +=

								'<div style="margin-top: 20px; padding: 10px; background-color: #FBFBFB; border: 1px solid #ccc; border-radius: 10px;">' +
								'<h5 style="color: #1F509A; margin-bottom: 10px;">Device Health</h5>' +
								'<div style="display: flex; flex-wrap: nowrap; gap: 10px; overflow-x: auto; font-size:13px;">' +
								'<div style="flex: 0 0 auto; background-color:#B1F0F7; color:#1d1d1d; padding:5px; border-radius:10px;"><strong> <i class="fa fa-refresh mx-1" aria-hidden="true"></i>Sync Time:</strong> ' + syncTimeFormatted + '</div>' +
								'<div style="flex: 0 0 auto; background-color:#BFECFF; color:#1d1d1d; padding:5px; border-radius:10px;""><strong><i class="fa fa-plug mx-1" aria-hidden="true"></i>Main:</strong>  ' + mainStatus + '</div>' +
								'<div style="flex: 0 0 auto; background-color:#CDFCF6; color:#1d1d1d; padding:5px; border-radius:10px;""><strong><i class="fa fa-sun-o mx-1" aria-hidden="true"></i>Solar:</strong> ' + solarStatus + '</div>' +
								'<div style="flex: 0 0 auto; background-color:#B9F3E4; color:#1d1d1d; padding:5px; border-radius:10px;""><strong> <i class="fa fa-battery-full mx-1" aria-hidden="true"></i>Battery:</strong> ' + batteryStatus + '</div>' +
								'<div style="flex: 0 0 auto; background-color:#C6E7FF; color:#1d1d1d; padding:5px; border-radius:10px;""><strong> <i class="fa fa-signal mx-1" aria-hidden="true"></i>Signal:</strong> ' + signalStatus + '</div>' +
								'<div style="flex: 0 0 auto; background-color:#D9EDBF; color:#1d1d1d; padding:5px; border-radius:10px;""><strong><i class="fa fa-shield mx-1" aria-hidden="true"></i>Status:</strong> ' + armedStatus + '</div>' +
								'</div>' +
								'</div>';



						}
						$('#modalContent').html(content);
					},
					error: function(xhr) {
						console.error("Device health AJAX error:", xhr.responseText);
						$('#modalContent').html(content);
					}
				});
			} else {
				$('#modalContent').html('<p class="text-danger" style="color:black;">Error: ' + response.message + '</p>');
			}
		},
		error: function(xhr, status, error) {
			$('#modalContent').html('<p class="text-danger" style="color:black;">Failed to fetch data.</p>');
			console.error('Error: ' + error);
		}
	});
}

function toggleMarkers() {
	markers.forEach(function(marker) {
		if (braodcastBranchCode.includes(marker.branch)) {
			marker.setVisible(!marker.getVisible());
		} else {

		}
	});
	if (braodcastBranchCode.includes(markers[0].branch)) {
		marker.setVisible(!marker.getVisible());
	}
}

/*    document.getElementById("branchIdDropdown").addEventListener("change", function () {
		const selectedBranchId = this.value;

		fetch("/updateBranchId", {
			method: "POST",
			headers: {
				"Content-Type": "application/x-www-form-urlencoded"
			},
			body: new URLSearchParams({
				"branchId": selectedBranchId
			})
		})
		.then(response => {
			if (response.ok) {
				return response.json();
			}
			throw new Error("Network response was not ok.");
		})
		.then(data => {
			if (data.statusCode === 200) {
				// Success, reload the page
				location.reload();
			} else {
				alert("Failed to update branch ID: " + data.message);
			}
		})
		.catch(error => {
			console.error("Error updating branch ID:", error);
			alert("An error occurred while updating branch ID.");
		});
	});*/

$(document).ready(function() {
	
	var loggedInbranchCode = sessionStorage.getItem("branch");
	$('#zoneIdDropdown').prop('disabled', true);
	$('#branchIdDropdown').prop('disabled', true);

	$.ajax({
		url: './getAllChildren',
		method: 'GET',
		data:{branchCode:loggedInbranchCode},
		success: function(data) {
			
			
			var selectElement = $('#headIdDropdown');
			selectElement.empty();
			selectElement.append('<option value="">Select Head-Office Branch</option>');

			if (data.payload && data.payload.length > 0) {
    $.each(data.payload, function(index, branch) {
        if (branch.branchType === "HEAD OFFICE") {
            selectElement.append('<option value="' + branch.branchCode + '">' + branch.branchDesc + '</option>');
        }
    });
    selectElement.prop('disabled', false);
}else {
				selectElement.prop('disabled', true);
			}
		},
		error: function(xhr, status, error) {
			console.error("AJAX error:", status, error);
			alert("Error loading head office branches. Please try again.");
		}
	});


	$('#headIdDropdown').change(function() {
		var branchCode = $(this).val();
		if (branchCode) {
			$('#zoneIdDropdown').prop('disabled', false);

			$.ajax({
				url: '/getBranchesByBranchCodes',
				method: 'GET',
				data: { branchCode: branchCode },
				success: function(data) {
					var selectElement = $('#zoneIdDropdown');
					selectElement.empty();
					selectElement.append('<option value="">Select Zone</option>');

					if (data.payload && data.payload.length > 0) {
						$.each(data.payload, function(index, zone) {
							selectElement.append('<option value="' + zone.branchCode + '">' + zone.branchDesc + '</option>');
						});
						selectElement.prop('disabled', false);
					} else {
						selectElement.prop('disabled', true);
					}
				},
				error: function(xhr, status, error) {
					console.error("AJAX error:", status, error);
					alert("Error loading zones. Please try again.");
				}
			});
		} else {
			$('#zoneIdDropdown').empty().append('<option value="">Select Zone</option>');
			$('#zoneIdDropdown').prop('disabled', true);
			$('#branchIdDropdown').empty().append('<option value="">Select Branch</option>');
			$('#branchIdDropdown').prop('disabled', true);
		}
	});

	$('#zoneIdDropdown').change(function() {
		var zoneCode = $(this).val();
		if (zoneCode) {
			$('#branchIdDropdown').prop('disabled', false);

			$.ajax({
				url: '/getBranchesByBranchCodes',
				method: 'GET',
				data: { branchCode: zoneCode },
				success: function(data) {
					var selectElement = $('#branchIdDropdown');
					selectElement.empty();
					selectElement.append('<option value="">Select Branch</option>');

					if (data.payload && data.payload.length > 0) {
						$.each(data.payload, function(index, branch) {
							selectElement.append('<option value="' + branch.branchCode + '">' + branch.branchDesc + '</option>');
						});
						selectElement.prop('disabled', false);
					} else {
						selectElement.prop('disabled', true);
					}
				},
				error: function(xhr, status, error) {
					console.error("AJAX error:", status, error);
					alert("Error loading branches. Please try again.");
				}
			});
		} else {
			$('#branchIdDropdown').empty().append('<option value="">Select Branch</option>');
			$('#branchIdDropdown').prop('disabled', true);
		}
	});

	$('#searchButton').click(function() {
		var headOfficeCode = $('#headIdDropdown').val();
		var zoneCode = $('#zoneIdDropdown').val();
		var branchCode = $('#branchIdDropdown').val();

		var selectedCode = "";

		if (branchCode) {
			selectedCode = branchCode;
		}
		else if (zoneCode) {
			selectedCode = zoneCode;
		}
		else if (headOfficeCode) {
			selectedCode = headOfficeCode;
		}

		if (selectedCode) {
			fetch("./updateBranchId", {
				method: "POST",
				headers: {
					"Content-Type": "application/x-www-form-urlencoded"
				},
				body: new URLSearchParams({
					"branchId": selectedCode
				})
			})
				.then(response => {
					if (response.ok) {
						return response.json();
					}
					throw new Error("Network response was not ok.");
				})
				.then(data => {
					if (data.statusCode === 200) {
						location.reload();
					} else {
						alert("Failed to update branch ID: " + data.message);
					}
				})
				.catch(error => {
					console.error("Error updating branch ID:", error);
					alert("An error occurred while updating branch ID.");
				});
		} else {
			alert("Please select Head-Office, Zone, or Branch before searching.");
		}
	});

	$('#resetButton').click(function() {
		$('#headIdDropdown').val('').prop('disabled', false);
		$('#zoneIdDropdown').val('').prop('disabled', true).empty().append('<option value="">Select Zone</option>');
		$('#branchIdDropdown').val('').prop('disabled', true).empty().append('<option value="">Select Branch</option>');
		var loggedInBranchCode = sessionStorage.getItem("branch");

		fetch("/updateBranchId", {
			method: "POST",
			headers: {
				"Content-Type": "application/x-www-form-urlencoded"
			},
			body: new URLSearchParams({
				"branchId": loggedInBranchCode
			})
		})
			.then(response => {
				if (response.ok) {
					return response.json();
				}
				throw new Error("Network response was not ok.");
			})
			.then(data => {
				if (data.statusCode === 200) {
					console.log("Branch reset successfully.");
				} else {
					alert("Failed to reset branch ID: " + data.message);
				}
			})
			.catch(error => {
				console.error("Error resetting branch ID:", error);
				alert("An error occurred while resetting branch ID.");
			});
	});
});




/*---------------------------------------------------------------HMS CHATBOT JS START------------------------------------------------------------------------------*/

document.addEventListener('DOMContentLoaded', function() {
	const chatToggleBtn = document.getElementById('chatToggleBtn');
	const chatbotWrapper = document.getElementById('chatbotWrapper');
	const minimizeChatBtn = document.getElementById('minimizeChatBtn');
	const chatMessages = document.getElementById('chatMessages');
	const userMessageInput = document.getElementById('userMessageInput');
	const sendMessageBtn = document.getElementById('sendMessageBtn');
	const quickActionBtns = document.querySelectorAll('.quick-actions .btn');

	let typingDiv = null;

	// Mapping button text to API parameters

const buttonMappings = {
    'Closed-Circuit Television (CCTV)': 'CCTV',
    'Security Alarm System (SAS)': 'Security Alarm',
    'Fire Alarm System (FAS)': 'Fire Alarm',
    'Biometric Access Control System (BACS)': 'BACS',
    'Electronic Time Lock (ETL)': 'ETL'
};


	chatToggleBtn.addEventListener('click', function() {
		chatbotWrapper.classList.toggle('active');
	});

	minimizeChatBtn.addEventListener('click', function() {
		chatbotWrapper.classList.remove('active');
	});

	userMessageInput.addEventListener('keypress', function(e) {
		if (e.key === 'Enter') {
			handleUserMessage();
		}
	});

	sendMessageBtn.addEventListener('click', handleUserMessage);


quickActionBtns.forEach(btn => {
    btn.addEventListener('click', async function() {
        const zoneType = this.textContent.trim();
        const parameter = buttonMappings[zoneType] || zoneType;
        var loggedInBranchCode = sessionStorage.getItem("branch");
        //alert(loggedInBranchCode)
        if (zoneType === 'Other') {
            addMessage('Ask your query in the message box.', 'bot');
            return;
        }

        addMessage(`Fetching status for ${parameter}...`, 'bot');
        showTypingIndicator();

        try {
          const apiResponse = await fetch(`getCountsByZoneStatus?branchCode=${loggedInBranchCode}`);
            if (!apiResponse.ok) {
                throw new Error(`API request failed with status ${apiResponse.status}`);
            }

            const data = await apiResponse.json();
            removeTypingIndicator();

           
if (data.statusCode === 200 && data.payload[parameter]) {
    const zoneData = data.payload[parameter];
    const total = zoneData.Working + zoneData['Not Working'] + zoneData['Partially Working'];

    const message = `
        <p><strong>${zoneType} Status:</strong></p>
        <ul>
            <li>Total: ${total}</li>
            <li>Working: ${zoneData.Working}</li>
            <li>Not Working: ${zoneData['Not Working']}</li>
            <li>Partially Working: ${zoneData['Partially Working']}</li>
            <li class="mt-1">
            <div style="display: flex; flex-direction: column; gap: 10px;">
                <p style="color:#7F8CAA">If you want any other information.<br> 
                Click Here : <b id="otherButton" style="cursor:pointer;">Other Info</b></p>
            </li>
            </div>
        </ul>
    `;

    addMessage(message, 'bot');

    // Attach event listener to the "Other" button
    document.getElementById('otherButton').addEventListener('click', function() {
        addMessage('Ask your query in the message box.', 'bot');
    });
}
 else {
                addMessage(`No data available for ${parameter}.`, 'bot');
            }
        } catch (error) {
            removeTypingIndicator();
            addMessage("Failed to fetch data. Please try again later.", 'bot');
            console.error('Error fetching data:', error);
        }
    });
});







	async function handleUserMessage() {
		const message = userMessageInput.value.trim();
		if (!message) return;

		addMessage(message, 'user');
		userMessageInput.value = '';
		showTypingIndicator();

		try {
			const botResponse = await getBotResponse(message);
			removeTypingIndicator();
			addMessage(botResponse, 'bot');
		} catch (error) {
			removeTypingIndicator();
			addMessage("Sorry, I couldn't process your request. Please try again later.", 'bot');
			console.error('Bot error:', error);
		}
	}

	
function addMessage(message, sender) {
    const messageDiv = document.createElement('div');
    messageDiv.className = sender === 'user' ? 'user-message' : 'bot-message';

    const messageContent = document.createElement('div');
    messageContent.className = 'message-content';
    messageContent.innerHTML = message;

    const messageTime = document.createElement('span');
    messageTime.className = 'message-time';
    messageTime.textContent = getCurrentTime();

    messageDiv.appendChild(messageContent);
    messageDiv.appendChild(messageTime);
    chatMessages.appendChild(messageDiv);


    messageDiv.scrollIntoView({ behavior: 'smooth', block: 'end' });
}

	function getCurrentTime() {
		const now = new Date();
		let hours = now.getHours();
		const minutes = now.getMinutes().toString().padStart(2, '0');
		const ampm = hours >= 12 ? 'PM' : 'AM';
		hours = hours % 12 || 12;
		return `${hours}:${minutes} ${ampm}`;
	}

	function showTypingIndicator() {
		if (!typingDiv) {
			typingDiv = document.createElement('div');
			typingDiv.className = 'bot-message typing-indicator';
			typingDiv.innerHTML = '<div class="dots"><span></span><span></span><span></span></div>';
			chatMessages.appendChild(typingDiv);
			chatMessages.scrollTop = chatMessages.scrollHeight;
		}
		 typingDiv.scrollIntoView({ behavior: 'smooth', block: 'end' });

	}

	function removeTypingIndicator() {
		if (typingDiv && chatMessages.contains(typingDiv)) {
			chatMessages.removeChild(typingDiv);
			typingDiv = null;
		}
	}

	function getBotResponse(input) {
    const branchCode = sessionStorage.getItem("branch");

    return $.ajax({
        url: 'http://localhost:9091/chat/ask',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            query: input,
            branchCode: branchCode
        })
    })
    .then(function(responseText) {
        try {
            // If it's JSON
            const data = typeof responseText === "string" ? JSON.parse(responseText) : responseText;

            if (!data || !data.response) {
                throw new Error('Invalid response structure');
            }

            return data.response.replace(/^Result:\s*/, '');
        } catch (error) {
            // If not JSON, just strip the "Result: " prefix
            return responseText.replace(/^Result:\s*/, '');
        }
    })
    .catch(function(error) {
        console.error('Bot API error:', error);
        return "An error occurred while fetching response.";
    });
}


/*async function getFallbackResponse(userInput) {
    const systemKeywords = {
        CCTV: ['cctv', 'camera', 'closed-circuit television', 'kamra', 'सीसीटीवी', 'cc tv', 'cctv system'],
        'Fire Alarm': ['fire alarm', 'fas', 'aag alarm', 'fire alert', 'fire safety', 'alarm system', 'फायर अलार्म'],
        'Security Alarm': ['security alarm', 'sas', 'suraksha alarm', 'safety alarm', 'security alert', 'burglar alarm', 'guard alarm', 'सेक्योरिटी अलार्म'],
        ETL: ['electronic time lock', 'etl', 'locker', 'safe lock', 'vault lock', 'ईटीएल', 'टाइम लॉक', 'time-lock'],
        BACS: ['biometric access', 'bacs', 'fingerprint scanner', 'thumb scanner', 'bio access', 'फिंगर स्कैनर', 'biometric system', 'biometric']
    };

    const statusKeywords = {
        'Working': ['working', 'wrking','chal', 'chalu', 'ok', 'fine', 'running', 'operational', 'work kar raha hai', 'working fine', 'sahi chal raha hai', 'functional', 'active', 'on'],
        'Not Working': ['not working', 'down', 'failed', 'nhi chal', 'chal nhi', 'band hai', 'band h', 'error', 'offline', 'not operational', 'dead', 'broken', 'inactive', 'crashed', 'fail', 'na chal', 'does not work', 'not running'],
        'Partially Working': ['partially working', 'partly working', 'kabhi kabhi chal raha hai', 'intermittent', 'partial', 'lag', 'slow', 'thoda chal raha hai', 'inconsistent', 'unstable', 'halka chal raha hai']
    };

    const lowerInput = userInput.toLowerCase();

    // Match system normally
    const matchedSystem = matchKeyword(lowerInput, systemKeywords);

    // Match status with priority: Not Working > Partially Working > Working
    const matchedStatus = matchStatusWithPriority(lowerInput, statusKeywords);

    if (!matchedSystem) {
        return "Sorry, I couldn't identify the system you're asking about (e.g., CCTV, Fire Alarm). Please clarify.";
    }

    const branchCode = sessionStorage.getItem("branch");

    try {
        const response = await fetch(`getCountsByZoneStatus?branchCode=${branchCode}`);
        if (!response.ok) throw new Error(`API error ${response.status}`);
        const data = await response.json();

        if (data.statusCode !== 200 || !data.payload[matchedSystem]) {
            return `No data found for ${matchedSystem}.`;
        }

        const zoneData = data.payload[matchedSystem];

        if (matchedStatus) {
            const count = zoneData[matchedStatus] ?? 0;
            return `There are <b>${count}</b> <b>${matchedStatus}</b> <b>${matchedSystem}</b>.`;
        } else {
            const total =
                (zoneData['Working'] || 0) +
                (zoneData['Not Working'] || 0) +
                (zoneData['Partially Working'] || 0);

            return `There are a total of <b>${total}</b> <b>${matchedSystem}</b>.`;
        }
    } catch (err) {
        console.error('Fallback API error:', err);
        return "Something went wrong while fetching status. Please try again later.";
    }

    // Helper for system keyword matching
    function matchKeyword(input, keywordsMap) {
        for (const [key, terms] of Object.entries(keywordsMap)) {
            for (const term of terms) {
                const regex = new RegExp(`\\b${term.replace(/[-[\]/{}()*+?.\\^$|]/g, '\\$&')}\\b`, 'i');
                if (regex.test(input)) return key;
            }
        }
        return null;
    }

    // Helper for prioritized status matching
    function matchStatusWithPriority(input, keywordsMap) {
        const priorityOrder = ['Not Working', 'Partially Working', 'Working'];
        for (const key of priorityOrder) {
            for (const term of keywordsMap[key]) {
                const regex = new RegExp(`\\b${term.replace(/[-[\]/{}()*+?.\\^$|]/g, '\\$&')}\\b`, 'i');
                if (regex.test(input)) return key;
            }
        }
        return null;
    }
}*/


});



/*---------------------------------------------------------------HMS CHATBOT JS END------------------------------------------------------------------------------*/



/*---------------------------------------------------------------AI POP UP JS START--------------------------------------------------------------------*/

window.addEventListener('load', function() {

    const shouldShowPopup = sessionStorage.getItem("showChatbotPopup");
    
    if (shouldShowPopup === "true") {
 
        sessionStorage.removeItem("showChatbotPopup");
        
        const popup = document.getElementById('aiPopupOverlay');
        const closeBtn = document.getElementById('closePopup');

        if (popup && closeBtn) {
            setTimeout(() => {
                popup.classList.add('show');
            }, 500);

            const autoHideTimer = setTimeout(() => {
                hidePopup();
            }, 7000);

            // Close button functionality
            closeBtn.addEventListener('click', () => {
                clearTimeout(autoHideTimer);
                hidePopup();
            });

            // Close on overlay click
            popup.addEventListener('click', (e) => {
                if (e.target === popup) {
                    clearTimeout(autoHideTimer);
                    hidePopup();
                }
            });

            // Hide popup function
            function hidePopup() {
                popup.classList.remove('show');
                setTimeout(() => {
                    popup.style.display = 'none';
                }, 300);
            }

            // Prevent popup from closing when clicking inside
            const popupContent = document.querySelector('.ai-popup');
            if (popupContent) {
                popupContent.addEventListener('click', (e) => {
                    e.stopPropagation();
                });
            }
        }
    }
});


