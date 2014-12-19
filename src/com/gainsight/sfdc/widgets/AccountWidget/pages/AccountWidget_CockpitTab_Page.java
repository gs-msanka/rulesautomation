package com.gainsight.sfdc.widgets.AccountWidget.pages;

import com.gainsight.sfdc.workflow.pages.WorkflowPage;
import com.gainsight.sfdc.workflow.pojos.CTA;

public class AccountWidget_CockpitTab_Page extends AccountWidgetPage{
	WorkflowPage  wfPage;
	
	public AccountWidget_CockpitTab_Page(){
		wfPage=new WorkflowPage("Account Widget");	
	}
	public void createCTA(CTA cta) {
		wfPage.createCTA(cta);
	}

	public boolean isCTADisplayed(CTA cta) {
		return wfPage.isCTADisplayed(cta);
	}
	

}
