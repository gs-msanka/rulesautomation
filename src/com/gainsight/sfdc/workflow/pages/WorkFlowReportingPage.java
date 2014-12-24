package com.gainsight.sfdc.workflow.pages;

import com.gainsight.sfdc.pages.BasePage;

/**
 * Created by gainsight on 21/11/14.
 */
public class WorkFlowReportingPage extends BasePage {

    private final String LOADING_ICON       = "//div[contains(@class, 'gs-loader-image')]";
    //Sub tab elements.
    private final String OVERVIEW_SUB_TAB   = "//a[@data-tab='OVERVIEW']";
    private final String LEADER_BOARD_TAB   = "//a[@data-tab='LEADERBOARD']";
    private final String PERIOD_SELECT      = "//[@class='date-dropdown-anchor']";
    
    //Drop-down filter menu options
    private final String DROP_DOWN_MENU     = "//div[contains(@class,'gs-dropdown gs-dropdown-date-action pull-left')]";
    private final String LAST_7DAYS         = "//a[@data-value='JBlast7']";
    private final String LAST_30DAYS        = "//a[@data-value='JBlast30']";
    private final String CURRENT_MONTH      = "//a[@data-value='JBthismonth']";
    private final String LAST_MONTH         = "//a[@data-value='JBlastmonth']";
    private final String CURRENT_QUARTER    = "//a[@data-value='JBcurrentq']";
    private final String LAST_QUARTER       = "//a[@data-value='JBprevq']";
    private final String FROM_DATE_INPUT    = "r-from-date";
    private final String TO_DATE_INPUT      = "r-to-date";
    private final String APPLY_BUTTON       = "//input[@type='button' and @value='Apply']";
    private final String CANCEL_BUTTON      = "//input[@type='button' and @value='Cancel']";

    //Leader board elements
    private final String LEADER_TABLE                   = "//table[@class='table dashboard-leaderboard-table']";
    private final String USER                           = LEADER_TABLE+"/descendant::div[@class='cta-userName']/span[text()='%s']";
    private final String LEADER_TABLE_OPEN_COLUMN       = LEADER_TABLE+"/tbody/tr/td[2]";
    private final String LEADER_TABLE_CLOSED_COLUMN     = LEADER_TABLE+"/tbody/tr/td[3]";
    private final String LEADER_TABLE_CUSTOMER_COLUMN   = LEADER_TABLE+"/tbody/tr/td[4]";
    private final String LEADER_TABLE_ASV_COLUMN        = LEADER_TABLE+"/tbody/tr/td[5]";
    private final String LEADER_TABLE_TASK_COLUMN       = LEADER_TABLE+"/tbody/tr/td[6]";
    private final String NO_DATA_MSG		="//div[@class='leaderboard-table-nodata']/div[@class='noDataFound' and contains(text(),'No data found')]";
    
    
    public WorkFlowReportingPage() {
        wait.waitTillElementDisplayed(LEADER_BOARD_TAB, MIN_TIME, MAX_TIME);
    }

    public WorkFlowReportingPage clickOnLeaderBoard() {
        item.click(LEADER_BOARD_TAB);
        waitTillNoLoadingIcon();
        return this;
    }

    public WorkFlowReportingPage selectLast7Days() {
        item.click(DROP_DOWN_MENU);
        item.click(LAST_7DAYS);
        waitTillNoLoadingIcon();
        return this;
    }

    public WorkFlowReportingPage selectLast30Days() {
        item.click(DROP_DOWN_MENU);
        item.click(LAST_30DAYS);
        waitTillNoLoadingIcon();
        return this;
    }

    public WorkFlowReportingPage selectCurrentMonth() {
        item.click(DROP_DOWN_MENU);
        item.click(CURRENT_MONTH);
        waitTillNoLoadingIcon();
        return this;
    }

    public WorkFlowReportingPage selectLastMonth() {
        item.click(DROP_DOWN_MENU);
        item.click(LAST_MONTH);
        waitTillNoLoadingIcon();
        return this;
    }

    public WorkFlowReportingPage selectCurrentQuarter() {
        item.click(DROP_DOWN_MENU);
        item.click(CURRENT_QUARTER);
        waitTillNoLoadingIcon();
        return this;
    }

    public WorkFlowReportingPage selectLastQuarter() {
        item.click(DROP_DOWN_MENU);
        item.click(LAST_QUARTER);
        waitTillNoLoadingIcon();
        return this;
    }

    public WorkFlowReportingPage selectCustomDate(String fromDate, String endDate) {
        item.click(DROP_DOWN_MENU);
        element.clearAndSetText(FROM_DATE_INPUT, fromDate);
        element.clearAndSetText(TO_DATE_INPUT, endDate);
        item.click("//div[@class='dropdown-date']/descendant::label[contains(text(),'To')]");//just clicking somewhere else because the dropdown does not collapse..because of datepicker
        item.click(APPLY_BUTTON);
        waitTillNoLoadingIcon();
        return this;
    }

    

    public boolean isUserDisplayedInLeaderBoard(String assignee) {
        Log.info("Checking is user is dispalyed in leader board.");
        return element.isElementPresent(String.format(USER, assignee));
    }
    public boolean checkforNoDataMessage(){
    	return element.isElementPresent(NO_DATA_MSG);
    }
    public int getCountOfUserOpenCTAs(String assignee, String type) {
        try {
            if(type.equalsIgnoreCase("Risk")) {
                String tmp = field.getText(LEADER_TABLE_OPEN_COLUMN+"/div[@class='bGraph-leaderboard' and @data-username='"+assignee+"']/div[contains(@title, 'Risk')]");
                return Integer.valueOf(tmp.trim());
            } else if (type.equalsIgnoreCase("Event")) {
                String tmp = field.getText(LEADER_TABLE_OPEN_COLUMN+"/div[@class='bGraph-leaderboard' and @data-username='"+assignee+"']/div[contains(@title, 'Event')]");
                return Integer.valueOf(tmp.trim());
            } else if (type.equalsIgnoreCase("Opportunity")){
                String tmp = field.getText(LEADER_TABLE_OPEN_COLUMN+"/div[@class='bGraph-leaderboard' and @data-username='"+assignee+"']/div[contains(@title, 'Opportunity')]");
                return Integer.valueOf(tmp.trim());
            }
        } catch (Exception e)  {
            e.printStackTrace();
            Log.info("Failed to get CTA count, " +e.getLocalizedMessage());
        }
        return 0;
    }

    public int getCountOfUserClosedCTAs(String assignee, String type) {
        try {
            if(type.equalsIgnoreCase("Risk")) {
                String tmp = field.getText(LEADER_TABLE_CLOSED_COLUMN+"/div[@class='bGraph-leaderboard' and @data-username='"+assignee+"']/div[contains(@title, 'Risk')]");
                return Integer.valueOf(tmp.trim());
            } else if (type.equalsIgnoreCase("Event")) {
                String tmp = field.getText(LEADER_TABLE_CLOSED_COLUMN+"/div[@class='bGraph-leaderboard' and @data-username='"+assignee+"']/div[contains(@title, 'Event')]");
                return Integer.valueOf(tmp.trim());
            } else if (type.equalsIgnoreCase("Opportunity")){
                String tmp = field.getText(LEADER_TABLE_CLOSED_COLUMN+"/div[@class='bGraph-leaderboard' and @data-username='"+assignee+"']/div[contains(@title, 'Opportunity')]");
                return Integer.valueOf(tmp.trim());
            }
        } catch (Exception e)  {
            e.printStackTrace();
            Log.info("Failed to get CTA count, " +e.getLocalizedMessage());
        }
        return 0;
    }

    public int getCountOfUserCustomerCTAs(String assignee, String type) {
        try {
            if(type.equalsIgnoreCase("Risk")) {
                String tmp = field.getText(LEADER_TABLE_CUSTOMER_COLUMN+"/div[@class='bGraph-leaderboard' and @data-username='"+assignee+"']/div[contains(@title, 'Risk')]");
                return Integer.valueOf(tmp.trim());
            } else if (type.equalsIgnoreCase("Event")) {
                String tmp = field.getText(LEADER_TABLE_CUSTOMER_COLUMN+"/div[@class='bGraph-leaderboard' and @data-username='"+assignee+"']/div[contains(@title, 'Event')]");
                return Integer.valueOf(tmp.trim());
            } else if (type.equalsIgnoreCase("Opportunity")){
                String tmp = field.getText(LEADER_TABLE_CUSTOMER_COLUMN+"/div[@class='bGraph-leaderboard' and @data-username='"+assignee+"']/div[contains(@title, 'Opportunity')]");
                return Integer.valueOf(tmp.trim());
            }
        } catch (Exception e)  {
            e.printStackTrace();
            Log.info("Failed to get CTA count, " +e.getLocalizedMessage());
        }
        return 0;
    }

    public int getUserASVAtRisk(String assignee) {
        int asv = 0;
        try {
            String temp = field.getText(LEADER_TABLE_ASV_COLUMN+"/div[@class='bGraph-leaderboard' and @data-username='"+assignee+"']");
            asv = Integer.valueOf(temp.trim().substring(1, temp.length()).replaceAll(",", ""));
        } catch (Exception e)  {
            e.printStackTrace();
            Log.info("Failed to get Task count, " +e.getLocalizedMessage());
        }
        return asv;
    }

    public int getCountOfUserTasks(String assignee, boolean isOpenTask, boolean isAll) {
        env.setTimeout(1);
    	int count = 0;
        try {
            String temp = field.getText(LEADER_TABLE_TASK_COLUMN+"/div[@class='cta-tc' and @data-username='"+assignee+"']");
            if(temp.split("\\(").length>1) {
                if(isAll) {
                    count = Integer.valueOf(temp.split("\\(")[0].trim());
                } else {
                    if(isOpenTask)
                        count = Integer.valueOf(temp.split("\\(")[1].split("/")[0].trim());
                    else
                        count = Integer.valueOf(temp.split("\\(")[1].split("/")[1].trim().substring(0, 1));
                }
            } else {
                count = Integer.valueOf(temp.trim());
            }

        } catch (Exception e)  {
            e.printStackTrace();
            Log.info("Failed to get Task count, " +e.getLocalizedMessage());
        }
        env.setTimeout(30);
        return count;
    }







}
