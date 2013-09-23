package com.gainsight.sfdc.survey.pages;

public class PublishSurveyPage extends SurveyBasePage {

	private final String PUBLISHED_URL = "//input[@class='jbaraDummySurveyInputCtrl jbaraSurveypublishedURLInput']";
	private final String EMAIL_TEMPLATE = "//select[@class='jbaraDummyEmailTemplateSelectClass']";
	private final String SAVE_PUBLISH_PAGE = "//input[@id='btnsavePublishParam']";
	private final String EDIT_PUBLISH_PAGE = "//input[@id='btnEdit']";
	private final String SEND_TEST_EMAILS = "//input[@id='btnshowTestEmailsForm']";
	private final String BTN_SEND_EMAIL="//input[@id='btnSendEmails']";
	private final String BTN_CANCEL_SENDEMAIL = "//input[@id='btnCancel']";
	private final String BTN_PUBLISH = "//input[@id='btnPublish']";

	public PublishSurveyPage() {
		wait.waitTillElementPresent(PUBLISHED_URL, MIN_TIME, MAX_TIME);
	}

	public void savePublishPage() {

		field.setTextField(PUBLISHED_URL,
				"http://gainsightsampletestautomation.force.com");
		item.selectFromDropDown(EMAIL_TEMPLATE, "");
		// JBara Anonymous Survey with Account Tracking
		// JBara Anonymous Survey without Account Tracking
		// JBara Survey Template
		item.click(SAVE_PUBLISH_PAGE);
	}

	public void clickOnEditInPublish() {

		item.click(EDIT_PUBLISH_PAGE);
	}

	public void clickSendTestEmails() {
		item.click(SEND_TEST_EMAILS);

	}

	public void addSingpleEmailId() {
		field.setTextField(
				"//ul[@id='emailUlContainer']/li/input[@class='dummyEmailText']",
				"");
	}

	public void addrMultipleEmailIds(int num) {

		int i = 1;
		while (i == num) {
			if(num==2){
				field.setTextField("//ul[@id='emailUlContainer']/li/input[@class='dummyEmailText']", "");
				field.click("//ul[@id='emailUlContainer']/li/div/span");
				i=num;
			}
			else{
				field.click("//ul[@id='emailUlContainer']/li/div["+i+"]/span");	
			}
			field.setTextField("//ul[@id='emailUlContainer']/li[" + i
					+ "]/input[@class='dummyEmailText']", "");
			
			i++;

		}

	}
	
	public void clickSendEmail(){
		
		field.click(BTN_SEND_EMAIL);
	}

	public void cancelSendEmail(){
		
		field.click(BTN_CANCEL_SENDEMAIL);
	}
	
	public void clickPublish(){
		
		field.click(BTN_PUBLISH);
	}
}
