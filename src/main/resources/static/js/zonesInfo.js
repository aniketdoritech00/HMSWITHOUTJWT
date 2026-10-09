 function updateButtonText(element, branchCode) {
        // Update the button text
        const button = document.getElementById('dropdownMenuButton');
        button.textContent = element.textContent.trim();

        // Call fetchZones with the branchCode (optional functionality)
        fetchZones(branchCode);
    }

function fetchZones(branchCode) {
    const apiUrl = "getLatestRecord?branchCode=" + branchCode; // Update to your API URL

    // AJAX call to the API
    $.ajax({
        url: apiUrl,
        type: "GET", // Assuming it's a GET request
        dataType: "json", // Expecting JSON response
        success: function(response) {
            if (response.payload && response.payload.latestRecord) {
                // Clear the zone list container
                const zoneListContainer = document.getElementById("zone-list");
                zoneListContainer.innerHTML = "";

                // Fetch the timestamp from the latestRecord field
                const dateTime = new Date(response.payload.latestRecord.dateTime);
                const formattedDateTime = dateTime.toLocaleString(); // Format date and time

                // Extract zones from the latestRecord
                const latestRecord = response.payload.latestRecord;
                const zones = Object.keys(latestRecord)
                    .filter(key => key.startsWith("zone")) // Get keys starting with "zone"
                    .map(key => ({ zoneName: key, status: latestRecord[key] })); // Map to array of objects

                // Process each zone and render it
                zones.forEach(({ zoneName, status }) => {
                    let statusText = "";
                    let iconClass = "";
                    let buttonClass = "";
                    let iconColor = "";

                    // Map status to descriptive text and icons
                    switch (status) {
                        case "A":
                            statusText = "Alarmed";
                            iconClass = "fa-solid fa-bell";
                            buttonClass = "text-danger";
                            iconColor = "#AF1740";
                            break;
                        case "N":
                            statusText = "Normal";
                            iconClass = "fa-solid fa-check-circle";
                            buttonClass = "text-success";
                            iconColor = "#347928";
                            break;
                        case "I":
                            statusText = "Isolated";
                            iconClass = "fa-solid fa-exclamation-circle";
                            buttonClass = "text-warning";
                            iconColor = "#FFF100";
                            break;
                        default:
                            statusText = "Unknown";
                            iconClass = "fa-solid fa-question-circle";
                            buttonClass = "text-secondary";
                            iconColor = "#405D72";
                            break;
                    }

                    // Create a new list item for each zone
                    const zoneItem = document.createElement("div");
                    zoneItem.className = "room-card";

                    // Apply styles
                    zoneItem.style.padding = "15px";
                    zoneItem.style.marginBottom = "10px";
                    zoneItem.style.borderRadius = "5px";
                    zoneItem.style.backgroundColor = "#fff";
                    zoneItem.style.maxHeight = "80vh"; // Optional for height limit
                    zoneItem.style.position = "relative";

                    zoneItem.innerHTML = `
    <div>
        <span class="room-icon">
            <i class="${iconClass}" style="color: ${iconColor}"></i>
        </span>
        <span class="mx-4" style="font-size:12px;">${zoneName}</span>
        <span class="date-time" style="position: absolute; top: 10px; right: 10px; font-size: 10px; color: #777;">
            ${formattedDateTime}
        </span>
    </div>
    <span class="${buttonClass}" style="font-size:14px;">
        ${statusText}
    </span>
`;

                    zoneListContainer.appendChild(zoneItem);
                });

                // Update total zones count
                const totalZonesElement = document.getElementById("total-zones");
                totalZonesElement.textContent = `Total Zones: ${zones.length}`;
            } else {
                alert("No zones data available for the selected device");
            }
        },
        error: function(xhr, status, error) {
            console.error("Error:", error);
            alert("An error occurred while fetching zones data");
        }
    });
}

