
async function fetchNotifications() {
    const branchCode = $("#branchId").val();

    try {
        const response = await fetch(`getGuardNgLogsByBranchCode?branchCode=${branchCode}`);
        const result = await response.json();

        if (result.statusCode === 200 && result.payload) {
            // Transform API data into the required format
            const notifications = result.payload.map(item => {
                const timestamp = new Date(item.requestTimestamp);
                const date = timestamp.toLocaleDateString("en-GB", {
                    day: '2-digit', month: 'long', year: 'numeric'
                });
                const time = timestamp.toLocaleTimeString("en-US", {
                    hour: '2-digit', minute: '2-digit'
                });

                // Function to convert text to sentence case
                function toSentenceCase(text) {
                    return text.charAt(0).toUpperCase() + text.slice(1).toLowerCase();
                }

                // Construct the message with sentence case and bold formatting
                const message = `User <b>${toSentenceCase(item.userName)}</b> pressed the <b>${toSentenceCase(item.command)}</b> button at <b>${toSentenceCase(item.branchName)}</b>.`;

                return {
                    timestamp,
                    date,
                    time,
                    message,
                    icon: "fas fa-exclamation-circle" // Default icon
                };
            });

            // Sort notifications by timestamp in descending order
            notifications.sort((a, b) => b.timestamp - a.timestamp);

            // Render notifications
            renderNotifications(notifications);
        } else {
            console.error("Failed to fetch notifications:", result.message);
        }
    } catch (error) {
        console.error("Error fetching notifications:", error);
    }
}

// Function to render notifications into the container
function renderNotifications(data) {
    const notificationContainer = document.getElementById("notification-container");
    notificationContainer.innerHTML = ""; // Clear existing notifications
    let currentSection = "";

    data.forEach(notification => {
        if (currentSection !== notification.date) {
            currentSection = notification.date;
            const dateHeader = document.createElement("div");
            dateHeader.className = "notification-date";
            dateHeader.innerText = currentSection;
            notificationContainer.appendChild(dateHeader);
        }

        const notificationCard = document.createElement("div");
        notificationCard.className = "notification-card";
        notificationCard.innerHTML = `<div class="notification-header">
            <span class="icon"><i class="${notification.icon}" style="color:#028bd7;"></i></span> 
            <span class="notification-time">${notification.time}</span>
          </div>
          <p>${notification.message}</p>
        `;
        notificationContainer.appendChild(notificationCard);
    });
}

// Auto-refresh notifications every 5 seconds (5000ms)
$(document).ready(function() {
    fetchNotifications(); // Initial call
    setInterval(fetchNotifications, 5000);
});

