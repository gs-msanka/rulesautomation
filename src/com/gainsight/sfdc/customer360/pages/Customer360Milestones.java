package com.gainsight.sfdc.customer360.pages;

public class Customer360Milestones extends Customer360Page {
	
	private final String NO_MILESTONES_MSG          = "//div[@class='noMilestone noDataFound' and contains(.,'No Milestones Found')]";
	private final String MILESTONES_SUB_TAB         = "//li[@data-tabname='Milestones']/a[contains(.,'Milestones')]";
	private final String ADD_MILESTONES             = "//a[@class='addNewMilestone']";
	private final String DATE_FIELD                 = "//input[@id='DateId']";
	private final String MILESTONE_DROP_BOX         = "//select[@id='MilestoneOptions']/following-sibling::button";
	private final String OPPORTUNITY_DROP_BOX       = "//select[@id='OppOptions']/following-sibling::button";
	private final String COMMENT_FIELD              = "//textarea[@id='CommentsId']";
	private final String SAVE_BUTTON                = "//a[@class='btn_save']";
	private final String CLOSE_BUTTON               = "//a[@class='btn_cancel']";
	private final String CLOSE_X                    = "//button[@title='close']/span[@title='Close']";
	private final String MILESTONES_TABLE           = "//table[@class='gs_features_grid gs_milestones_grid']";
	private final String MILESTONES_TABLE_DATA_GRID = MILESTONES_TABLE+"/tbody";
	private final String EDIT_LINK_ICON             = MILESTONES_TABLE+"/tbody/tr[%d]/td[@class='edit-icon']";
	private final String DELETE_LINK_ICON           = MILESTONES_TABLE+"/tbody/tr[%d]/td[@class='delete-icon']";
	private final String MILESTONES_POPUP           = "//div[@class='ui-dialog-titlebar ui-widget-header ui-corner-all ui-helper-clearfix']/span[contains(text(), 'Milestone')]";
	private final String MILESTONE_ROW_CHECK        = MILESTONES_TABLE+"/tbody/tr[%d]";
    private final String NO_OF_ROWS                 = "//table[@class='gs_features_grid gs_milestones_grid']/tbody/tr";
    private final String MILESTONE_ROW              = "//table[@class='gs_features_grid gs_milestones_grid']"+
                                                       "/tbody/tr/td[1][contains(.,'%s')]"+
                                                       "/following-sibling::td[1]/span[@style='background-color:%s;']"+
                                                       "/following-sibling::span[contains(.,'%s')]/parent::td"+
                                                       "/following-sibling::td[1][contains(.,'%s')]"+
                                                       "/following-sibling::td[1][contains(.,'%s')]";


	
	public void gotoMilestonesSubtab(){
		item.click(MILESTONES_SUB_TAB);
		wait.waitTillElementDisplayed(MILESTONES_TABLE, MIN_TIME, MAX_TIME);
		
	}
	
	public void clickOnAddMilestones(){
		item.click(ADD_MILESTONES);
		wait.waitTillElementDisplayed(MILESTONES_POPUP, MIN_TIME, MAX_TIME);
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
		item.clearAndSetText(DATE_FIELD, date);
	}
	
	public void selectMileStone(String Milestone){
		item.click(MILESTONE_DROP_BOX);
		item.click("//ul[@class='ui-multiselect-checkboxes ui-helper-reset']/descendant::span[contains(text(), '"+Milestone+"')]");
	}
	
	public void selectOpportunityForMilestone(String Opportunity){
		item.click(OPPORTUNITY_DROP_BOX);
        item.click("//ul[@class='ui-multiselect-checkboxes ui-helper-reset']/descendant::span[contains(text(), '"+Opportunity+"')]");
	}
	
	public void addComments(String Comments){
		item.clearAndSetText(COMMENT_FIELD, Comments);
	}
	
	public boolean checkMilestoneRow(String date,String Color,String Milestone,String Opportunity,String Comments){
		return item.isElementPresent(String.format(MILESTONE_ROW, date,Color,Milestone,Opportunity,Comments));
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
		wait.waitTillElementDisplayed(MILESTONES_POPUP, MIN_TIME, MAX_TIME);
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

	public int getCurrentNoOfRows() {
		return element.getElementCount(NO_OF_ROWS);
	}
}
