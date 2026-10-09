$(document).ready(function() {
    $('#example').DataTable({
        responsive: true,
        paging: false,
        scrollCollapse: true,
        scrollY: '45vh',
        dom: 'Bfrtip',
        buttons: [
            'copy', 'csv', 'excel', 'pdf', 'print'
        ],
        searching: true  
    });
});
;