function saveNewVendor() {
    if (validateVendorFormData()) {
        const formData = {
            vendorId: $("#vendorId").val(),
            vendorName: $("#vendorName").val(),
            contactPerson: $("#contactPerson").val(),
            email: $("#email").val(),
            mobile: $("#mobile").val(),
            vendorStatus: $("#vendorStatus").val(),
            address: $("#address").val(),
            pin: $("#pin").val(),
            city: $("#city").val(),
            state: $("#state").val(),
            country: $("#country").val(),
        };

        $.ajax({
            url: "createVendor",  
            type: "POST",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(formData),
            success: function(data) {
                Swal.fire({
                    icon: 'success',
                    title: 'Success!',
                    text: 'Vendor saved successfully.',
                }).then(function() {
                    location.reload();  
                });
            },
            error: function(error) {
                Swal.fire({
                    icon: 'error',
                    title: 'Error!',
                    text: 'Failed to save Vendor.',
                });
            }
        });
    }
}

function validateVendorFormData() {
    if ($("#vendorName").val() && $("#email").val() && $("#mobile").val()) {
        return true;
    } else {
        Swal.fire({
            icon: 'error',
            title: 'Validation Error!',
            text: 'Please fill all required fields.',
        });
        return false;
    }
}

function resetForm() {
    $('#vendorForm')[0].reset();  
    $('#editButton').hide();
    $('#saveVendorButton').show();
}

function editVendor() {
    if (validateVendorFormData()) {
        const formData = {
            vendorId: $("#vendorId").val(),
            vendorName: $("#vendorName").val(),
            contactPerson: $("#contactPerson").val(),
            email: $("#email").val(),
            mobile: $("#mobile").val(),
            vendorStatus: $("#vendorStatus").val(),
            address: $("#address").val(),
            pin: $("#pin").val(),
            city: $("#city").val(),
            state: $("#state").val(),
            country: $("#country").val(),
        };

alert(JSON.stringify(formData))
        $.ajax({
            url: "updateVendor",  
            type: "POST",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(formData),
            success: function(data) {
                Swal.fire({
                    icon: 'success',
                    title: 'Success!',
                    text: 'Vendor updated successfully.',
                }).then(function() {
                    location.reload();  
                });
            },
            error: function(error) {
                Swal.fire({
                    icon: 'error',
                    title: 'Error!',
                    text: 'Failed to update vendor.',
                });
            }
        });
    }
}

function importVendorMaster() {
    const formData = new FormData(document.getElementById('uploadForm'));

    $.ajax({
        url: 'saveVendorMasterFile',  
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function(response) {
            Swal.fire({
                icon: 'success',
                title: 'Success!',
                text: 'Vendor data imported successfully.',
            }).then(function() {
                location.reload();  
            });
        },
        error: function(error) {
            Swal.fire({
                icon: 'error',
                title: 'Error!',
                text: 'Failed to import Vendor data.',
            });
        }
    });
}

function exportVendorMaster() {
    window.location.href = 'exportVendorMaster';
}

function fetchVendor(vendorId) {
	//alert(vendorId)
    $.ajax({
        url: 'getVendorById', 
        type: 'GET',
        data: { vendorId: vendorId },
        success: function(response) {
			
			var data = response.payload;
			//alert(JSON.stringify(data))
            $("#vendorId").val(data.vendorId);
            $("#vendorName").val(data.vendorName);
            $("#contactPerson").val(data.contactPerson);
            $("#email").val(data.email);
            $("#mobile").val(data.mobile);
            $("#vendorStatus").val(data.vendorStatus);
            $("#address").val(data.address);
            $("#pin").val(data.pin);
            $("#city").val(data.city);
            $("#state").val(data.state);
            $("#country").val(data.country);
            $('#editButton').show();
            $('#saveVendorButton').hide();
        },
        error: function(error) {
            Swal.fire({
                icon: 'error',
                title: 'Error!',
                text: 'Failed to fetch vendor details.',
            });
        }
    });
}

