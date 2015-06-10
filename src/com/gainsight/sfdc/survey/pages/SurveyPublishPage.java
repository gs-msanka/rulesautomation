package com.gainsight.sfdc.survey.pages;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.testdriver.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by gainsight on 05/12/14.
 */
public class SurveyPublishPage extends SurveyPage {


	private static final String SURVEY_STATUS                   = "//div[@class='col-sm-5 status-msg']";
	private static final String SITE_URL_INPUT                  = "//input[@name='site']";
	private static final String EMAIL_TEMPLATE_SELECT           = "//select[@name='template']/following-sibling::button";
    private static final String DEFAULT_ADDRESS_SELECT          = "//select[@name='email']/following-sibling::button";
    private static final String PUBLISH_DATE_ON_OFF             = "//label[@for='hpd']";
    private static final String PUBLISH_DATE_CHECKBOX           = "hpd";
    private static final String SEND_TEST_EMAIL_BUTTON          = "//input[contains(@class, 'btn-send-test-mail') and @value='Send Test Email']";
    private static final String PUBLISH_BUTTON                  = "//input[contains(@class, 'btn-host-survey') and @value='Publish']";
    private static final String SUCCESS_MESSAGE                 = "//div[@class='errorMessage alert alert-success' and contains(@style,'opacity') and contains(text(),'Survey published successfully')]";
    private static final String ERROR_MESSAGE_DIV               = "//div[@class='errorMessage alert alert-danger']";

    private static final String SEND_TEST_EMAIL_DIV             = "//span[@class='ui-dialog-title' and contains(text(),'Send Test Email')]/../..[@role='dialog']";
    private static final String SEND_TEST_MAIL_TITLE            = "//span[@class='ui-dialog-title' and contains(text(),'Send Test Email')]";
    private static final String TEST_FROM_EMAIL_DROP_DOWN       = "//select[@name='senderEmail']/following-sibling::button";
    private static final String TEST_EMAIL_DIV                  = "//div[@class='addEmail clearfix'][last()]";
    private static final String TEST_EMAIL_INPUT                = ".//input[@type='email' and @name='testEmails[]']";
    private static final String ADD_TEST_EMAIL                  = ".//div[@class='addmail']/a[@data-action='add']";
    private static final String REMOVE_TEST_EMAIL               = ".//div[@class='addmail']/a[@data-action='delete']";
    private static final String SEND_TEST_EMAILS                = "//button[contains(@class,'btn-send') and text() = 'Send Test Email']";
    private static final String CANCEL_TEST_EMAILS              = "//button[contains(@class, 'btn-cancel') and text() = 'Cancel']";
    private static final String TEST_EMAIL_ERROR_MSG            = ".//div[@class='errorMessage alert alert-danger']";
    private static final String TEST_EMAIL_ERROR_INFO_MSG       = ".//div[@class='sendTestMailMsg alert alert-danger']";
    private static final String TEST_EMAIL_SUCCESS_MSG          = "//div[@class='sendTestMailMsg alert alert-success']";
    private static final String EMAIL_ALERT_BUTTON              = "//div[@class='modal_footer']/input";
    private static final String EMAIL_ALERT_DIV                 = "//div[@class='modal_body']/descendant::div[@class='layout_popup_text']";

    public SurveyPublishPage(String surveyName) {
        super(surveyName);
        wait.waitTillElementDisplayed(PUBLISH_BUTTON, MIN_TIME, MAX_TIME);
    }

    public String getSurveyStatus() {
        String result = field.getText(SURVEY_STATUS).trim();
        Log.info("Survey Status : " +result);
		return result;
	}
	
	public SurveyPublishPage updatePublishDetails(SurveyProperties sProp) {
		field.clearAndSetText(SITE_URL_INPUT, sProp.getSiteURL());
        if(sProp.getEmailTemplate()!=null & sProp.getEmailTemplate()!="") {
            item.click(EMAIL_TEMPLATE_SELECT);
            selectValueInDropDown(sProp.getEmailTemplate());
        }
		if(sProp.getDefaultAddress()!=null && sProp.getDefaultAddress()!="") {
            item.click(DEFAULT_ADDRESS_SELECT);
            selectValueInDropDown(sProp.getDefaultAddress());
        }

        //TODO - show/hide publish date
        /*if(sProp.isHidePublishDate()) {
            boolean a = Boolean.valueOf(element.getElement(PUBLISH_DATE_CHECKBOX).getAttribute("checked"));
            System.out.println();
            if(!a) {
                item.click(PUBLISH_DATE_ON_OFF);
            }
        }*/
        
		clickOnPublishSurvey();
        return this;
	}
	
	public void clickOnPublishSurvey(){
        item.click(PUBLISH_BUTTON);
		waitTillNoLoadingIcon();
	}

    public SurveyPublishPage sendTestEmail(String[] recipientEmails, String fromAddress){
        item.click(SEND_TEST_EMAIL_BUTTON);
        wait.waitTillElementDisplayed(SEND_TEST_EMAIL_DIV, MIN_TIME, MAX_TIME);
        if(recipientEmails.length==0) {
            Log.error("No Email Addresses Specified.");
        }
        wait.waitTillElementDisplayed(SEND_TEST_MAIL_TITLE, MIN_TIME, MAX_TIME);
        if(fromAddress!=null && fromAddress!="") {
            item.click(TEST_FROM_EMAIL_DROP_DOWN);
            selectValueInDropDown(fromAddress);
        }
        WebElement temp = element.getElement(TEST_EMAIL_DIV);
        for(String email : recipientEmails) {
            temp.findElement(By.xpath(TEST_EMAIL_INPUT)).sendKeys(email);
            temp.findElement(By.xpath(ADD_TEST_EMAIL)).click();
            temp = element.getElement(TEST_EMAIL_DIV);
        }
        temp.findElement(By.xpath(REMOVE_TEST_EMAIL)).click();
        item.click(SEND_TEST_EMAILS);
        waitTillNoLoadingIcon();
        return this;
	}

    public SurveyPublishPage closeTestEmailDialog() {
       item.click(CANCEL_TEST_EMAILS);
        return this;
    }

    public String getTestEmailErrorInfoMsg() {
        WebElement POPUP_ELE = element.getElement(By.xpath(SEND_TEST_EMAIL_DIV));
        String result = POPUP_ELE.findElement(By.xpath((TEST_EMAIL_ERROR_INFO_MSG))).getText().trim();
        Log.info("Error Message : " +result);
        return result;
    }

    public String getTestErrorMsg() {
        WebElement POPUP_ELE = element.getElement(By.xpath(SEND_TEST_EMAIL_DIV));
        String result = POPUP_ELE.findElement(By.xpath((TEST_EMAIL_ERROR_MSG))).getText().trim();
        Log.info("Error Message : " +result);
        return result;
    }

    public String getTestEmailSuccessMsg() {
        WebElement POPUP_ELE = element.getElement(By.xpath(SEND_TEST_EMAIL_DIV));
        String result = POPUP_ELE.findElement(By.xpath((TEST_EMAIL_SUCCESS_MSG))).getText().trim();
        Log.info("Error Message : " +result);
        return result;
    }

    public String getErrorMessage() {
        String result = field.getText(ERROR_MESSAGE_DIV).trim();
        Log.info("Error Message : " +result);
        return result;
    }


    public boolean statusOfSendTestEmailButton() {
        try {
            WebElement ele =  element.getElement(SEND_TEST_EMAIL_BUTTON);
        } catch (Exception e) {
            if(e.getLocalizedMessage().contains("Element is disabled and so may not be used for actions")) {
                Log.info("Send Test Email Button is disabled.");
                return false;
            } else {
                Log.error("Some other error", e);
                throw new RuntimeException(e.getLocalizedMessage());
            }

        }
        return true;
    }
	
	public void sendTestEmails(String[] recipientEmails) {
		item.click(SEND_TEST_EMAIL_BUTTON); /*iterating 10 times below, since according to testcase we can send maximum 10 test emails*/
		for (int i = 0; i < 10; i++) {
			String emails = recipientEmails[0];
			WebElement temp = element.getElement(TEST_EMAIL_DIV);
			temp.findElement(By.xpath(TEST_EMAIL_INPUT)).sendKeys(emails);
			temp.findElement(By.xpath(ADD_TEST_EMAIL)).click();
			temp = element.getElement(TEST_EMAIL_DIV);
		}
	}
    
	public String getMaxTestEmailsAletMsg() {
		wait.waitTillElementDisplayed(EMAIL_ALERT_DIV, MIN_TIME, MAX_TIME);
		return element.getElement(EMAIL_ALERT_DIV).getText();
	}
    
	public SurveyPublishPage closeAlertEmailDialog() {
		item.click(EMAIL_ALERT_BUTTON);
		return this;
	}
}
