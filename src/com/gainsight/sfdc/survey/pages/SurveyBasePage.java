package com.gainsight.sfdc.survey.pages;

import static org.testng.AssertJUnit.assertTrue;

import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.survey.pojo.SurveyData;
import com.gainsight.sfdc.util.Utilities;

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
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
		
	}

	public NewSurveyPage clickOnNew() {
		item.click(NEW_SURVEY);
		return new NewSurveyPage();
	}

	public NewSurveyPage clickOnEdit(String surveystatus) {

		String surveyname=getSurveyWithStatus(surveystatus);
		int i=getSurveyRowNum(surveyname);
		item.click("//tr[@id=\""+i+"\"]/td[8]/a/span[@class='gridLinkEditSurvey']");
		return new NewSurveyPage();
	}

	public SurveyDesignPage clickOnDesign(String surveystatus) {

		String surveyname=getSurveyWithStatus(surveystatus);
		int i=getSurveyRowNum(surveyname);
		item.click("//tr[@id=\""+i+"\"]/td[8]/a/span[@class='gridLinkDesignSurvey']");
		return new SurveyDesignPage();
	}

	public PublishSurveyPage clickOnPublish(String surveystatus) {

		String surveyname=getSurveyWithStatus(surveystatus);
		int i=getSurveyRowNum(surveyname);
		item.click("//tr[@id=\""+i+"\"]/td[8]/a/span[@class='gridLinkPublishSurvey']");
		return new PublishSurveyPage();
	}

	public DistributeSurveyPage clickOnDistribute(String surveystatus) {

		String surveyname=getSurveyWithStatus(surveystatus);
		int i=getSurveyRowNum(surveyname);
		item.click("//tr[@id=\""+i+"\"]/td[8]/a/span[@class='gridLinkDistributeSurvey']");
		return new DistributeSurveyPage();
	}

	public CloseSurveyPage clickOnClose(String surveystatus) {

		String surveyname=getSurveyWithStatus(surveystatus);
		int i=getSurveyRowNum(surveyname);
		item.click("//tr[@id=\""+i+"\"]/td[8]/a/span[@class='gridLinkClosedSurvey']");
		return new CloseSurveyPage();
	}

	public PreviewSurveyPage clickOnPreview(String surveystatus) {

		String surveyname=getSurveyWithStatus(surveystatus);
		int i=getSurveyRowNum(surveyname);
		item.click("//tr[@id=\""+i+"\"]/td[8]/a/span[@class='gridLinkPreviewSurvey']");
		return new PreviewSurveyPage();
	}

	public AnalyzeSurveyPage clickOnAnalyze(String surveystatus) {

		String surveyname=getSurveyWithStatus(surveystatus);
		int i=getSurveyRowNum(surveyname);
		item.click("//tr[@id=\""+i+"\"]/td[8]/a/span[@class='gridLinkAnalyzeSurvey']");
		return new AnalyzeSurveyPage();
	}

	public void DeleteSurvey(String surveystatus) {

		String surveyname=getSurveyWithStatus(surveystatus);
		int i=getSurveyRowNum(surveyname);
		item.click("//tr[@id=\""+i+"\"]/td[8]/a/span[@class='gridLinkDeleteSurvey']");
		try {
			modal.exists();
			String alerttext = modal.getText();
			if (ACCEPT_NEXT_ALERT) {
				modal.accept();
			} else {
				modal.dismiss();
			}

			assertTrue(alerttext.matches("^Are you sure [\\s\\S]$"));

		} finally {
			ACCEPT_NEXT_ALERT = true;
		}

	}

	public SurveyBasePage clickOnBack() {

		item.click(BACK);
		return this;
	}

	public String getSurveyWithStatus(String status) {

		try {
			int i = getNumberOfSurveyCount();
			for(int j=0;j<i;j++){
				
				if(j%30==0){
					navigateToNextPage();
					item.wait();
				}
				if (item.getText("//tr[@id=" + j + "]/td[2]").equalsIgnoreCase(
						status)) {
					return item.getText("//tr[@id=" + j + "]/td[3]");
				}
			}
			
			NewSurveyPage surveypage = clickOnNew();
			SurveyData sdata = new SurveyData();
			String surveyname=Utilities.getRandomString();
			sdata.setCode(surveyname);
			sdata.setTitle("test");
			sdata.setStartDate(Utilities.generateDate(0));
			sdata.setEndDate(Utilities.generateDate(2));
			sdata.setAnanymous(false);
			sdata.setImageName("TestImage.png");
			sdata.setTUOption("Message");
			
			SurveyDesignPage surveydesign = surveypage.createNewSurvey(sdata);
			AddQuestionsPage addquestions = surveydesign.clickOnNewQuestion();
			SurveyBasePage base = addquestions.clickOnBack();
			if(status.equalsIgnoreCase("Design"))
				return surveyname;
			PublishSurveyPage publishsurvey = base.clickOnPublish("Design");
			publishsurvey.savePublishPage();
			publishsurvey.clickPublish();
			publishsurvey.clickOnBack();
			if(status.equalsIgnoreCase("Publish"))
				return surveyname;
			
			DistributeSurveyPage distributepage = base.clickOnDistribute("Publish");
			distributepage.addParticipants();
			
			if(status.equalsIgnoreCase("Distribute"))
				return surveyname;				
			CloseSurveyPage surveyclose = distributepage.clickOnClose("Distribute");
			surveyclose.clickOnYes();
			surveyclose.clickOnBack();
			if(status.equalsIgnoreCase("Closed"))
				return surveyname;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "No Survey found with status: " + status;
	}
	
	public int getSurveyRowNum(String surveyname) {

		try {
			int i = item.getElementCount("//tr");

			while (i > 0) {

				if (item.getText("//tr[@id=" + i + "]/td[3]").equalsIgnoreCase(
						surveyname)) {
					return i;
				}
				i--;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public String[] getAllSurveyNames() {
		String[] surveys = null;

		try {

			int i = item.getElementCount("//tr");

			while (i > 0) {

				surveys[i] = item.getText("//tr[@id=" + i + "]/td[3]");
				i--;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return surveys;

	}

	public String getStartDate(String survey) {

		try {
			int i = item.getElementCount("//tr");

			while (i > 0) {
				if (item.getText("//tr[@id=" + i + "]/td[3]").equalsIgnoreCase(
						survey)) {
					return item.getText("//tr[@id=" + i + "]/td[4]");
				}
				i--;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "No Survey found with name: " + survey;
	}
	
	public String getEndDate(String survey) {

		try {
			int i = item.getElementCount("//tr");

			while (i > 0) {
				if (item.getText("//tr[@id=" + i + "]/td[3]").equalsIgnoreCase(
						survey)) {
					return item.getText("//tr[@id=" + i + "]/td5]");
				}
				i--;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "No Survey found with name: " + survey;
	}

	public String getRespondedCount(String survey) {

		try {
			int i = item.getElementCount("//tr");

			while (i > 0) {
				if (item.getText("//tr[@id=" + i + "]/td[3]").equalsIgnoreCase(
						survey)) {
					return item.getText("//tr[@id=" + i + "]/td[6]");
				}
				i--;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "No Survey found with name: " + survey;
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
