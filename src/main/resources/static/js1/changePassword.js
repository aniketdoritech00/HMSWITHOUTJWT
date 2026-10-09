function changePassword() {
    // Fetch email from the input field
    var email = $("#emailForUpdatePassword").val();
    var password = $('#newPassword').val();
    var confirmPassword = $('#password').val();

    try {
        // Validate email

        if (!email) {
            Swal.fire({
                icon: 'warning',
                title: 'Email required',
                showConfirmButton: false,

            })
        } else if (password !== confirmPassword) {
            Swal.fire({
                icon: 'warning',
                title: 'Password does not match',


            })
        } else if (password === confirmPassword) {
            // Make AJAX request


                    $.ajax({
                        type: "POST",
                        url: "updatePasswordByEmail",
                        data: { email: email, password: password },
                        dataType: "json",
                        success: function(response) {


                            if (response) {
                                Swal.fire({
                                    icon: 'success',
                                    title: 'Password Updated Successfully!',


                                });
                              const otpMatched = setTimeout(otpVerify, 3000);
                                function otpVerify() {
                                 window.location.href = '/';
                                                          }

                            } else {
                                // Handle server-side errors
                                Swal.fire({
                                    icon: 'error',
                                    title: 'Error updating password',

                                });
                            }
                        },
                        error: function(xhr, status, error) {
                            // Handle error response
                            Swal.fire({
                                icon: 'error',
                                title: 'Something went wrong',
                                text: xhr.responseText || 'Unknown error',
                            });
                        }
                    });
        }
    } catch (error) {
        // Handle validation error
        Swal.fire({
            icon: 'error',
            title: 'Validation Error',
            text: error,
        });
    }


}




