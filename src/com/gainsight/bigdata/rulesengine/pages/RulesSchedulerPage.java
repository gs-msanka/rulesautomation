package com.gainsight.bigdata.rulesengine.pages;


import com.gainsight.bigdata.rulesengine.pojo.scheduler.ShowScheduler;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.testdriver.Log;

/**
 * Created by vmenon on 9/3/2015.
 *
 */
public class RulesSchedulerPage extends BasePage {

    private final String DAILY = "//input[@value='DAILY']";
    private final String DAILY_WEEKDAY_RADIOBUTTON = "//input[@id='dr-weekday']";
    private final String DAILY_EVERYDAY_RADIOBUTTON = "//input[@id='dr-day']";
    private final String WEEKLY = "//input[@value='WEEKLY']";
    private final String WEEKLY_DAY = "//div[@class='schedulereventoptions-WEEKLY']/descendant::label[contains(., '%s')]";
    private final String MONTHLY = "//input[@value='MONTHLY']";
    private final String MONTHLY_RADIO_BUTTON = "//div[@class='schedulereventoptions-MONTHLY']/descendant::label[contains(., '%s')]";
    private final String MONTHLY_ONDAY = "//select[contains(@class, 'monthly-onDay-daynumpick')]/following-sibling::button";
    private final String MONTHLY_ONDAY_DAYNUMBERPIC = "//select[contains(@class,'monthly-onthe-daynumpick')]/following-sibling::button";
    private final String MONTHLY_ONDAY_DAYPIC = "//select[contains(@class,'monthly-onthe-daypick')]/following-sibling::button";
    private final String YEARLY = "//input[@value='YEARLY']";
    private final String YEARLY_RADIO_BUTTON = "//div[@class='schedulereventoptions-YEARLY']/descendant::label[contains(., '%s')]";
    private final String YEARLY_ONTHE_DAYNUMPIC = "//select[contains(@class,'yearly-onthe-daynumpick')]/following-sibling::button";
    private final String YEARLY_ONTHE_DAYPIC = "//select[contains(@class,'yearly-onthe-daypick')]/following-sibling::button";
    private final String YEARLY_ONTHE_MONTHPIC = "//select[contains(@class,'yearly-onthe-monthpick')]/following-sibling::button";  
    private final String START_DATE         = "//input[@class='form-control date-input scheduler-event-start-datepick']";
    private final String END_DATE           = "//input[@class='form-control date-input scheduler-event-end-datepick']";
    private final String START_TIME_HOURS = "//select[contains(@class,'preferedStartTime-mins')]/preceding-sibling::button";
    private final String START_TIME_MINUTES = "//select[contains(@class,'preferedStartTime-mins')]/following-sibling::button";
    private final String TIME_ZONE = "//select[contains(@class, 'timezone-list')]//following-sibling::button";
    private final String EMAIL_FEATURE = "//textarea[contains(@class, 'emailList form-control')]";
    private final String HISTORYRUN = "//div[contains(text(),'Run for historical periods')]/preceding-sibling::input";
    private final String SCHEDULER_HYPERLINK = "//li[@data-view='ScheduleView']/a";
    private final String SCHEDULER_DIV_CONTAINER = "//div[@class='RuleContainer']/descendant::div[contains(@class, 'scheduler-events')]";
    private final String START_SCHEDULER_BUTTON = "//a[contains(@class, 'btn-save') and text()='Start']";
    private final String YEARY_ON_EVERY_MONTHLYPICK = "//select[contains(@class, 'yearly-onevery-monthpick')]/following-sibling::button";
    private final String YEARY_ON_EVERY_DAY_NUM_PICK = "//select[contains(@class, 'yearly-onevery-daynumpick')]/following-sibling::button";
    
    
    /**
     * Clicks on the Scheduler Link
     * @return RulesSchedulerPage object after clicking on scheduler link
     */
    public RulesSchedulerPage clickOnSchedulerLink(){
    	item.click(SCHEDULER_HYPERLINK);
    	wait.waitTillElementNotDisplayed("//div[contains(@class, 'ui-draggable') and contains(@style,'display: block;')]", MIN_TIME, MAX_TIME);
    	return this; 	
    }
    
    /**
     * Clicks on Start Scheduler button in Scheduler UI
     */
    public void clickOnStartSchedulerButton(){
    	item.click(START_SCHEDULER_BUTTON);
    }
    
    /**
     * Fills daily Scheduler info in UI
     * @param scheduler object
     */
    public void dailySchedule(ShowScheduler scheduler) {
		Log.info("Filling scheduler information for scheduler type - Daily");
		item.click(DAILY);
		if (scheduler.getDailyRecurringInterval().equals("EveryWeekday")) {
			item.click(DAILY_WEEKDAY_RADIOBUTTON);
		} else {
			item.click(DAILY_EVERYDAY_RADIOBUTTON);
		}
		commonSchedularActions(scheduler);
		clickOnStartSchedulerButton();
    }

    /**
     * Fills weekly Scheduler info in UI
     * @param scheduler object
     */
    public void weeklySchedule(ShowScheduler scheduler) {
		Log.info("Filling scheduler information for scheduler type - Weekly");
		item.click(WEEKLY);
		Log.info("Weekly schedule check box selected");
		field.selectCheckBox(String.format(WEEKLY_DAY,scheduler.getWeeklyRecurringInterval()));
		commonSchedularActions(scheduler);
		clickOnStartSchedulerButton();
	}

    
    /**
     * Fills monthly Scheduler info in UI
     * @param scheduler object
     */
    public void monthlySchedule(ShowScheduler scheduler) {
		Log.info("Filling scheduler information for scheduler type - Monthly");
		item.click(MONTHLY);
		if (scheduler.getMonthlyRecurringInterval().startsWith("Day")) {
			item.click(String.format(MONTHLY_RADIO_BUTTON, scheduler.getMonthlyRecurringInterval().split("_")[0]));
			item.click(MONTHLY_ONDAY);
			selectValueInDropDown(scheduler.getMonthlyRecurringInterval().split("_")[1]);
		} else {
			item.click(String.format(MONTHLY_RADIO_BUTTON, scheduler.getMonthlyRecurringInterval().split("_")[0]));
			item.click(MONTHLY_ONDAY_DAYNUMBERPIC);
			selectValueInDropDown(scheduler.getMonthlyRecurringInterval().split("_")[1]);
			item.click(MONTHLY_ONDAY_DAYPIC);
			selectValueInDropDown(scheduler.getMonthlyRecurringInterval().split("_")[2]);
		}
		commonSchedularActions(scheduler);
		clickOnStartSchedulerButton();
	}

    /**
     * Fills yearly Scheduler info in UI
     * @param scheduler object
     */
    public void yearlySchedule(ShowScheduler scheduler) {
		Log.info("Filling scheduler information for scheduler type - Yearly");
		item.click(YEARLY);
		if (scheduler.getYearlyRecurringInterval().startsWith("On every")) {
			item.click(String.format(YEARLY_RADIO_BUTTON, scheduler.getYearlyRecurringInterval().split("_")[0]));
			item.click(YEARY_ON_EVERY_MONTHLYPICK);
			selectValueInDropDown(scheduler.getYearlyRecurringInterval().split("_")[1]);
			item.click(YEARY_ON_EVERY_DAY_NUM_PICK);
			selectValueInDropDown(scheduler.getYearlyRecurringInterval().split("_")[2]);
		} else {
			item.click(String.format(YEARLY_RADIO_BUTTON, scheduler.getYearlyRecurringInterval().split("_")[0]));
			item.click(YEARLY_ONTHE_DAYNUMPIC);
			selectValueInDropDown(scheduler.getYearlyRecurringInterval().split("_")[1]);
			item.click(YEARLY_ONTHE_DAYPIC);
			selectValueInDropDown(scheduler.getYearlyRecurringInterval().split("_")[2]);
			item.click(YEARLY_ONTHE_MONTHPIC);
			selectValueInDropDown(scheduler.getYearlyRecurringInterval().split("_")[3]);
		}   
        commonSchedularActions(scheduler);
        clickOnStartSchedulerButton();
    }
    
    
    /**
     * Fills all the common action relates to all types of schedulers
     * (Daily/Weekly/Monthly/Yearly)
     * @param scheduler object
     */
    public void commonSchedularActions(ShowScheduler scheduler){
    	element.setText(START_DATE, scheduler.getStartDate());
		element.setText(END_DATE, scheduler.getEndDate());
		item.click(START_TIME_HOURS);
		selectValueInDropDown(scheduler.getPreferredStartTimeHours());
		item.click(START_TIME_MINUTES);
		selectValueInDropDown(scheduler.getPreferredStartTimeMinutes());
		item.click(TIME_ZONE);
		selectValueInDropDown(scheduler.getTimeZone());
		element.clearAndSetText(EMAIL_FEATURE, scheduler.getEmailFailures());
		if (scheduler.isRunForHistoricalPeriods()) {
			item.click(HISTORYRUN);
		}	
    }
    
    /**
     * Fills Scheduler info in UI
     * @param scheduler object
     */
    public void fillSchedulerInformation(ShowScheduler scheduler){
		clickOnSchedulerLink();
		wait.waitTillElementDisplayed(SCHEDULER_DIV_CONTAINER, MIN_TIME, MAX_TIME);
		Log.info("Started Filling Scheduler Info....");
		if (scheduler.getRecurringType().equalsIgnoreCase(("Daily"))) {
			dailySchedule(scheduler);
		} else if ((scheduler.getRecurringType().equalsIgnoreCase(("Weekly")))) {
			weeklySchedule(scheduler);
		} else if ((scheduler.getRecurringType().equalsIgnoreCase(("Monthly")))) {
			monthlySchedule(scheduler);
		} else {
			yearlySchedule(scheduler);
		}
		clickOnStartSchedulerButton();
	}
}