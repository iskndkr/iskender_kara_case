package com.iskender.pages;

import com.iskender.utils.BrowserUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CareersPage extends BasePage {

    @FindBy(xpath = "//h3[contains(text(), 'Locations')]")
    private WebElement locationsSection;

    @FindBy(xpath = "//a[contains(text(), 'See all teams')]")
    private WebElement teamsSection;

    @FindBy(xpath = "//h2[contains(text(), 'Life at Insider')]")
    private WebElement lifeAtInsiderSection;

    public boolean isLocationsVisible() {
        return isDisplayed(locationsSection);
    }

    public boolean isTeamsVisible() {
        return isDisplayed(teamsSection);
    }

    public boolean isLifeAtInsiderVisible() {
        return isDisplayed(lifeAtInsiderSection);
    }

    public void navigateToQualityAssurance() {
        String qualityAssuranceUrl="https://useinsider.com/careers/quality-assurance/";
        BrowserUtils.navigateTo(qualityAssuranceUrl);
        waitForPageLoad();
    }

}