<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Complaint Report</title>

<link rel="stylesheet" href="./css/deviceLogDetails.css" />
<link rel="stylesheet" href="./css/multilingual.css" />
<link rel="stylesheet" href="./externalCSS/bootstrap.min.css">
<link href="./fonts/poppins.css" rel="stylesheet">
<script src="./externalJS/fontawesome.js"></script>

<!-- DATATABLES CSS Links -->
<link rel="stylesheet" href="./externalCSS/dataTables.bootstrap4.min.css">
<link rel="stylesheet" href="./externalCSS/buttons.dataTables.min.css">
<link rel="stylesheet" href="./externalCSS/responsive.bootstrap4.min.css">

<!-- jQuery -->
<script src="./externalJS/jquery-3.5.1.min.js"></script>
<!-- Bootstrap JS -->
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.bundle.min.js"></script>

<style>
    /* Add your custom styles here */
    .image-icon i {
        color: blue; /* Default icon color */
        transition: transform 0.3s ease, color 0.3s ease;
    }

    .image-icon:hover i {
        color: #68CD23; /* Change icon color on hover */
        transform: scale(1.5); /* Increase size on hover */
    }
</style>

</head>
<body style="font-size: 12px; overflow-y: hidden; font-family:Poppins;">
    <div class="row mx-1">
        <div class="col-md-12">
            <div class="row">
                <div class="col-md-12">
                    <h5 class="heading">Complaint Reports</h5>
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
                            <th>Complaint Id</th>
                                <th>Branch Name</th>
                                <th>Bank Name</th>
                                <th>IFSC Code</th>
                                <th>Product</th>
                                <th class="col-md-1">Date</th>
                                <th>Remarks</th>
                                <th>Image</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="qrReport" items="${qrReport.payload}">
                                <tr>
                                <td>${qrReport.complaintId}</td>
                                    <td>${qrReport.branchName}</td>
                                    <td>${qrReport.bank}</td>
                                    <td>${qrReport.ifsc}</td>
                                    <td>${qrReport.product}</td>
                                    <td style="text-align:right;">${qrReport.date}</td>
                                    <td>${qrReport.remarks}</td>
                                    <td><button class="image-icon btn btn-link" data-image="${qrReport.imageBase64}"><i class="fa fa-image"></i></button></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
    <!-- Image Modal -->
    <div id="imageModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="imageModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="imageModalLabel">Image Preview</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <img id="modalImage" class="img-fluid" src="" alt="Image Preview">
                </div>
            </div>
        </div>
    </div>

    <script>
        $(document).ready(function() {
            $('body').on('click', '.image-icon', function() {
                var imageData = $(this).data('image');
                if (imageData) {
                    $('#modalImage').attr('src', 'data:image/jpeg;base64,' + imageData);
                    $('#imageModal').modal('show');
                } else {
                    alert('No image data available.');
                }
            });
        });
    </script>

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
