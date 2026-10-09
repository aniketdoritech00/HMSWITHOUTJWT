
/*------------------------------MODAL JS FOR USER LIST ON THE BASIS OF THEIR ROLES ACTIVITY - SUYASH---------------------------- */
/******* @Author SWATI *******/
const openModalButton = document.getElementById('roleMasterModalButton');
const roleMasterModal = document.getElementById('roleMasterModal');

function openRoleMaster() {

    roleMasterModal.style.display = 'block';
}

function closeRoleMaster() {

    roleMasterModal.style.display = 'none';
}

openModalButton.addEventListener('click', openRoleMaster);

roleMasterModal.addEventListener('click', function(event) {
    if (event.target === roleMasterModal) {
        closeRoleMaster();
    }
});

const closeModalButton = document.querySelector('#roleMasterModal .close');
closeModalButton.addEventListener('click', closeRoleMaster);


/* ------------------------------ LOOP TO ASSIGN MODALS OF MENU DETAILS TO ALL VIEW BUTTONS ---------------------------- */

document.addEventListener('DOMContentLoaded', function() {
    const viewButtons = document.querySelectorAll('.view-button');
    const roleMasterModal = document.getElementById('roleMasterModal');

    function openRoleMaster() {
        roleMasterModal.style.display = 'block';
    }

    function closeRoleMaster() {
        roleMasterModal.style.display = 'none';
    }

    function handleViewButtonClick() {
        openRoleMaster();
    }

    function handleModalClick(event) {
        if (event.target === roleMasterModal) {
            closeRoleMaster();
        }
    }

    function handleCloseButtonClick() {
        closeRoleMaster();
    }

    viewButtons.forEach(button => {
        button.addEventListener('click', handleViewButtonClick);
    });

    roleMasterModal.addEventListener('click', handleModalClick);

    const closeModalButton = document.querySelector('#roleMasterModal .close');
    closeModalButton.addEventListener('click', handleCloseButtonClick);
});

/********************** POPULATE THE MENU DETAILS BY EMPLOYEE ID *************************/
//$('.card-header h5').text("Menu from Menu Master - " + empRole);
function loadMenuData(empRole) {
$('.card-header h5').text("Menu Master for " + empRole.toUpperCase());
    $.ajax({
        type: 'GET',
        url: 'getAllMenuDetails',
        dataType: 'json',
        success: function (allMenuResponse) {
            try {
                $('form[name="roleMaster"] tbody').empty();

                $.ajax({
                    type: 'GET',
                    url: 'getMenuDetailsByRole',
                    data: { empRole: empRole },
                    dataType: 'json',
                    success: function (roleMenuResponse) {
                        try {
                            // Loop through allMenuResponse and compare with roleMenuResponse
                            $.each(allMenuResponse.payload, function (index, menu) {
                                var isAssigned = false;

                                // Check if menuId is present in roleMenuResponse
                                for (var i = 0; i < roleMenuResponse.payload.length; i++) {
                                    if (menu.id === roleMenuResponse.payload[i].id) {
                                        isAssigned = true;
                                        break;
                                    }
                                }

                                var actionButton;
                                var buttonBackgroundColor;

                                if (isAssigned) {
                                    actionButton = '<button type="button" class="submit-btn toggle-action revoke" style="font-size:12px; background-color: #C70039;">Revoke</button>';
                                } else {
                                    actionButton = '<button type="button" class="submit-btn toggle-action assign" style="font-size:12px; background-color: #028bd7;">Assign</button>';
                                }

                                var newRow = $('<tr>' +
                                    '<td>' + menu.id + '</td>' +
                                    '<td>' + menu.menuName + '</td>' +
                                    '<td>' + menu.menuHandlerName + '</td>' +
                                    '<td>' + menu.menuIcon + '</td>' +
                                    '<td>' + actionButton + '</td>' +
                                    '</tr>');

                                newRow.find('.toggle-action.assign').on('click', function () {
                                    var menuId = menu.id;
                                    confirmAction("Assign", function() {
                                        assignMenu(empRole, menuId, newRow);
                                    });
                                });

                                newRow.find('.toggle-action.revoke').on('click', function () {
                                    var menuId = menu.id;
                                    confirmAction("Revoke", function() {
                                        revokeMenu(empRole, menuId, newRow);
                                    });
                                });

                                $('form[name="roleMaster"] tbody').append(newRow);
                            });
                        } catch (error) {
                            console.error('Error handling roleMenuResponse:', error);
                        }
                    },
                    error: function (error) {
                        console.error('Error loading roleMenu data:', error);
                    }
                });
            } catch (error) {
                console.error('Error handling allMenuResponse:', error);
            }
        },
        error: function (error) {
            console.error('Error loading allMenu data:', error);
        }
    });
}

function confirmAction(actionType, callback) {
    Swal.fire({
        title: `Confirm ${actionType}`,
        text: `Do you really want to ${actionType.toLowerCase()} this menu?`,
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: `Yes, ${actionType.toLowerCase()} it!`
    }).then((result) => {
        if (result.isConfirmed) {
            callback();
        }
    });
}

function assignMenu(empRole, menuId, row) {
    $.ajax({
        type: 'PUT',
        url: 'assignMenuDetailsToRole',
        data: {
            empRole: empRole,
            menuId: menuId
        },
        dataType: 'json',
        success: function (response) {
            console.log('Menu assigned successfully:', response);
            row.find('.toggle-action.assign')
                .removeClass('assign')
                .addClass('revoke')
                .text('Revoke')
                .css('background-color', '#C70039');
            row.find('.toggle-action.revoke').on('click', function () {
                confirmAction("Revoke", function() {
                    revokeMenu(empRole, menuId, row);
                });
            });
        },
        error: function (error) {
            console.error('Error assigning menu:', error);
            Swal.fire({
                icon: 'error',
                title: 'Oops...',
                text: 'An error occurred while assigning the menu!',
            });
        }
    });
}

function revokeMenu(empRole, menuId, row) {
    $.ajax({
        type: 'PUT',
        url: 'revokeMenuDetailsFromTheRole',
        data: {
            empRole: empRole,
            menuId: menuId
        },
        dataType: 'json',
        success: function (response) {
            console.log('Menu revoked successfully:', response);
            row.find('.toggle-action.revoke')
                .removeClass('revoke')
                .addClass('assign')
                .text('Assign')
                .css('background-color', '#028bd7');
            row.find('.toggle-action.assign').off('click').on('click', function () {
                confirmAction("Assign", function() {
                    assignMenu(empRole, menuId, row);
                });
            });
        },
        error: function (error) {
            console.error('Error revoking menu:', error);
            Swal.fire({
                icon: 'error',
                title: 'Oops...',
                text: 'An error occurred while revoking the menu!',
            });
        }
    });
}