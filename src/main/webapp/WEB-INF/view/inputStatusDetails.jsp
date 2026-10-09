<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
<title>All Alert Report</title>

<link rel="stylesheet" href="./css/deviceLogDetails.css" />
<link rel="stylesheet" href="./css/multilingual.css" />
<link rel="stylesheet" href="./externalCSS/bootstrap.min.css" />
<link href="./fonts/poppins.css" rel="stylesheet">
<script src="./externalJS/fontawesome.js"></script>

<!-- DATATABLES CSS Links -->
<link rel="stylesheet" href="./externalCSS/dataTables.bootstrap4.min.css">
<link rel="stylesheet" href="./externalCSS/buttons.dataTables.min.css">
<link rel="stylesheet" href="./externalCSS/responsive.bootstrap4.min.css">

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
<script src="./externalJS/jszip.min.js"></script>

<script src="https://cdn.amcharts.com/lib/5/index.js"></script>
<script src="https://cdn.amcharts.com/lib/5/percent.js"></script>
<script src="https://cdn.amcharts.com/lib/5/themes/Animated.js"></script>

<style>
#chartdiv {
	width: 100%;
	height: 350px;
}

#example thead th, #example tbody td {
	white-space: nowrap;
}

#example_wrapper .dataTables_scrollBody::-webkit-scrollbar,
	#detailsTable_wrapper .dataTables_scrollBody::-webkit-scrollbar {
	height: 4px; /* Set the height of the horizontal scrollbar */
}

.acknowledged {
	color: rgba(128,103,220,255);
	border: 1px solid rgba(128,103,220,255);
	font-weight: bold;
	padding: 3px;
}

.testing {
	color: rgba(103,183,220,255);
	border: 1px solid rgba(103,183,220,255);
	font-weight: bold;
	padding: 3px;
}

.informedVendor {
	color: rgba(103,148,220,255);
	border: 1px solid rgba(103,148,220,255);
	font-weight: bold;
	padding: 3px;
}

.falseAlarm {
	color: rgba(103,113,220,255);
	border: 1px solid rgba(103,113,220,255);
	font-weight: bold;
	padding: 3px;
}

/* Custom styles for filter alignment */
.filter-section {
	background-color: #f8f9fa;
	padding: 20px;
	border-radius: 8px;
	margin-bottom: 20px;
	border: 1px solid #dee2e6;
}

.filter-row {
	display: flex;
	flex-wrap: wrap;
	gap: 15px;
	align-items: flex-end;
	margin-bottom: 15px;
}

.filter-item {
	display: flex;
	flex-direction: column;
	min-width: 180px;
	flex: 1;
}

.filter-item label {
	font-size: 12px;
	font-weight: 600;
	color: #495057;
	margin-bottom: 5px;
	white-space: nowrap;
}

.filter-item select,
.filter-item input {
	height: 38px;
	font-size: 12px;
	border: 1px solid #ced4da;
	border-radius: 4px;
	padding: 6px 12px;
}

.search-button-container {
	display: flex;
	justify-content: center;
	margin-top: 10px;
}

.search-button-container .btn {
	min-width: 120px;
	height: 40px;
	font-size: 14px;
	font-weight: 600;
}

/* Custom Loader Styles */
.full-page-loader {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(255, 255, 255, 0.9);
	display: flex;
	justify-content: center;
	align-items: center;
	z-index: 9999;
	backdrop-filter: blur(2px);
}

.loader-content {
	text-align: center;
	color: #333;
}

.spinner {
	width: 50px;
	height: 50px;
	border: 4px solid #f3f3f3;
	border-top: 4px solid #007bff;
	border-radius: 50%;
	animation: spin 1s linear infinite;
	margin: 0 auto 15px;
}

@keyframes spin {
	0% { transform: rotate(0deg); }
	100% { transform: rotate(360deg); }
}

.loader-text {
	font-size: 16px;
	font-weight: 500;
}

/* Responsive adjustments */
@media (max-width: 768px) {
	.filter-row {
		flex-direction: column;
		gap: 10px;
	}
	
	.filter-item {
		min-width: 100%;
	}
}

@media (max-width: 1200px) {
	.filter-item {
		min-width: 160px;
	}
}

.table-container {
    position: relative;
}

#loaderOverlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    backdrop-filter: blur(4px);
    background-color: rgba(255, 255, 255, 0.8);
    z-index: 10;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 4px;
}

.loader-spinner {
    width: 40px;
    height: 40px;
    border: 5px solid #ccc;
    border-top: 5px solid #028bd7;
    border-radius: 50%;
    animation: spin 1s linear infinite;
}

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}

</style>
</head>
<body style="font-size: 12px; overflow-y: auto; font-family:Poppins; ">
    <!-- Full Page Loader -->
    <div id="fullPageLoader" class="full-page-loader" style="display: none;">
        <div class="loader-content">
            <div class="spinner"></div>
            <div class="loader-text">Loading...</div>
        </div>
    </div>

    <div class="row mx-1">
        <div class="col-md-12">
            <div class="row">
                <div class="col-md-12">
                    <h5 class="heading">All Alerts Report</h5>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <div class="m-2">
                <div class="card">
                    <div id="chartdiv"></div>
                    
                    <!-- Improved Filter Controls -->
                    <div class="filter-section mx-1">
                        <div class="filter-row">
                            <!-- Head-Office Branch Dropdown -->
                            <div class="filter-item">
                                <label for="headIdDropdown">Head-Office Branch</label>
                                <select class="form-control" name="headIdDropdown" id="headIdDropdown">
                                    <!-- Options will be populated via AJAX -->
                                </select>
                            </div>

                            <!-- Zone Dropdown -->
                            <div class="filter-item">
                                <label for="zoneIdDropdown">Zone</label>
                                <select class="form-control" name="zoneIdDropdown" id="zoneIdDropdown">
                                    <!-- Options will be populated dynamically -->
                                </select>
                            </div>

                            <!-- Branch Dropdown -->
                            <div class="filter-item">
                                <label for="branchIdDropdown">Branch</label>
                                <select class="form-control" name="branchIdDropdown" id="branchIdDropdown">
                                    <!-- Options will be populated dynamically -->
                                </select>
                            </div>

                            <!-- System Integrator Filter -->
                            <div class="filter-item">
                                <c:set var="usedIntegrators" value="" />
                                <label for="vendorFilter">System Integrator</label>
                                <select id="vendorFilter" class="form-control">
                                    option value="all">All</option>
									
                                    
                                </select>
                            </div>
							
							<!-- Product Filter -->
							<div class="filter-item">
							    <label for="productFilter">Product</label>
							    <select id="productFilter" class="form-control">
							        <option value="all">All</option>
							        <option value="CCTV">CCTV</option>
							        <option value="SECURITY ALARM">Security alarm</option>
							        <option value="FIRE ALARM">Fire alarm</option>
							        <option value="BACS">BACS</option>
							        <option value="ETL">ETL</option>
							    </select>
							</div>


                            <!-- Status Filter -->
                            <div class="filter-item">
                                <label for="statusFilter">Status</label>
                                <select id="statusFilter" class="form-control">
                                    <option value="all">All</option>
                                    <option value="Open">Open</option>
                                    <option value="Resolved">Resolved</option>
                                </select>
                            </div>

                            <!-- From Date Filter -->
                            <div class="filter-item">
                                <label for="fromDate">From Date</label>
                                <input type="date" id="fromDate" class="form-control">
                            </div>

                            <!-- To Date Filter -->
                            <div class="filter-item">
                                <label for="toDate">To Date</label>
                                <input type="date" id="toDate" class="form-control">
                            </div>
                        </div>

                        <!-- Search Button (Centered below filters) -->
                        <div class="search-button-container">
                            <button type="button" class="btn btn-primary" id="searchButton">
                                <i class="fas fa-search mr-2"></i>SEARCH
                            </button>
                        </div>
                    </div>
                    
                   <div class="table-container">
    <div id="loaderOverlay" style="display: none;">
        <div class="loader-spinner"></div>
    </div>
    
    <table id="example" class="table table-striped table-bordered">
        <thead>
            <tr>
                <th>Branch Code</th>
                <th>Branch Name</th>
                <th>Branch Contact</th>
                <th>System Integrater</th>
                <th>Product</th>
                <th>Alert Type</th>
                <th>Alert Time Stamp</th>
                <th>Resolved Times Stamp</th>
                <th>Resolution Time</th>
                <th>Status</th>
            </tr>
        </thead>
        <tbody id="inputListTableBody">
            
        </tbody>
    </table>
</div>

                    <div class="modal fade" id="detailsModal" tabindex="-1" aria-labelledby="detailsModalLabel" aria-hidden="true">
                      <div class="modal-dialog modal-xl">
                        <div class="modal-content">
                          <div class="modal-header">
                            <h5 class="modal-title" id="detailsModalLabel">Alert Resolution Details</h5>
                            <button type="button" class="close" data-bs-dismiss="modal" aria-label="Close">
                              <span aria-hidden="true">&times;</span>
                            </button>
                          </div>
                          <div class="modal-body">
                            <table id="detailsTable" class="table table-striped table-bordered">
                              <thead>
                                <tr>
                                  <th>Branch Name</th>
                                  <th>Contact No.</th>
                                  <th>System Integrater</th>
                                  <th>Alarmed Zone</th>
                                  <th>Alert Type</th>
                                  <th>Alert Time Stamp</th>
                                  <th>Resolved Time Stamp</th>
                                  <th>Resolution Time</th>
                                  <th>Status</th>
                                </tr>
                              </thead>
                              <tbody id="detailsTableBody">
                              </tbody>
                            </table>
                          </div>
                        </div>
                      </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    


<script>
// Custom Loader Functions
function displayLoader(text = 'Loading...') {
    $('#fullPageLoader .loader-text').text(text);
    $('#fullPageLoader').fadeIn(200);
}

function dismissLoader() {
    $('#fullPageLoader').fadeOut(200);
}

function showLoader() {
    document.getElementById('loaderOverlay').style.display = 'flex';
}

function hideLoader() {
    document.getElementById('loaderOverlay').style.display = 'none';
}


var branchCode = sessionStorage.getItem('branch');
am5.ready(function() {
    var root = am5.Root.new("chartdiv");
    root.setThemes([am5themes_Animated.new(root)]);
    var chart = root.container.children.push(am5percent.PieChart.new(root, {
        layout: root.verticalLayout,
        innerRadius: am5.percent(50)
    }));
    var series = chart.series.push(am5percent.PieSeries.new(root, {
        valueField: "value",
        categoryField: "category",
        alignLabels: false
    }));
    series.labels.template.setAll({
        textType: "circular",
        centerX: 0,
        centerY: 0
    });
    series.labels.template.setAll({
        visible: false
    });

    fetch('./getInputStatusDetails?branchCodeRequest=' + branchCode)
        .then(response => response.json())
        .then(data => {
        	const chartData = Object.keys(data.payload.alarmTypeCounts)
            .filter(key => key && key.trim()) // ✅ Ignore empty, null, or undefined keys
            .map(key => ({
                category: key,
                value: data.payload.alarmTypeCounts[key]
            }));
            series.data.setAll(chartData);

            var legend = chart.children.push(am5.Legend.new(root, {
                centerX: am5.percent(50),
                x: am5.percent(50),
                marginTop: 15,
                marginBottom: 15
            }));
            legend.data.setAll(series.dataItems);

            series.dataItems.forEach(function (dataItem) {
                dataItem.get("slice").events.on("click", function () {
                    const category = dataItem.get("category");
                    const filteredData = data.payload.inputStatusDetails.filter(item => item.alarmtype === category);
                    const detailsTableBody = document.getElementById('detailsTableBody');
                    detailsTableBody.innerHTML = '';

                    filteredData.forEach(function(item) {
                        const row = '<tr>' +
                            '<td>' + item.branchName + '</td>' +
                            '<td style="text-align: right">' + item.mobileNo + '</td>' +
                            '<td>' + item.vendorName + '</td>' +
                            '<td>' + item.alarmtype + '</td>' +
                            '<td>' + item.alertType + '</td>' +
                            '<td style="text-align: right;">' + item.alarmTime + '</td>' +
                            '<td style="text-align: right;">' + item.issueResolvedTime + '</td>' +
                            '<td>' + item.timeDifference + '</td>' +
                            '<td>' + item.alarmDescription + '</td>' +
                        '</tr>';
                        detailsTableBody.insertAdjacentHTML('beforeend', row);
                    });

                    $('#detailsModal').modal('show');
                   
                    $('#detailsTable').DataTable();
                });
            });

            series.appear(1000, 100);
        })
        .catch(error => console.error('Error fetching data:', error));
});

</script>

<script>
$(document).ready(function() {
    $('#zoneIdDropdown').prop('disabled', true);
    $('#branchIdDropdown').prop('disabled', true);

    var loggedBranchCode=sessionStorage.getItem("branch")
    
    $.ajax({
        url: './getAllChildren',  
        method: 'GET',
        data: { branchCode: loggedBranchCode },
        success: function(data) {
            var selectElement = $('#headIdDropdown');
            selectElement.empty(); 
            selectElement.append('<option value="All">All</option>');
            
            if (data.payload && data.payload.length > 0) {
            	
            	var headOffices = data.payload.filter(function(branch) {
                    return branch.branchType === "HEAD OFFICE";
                });
               if (headOffices.length > 0) {
                    $.each(headOffices, function(index, branch) {
                        selectElement.append('<option value="' + branch.branchCode + '">' + branch.branchDesc + '</option>');
                    });
                    selectElement.prop('disabled', false);  
                } else {
                    selectElement.prop('disabled', true);  
                } 
            } else {
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
                url: './getAllZonesByBranchCode',  
                method: 'GET',
                data: { branchCode: branchCode },  
                success: function(data) {
                    var selectElement = $('#zoneIdDropdown');
                    selectElement.empty();  
                    selectElement.append('<option value="All">All</option>');  
                    
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
            $('#zoneIdDropdown').empty().append('<option value="">Select Zone</option>').prop('disabled', true);  
            $('#branchIdDropdown').empty().append('<option value="">Select Branch</option>').prop('disabled', true);  
        }
    });

    $('#zoneIdDropdown').change(function() {
        var zoneCode = $(this).val(); 
        if (zoneCode) {
            $('#branchIdDropdown').prop('disabled', false);

            $.ajax({
                url: './getBranchesByBranchCodes', 
                method: 'GET',
                data: { branchCode: zoneCode },  
                success: function(data) {
                    var selectElement = $('#branchIdDropdown');
                    selectElement.empty();  
                    selectElement.append('<option value="All">All</option>');  
                    
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
            $('#branchIdDropdown').empty().append('<option value="">Select Branch</option>').prop('disabled', true);  
        }
    });


});

    var reportTitle = "All Alert Report";
    var aboutText = "ABOUT: All Alert Report provides insights into the time taken to resolve faults in security systems. It records alert timestamps, resolution timestamps, and calculates the response duration for each incident. This report helps in monitoring efficiency, improving response times, and ensuring system reliability.";

function convertImageToBase64(url, callback) {
    var img = new Image();
    img.crossOrigin = 'Anonymous';
    img.onload = function () {
        var canvas = document.createElement('canvas');
        canvas.width = img.width;
        canvas.height = img.height;
        var ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0);
        var dataURL = canvas.toDataURL('image/png');
        callback(dataURL);
    };
    img.src = url;
}
document.addEventListener('DOMContentLoaded', function () {
    // Convert logo image to Base64 once
    convertImageToBase64('./logo/boi_logo.png', function (base64Logo) {
        
        // Add custom filter for DataTables based on your dropdowns and date range
        $.fn.dataTable.ext.search.push(function(settings, data, dataIndex) {
            if (settings.nTable.id !== 'example') return true; // only filter #example table
            
            const selectedProduct = $('#productFilter').val();
            const selectedStatus = $('#statusFilter').val();
            const selectedVendor = $('#vendorFilter').val();
            const selectedHead = $('#headIdDropdown').val();
            const selectedZone = $('#zoneIdDropdown').val();
            const selectedBranch = $('#branchIdDropdown option:selected').text();
            
            const fromDate = $('#fromDate').val() ? new Date($('#fromDate').val()) : null;
            const toDate = $('#toDate').val() ? new Date($('#toDate').val()) : null;

            const rowBranchName = data[1] ? data[1].trim() : '';
            const vendor = data[3] ? data[3].trim() : '';
            const product = data[4] ? data[4].trim() : '';
            const status = data[9] ? data[9].trim() : '';
            const alarmTimeText = data[6] ? data[6].trim() : '';

            if (selectedBranch && selectedBranch !== "All" && selectedBranch !== rowBranchName) {
                return false;
            }
            if (selectedStatus && selectedStatus !== "all" && selectedStatus !== status) {
                return false;
            }
            if (selectedProduct && selectedProduct !== "all" && selectedProduct !== product) {
                return false;
            }
            if (selectedVendor && selectedVendor !== "all" && selectedVendor !== vendor) {
                return false;
            }

            if (alarmTimeText) {
                // Parse date: format "13/06/2024, 10:46:03"
                const [datePart, timePart] = alarmTimeText.split(', ');
                const [day, month, year] = datePart.split('/').map(Number);
                const [hours, minutes, seconds] = timePart.split(':').map(Number);
                const alertDate = new Date(year, month - 1, day, hours, minutes, seconds);

                if (fromDate && alertDate < fromDate) return false;
                if (toDate && alertDate > toDate) return false;
            }

            return true;
        });

        // Initialize DataTable with empty data first
        window.inputStatusTable = $('#example').DataTable({
            pageLength: 100,
            paging: true,
            lengthChange: false,
            scrollY: '45vh',
            scrollX: true,
            dom: 'Bfrtip',
            buttons: [
                {
                    extend: 'excelHtml5',
                    title: function() {
                        const now = new Date();
                        return 'DI-HMS : ' + document.title + ' - ' + now.toLocaleString();
                    },
                    customize: function(xlsx) {
                        const sheet = xlsx.xl.worksheets['sheet1.xml'];
                        const styles = xlsx.xl['styles.xml'];
                        const font = '<font><sz val="14"/><color rgb="FFFFFFFF"/><b/></font>';
                        $(styles).find('fonts').append(font);
                        const fontId = $(styles).find('fonts font').length - 1;
                        const fill = '<fill><patternFill patternType="solid"><fgColor rgb="3674B5"/></patternFill></fill>';
                        $(styles).find('fills').append(fill);
                        const fillId = $(styles).find('fills fill').length - 1;
                        const cellXfs = $(styles).find('cellXfs');
                        const xf = '<xf xfId="0" applyFont="1" fontId="' + fontId + '" applyFill="1" fillId="' + fillId + '"/>';
                        cellXfs.append(xf);
                        const xfId = cellXfs.find('xf').length - 1;
                        $('row c[r="A1"]', sheet).attr('s', xfId);
                        // Optional: add image to Excel sheet if needed (complex, can be skipped)
                    }
                },
                {
                    extend: 'pdfHtml5',
                    text: 'PDF',
                    orientation: 'landscape',
                    pageSize: 'A4',
                    title: function() {
                        const now = new Date();
                        return 'DI-HMS : ' + document.title + ' - ' + now.toLocaleString();
                    },
                    customize: function (doc) {
                        doc.pageMargins = [20, 30, 20, 40];
                        doc.content.splice(0, 0, {
                            columns: [
                                {
                                    image: base64Logo,
                                    width: 90,
                                    alignment: 'left',
                                    margin: [0, 0, 10, 10]
                                },
                                {
                                    text: document.title,
                                    fontSize: 16,
                                    bold: true,
                                    alignment: 'right',
                                    margin: [0, 15, 0, 0]
                                }
                            ]
                        });
                        doc.content.splice(1, 0, {
                            canvas: [{ type: 'line', x1: 0, y1: 0, x2: 535, y2: 0, lineWidth: 1 }]
                        });
                        doc.watermark = {
                            text: 'Digitals India',
                            color: 'gray',
                            opacity: 0.1,
                            bold: true,
                            italics: false
                        };
                        doc.styles.tableHeader = {
                            bold: true,
                            fontSize: 12,
                            color: 'black',
                            fillColor: '#e0e0e0',
                            alignment: 'left',
                            margin: [5, 5, 5, 5]
                        };
                        doc.styles.tableBodyEven = {
                            fontSize: 13,
                            color: 'black',
                            fillColor: '#f9f9f9',
                            margin: [5, 5, 5, 5]
                        };
                        doc.styles.tableBodyOdd = {
                            fontSize: 13,
                            color: 'black',
                            margin: [5, 5, 5, 5]
                        };
                        doc.footer = function (currentPage, pageCount) {
                            return {
                                columns: [
                                    {
                                        text: 'Generated on: ' + new Date().toLocaleDateString(),
                                        alignment: 'left',
                                        fontSize: 8,
                                        margin: [20, 0, 0, 0]
                                    },
                                    {
                                        text: 'Page ' + currentPage + ' of ' + pageCount,
                                        alignment: 'right',
                                        fontSize: 8,
                                        margin: [0, 0, 20, 0]
                                    }
                                ]
                            };
                        };
                    }
                },
                'print'
            ],
            order: [[5, 'desc']],
            data: [], // start empty; load after fetch
            columns: [
                { data: 'ifsc' },
                { data: 'branchName' },
                { data: 'mobileNo' },
                { data: 'vendorName' },
                { data: 'alarmtype' },
                { data: 'alertType' },
                { data: 'alarmTime' },
                { data: 'issueResolvedTime' },
                { data: 'timeDifference' },
                { data: 'alarmDescription' }
            ]
        });

        // Fetch data and load into table
        const branchCode = sessionStorage.getItem('branch');
        
        //alert(branchCode)
        showLoader();
        fetch('./getResponseTimeReport?branchCode=' + branchCode)
            .then(response => response.json())
            .then(data => {
            	
				
				const inputList = data?.payload?.inputStatusDetails || [];

                // Extract unique vendor names
                const usedIntegrators = new Set();

                // Get the dropdown element
                const vendorFilter = document.getElementById('vendorFilter');

                // Clear existing options
                vendorFilter.innerHTML = '<option value="all">All</option>';

                // Populate dropdown with unique vendor names
                inputList.forEach(input => {
                    const vendorName = input.vendorName || '';
                    if (vendorName && !usedIntegrators.has(vendorName)) {
                        usedIntegrators.add(vendorName);

                        const option = document.createElement('option');
                        option.value = vendorName;
                        option.textContent = vendorName;
                        vendorFilter.appendChild(option);
                    }
                });

				
                const rows = (data?.payload?.inputStatusDetails || []).map(input => ({
                    ifsc: input.ifsc || '',
                    branchName: input.branchName || '',
                    mobileNo: input.mobileNo || '',
                    vendorName: input.vendorName || '',
                    alarmtype: input.alarmtype || '',
                    alertType: input.alertType || '',
                    alarmTime: input.alarmTime || '',
                    issueResolvedTime: input.issueResolvedTime || '',
                    timeDifference: input.timeDifference || '',
                    alarmDescription: input.alarmDescription || ''
                }));

                window.inputStatusTable.clear().rows.add(rows).draw();
                hideLoader();
            })
            .catch(error => console.error('Error fetching input status details:', error));

        // Search button click handler with loader and alert messages
        $('#searchButton').click(function () {
            displayLoader('Filtering data...');
            $('#searchButton').prop('disabled', true);
            setTimeout(function() {
                try {
                    window.inputStatusTable.draw();
                    const visibleRowCount = window.inputStatusTable.rows({ filter: 'applied' }).count();
                    setTimeout(function() {
                        dismissLoader();
                        if (visibleRowCount === 0) {
                            Swal.fire({
                                icon: 'warning',
                                title: 'No Matched Data Found',
                                text: 'No records match your filter criteria.',
                                timer: 2000,
                                showConfirmButton: false
                            });
                        } else {
                            Swal.fire({
                                icon: 'success',
                                title: 'Search Completed',
                                text: `Found ${visibleRowCount} matching records`,
                                timer: 2000,
                                showConfirmButton: false
                            });
                        }
                        $('#searchButton').prop('disabled', false);
                    }, 500);
                } catch (error) {
                    console.error('Error during search:', error);
                    dismissLoader();
                    Swal.fire({
                        icon: 'error',
                        title: 'Search Error',
                        text: 'An error occurred while filtering the data. Please try again.',
                        confirmButtonText: 'OK'
                    });
                    $('#searchButton').prop('disabled', false);
                }
            }, 100);
        });

    }); 
});

</script>

<script src="./js/navBarToggle.js"></script>
<script src="./externalJS/bootstrap.bundle.min.js"></script>



</body>
</html>