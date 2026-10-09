/**
 * 
 */

$(document).ready(function () {
    var branchName = $('#branchName').val();
    
    fetchDeviceLogDetails();

    function fetchDeviceLogDetails() {
        $.ajax({
            type: "GET",
            url: "getDeviceLogDetails",
            data: {
                branchName: branchName
            },
            success: function (response) {
                console.log("Response:", response);

                if (response.statusCode === 200) {  // Check if status code is a number
                    $("#example tbody").empty();

                    $.each(response.payload, function (index, deviceLog) {
                        var newRow = "<tr>" +
                            "<td>" + deviceLog.deviceName + "</td>" +
                            "<td>" + deviceLog.panelSerialNo + "</td>" +
                            "<td>" + deviceLog.lastUpdatedDateTime + "</td>" +
                            "<td>" + deviceLog.notSyncedFrom + "</td>" +
                            "</tr>";
                        $("#example tbody").append(newRow);
                    });
                } else {
                    alert("Error: " + response.message);
                }
            },
            error: function (error) {
                console.error("Error: Unable to fetch data from the server.", error);
                alert("Error: Unable to fetch data from the server. " + JSON.stringify(error));
            }
        });
    }
});




