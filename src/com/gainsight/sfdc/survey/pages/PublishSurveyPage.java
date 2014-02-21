package com.gainsight.sfdc.survey.pages;

public class PublishSurveyPage extends SurveyBasePage {

	private final String PUBLISHED_URL_TEXT             = "//input[@class='jbaraDummySurveyInputCtrl jbaraSurveypublishedURLInput']";
	private final String EMAIL_TEMPLATE_SELECT          = "//select[@class='jbaraDummyEmailTemplateSelectClass']";
	private final String SAVE_PUBLISH_BUTTON            = "btnsavePublishParam";
	private final String EDIT_PUBLISH_BUTTON            = "btnEdit";
	private final String SEND__TEST_EMAILS_BUTTON       = "btnshowTestEmailsForm";
	private final String SEND_EMAIL_BUTTON              = "btnSendEmails";
	private final String CANCEL_SEND_EMAIL_BUTTON       = "btnCancel";
	private final String PUBLISH_BUTTON                 = "btnPublish";
    private final String SURVEY_STATUS                  = "tdSurveyStatus";
    private final String ERROR_MESSAGES                 = "//div[@class='message errorM3']";
    private final String TEST_EMAIL_ADD_BUTTON          = "//span[@class='ui-icon addIconClass emailOption_addIcon' and @title='Add']";
    private final String TESTEMAIL_REMOVE_BUTTON        = "//span[@class='ui-icon removeIconClass emailOption_removeIcon' and @title='Remove']";

	public PublishSurveyPage() {
		wait.waitTillElementPresent(SURVEY_STATUS, MIN_TIME, MAX_TIME);
	}

	public PublishSurveyPage saveSurveyPublishDetails(String publishUrl, String emailTemplate) {
        field.setTextField(PUBLISHED_URL_TEXT, publishUrl);
		item.selectFromDropDown(EMAIL_TEMPLATE_SELECT, emailTemplate); // JBara Anonymous Survey with Account Tracking, JBara Anonymous Survey without Account Tracking, JBara Survey Template
        item.click(SAVE_PUBLISH_BUTTON);
        wait.waitTillElementDisplayed(PUBLISH_BUTTON, MIN_TIME, MAX_TIME);
        return this;
	}

	public PublishSurveyPage clickOnEditInPublish() {
        item.click(EDIT_PUBLISH_BUTTON);
        wait.waitTillElementDisplayed(SAVE_PUBLISH_BUTTON, MIN_TIME, MAX_TIME);
        return this;
	}

	public PublishSurveyPage clickSendTestEmails() {
		item.click(SEND__TEST_EMAILS_BUTTON);
        wait.waitTillElementDisplayed(SEND_EMAIL_BUTTON, MIN_TIME, MAX_TIME);
        return this;
	}

    public PublishSurveyPage cancelSendEmail(){
        field.click(CANCEL_SEND_EMAIL_BUTTON);
        return this;
    }

    public PublishSurveyPage clickPublish(){
        field.click(PUBLISH_BUTTON);
        return this;
    }

    public PublishSurveyPage sendTestEmails(String[] emailList) {
        if(emailList != null && emailList.length == 1) {
            field.setTextField("//ul[@id='emailUlContainer']/li[@class='dummyEmailItemLi']/input[@class='dummyEmailText']", emailList[0]);
        } else if (emailList != null && emailList.length > 1) { //expecting alert exception.
            for(int i=1; i < emailList.length; i++) {
                item.click(TEST_EMAIL_ADD_BUTTON);
            }
            amtDateUtil.stalePause();
            int a = element.getElementCount("//input[@class='dummyEmailText']");
            for(int i=0; i < emailList.length; i++) {
                field.setTextField("//ul[@id='emailUlContainer']/li[i]/input[@class='dummyEmailText']", emailList[i]);
            }
        }
        item.click(SEND_EMAIL_BUTTON);
        return this;
    }

    public boolean checkSurveyStatus(String status) {
        String eleText = element.getText(SURVEY_STATUS);
        if(eleText.equalsIgnoreCase(status)) {
            return true;
        }
        return false;
    }


    public boolean checkErrorMsg(String errMsg) {
        return false;
    }



}
