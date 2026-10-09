$(document).ready(function() {
  
    if ($.fn.DataTable.isDataTable('#example')) {
        $('#example').DataTable().destroy();
    }
    
    $('#example').DataTable({
        responsive: true,
        paging: false,
        scrollCollapse: true,
        scrollY: '45vh',
        dom: 'Bfrtip', 
        buttons: [
            'copy', 'csv', 'excel', 'pdf', 'print'
        ],
        searching: false, // Completely disable search functionality
        order: [[5, 'desc']]
    });
});

$(document).ready(function() {
    var loggedInUser = $('#branchCodeContainer').text().trim();

    $.ajax({
        type: 'GET',
        url: './getAllAlertTypes',
        data: {
            branchCode: loggedInUser
        },
        success: function(response) {
            if (response.statusCode === 200) {
                var branches = response.payload;
                var tableBody = $('#example tbody');

                // Function to append branches recursively
                function appendBranches(branch, level) {
                    var alertTypes = branch.alertTypes;

                    var branchRow = '<tr>';
                    branchRow += '<td>' + ' '.repeat(level * 4) + branch.branchName + '</td>';
                    branchRow += '<td>' + branch.branchType + '</td>';
                    branchRow += '<td>' + branch.bankName + '</td>';
                    branchRow += '<td>' + branch.branchCode + '</td>';
                    branchRow += '<td><button class="alert-btn btn btn-sm btn-danger status-btn" data-branch-code="' + branch.branchCode + '">Select Alert</button></td>';
                    branchRow += '<td><button class="save-btn btn btn-sm btn-info status-btn" onclick="saveSocialMediaData(\'' + branch.bankName + '\', \'' + branch.branchCode + '\')">Save</button></td>';
                    branchRow += '</tr>';

                    tableBody.append(branchRow);

                    if (branch.children && branch.children.length > 0) {
                        branch.children.forEach(function(child) {
                            appendBranches(child, level + 1);
                        });
                    }
                }

                // Clear existing table data before appending new data
                tableBody.empty();

                // Iterate through branches and append to the table
                branches.forEach(function(branch) {
                    appendBranches(branch, 0);
                });

                // Refresh the DataTable after adding new data
                if ($.fn.DataTable.isDataTable('#example')) {
                    $('#example').DataTable().clear().rows.add(tableBody.find('tr')).draw();
                }

                // Click event for "Select Alert Type" button
                $(document).on('click', '.alert-btn', function() {
                    var branchCode = $(this).data('branch-code');
                    var branchAlerts = branches.find(branch => branch.branchCode === branchCode).alertTypes;

                    // Disable save buttons for all branches except the one being clicked
                    $('.save-btn').prop('disabled', true);  // Disable all save buttons
                    $(this).closest('tr').find('.save-btn').prop('disabled', false);  // Enable the save button for the selected branch

                    // Fetch saved alert types
                    $.ajax({
                        url: './api/social-media/getSavedAlertTypes',
                        type: 'GET',
                        data: { branchCode: branchCode },
                        success: function(response) {
                            var savedAlerts = response.statusCode === 200 ? response.payload : [];

                            // Create a map for easy lookup of saved alerts
                            var savedAlertsMap = {};
                            savedAlerts.forEach(function(alert) {
                                savedAlertsMap[alert.alertType] = alert;
                            });

                            // Populate modal with branch-specific alerts
                            var modalBody = $('#alertModal .modal-body');
                            modalBody.empty();

                            branchAlerts.forEach(function(alertType) {
                                var savedAlert = savedAlertsMap[alertType];

                                // If the alert type exists in saved alerts, check the values for each notification type
                                var isCheckedWhatsapp = savedAlert && savedAlert.whatsapp === 'Y';
                                var isCheckedSms = savedAlert && savedAlert.sms === 'Y';
                                var isCheckedCalling = savedAlert && savedAlert.calling === 'Y';
                                var isCheckedMail = savedAlert && savedAlert.mail === 'Y';

                               var alertRow = `
                                    <div class="alert-row">
                                        <p>${alertType}</p>
                                        <div class="checkbox-group">
                                            <label>
                                                <input type="checkbox" id="${alertType}_whatsapp" name="${alertType}" value="Y" data-initial-value="${isCheckedWhatsapp ? 'Y' : 'N'}" ${isCheckedWhatsapp ? 'checked' : ''}>
                                                <i class="fa fa-whatsapp whatsapp-icon"></i>
                                            </label>
                                           	<label>
											    <input type="checkbox" id="${alertType}_sms" name="${alertType}" value="Y" data-initial-value="${isCheckedSms ? 'Y' : 'N'}" ${isCheckedSms ? 'checked' : ''}>
											    <img src="./img/sms.png" alt="SMS Icon" class="sms-image">
											</label>
                                            <label>
                                                <input type="checkbox" id="${alertType}_calling" name="${alertType}" value="Y" data-initial-value="${isCheckedCalling ? 'Y' : 'N'}" ${isCheckedCalling ? 'checked' : ''}>
                                                <i class="fa fa-phone call-icon"></i>
                                            </label>
                                            <label>
                                                <input type="checkbox" id="${alertType}_mail" name="${alertType}" value="Y" data-initial-value="${isCheckedMail ? 'Y' : 'N'}" ${isCheckedMail ? 'checked' : ''}>
												<img src="./img/mail.png" alt="Mail Icon" class="mail-image">
								            </label>
                                        </div>
                                    </div>`;

                                modalBody.append(alertRow);
                            });

                            $('#alertModal').modal('show');
                        },
                        error: function(xhr, status, error) {
                            console.error('Error fetching saved alert types:', error);
                        }
                    });
                });

                // Update checkbox values on change
                $(document).on('change', '#alertModal input[type="checkbox"]', function() {
                    var isChecked = $(this).is(':checked');
                    $(this).val(isChecked ? 'Y' : 'N');
                });

                // Close modal or submit and re-enable save buttons
                /*$('#alertModal').on('hidden.bs.modal', function() {
                    $('.save-btn').prop('disabled', false);  // Enable all save buttons when modal is closed
                });*/
            } else {
                alert('Failed to fetch branch data: ' + response.message);
            }
        },
        error: function(xhr, status, error) {
            console.error('Error fetching branch data:', error);
            alert('Error fetching branch data');
        }
    });
});

// Save social media alert data
function saveSocialMediaData(bank, branchCode) {
    var dataToSave = [];
    var currentTime = new Date().toISOString();

    $('.alert-row').each(function() {
        var alertType = $(this).find('p').text().trim();
        var checkboxes = $(this).find('input[type="checkbox"]');
        var whatsapp = $(checkboxes[0]).is(':checked') ? 'Y' : 'N';
        var sms = $(checkboxes[1]).is(':checked') ? 'Y' : 'N';
        var calling = $(checkboxes[2]).is(':checked') ? 'Y' : 'N';
        var mail = $(checkboxes[3]).is(':checked') ? 'Y' : 'N';

        // Only save if there are changes
        if (
            whatsapp !== $(checkboxes[0]).data('initial-value') ||
            sms !== $(checkboxes[1]).data('initial-value') ||
            calling !== $(checkboxes[2]).data('initial-value') ||
            mail !== $(checkboxes[3]).data('initial-value')
        ) {
            dataToSave.push({
                bankName: bank,
                branchCode: branchCode,
                alertType: alertType,
                whatsapp: whatsapp,
                sms: sms,
                mail: mail,
                calling: calling,
                timestamp: currentTime
            });
        }
    });

    if (dataToSave.length === 0) {
        alert('No changes to save.');
        return;
    }
    //alert(JSON.stringify(dataToSave))
    
    // Send data to the API endpoint
    $.ajax({
        type: 'POST',
        url: './api/social-media/save',
        contentType: 'application/json',
        data: JSON.stringify(dataToSave),
        success: function(response) {
            console.log('Data saved successfully:', response);
            Swal.fire({
                icon: 'success',
                title: 'Data saved',
                text: response,
                confirmButtonText: 'OK'
            }).then(() => {
                location.reload();
            });
        },
        error: function(xhr, status, error) {
            console.error('Error saving data:', error);
            alert('Error saving data');
        }
    });
}

$(document).ready(function() {
    // Close the modal on Submit button click
    $('#submitAlertButton').on('click', function() {
        $('#alertModal').modal('hide');  // Hide the modal when submit is clicked
    });

    // Optionally, if you want to handle closing via the close icon
    $('.modal-close').on('click', function() {
        $('#alertModal').modal('hide');  // Hide the modal when close icon is clicked
    });
});