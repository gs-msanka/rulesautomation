package com.gainsight.sfdc.workflow.pojos;

import java.util.List;

/**
 * Created by gainsight on 07/11/14.
 */
public class CTA {
	private String type;
	private String subject;
	private String customer;
	private String status;
	private String reason;
	private String dueDate;
	private String comments;
	private boolean isImp;
	private String priority;
	private int taskCount;
	private String assignee;
	private List<Att> attributes;
	private boolean isRecurring;
	private String recurringType;
	private String dailyRecurringInterval; //should be either "EveryWeekday" or "N" .. where N is number of days
	private String weeklyRecurringInterval; //should be in the format : "Week_n_Weekday" 
    private String monthlyRecurInterval;  // should be either "Day_n_Month_n" or "Week_n_Month_n" --where 'n' is valid number --and Day or week is the option to be set
    private String yearlyRecurringInterval;  //should be in the format : "Month_n" or "Week_n_Month"
    private String recurStartDate;
    private String recurEndDate;
    
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setRecurringType(String recurringType) {
		this.recurringType = recurringType;
	}

	public String getRecurringType() {
		return recurringType;
	}
	public void setDailyRecurringInterval(String dailyRecurringInterval){
		this.dailyRecurringInterval=dailyRecurringInterval;
	}
	
	public String  getDailyRecurringInterval(){
		return dailyRecurringInterval;
	}
	
	public void setWeeklyRecurringInterval(String weeklyRecurringInterval) {
		this.weeklyRecurringInterval = weeklyRecurringInterval;
	}

	public String getWeeklyRecurringInterval() {
		return weeklyRecurringInterval;
	}
	
	public void  setMonthlyRecurringInterval(String monthlyRecurInterval){
		this.monthlyRecurInterval=monthlyRecurInterval;
	}
	
	public String getMonthlyRecurringInterval(){
		return monthlyRecurInterval;
	}
	
	public void setYearlyRecurringInterval(String yearlyRecurringInterval){
		this.yearlyRecurringInterval=yearlyRecurringInterval;
	}
	
	public String getYearlyRecurringInterval(){
		return yearlyRecurringInterval;
	}
	
	public void setRecurStartDate(String recurStartDate){
		this.recurStartDate=recurStartDate;
	}
	
	public String getRecurStartDate(){
		return recurStartDate;
	}
	
	public void setRecurEndDate(String recurEndDate){
		this.recurEndDate =recurEndDate;
	}
	
	public String getRecurEndDate(){
		return recurEndDate;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public void setIsRecurring(boolean isRecurring) {
		this.isRecurring = isRecurring;
	}

	public boolean getIsRecurring() {
		return isRecurring;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public boolean isImp() {
		return isImp;
	}

	public void setImp(boolean isImp) {
		this.isImp = isImp;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public int getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public List<Att> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Att> attributes) {
		this.attributes = attributes;
	}

	public class Att {
		private String attLabel;
		private String attValue;
		private boolean inSummary;

		public boolean isInSummary() {
			return inSummary;
		}

		public void setInSummary(boolean inSummary) {
			this.inSummary = inSummary;
		}

		public String getAttLabel() {
			return attLabel;
		}

		public void setAttLabel(String attLabel) {
			this.attLabel = attLabel;
		}

		public String getAttValue() {
			return attValue;
		}

		public void setAttValue(String attValue) {
			this.attValue = attValue;
		}
	}

}
