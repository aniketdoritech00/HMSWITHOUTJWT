<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.util.List"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.time.LocalDate"%>
<%@ page import="org.springframework.http.HttpStatus"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>DI-HMS</title>

<link href="./dashboardCSS/style.css" rel="stylesheet">
<script src="./externalJS/fontawesome.js"></script>
<link href="./fonts/poppins.css" rel="stylesheet">
<link href="./externalCSS/select2.min.css" rel="stylesheet" />
<link href="./css/hmsDashboard.css" rel="stylesheet" />
<link rel="stylesheet"
	href="./externalCSS/responsive.bootstrap4.min.css">
<script src="./externalJS/sweetalert2.all.min.js"></script>
<script src="./externalJS/jquery-3.7.0.js"></script>
<script src="https://cdn.amcharts.com/lib/5/index.js"></script>
<script src="https://cdn.amcharts.com/lib/5/percent.js"></script>
<script src="https://cdn.amcharts.com/lib/5/themes/Animated.js"></script>
<script
	src="https://cdn.jsdelivr.net/npm/html2canvas@1.4.1/dist/html2canvas.min.js"></script>

<style>
.card-bodyPie {
	display: flex;
	justify-content: space-around;
	align-items: center;
	padding: 10px;
}

.chart-container {
	flex: 1;
	padding: 5px;
}

canvas {
	max-width: 100%;
	max-height: 100%;
}

.chart-title {
	margin-top: 5px;
	text-align: center;
	font-size: 14px;
	color: black;
}

.legend-container {
	display: flex;
	justify-content: center;
	margin-bottom: 10px;
}

.legend-item {
	display: flex;
	align-items: center;
	margin: 0 10px;
}

.legend-color {
	width: 15px;
	height: 15px;
	margin-right: 5px;
	border-radius: 50%;
}

#chartdiv, #chartdiv1, #chartdiv2 {
	width: 100%;
	height: 400px;
}
</style>
</head>
<body style="font-family: Poppins;">
	<input id="userId" value="${userId}" style="display: none;">
	<input id="loggedInBranch" value="${branchId}" style="display: none;">
	<input id="remarksList" value="${remarksList.payload}"
		style="display: none;">
	<div class="container-fluid " id="accordion">
	<button class="btn btn-primary ignoreInScreenShot m-3" onclick="convertPageToImage()">Convert Page to Image</button>
		<!-- Row 2: Status of Connecting Devices -->
		<div class="row d-flex align-items-stretch">
			<div class="card flex-grow-1 mx-4 mt-2">
				<div class="row mt-0">
					<div class="col-12">
						<h5 class="heading">Status of Connecting Devices</h5>
					</div>
				</div>
				<div class="card-body py-2">
					<!-- Legends -->
					<div class="legend-container">
						<div class="legend-item">
							<div class="legend-color" style="background-color: #5DB996;"></div>
							<span class="lColor">Working</span>
						</div>
						<div class="legend-item">
							<div class="legend-color" style="background-color: #ECE852;"></div>
							<span class="lColor">Partially Working</span>
						</div>
						<div class="legend-item">
							<div class="legend-color" style="background-color: #FB4141;"></div>
							<span class="lColor">Not Working</span>
						</div>
					</div>
					<!-- Doughnut Charts -->
					<div class="row">
						<div class="col-lg-1 mb-2"></div>
						<div class="col-lg-2 mb-2" id="cctv-chart-container">
							<canvas id="cctvChart" onclick="toggleCollapseAndScroll();"></canvas>
						</div>
						<div class="col-lg-2 mb-2" id="security-alarm-chart-container">
							<canvas id="securityAlarmChart"
								onclick="toggleCollapseAndScroll();"></canvas>
						</div>
						<div class="col-lg-2 mb-2" id="fire-alarm-chart-container">
							<canvas id="fireAlarmChart" onclick="toggleCollapseAndScroll();"></canvas>
						</div>
						<div class="col-lg-2 mb-2" id="bacs-chart-container">
							<canvas id="bacsChart" onclick="toggleCollapseAndScroll();"></canvas>
						</div>
						<div class="col-lg-2 mb-2" id="etl-chart-container">
							<canvas id="etlChart" onclick="toggleCollapseAndScroll();"></canvas>
						</div>
						<div class="col-lg-1 mb-2"></div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="col-lg-12 col-md-12" id="statusTable"
		style="display: none;">
		<div class="">
			<div class="row" id="inputFilter" onchange="filterInputs(this.value)">
			</div>
			<div class="card-header">
				<div class="table-responsive table-striped table-bordered mb-2"
					style="max-height: 250px; overflow-y: auto; font-size: 12px;">
					<table class="table mb-0">
						<thead
							style="color: #fff; position: sticky; top: 0; z-index: 1; background: #028bd7; text-align: center;">
							<tr>
								<th>Device Name</th>
								<th>Status</th>
								<th>Alert Type</th>
								<th>Status</th>
							</tr>
						</thead>
						<tbody id="activeZonesBodyOne"
							style="color: black; text-align: center; font-size: 14px;"></tbody>
					</table>
				</div>
			</div>
		</div>
	</div>

	<div class="row mx-2 d-none">
		<div class=" col-lg-12 col-md-12 col-sm-12">
			<div class="card" style="">
				<div class="row">
					<div class="col-md-12">
						<h5 class="heading">Recent Activities</h5>
					</div>
				</div>
				<div class="container">
					<div class="col-lg-12">
						<div class="row">
							<div class="row col-lg-12">
								<div class="col-lg-2 col-md-2 col-sm-6 mt-1">
									<label for="dateFilter"
										style="font-weight: 600; color: #028bd7"> Start Date</label>
								</div>
								<div class="col-lg-2 col-md-2 col-sm-6 mt-1">
									<input
										class="form-control form-control-sm rounded typeahead stylish-input"
										type="date" id="startDate" value="">
								</div>
								<div class="col-lg-2 col-md-2 col-sm-6 mt-1">
									<label for="dateFilter"
										style="font-weight: 600; color: #028bd7"> End Date</label>
								</div>
								<div class="col-lg-2 col-md-2 col-sm-6 mt-1">
									<input
										class="form-control form-control-sm rounded typeahead stylish-input"
										type="date" id="endDate" value="<%=LocalDate.now()%>">
								</div>
								<div class="col-lg-2 col-sm-6 col-md-2 mt-1">
									<label style="font-weight: 600; color: #028bd7">Select
										Device</label>
								</div>
								<div class="col-lg-2 col-sm-6 col-md-2">
									<div class="btn-group">
										<button type="button"
											class="btn btn-primary dropdown-toggle active-device w-100 btn-block"
											data-toggle="dropdown" aria-haspopup="true"
											aria-expanded="false">Select Device</button>
										<div class="dropdown-menu deviceFilter"
											style="cursor: pointer; color: #1d1d1d;">
											<a class="dropdown-item activeZoneFilter">SECURITY ALARM</a>
											<a class="dropdown-item activeZoneFilter">FIRE ALARM</a> <a
												class="dropdown-item activeZoneFilter">CCTV</a>
										</div>
									</div>
								</div>
							</div>
							<div class="row col-lg-12 col-sm-12 col-md-12 mt-2">
								<div class="col-lg-2 col-sm-6 mt-1">
									<label for="monthFilter"
										style="font-weight: 600; color: #028bd7; display: none;">Select
										Month</label>
								</div>
								<div class="col-lg-2  col-md-2 col-sm-6 mt-1">
									<input
										class="form-control form-control-sm rounded typeahead stylish-input"
										type="month" id="monthFilter" style="display: none;">
								</div>
								<div class="col-lg-2 col-sm-2 col-md-2 mt-1">
									<label style="font-weight: 600; color: #028bd7; display: none;">Select
										Year</label>
								</div>
								<div class="col-lg-4 col-sm-4 col-md-4">
									<div class="btn-group">
										<button type="button"
											class="btn btn-primary dropdown-toggle year-dropdown w-100 btn-block"
											data-toggle="dropdown" aria-haspopup="true"
											aria-expanded="false" style="display: none;">Select
											Year</button>
										<div class="dropdown-menu yearFilter"
											style="cursor: pointer; color: #1d1d1d;"></div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="card-body">
					<div class="row">
						<div class="col-lg-4 col-md-4 col-sm-12">
							<canvas id="sold-product"></canvas>
						</div>
						<div class="col-lg-8 col-md-8 col-sm-12 mx-0">
							<div class="table-responsive"
								style="max-height: 220px; overflow-y: auto;">
								<!-- <table id="auditTable" class="table table-bordered" style="color: #003d87;">
                                    <thead style="color: #fff; position: sticky; top: 0; z-index: 1; background: #028bd7;">
                                        <tr>
                                            <th class="text-left col-3">Service Provider</th>
                                            <th class="text-left col-2">Alert Zones</th>
                                            <th class="text-left col-3">Zone</th>
                                            <th style="text-align: right;" class="col-3">Alert Time Stamp</th>
                                            <th class="text-left">Status</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                    </tbody>
                                </table> -->
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="row mx-2">
		<div class="col-lg-4 col-md-6 col-sm-12">
			<div class="card">
				<div class="row">
					<div class="col-md-12">
						<h5 class="heading">Recent Alerts (LAST 30 DAYS)</h5>
					</div>
				</div>
				<div class="container">
					<div class="col-lg-12">
						<div class="row align-items-center">
							<div class="col-12">
								<p class="card-title mb-0" style="font-size: 18px;">
									<i class="fa-regular fa-bell fa-lg"></i> Recent Alerts: <span
										id="total"></span>
								</p>
							</div>
						</div>
					</div>
				</div>
				<div class="card-body">
					<div class="row">
						<div class="col-lg-12  col-sm-12">
							<div id="chartdiv"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
				
		
			<div class="col-lg-4 col-md-6 col-sm-12">
			<div class="card">
				<div class="row">
					<div class="col-md-12">
						<h5 class="heading">Alert Report</h5>
					</div>
				</div>
				
					<div class="container">
					<div class="col-lg-12">
						<div class="row align-items-center">
							<div class="col-12">
								<p class="card-title mb-0" style="font-size: 18px;">
									<i class="fa-regular fa-bell fa-lg"></i> Alerts Log: <span
										id="totalAlerts"></span>
								</p>
							</div>
						</div>
					</div>
				</div>
				
				<div class="card-body">
			 <div class="row">
          <div class="col-md-12">
            <div id="chartdiv1"></div>
          </div>
        </div>
				</div>
			</div>
		</div>
		
			<div class="col-lg-4 col-md-6 col-sm-12">
			<div class="card">
				<div class="row">
					<div class="col-md-12">
						<h5 class="heading">Resolved Alerts</h5>
					</div>
				</div>
				
					<div class="container">
					<div class="col-lg-12">
						<div class="row align-items-center">
							<div class="col-12">
								<p class="card-title mb-0" style="font-size: 18px;">
									<i class="fa-regular fa-bell fa-lg"></i> Alerts Log: <span
										id="totalResolved"></span>
								</p>
							</div>
						</div>
					</div>
				</div>
				
				<div class="card-body">
			 <div class="row">
          <div class="col-md-12">
            <div id="chartdiv2"></div>
          </div>
        </div>
				</div>
			</div>
		</div>
	</div>
	

	<div class="footer">
		<div class="copyright">
			<p>
				Copyright © Designed &amp; Developed by <a href="#" target="_blank">Doritech</a>
				2024
			</p>
		</div>
	</div>

	

	<script type="text/javascript"
		src="https://cdn.jsdelivr.net/momentjs/latest/moment.min.js"></script>
	<script type="text/javascript"
		src="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.min.js"></script>

	<script>
	
	
var branchCode = sessionStorage.getItem('branch');



/*------------------------------------------------------------STATUS OF RESOLVED DEVICES-----------------------------------------------------------------------*/


am5.ready(function() {
    var root2 = am5.Root.new("chartdiv2");
    root2.setThemes([am5themes_Animated.new(root2)]);
    var chart2 = root2.container.children.push(am5percent.PieChart.new(root2, {
        layout: root2.verticalLayout,
        innerRadius: am5.percent(50)
    }));
    var series2 = chart2.series.push(am5percent.PieSeries.new(root2, {
        valueField: "value",
        categoryField: "category",
        alignLabels: false
    }));
    series2.labels.template.setAll({
        textType: "circular",
        centerX: 0,
        centerY: 0
    });
    series2.labels.template.setAll({
        visible: false
    });

    fetch('./getInputStatusDetails?branchCodeRequest=' + branchCode)
        .then(response => response.json())
        .then(data => {
            const chartData = Object.keys(data.payload.alarmTypeCounts)
                .filter(key => key) // Remove "Unknown" category
                .map(key => ({
                    category: key,
                    value: data.payload.alarmTypeCounts[key]
                }));

            series2.data.setAll(chartData);

            var totalCount = chartData.reduce((sum, item) => sum + item.value, 0);
            document.getElementById("totalResolved").innerText = totalCount;

            var legend2 = chart2.children.push(am5.Legend.new(root2, {
                centerX: am5.percent(50),
                x: am5.percent(50),
                marginTop: 15,
                marginBottom: 15
            }));
            legend2.data.setAll(series2.dataItems);

            series2.appear(1000, 100);
        })
        .catch(error => console.error('Error fetching data:', error));
});

/*------------------------------------------------------------ALERT LOGS-----------------------------------------------------------------------*/

am5.ready(function() {
  
  var root1 = am5.Root.new("chartdiv1");

  // Set themes
  root1.setThemes([am5themes_Animated.new(root1)]);

  // Create chart
  var chart1 = root1.container.children.push(am5percent.PieChart.new(root1, {
    layout: root1.verticalLayout,
    innerRadius: am5.percent(50)
  }));

  // Create series
  var series1 = chart1.series.push(am5percent.PieSeries.new(root1, {
    valueField: "value",
    categoryField: "category",
    alignLabels: false
  }));

  series1.labels.template.setAll({
    textType: "circular",
    centerX: 0,
    centerY: 0
  });

  // Fetch data from API
  fetch('./getAlaramAuditLogcount?remark=&branchCode=' + branchCode)
    .then(response => response.json())
    .then(data => {
      const totalAlerts = data.payload.testing + data.payload.acknowledge + data.payload.venderInfo + data.payload.falseAlarms;
      document.getElementById("totalAlerts").innerText = totalAlerts;

      series1.data.setAll([
        { value: data.payload.testing, category: "Testing" },
        { value: data.payload.acknowledge, category: "Acknowledged" },
        { value: data.payload.venderInfo, category: "Vendor Informed" },
        { value: data.payload.falseAlarms, category: "False Alarm" }
      ]);

      var legend1 = chart1.children.push(am5.Legend.new(root1, {
        centerX: am5.percent(50),
        x: am5.percent(50),
        marginTop: 15,
        marginBottom: 15
      }));

      legend1.data.setAll(series1.dataItems);

      series1.appear(1000, 100);

      series1.labels.template.setAll({
        visible: false
      });

      series1.dataItems.forEach(function (dataItem) {
        dataItem.get("slice").events.on("click", function () {
          var category = dataItem.get("category");

          var filteredCategory = category;
          if (category === "FALSE ALARM") filteredCategory = "False";
          else if (category === "SECURITY VENDOR INFORMED") filteredCategory = "Vendor Informed";
        });
      });

    })
    .catch(error => console.error('Error fetching data:', error));
});


	
	/*-----------------------------------------DASHBOARD CHARTS------------------------------------------------------------------ */
	
        document.addEventListener("DOMContentLoaded", function () {
            var startDate = moment().subtract(30, 'days').startOf('day');
            var endDate = moment().endOf('day');
            var startTime = null;
            var endTime = null;
            var chart = null;
            var root = null;

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
                    url: "/getStatusCountByDateRange",
                    method: 'GET',
                    data: requestData,
                    success: function (data) {
                        if (data.statusCode === 200) {
                            updateChart(data.payload, startTime, endTime);
                            updateTotalAlerts(data.payload);
                        } else {
                            console.error("Failed to fetch data", data.message);
                            alert("Error fetching data");
                        }
                    },
                    error: function (xhr, status, error) {
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

                document.getElementById("chartdiv").style.display = 'block';
            }

            function updateTotalAlerts(statusData) {
                let totalAlerts = statusData.reduce((total, item) => total + item.count, 0);
                document.getElementById("total").innerText = totalAlerts;
            }

            fetchStatusData(startDate, endDate, startTime, endTime);
        });




        function convertPageToImage() {
            // Hide elements with the class 'ignoreInScreenShot'
            const elementsToHide = document.querySelectorAll('.ignoreInScreenShot');
            elementsToHide.forEach(element => {
                element.style.display = 'none';
            });

            // Take the screenshot
            html2canvas(document.body).then(canvas => {
                canvas.toBlob(blob => {
                    const formData = new FormData();
                    formData.append('attachments', blob, 'screenshot.png');

                    // Send the screenshot to the backend API
                    fetch('/sendEmailWithAttachment', {
                        method: 'POST',
                        body: formData
                    })
                    .then(response => response.json())
                    .then(data => {
                        console.log('Email sent successfully:', data);
                    })
                    .catch(error => {
                        console.error('Error sending email:', error);
                    });

                    // Show the elements again
                    elementsToHide.forEach(element => {
                        element.style.display = '';
                    });
                }, 'image/png');
            });
        }


    </script>

	<script src="./js/hmsDashboard.js"></script>
	<script src="./externalJS/chart.js"></script>
	<script src="./vendor/chart.js/chart.min.js"></script>
	<script src="./vendor/chartist/chartist.min.js"></script>
	<script src="./externalJS/bootstrap.bundle.min.js"></script>
</body>
</html>
