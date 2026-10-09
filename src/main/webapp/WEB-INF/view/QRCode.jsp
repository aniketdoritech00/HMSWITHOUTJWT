
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>QR Code Example</title>
    <style>
        .center {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
    </style>
</head>
<body>
    <h1>Generated QR Code</h1>
    <div class="center">
        <img src="./generateQRCode?url=http://122.160.60.151:9090/HMS/RegistrationForm&branchCode=${branchCode}" alt="QR Code">
    </div>
</body>
</html>
