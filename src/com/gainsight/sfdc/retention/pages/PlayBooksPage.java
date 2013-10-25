package com.gainsight.sfdc.retention.pages;

import com.gainsight.sfdc.pages.BasePage;

public class PlayBooksPage extends BasePage {

	private final String READY_INDICATOR = "//div[@class='ga-addPlaybookIcon btn']";
	
	
	public PlayBooksPage() {
		wait.waitTillElementDisplayed(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	
	private final String ADD_PLAYBOOK_BUTTON = "//div[@class='ga-addPlaybookIcon btn']";
	private final String ISALERT_PLAYBOOK_CHECK = "ga-alert";
	private final String ISEVENT_PLAYBOOK_CHECK = "ga-event";
	private final String PBNAME_INPUT = "//input[@class='ga-playbookName pbInputVal']";
	private final String PBDES_INPTU  = "//textarea[@class='ga-playbookDes pbInputVal']";
	private final String TASK_SUB_INPUT = "//input[@class='Subject__cInputCls taskParamControlDataInput']";
	private final String TASK_RELDATECOUNT_INPUT = "//input[@class='Date__cInputCls taskParamControlDataInput']";
	private final String TASK_PRIORITY_SELECT = "//select[@class='Priority__cInputCls taskParamControlDataInput']";
	private final String TASK_STATUS_SELECT = "//select[@class='Status__cInputCls taskParamControlDataInput']";
	private final String CREATE_BUTTON = "//input[@class='ga-btnPrimary ga-savePlaybook' and value='Create']";
	private final String CANCEL_BUTTON = "//input[@class='ga-btnSecondary ga-cancelPlaybook']";
	private final String UPDATE_BUTTON = "//input[@class='ga-btnPrimary ga-savePlaybook' and value='Update']";
	private final String ADD_TASK_BUTTON = "ga-addTaskIconId";
	private final String TASK_SERCH_INPUT = "//input[@class='ga-searchInput ga-searchTasks'";
	private final String TASK_SEARCH_CLEAR_BUTTON = "//input[@class='ga-clearBtn ga-searchTaskClearBtn']";
	private final String PB_EDIT_LINK =  "//a[@class='ga-icn ga-edit ga-editPlaybook']";
	private final String PB_DELETE_LINK = "//a[@class='ga-icn ga-ignore ga-ignorePlaybook']";

	
}
