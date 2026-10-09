<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="org.springframework.http.HttpStatus"%>
<%@ page import="com.doritech.api.Entity.*"%>
<%@ page import="com.doritech.api.DTO.EmployeeMasterDTO"%>
<%@ page import="com.doritech.api.Entity.ParamEntity"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>All Alert Logs</title>

<link rel="stylesheet" href="./css/deviceLogDetails.css" />
<link rel="stylesheet" href="./css/multilingual.css" />
<link rel="stylesheet" href="./externalCSS/bootstrap.min.css" />
<script src="./externalJS/fontawesome.js"></script>

<!-- DATATABLES CSS Links -->
<link rel="stylesheet"
	href="./externalCSS/dataTables.bootstrap4.min.css">
<link rel="stylesheet" href="./externalCSS/buttons.dataTables.min.css">
<link rel="stylesheet"
	href="./externalCSS/responsive.bootstrap4.min.css">

<!-- DATATABLES JS Links -->
<script src="./externalJS/sweetalert2.all.min.js"></script>
<script src="./externalJS/jquery-3.7.0.js"></script>
<script src="./externalJS/jquery.dataTables.min.js"></script>
<script src="./externalJS/dataTables.bootstrap4.min.js"></script>
<script src="./externalJS/responsive.bootstrap4.min.js"></script>
<script src="./externalJS/dataTables.responsive.min.js"></script>
<script src="./externalJS/dataTables.buttons.min.js"></script>
<script src="./externalJS/pdfmake.min.js"></script>
<script src="./externalJS/vfs_fonts.js"></script>
<script src="./externalJS/buttons.html5.min.js"></script>
<script src="./externalJS/buttons.print.min.js"></script>

</head>
<body style="font-size: 12px; overflow-y: hidden;">
	<div class="row mx-1">
		<div class="col-md-12">
			<div class="row">
				<div class="col-md-12">
					<h5 class="heading">All Alert Logs</h5>
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
		<input id="loggedInBranch" value="${branchId}" style="display: none;">
			<div class="m-2">
			
				<div class="card" style="max-height: 70vh; overflow-y: auto;">
				
				<div class="input-group rounded mt-2">
						
						<select id="branchFilter" class="form-control form-control-sm rounded mx-1" style="height: 32px;">
    <option value="">All Branches</option>
</select>
						<select id="zoneFilter"  class="form-control form-control-sm rounded mx-1" style="height: 32px;">
  <option value="">Product Type</option>
</select>
						<select id="alertTypeFilter" class="form-control form-control-sm rounded mx-1" style="height: 32px;">
  <option value="">All Alert Types</option>
</select>

<!-- Device Type Filter -->
<select id="deviceTypeFilter" class="form-control form-control-sm rounded mx-1" style="height: 32px;">
  <option value="">All Device Types</option>
  <option value="D">Digitals</option>
  <option value="O">Others</option>
  <option value="NG">Guard NG</option>
</select>

<!-- Timestamp Filters -->
<input type="date" id="fromDate" class="form-control form-control-sm rounded mx-1" style="height: 32px;"/>
<input type="date" id="toDate" class="form-control form-control-sm rounded mx-1" style="height: 32px;"/>
<button id="searchButton" type="button" class="btn btn-primary">Search</button>
<button id="resetButton" type="button" class="btn btn-secondary mx-1">Reset</button>

					</div>

					<table id="example" class="table table-striped table-bordered" >
						<thead>
							<tr>
								<th>Branch Name</th>
								<th style="display:none">Bank Name</th>
								<th>Branch Code</th>
								<th>System Integrator</th>
								<th style="display:none">Branch Address</th>
								<th>Time Stamp</th>
								<th>Zone Name</th>
								<th>Alert Type</th>
								

							</tr>
						</thead>
						<tbody>
							
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>



<script>
// Main document ready function - initializes everything
$(document).ready(function() {
  // Initialize DataTable with client-side processing to ensure filtering works
  initializeDataTable();

  // Initialize all filters
  initializeFilters();

  // Set up event listeners
  setupEventListeners();
});

// Global variable to store all alert data
var allAlertData = [];

// Initialize DataTable with client-side processing for filtering
function initializeDataTable() {
  // First load all data
  loadAllData(function(data) {
    allAlertData = data;
    
    // Initialize DataTable with the loaded data
    var dataTable = $('#example').DataTable({
      data: allAlertData,
      searching: true, // Enable built-in search for additional flexibility
      lengthChange: true,
      pageLength: 10,
      dom: 'Bfrtip',
      buttons: [
        'copy', 'csv', 'excel', 'pdf', 'print'
      ],
      columns: [
        { data: 'branchName' },  // Branch Name
        { data: 'bank',visible: false  },        // Bank Name
        { data: 'ifsc' },        // Branch IFSC
        { data: 'vendorName' },  // Vendor Name
        { data: 'address',visible: false },     // Branch Address
        { data: 'formattedTime' }, // Time Stamp (formatted)
        { data: 'zoneName' },    // Zone Name
        { data: 'alertType' }    // Alert Type
      ]
    });

    // Store DataTable instance in a global variable for later use
    window.alertLogsDataTable = dataTable;
  });
}

// Function to load all data initially
function loadAllData(callback) {
  showLoader();
  
  var branchCode = $('#loggedInBranch').val();
  
  $.ajax({
    url: "./getAllAlertsReport",
    data: {
      branchCode: branchCode,
      page: 0,
      size: 5000 // Request a large number to get all data
    },
    type: "GET",
    success: function(response) {
      hideLoader();
      
      var payload = response.payload || [];
      console.log("Loaded data count:", payload.length);
      var formattedData = formatDataForTable(payload);
      
      callback(formattedData);
    },
    error: function(xhr, status, error) {
      hideLoader();
      console.error("Error fetching data:", error);
      
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'Failed to fetch alert logs data. Please try again.'
      });
      
      callback([]);
    }
  });
}

// Format raw data for DataTable - enhanced for better filtering
function formatDataForTable(payload) {
  var formattedData = [];
  for (var i = 0; i < payload.length; i++) {
    var item = payload[i];
    
    // Parse timestamp once for efficiency
    var parsedDate = item.timeStamp ? new Date(item.timeStamp) : null;
    
    formattedData.push({
      branchName: item.branchName || '',
      bank: item.bank || '',
      ifsc: item.ifsc || '',
      vendorName: item.vendorName || '',
      address: item.address || '',
      timeStamp: parsedDate, // Store the actual Date object for filtering
      formattedTime: formatDateTime(parsedDate) || '', // Formatted string for display
      zoneName: item.zoneDesiredName || '',
      alertType: item.alertType || '',
      deviceType: item.deviceType || '', // Store deviceType for filtering
      // Store normalized lowercase values for case-insensitive filtering
      branchNameLower: (item.branchName || '').toLowerCase(),
      zoneNameLower: (item.zoneDesiredName || '').toLowerCase(),
      alertTypeLower: (item.alertType || '').toLowerCase(),
      deviceTypeLower: (item.deviceType || '').toLowerCase()
    });
  }
  return formattedData;
}

// Initialize all filter dropdowns
function initializeFilters() {
  var loggedInBranch = $('#loggedInBranch').val();
  
  // Fetch branches for branch filter
  fetchBranches();
  
  // Fetch zones and alert types based on initial branch
  fetchZones(loggedInBranch);
  
  // Clear date values (removed auto-fill date functionality)
  $('#fromDate').val('');
  $('#toDate').val('');
  
  console.log("Filters initialized without default date range");
}

// Set up event listeners for all interactive elements
function setupEventListeners() {
  // Branch filter change event
  $('#branchFilter').change(function() {
    var selectedBranch = $(this).val() || $('#loggedInBranch').val();
    fetchZones(selectedBranch);
  });
  
  // Search button click event - only filter when this is clicked
  $('#searchButton').click(function() {
    applyFilters();
  });

  // Add event listeners for the date inputs (only for validation)
  $('#fromDate, #toDate').change(function() {
    validateDateRange();
  });
  
  // Reset button click event
  $('#resetButton').click(function() {
    resetFilters();
  });
}

// Reset all filters to default values
function resetFilters() {
  // Reset all dropdown filters
  $('#branchFilter').val('');
  $('#zoneFilter').val('');
  $('#alertTypeFilter').val('');
  $('#deviceTypeFilter').val('');
  
  // Clear date fields
  $('#fromDate').val('');
  $('#toDate').val('');
  
  // Reset filters in DataTable
  if (window.alertLogsDataTable) {
    // Clear any existing filters
    $.fn.dataTable.ext.search.pop();
    window.alertLogsDataTable.search('').draw();
  }
  
  console.log("All filters have been reset");
  
  // Show confirmation for better UX
  Swal.fire({
    icon: 'success',
    title: 'Filters Reset',
    text: 'All filters have been cleared',
    timer: 1500,
    showConfirmButton: false
  });
}

// Apply all filters to the DataTable
function applyFilters() {
  if (!window.alertLogsDataTable) {
    console.error("DataTable not initialized yet");
    return;
  }
  
  showLoader();
  
  // Get filter values
  var selectedBranchCode = $('#branchFilter').val();
  var selectedZone = $('#zoneFilter').val();
  var selectedAlertType = $('#alertTypeFilter').val();
  var selectedDeviceType = $('#deviceTypeFilter').val();
  var fromDateStr = $('#fromDate').val();
  var toDateStr = $('#toDate').val();
  
  console.log("Applying filters:", {
    branchCode: selectedBranchCode,
    zone: selectedZone,
    alertType: selectedAlertType,
    deviceType: selectedDeviceType,
    fromDate: fromDateStr,
    toDate: toDateStr
  });
  
  // Create a new filtering function that will be applied to the data
  $.fn.dataTable.ext.search.push(function(settings, searchData, index, rowData, counter) {
    var row = allAlertData[index];
    
    // Check if we have a valid row - should never happen but just in case
    if (!row) return false;
    
    // Branch filter
    if (selectedBranchCode && selectedBranchCode !== '') {
      // Find the branch name that matches the selected branch code
      var selectedBranchName = $("#branchFilter option:selected").text();
      if (selectedBranchName !== 'All Branches' && selectedBranchName !== 'Current Branch' && 
          row.branchNameLower !== selectedBranchName.toLowerCase()) {
        return false;
      }
    }
    
    // Zone filter (Product Type)
    if (selectedZone && selectedZone !== '' && 
        row.zoneNameLower !== selectedZone.toLowerCase()) {
      return false;
    }
    
    // Alert type filter
    if (selectedAlertType && selectedAlertType !== '' && 
        row.alertTypeLower !== selectedAlertType.toLowerCase()) {
      return false;
    }
    
    // Device type filter
    if (selectedDeviceType && selectedDeviceType !== '' && 
        row.deviceTypeLower !== selectedDeviceType.toLowerCase()) {
      return false;
    }
    
    // Date range filter
    if (fromDateStr || toDateStr) {
      var rowDate = row.timeStamp;
      if (!rowDate) return false;
      
      var fromDate = fromDateStr ? new Date(fromDateStr) : null;
      var toDate = toDateStr ? new Date(toDateStr) : null;
      
      // Set time portion for proper comparison
      if (fromDate) {
        fromDate.setHours(0, 0, 0, 0); // Start of day
      }
      
      if (toDate) {
        toDate.setHours(23, 59, 59, 999); // End of day
      }
      
      if (fromDate && rowDate < fromDate) return false;
      if (toDate && rowDate > toDate) return false;
    }
    
    return true;
  });
  
  // Apply the filter and redraw the table
  window.alertLogsDataTable.draw();
  
  // Remove our custom filter function after drawing
  $.fn.dataTable.ext.search.pop();
  
  // Show message if no results found
  if (window.alertLogsDataTable.page.info().recordsDisplay === 0) {
    Swal.fire({
      icon: 'info',
      title: 'No Results',
      text: 'No data found for the selected filters.'
    });
  }
  
  hideLoader();
}

// Validate that the date range is valid
function validateDateRange() {
  var fromDateVal = $('#fromDate').val();
  var toDateVal = $('#toDate').val();
  
  // Skip validation if either field is empty
  if (!fromDateVal || !toDateVal) {
    return;
  }
  
  var fromDate = new Date(fromDateVal);
  var toDate = new Date(toDateVal);
  
  // Only validate if both dates are valid
  if (isNaN(fromDate.getTime()) || isNaN(toDate.getTime())) {
    console.log("One of the dates is invalid:", fromDateVal, toDateVal);
    return; // Invalid dates, don't validate
  }
  
  if (fromDate > toDate) {
    Swal.fire({
      icon: 'warning',
      title: 'Invalid Date Range',
      text: 'From date cannot be after To date'
    });
    
    // Clear both date fields when range is invalid
    $('#fromDate').val('');
    $('#toDate').val('');
  }
}

// Fetch all child branches and populate branchFilter dropdown
function fetchBranches() {
  var branchCode = $('#loggedInBranch').val();

  if (!branchCode) {
    console.error("Logged-in branch code not found!");
    return;
  }

  $.ajax({
    url: './getAllChildren',
    data: {
      branchCode: branchCode
    },
    type: 'GET',
    success: function(data) {
      if (data && data.payload && data.payload.length > 0) {
        var branchSelect = $('#branchFilter');
        branchSelect.html('<option value="">All Branches</option>');
        
        // Add the logged-in branch as first option
        branchSelect.append('<option value="' + branchCode + '">' + 'Current Branch' + '</option>');
        
        for (var i = 0; i < data.payload.length; i++) {
          var branch = data.payload[i];
          branchSelect.append('<option value="' + branch.branchCode + '">' + branch.branchDesc + '</option>');
        }
      } else {
        console.log('No child branches found, using only current branch');
      }
    },
    error: function(xhr, status, error) {
      console.error('Failed to fetch branch list:', error);
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'Failed to load branch list. Please refresh the page.'
      });
    }
  });
}

// Fetch zones and alert types for a specific branch
function fetchZones(branchCode) {
  if (!branchCode) {
    console.error("Branch code is missing for fetching zones.");
    return;
  }

  $.ajax({
    url: './getAllDeviceZoneNameByBranchCode',
    data: {
      branchCode: branchCode
    },
    type: 'GET',
    success: function(data) {
      if (data && data.statusCode === 200 && data.payload && data.payload.zoneList) {
        // Extract unique zone names and alert types
        var zoneSet = new Set();
        var alertTypeSet = new Set();

        for (var i = 0; i < data.payload.zoneList.length; i++) {
          var zone = data.payload.zoneList[i];
          if (zone.zoneDesiredName) {
            zoneSet.add(zone.zoneDesiredName);
          }
          if (zone.alertType) {
            alertTypeSet.add(zone.alertType);
          }
        }

        // Populate zone dropdown
        var zoneFilter = $('#zoneFilter');
        zoneFilter.html('<option value="">Product Type</option>');
        zoneSet.forEach(function(zone) {
          zoneFilter.append('<option value="' + zone + '">' + zone + '</option>');
        });

        // Populate alert type dropdown
        var alertTypeFilter = $('#alertTypeFilter');
        alertTypeFilter.html('<option value="">All Alert Types</option>');
        alertTypeSet.forEach(function(alertType) {
          alertTypeFilter.append('<option value="' + alertType + '">' + alertType + '</option>');
        });
      } else {
        console.warn("No zone/alert data found for branch", branchCode);
        
        // Reset dropdowns when no data is found
        $('#zoneFilter').html('<option value="">Product Type</option>');
        $('#alertTypeFilter').html('<option value="">All Alert Types</option>');
      }
    },
    error: function(xhr, status, error) {
      console.error("Failed to fetch zone names:", error);
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'Failed to load zone data. Please try again.'
      });
    }
  });
}

// Format date for date input fields (YYYY-MM-DD)
function formatDateForInput(date) {
  if (!date || isNaN(date.getTime())) return '';
  
  var year = date.getFullYear();
  var month = (date.getMonth() + 1).toString().padStart(2, '0');
  var day = date.getDate().toString().padStart(2, '0');
  return year + '-' + month + '-' + day;
}

// Format timestamp for display (DD-MM-YYYY HH:MM:SS)
function formatDateTime(date) {
  if (!date || isNaN(date.getTime())) return '';
  
  try {
    var year = date.getFullYear();
    var month = (date.getMonth() + 1).toString().padStart(2, '0');
    var day = date.getDate().toString().padStart(2, '0');
    var hours = date.getHours().toString().padStart(2, '0');
    var minutes = date.getMinutes().toString().padStart(2, '0');
    var seconds = date.getSeconds().toString().padStart(2, '0');
    
    return day + '-' + month + '-' + year + ' ' + hours + ':' + minutes + ':' + seconds;
  } catch (error) {
    console.error("Error formatting date:", error);
    return ''; // Return empty string on error
  }
}

// Handle padStart for older browsers
if (!String.prototype.padStart) {
  String.prototype.padStart = function padStart(targetLength, padString) {
    targetLength = targetLength >> 0; // truncate if number, or convert non-number to 0;
    padString = String(typeof padString !== 'undefined' ? padString : ' ');
    if (this.length >= targetLength) {
      return String(this);
    } else {
      targetLength = targetLength - this.length;
      if (targetLength > padString.length) {
        padString += padString.repeat(targetLength / padString.length); // append to original to ensure we are longer than needed
      }
      return padString.slice(0, targetLength) + String(this);
    }
  };
}

// Show loading spinner
function showLoader() {
  if (document.getElementById('loaderOverlay')) {
    document.getElementById('loaderOverlay').style.display = 'flex';
  } else {
    // Create loader if it doesn't exist
    var loader = document.createElement('div');
    loader.id = 'loaderOverlay';
    loader.style.cssText = 'position:fixed;top:0;left:0;width:100%;height:100%;background:rgba(255,255,255,0.7);display:flex;justify-content:center;align-items:center;z-index:9999;';
    
    var spinner = document.createElement('div');
    spinner.style.cssText = 'border:5px solid #f3f3f3;border-top:5px solid #3498db;border-radius:50%;width:50px;height:50px;animation:spin 2s linear infinite;';
    
    // Add keyframes for spinner animation
    var style = document.createElement('style');
    style.textContent = '@keyframes spin {0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); }}';
    document.head.appendChild(style);
    
    loader.appendChild(spinner);
    document.body.appendChild(loader);
  }
}

// Hide loading spinner
function hideLoader() {
  if (document.getElementById('loaderOverlay')) {
    document.getElementById('loaderOverlay').style.display = 'none';
  }
}
</script>

	<script src="./js/navBarToggle.js"></script>
	<script src="./externalJS/bootstrap.bundle.min.js"></script>
	<script src="./js/activeDeviceLogDetails.js"></script>
	<!-- <script src="./js/dateAndTime.js"></script> -->
	<script src="./externalJS/multilingual.js"></script>
	<script src="./js/googleTranslateElementInit.js"></script>
</body>
</html>