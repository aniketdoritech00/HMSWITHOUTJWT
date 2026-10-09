/**
*
*/
 
$(document).ready(function () {
    $("#searchButton").click(function () {
        var panelSerialNo = $("#panelSerialNo").val();
 
        $.ajax({
            type: "GET",
            url: "getActiveDeviceLogDetails",
            data: { panelSerialNo: panelSerialNo },
            success: function (response) {
                var data = response.payload;
 
                // Clear existing rows in the table
                $("#example tbody").empty();
 
                for (var i = 0; i < data.length; i++) {
                    // Set the same panelSerialNo for all rows
                    data[i].panelSerialNo = panelSerialNo;
 
                    var row = $("<tr>").append(
                        $("<td>").text(data[i].panelSerialNo),
                        $("<td>").text(data[i].deviceName),
                        $("<td>").text(data[i].deviceType),
                        $("<td>").text(data[i].deviceLocation),
                        $("<td>").text(data[i].mobileNo),
                        $("<td>").text(data[i].alertType),
                        $("<td>").text(data[i].alarmTime)
                    );
 
                    $("#example tbody").append(row);
                }
            },
            error: function (error) {
                console.error("Error fetching data:", error);
            }
        });
    });
});
 
 