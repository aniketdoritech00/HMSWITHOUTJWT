package com.doritech.api.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
public class EmailTemplateLoader {

    private static final Logger log = LoggerFactory.getLogger(EmailTemplateLoader.class);

    private final ServletContext servletContext;

    public EmailTemplateLoader(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public String loadTemplate(String emailBody) {

        if (emailBody == null) {
            log.error("[TEMPLATE LOADER] emailBody is null or blank.");
            return null;
        }

        // Agar emailBody directly HTML hai (legacy)
        if (emailBody.trim().startsWith("<")) {
            log.info("[TEMPLATE LOADER] Inline HTML detected. Using as-is.");
            return emailBody;
        }

        // ── DB wala path ──────────────────────────────────────────────────
        String dbPath = emailBody.trim();
        System.out.println("[TEMPLATE LOADER] Path from DB     : " + dbPath);
        log.info("[TEMPLATE LOADER] Path Loaded from DB     : {}", dbPath);

        // ── ServletContext se real path nikalo ────────────────────────────
        String resourcePath = "/" + dbPath; // e.g. /email-templates/MT-2_escalation_l1.html

        String realPath = servletContext.getRealPath(resourcePath);
        System.out.println("[TEMPLATE LOADER] ServletContext resourcePath : " + resourcePath);
        System.out.println("[TEMPLATE LOADER] ServletContext realPath      : " + realPath);
        log.info("[TEMPLATE LOADER] ServletContext resourcePath : {}", resourcePath);
        log.info("[TEMPLATE LOADER] ServletContext realPath      : {}", realPath);

        // ── Stream lo ─────────────────────────────────────────────────────
        try {
            InputStream is = servletContext.getResourceAsStream(resourcePath);

            if (is == null) {
                System.out.println("[TEMPLATE LOADER] InputStream NULL — file ot found at : " + resourcePath);
                log.error("[TEMPLATE LOADER] InputStream NULL — file not found at : {}", resourcePath);
                return null;
            }

            System.out.println("[TEMPLATE LOADER] InputStream Found — file loading ...");
            log.info("[TEMPLATE LOADER] InputStream Found — file loading ...");

            String content = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            System.out.println("[TEMPLATE LOADER] Content length: " + content.length() + " chars");
            log.info("[TEMPLATE LOADER] Content length: {} chars", content.length());

            return content;

        } catch (Exception e) {
            log.error("[TEMPLATE LOADER] Exception while reading '{}': {}", resourcePath, e.getMessage(), e);
            System.out.println("[TEMPLATE LOADER] Exception: " + e.getMessage());
            return null;
        }
    }
}
