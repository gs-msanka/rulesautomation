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
		cta.setFromCustomer360orWidgets(true);
		wfPage.createCTA(cta);
	}

	public boolean isCTADisplayed(CTA cta) {
		cta.setFromCustomer360orWidgets(true);
		return wfPage.isCTADisplayed(cta);
	}

	public void addTaskToCTA(CTA cta, ArrayList<Task> tasks) {
		cta.setFromCustomer360orWidgets(true);
		wfPage.addTaskToCTA(cta, tasks);
	}

	public boolean isTaskDisplayedUnderCTA(CTA cta, Task task) {
		cta.setFromCustomer360orWidgets(true);
		task.setFromCustomer360orWidgets(true);
		return wfPage.isTaskDisplayedUnderCTA(cta, task);
	}

	public Workflow360Page applyPlayBook(CTA cta, String playBookName,
			ArrayList<Task> tasks, boolean isApply) {
		cta.setFromCustomer360orWidgets(true);
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
		task.setFromCustomer360orWidgets(true);
		return wfPage.isTaskDisplayed(task);
	}

	public Workflow360Page flagCTA(CTA cta) {
		cta.setFromCustomer360orWidgets(true);
		wfPage.flagCTA(cta);
		return this;
	}

	public void updateCTAStatus_toClosedLost(CTA cta) {
		cta.setFromCustomer360orWidgets(true);
		wfPage.updateCTAStatus_toClosedLost(cta);
		
	}

	public void openORCloseTask(Task task) {
		task.setFromCustomer360orWidgets(true);
		wfPage.openORCloseTask(task);
	}

	public boolean verifyTaskDetails(Task task) {
		task.setFromCustomer360orWidgets(true);
		return wfPage.verifyTaskDetails(task);
	}

	public boolean verifyCTADetails(CTA cta) {
		cta.setFromCustomer360orWidgets(true);
		return wfPage.verifyCTADetails(cta);
	}
	
	public void openCTA(CTA cta,boolean hasTasks,ArrayList<Task> tasks){
		cta.setFromCustomer360orWidgets(true);
		wfPage.openCTA(cta, hasTasks, tasks);
	}

	public void updateCTADetails(CTA cta, CTA updatedCta) {
		cta.setFromCustomer360orWidgets(true);
		updatedCta.setFromCustomer360orWidgets(true);
		wfPage.updateCTADetails(cta,updatedCta);
	}

	public void deleteCTA(CTA cta) {
		cta.setFromCustomer360orWidgets(true);
		wfPage.deleteCTA(cta);
	}

	public void deleteTask(Task task) {
		task.setFromCustomer360orWidgets(true);
		wfPage.deleteTask(task);
	}

	public void updateTaskDetails(Task ExpectedTask, Task newTask) {
		ExpectedTask.setFromCustomer360orWidgets(true);
		newTask.setFromCustomer360orWidgets(true);
		wfPage.updateTaskDetails(ExpectedTask, newTask);
	}

	public void editTasks(CTA cta, Task updatedTask, Task task) {
		task.setFromCustomer360orWidgets(true);
		updatedTask.setFromCustomer360orWidgets(true);
		cta.setFromCustomer360orWidgets(true);
		wfPage.editTasks(cta, updatedTask, task);
	}

	public void syncTasksToSF(CTA cta, Task task) {
		cta.setFromCustomer360orWidgets(true);
		task.setFromCustomer360orWidgets(true);
		wfPage.syncTasksToSF(cta, task);
	}

	public void deSyncTaskFromSF(CTA cta, Task task, boolean keepInSF) {
		cta.setFromCustomer360orWidgets(true);
		task.setFromCustomer360orWidgets(true);
		wfPage.deSyncTaskFromSF(cta, task, keepInSF);
	}

	public boolean isOverDueCTADisplayed(CTA cta) {
		cta.setFromCustomer360orWidgets(true);
		return wfPage.isOverDueCTADisplayed(cta);
	}
	
}
