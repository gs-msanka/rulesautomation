/*Survey Base page is home page of survey

The following are the actions done in SurveyBasePage:
a) Create Survey
b) Navigation to all primary menu items, click on primary options, verify if particular page is loaded
SurveyBasePage extends the BasePage. BasePage is home page and contains clicking on all tabs*/

package com.gainsight.sfdc.survey.pages;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.testdriver.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class SurveyBasePage extends BasePage {

    //Top Section Navigation Selectors
    private final String SEARCH_SURVEY_INPUT        = "//div[@class='sh-search pull-left']/input[contains(@class, 'search-survey')]";
    private final String CREATE_SURVEY_BUTTON       = "//input[contains(@class, 'create-new-survey')]";
    private final String SURVEYS_HEADER_SECTION     = "//span[@class='surveyHeaderText' and text()='%s']";

    //Left Section Navigation Selectors
    private final String DASHBOARD_SECTION          = "//a[@ref-module='DASHBOARD']";
    private final String DRAFTS_SECTION             = "//a[@ref-module='DRAFTS']";
    private final String PUBLISHED_SECTION          = "//a[@ref-module='HOSTED']";
    private final String EXPIRED_SECTION            = "//a[@ref-module='EXPIRED']";

    //Survey Create From Selectors
    private final String NEW_SURVEY_RADIO               = "//input[@type='radio' and @data-val='new']";
    private final String PRE_PACK_SURVEY_RADIO          = "//input[@type='radio' and @data-val='prepack']";
    private final String NEW_SURVEY_TITLE_INPUT         = "//input[contains(@class, 'survey-title-input create-survey-title')]";
    private final String SURVEY_CREATE_BUTTON           = "//input[contains(@class, 'btn-save') and @value='Create']";
    private final String PRE_PACK_SURVEY_SELECT         = "//select[contains(@class, 'sel-prepack-survey')]/following-sibling::button";
    private final String SURVEY_CREATE_CANCEL_BUTTON    = "//input[contains(@class, 'btn-save') and @value='Cancel']";


    //Drafts Section/View Selectors
    private final String SURVEY_CARD_TITLE        = "//li[@class='box survey-card']/descendant::h3[text()='%s']";

    public SurveyBasePage(String s) {
        System.out.println("Dummy Constructor - Survey Base Page");
    }

    public SurveyBasePage() {
        waitTillNoLoadingIcon();
        wait.waitTillElementPresent(CREATE_SURVEY_BUTTON, MIN_TIME, MAX_TIME);
    }

    public SurveyPropertiesPage createSurvey(SurveyProperties surveyProp, boolean isNew) {
        item.click(CREATE_SURVEY_BUTTON);
        waitTillNoLoadingIcon();
        if(isNew) {
            item.click(NEW_SURVEY_RADIO);
            field.clearAndSetText(NEW_SURVEY_TITLE_INPUT, surveyProp.getSurveyName());
        } else {
            item.click(PRE_PACK_SURVEY_RADIO);
            item.click(PRE_PACK_SURVEY_SELECT);
            selectValueInDropDown(surveyProp.getSurveyName());
        }
        item.click(SURVEY_CREATE_BUTTON);
        waitTillNoLoadingIcon();
        return new SurveyPropertiesPage(surveyProp);
    }


    public SurveyBasePage clickOnDashboardView() {
        item.click(DASHBOARD_SECTION);
        wait.waitTillElementDisplayed(String.format(SURVEYS_HEADER_SECTION, "Ongoing Surveys"), MIN_TIME, MAX_TIME);
        waitTillNoLoadingIcon();
        return this;
    }

    public SurveyBasePage clickOnDraftsView() {
        item.click(DRAFTS_SECTION);
        wait.waitTillElementDisplayed(String.format(SURVEYS_HEADER_SECTION, "Drafts"), MIN_TIME, MAX_TIME);
        waitTillNoLoadingIcon();
        return this;
    }

    public SurveyBasePage clickOnPublishedView() {
        item.click(PUBLISHED_SECTION);
        waitTillNoLoadingIcon();
        wait.waitTillElementDisplayed(String.format(SURVEYS_HEADER_SECTION, "Hosted Surveys"), MIN_TIME, MAX_TIME);
        return this;
    }

    public SurveyBasePage clickOnExpiredView() {
        item.click(EXPIRED_SECTION);
        waitTillNoLoadingIcon();
        wait.waitTillElementDisplayed(String.format(SURVEYS_HEADER_SECTION, "Expired Surveys"), MIN_TIME, MAX_TIME);
        return this;
    }

    public SurveyPropertiesPage openSurveyFromDrafts(SurveyProperties surveyProp){
        clickOnDraftsView();
        searchSurvey(surveyProp.getSurveyName());
        item.click(String.format(SURVEY_CARD_TITLE, surveyProp.getSurveyName()));
        return new SurveyPropertiesPage(surveyProp);
    }

    public void searchSurvey(String surName) {
        element.clearAndSetText(SEARCH_SURVEY_INPUT, surName);
        Timer.sleep(2);
    }

    public boolean isSurveyDisplayed(SurveyProperties surveyProperties) {
         return isElementPresentAndDisplay(By.xpath(String.format(SURVEY_CARD_TITLE, surveyProperties.getSurveyName())));
    }

    public void selectValueInDropDown(String value) {
        boolean selected = false;
        for(WebElement ele : element.getAllElement("//input[contains(@title, '"+value+"')]/following-sibling::span[contains(text(), '"+value+"')]")) {
            if(ele.isDisplayed()) {
                ele.click();
                selected = true;
                Log.info("Selected From Drop-down : " + value);
                break;
            }
        }
        if(selected != true) {
            throw new RuntimeException("Unable to select element : //input[contains(@title, '"+value+"')]/following-sibling::span[contains(text(), '"+value+"')]" );
        }
    }

    protected void waitTillElementDisplayed(final WebElement wEle, final String cssIdentifier,int minTime, int timeout) {
        try {
            synchronized (driver) {
                driver.wait(minTime * 1000);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        (new WebDriverWait(driver, timeout)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return wEle.findElement(By.cssSelector(cssIdentifier))
                        .isDisplayed();

            }
        });

    }

    public boolean isElementPresentAndDisplayed(WebElement wEle, String xpath) {
        boolean result = false;
        Log.info("Checking if element is present : " +xpath);
        try {
            result = wEle.findElement(By.xpath(xpath)).isDisplayed();
        } catch (Exception e) {
            Log.error("Element not present, "+xpath, e);
        }
        return result;
    }

    public void selectValueFromDropDown(WebElement webEle, List<String> options) {
        Select dropDown = new Select(webEle);
        for(String option: options) {
            dropDown.selectByVisibleText(option);
        }
    }
}
