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
                  <h5 class="heading">Email Logs</h5>
               </div>
            </div>
         </div>
      </div>
      <div class="row">
         <div class="col-md-12">
            <div class="m-2">
               <div class="card" style="max-height: 85vh; overflow-y: auto;">
                  <div class="table-responsive" style="max-height: 75vh; overflow-y: auto;">
                     <table id="example" class="table table-striped table-bordered" style="margin-bottom:0; width:100%;">
                        <thead style="position: sticky; top: 0; z-index: 2; background: #028bd7;">
                           <tr style="background: #028bd7; color: #fff;">
                              <th>S.No</th>
                              <th>Email Send To</th>
                              <th>Email Send At</th>
                              <th>Email Subject</th>
                              <th>Email Body</th>
                           </tr>
                        </thead>
                        <tbody>
                           <!-- Rows will be populated by JS -->
                        </tbody>
                     </table>
                  </div>
               </div>
            </div>
         </div>
      </div>
      <script>
         $(document).ready(function() {
            $.ajax({
               url: "./getAllEmailLogReport",
               type: "GET",
               success: function(response) {
                  if (response && response.statusCode === 200 && response.payload) {
                     var rows = "";
                     response.payload.forEach(function(item, idx) {
                        // Format date as DD-MM-YYYY (only date part)
                        var dt = "";
                        if (item.datetime) {
                           var d = new Date(item.datetime);
                           var day = String(d.getDate()).padStart(2, '0');
                           var month = String(d.getMonth() + 1).padStart(2, '0');
                           var year = d.getFullYear();
                           dt = day + "-" + month + "-" + year;
                        }
                        // Safely render bodyText as HTML, fallback to empty string if missing
                        var body = item.bodyText ? item.bodyText : "";
                        rows += "<tr>" +
                           "<td>" + (idx + 1) + "</td>" +
                           "<td>" + (item.emailIdTo || "") + "</td>" +
                           "<td>" + dt + "</td>" +
                           "<td>" + (item.subject || "") + "</td>" +
                           "<td>" + body + "</td>" +
                           "</tr>";
                     });
                     $("#example tbody").html(rows);
                     // Initialize DataTable after data is loaded with export buttons
                     $('#example').DataTable({
                        destroy: true,
                        ordering: true,
                        searching: true,
                        paging: true,
                        info: true,
                        lengthChange: true,
                        pageLength: 10,
                        autoWidth: false,
                        responsive: true,
                        dom: 'Bfrtip',
                        buttons: [
                           'copy', 'csv', 'excel', 'pdf', 'print'
                        ]
                     });
                  } else {
                     $("#example tbody").html('<tr><td colspan="5" class="text-center">No data found</td></tr>');
                  }
               },
               error: function() {
                  $("#example tbody").html('<tr><td colspan="5" class="text-center">Error loading data</td></tr>');
               }
            });
         });
      </script>
      <script src="./js/navBarToggle.js"></script>
      <script src="./externalJS/bootstrap.bundle.min.js"></script>
      <script src="./externalJS/multilingual.js"></script>
      <script src="./js/googleTranslateElementInit.js"></script>
   </body>
</html