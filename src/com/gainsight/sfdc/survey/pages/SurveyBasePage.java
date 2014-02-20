package com.gainsight.sfdc.survey.pages;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.survey.pojo.SurveyData;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SurveyBasePage extends BasePage {

	private final String READY_INDICATOR = "//a[contains(text(),'Survey')]";
	private final String NEW_SURVEY = "//input[@value='New']";

	private final String BACK = "//a[contains(text(),'« Back')]";
	private final String NEXT_PAGE = "css=span.ui-icon.ui-icon-seek-next";
	private final String PREV_PAGE = "css=span.ui-icon.ui-icon-seek-prev";
	private final String FIRST_PAGE = "css=span.ui-icon.ui-icon-seek-end";
	private final String LAST_PAGE = "css=span.ui-icon.ui-icon-seek-first";
	private final String SELECT_NUMBER = "//select[@class='ui-pg-selbox']";
	private final String PAGE_INFO = "css=div.ui-paging-info";
	
	private boolean ACCEPT_NEXT_ALERT = true;

	public SurveyBasePage() {
		wait.waitTillElementPresent(NEW_SURVEY, MIN_TIME, MAX_TIME);
		
	}

	public NewSurveyPage clickOnNew() {
		item.click(NEW_SURVEY);
		return new NewSurveyPage();
	}

	public NewSurveyPage clickOnEdit(SurveyData surveyData) {
        String xPath = buildSurveyXpath(surveyData);
        xPath = xPath+"/following-sibling::td[@aria-describedby='SurveyList_Actions']/a[@title='Edit']";
		item.click(xPath);
		return new NewSurveyPage();
	}

	public SurveyDesignPage clickOnDesign(SurveyData surveyData) {
        String xPath = buildSurveyXpath(surveyData);
        xPath = xPath+"/following-sibling::td[@aria-describedby='SurveyList_Actions']/a[@title='Design']";
        item.click(xPath);
        return new SurveyDesignPage();
	}

	public PublishSurveyPage clickOnPublish(SurveyData surveyData) {
        String xPath = buildSurveyXpath(surveyData);
        xPath = xPath+"/following-sibling::td[@aria-describedby='SurveyList_Actions']/a[@title='Publish']";
        item.click(xPath);
        return new PublishSurveyPage();
	}

	public DistributeSurveyPage clickOnDistribute(SurveyData surveyData) {
        String xPath = buildSurveyXpath(surveyData);
        xPath = xPath+"/following-sibling::td[@aria-describedby='SurveyList_Actions']/a[@title='Distribute']";
        item.click(xPath);
        return new DistributeSurveyPage();
	}

	public CloseSurveyPage clickOnClose(SurveyData surveyData) {
        String xPath = buildSurveyXpath(surveyData);
        xPath = xPath+"/following-sibling::td[@aria-describedby='SurveyList_Actions']/a[@title='Close']";
        item.click(xPath);
        return new CloseSurveyPage();
	}

	public PreviewSurveyPage clickOnPreview(SurveyData surveyData) {
        String xPath = buildSurveyXpath(surveyData);
        xPath = xPath+"/following-sibling::td[@aria-describedby='SurveyList_Actions']/a[@title='Preview']";
        item.click(xPath);
        return new PreviewSurveyPage();
	}

	public AnalyzeSurveyPage clickOnAnalyze(SurveyData surveyData) {
        String xPath = buildSurveyXpath(surveyData);
        xPath = xPath+"/following-sibling::td[@aria-describedby='SurveyList_Actions']/a[@title='Analyze']";
        item.click(xPath);
        return new AnalyzeSurveyPage();
	}

	public SurveyBasePage DeleteSurvey(SurveyData surveyData) {
        String xPath = buildSurveyXpath(surveyData);
        xPath = xPath+"/following-sibling::td[@aria-describedby='SurveyList_Actions']/a[@title='Delete']";
        item.click(xPath);
		modal.accept();
        return this;
    }

    public boolean isSurveyPresent(SurveyData surveyData) {
        boolean result = false;
        String xPath = buildSurveyXpath(surveyData);
        List<WebElement> surveyList = element.getAllElement(xPath);
        if(surveyList.size() >0) {
            result = true;
        }
        return result;
    }

    public String buildSurveyXpath(SurveyData surveyData) {
        //td/span[contains(text(), 'Design')]/parent::td/following-sibling::td[text()='Sample:Sample']/following-sibling::td[text()='02/13/2014']
        String xPath = "//td[@aria-describedby='SurveyList_Status__c']/span[contains(text(),"+"'"+surveyData.getStatus()+"')]"
                +"/parent::td[@aria-describedby='SurveyList_Status__c']/following-sibling::td[@aria-describedby='SurveyList_Code__c'and text()='"
                +surveyData.getCode()+":"+surveyData.getTitle()+"']";
        if(surveyData.getStartDate() != null) {
            xPath = xPath+"/following-sibling::td[@aria-describedby='SurveyList_StartDate__c' and text()='"+surveyData.getStartDate()+"']";
        }
        if(surveyData.getEndDate() != null) {
            xPath = xPath+"/following-sibling::td[@aria-describedby='SurveyList_EndDate__c'and text()='"+surveyData.getEndDate()+"']";
        }
        Report.logInfo("Survey xpath in list :" +xPath);
        return xPath;
    }

	public void navigateToNextPage(){
		
		try{
		if(item.isElementPresent(NEXT_PAGE)){
			
			item.click(NEXT_PAGE);
		}

		}
		catch(Exception e){
			
			e.printStackTrace();
		}
		
	}
	
	public void navigateToPreviousPage(){
		try{
			if(item.isElementPresent(PREV_PAGE)){
				
				item.click(PREV_PAGE);
			}

			}
			catch(Exception e){
				
				e.printStackTrace();
			}		
	}

	public void navigateToFirstPage(){
		try{
			if(item.isElementPresent(FIRST_PAGE)){
				
				item.click(FIRST_PAGE);
			}

			}
			catch(Exception e){
				
				e.printStackTrace();
			}		
	}

	public void navigateToLastPage(){
		try{
			if(item.isElementPresent(LAST_PAGE)){
				
				item.click(LAST_PAGE);
			}

			}
			catch(Exception e){
				
				e.printStackTrace();
			}		
	}
	
	public void chooseNumberOfSurveysPerPage(String num){
		
		if(num.equals("30")){
			
			BasePage base = new BasePage();
			base.clickOnSurveyTab();	
		}
		else
		{
			item.selectFromDropDown(SELECT_NUMBER, num);
		}
	}
	
	public int getNumberOfSurveyCount(){
		String pageinfo = item.getText(PAGE_INFO);
		String[] num = pageinfo.split(" ");
		return Integer.parseInt(num[5]);
	}

}
