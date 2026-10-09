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
<title>Alert Audit Logs</title>

<link rel="stylesheet" href="./css/deviceLogDetails.css" />
<link rel="stylesheet" href="./css/multilingual.css" />
<link rel="stylesheet" href="./externalCSS/bootstrap.min.css" />
<link href="./fonts/poppins.css" rel="stylesheet">
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
<script src="./externalJS/jszip.min.js"></script>

<script src="https://cdn.amcharts.com/lib/5/index.js"></script>
<script src="https://cdn.amcharts.com/lib/5/percent.js"></script>
<script src="https://cdn.amcharts.com/lib/5/themes/Animated.js"></script>


<script type="text/javascript" src="https://cdn.jsdelivr.net/momentjs/latest/moment.min.js"></script>
<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.min.js"></script>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.css" />


<style>


/* Custom scrollbar styles */
#example_wrapper .dataTables_scrollBody::-webkit-scrollbar {
  height: 4px; /* Set the height of the horizontal scrollbar */
}


  #chartdiv {
    width: 100%;
    height: 400px;
  }

  .daterange-input-group {
    max-width: 300px;
    margin: 0 auto;
  }

  .daterange-input {
    border-radius: 5px 0 0 5px;
    border: 1px solid #ced4da;
  }

  .input-group-text {
    border-radius: 0 5px 5px 0;
    border: 1px solid #ced4da;
    background-color: #fff;
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
.table thead th, .table tbody td {
	white-space: nowrap;
}


</style>
</head>
<body style="font-size: 12px; overflow-y: auto; font-family: Poppins;">
	<div class="row mx-1">
		<div class="col-md-12">
			<div class="row">
				<div class="col-md-12">
					<h5 class="heading">Alert Audit Logs</h5>
				</div>
			</div>
		</div>
	</div>

<div class="row">
  <div class="col-md-12">
    <div class="m-2">
      <div class="card">

<div class="row mt-3">
  <div class="col-md-12 text-center">
    <div class="input-group daterange-input-group">
      <input type="text" name="daterange" class="form-control daterange-input" value="" />
      <div class="input-group-append">
        <span class="input-group-text"><i class="fas fa-calendar-alt"></i></span>
      </div>
    </div>

  </div>
</div>

      
        <div class="row">
          <div class="col-md-12">
            <div id="chartdiv"></div>
          </div>
        </div>
       
      </div>
    </div>
  </div>
</div>


	<!-- Modal -->

	<div class="modal fade" id="tableModal" tabindex="-1"
		aria-labelledby="tableModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-xl">
			<div class="modal-content">

				<div class="modal-header">
					<h5 class="modal-title" id="tableModalLabel">Alert Audit Logs</h5>
					<button type="button" class="close" data-bs-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>

				<div class="modal-body">
			<table id="example" class="table table-striped table-bordered">
						<thead>
							<tr>
								<th>Branch Name</th>
								<th>Vendor Name</th>
								<th>Contact No.</th>
								<th>Branch Address</th>
								<th>Active Zones</th>
								<th>Date Time</th>
								<th>User Name</th>
								<th>Remarks</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="auditLog" items="${auditLogList.payload}">
								<tr>
									<td>${auditLog.branchCode}</td>
									<td>${auditLog.vendorName}</td>
									<td>${auditLog.mobile}</td>
									<td>${auditLog.address}</td>
									<td>${auditLog.activeZone}</td>
									<td>${auditLog.dateTime}</td>
									<td>${auditLog.userName}</td>
									<td class="remark-cell">${auditLog.remarks}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>


<script>
var reportTitle = "Alarm Audit Logs";
var aboutText = "ABOUT:  This report provides essential logs and insights related to system performance and events.";
</script>

	<script>


	var branchCode = sessionStorage.getItem('branch');


	am5.ready(function() {
	  // Create root element
	  var root = am5.Root.new("chartdiv");

	  // Set themes
	  root.setThemes([am5themes_Animated.new(root)]);

	  // Create chart
	  var chart = root.container.children.push(am5percent.PieChart.new(root, {
	    layout: root.verticalLayout,
	    innerRadius: am5.percent(50)
	  }));

	  // Create series
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

	  // Fetch data from API
	  fetch('./getAlaramAuditLogcount?remark=T&branchCode=' + branchCode)
	    .then(response => response.json())
	    .then(data => {
	      series.data.setAll([
	        { value: data.payload.testing, category: "Testing" },
	        { value: data.payload.acknowledge, category: "Acknowledged" },
	        { value: data.payload.venderInfo, category: "Vendor Informed" },
	        { value: data.payload.falseAlarms, category: "False Alarm" }
	      ]);

	      var legend = chart.children.push(am5.Legend.new(root, {
	        centerX: am5.percent(50),
	        x: am5.percent(50),
	        marginTop: 15,
	        marginBottom: 15
	      }));

	      legend.data.setAll(series.dataItems);

	      series.appear(1000, 100);

	      series.labels.template.setAll({
	        visible: false
	      });

	      series.dataItems.forEach(function (dataItem) {
	        dataItem.get("slice").events.on("click", function () {
	          var category = dataItem.get("category");

	          var filteredCategory = category;
	          if (category === "FALSE ALARM") filteredCategory = "False";
	          else if (category === "SECURITY VENDOR INFORMED") filteredCategory = "Vendor Informed";

	          filterTableByCategory(filteredCategory);
	          $('#tableModal').modal('show');
	        });
	      });

	    })
	    .catch(error => console.error('Error fetching data:', error));
	});

	function filterTableByCategory(category) {
	  var table = $('#example').DataTable();
	  table.search(category).draw();
	}

	document.addEventListener('DOMContentLoaded', function() {
	  var remarkCells = document.querySelectorAll('.remark-cell');
	  remarkCells.forEach(function(cell) {
	    var remarks = cell.innerHTML;
	    if (remarks) { // Add condition to check if remarks is not empty
	      switch (remarks) {
	        case 'TESTING':
	          cell.innerHTML = '<span class="testing">Testing</span>';
	          break;
	        case 'FALSE ALARM':
	          cell.innerHTML = '<span class="falseAlarm">False Alarm</span>';
	          break;
	        case 'ACKNOWLEDGED':
	          cell.innerHTML = '<span class="acknowledged">Acknowledged</span>';
	          break;
	        case 'SECURITY VENDOR INFORMED':
	          cell.innerHTML = '<span class="informedVendor">Vendor Informed</span>';
	          break;
	        default:
	          break;
	      }
	    }
	  });


	  $(document).ready(function() {
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

	      convertImageToBase64('./logo/digitals.png', function (base64Logo) {
	          var table = $('#example').DataTable({
	              paging: false,
	              scrollY: '45vh',
	              scrollX: true,
	              dom: 'Bfrtip',
	              buttons: [
	                  {
	                      extend: 'excelHtml5',
	                      title: function() {
	                          var now = new Date();
	                          var dateTime = now.toLocaleString();
	                          var pageTitle = document.title;
	                          return 'DI-HMS : ' + pageTitle + ' - ' + dateTime;
	                      },
	                      customize: function(xlsx) {
	                          var sheet = xlsx.xl.worksheets['sheet1.xml'];
	                          var styles = xlsx.xl['styles.xml'];
	                          var font = '<font><sz val="14"/><color rgb="FFFFFFFF"/><b/></font>';
	                          $(styles).find('fonts').append(font);
	                          var fontId = $(styles).find('fonts font').length - 1;
	                          var fill = '<fill><patternFill patternType="solid"><fgColor rgb="3674B5"/></patternFill></fill>';
	                          $(styles).find('fills').append(fill);
	                          var fillId = $(styles).find('fills fill').length - 1;
	                          var cellXfs = $(styles).find('cellXfs');
	                          var xf = '<xf xfId="0" applyFont="1" fontId="' + fontId + '" applyFill="1" fillId="' + fillId + '"/>';
	                          cellXfs.append(xf);
	                          var xfId = cellXfs.find('xf').length - 1;
	                          $('row c[r="A1"]', sheet).attr('s', xfId);

	                          // Add image to the Excel sheet
	                          var img = new Image();
	                          img.src = base64Logo;
	                          img.onload = function() {
	                              var canvas = document.createElement('canvas');
	                              canvas.width = img.width;
	                              canvas.height = img.height;
	                              var ctx = canvas.getContext('2d');
	                              ctx.drawImage(img, 0, 0);
	                              var dataURL = canvas.toDataURL('image/png');
	                              var image = '<image r:id="rId1" xlink:href="' + dataURL + '"/>';
	                              $(sheet).find('drawing').append(image);
	                          };
	                      }
	                  },
	                  {
	                      extend: 'pdfHtml5',
	                      text: 'PDF',
	                      orientation: 'landscape',
	                      pageSize: 'A4',
	                      title: function() {
	                          var now = new Date();
	                          var dateTime = now.toLocaleString();
	                          var pageTitle = document.title;
	                          return 'DI-HMS : ' + pageTitle + ' - ' + dateTime;
	                      },
	                      customize: function (doc) {
	                          doc.pageMargins = [20, 30, 20, 40];
	                          // Add logo and title together
	                          doc.content.splice(0, 0, {
	                              columns: [
	                                  {
	                                      image: base64Logo,
	                                      width: 90,
	                                      alignment: 'left',
	                                      margin: [0, 0, 10, 10]
	                                  },
	                                  {
	                                      text: reportTitle,  // Dynamic Title
	                                      fontSize: 16,
	                                      bold: true,
	                                      alignment: 'right',
	                                      margin: [0, 15, 0, 0]
	                                  }
	                              ]
	                          });
	                          // Add a horizontal line below title
	                          doc.content.splice(1, 0, {
	                              canvas: [{ type: 'line', x1: 0, y1: 0, x2: 535, y2: 0, lineWidth: 1 }]
	                          });
	                          // Add Dynamic About Section
	                          if (aboutText) {
	                              doc.content.splice(2, 0, {
	                                  text: aboutText,
	                                  fontSize: 10,
	                                  alignment: 'justify',
	                                  margin: [0, 10, 0, 20],
	                                  fillColor: '#f9f9f9',
	                                  border: [false, false, false, true] // Bottom border
	                              });
	                          }
	                          // Watermark
	                          doc.watermark = {
	                              text: 'Digitals India',
	                              color: 'gray',
	                              opacity: 0.1, // More transparent
	                              bold: true,
	                              italics: false
	                          };
	                          // Table Styling
	                          doc.styles.tableHeader = {
	                              bold: true,
	                              fontSize: 12,
	                              color: 'black',
	                              fillColor: '#e0e0e0',
	                              alignment: 'left',
	                              margin: [5, 5, 5, 5]  // Add padding (top, right, bottom, left)
	                          };
	                          doc.styles.tableBodyEven = {
	                              fontSize: 9,
	                              color: 'black',
	                              fillColor: '#f9f9f9'
	                          };
	                          doc.styles.tableBodyOdd = {
	                              fontSize: 9,
	                              color: 'black'
	                          };
	                          // Add footer with page number
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
	                  'copy', 'csv', 'print'
	              ],
	              order: [[5, 'desc']],
	              deferRender: true
	          });

	          $('input[name="daterange"]').daterangepicker({
	              opens: 'left',
	              ranges: {
	                  'Last 24 Hours': [moment().subtract(1, 'days'), moment()],
	                  'Last 30 Days': [moment().subtract(29, 'days'), moment()],
	                  'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
	                  'Current Month': [moment().startOf('month'), moment().endOf('month')],
	                  'All Data': [moment().subtract(50, 'years'), moment()]
	              }
	          }, function(start, end, label) {
	              // Show SweetAlert processing alert
	              Swal.fire({
	                  title: 'Processing',
	                  text: 'Fetching data, please wait...',
	                  allowOutsideClick: false,
	                  didOpen: () => {
	                      Swal.showLoading();
	                  }
	              });

	              // Make an AJAX call to fetch data based on the selected date range
	              $.ajax({
	                  url: './getAllRecordsByDateRange',
	                  method: 'GET',
	                  data: {
	                      startDate: start.format('YYYY-MM-DD'),
	                      endDate: end.format('YYYY-MM-DD')
	                  },
	                  success: function(response) {
	                      table.search('').draw(); // Clear existing search filter
	                      table.clear().draw(); // Clear existing data

	                      response.payload.forEach(function(record) {
	                          table.row.add([
	                              record.deviceId,
	                              record.vendorName,
	                              record.mobile,
	                              record.address,
	                              record.activeZone,
	                              record.dateTime,
	                              record.userName,
	                              '<span class="remark-cell">' + record.remarks + '</span>'
	                          ]).draw(false);
	                      });

	                      // Reapply remark-cell logic after DataTables renders the new data
	                      setTimeout(() => {
	                          $('.remark-cell').each(function() {
	                              var remarks = $(this).text().trim();
	                              if (remarks) {
	                                  switch (remarks) {
	                                      case 'TESTING':
	                                          $(this).html('<span class="testing">Testing</span>');
	                                          break;
	                                      case 'FALSE ALARM':
	                                          $(this).html('<span class="falseAlarm">False Alarm</span>');
	                                          break;
	                                      case 'ACKNOWLEDGED':
	                                          $(this).html('<span class="acknowledged">Acknowledged</span>');
	                                          break;
	                                      case 'SECURITY VENDOR INFORMED':
	                                          $(this).html('<span class="informedVendor">Vendor Informed</span>');
	                                          break;
	                                  }
	                              }
	                          });
	                      }, 100); // Delay to ensure DataTables renders the new rows

	                      // Show modal & fix overflow
	                      $('#tableModal').modal('show');
	                      $('.modal-body').css({ 'max-height': '80vh', 'overflow': 'auto' });

	                      // Adjust table columns for proper rendering
	                      table.columns.adjust().draw();

	                      // Close SweetAlert processing alert
	                      Swal.close();
	                  },
	                  error: function(error) {
	                      console.error('Error fetching data:', error);
	                      // Close SweetAlert processing alert
	                      Swal.close();
	                  }
	              });
	          });

	          function initializeDataTable() {
	              if (!$.fn.DataTable.isDataTable('#example')) {
	                  $('#example').DataTable({
	                      paging: false,
	                      scrollY: '45vh',
	                      scrollX: true,
	                      dom: 'Bfrtip',
	                      buttons: ['copy', 'csv', 'excel', 'pdf', 'print'],
	                      order: [[5, 'desc']],
	                      deferRender: true
	                  });
	              }
	          }

	          // Initialize DataTable when modal is shown
	          $('#tableModal').on('shown.bs.modal', function() {
	              initializeDataTable();
	              $('#example').DataTable().columns.adjust().draw();
	          });
	      });
	  });

	});
	</script>


	<script src="./js/navBarToggle.js"></script>
	<script src="./externalJS/bootstrap.bundle.min.js"></script>
</body>
</html>
