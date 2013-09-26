package com.gainsight.sfdc.survey.pages;

import static org.testng.AssertJUnit.assertTrue;

import com.gainsight.sfdc.pages.BasePage;

public class SurveyBasePage extends BasePage {

	private final String READY_INDICATOR = "//a[contains(text(),'Survey')]";
	private final String NEW_SURVEY = "//input[@value='New']";
	private final String EDIT_SURVEY = "css=span.gridLinkEditSurvey";
	private final String DESIGN_SURVEY = "css=span.gridLinkDesignSurvey";
	private final String PUBLISH_SURVEY = "css=span.gridLinkPublishSurvey";
	private final String DISTRIBUTE_SURVEY = "css=span.gridLinkDistributeSurvey";
	private final String CLOSE_SURVEY = "css=span.gridLinkClosedSurvey";
	private final String PREVIEW_SURVEY = "css=span.gridLinkPreviewSurvey";
	private final String ANALYZE_SURVEY = "css=span.gridLinkAnalyzeSurvey";
	private final String DELETE_SURVEY = "css=span.gridLinkDeleteSurvey";

	private final String BACK = "//a[contains(text(),'« Back')]";

	private boolean ACCEPT_NEXT_ALERT = true;

	public SurveyBasePage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

	public NewSurveyPage clickOnNew() {
		item.click(NEW_SURVEY);
		return new NewSurveyPage();
	}

	public NewSurveyPage clickOnEdit(){
		
		item.click(EDIT_SURVEY);
		return new NewSurveyPage();
	}
	
	public DesignSurveyPage clickOnDesign(){
		
		item.click(DESIGN_SURVEY);
		return new DesignSurveyPage();
	}
	
	public PublishSurveyPage clickOnPublish(){
		
		item.click(PUBLISH_SURVEY);
		return new PublishSurveyPage();
	}

	public DistributeSurveyPage clickOnDistribute(){
		
		item.click(DISTRIBUTE_SURVEY);
		return new DistributeSurveyPage();
	}
	
	public CloseSurveyPage clickOnClose(){
		
		item.click(CLOSE_SURVEY);
		return new CloseSurveyPage();
	}
	
	public PreviewSurveyPage clickOnPreview(){
		
		item.click(PREVIEW_SURVEY);
		return new PreviewSurveyPage();
	}
	
	public AnalyzeSurveyPage clickOnAnalyze(){
		
		item.click(ANALYZE_SURVEY);
		return new AnalyzeSurveyPage();
	}
	
	public void DeleteSurvey(){
		
		item.click(DELETE_SURVEY);
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
	
	public SurveyBasePage clickOnBack(){
		
		item.click(BACK);
		return this;
	}
	
	
}
