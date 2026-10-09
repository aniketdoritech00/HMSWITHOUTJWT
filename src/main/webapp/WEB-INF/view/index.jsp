<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>RTSP Stream Viewer</title>
</head>
<body>
    <h2>Live Video Stream</h2>
    <canvas id="videoCanvas"></canvas>

    <script>
        const socket = new WebSocket("ws://localhost:8080/video-stream");
        const canvas = document.getElementById("videoCanvas");
        const ctx = canvas.getContext("2d");

        socket.binaryType = "arraybuffer"; // Expect binary data

        socket.onmessage = function(event) {
            const arrayBuffer = event.data;
            const blob = new Blob([arrayBuffer], { type: "image/jpeg" });
            const url = URL.createObjectURL(blob);

            const img = new Image();
            img.onload = function() {
                canvas.width = img.width;
                canvas.height = img.height;
                ctx.drawImage(img, 0, 0, img.width, img.height);
                URL.revokeObjectURL(url); // Free memory
            };
            img.src = url;
        };

        socket.onerror = function(error) {
            console.error("WebSocket Error:", error);
        };
    </script>
</body>
</html>
