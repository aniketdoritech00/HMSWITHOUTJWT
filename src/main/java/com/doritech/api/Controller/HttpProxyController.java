package com.doritech.api.Controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@RequestMapping("/proxy")
public class HttpProxyController {

    private final RestTemplate insecureRestTemplate;
    @Value("${camera.internal-api:http://122.176.136.51:8025}")
    private String cameraInternalApi;

    public HttpProxyController() {
        this.insecureRestTemplate = createInsecureRestTemplate();
    }

    @Value("${chatbot.api.url}")
    private String chatBotApiUrl;

    @Value("${chatbot.download.api.url}")
    private String chatBotDownloadApiUrl;

    @Value("${deviceAlarmReport.download.api.url}")
    private String deviceAlarmReport;

    @Value("${dateRangeAlarmReport.download.api.url}")
    private String dateRangeAlarmReport;

    @Value("${camera.base-url:http://122.176.136.51:8025/Dahua}")
    private String cameraBase;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ---------------- existing endpoints (unchanged) ----------------
    @GetMapping("/proxy")
    public ResponseEntity<JsonNode> proxyGet(@RequestParam("url") String url) {
        try {
            if (!url.startsWith("http://")) {
                return ResponseEntity.badRequest().body(null);
            }

            String response = restTemplate.getForObject(url, String.class);

            JsonNode jsonResponse = objectMapper.readTree(response);

            return ResponseEntity.ok(jsonResponse);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/image")
    public ResponseEntity<?> proxyImage(@RequestParam("url") String url) {
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                return ResponseEntity.badRequest().body("Invalid URL");
            }

            // Get image as byte[]
            byte[] imageBytes = restTemplate.getForObject(url, byte[].class);

            // Encode image to Base64
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // Return as JSON
            return ResponseEntity.ok().body("{\"imageBase64\":\"" + base64Image + "\"}");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching image: " + e.getMessage());
        }
    }

    @PostMapping("/ask")
    public ResponseEntity<String> proxyAsk(@RequestBody Map<String, String> input) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(input, headers);

            ResponseEntity<String> response = insecureRestTemplate.postForEntity(chatBotApiUrl, request, String.class);

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    private RestTemplate createInsecureRestTemplate() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            HttpComponentsClientHttpRequestFactory factory =
                    new HttpComponentsClientHttpRequestFactory(HttpClients.custom()
                            .setSSLContext(sslContext)
                            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                            .build());

            return new RestTemplate(factory);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create insecure RestTemplate", e);
        }
    }

    @GetMapping("/download/excel/{code}")
    public ResponseEntity<byte[]> downloadExcel(@PathVariable("code") String code) {
        try {
            String excelDownloadUrl = chatBotDownloadApiUrl + code;

            byte[] fileBytes = insecureRestTemplate.getForObject(excelDownloadUrl, byte[].class);

            if (fileBytes == null || fileBytes.length == 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename(code + ".xlsx")
                    .build());

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping(value = "/download/excel/deviceAlarms/{branchCode}", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> downloadDeviceAlarmsExcel(@PathVariable String branchCode) {
        try {
            String excelDownloadUrl = deviceAlarmReport  + branchCode;
            byte[] fileBytes = insecureRestTemplate.getForObject(excelDownloadUrl, byte[].class);
            if (fileBytes == null || fileBytes.length == 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("device_alarms_" + branchCode + ".xlsx")
                    .build());
            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping(value = "/download/dateRange/excel/{branchCode}", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> downloadResponseTimeExcel(@PathVariable String branchCode,
                                                            @RequestParam(required = false) String fromDate,
                                                            @RequestParam(required = false) String toDate) {
        try {
            StringBuilder excelDownloadUrl = new StringBuilder(dateRangeAlarmReport)
                    .append("?branchCode=").append(URLEncoder.encode(branchCode, StandardCharsets.UTF_8));

            if (fromDate != null && !fromDate.isEmpty()) {
                excelDownloadUrl.append("fromDate=").append(URLEncoder.encode(fromDate, StandardCharsets.UTF_8)).append("&");
            }

            if (toDate != null && !toDate.isEmpty()) {
                excelDownloadUrl.append("toDate=").append(URLEncoder.encode(toDate, StandardCharsets.UTF_8));
            }

            byte[] fileBytes = insecureRestTemplate.getForObject(excelDownloadUrl.toString(), byte[].class);

            if (fileBytes == null || fileBytes.length == 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("response_time_" + branchCode + ".xlsx")
                    .build());

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ----------------- HLS proxy endpoints (added) -----------------

    /**
     * Proxy any HLS file under /proxy/hls/{channel}/...
     * Example:
     *  - /proxy/hls/102/stream.m3u8
     *  - /proxy/hls/102/segment0.ts
     */
    @GetMapping("/hls/{channel}/{file:.+}")
    public ResponseEntity<StreamingResponseBody> proxyHlsFile(
            @PathVariable String channel,
            @PathVariable String file,
            HttpServletRequest request
    ) {
        String upstreamUrl = String.format("%s/hls/%s/%s", cameraBase.endsWith("/") ? cameraBase.substring(0, cameraBase.length()-1) : cameraBase, channel, file);

        String incomingRange = request.getHeader("Range");
        HttpHeaders responseHeaders = new HttpHeaders();

        // Try HEAD to copy headers like content-type, content-length, accept-ranges, cache-control
        try {
            HttpHeaders forward = new HttpHeaders();
            if (incomingRange != null) forward.add("Range", incomingRange);
            HttpEntity<Void> headReq = new HttpEntity<>(forward);
            ResponseEntity<Void> headResp = insecureRestTemplate.exchange(upstreamUrl, HttpMethod.HEAD, headReq, Void.class);
            HttpHeaders upstreamHead = headResp.getHeaders();
            if (upstreamHead.getContentType() != null) responseHeaders.setContentType(upstreamHead.getContentType());
            List<String> cl = upstreamHead.get(HttpHeaders.CONTENT_LENGTH);
            if (cl != null && !cl.isEmpty()) responseHeaders.set(HttpHeaders.CONTENT_LENGTH, cl.get(0));
            if (upstreamHead.containsKey(HttpHeaders.ACCEPT_RANGES)) {
                responseHeaders.set(HttpHeaders.ACCEPT_RANGES, upstreamHead.getFirst(HttpHeaders.ACCEPT_RANGES));
            }
            if (upstreamHead.containsKey(HttpHeaders.CACHE_CONTROL)) {
                responseHeaders.set(HttpHeaders.CACHE_CONTROL, upstreamHead.getFirst(HttpHeaders.CACHE_CONTROL));
            } else {
                if (file.toLowerCase().endsWith(".m3u8")) {
                    responseHeaders.setCacheControl(CacheControl.noCache());
                }
            }
        } catch (Exception e) {
            // HEAD may fail — continue to stream GET
        }

        StreamingResponseBody streamingBody = (OutputStream out) -> {
            RequestCallback reqCallback = clientHttpRequest -> {
                if (incomingRange != null) {
                    clientHttpRequest.getHeaders().add("Range", incomingRange);
                }
            };

            ResponseExtractor<Void> responseExtractor = clientHttpResponse -> {
                try (InputStream upstreamStream = clientHttpResponse.getBody()) {
                    StreamUtils.copy(upstreamStream, out);
                    out.flush();
                }
                return null;
            };

            insecureRestTemplate.execute(upstreamUrl, HttpMethod.GET, reqCallback, responseExtractor);
        };

        if (responseHeaders.getContentType() == null && file.toLowerCase().endsWith(".m3u8")) {
            responseHeaders.setContentType(MediaType.parseMediaType("application/vnd.apple.mpegurl"));
        }

        HttpStatus status = (incomingRange != null) ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK;
        return new ResponseEntity<>(streamingBody, responseHeaders, status);
    }

    /**
     * Forward start/stop commands through backend:
     * GET /proxy/stream/start?channel=102
     * GET /proxy/stream/stop?channel=102
     */
    @GetMapping("/stream/start")
    public ResponseEntity<String> startCameraStream(@RequestParam String channel) {
        String url = String.format("%s/streamFFMPEG/start?channel=%s", cameraBase, channel);
        try {
            ResponseEntity<String> resp = insecureRestTemplate.getForEntity(url, String.class);
            return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Failed to start camera stream: " + ex.getMessage());
        }
    }

    @GetMapping("/stream/stop")
    public ResponseEntity<String> stopCameraStream(@RequestParam String channel) {
        String url = String.format("%s/streamFFMPEG/stop?channel=%s", cameraBase, channel);
        try {
            ResponseEntity<String> resp = insecureRestTemplate.getForEntity(url, String.class);
            return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Failed to stop camera stream: " + ex.getMessage());
        }
    }

    
    @GetMapping("/camera/liveStream")
    public void proxyLiveStream(
            @RequestParam String channel,
            @RequestParam String panelSerialNo,
            @RequestParam(required = false, defaultValue = "main") String type,
            HttpServletResponse response) {

        String internalUrl = String.format(
            "%s/Dahua/api/camera/liveStrem?channel=%s&panelSerialNo=%s&type=%s",
            cameraInternalApi, channel, panelSerialNo, type
        );

        try {
            // Create headers for the internal request
            HttpHeaders headers = new HttpHeaders();
            headers.set("accept", "*/*");

            // Stream the response using RequestCallback and ResponseExtractor
            RequestCallback requestCallback = clientHttpRequest -> {
                clientHttpRequest.getHeaders().addAll(headers);
            };

            ResponseExtractor<Void> responseExtractor = clientHttpResponse -> {
                // Copy headers from internal response
                HttpHeaders upstreamHeaders = clientHttpResponse.getHeaders();
                
                // Set Content-Type
                if (upstreamHeaders.getContentType() != null) {
                    response.setContentType(upstreamHeaders.getContentType().toString());
                } else {
                    // Default for video streams
                    response.setContentType("video/mp4");
                }

                // Set Content-Length if available
                long contentLength = upstreamHeaders.getContentLength();
                if (contentLength > 0) {
                    response.setContentLengthLong(contentLength);
                }

                // Copy other important headers
                upstreamHeaders.forEach((name, values) -> {
                    if (!name.equalsIgnoreCase("Transfer-Encoding")) {
                        values.forEach(value -> response.addHeader(name, value));
                    }
                });

                // Stream the response body
                try (InputStream inputStream = clientHttpResponse.getBody();
                     OutputStream outputStream = response.getOutputStream()) {
                    
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        outputStream.flush();
                    }
                }
                
                return null;
            };

            // Execute the streaming request
            insecureRestTemplate.execute(internalUrl, HttpMethod.GET, requestCallback, responseExtractor);

        } catch (Exception e) {
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Failed to proxy video stream: " + e.getMessage() + "\"}");
            } catch (Exception ex) {
                // If we can't write error response, log it
                ex.printStackTrace();
            }
        }
    }

    /**
     * Alternative implementation using StreamingResponseBody
     * GET /proxy/camera/liveStreamAlt?channel=2&panelSerialNo=1&type=main
     */
    @GetMapping("/camera/liveStreamAlt")
    public ResponseEntity<StreamingResponseBody> proxyLiveStreamAlt(
            @RequestParam String channel,
            @RequestParam String panelSerialNo,
            @RequestParam(required = false, defaultValue = "main") String type) {

        String internalUrl = String.format(
            "%s/Dahua/api/camera/liveStrem?channel=%s&panelSerialNo=%s&type=%s",
            cameraInternalApi, channel, panelSerialNo, type
        );

        HttpHeaders responseHeaders = new HttpHeaders();

        StreamingResponseBody streamingBody = outputStream -> {
            RequestCallback requestCallback = clientHttpRequest -> {
                clientHttpRequest.getHeaders().set("accept", "*/*");
            };

            ResponseExtractor<Void> responseExtractor = clientHttpResponse -> {
                // Get content type from upstream
                HttpHeaders upstreamHeaders = clientHttpResponse.getHeaders();
                MediaType contentType = upstreamHeaders.getContentType();
                
                if (contentType != null) {
                    responseHeaders.setContentType(contentType);
                }

                // Stream data
                try (InputStream inputStream = clientHttpResponse.getBody()) {
                    StreamUtils.copy(inputStream, outputStream);
                    outputStream.flush();
                }
                
                return null;
            };

            insecureRestTemplate.execute(internalUrl, HttpMethod.GET, requestCallback, responseExtractor);
        };

        // Set default content type if not set
        if (responseHeaders.getContentType() == null) {
            responseHeaders.setContentType(MediaType.parseMediaType("video/mp4"));
        }

        return new ResponseEntity<>(streamingBody, responseHeaders, HttpStatus.OK);
    }
    
}
