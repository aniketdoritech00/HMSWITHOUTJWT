const mobileScreen = window.matchMedia("(max-width: 600px )");
$(document).ready(function () {
   
    $(".menu-toggle").click(function () {
        if (mobileScreen.matches) {
            $(".dashboard-nav").toggleClass("mobile-show");
        } else {
            $(".dashboard").toggleClass("dashboard-compact");
        }
    });
});


  
  /*-----------------------------------------IFRAME RESPONSIVE FUNCTION-------------------------------------------------------------*/
  
  
   function adjustIframeHeight() {
    var iframe = document.getElementById('adminRight');
    if (iframe) {
      var windowHeight = window.innerHeight ||
        document.documentElement.clientHeight ||
        document.body.clientHeight;

      
      iframe.style.height = (windowHeight-100) + 'px';
    }
  }

  
  adjustIframeHeight();
  window.addEventListener('resize', adjustIframeHeight);
  


  /**************** DYNAMIC MENU-BAR ******************/
  
 $(document).ready(function () {
    // Toggle submenu visibility
    $('.with-submenu').on('click', function (event) {
        event.preventDefault(); // Prevent default anchor click behavior

        // Close all other submenus
        $('.submenu-container .submenu').slideUp();

        // Toggle the clicked submenu
        var submenu = $(this).siblings('.submenu');
        if (submenu.is(':visible')) {
            submenu.slideUp();
        } else {
            submenu.slideDown();
        }
    });

    // Close the quixnav on menu or submenu click for mobile/tablet screens
    $('.submenu-item').on('click', function () {
        if ($(window).width() <= 768) { // For mobile/tablet screens
            $('.quixnav').fadeOut(); // Or use slideUp() for animation
        }
    });

    // Optionally, add a toggle button to open/close the quixnav on smaller screens
    $('.menu-toggle').on('click', function () {
        if ($(window).width() <= 768) { // Apply toggle only for mobile/tablet screens
            $('.quixnav').fadeToggle(); // Or use slideToggle() for animation
        }
    });
});

// Hamburger button toggle for menu on mobile/tablet screens only
const hamburger = document.querySelector('.arrow');
hamburger.addEventListener('click', () => {
	hamburger.classList.toggle('isactive');
    if ($(window).width() <= 768) { // Only for mobile/tablet screens
        hamburger.classList.toggle('isactive'); // Toggle class for animation

        // Show quixnav when the hamburger button is clicked on mobile/tablet
        if ($('.quixnav').is(':visible')) {
           
        } else {
            $('.quixnav').fadeIn();  // Show if hidden
        }
    }
});



 
 function confirmLogout() {
	   Swal.fire({
	     title: "Are you sure?",
	     text: "You are about to logout!",
	     icon: "warning",
	     showCancelButton: true,
	     cancelButtonColor: "#b20600",
	     confirmButtonColor: "#000",
	     confirmButtonText: "Log Out",
	     cancelButtonText: "Cancel",
	     allowOutsideClick: () => !Swal.isLoading(),
	     preConfirm: async () => {
	       try {
	         Swal.showLoading();
	         await logoutAndRedirect();
	         return true; 
	       } catch (error) {
	         Swal.hideLoading();
	         Swal.fire("Error", "An error occurred while logging out", "error");
	         return false;  
	       }
	     },
	   }).then((result) => {
	     if (result.value) { 
	       Swal.fire("Log Out!", "You have been successfully logged out", "success");
	     } else if (result.dismiss === Swal.DismissReason.cancel) {
	       Swal.fire("Cancelled", "Log Out cancelled", "error");
	     }
	   });
	 }

	 async function logoutAndRedirect() {
	   await new Promise(resolve => setTimeout(resolve, 1000));
	   window.location.href = "/"; 
	 }


