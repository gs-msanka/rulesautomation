package com.gainsight.bigdata.rulesengine.pages;

import java.util.ArrayList;


import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.testdriver.Log;

/**
 * Created by vmenon on 9/3/2015.
 *
 * TODO: WIP
 */
public class RulesSchedulerPage extends BasePage {

    private final String DAILY = "//input[@value='DAILY']";
    private final String EVERYDAY = "//input[@value='day']";
    private final String WEEKLY = "//input[@value='WEEKLY']";
    private final String WEEKLY_DAY = "//input[contains(@value,'%s')]";
    private final String MONTHLY = "//input[@value='MONTHLY']";
    private final String MONTHLY_ONDAY = "//input[contains(@value,'Monthly-onDay')]/../../following-sibling::div//button";
    private final String MONTHLY_ONDAY_DAYNUMBERPIC = "//select[contains(@class,'monthly-onthe-daynumpick')]/following-sibling::button";
    private final String MONTHLY_ONDAY_DAYPIC = "//select[contains(@class,'monthly-onthe-daypick')]/following-sibling::button";
    private final String YEARLY = "//input[@value='YEARLY']";
    private final String YEARLY_MONTHPIC = "//select[contains(@class,'yearly-onevery-monthpick')]/following-sibling::button";
    private final String YEARLY_DAYNUMBERPIC = "//select[contains(@class,'yearly-onevery-daynumpick')]/following-sibling::button";
    private final String YEARLY_ONTHE_DAYNUMPIC = "//select[contains(@class,'yearly-onthe-daynumpick')]/following-sibling::button";
    private final String YEARLY_ONTHE_DAYPIC = "//select[contains(@class,'yearly-onthe-daypick')]/following-sibling::button";
    private final String YEARLY_ONTHE_MONTHPIC = "//select[contains(@class,'yearly-onthe-monthpick')]/following-sibling::button";
    private final String START_DATE = "//label[contains(text(),'Start Date')]/following-sibling::div/input[contains(@class,'start-datepick')]";
    private final String END_DATE = "//label[contains(text(),'Start Date')]/following-sibling::div/input[contains(@class,'end-datepick')]";
    private final String PSTHRS = "//select[contains(@class,'preferedStartTime-mins')]/preceding-sibling::button";
    private final String PSTMINS = "//select[contains(@class,'preferedStartTime-mins')]/following-sibling::button";
    private final String TIME_ZONE = "//label[contains(text(),'Time Zone')]/following-sibling::div";
    private final String EMAIL_FEATURE = "//label[contains(text(),'Email failures during')]/following-sibling::div";
    private final String HISTORYRUN = "//div[contains(text(),'Run for historical periods')]/preceding-sibling::input";
    private final String SCHEDULESTART = "//a[contains(text(),'Start')]";

    public RulesSchedulerPage() {
        throw new RuntimeException(this.getClass().getName() +" is not yet implemented.");
    }

    public void dailySchedule(String ruleName) {

        // Select daily
        item.click(DAILY);
        Log.info("Daily schedule check box selected");

        // every day or only week days
        if ("".contains("")) {
            item.click(EVERYDAY);

        }

        // Start date
        element.setText(START_DATE, "");

        // End date
        element.setText(END_DATE, "");

        // Preferred start date hrs
        item.click(PSTHRS);
        selectValueInDropDown("");

        // Preferred start date mins
        item.click(PSTMINS);
        selectValueInDropDown("");

        // selecting time zone
        item.click(TIME_ZONE);
        selectValueInDropDown("");

        // Email feature during schedules
        element.setText(EMAIL_FEATURE, "");

        // If asked un-checking historical run
        if (true) {
            item.click(HISTORYRUN);
        }

        // Start the schedule
        item.click(SCHEDULESTART);

    }

    public void weeklySchedule(String ruleName) {

        // Select daily
        item.click(WEEKLY);
        Log.info("Weekly schedule check box selected");

        // Selecting the day in a week
        String weeklyDay = String.format(WEEKLY_DAY, "");
        item.click(weeklyDay);

        // Start date
        element.setText(START_DATE, "");

        // End date
        element.setText(END_DATE, "");

        // Preferred start date hrs
        item.click(PSTHRS);
        selectValueInDropDown("");

        // Preferred start date mins
        item.click(PSTMINS);
        selectValueInDropDown("");

        // selecting time zone
        item.click(TIME_ZONE);
        selectValueInDropDown("");

        // Email feature during schedules
        element.setText(EMAIL_FEATURE, "");

        // If asked un-checking historical run
        if (true) {
            item.click(HISTORYRUN);
        }

        // Start the schedule
        item.click(SCHEDULESTART);

    }

    public void monthlySchedule(String ruleName) {

        // Select daily
        item.click(MONTHLY);
        Log.info("Monthly schedule check box selected");

        // Selecting on day of every month
        if ("".contains("")) {
            item.click(MONTHLY_ONDAY);
            selectValueInDropDown("");

        } else {
            // selecting the particular day in terms of week
            item.click(MONTHLY_ONDAY_DAYNUMBERPIC);
            selectValueInDropDown("");

            item.click(MONTHLY_ONDAY_DAYPIC);
            selectValueInDropDown("");
        }

        // Start date
        element.setText(START_DATE, "");

        // End date
        element.setText(END_DATE, "");

        // Preferred start date hrs
        item.click(PSTHRS);
        selectValueInDropDown("");

        // Preferred start date mins
        item.click(PSTMINS);
        selectValueInDropDown("");

        // selecting time zone
        item.click(TIME_ZONE);
        selectValueInDropDown("");

        // Email feature during schedules
        element.setText(EMAIL_FEATURE, "");

        // If asked un-checking historical run
        if (true) {
            item.click(HISTORYRUN);
        }

        // Start the schedule
        item.click(SCHEDULESTART);

    }

    public void yearlySchedule(String ruleName) {

        // Select daily
        item.click(YEARLY);
        Log.info("Yearly schedule check box selected");

        // Selecting on every day and month
        if ("".contains("")) {
            item.click(YEARLY_MONTHPIC);
            selectValueInDropDown("");

            item.click(YEARLY_DAYNUMBERPIC);
            selectValueInDropDown("");

        } else {
            // selecting on the day number, day and month
            item.click(YEARLY_ONTHE_DAYNUMPIC);
            selectValueInDropDown("");

            item.click(YEARLY_ONTHE_DAYPIC);
            selectValueInDropDown("");

            item.click(YEARLY_ONTHE_MONTHPIC);
            selectValueInDropDown("");
        }

        // Start date
        element.setText(START_DATE, "");

        // End date
        element.setText(END_DATE, "");

        // Preferred start date hrs
        item.click(PSTHRS);
        selectValueInDropDown("");

        // Preferred start date mins
        item.click(PSTMINS);
        selectValueInDropDown("");

        // selecting time zone
        item.click(TIME_ZONE);
        selectValueInDropDown("");

        // Email feature during schedules
        element.setText(EMAIL_FEATURE, "");

        // If asked un-checking historical run
        if (true) {
            item.click(HISTORYRUN);
        }

        // Start the schedule
        item.click(SCHEDULESTART);

    }

}