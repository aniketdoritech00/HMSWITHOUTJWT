<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ page import="java.util.List" %>
        <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
            <%@ page import="org.springframework.http.HttpStatus" %>
                <%@ page import="com.doritech.api.Entity.*" %>
                    <%@ page import="com.doritech.api.DTO.EmployeeMasterDTO" %>
                        <%@ page import="com.doritech.api.Entity.ParamEntity" %>

                            <!DOCTYPE html>
                            <html lang="en">

                            <head>
                                <meta charset="UTF-8">
                                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                <title>Alert Creation</title>

                                <link rel="stylesheet" href="./css/deviceLogDetails.css" />
                                <link rel="stylesheet" href="./css/multilingual.css" />
                                <link href="./fonts/poppins.css" rel="stylesheet">
                                <link rel="stylesheet" href="./externalCSS/bootstrap.min.css" />
                                <script src="./externalJS/fontawesome.js"></script>

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
                                <style>
                                    #loader {
                                        border: 8px solid #f3f3f3;
                                        border-radius: 50%;
                                        border-top: 8px solid #3498db;
                                        width: 50px;
                                        height: 50px;
                                        animation: spin 2s linear infinite;
                                        margin: 20px auto;
                                    }

                                    @keyframes spin {
                                        0% {
                                            transform: rotate(0deg);
                                        }

                                        100% {
                                            transform:
                                                rotate (360 deg);
                                        }
                                    }

                                    .zones-container {
                                        display: flex;
                                        flex-direction: column;
                                        align-items: flex-start;
                                    }

                                    .zone-item {
                                        display: flex;
                                        align-items: center;
                                        margin-bottom: 5px;
                                    }

                                    .zone-item input {
                                        margin-right: 5px;
                                    }

                                    button {
                                        background-color: #028bd7;
                                        color: #fff;
                                        border: #fff;
                                        cursor: pointer;
                                    }
                                </style>
                            </head>

                            <body style="font-size: 12px; font-family:Poppins;">
                                <div class="row mx-1">
                                    <div class="col-md-12">
                                        <div class="row">
                                            <div class="col-md-12">
                                                <h5 class="heading">Alert Creation</h5>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="row m-2">
                                    <div class="col-md-12">
                                        <div class="card p-2" style="max-height: 500px; overflow-y: scroll;">
                                            <table class="table table-bordered">
                                                <thead>
                                                    <tr>
                                                        <th>Branch Code</th>
                                                        <th>Branch Type</th>
                                                        <th>Branch Description</th>
                                                        <th>Device ID</th>
                                                        <th>Device Serial Number</th>
                                                        <th>Device Zones</th>
                                                        <th>Action</th>
                                                    </tr>
                                                </thead>
                                                <tbody id="table-body">
                                                    <!-- Rows will be dynamically added here -->
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>

                                <script>
                                async function fetchData() {
                                    const apiURL = "http://localhost:9090/getAllDetailsByBranchCodeForReport?branchCode=BR-1"; 
                                    try {
                                        const response = await fetch(apiURL);
                                        const data = await response.json();

                                        populateTable(data.payload);
                                    } catch (error) {
                                        console.error("Error fetching data:", error);
                                        alert("Failed to fetch data from the API.");
                                    }
                                }

                                function populateTable(data) {
                                    const tableBody = document.getElementById("table-body");
                                    tableBody.innerHTML = "";

                                    data.forEach((item) => {
                                        const row = document.createElement("tr");

                                        const branchCodeCell = document.createElement("td");
                                        branchCodeCell.textContent = item.branchCode;
                                        row.appendChild(branchCodeCell);

                                        const branchTypeCell = document.createElement("td");
                                        branchTypeCell.textContent = item.branchType;
                                        row.appendChild(branchTypeCell);

                                        const branchDescCell = document.createElement("td");
                                        branchDescCell.textContent = item.branchDesc;
                                        row.appendChild(branchDescCell);

                                        const deviceIdCell = document.createElement("td");
                                        deviceIdCell.textContent = item.deviceId || "N/A";
                                        row.appendChild(deviceIdCell);

                                        const deviceSerialCell = document.createElement("td");
                                        deviceSerialCell.textContent = item.deviceSerialNo || "N/A"; 
                                        row.appendChild(deviceSerialCell);

                                        const deviceZonesCell = document.createElement("td");
                                        const zonesContainer = document.createElement("div");
                                        zonesContainer.className = "zones-container";

                                        if (item.deviceZones && item.deviceZones.length > 0) {
                                            item.deviceZones.forEach((zone) => {
                                                const zoneItem = document.createElement("div");
                                                zoneItem.className = "zone-item";

                                                const zoneCheckbox = document.createElement("input");
                                                zoneCheckbox.type = "checkbox";
                                                zoneCheckbox.value = "N"; 

                                                zoneCheckbox.addEventListener("change", () => {
                                                    zoneCheckbox.value = zoneCheckbox.checked ? "A" : "N";
                                                });

                                                const zoneLabel = document.createElement("label");
                                                zoneLabel.textContent = zone.zoneOriginalName + " - " + zone.zoneDesiredName + " - " + zone.alertType;

                                                zoneItem.appendChild(zoneCheckbox);
                                                zoneItem.appendChild(zoneLabel);

                                                zonesContainer.appendChild(zoneItem);
                                            });
                                        } else {
                                            zonesContainer.textContent = "No Zones Available";
                                        }

                                        deviceZonesCell.appendChild(zonesContainer);
                                        row.appendChild(deviceZonesCell);

                                        const actionCell = document.createElement("td");
                                        const submitButton = document.createElement("button");
                                        submitButton.textContent = "Submit";

                                        submitButton.addEventListener("click", () => {
                                            const selectedZones = [];
                                            const unselectedZones = [];

                                            item.deviceZones.forEach((zone, index) => {
                                                const checkbox = zonesContainer.querySelectorAll("input")[index];
                                                const zoneName = zone.zoneOriginalName;

                                                if (checkbox.checked) {
                                                    selectedZones.push({ zoneName, value: "A" });
                                                } else {
                                                    unselectedZones.push({ zoneName, value: "N" });
                                                }
                                            });

                                            const allZones = [...selectedZones];

                                            const rowData = {
                                                branchCode: item.branchCode,
                                                branchType: item.branchType,
                                                branchDesc: item.branchDesc,
                                                deviceId: item.deviceId,
                                                deviceSerialNo: item.deviceSerialNo, 
                                                zones: allZones, 
                                            };

                                            saveSmsDetails(rowData.zones, rowData.deviceSerialNo); 
                                        });

                                        actionCell.appendChild(submitButton);
                                        row.appendChild(actionCell);

                                        tableBody.appendChild(row);
                                    });
                                }
                                function saveSmsDetails(zones, deviceSerialNo) {
                                	alert(JSON.stringify(zones));
                                    const zoneDetails = {};

                                    for (let i = 1; i <= 40; i++) {
                                        const zoneKey = "zone" + i; 
                                        zoneDetails[zoneKey] = "N"; 
                                    }


                                    zones.forEach(zone => {
                                        const zoneKey = "zone" + zone.zoneName.replace("ZONE", ""); 
                                        
                                        if (zone.value === "A") {
                                            zoneDetails[zoneKey] = "A"; 
                                            alert("Matching zone: "+zone.zoneName+", Value: "+zone.value); 
                                        }
                                    });

                                    const smsDetails = {
                                        senderMobileNo: "1234567890",
                                        panelSerialNo: deviceSerialNo,
                                        dateTime: new Date().toISOString(),
                                        uniqueId: '00000',
                                        main: '1', 
                                        solar: '0', 
                                        battery: '5', 
                                        signalFlag: '1',
                                        armed: '1', 
                                        ...zoneDetails, 
                                        completeMessage: "Sample Complete Message", 
                                        entryDateTime: new Date().toISOString(),
                                        broadcastFlag: '1' 
                                     
                                    };

                                    alert(JSON.stringify(smsDetails)); 

                                    $.ajax({
                                        url: "http://localhost:9090/saveNewSMS",
                                        type: "POST",
                                        contentType: "application/json",
                                        data: JSON.stringify(smsDetails),
                                        success: function(response) {
                                            alert("SMS Details Saved Successfully!");
                                            console.log(response);
                                        },
                                        error: function(xhr, status, error) {
                                            alert("Error occurred while saving SMS details.");
                                            console.error(error);
                                        }
                                    });
                                }

                                fetchData();

                                    
                                </script>
                                <script src="./js/navBarToggle.js"></script>
                                <script src="./externalJS/bootstrap.bundle.min.js"></script>
                                <script src="./js/activeDeviceLogDetails.js"></script>
                                <script src="./js/tmsDataTables.js"></script>

                            </body>

                            </html>