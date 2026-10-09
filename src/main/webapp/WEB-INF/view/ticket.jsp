<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ticket Details</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }

        .ticket {
            background: #ffffff;
            border: 2px dashed #028bd7;
            border-radius: 10px;
            width: 400px;
            padding: 20px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        .ticket-header {
            text-align: center;
            margin-bottom: 20px;
        }

        .ticket-header h1 {
            font-size: 1.5em;
            color: #333;
            margin: 0;
        }

        .ticket-header p {
            font-size: 0.9em;
            color: #777;
            margin: 5px 0 0;
        }

        .ticket-body {
            margin-top: 10px;
        }

        .ticket-body ul {
            list-style: none;
            padding: 0;
        }

        .ticket-body ul li {
            padding: 8px 0;
            border-bottom: 1px solid #ddd;
            font-size: 1em;
        }

        .ticket-body ul li span {
            font-weight: bold;
            color: #333;
        }

        .ticket-footer {
            text-align: center;
            margin-top: 20px;
        }

        .ticket-footer a {
            display: inline-block;
            text-decoration: none;
            background: #4caf50;
            color: #fff;
            padding: 10px 20px;
            border-radius: 5px;
            font-size: 1em;
        }

        .ticket-footer a:hover {
            background: #45a049;
        }
    </style>
</head>
<body>
    <div class="ticket">
        <div class="ticket-header">
            <h1>Alert Ticket</h1>
            <p>Details for your generated ticket</p>
        </div>
        
        <div class="ticket-body">
           <ul>
             <!-- Displaying the ticket details using JSTL -->
               <c:if test="${not empty ticketDetails}">
                   <li><span>Ticket ID:</span> ${ticketDetails.ticketId}</li>
                   <li><span>Branch Code:</span> ${ticketDetails.branchCode}</li>
                   <li><span>Branch Name:</span> ${ticketDetails.branchName}</li>
                   <li><span>Bank Name:</span> ${ticketDetails.bankName}</li>
                   <li><span>Vendor Name:</span> ${ticketDetails.vendorName}</li>
                   <li><span>Alert Type:</span> ${ticketDetails.alertType}</li>
                   
               </c:if>
    </ul>
        </div>
        <div class="ticket-footer">
           <img alt="" src="./logo/digitals.png" width="120">
        </div>
    </div>
</body>
</html>
