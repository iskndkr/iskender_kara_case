package com.iskender.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for API testing operations
 * Contains reusable helper methods for API test automation
 */
public class ApiTestUtils {
    
    private static final Logger logger = LogManager.getLogger(ApiTestUtils.class);
    
    /**
     * Generates a unique pet ID based on current timestamp
     * @return unique integer ID within integer range
     */
    public static int generateUniquePetId() {
        return (int)(System.currentTimeMillis() % Integer.MAX_VALUE);
    }
    
    /**
     * Creates JSON payload for pet creation/update operations
     * @param id pet ID
     * @param name pet name
     * @param status pet status (available, pending, sold)
     * @return JSON string payload
     */
    public static String createPetPayload(int id, String name, String status) {
        return "{"
            + "\"id\": " + id + ","
            + "\"category\": {"
            + "\"id\": 1,"
            + "\"name\": \"Dogs\""
            + "},"
            + "\"name\": \"" + name + "\","
            + "\"photoUrls\": ["
            + "\"https://example.com/photo1.jpg\""
            + "],"
            + "\"tags\": ["
            + "{"
            + "\"id\": 1,"
            + "\"name\": \"friendly\""
            + "}"
            + "],"
            + "\"status\": \"" + status + "\""
            + "}";
    }
    
    /**
     * Logs test step with standardized format
     * @param step the test step description
     */
    public static void logStep(String step) {
        logger.info("STEP: " + step);
    }
    
    /**
     * Logs informational message
     * @param message the information message
     */
    public static void logInfo(String message) {
        logger.info(message);
    }
    
    /**
     * Creates a simple pet payload with minimal data
     * @param name pet name only
     * @return JSON string with name only
     */
    public static String createMinimalPetPayload(String name) {
        return "{\"name\": \"" + name + "\"}";
    }
    
    /**
     * Creates malformed JSON for negative testing
     * @return malformed JSON string
     */
    public static String createMalformedJsonPayload() {
        return "{\"id\": 123, \"name\": \"test\", \"invalid\": }";
    }
    
    /**
     * Creates JSON payload with long ID for boundary testing
     * @param id large ID value (beyond integer range)
     * @param name pet name
     * @param status pet status
     * @return JSON string payload with large ID
     */
    public static String createLargeIdPetPayload(long id, String name, String status) {
        return "{"
            + "\"id\": " + id + ","
            + "\"category\": {"
            + "\"id\": 1,"
            + "\"name\": \"Dogs\""
            + "},"
            + "\"name\": \"" + name + "\","
            + "\"photoUrls\": ["
            + "\"https://example.com/photo1.jpg\""
            + "],"
            + "\"tags\": ["
            + "{"
            + "\"id\": 1,"
            + "\"name\": \"friendly\""
            + "}"
            + "],"
            + "\"status\": \"" + status + "\""
            + "}";
    }
}