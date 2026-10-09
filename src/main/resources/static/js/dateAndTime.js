
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
