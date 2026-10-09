<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="ISO-8859-1"%>
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
<title>Communicator Sync Logs</title>

<link rel="stylesheet" href="./css/deviceLogDetails.css" />
<link rel="stylesheet" href="./css/multilingual.css" />
<link href="./fonts/poppins.css" rel="stylesheet">
<link rel="stylesheet" href="./externalCSS/bootstrap.min.css" />
<script src="/externalJS/fontawesome.js"></script>

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

<style type="text/css">
td a {
 color: black;
 text-decoration: none !important;
}

.table thead th, .table tbody td {
 white-space: nowrap;
}
</style>

</head>
<body style="font-size: 12px; overflow-y: hidden; font-family:Poppins;">

 <div class="row mx-1">
  <div class="col-md-12">
   <div class="row">
    <div class="col-md-12">
     <h5 class="heading">Communicator Sync Logs</h5>
    </div>
   </div>
  </div>
 </div>

 <div class="row mx-2">
  <div class="col-md-12">
   <div class="card-container">
    <div class="card" style="">
     <div class="">
     <input type="hidden" placeholder="" class="form-control form-control-sm" id="branchName" value="${branchId}"/>
      <table id="example" class="table table-striped table-bordered">
       <thead>
        <tr style="text-align:center;">
         <th>Branch Name</th>
         <th>Bank Name</th>
         <th>Vendor Name</th>
         <th>Contact No.</th>
         <th>Serial No</th>
         <th>Last Updated Time</th>
         <th>Not Synced From</th>
        </tr>
       </thead>
       <tbody>
        <c:forEach items="${logDetails.payload}" var="log">
         <tr>
          <td>${log.branchName}</td>
          <td style="text-align:left;">${log.bankName}</td>
          <td>${log.vendorName}</td>
          <td style="text-align:right;">${log.mobile}</td>
           <td style="text-align:right;">${log.panelSerialNo}</td>
          <td style="text-align:right;">${log.lastUpdatedDateTime}</td>
          <td style="text-align:right;">${log.notSyncedFrom}</td>
         </tr>
        </c:forEach>
       </tbody>
      </table>
     </div>
    </div>
   </div>
  </div>
 </div>

 <script src="./js/navBarToggle.js"></script>
 <script src="./externalJS/bootstrap.bundle.min.js"></script>
 
 
<script>
var reportTitle = "Communicator Sync Logs";
var aboutText = "ABOUT: The Communicator Sync Logs report provides detailed insights into the synchronization status of communication devices. It records the last updated time. This report helps in diagnosing connectivity issues, ensuring data integrity, and improving system reliability by tracking sync performance over time.";
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

	 convertImageToBase64('./logo/digitals.png', function (base64Logo) {
	     $('#example').DataTable({
	         paging: false,
	         scrollY: '45vh',
	         scrollX: false,
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
	                     var rels = xlsx.xl['worksheets/_rels/sheet1.xml.rels'];
	                     var drawingRels = '<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/image" Target="media/image1.png"/>';
	                     $(rels).append(drawingRels);

	                     var drawing = xlsx.xl.drawings['drawing1.xml'];
	                     var twoCellAnchor = `
	                         <xdr:twoCellAnchor>
	                             <xdr:from>
	                                 <xdr:col>0</xdr:col>
	                                 <xdr:colOff>0</xdr:colOff>
	                                 <xdr:row>0</xdr:row>
	                                 <xdr:rowOff>0</xdr:rowOff>
	                             </xdr:from>
	                             <xdr:to>
	                                 <xdr:col>1</xdr:col>
	                                 <xdr:colOff>0</xdr:colOff>
	                                 <xdr:row>1</xdr:row>
	                                 <xdr:rowOff>0</xdr:rowOff>
	                             </xdr:to>
	                             <xdr:pic>
	                                 <xdr:nvPicPr>
	                                     <xdr:cNvPr id="0" name="Picture 1"/>
	                                     <xdr:cNvPicPr/>
	                                 </xdr:nvPicPr>
	                                 <xdr:blipFill>
	                                     <a:blip r:embed="rId1"/>
	                                     <a:stretch>
	                                         <a:fillRect/>
	                                     </a:stretch>
	                                 </xdr:blipFill>
	                                 <xdr:spPr>
	                                     <a:xfrm>
	                                         <a:off x="0" y="0"/>
	                                         <a:ext cx="0" cy="0"/>
	                                     </a:xfrm>
	                                     <a:prstGeom prst="rect">
	                                         <a:avLst/>
	                                     </a:prstGeom>
	                                 </xdr:spPr>
	                             </xdr:pic>
	                             <xdr:clientData/>
	                         </xdr:twoCellAnchor>`;
	                     $(drawing).append(twoCellAnchor);

	                     var media = xlsx.xl['media/image1.png'];
	                     media.data = base64Logo.split(',')[1];
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
	                         fontSize: 11,
	                         color: 'black',
	                         fillColor: '#f9f9f9',
	                         margin: [5, 5, 5, 5]
	                     };
	                     doc.styles.tableBodyOdd = {
	                         fontSize: 11,
	                         color: 'black',
	                         margin: [5, 5, 5, 5]
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
	             'print'
	         ],
	         order: [[5, 'desc']]
	     });
	 });

 });


</script>
</body>
</html>
