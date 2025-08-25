package com.iskender.tests.ui;

import com.iskender.tests.TestBase;
import com.iskender.pages.HomePage;
import com.iskender.pages.CareersPage;
import com.iskender.pages.QualityAssurancePage;
import com.iskender.utils.BrowserUtils;
import org.testng.annotations.Test;

public class InsiderCareersTest extends TestBase {

    private HomePage homePage;
    private CareersPage careersPage;
    private QualityAssurancePage qaPage;

    @Test(description = "Verify that the Insider home page is opened correctly", priority = 1)
    public void verifyInsiderHomePageIsOpened() {
        navigateToHomePage();
        
        logStep("Initializing Home Page");
        homePage = new HomePage();

        logStep("Verifying Home Page is loaded");
        verifyTrue(homePage.isLoaded(), "Home page should be loaded successfully");

        String currentUrl = getCurrentPageUrl();
        verifyContains(currentUrl, "useinsider.com", "URL should contain 'useinsider.com'");
    }

    @Test(description = "Verify that Careers page sections are displayed", priority = 2)
    public void verifyCareersPageSectionsAreDisplayed() {
        navigateToHomePage();
        
        logStep("Navigating to Careers page");
        homePage = new HomePage();
        homePage.hoverCompanyMenu();
        homePage.clickCareers();

        logStep("Initializing Careers Page");
        careersPage = new CareersPage();

        logStep("Verifying Locations section is visible");
        verifyTrue(careersPage.isLocationsVisible(), "Locations section should be visible");

        logStep("Verifying Teams section is visible");
        verifyTrue(careersPage.isTeamsVisible(), "Teams section should be visible");

        logStep("Verifying Life at Insider section is visible");
        verifyTrue(careersPage.isLifeAtInsiderVisible(), "Life at Insider section should be visible");
    }

    @Test(description = "Verify QA job filter results are displayed", priority = 3)
    public void verifyQAJobFilterResults() {
        logStep("Navigating to Quality Assurance jobs");
        careersPage = new CareersPage();
        careersPage.navigateToQualityAssurance();

        logStep("Initializing Quality Assurance Page");
        qaPage = new QualityAssurancePage();

        logStep("Clicking 'See all QA jobs' button");
        qaPage.clickSeeAllQAJobs();

        logStep("Filtering jobs by location (Istanbul, Turkey)");
        qaPage.filterByLocation();

        logStep("Verifying jobs are displayed after filtering");
        verifyTrue(qaPage.areJobsVisible(), "Job list should be present after filtering");

        int jobCount = qaPage.getJobCount();
        logInfo("Found " + jobCount + " jobs after filtering");
    }

    @Test(description = "Verify filtered jobs details match expected criteria", priority = 4, dependsOnMethods = "verifyQAJobFilterResults")
    public void verifyFilteredJobsDetails() {
        logStep("Verifying job details match filter criteria");
        qaPage = new QualityAssurancePage();
        verifyTrue(qaPage.verifyJobDetails(),
                "All jobs should have Quality Assurance department and Istanbul, Turkey location");

        logInfo("All filtered jobs have correct department and location details");
    }

    @Test(description = "Verify View Role button redirects to Lever application form", priority = 5, dependsOnMethods = "verifyQAJobFilterResults")
    public void verifyViewRoleButtonRedirectsToLever() {
        logStep("Clicking on first 'View Role' button");
        qaPage = new QualityAssurancePage();

        qaPage.clickFirstViewRole();
        waitForSeconds(3);

        switchToNewTabAndVerifyUrl("lever.co", "Should be redirected to Lever application form (lever.co)");

        logInfo("Successfully redirected to Lever application form");
    }
}