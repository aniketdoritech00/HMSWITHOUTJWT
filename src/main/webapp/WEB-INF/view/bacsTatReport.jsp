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
<title>BACS TAT Report</title>

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
<script src="./externalJS/jszip.min.js"></script>

</head>
<style>
    .status-label {
        display: inline-block;
        padding: 2px 10px;
        border-radius: 5px;
        font-weight: bold;
        font-size: 12px;
        background-color: white;
        border: 1px solid;
    }
    .status-online {
        border-color: #28a745; /* green */
        color: #28a745;
    }
    .status-offline {
        border-color: #dc3545; /* red */
        color: #dc3545;
    }
    .status-fault {
        border-color: #ffc107; 
        color: #ffc107;
    }
</style>

<body style="font-size: 12px; overflow-y: hidden;">
	<div class="row mx-1">
		<div class="col-md-12">
			<div class="row">
				<div class="col-md-12">
					<h5 class="heading">BACS TAT Report</h5>
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<div class="m-2">
			
				<button class ="btn btn-sm btn-primary mx-2">CCTV TAT Report</button>
	            <button class ="btn btn-sm btn-primary">Resolution Time</button>
				<div class="card mt-2">

					<table id="example" class="table table-striped table-bordered">
							<thead>
								<tr style="text-align:left;">
									<th>Branch Name</th>
									<th>Ifsc Code</th>
									<th>Alert Type</th>
									<th>Created Time</th>
									
									<th>TAT</th>
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
    var reportTitle = "BACS TAT Report";
    var aboutText = "ABOUT: BACS TAT Report provides clear insights into system performance by showing how consistently the system remains operational."
    </script>
<script>


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

$(document).ready(function () {
    const branchCode = sessionStorage.getItem("branch"); 
    const zoneType = "BACS";

    $.ajax({
        url: "getTATReport",
        type: "GET",
        data: {
            branchCode: branchCode,
            zoneType: zoneType
        },
        success: function (response) {
            if (response.statusCode === 200 && response.payload && response.payload.length > 0) {
                populateTable(response.payload);
                initializeDataTable(); 
            } else {
            	initializeDataTable(); 
            }
        },
        error: function () {
            $("#example tbody").html("<tr><td colspan='5'>Error fetching data</td></tr>");
        }
    });

    function populateTable(data) {
        const tbody = $("#example tbody");
        tbody.empty();

        data.forEach(function (item) {
            const row = "<tr>" +
                "<td>" + item.branchName + "</td>" +
                "<td>" + item.ifsc + "</td>" +
                "<td>" + item.zone + "</td>" +
                "<td>" + "Faulty since " + formatDateTime(item.tat) + "</td>" +
                "<td>" + (item.downtime || '-') + "</td>" +
                "</tr>";
            tbody.append(row);
        });
    }

    function formatDateTime(dateTimeStr) {
        if (!dateTimeStr) return "-";
        const date = new Date(dateTimeStr);
        return date.toLocaleString("en-IN", {
            year: "numeric", month: "short", day: "numeric",
            hour: "2-digit", minute: "2-digit", second: "2-digit"
        });
    }

	
    function initializeDataTable() {
        convertImageToBase64('./logo/digitals.png', function (base64Logo) {
            $('#example').DataTable({
                responsive: true,
                paging: false,
                scrollCollapse: true,
                scrollY: '45vh',
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
                            // Set table width to 100%
                            if (doc.content[doc.content.length - 1].table) {
                                doc.content[doc.content.length - 1].table.widths = '*';
                            }
                            // Apply conditional formatting

var table = doc.content[doc.content.length - 1].table.body;

for (var i = 1; i < table.length; i++) {
    for (var j = 0; j < table[i].length; j++) {
        var cell = table[i][j];

        if (typeof cell.text === 'string') {
            var percentage = parseInt(cell.text.replace('%', ''));

            if (cell.text.includes('Faulty since')) {
                cell.text = {
                    text: cell.text,
                    style: 'faultText',
                    padding: [2, 5, 2, 5],
                    borderRadius: 5
                };
            }  
        }
    }
}


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

                            doc.styles.faultText = {
                                background: '#fff3cd',
                                color: '#856404',
                                margin: [2, 5, 2, 5],
                                borderRadius: 5
                            };
                        }
                    },
                    'print'
                ],
                destroy: true
            });
        });
    }
});




</script>




	<script src="./js/navBarToggle.js"></script>
	<script src="./externalJS/bootstrap.bundle.min.js"></script>
	<script src="./js/activeDeviceLogDetails.js"></script>

</body>
</html>