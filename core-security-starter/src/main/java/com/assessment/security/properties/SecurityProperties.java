package com.assessment.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for JWT security.
 * Allows external configuration via application.yml/properties.
 * 
 * Example configuration:
 * security.jwt.secret=your-secret-key
 * security.jwt.expiration=86400000
 */
@ConfigurationProperties(prefix = "security.jwt")
public class SecurityProperties {

    /**
     * Secret key used for JWT signing and validation.
     * Should be at least 256 bits for HS256 algorithm.
     */
    private String secret = "default-secret-key-change-in-production-must-be-at-least-256-bits";

    /**
     * JWT token expiration time in milliseconds Default: 24 hours (86400000 ms)
     */
    private long expiration = 86400000;

    private boolean enableRequestLogging = true;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public boolean isEnableRequestLogging() {
        return enableRequestLogging;
    }

    public void setEnableRequestLogging(boolean enableRequestLogging) {
        this.enableRequestLogging = enableRequestLogging;
    }
}
