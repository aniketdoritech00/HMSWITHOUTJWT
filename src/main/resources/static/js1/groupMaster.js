/*-------------------------------- MODAL FUNCTION FOR ADD GROUP MODAL: SUYASH-------------------------------- */
/******* @Author SWATI *******/
const openModalButton = document.getElementById('addGroupModalButton');
const groupModal = document.getElementById('groupMasterModal');

function opengroupModal() {
    groupModal.style.display = 'block';
}


function closegroupModal() {
   groupModal.style.display ='none';
   $("#groupForm")[0].reset();
}


openModalButton.addEventListener('click', opengroupModal);

groupModal.addEventListener('click', function(event) {
    if (event.target === groupModal) {
        closegroupModal();
    }
});

const closeModalButton = document.querySelector('#groupMasterModal .close');
closeModalButton.addEventListener('click', closegroupModal);

/******************************************************************************/

/************* SAVE NEW GROUP ---- SWATI *****************/

function saveNewGroup() {
if (validateGroupFormData()) {
}
    try {
            var formData = {
                groupId: $("#groupId").val(),
                groupName: $("#groupName").val(),
                groupDescription: $("#groupDescription").val(),
                createdDate: $("#createdDate").val(),
                empId: $("#empId").val(),
            };

            $.ajax({
                url: "saveOrUpdateGroupMaster",
                type: "POST",
                dataType: "json",
                contentType: "application/json",
                data: JSON.stringify(formData),
                success: function (data) {
                    try {
                        Swal.fire({
                            icon: 'success',
                            title: 'Success!',
                            text: 'Group Master saved successfully.',
                        }).then(function(){
                         location.reload();
                        });

                    } catch (successError) {
                        console.error(successError);
                    }
                },
                error: function (error) {
                alert("error");
                    try {
                        Swal.fire({
                            icon: 'error',
                            title: 'Error!',
                            text: 'Failed to save Group Master.',
                        });
                        console.error(error);
                    } catch (errorHandlingError) {
                        console.error(errorHandlingError);
                    }
                }
            });
        } catch (outerError) {
            console.error(outerError);
        }
}
function resetForm() {
    try {
        $("#groupForm")[0].reset();
    } catch (resetError) {
        console.error(resetError);
    }
}


function validateGroupFormData() {
	try {
		errStr = "";
		if ($('#groupName').val() == '') {
			Swal.fire({
				text: "Please enter Group Name.",
				icon: "error",
			});
			return false;
		}
		if ($('#groupDescription').val() == '') {
        	Swal.fire({
        		text: "Please Select Group description.",
        		icon: "error",
        	});
        	return false;
        }
		return true;
	} catch (err) {
		alert(err.message);
	}
}

/**************JS TO FETCH GROUP BY GROUP ID****************** */

function fetchGroupById(groupId) {
    $.ajax({
        type: "GET",
        url: "getGroupById" ,
        data: { groupId: groupId }, // Replace with your actual API endpoint
        success: function (response) {
            if (response.statusCode === 200) {
                var trainingAssignment = response.payload;
				console.log(response.payload)
              
              
                $("#groupName").val(trainingAssignment.groupName);
                $("#groupDescription").val(trainingAssignment.groupDescription);
                $("#createdDate").val(trainingAssignment.createdDate);
                $("#empId").val(trainingAssignment.empId);
      


                 opengroupModal();
            } else {
                // Handle error scenario
                console.error("Error fetching group details:", response.message);
            }
        },
        error: function (error) {
            console.error("Error fetching group details:", error);
        }
    });
}

