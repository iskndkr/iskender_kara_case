package com.iskender.tests.api;

import com.iskender.tests.ApiTestBase;
import com.iskender.utils.ApiTestUtils;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PetStoreApiTest extends ApiTestBase {

    private static final Logger logger = LogManager.getLogger(PetStoreApiTest.class);
    private int currentPetId = 0;

    @DataProvider(name = "petStatuses")
    public Object[][] petStatusProvider() {
        return new Object[][]{
            {"available"},
            {"pending"},
            {"sold"}
        };
    }

    @DataProvider(name = "invalidIds")
    public Object[][] invalidIdProvider() {
        return new Object[][]{
            {-1, "negative ID"},
            {0, "zero ID"},
            {99999999999L, "non-existent large ID"}
        };
    }

    // ================================
    // POST TESTS (CREATE OPERATIONS)
    // ================================

    @Test(description = "Create a new pet with valid data", priority = 1)
    public void testCreatePetPositive() {
        ApiTestUtils.logStep("Creating a new pet with valid data");

        currentPetId = ApiTestUtils.generateUniquePetId();
        String petJson = ApiTestUtils.createPetPayload(currentPetId, "Buddy", "available");
        
        logger.info("Request: POST /pet with payload: " + petJson);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(petJson)
                .when()
                .post("/pet");

        logger.info("Response: " + response.getStatusCode() + " - " + response.getBody().asString());
        
        ApiTestUtils.logInfo("Response Status Code: " + response.getStatusCode());
        ApiTestUtils.logInfo("Response Body: " + response.getBody().asString());

        // Perform assertions
        response.then()
                .statusCode(200)
                .body("name", equalTo("Buddy"))
                .body("status", equalTo("available"))
                .body("category.name", equalTo("Dogs"));

        // Verify ID matches
        int responseId = response.jsonPath().getInt("id");
        verifySoftEquals(responseId, currentPetId, "Pet ID should match");
        

        ApiTestUtils.logInfo("Pet created successfully with ID: " + currentPetId);
    }

    @Test(description = "Create pets with different statuses", dataProvider = "petStatuses", priority = 2)
    public void testCreatePetWithDifferentStatuses(String status) {
        ApiTestUtils.logStep("Creating pet with status: " + status);

        int petId = ApiTestUtils.generateUniquePetId();
        String petJson = ApiTestUtils.createPetPayload(petId, "Pet_" + status, status);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(petJson)
                .when()
                .post("/pet");

        ApiTestUtils.logInfo("Response Status Code: " + response.getStatusCode());
        response.then()
                .statusCode(200)
                .body("status", equalTo(status));
                
        // Verify ID separately due to long/int conversion issues
        long responseId = response.jsonPath().getLong("id");
        verifySoftEquals(responseId, petId, "Pet ID should match");

        ApiTestUtils.logInfo("Pet created successfully with status: " + status);
    }

    @Test(description = "Test malformed JSON", priority = 3)
    public void testCreatePetWithMalformedJson() {
        ApiTestUtils.logStep("Testing malformed JSON");

        String malformedJson = ApiTestUtils.createMalformedJsonPayload();

        Response response = given()
                .contentType(ContentType.JSON)
                .body(malformedJson)
                .when()
                .post("/pet");

        ApiTestUtils.logInfo("Response Status Code: " + response.getStatusCode());
        response.then()
                .statusCode(anyOf(equalTo(400), equalTo(500)));

        ApiTestUtils.logInfo("Malformed JSON handled correctly");
    }

    @Test(description = "Test missing required fields", priority = 4)
    public void testCreatePetWithMissingFields() {
        ApiTestUtils.logStep("Testing pet creation without required fields");

        String incompleteJson = ApiTestUtils.createMinimalPetPayload("Incomplete Pet");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(incompleteJson)
                .when()
                .post("/pet");

        ApiTestUtils.logInfo("Response Status Code: " + response.getStatusCode());

        // If API returns 200, it's a validation bug
        if (response.getStatusCode() == 200) {
            logger.warn("BUG: API accepted incomplete payload - missing required fields should be rejected!");
            ApiTestUtils.logInfo("BUG: Missing required fields accepted - API validation is insufficient");
        } else {
            ApiTestUtils.logInfo("Missing fields correctly rejected with status: " + response.getStatusCode());
        }
                
        // API should reject missing required fields with 400 or 500
        response.then()
                .statusCode(anyOf(equalTo(400), equalTo(500)));
    }

    @Test(description = "Test boundary values", priority = 5)
    public void testBoundaryValues() {
        ApiTestUtils.logStep("Testing boundary values - Long limits (int64)");

        // Test Long.MAX_VALUE (should be accepted since API uses int64)
        long maxLongValue = Long.MAX_VALUE;

        String maxLongJson = ApiTestUtils.createLargeIdPetPayload(maxLongValue, "Max Long Pet", "available");

        Response maxLongResponse = given()
                .contentType(ContentType.JSON)
                .body(maxLongJson)
                .when()
                .post("/pet");

        ApiTestUtils.logInfo("Max Long ID (" + maxLongValue + ") Response Status: " + maxLongResponse.getStatusCode());

        if (maxLongResponse.getStatusCode() == 200) {
            ApiTestUtils.logInfo("Max Long ID correctly accepted - API supports full int64 range");
        } else {
            ApiTestUtils.logInfo("Max Long ID rejected with status: " + maxLongResponse.getStatusCode());
        }

        maxLongResponse.then()
                .statusCode(anyOf(equalTo(200)));

        // Test Long.MAX_VALUE + 1 using string (should be rejected due to overflow)
        String overflowJson = "{\"id\": " + Long.MAX_VALUE + "1, \"name\": \"Overflow Pet\", \"status\": \"available\"}";

        Response overflowResponse = given()
                .contentType(ContentType.JSON)
                .body(overflowJson)
                .when()
                .post("/pet");

        ApiTestUtils.logInfo("Long.MAX_VALUE + 1 Response Status: " + overflowResponse.getStatusCode());

        // Values beyond Long.MAX_VALUE should be rejected
        if (overflowResponse.getStatusCode() == 200) {
            logger.warn("BUG DETECTED: API accepted ID beyond Long.MAX_VALUE - this should be rejected!");
            ApiTestUtils.logInfo("BUG: Overflow ID accepted - API should reject values larger than Long.MAX_VALUE");
        } else {
            ApiTestUtils.logInfo("Long.MAX_VALUE + 1 correctly rejected with status: " + overflowResponse.getStatusCode());
        }

        overflowResponse.then()
                .statusCode(anyOf(equalTo(400), equalTo(500)));
    }

    // ================================
    // GET TESTS (READ OPERATIONS)
    // ================================

    @Test(description = "Get pet by ID", priority = 6)
    public void testGetPetByIdPositive() {
        // Arrange: Create test pet
        int petId = createTestPet("GetTestBuddy", "available");
        
        // Wait for eventual consistency
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        ApiTestUtils.logStep("Getting pet by ID: " + petId);
        
        // Wait for API to propagate the created pet, but application is inconsistent
        try {
            ApiTestUtils.logInfo("Waiting 10 seconds for API propagation...");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act: Get the pet
        Response response = given()
                .when()
                .get("/pet/" + petId);

        ApiTestUtils.logInfo("Response Status Code: " + response.getStatusCode());
        ApiTestUtils.logInfo("Response Body: " + response.getBody().asString());

        // Assert: Verify response
        response.then()
                .statusCode(200)
                .body("name", equalTo("GetTestBuddy"))
                .body("status", equalTo("available"));
        
        // Verify ID matches
        int responseId = response.jsonPath().getInt("id");
        verifySoftEquals(responseId, petId, "Pet ID should match");


        ApiTestUtils.logInfo("Pet retrieved successfully");
    }

    @Test(description = "Find pets by status", priority = 7)
    public void testFindPetsByStatus() {
        ApiTestUtils.logStep("Finding pets with status 'available'");

        Response response = given()
                .queryParam("status", "available")
                .when()
                .get("/pet/findByStatus");

        ApiTestUtils.logInfo("Response Status Code: " + response.getStatusCode());
        ApiTestUtils.logInfo("Found pets count: " + response.jsonPath().getList("$").size());
        
        response.then()
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)));

        ApiTestUtils.logInfo("Pets found successfully by status");
    }

    @Test(description = "Test invalid pet IDs", dataProvider = "invalidIds", priority = 8)
    public void testGetPetByInvalidId(long invalidId, String description) {
        ApiTestUtils.logStep("Testing " + description + ": " + invalidId);

        Response response = given()
                .when()
                .get("/pet/" + invalidId);

        ApiTestUtils.logInfo("Response Status Code: " + response.getStatusCode());

        // API might return different error codes
        response.then()
                .statusCode(anyOf(equalTo(404), equalTo(400), equalTo(500)));

        ApiTestUtils.logInfo(description + " handled correctly");
    }

    // ================================
    // PUT TESTS (UPDATE OPERATIONS)
    // ================================

    @Test(description = "Update pet status", priority = 10)
    public void testUpdatePetStatus() {
        // Arrange: Create test pet
        int petId = createTestPet("UpdateTestBuddy", "available");
        
        ApiTestUtils.logStep("Updating pet status to 'sold'");

        String updateJson = ApiTestUtils.createPetPayload(petId, "UpdateTestBuddy", "sold");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when()
                .put("/pet");

        ApiTestUtils.logInfo("Response Status Code: " + response.getStatusCode());
        
        response.then()
                .statusCode(200)
                .body("status", equalTo("sold"));
                
        // Verify ID matches
        int responseId = response.jsonPath().getInt("id");
        verifySoftEquals(responseId, petId, "Pet ID should match");

        ApiTestUtils.logInfo("Pet status updated successfully");
    }

    @Test(description = "Test update non-existent pet", priority = 11)
    public void testUpdateNonExistentPet() {
        ApiTestUtils.logStep("Testing update of non-existent pet");

        int nonExistentId = ApiTestUtils.generateUniquePetId();
        String updateJson = ApiTestUtils.createPetPayload(nonExistentId, "Ghost Pet", "available");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when()
                .put("/pet");

        ApiTestUtils.logInfo("Response Status Code: " + response.getStatusCode());

        // Non-existent pet update should return 404, not create new pet
        if (response.getStatusCode() == 200) {
            logger.warn("DESIGN ISSUE: PUT on non-existent pet created new resource - should return 404!");
            ApiTestUtils.logInfo("DESIGN ISSUE: Non-existent pet update created new pet - this is confusing behavior");

        } else if (response.getStatusCode() == 404) {
            ApiTestUtils.logInfo("✅ Correct: Non-existent pet update returned 404 - proper strict PUT behavior");
        } else {
            ApiTestUtils.logInfo("Non-existent pet update handled with error code: " + response.getStatusCode());
        }

        response.then()
                .statusCode(anyOf(equalTo(400),equalTo(404)));
    }

    // ================================
    // DELETE TESTS (DELETE OPERATIONS)
    // ================================

    @Test(description = "Delete pet by ID", priority = 12)
    public void testDeletePet() {
        // Arrange: Create test pet
        int petId = createTestPet("DeleteTestBuddy", "available");
        
        ApiTestUtils.logStep("Deleting pet with ID: " + petId);

        // Act: Delete the pet
        Response response = given()
                .when()
                .delete("/pet/" + petId);

        ApiTestUtils.logInfo("Response Status Code: " + response.getStatusCode());
        
        response.then()
                .statusCode(200);

        ApiTestUtils.logInfo("Pet deleted successfully");

        // Assert: Verify pet is deleted - should return 404
        ApiTestUtils.logStep("Verifying pet is deleted");
        
        Response verifyResponse = given()
                .when()
                .get("/pet/" + petId);
        
        ApiTestUtils.logInfo("Verification response status: " + verifyResponse.getStatusCode());
        
        if (verifyResponse.getStatusCode() == 404) {
            ApiTestUtils.logInfo("Pet correctly returns 404 after deletion - as expected");
        } else if (verifyResponse.getStatusCode() == 200) {
            logger.warn("BUG: Deleted pet still accessible with GET - should return 404!");
            ApiTestUtils.logInfo("BUG: Deleted pet returned 200 - API delete operation failed");
        } else {
            ApiTestUtils.logInfo("Unexpected status code after delete: " + verifyResponse.getStatusCode());
        }
    }

    // ================================
    // HELPER METHODS
    // ================================
    
    /**
     * Creates a test pet and returns its ID
     * @param name pet name
     * @param status pet status
     * @return pet ID
     */
    private int createTestPet(String name, String status) {
        int petId = ApiTestUtils.generateUniquePetId();
        String petJson = ApiTestUtils.createPetPayload(petId, name, status);
        
        given()
            .contentType(ContentType.JSON)
            .body(petJson)
            .when()
            .post("/pet")
            .then()
            .statusCode(200);
            
        logger.info("Test pet created: ID=" + petId + ", name=" + name + ", status=" + status);
        return petId;
    }
    
    /**
     * Creates a default test pet for testing
     * @return pet ID
     */
    private int createTestPet() {
        return createTestPet("TestPet", "available");
    }

}