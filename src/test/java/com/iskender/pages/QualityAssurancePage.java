package com.iskender.pages;

import com.iskender.utils.BrowserUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class QualityAssurancePage extends BasePage {

    @FindBy(xpath = "//a[text()='See all QA jobs']")
    private WebElement seeAllQAJobsButton;

    @FindBy(xpath = "//b[@role='presentation']")
    private WebElement locationFilter;

    @FindBy(xpath = "//span[@id='select2-filter-by-department-container']")
    private WebElement departmentFilter;

    @FindBy(xpath = "//li[contains(text(), 'Istanbul, Turkiye')]")
    private WebElement istanbulOption;

    @FindBy(xpath = "//li[contains(text(), 'Quality Assurance')]")
    private WebElement qualityAssuranceOption;

    @FindBy(xpath = "//div[@class='position-list-item-wrapper bg-light']")
    private List<WebElement> jobList;

    @FindBy(xpath = "//div[@class='position-list-item-wrapper bg-light']//p[@class='position-title font-weight-bold']")
    private List<WebElement> jobTitles;

    @FindBy(xpath = "//div[@class='position-list-item-wrapper bg-light']//span[@class='position-department text-large font-weight-600 text-primary']")
    private List<WebElement> jobDepartments;

    @FindBy(xpath = "//div[@class='position-list-item-wrapper bg-light']//div[@class='position-location text-large']")
    private List<WebElement> jobLocations;

    @FindBy(xpath = "//a[text()='View Role']")
    private List<WebElement> viewRoleButtons;

    public void clickSeeAllQAJobs() {
        scrollToElement(seeAllQAJobsButton);
        click(seeAllQAJobsButton);

    }

    public void filterByLocation() {
       click(locationFilter);
       click(istanbulOption);
    }


    public boolean areJobsVisible() {
        waitFor(1); // Wait for jobs to load after filtering
        return !jobList.isEmpty();
    }

    public int getJobCount() {
        return jobList.size();
    }

    public boolean verifyJobDetails() {
        boolean allJobsValid = true;
        logger.info("Starting job details verification for " + jobTitles.size() + " jobs");
        
        for (int i = 0; i < jobTitles.size(); i++) {
            // Scroll to element first, then get text
            scrollToElement(jobTitles.get(i));
            BrowserUtils.waitForVisibility(jobTitles.get(0),3);
            String title = jobTitles.get(i).getText();
            String department = jobDepartments.get(i).getText();
            String location = jobLocations.get(i).getText();

            boolean titleContainsQA = title.toLowerCase().contains("quality assurance");
            boolean departmentContainsQA = department.toLowerCase().contains("quality assurance");
            boolean locationContainsIstanbul = location.contains("Istanbul") &&
                                              location.contains("Turkiye");

            if (!titleContainsQA || !departmentContainsQA || !locationContainsIstanbul) {
                logger.error("Job " + (i + 1) + " validation failed:");
                logger.error("  Title: " + title + " (Contains QA: " + titleContainsQA + ")");
                logger.error("  Department: " + department + " (Contains QA: " + departmentContainsQA + ")");
                logger.error("  Location: " + location + " (Contains Istanbul: " + locationContainsIstanbul + ")");
                allJobsValid = false;
            } else {
                logger.info("Job " + (i + 1) + " validation passed");
            }
        }
        
        logger.info("Job details verification completed. All jobs valid: " + allJobsValid);
        return allJobsValid;
    }

    public void clickFirstViewRole() {
        if (!viewRoleButtons.isEmpty()) {
            BrowserUtils.clickWithJS(viewRoleButtons.get(0));
        } else {
            throw new RuntimeException("No View Role buttons found");
        }
    }
}