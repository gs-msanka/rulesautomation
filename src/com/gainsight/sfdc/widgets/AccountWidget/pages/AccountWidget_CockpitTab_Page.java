package com.gainsight.sfdc.widgets.AccountWidget.pages;

import java.util.ArrayList;

import com.gainsight.sfdc.customer360.pages.Workflow360Page;
import com.gainsight.sfdc.workflow.pages.WorkflowPage;
import com.gainsight.sfdc.workflow.pojos.CTA;
import com.gainsight.sfdc.workflow.pojos.Task;

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
	
	public void addTaskToCTA(CTA cta, ArrayList<Task> tasks) {
		cta.setFromCustomer360orWidgets(true);
		wfPage.addTaskToCTA(cta, tasks);
	}

	public boolean isTaskDisplayedUnderCTA(CTA cta, Task task) {
		return wfPage.isTaskDisplayedUnderCTA(cta, task);
	}

	public AccountWidget_CockpitTab_Page applyPlayBook(CTA cta, String playBookName,
			ArrayList<Task> tasks, boolean isApply) {
		wfPage.applyPlayBook(cta, playBookName, tasks, isApply);		
		return this;
	}

	public void createMilestoneForCTA(CTA cta) {
		cta.setFromCustomer360orWidgets(true);
		wfPage.createMilestoneForCTA(cta);
	}

	public void closeCTA(CTA cta, boolean hasOpenTasks) {
		cta.setFromCustomer360orWidgets(true);
		wfPage.closeCTA(cta, hasOpenTasks);
	}

	public void snoozeCTA(CTA cta) {
		cta.setFromCustomer360orWidgets(true);
		wfPage.snoozeCTA(cta);
	}

	public boolean isTaskDisplayed(Task task) {
		return wfPage.isTaskDisplayed(task);
	}

	public AccountWidget_CockpitTab_Page flagCTA(CTA cta) {
		cta.setFromCustomer360orWidgets(true);
		wfPage.flagCTA(cta);
		return this;
	}

	public void updateCTAStatus_toClosedLost(CTA cta) {
		cta.setFromCustomer360orWidgets(true);
		wfPage.updateCTAStatus_toClosedLost(cta);
		
	}

	public void openORCloseTask(Task task) {
		wfPage.openORCloseTask(task);
	}

	public boolean verifyTaskDetails(Task task) {
		return wfPage.verifyTaskDetails(task);
	}

	public boolean verifyCTADetails(CTA cta) {
		return wfPage.verifyCTADetails(cta);
	}
	
	public void openCTA(CTA cta,boolean hasTasks,ArrayList<Task> tasks){
		wfPage.openCTA(cta, hasTasks, tasks);
	}

	public void updateCTADetails(CTA cta, CTA updatedCta) {
		cta.setFromCustomer360orWidgets(true);
		wfPage.updateCTADetails(cta,updatedCta);
	}

	public void deleteCTA(CTA cta) {
		cta.setFromCustomer360orWidgets(true);
		wfPage.deleteCTA(cta);
	}

	public void deleteTask(Task task) {
		wfPage.deleteTask(task);
	}

	public void updateTaskDetails(Task ExpectedTask, Task newTask) {
		wfPage.updateTaskDetails(ExpectedTask, newTask);
	}

	public void editTasks(CTA cta, Task updatedTask, Task task) {
		wfPage.editTasks(cta, updatedTask, task);
	}

	public void syncTasksToSF(CTA cta, Task task) {
		task.setFromCustomer360orWidgets(true);
		wfPage.syncTasksToSF(cta, task);
	}

	public void deSyncTaskFromSF(CTA cta, Task task, boolean keepInSF) {
		task.setFromCustomer360orWidgets(true);
		wfPage.deSyncTaskFromSF(cta, task, keepInSF);
	}

	public boolean isOverDueCTADisplayed(CTA cta) {
		return wfPage.isOverDueCTADisplayed(cta);
	}
	

}
