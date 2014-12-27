package com.gainsight.sfdc.survey.pages;

import com.gainsight.testdriver.Log;
import org.openqa.selenium.WebElement;

import com.gainsight.sfdc.survey.pojo.SurveyProperties;

/**
 * Created by gainsight on 05/12/14.
 */
public class SurveyPropertiesPage extends SurveyDesignPage {
	
	private final String PROPERTIES_PAGE ="//a[@class='sub-menu-option  sub-opt-properties active']";
	private final String SURVEY_NAME ="//input[@class='form-control forminput survey-prop-title']";
	private final String EMAIL_SERVICE ="//div[@class='formfill clearfix']/button/span[@class='ui-icon ui-icon-triangle-2-n-s']"; //button[@class='ui-multiselect ui-widget ui-state-default ui-corner-all ui-state-active']";
	private final String AUTOSELECT_EMAILSERVICE = "//ul/li/label/span[text()='%s']"; //li[@class='ui-multiselect-option ui-multiselect-disabled ']//label[@class='ui-corner-all ui-state-active']//input[@title='Gainsight email service']";
	private final String START_DATE ="//input[@class='survey-date form-control forminput' and @type='text' and @name='startDate']";
	private final String END_DATE ="//input[@class='survey-date form-control forminput' and @type='text' and @name='endDate']";
	private final String ANONYMOUS_SUBMISSION ="//input[@class='anonymous-chk' and @type='checkbox' and @name='isAnonymous' and @value='true']";
	private final String INTERNAL_SUBMISSION ="//input[@class='internal-submission' and @type='checkbox' and @name='internalSubmission' and @value='true']";
	private final String DESCRIPTION ="//textarea[@class='form-control forminput survey-prop-description']";
	private final String THANK_YOU ="//label[@class='form-label thnq']/following-sibling::button/span[@class='ui-icon ui-icon-triangle-2-n-s']"; //select[@class='form-control  survey-prop-msgtype forminput]";
	private final String AUTOSELECT_THANKYOU ="//ul/li/label/span[text()='%s']";
	private final String MESSAGE_INPUT ="//input[@class='form-control forminput survey-prop-msgval ' and @type='text' and @placeholder='Enter thank you message']";
	private final String ENTER_URL= "//input[@class='form-control forminput survey-direct-url ' and @type='text' and @name='value' and @placeholder='Enter URL']";
	private final String ENTER_CUSTOMPAGEURL ="//input[@class='form-control forminput survey-custom-url ' and @type='text' and @placeholder='Enter custom page URL']";
	private final String FOOTER_MSG ="//textarea[@class='form-control forminput survey-prop-footermsg']";
	private final String SURVEY_CODE ="//input[@class='form-control forminput survey-prop-code']";
	private final String BCKGROUND_COLOR = "//input[@class='color' and @type='radio' and @value ='dcebdf']";
//	private final String UPLOAD_LOGO = "//input[@class='upbtn btn-file' and @type='button' and @value='Upload your logo']";
//	private final String BROWSE_IMAGE ="";
//	private final String SELECTEXISTING_IMAGE = "";
	private final String SAVE_SURVEY="//input[@class='gs-btn btn-save']";
	
	

    public SurveyPropertiesPage() {
        wait.waitTillElementDisplayed(PROPERTIES_PAGE, MIN_TIME, MAX_TIME);
    }
    
   
    public void fillAndSaveSurveyProperties (SurveyProperties surveyproperties) {
    	Log.info("Started Filling Survey Form");
    	//field.clearAndSetText(EMAIL_SERVICE, surveyproperties.getEmailService());
    //	field.selectFromDropDown(EMAIL_SERVICE, surveyproperties.getEmailService());
    	item.click(EMAIL_SERVICE);
    	wait.waitTillElementDisplayed(String.format(AUTOSELECT_EMAILSERVICE,surveyproperties.getEmailService()), MIN_TIME, MAX_TIME);
    	selectValueInDropDown(surveyproperties.getEmailService());
    	field.clearAndSetText(START_DATE, surveyproperties.getStartDate());
    	field.clearAndSetText(END_DATE, surveyproperties.getEndDate());
    //	field.setBooleanField(ANONYMOUS_SUBMISSION, true);
    //	field.assertSelectItemEnabled(ANONYMOUS_SUBMISSION, surveyproperties.isAnonymous());
    	field.setBooleanField(ANONYMOUS_SUBMISSION, surveyproperties.isAnonymous());
    	field.setBooleanField(INTERNAL_SUBMISSION, surveyproperties.isAllowInternalSub());
   // 	field.setBooleanField(INTERNAL_SUBMISSION, surveyproperties.isAnonymous());
    //	field.assertSelectItemEnabled(INTERNAL_SUBMISSION, surveyproperties.isAllowInternalSub());
    	field.clearAndSetText(DESCRIPTION, surveyproperties.getDescription());
   // 	field.clearAndSetText(THANK_YOU, surveyproperties.getThankYou());
    //	field.selectFromDropDown(THANK_YOU, surveyproperties.getThankYou());
    	item.click(THANK_YOU);
    	wait.waitTillElementDisplayed(String.format(AUTOSELECT_THANKYOU,surveyproperties.getThankYou()), MIN_TIME, MAX_TIME);
    	selectValueInDropDown(surveyproperties.getThankYou());
    	field.clearAndSetText(MESSAGE_INPUT, surveyproperties.getMessage());
    	field.clearAndSetText(FOOTER_MSG, surveyproperties.getFooterMsg());
    	field.clearAndSetText(SURVEY_CODE, surveyproperties.getSurveyCode());
    //	field.clearAndSetText(BCKGROUND_COLOR, surveyproperties.getBackgroundColor());
    	field.selectCheckBox(BCKGROUND_COLOR);
    	item.click(SAVE_SURVEY);
    	
    	}


	public void selectValueInDropDown(String emailService) {
		// TODO Auto-generated method stub
		boolean selected = false;
        for(WebElement ele : element.getAllElement("//input[contains(@title, '"+emailService+"')]/following-sibling::span[contains(text(), '"+emailService+"')]")) {
            Log.info("Checking : "+ele.isDisplayed());
            if(ele.isDisplayed()) {
                ele.click();
                selected = true;
                break;
            }
        }
        if(selected != true) {
            throw new RuntimeException("Unable to select element : //input[contains(@title, '"+emailService+"')]/following-sibling::span[contains(text(), '"+emailService+"')]" );
	}
	}
        
        public void selectValueInDropDown1(String thankYou) {
    		// TODO Auto-generated method stub
    		boolean selected = false;
            for(WebElement ele : element.getAllElement("//input[contains(@title, '"+thankYou+"')]/following-sibling::span[contains(text(), '"+thankYou+"')]")) {
                Log.info("Checking : "+ele.isDisplayed());
                if(ele.isDisplayed()) {
                    ele.click();
                    selected = true;
                    break;
                }
            }
            if(selected != true) {
                throw new RuntimeException("Unable to select element : //input[contains(@title, '"+thankYou+"')]/following-sibling::span[contains(text(), '"+thankYou+"')]" );
    	}
    	
    	
	}
}
