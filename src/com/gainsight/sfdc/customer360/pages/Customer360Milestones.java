package com.gainsight.sfdc.customer360.pages;

public class Customer360Milestones extends Customer360Page {
	
	private final String MILESTONES_SUB_TAB="//li[@data-tabname='Milestones']/a[contains(.,'Milestones')]";
	private final String ADD_MILESTONES="//a[@class='addNewMilestone']";
	private final String DATE_FIELD="//input[@id='DateId']";
	private final String DATE_MONTH="";
	private final String DATE_YEAR="";
	private final String DATE_DAY="";
	private final String MILESTONE_DROP_BOX="//select[@id='MilestoneOptions']";
	private final String OPPORTUNITY_DROP_BOX="//select[@id='OppOptions']";
	private final String COMMENT_FIELD="//textarea[@id='CommentsId']";
	private final String SAVE_BUTTON="//a[@class='btn_save']";
	private final String CLOSE_BUTTON="//a[@class='btn_cancel']";
	private final String CLOSE_X="//button[@title='close']/span[@title='Close']";
	private final String EDIT_LINK_ICON="//td[@class='edit-icon']";
	private final String DELETE_LINK_ICON="//td[@class='delete-icon']";
	private final String MILESTONES_TABLE="//table[@class='gs_features_grid gs_milestones_grid']";
	private final String MILESSTONES_ADD_FRAME="//div[@class='ui-widget-overlay ui-front']";

	
	public void gotoMilestonesSubtab(){
		item.click(MILESTONES_SUB_TAB);
		wait.waitTillElementDisplayed(MILESTONES_TABLE, MIN_TIME, MAX_TIME);
		
	}
	
	public void clickOnAddMilestones(){
		item.click(ADD_MILESTONES);
		wait.waitTillElementDisplayed(MILESSTONES_ADD_FRAME, MIN_TIME, MAX_TIME);
	}
	
	public void setDateInField(String date){
		
		/*JB-3102 needs to be fixed to select date from date-picker
		 item.click(DATE_FIELD);
		//sample for date = "2014-December-13"
		String dElements[]=date.split("-");
		item.click(DATE_YEAR);
		item.click(DATE_MONTH);
		item.click(DATE_DAY);*/
		
		//(OR) simply enter the date in the date field
		item.clearAndSetText(DATE_FIELD, date);
	}
	
	public void selectMileStone(String Milestone){
		item.click(MILESTONE_DROP_BOX);
		item.click(MILESTONE_DROP_BOX+"/option[contains(.,'"+Milestone+"')]");
	}
	
	public void selectOpportunityForMilestone(String Opportunity){
		item.click(OPPORTUNITY_DROP_BOX);
		item.click(OPPORTUNITY_DROP_BOX+"/option[contains(.,'"+Opportunity+"')]");
	}
	
	public void addComments(String Comments){
		item.clearAndSetText(COMMENT_FIELD, Comments);
	}
	
	public void clickOnSave(){
		item.click(SAVE_BUTTON);
		wait.waitTillElementDisplayed(MILESTONES_TABLE, MIN_TIME, MAX_TIME);
	}
	
	public void clickOnCloseButton(){
		item.click(CLOSE_BUTTON);
		wait.waitTillElementDisplayed(MILESTONES_TABLE, MIN_TIME, MAX_TIME);
	}
	
	public void clickOnCloseX(){
		item.click(CLOSE_X);
		wait.waitTillElementDisplayed(MILESTONES_TABLE, MIN_TIME, MAX_TIME);
	}
	
	public void clickOnEditFeature(){
		item.click(EDIT_LINK_ICON);
		wait.waitTillElementDisplayed(MILESSTONES_ADD_FRAME, MIN_TIME, MAX_TIME);
	}
	
	public void clickOnDeleteFeature(){
		item.click(DELETE_LINK_ICON);
		driver.switchTo().alert().accept();
		wait.waitTillElementDisplayed(MILESTONES_TABLE, MIN_TIME, MAX_TIME);
	}
}
