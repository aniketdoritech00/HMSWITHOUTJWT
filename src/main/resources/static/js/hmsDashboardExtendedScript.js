



/*---------------------------------------------------------------HMS CHATBOT JS START------------------------------------------------------------------------------*/

document.addEventListener('DOMContentLoaded', function() {
	const chatToggleBtn = document.getElementById('chatToggleBtn');
	const chatbotWrapper = document.getElementById('chatbotWrapper');
	const minimizeChatBtn = document.getElementById('minimizeChatBtn');
	const chatMessages = document.getElementById('chatMessages');
	const userMessageInput = document.getElementById('userMessageInput');
	const sendMessageBtn = document.getElementById('sendMessageBtn');
	const quickActionBtns = document.querySelectorAll('.quick-actions .btn');

	let typingDiv = null;

	// Mapping button text to API parameters

const buttonMappings = {
    'Closed-Circuit Television (CCTV)': 'CCTV',
    'Security Alarm System (SAS)': 'Security Alarm',
    'Fire Alarm System (FAS)': 'Fire Alarm',
    'Biometric Access Control System (BACS)': 'BACS',
    'Electronic Time Lock (ETL)': 'ETL'
};


	chatToggleBtn.addEventListener('click', function() {
		chatbotWrapper.classList.toggle('active');
	});

	minimizeChatBtn.addEventListener('click', function() {
		chatbotWrapper.classList.remove('active');
	});

	userMessageInput.addEventListener('keypress', function(e) {
		if (e.key === 'Enter') {
			handleUserMessage();
		}
	});

	sendMessageBtn.addEventListener('click', handleUserMessage);


quickActionBtns.forEach(btn => {
    btn.addEventListener('click', async function() {
        const zoneType = this.textContent.trim();
        const parameter = buttonMappings[zoneType] || zoneType;
        var loggedInBranchCode = sessionStorage.getItem("branch");
        //alert(loggedInBranchCode)
        if (zoneType === 'Other') {
            addMessage('Ask your query in the message box.', 'bot');
            return;
        }

        addMessage(`Fetching status for ${parameter}...`, 'bot');
        showTypingIndicator();

        try {
          const apiResponse = await fetch(`./getCountsByZoneStatus?branchCode=${loggedInBranchCode}`);
            if (!apiResponse.ok) {
                throw new Error(`API request failed with status ${apiResponse.status}`);
            }

            const data = await apiResponse.json();
            removeTypingIndicator();

           
if (data.statusCode === 200 && data.payload[parameter]) {
    const zoneData = data.payload[parameter];
    const total = zoneData.Working + zoneData['Not Working'] + zoneData['Partially Working'];

    const message = `
        <p><strong>${zoneType} Status:</strong></p>
        <ul>
            <li>Total: ${total}</li>
            <li>Working: ${zoneData.Working}</li>
            <li>Not Working: ${zoneData['Not Working']}</li>
            <li>Partially Working: ${zoneData['Partially Working']}</li>
            <li class="mt-1">
            <div style="display: flex; flex-direction: column; gap: 10px;">
                <p style="color:#7F8CAA">If you want any other information.<br> 
                Click Here : <b id="otherButton" style="cursor:pointer;">Other Info</b></p>
            </li>
            </div>
        </ul>
    `;

    addMessage(message, 'bot');

    // Attach event listener to the "Other" button
    document.getElementById('otherButton').addEventListener('click', function() {
        addMessage('Ask your query in the message box.', 'bot');
    });
}
 else {
                addMessage(`No data available for ${parameter}.`, 'bot');
            }
        } catch (error) {
            removeTypingIndicator();
            addMessage("Failed to fetch data. Please try again later.", 'bot');
            console.error('Error fetching data:', error);
        }
    });
});







	async function handleUserMessage() {
		const message = userMessageInput.value.trim();
		if (!message) return;

		addMessage(message, 'user');
		userMessageInput.value = '';
		showTypingIndicator();

		try {
			const botResponse = await getBotResponse(message);
			removeTypingIndicator();
			addMessage(botResponse, 'bot');
		} catch (error) {
			removeTypingIndicator();
			addMessage("Sorry, I couldn't process your request. Please try again later.", 'bot');
			console.error('Bot error:', error);
		}
	}

	
function addMessage(message, sender) {
    const messageDiv = document.createElement('div');
    messageDiv.className = sender === 'user' ? 'user-message' : 'bot-message';

    const messageContent = document.createElement('div');
    messageContent.className = 'message-content';
    messageContent.innerHTML = message;

    const messageTime = document.createElement('span');
    messageTime.className = 'message-time';
    messageTime.textContent = getCurrentTime();

    messageDiv.appendChild(messageContent);
    messageDiv.appendChild(messageTime);
    chatMessages.appendChild(messageDiv);


    messageDiv.scrollIntoView({ behavior: 'smooth', block: 'end' });
}

	function getCurrentTime() {
		const now = new Date();
		let hours = now.getHours();
		const minutes = now.getMinutes().toString().padStart(2, '0');
		const ampm = hours >= 12 ? 'PM' : 'AM';
		hours = hours % 12 || 12;
		return `${hours}:${minutes} ${ampm}`;
	}

	function showTypingIndicator() {
		if (!typingDiv) {
			typingDiv = document.createElement('div');
			typingDiv.className = 'bot-message typing-indicator';
			typingDiv.innerHTML = '<div class="dots"><span></span><span></span><span></span></div>';
			chatMessages.appendChild(typingDiv);
			chatMessages.scrollTop = chatMessages.scrollHeight;
		}
		 typingDiv.scrollIntoView({ behavior: 'smooth', block: 'end' });

	}

	function removeTypingIndicator() {
		if (typingDiv && chatMessages.contains(typingDiv)) {
			chatMessages.removeChild(typingDiv);
			typingDiv = null;
		}
	}
async function getBotResponse(input) {
		
		const branchCode = sessionStorage.getItem("branch");
		
    const apiUrl = './proxy/ask';
    const requestBody = { 
	 query: input,
     branchCode: branchCode };

    try {
        const response = await fetch(apiUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody)
        });

        if (!response.ok) {
            throw new Error('API request failed');
        }

        const responseText = await response.text();
        let data;
        try {
            data = JSON.parse(responseText);
        } catch (error) {
            return responseText.replace(/^Result:\s*/, '');
        }

        if (!data || !data.response) {
            throw new Error('Invalid response structure');
        }

        return data.response.replace(/^Result:\s*/, '');
    } catch (error) {
        console.error('Bot API error:', error);
		return data.response.replace(/^Result:\s*/, '');
        //return getFallbackResponse(input);
    }
}


});



/*---------------------------------------------------------------HMS CHATBOT JS END------------------------------------------------------------------------------*/






/*---------------------------------------------------------------AI POP UP JS START------------------*/


window.addEventListener('load', function() {

    const shouldShowPopup = sessionStorage.getItem("showChatbotPopup");
    
    if (shouldShowPopup === "true") {
 
        sessionStorage.removeItem("showChatbotPopup");
        
        const popup = document.getElementById('aiPopupOverlay');
        const closeBtn = document.getElementById('closePopup');

        if (popup && closeBtn) {
            setTimeout(() => {
                popup.classList.add('show');
            }, 500);

            const autoHideTimer = setTimeout(() => {
                hidePopup();
            }, 7000);

            // Close button functionality
            closeBtn.addEventListener('click', () => {
                clearTimeout(autoHideTimer);
                hidePopup();
            });

            // Close on overlay click
            popup.addEventListener('click', (e) => {
                if (e.target === popup) {
                    clearTimeout(autoHideTimer);
                    hidePopup();
                }
            });

            // Hide popup function
            function hidePopup() {
                popup.classList.remove('show');
                setTimeout(() => {
                    popup.style.display = 'none';
                }, 300);
            }

            // Prevent popup from closing when clicking inside
            const popupContent = document.querySelector('.ai-popup');
            if (popupContent) {
                popupContent.addEventListener('click', (e) => {
                    e.stopPropagation();
                });
            }
        }
    }
});