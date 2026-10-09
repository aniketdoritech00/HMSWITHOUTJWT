<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="org.springframework.http.HttpStatus"%>
<%@ page import="com.doritech.api.Entity.*"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Alert Message Report</title>

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

</head>
<body style="font-size: 12px; overflow-y: hidden; font-family: Poppins;">
	<div class="row mx-1">
		<div class="col-md-12">
			<div class="row">
				<div class="col-md-12">
					<h5 class="heading">Alerts Message Report</h5>
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<div class="m-2">
				<div class="card">

<div class="d-flex">
					<div class="input-group rounded mt-2 col-4">
						<select class="input-11 form-control form-control-sm" id="branchCode" name="branchCode">
							<option id="branchCode" value="">--select--</option>
							<c:forEach var="branch" items="${branchList.payload}">
								<option id="branchCode" value="${branch.branchCode}">${branch.branchDesc}
									-> ${branch.branchCode}</option>
							</c:forEach>
						</select>
						<button class="input-group-text border-0 ml-2" id="filterButton"
							style="background-color: #028bd7; color: #ffff; height: 32px;">
							<i class="fa fa-search" aria-hidden="true"></i>
						</button>

						

					</div>
					
          <div class="col-8">
					<button onclick="printFilteredTable()" class="btn btn-sm btn-primary mt-2" style="background-color: #028bd7; color: #ffff; height: 32px;">Print Filtered Data</button>
					<button onclick="downloadFilteredTable()" class="btn btn-sm btn-success mt-2 mx-2" style="background-color: #FC8F54; color: #ffff; height: 32px;">Download Filtered Data</button>
					</div>
</div>
					<table id="example" class="table table-striped table-bordered">
						<thead>
							<tr>
								<th>Sr No.</th>
								<th>Branch Name</th>
								<th>Branch Code</th>
								<th>Bank Name</th>
								<th>Panel Serial No</th>
								<th>Sync DateTime</th>
								<th>Message</th>
								

							</tr>
						</thead>
						<tbody>
							<c:forEach var="input" items="${AlertsMessage}">
								<tr>
									<td>${input.id}</td>
									<td>${input.branchDesc}</td>
									<td>${input.branchCode}</td>
									<td>${input.bankName}</td>
									<td>${input.panelSerialNumber}</td>
									<td>${input.dateTime}</td>
									<td>${input.alertMessage}</td>
									
								</tr>
							</c:forEach>

						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>



	<script>
    // Filter the table
    document.getElementById('filterButton').addEventListener('click', function () {
        const selectedBranchCode = document.getElementById('branchCode').value;
        const tableRows = document.querySelectorAll('#example tbody tr');

        tableRows.forEach(row => {
            const branchCodeCell = row.querySelector('td:nth-child(3)'); // Branch Code column
            if (selectedBranchCode === "" || branchCodeCell.textContent.trim() === selectedBranchCode) {
                row.style.display = ""; // Show the row
            } else {
                row.style.display = "none"; // Hide the row
            }
        });
    });

    // Print the filtered table
   function printFilteredTable() {
    const table = document.getElementById('example');
    const clonedTable = table.cloneNode(true); // Clone table to avoid affecting original
    const rows = clonedTable.querySelectorAll('tbody tr');

    // Hide rows not visible in the original table
    rows.forEach(row => {
        if (row.style.display === 'none') {
            row.remove();
        }
    });

    // Create a temporary print area
    const printWindow = window.open('', '', 'width=800,height=1200');
    printWindow.document.write('<html><head><title>Print Table</title></head><body>');
    printWindow.document.write('<style>');
    printWindow.document.write(`
        table { 
            width: 80%; 
            border-collapse: collapse; 
            font-size: 16px; /* Increase font size */
        } 
        th, td { 
            border: 1px solid #ddd; 
            padding: 10px; 
            text-align: left; 
        }
        th { 
            font-size: 18px; /* Larger font size for headers */
            font-weight: bold;
        }
    `);
    printWindow.document.write('</style>');
    printWindow.document.write(clonedTable.outerHTML);
    printWindow.document.write('</body></html>');
    printWindow.document.close();
    printWindow.print();
}


    // Download filtered table as CSV
    function downloadFilteredTable() {
        const table = document.getElementById('example');
        let csvContent = '';

        // Get table headers
        const headers = table.querySelectorAll('thead th');
        csvContent += Array.from(headers).map(th => th.textContent).join(',') + '\n';

        // Get filtered rows
        const rows = table.querySelectorAll('tbody tr');
        rows.forEach(row => {
            if (row.style.display !== 'none') { // Include only visible rows
                const cells = row.querySelectorAll('td');
                csvContent += Array.from(cells).map(td => td.textContent).join(',') + '\n';
            }
        });

        // Create a blob and download
        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'filtered_table.csv';
        a.style.display = 'none';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
    }
</script>


	<script src="./js/navBarToggle.js"></script>
	<script src="./externalJS/bootstrap.bundle.min.js"></script>
	<script src="./js/activeDeviceLogDetails.js"></script>
	<script src="./js/dateAndTime.js"></script>
	<script src="./externalJS/multilingual.js"></script>
</body>
</html>