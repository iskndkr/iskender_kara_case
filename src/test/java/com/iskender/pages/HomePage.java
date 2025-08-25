package com.iskender.pages;

import com.iskender.utils.BrowserUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage {

    @FindBy(xpath = "//a[contains(@class, 'navbar-brand')]")
    private WebElement logo;

    @FindBy(xpath = "//a[contains(text(), 'Company')]")
    private WebElement companyMenu;

    @FindBy(xpath = "//a[contains(text(), 'Careers')]")
    private WebElement careersLink;

    @FindBy(xpath = "//div[@class='career-load-more']")
    private WebElement careerPage;

    public boolean isLoaded() {
        return isDisplayed(logo);
    }

    public void hoverCompanyMenu() {
        hover(companyMenu);
    }

    public void clickCareers() {
        BrowserUtils.waitFor(1);
        click(careersLink);
        waitForPageLoad();
        BrowserUtils.dismissCookiePopupIfPresent();
    }
}