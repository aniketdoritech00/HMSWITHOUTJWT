
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Live Stream</title>
    <link href="https://vjs.zencdn.net/7.11.4/video-js.css" rel="stylesheet" />
    <style>
        #video-container {
            display: flex;
            flex-wrap: wrap;
            justify-content: space-between;
        }
        .video-item {
            flex: 1 1 25%;
            margin: 5px;
            text-align: center;
        }
        .video-item video {
            border: 2px solid white;
            width: 100%;
            height: auto;
        }
        .video-name {
            color: white;
            margin-top: 5px;
        }
    </style>
</head>
<body style="background-color:black">
    <div id="video-container">
        <!-- Video elements will be dynamically generated -->
    </div>
    <script src="https://vjs.zencdn.net/7.11.4/video.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/hls.js@latest"></script>
    <script>

    document.addEventListener('DOMContentLoaded', function() {
        var videoContainer = document.getElementById('video-container');
        var output = [
        	{ name: 'Camera 1', url: 'http://192.168.1.16:9090/videos/output.m3u8' },
            { name: 'Camera 2', url: 'http://192.168.1.16:9090/videos/stream1.m3u8' },
            { name: 'Camera 3', url: 'http://192.168.1.16:9090/videos/stream3.m3u8' },
            
        ];

        // Dynamically generate video elements for each stream
        output.forEach(function(stream, index) {
            var videoWrapper = document.createElement('div');
            videoWrapper.className = 'video-item';

            var video = document.createElement('video');
            video.className = 'video-js vjs-default-skin';
            video.controls = true;
            video.preload = 'auto';
            video.loop = true;

            if (Hls.isSupported()) {
                var hls = new Hls();
                hls.loadSource(stream.url);
                hls.attachMedia(video);
                hls.on(Hls.Events.MANIFEST_PARSED, function() {
                    video.play();
                });
            } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
                video.src = stream.url;
                video.addEventListener('loadedmetadata', function() {
                    video.play();
                });
            }

            var videoName = document.createElement('div');
            videoName.className = 'video-name';
            videoName.textContent = stream.name;

            videoWrapper.appendChild(video);
            videoWrapper.appendChild(videoName);
            videoContainer.appendChild(videoWrapper);
        });
    });

    </script>
</body>
</html>

<!-- ffmpeg -loglevel debug -i "rtsp://admin:admin%40123@203.145.171.162:556/cam/realmonitor?channel=1&subtype=1" -c:v libx264 -c:a aac -f hls -hls_time 2 -hls_list_size 3 -hls_flags delete_segments -hls_segment_type mpegts C:\\Users\\dell\\Downloads\\stream1.m3u8
 -->