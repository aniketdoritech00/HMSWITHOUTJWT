package com.doritech.api.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

@RestController
@RequestMapping("/api/secure")
public class SecureStreamProxyController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${stream.http.endpoint:http://122.176.136.51:8025/Dahua/video/live}")
    private String streamHttpEndpoint;

    /**
     * HTTPS Proxy endpoint for RTSP streaming
     * Forwards requests to the HTTP endpoint and streams the response
     */
    @GetMapping(value = "/livestream")
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> proxyLivestream(
            @RequestParam String rtspUrl,
            HttpServletResponse response) {
        
        System.out.println("Proxy received RTSP URL: " + rtspUrl);
        
        try {
            // Build the target URL with proper encoding
            String targetUrl = UriComponentsBuilder.fromHttpUrl(streamHttpEndpoint)
                    .queryParam("rtspUrl", rtspUrl)
                    .build()
                    .toUriString();
            
            System.out.println("Proxying to: " + targetUrl);
            
            // Create streaming response body
            StreamingResponseBody streamingBody = outputStream -> {
                try {
                    // Make request to the HTTP endpoint
                    restTemplate.execute(
                        targetUrl,
                        HttpMethod.GET,
                        null,
                        clientHttpResponse -> {
                            System.out.println("Response status: " + clientHttpResponse.getStatusCode());
                            
                            // Copy content type from upstream response
                            MediaType contentType = clientHttpResponse.getHeaders().getContentType();
                            if (contentType != null) {
                                response.setContentType(contentType.toString());
                            }
                            
                            try (InputStream inputStream = clientHttpResponse.getBody()) {
                                byte[] buffer = new byte[8192];
                                int bytesRead;
                                long totalBytes = 0;
                                
                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                    outputStream.write(buffer, 0, bytesRead);
                                    outputStream.flush();
                                    totalBytes += bytesRead;
                                    
                                    if (totalBytes % (1024 * 1024) == 0) {
                                        System.out.println("Streamed: " + (totalBytes / 1024 / 1024) + " MB");
                                    }
                                }
                                System.out.println("Streaming completed. Total bytes: " + totalBytes);
                            }
                            return null;
                        }
                    );
                } catch (Exception e) {
                    System.err.println("Error streaming data: " + e.getMessage());
                    e.printStackTrace();
                    throw new RuntimeException("Streaming failed", e);
                }
            };
            
            // Use multipart/x-mixed-replace for MJPEG or video/mp4 for MP4
            // Let the upstream server determine the content type
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("video/mp4")) // or "multipart/x-mixed-replace;boundary=frame"
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .header(HttpHeaders.CONNECTION, "keep-alive")
                    .header("X-Content-Type-Options", "nosniff")
                    .body(streamingBody);
                    
        } catch (Exception e) {
            System.err.println("Error in proxy controller: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}