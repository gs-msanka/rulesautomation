package com.gainsight.sfdc.customer360.pages;

import org.openqa.selenium.By;

public class Customer360Milestones extends Customer360Page {
	
	private final String NO_MILESTONES_MSG = "//div[@class='noMilestone noDataFound' and contains(.,'No Milestones Found')]";
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
	private final String MILESTONES_TABLE="//table[@class='gs_features_grid gs_milestones_grid']";
	private final String MILESTONES_TABLE_DATA_GRID=MILESTONES_TABLE+"/tbody";
	private final String MILESTONE_DATE=MILESTONES_TABLE+"/tbody/tr[%d]/td[1]";
	private final String MILESTONE_COLOR=MILESTONES_TABLE+"/tbody/tr[%d]/td[2]/span[@style='background-color:%s;']";
	private final String MILESTONE_NAME=MILESTONES_TABLE+"/tbody/tr[%d]/td[2]/span[contains(.,'%s')]";
	private final String MILESTONE_OPPOR=MILESTONES_TABLE+"/tbody/tr[%d]/td[3]";
	private final String MILESTONE_COMMENTS=MILESTONES_TABLE+"/tbody/tr[%d]/td[4]";
	private final String EDIT_LINK_ICON=MILESTONES_TABLE+"/tbody/tr[%d]/td[@class='edit-icon']";
	private final String DELETE_LINK_ICON=MILESTONES_TABLE+"/tbody/tr[%d]/td[@class='delete-icon']";
	private final String MILESSTONES_ADD_FRAME="//div[@class='ui-widget-overlay ui-front']";
	private final String MILESTONE_ROW_CHECK=MILESTONES_TABLE+"/tbody/tr[%d]";

	
	public void gotoMilestonesSubtab(){
		item.click(MILESTONES_SUB_TAB);
		wait.waitTillElementDisplayed(MILESTONES_TABLE, MIN_TIME, MAX_TIME);
		
	}
	
	public void clickOnAddMilestones(){
		item.click(ADD_MILESTONES);
		wait.waitTillElementDisplayed(MILESSTONES_ADD_FRAME, MIN_TIME, MAX_TIME);
	}
	
	public boolean isHeaderPresent(){
		wait.waitTillElementDisplayed(MILESTONES_TABLE, MIN_TIME, MAX_TIME);
		return true;
	}
	
	
	public boolean isHeaderItemPresent(String columnName){
		wait.waitTillElementDisplayed(MILESTONES_TABLE+ "/thead/tr/th[text()='"+ columnName+ "']",MIN_TIME,MAX_TIME);
		return true;
	}
	
	public boolean isMsTableDataPresent(){
		System.out.println("xpath of table data-->"+MILESTONES_TABLE_DATA_GRID);
		wait.waitTillElementDisplayed(MILESTONES_TABLE_DATA_GRID, MIN_TIME, MAX_TIME);
		return true;
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
	
	public boolean checkMilestoneDate(String date,int col){
		amtDateUtil.stalePause();
		System.out.println("got date-->"+date+", got date from UI-->"+item.getText(String.format(MILESTONE_DATE,col)));
		return((item.getText(String.format(MILESTONE_DATE,col))).compareTo(date)==0 ? true : false);
	}
	
	public boolean checkMilestoneColor(String color,int col){	
		amtDateUtil.stalePause();
		System.out.println("got color-->"+color+", got color from UI-->"+String.format(MILESTONE_COLOR,col,color));
		wait.waitTillElementDisplayed(String.format(MILESTONE_COLOR,col,color), MIN_TIME, MAX_TIME);
		return true;
	}
	
	public boolean checkMilestoneName(String Name,int col){
		amtDateUtil.stalePause();
		System.out.println("got name-->"+Name+", got name from UI-->"+String.format(MILESTONE_NAME,col,Name));
		wait.waitTillElementDisplayed(String.format(MILESTONE_NAME,col,Name), MIN_TIME, MAX_TIME);
		return true;
	}
	
	public boolean checkMilestoneOpportunity(String oppor,int col){
		amtDateUtil.stalePause();
		System.out.println("got opor-->"+oppor+", got op from UI-->"+item.getText(String.format(MILESTONE_OPPOR,col)));
		return((item.getText(String.format(MILESTONE_OPPOR,col))).compareTo(oppor)==0 ? true : false);
	}
	
	public boolean checkMilestoneComments(String comments,int col){
		amtDateUtil.stalePause();
		return ((item.getText(String.format(MILESTONE_COMMENTS,col))).compareTo(comments)==0 ? true : false);
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
	
	public void clickOnEditMilestone(int row){
		item.click(String.format(EDIT_LINK_ICON,row));
		wait.waitTillElementDisplayed(MILESSTONES_ADD_FRAME, MIN_TIME, MAX_TIME);
	}
	
	public void clickOnDeleteMilestone(int row){
		amtDateUtil.stalePause();
		item.click(String.format(DELETE_LINK_ICON,row));
		driver.switchTo().alert().accept();
		amtDateUtil.stalePause();
		//wait.waitTillElementDisplayed(MILESTONES_TABLE, MIN_TIME, MAX_TIME);
	}
	
	public boolean isRowPresentAfterDelete(int row)
	{
		wait.waitTillElementNotPresent(String.format(MILESTONE_ROW_CHECK, row), MIN_TIME, MAX_TIME);
		return true;
	}

	public boolean isNoMilestoneMessagePresent() {
		amtDateUtil.stalePause();
		wait.waitTillElementDisplayed(NO_MILESTONES_MSG, MIN_TIME, MAX_TIME);
		return true;
	}
}
