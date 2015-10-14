/**
 * 
 */
package com.gainsight.bigdata.rulesengine.pojo.scheduler;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * @author Abhilash Thaduka
 *
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class ShowScheduler {
	
	private boolean isScheduler = false;
    private String recurringType;
    private String dailyRecurringInterval; //should be either "EveryWeekday" or "N" .. where N is number of days
    private String weeklyRecurringInterval; //should be in the format : "Week_n_Weekday"
    private String monthlyRecurringInterval;  // should be either "Day_n_Month_n" or "Week_n_WEEKDAY_Month_n" --where 'n' is valid number --and Day or week is the option to be set
    private String yearlyRecurringInterval;  //should be in the format : "Month_n" or "Week_n_Month"
    private String preferredStartTimeHours;
    private String preferredStartTimeMinutes;
	private String startDate;
	private String endDate;
	private String timeZone;
	private String emailFailures;
	private String cronExpression;
	

	private boolean runForHistoricalPeriods;
	
	
	public String getPreferredStartTimeHours() {
		return preferredStartTimeHours;
	}

	public void setPreferredStartTimeHours(String preferredStartTimeHours) {
		this.preferredStartTimeHours = preferredStartTimeHours;
	}

	public String getPreferredStartTimeMinutes() {
		return preferredStartTimeMinutes;
	}

	public void setPreferredStartTimeMinutes(String preferredStartTimeMinutes) {
		this.preferredStartTimeMinutes = preferredStartTimeMinutes;
	}

	public boolean isScheduler() {
		return isScheduler;
	}

	public void setScheduler(boolean isScheduler) {
		this.isScheduler = isScheduler;
	}

	public String getRecurringType() {
		return recurringType;
	}

	public void setRecurringType(String recurringType) {
		this.recurringType = recurringType;
	}

	public String getDailyRecurringInterval() {
		return dailyRecurringInterval;
	}

	public void setDailyRecurringInterval(String dailyRecurringInterval) {
		this.dailyRecurringInterval = dailyRecurringInterval;
	}

	public String getWeeklyRecurringInterval() {
		return weeklyRecurringInterval;
	}

	public void setWeeklyRecurringInterval(String weeklyRecurringInterval) {
		this.weeklyRecurringInterval = weeklyRecurringInterval;
	}

	public String getMonthlyRecurringInterval() {
		return monthlyRecurringInterval;
	}

	public void setMonthlyRecurringInterval(String monthlyRecurringInterval) {
		this.monthlyRecurringInterval = monthlyRecurringInterval;
	}

	public String getYearlyRecurringInterval() {
		return yearlyRecurringInterval;
	}

	public void setYearlyRecurringInterval(String yearlyRecurringInterval) {
		this.yearlyRecurringInterval = yearlyRecurringInterval;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getEmailFailures() {
		return emailFailures;
	}

	public void setEmailFailures(String emailFailures) {
		this.emailFailures = emailFailures;
	}

	public boolean isRunForHistoricalPeriods() {
		return runForHistoricalPeriods;
	}

	public void setRunForHistoricalPeriods(boolean runForHistoricalPeriods) {
		this.runForHistoricalPeriods = runForHistoricalPeriods;
	}
	
	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
}
