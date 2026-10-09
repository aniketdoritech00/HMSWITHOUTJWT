<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Report Status</title>

<link rel="stylesheet" href="./css/deviceLogDetails.css">
<link rel="stylesheet" href="./externalCSS/bootstrap.min.css">
<link rel="stylesheet"
	href="./externalCSS/dataTables.bootstrap4.min.css">
<link rel="stylesheet" href="./externalCSS/buttons.dataTables.min.css">
<link rel="stylesheet"
	href="./externalCSS/responsive.bootstrap4.min.css">

<style>
input[type] {
	color: white;
}
</style>

</head>

<body style="font-size: 12px; overflow-y: hidden;">
	<div class="row mx-1">
		<div class="col-md-12">
			<div class="row">
				<div class="col-md-12">
					<h5 class="heading">Report Details</h5>
				</div>
			</div>
		</div>
	</div>
	<div class="row button-container" style="padding-left: 19px;">
		<div class="col-md-12 col-sm-12 row">
			<input type="date" id="dailyPicker"
				class="btn btn-sm col-md-2 col-sm-12 m-1"
				style="background-color: #028bd7; color: #ffff;"> <input
				type="month" id="monthlyPicker"
				class="btn btn-sm col-md-2 col-sm-12 m-1"
				style="background-color: #028bd7; color: #ffff;">
			<button id="weeklyBtn" class="btn btn-sm col-md-2 col-sm-12 m-1"
				style="background-color: #028bd7; color: #ffff;">Weekly
				Basis</button>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<div class="m-2">
				<div class="card">
					<div class="table-responsive">
						<table id="example"
							class="table table-striped table-bordered display nowrap">
							<thead id="tableHead">
								<tr>
									<th>S.N.</th>
									<th>Branch</th>
									<th>Zonal Office</th>
									<th>Parent</th>
									<th>Vendor Name</th>
									<th>Vendor Contact No.</th>
									<th>Device</th>
									<th>Input Desired Name</th>
									<th>Status</th>
									<th>Make & Model</th>
									<th>Installation</th>
									<th>Life Cycle / Age</th>
								</tr>
							</thead>
							<tbody id="inputListTableBody">
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
	<script>
document.addEventListener('DOMContentLoaded', function () {
    fetch('/sendReportForMonthlyAlertReports')
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById('inputListTableBody');
            tableBody.innerHTML = ''; // Clear any existing rows

            data.forEach((report, index) => {
                report.deviceZoneEntity.forEach((device, deviceIndex) => {
                    const row = document.createElement('tr');

                    // Add cells to the row
                    row.innerHTML = `
                        <td>${index + 1}</td>
                        <td>${report.branchName}</td>
                        <td>${report.zonalOffice}</td>
                        <td>${report.parentBranchName}</td>
                        <td>${report.vendorName}</td>
                        <td>${report.vendorEmail}</td>
                        <td>${device.deviceId}</td>
                        <td>${device.zoneDesiredName}</td>
                        <td>${report.status ? report.status[deviceIndex] : 'N/A'}</td>
                        <td>${report.makeAndModel || 'N/A'}</td>
                        <td>${report.installation || 'N/A'}</td>
                        <td>${report.lifeCycle || 'N/A'}</td>
                    `;

                    // Append the row to the table body
                    tableBody.appendChild(row);
                });
            });
        })
        .catch(error => {
            console.error('Error fetching data:', error);
        });
});
</script>

	<script src="./externalJS/fontawesome.js"></script>
	<script src="./externalJS/jquery-3.7.0.js"></script>
	<script src="./externalJS/jquery.dataTables.min.js"></script>
	<script src="./externalJS/dataTables.bootstrap4.min.js"></script>
	<script src="./externalJS/dataTables.responsive.min.js"></script>
	<script src="./externalJS/dataTables.buttons.min.js"></script>
	<script src="./externalJS/pdfmake.min.js"></script>
	<script src="./externalJS/vfs_fonts.js"></script>
	<script src="./externalJS/buttons.html5.min.js"></script>
	<script src="./externalJS/buttons.print.min.js"></script>
	<script src="./externalJS/sweetalert2.all.min.js"></script>



	<script>
        function addDays(date, days) {
            let result = new Date(date);
            result.setDate(result.getDate() + days);
            return result;
        }

        function formatDate(date) {
            let d = new Date(date);
            let month = '' + (d.getMonth() + 1);
            let day = '' + d.getDate();
            let year = d.getFullYear();

            if (month.length < 2) month = '0' + month;
            if (day.length < 2) day = '0' + day;

            return [year, month, day].join('-');
        }

        $(document).ready(function() {
            let table = $('#example').DataTable({
                scrollX: true,
                scrollCollapse: true,
                scrollY: '45vh',
                autoWidth: false,
                fixedColumns: {
                    leftColumns: 1
                },
                dom: 'Bfrtip',
                buttons: [
                    'copy', 'csv', 'pdf', 'print'
                ],
                responsive: true,
                paging: false
            });
    

            
     
            $(document).ready(function() {
               
                let currentDate = new Date();
                let formattedDate = currentDate.toISOString().slice(0, 10);
                $('#dailyPicker').val(formattedDate);

               
                let currentMonth = currentDate.getFullYear() + '-' + ('0' + (currentDate.getMonth() + 1)).slice(-2);
                $('#monthlyPicker').val(currentMonth);
            });
 


            $('#dailyPicker').on('change', function() {
                let selectedDate = $(this).val();
                if (selectedDate) {
                    updateTableForDate(new Date(selectedDate));
                }
            });

            $('#monthlyPicker').on('change', function() {
                let selectedMonth = $(this).val();
                if (selectedMonth) {
                    updateTableForMonth(selectedMonth);
                }
            });

            $('#weeklyBtn').on('click', function() {
                updateTableForDays(7);
            });

            function updateTableForDate(date) {
                updateTable(1, date);
            }

            function updateTableForMonth(month) {
                let [year, monthIndex] = month.split('-');
                let daysInMonth = new Date(year, monthIndex, 0).getDate();
                updateTable(daysInMonth, new Date(year, monthIndex - 1, 1));
            }

            function updateTableForDays(days) {
                updateTable(days, new Date());
            }

            function updateTable(days, startDate) {
           
                table.destroy();

                let tableHead = document.getElementById('tableHead').rows[0];

              
                while (tableHead.cells.length > 12) {
                    tableHead.deleteCell(12);
                }

                for (let i = 0; i < days; i++) {
                    let newHeader = document.createElement('th');
                    newHeader.innerText = formatDate(addDays(startDate, i));
                    tableHead.appendChild(newHeader);
                }

               
                let tableBody = document.getElementById('inputListTableBody');
                for (let row of tableBody.rows) {
                    while (row.cells.length > 12) {
                        row.deleteCell(12);
                    }
                    for (let i = 0; i < days; i++) {
                        let newCell = document.createElement('td');
                        newCell.innerText = '';  
                        row.appendChild(newCell);
                    }
                }

           
                table = $('#example').DataTable({
                    scrollX: true,
                    scrollCollapse: true,
                    paging: false,
                    autoWidth: false,
                    fixedColumns: {
                        leftColumns: 1
                    },
                    dom: 'Bfrtip',
                    buttons: [
                        'copy', 'csv', 'pdf', 'print'
                    ]
                });
            }
        });
    </script>

	<script src="./js/navBarToggle.js"></script>
	<script src="./externalJS/bootstrap.bundle.min.js"></script>
	<script src="./js/activeDeviceLogDetails.js"></script>
	<script src="./externalJS/multilingual.js"></script>
	<script src="./js/googleTranslateElementInit.js"></script>
</body>
</html>
