function updateDisplay(buttonName, iconClass) {
            
            const displayIcon = document.getElementById('display-icon');
            const displayDigitals = document.getElementById('display-digitals');
            const displayText = document.getElementById('display-text');

            
            displayIcon.className = `display-icon ${iconClass}`;
            displayDigitals.textContent= 'Digitals India Hub'
            displayText.textContent = `${buttonName}`;
        }
        
        
      function updateSelectedDevice(element) {
    // Get the text content of the selected item
    const selectedText = element.textContent.trim();

    // Update the button text to show the selected device
    const button = document.getElementById('dropdownMenuButton');
    button.textContent = selectedText;

    // Update the selected serial number
    const serialNumber = element.getAttribute('data-serial-number');
    selectedDeviceSerialNumber = serialNumber; // Save the serial number
} 
        
        let selectedDeviceSerialNumber = null; // Global variable to store the selected serial number

    function selectDevice(serialNumber) {
        selectedDeviceSerialNumber = serialNumber; // Save the serial number
       // alert("Selected Device Serial Number:"+serialNumber); // Optional: Alert to confirm selection
    }

    function sendCommand(commandType, iconClass) {
		
		var userId = $("#userId").val();
		var branchCode = $("#branchId").val();
        if (!selectedDeviceSerialNumber) {
            alert("Please select a device first!");
            return;
        }
     
      $.ajax({
    url: "hitUrlForCommands",
    type: "POST",
    contentType: "application/json",
    data: JSON.stringify(payload),
    success: function (response) {
        Swal.fire({
            icon: 'success',
            title: 'Command Sent Successfully',
            text: response,
            confirmButtonText: 'OK'
        });
        updateDisplay(commandType, iconClass);
    },
    error: function (error) {
        console.error("Error Sending Command:", error);
        Swal.fire({
            icon: 'error',
            title: 'Failed to Send Command',
            text: 'Command Type: ' + commandType,
            confirmButtonText: 'OK'
        });
    },
});

    }

 