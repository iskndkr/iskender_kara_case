# Iskender Kara Test Automation Project

## Project Overview
This project contains comprehensive test automation solutions including:
- **UI Test Automation** for Insider.com using Selenium WebDriver with Page Object Model
- **API Test Automation** for Pet Store API using REST Assured
- **Load Testing** for N11.com search functionality using JMeter

## Project Structure
```
iskender_kara_case/
├── src/
│   └── test/
│       ├── java/com/iskender/
│       │   ├── pages/             # Page Object Model classes
│       │   │   ├── BasePage.java  # Common page methods
│       │   │   ├── HomePage.java
│       │   │   ├── CareersPage.java
│       │   │   └── QualityAssurancePage.java
│       │   ├── tests/             # Test packages organized by type  
│       │   │   ├── TestBase.java  # Base test class with setup/teardown
|       |   |   |── ApiTestBase.java  # Base test class for API tests with setup/teardown
│       │   │   ├── ui/            # UI test classes
│       │   │   │   └── InsiderCareersTest.java
│       │   │   ├── api/           # API test classes
│       │   │   │   └── PetStoreApiTest.java
│       │   │   └── load/          # JMeter Load tests
│       │   │       ├── N11_Search_LoadTest.jmx  # Working N11 test
│       │   │       └── search_terms.csv         # Test data
│       │   └── utils/             # Utility classes
│       │       ├── Driver.java    # WebDriver management
│       │       ├── BrowserUtils.java
│       │       ├── ConfigurationReader.java # Simple config reader
│       │       ├── ApiTestUtils.java   # Api utility methods
│       │       └── ReportManager.java # ExtentReports
│       └── resources/
│           ├── testng.xml         # Master suite (default entry point)
│           ├── testng-ui-only.xml # UI tests only
│           ├── testng-api-only.xml # API tests only
│           ├── config.properties  # Test configuration
│           └── log4j2.xml         # Logging configuration
├── configuration.properties       # Configuration file (project root)
├── N11_Load_Test_Complete_Report.md  # Interview presentation doc
├── test-output/
│   ├── logs/                      # Log files
│   ├── screenshots/               # Test failure screenshots  
│   └── reports/                   # HTML test reports
└── pom.xml
```

## Technologies Used
- **Java 11**
- **Selenium WebDriver 4.15.0** (with Selenium Manager)
- **TestNG 7.8.0** (with custom listeners)
- **REST Assured 5.3.2** (for API testing)
- **Log4j2 2.21.1** (for comprehensive logging)
- **ExtentReports 5.1.1** (for HTML reporting)
- **Apache JMeter** (for load testing)
- **Maven** (dependency management)

## Key Features

### Architecture & Design Patterns
- **Page Object Model (POM)** implementation with BasePage
- **Centralized Test Base** with TestBase class for setup/teardown and utilities
- **Simple Configuration Management** with ConfigurationReader (Properties-based)
- **Comprehensive Logging** with Log4j2 and custom LogManager
- **Detailed Reporting** with ExtentReports and ReportManager
- **Manual Test Control** - No automatic listeners, full control over test execution

### Test Automation Features
- **Cross-browser support** (Chrome, Firefox, Edge, Headless modes)
- **Parametric browser selection** via TestNG parameters
- **Automatic screenshot capture** on test failures
- **Detailed step-by-step logging** for debugging
- **HTML reports** with embedded screenshots
- **Thread-safe WebDriver management**
- **Robust wait strategies** and element interactions

### Test Coverage

#### UI Test Automation (Insider.com)

**HomePageTest.java:**
- Home page loading verification
- Logo visibility and functionality
- Page title validation
- URL correctness verification
- Company menu visibility check

**NavigationTest.java:**
- Company menu hover/click functionality
- Careers link accessibility
- Navigation to Careers page
- Session preservation during navigation
- Responsive navigation behavior

**CareersPageTest.java:**
- Careers page loading verification
- "Our Locations" section visibility
- "Teams" section visibility  
- "Life at Insider" section visibility
- All sections combined verification
- Quality Assurance link accessibility
- Navigation to QA page
- Complete content loading verification

**QualityAssuranceJobsTest.java:**
- QA page loading verification
- "See all QA jobs" button functionality
- Job filtering by location (Istanbul, Turkey)
- Job filtering by department (Quality Assurance)
- Jobs visibility after filtering
- Job details criteria matching
- Individual job components display
- Filtering consistency verification
- Job search without filters

**JobApplicationTest.java:**
- View Role buttons presence and clickability
- View Role button click functionality
- Lever application form redirect verification
- Application form page loading
- Multiple View Role buttons consistency
- Application form accessibility
- Back navigation from application

#### API Test Automation (Pet Store)
**Positive Scenarios:**
- Create a new pet with valid data
- Create pets with different statuses (available, pending, sold)
- Retrieve pet by ID
- Find pets by status filtering
- Update existing pet status
- Delete pet by ID

**Negative Scenarios:**
- Get non-existent pet (404 error handling)
- Get pet with invalid ID formats (negative, zero, overflow)
- Create pet with malformed JSON (validation testing)
- Create pet without required fields (validation testing)
- Update non-existent pet (error handling)
- Boundary value testing (int64 overflow limits)

**⚠️ Known Issues (PetStore API Design Problems):**
- **Create pet without required fields**: API accepts invalid data (returns 200 instead of 400/500)
- **Update non-existent pet**: Creates new resource instead of returning 404 error  
- **Delete operations**: Inconsistent behavior - sometimes returns 404 for valid deletions
- **Soft assertions used**: Tests identify API design flaws vs actual test failures
- **15 tests run**: 12 pass, 3 fail due to API design issues (not test problems)

#### Load Test (N11.com Search Module) ✅
**Complete working solution with 100% success rate**
- **Anti-bot protection bypass**: Successfully overcomes Cloudflare protection with N11-specific headers
- **Realistic user simulation**: Homepage → Autocomplete → Search flow with proper timing
- **Test data**: 8 international brand search terms (apple, macbook, adidas, nivea, siemens, samsung, rayban, sephora)
- **Performance metrics**: All requests return 200 OK, response times 50ms-3s average
- **Browser simulation**: Complete Chrome 132 fingerprinting with session management

**Critical Success Factors:**
```http
ADRUM: isAjax:true
X-KL-saas-Ajax-Request: Ajax_Request
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36
```

**⚠️ Known Issues (N11 Performance Variability):**
- Some search terms (macbook, nivea) may exceed 5-second response time limit
- Complex product categories show higher latency during peak hours
- Response time assertion failures are expected for heavy searches
- All requests maintain 200 OK status despite timing variations

## Setup and Execution

### Prerequisites
- Java 11 or higher
- Maven 3.6+
- Chrome/Firefox browsers (automatically managed by Selenium 4)
- Apache JMeter (for load tests)

### Running Tests

#### All Tests
```bash
mvn clean test
```

#### Specific Browser
```bash
mvn clean test -Dbrowser=chrome
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=headless-chrome
```

#### Test Suites

**Specific Test Classes:**
```bash
# UI Tests Only
mvn clean test -Dtest="com.iskender.tests.ui.*"

# API Tests Only  
mvn clean test -Dtest="com.iskender.tests.api.*"

# Specific Test Class
mvn clean test -Dtest=HomePageTest
mvn clean test -Dtest=PetStoreApiTest
```

**Using TestNG XML Suites:**
```bash
# Default suite (all tests)
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/testng.xml

# UI tests only
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/testng-ui-only.xml

# API tests only
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/testng-api-only.xml
```

#### Load Test (N11 Search Module)
**Files Location:** `src/test/java/com/iskender/tests/load/`

```bash
# GUI mode (development/debugging)
jmeter -t src/test/java/com/iskender/tests/load/N11_Search_LoadTest.jmx

# Command line mode (production)
cd src/test/java/com/iskender/tests/load
jmeter -n -t N11_Search_LoadTest.jmx -l results.jtl -e -o html-report

# Expected Results:
# ✅ All requests: 200 OK
# ✅ Homepage: ~350ms  
# ✅ Autocomplete: ~50-120ms
# ✅ Search: 1-3 seconds
```

**Complete Documentation:** See `N11_Load_Test_Complete_Report.md` for technical details and interview presentation materials.

## Configuration

### configuration.properties (Project Root)
```properties
browser=chrome
url=https://useinsider.com/
apiUrl=https://petstore.swagger.io/v2
timeout=10
```

**Location:** `configuration.properties` in project root directory for easy access and editing.

### Browser Support Matrix
- ✅ **Chrome** (default)
- ✅ **Firefox** 
- ✅ **Edge**
- ✅ **Headless Chrome**
- ✅ **Remote Chrome** (with --remote-allow-origins)

## Logging & Reporting

### Log4j2 Configuration
- **Console logging** for real-time feedback
- **File logging** with rotation (test-output/logs/)
- **Error-specific logs** for troubleshooting
- **Reduced verbosity** for Selenium/TestNG logs

### ExtentReports Features
- **HTML reports** with test execution details
- **Screenshot embedding** on failures
- **Test categorization** and author assignment
- **System information** capture
- **Timeline view** of test execution

### TestNG Listener Capabilities
- **Automatic screenshot** capture on test failures
- **Test execution time** tracking
- **Comprehensive logging** of test lifecycle events
- **Integration** with reporting framework

## Test Execution Flow

### UI Test Flow
1. **TestBase** initializes WebDriver and navigates to base URL
2. **Page objects** are initialized with BasePage inheritance
3. **Test steps** are logged with LogManager and ReportManager
4. **Assertions** are performed using BaseTest verification methods
5. **Screenshots** are captured automatically on failures
6. **Browser cleanup** in teardown methods

### API Test Flow  
1. **REST Assured** configuration with base URI
2. **Test data generation** with unique IDs
3. **CRUD operations** with comprehensive validations
4. **Response verification** using Hamcrest matchers
5. **Error scenario testing** with expected status codes

## Troubleshooting

### Common Issues
- **WebDriver issues**: Resolved automatically by Selenium Manager
- **Element not found**: Check page load waits and selectors
- **Test failures**: Screenshots automatically captured in test-output/screenshots/
- **Configuration issues**: Verify configuration.properties file

### Debug Information
- **Logs**: Check test-output/logs/ for detailed execution logs
- **Reports**: Open test-output/reports/TestReport_*.html for visual reports
- **Screenshots**: Review test-output/screenshots/ for failure evidence

## Author
**Iskender Kara**

---

## Architecture Benefits

### Maintainability
- **Centralized configuration** management
- **Reusable base classes** for common functionality
- **Page Object Model** for UI element management
- **Utility classes** for common operations

### Scalability  
- **Thread-safe WebDriver** management
- **Modular test structure** for easy expansion
- **Parameterized execution** for multiple environments
- **Comprehensive logging** for debugging

### Reliability
- **Robust wait strategies** to handle dynamic content
- **Error handling** with automatic screenshot capture
- **Comprehensive assertions** with detailed error messages
- **Cross-browser compatibility** testing

This enhanced architecture provides a solid foundation for scalable, maintainable, and reliable test automation across UI, API, and performance testing domains.
