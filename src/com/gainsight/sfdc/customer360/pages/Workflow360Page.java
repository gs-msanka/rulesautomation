package com.gainsight.sfdc.customer360.pages;

import java.util.ArrayList;

import com.gainsight.sfdc.workflow.pages.WorkflowPage;
import com.gainsight.sfdc.workflow.pojos.CTA;
import com.gainsight.sfdc.workflow.pojos.Task;

public class Workflow360Page extends Customer360Page{
    private final String READY_INDICATOR            = "//div[@class='gs_section_title']/h1[contains(.,'Scorecard')]";
	WorkflowPage wfPage;
	
	public Workflow360Page(String value){
        wfPage=new WorkflowPage("360 Page");
	}
	
	//TBD- if any specific page objects related to 360 - cockpit section are to be modified
	public void createCTA(CTA cta){
		cta.setFromCustomer360(true);
		wfPage.createCTA(cta);
	}

	public boolean isCTADisplayed(CTA cta) {
		cta.setFromCustomer360(true);
		return wfPage.isCTADisplayed(cta);
	}

	public void addTaskToCTA(CTA cta, ArrayList<Task> tasks) {
		cta.setFromCustomer360(true);
		wfPage.addTaskToCTA(cta, tasks);
	}

	public boolean isTaskDisplayedUnderCTA(CTA cta, Task task) {
		return wfPage.isTaskDisplayedUnderCTA(cta, task);
	}

	public Workflow360Page applyPlayBook(CTA cta, String playBookName,
			ArrayList<Task> tasks, boolean isApply) {
		wfPage.applyPlayBook(cta, playBookName, tasks, isApply);		
		return this;
	}

	public void createMilestoneForCTA(CTA cta) {
		cta.setFromCustomer360(true);
		wfPage.createMilestoneForCTA(cta);
	}

	public void closeCTA(CTA cta, boolean hasOpenTasks) {
		cta.setFromCustomer360(true);
		wfPage.closeCTA(cta, hasOpenTasks);
	}

	public void snoozeCTA(CTA cta) {
		cta.setFromCustomer360(true);
		wfPage.snoozeCTA(cta);
	}
	
}
