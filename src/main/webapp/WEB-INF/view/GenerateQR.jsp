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
<title>Generate QR</title>

<link rel="stylesheet" href="./css/deviceLogDetails.css" />
<link rel="stylesheet" href="./css/multilingual.css" />
<link href="./fonts/poppins.css" rel="stylesheet">
<link rel="stylesheet" href="./externalCSS/bootstrap.min.css">
<script src="./externalJS/fontawesome.js"></script>

<!-- DATATABLES CSS Links -->
<link rel="stylesheet" href="./externalCSS/dataTables.bootstrap4.min.css">
<link rel="stylesheet" href="./externalCSS/buttons.dataTables.min.css">
<link rel="stylesheet" href="./externalCSS/responsive.bootstrap4.min.css">

</head>
<body style="font-size: 12px; overflow-y: hidden; font-family:Poppins;">
    <div class="row mx-1">
        <div class="col-md-12">
            <div class="row">
                <div class="col-md-12">
                     <h5 class="heading">Generate QR code</h5> 
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <div class="m-2">
                <div class="card">
                    <table id="example" class="table table-striped table-bordered">
                        <thead>
                            <tr>
                                <th>Branch Code</th>
                                <th>Branch Name</th>
                                <th>Bank Name</th>
                                <th>IFSC Code</th>
                                <th>Generate QR</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="branch" items="${branches.payload}">
                                <tr>
                                    <td>${branch.branchCode}</td>
                                    <td>${branch.branchDesc}</td>
                                    <td>${branch.bank}</td>
                                    <td>${branch.ifsc}</td>
                                    <td><button class="btn btn-sm btn-info status-btn"
                                            onclick="generateQR('${branch.branchCode}', '${branch.branchDesc}', '${branch.bank}', '${branch.ifsc}')">Generate QR</button></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal -->
    <div class="modal fade" id="qrCodeModal" tabindex="-1" role="dialog" aria-labelledby="qrCodeModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="qrCodeModalLabel">QR Code</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body text-center">
                    <img id="qrCodeImage" src="" alt="QR Code" class="img-fluid"/>
                    <p id="branchName" class="mt-3"></p>
                    <p id="bankName"></p>
                    <p id="ifscCode"></p>
                </div>
                <div class="modal-footer">
                    
                      <button type="button" class="btn btn-primary" onclick="printQRModal()">Print</button>
                </div>
            </div>
        </div>
    </div>

    <script>
        function generateQR(branchCode, branchDesc, bank, ifsc) {
            var qrCodeUrl = "./generateQRCode?url=http://122.160.60.151:9090/HMS/RegistrationForm&branchCode=" + branchCode;
            document.getElementById('qrCodeImage').src = qrCodeUrl;
            document.getElementById('branchName').innerHTML = "<strong>Branch Name:</strong> " + branchDesc ;
            document.getElementById('bankName').innerHTML = "<strong>Bank Name:</strong> " + bank ;
            document.getElementById('ifscCode').innerHTML = "<strong>IFSC Code: </strong>" + ifsc;
            $('#qrCodeModal').modal('show');
        }
        
        function printQRModal() {
            var printContents = document.querySelector('#qrCodeModal .modal-content').innerHTML;
            var originalContents = document.body.innerHTML;

            document.body.innerHTML = printContents;
            window.print();
            document.body.innerHTML = originalContents;

            $('#qrCodeModal').modal('show');
        }
    </script>
    

    <!-- jQuery -->
    <script src="./externalJS/jquery-3.5.1.min.js"></script>
    <!-- Bootstrap JS -->
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.bundle.min.js"></script>

    <!-- Other JavaScript files -->
    <script src="./externalJS/sweetalert2.all.min.js"></script>
    <script src="./externalJS/jquery.dataTables.min.js"></script>
    <script src="./externalJS/dataTables.bootstrap4.min.js"></script>
    <script src="./externalJS/responsive.bootstrap4.min.js"></script>
    <script src="./externalJS/dataTables.responsive.min.js"></script>
    <script src="./externalJS/dataTables.buttons.min.js"></script>
    <script src="./externalJS/pdfmake.min.js"></script>
    <script src="./externalJS/vfs_fonts.js"></script>
    <script src="./externalJS/buttons.html5.min.js"></script>
    <script src="./externalJS/buttons.print.min.js"></script>
    <script src="./js/navBarToggle.js"></script>
    <script src="./js/activeDeviceLogDetails.js"></script>
    <script src="./js/tmsDataTables.js"></script>
    <script src="./externalJS/multilingual.js"></script>
    <script src="./js/googleTranslateElementInit.js"></script>
</body>
</html>
