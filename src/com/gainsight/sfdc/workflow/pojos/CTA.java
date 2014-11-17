package com.gainsight.sfdc.workflow.pojos;

import com.google.inject.internal.asm.$Attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gainsight on 07/11/14.
 */
public class CTA {
	private String type;
	private String subject;
	private String customer;
	private String status = "Open";
	private String reason = "Other - Uncategorized" ;
    private String dueDate = "5";
	private String comments;
    private boolean isImp = false;
    private boolean isClosed = false;
    private int taskCount = 0;
    private String priority = "Medium";
	private String assignee;
    private List<Attribute> attributes = new ArrayList<Attribute>();
    private boolean isRecurring = false;
    private EventRecurring eventRecurring;
    private String snoozeDate="1";
    private String snoozeReason = "Other";
    private boolean isOverDue= false;
    
    public boolean isCTAOverDue() {
        return isOverDue;
    }

    public void setOverDue(boolean isOverDue) {
        this.isOverDue=isOverDue;
    }
    public String getSnoozeReason() {
		return snoozeReason;
	}

	public void setSnoozeReason(String snoozeReason) {
		this.snoozeReason = snoozeReason;
	}

	public String getSnoozeDate() {
		return snoozeDate;
	}

	public void setSnoozeDate(String snoozeDate) {
		this.snoozeDate = snoozeDate;
	}

	public EventRecurring getEventRecurring() {
        return eventRecurring;
    }

    public void setEventRecurring(EventRecurring eventRecurring) {
        this.eventRecurring = eventRecurring;
    }

    public String getDueDate() {
    	return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
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

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttribute(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public static class Attribute {
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

    public static class EventRecurring {
        private String recurringType;
        private String dailyRecurringInterval; //should be either "EveryWeekday" or "N" .. where N is number of days
        private String weeklyRecurringInterval; //should be in the format : "Week_n_Weekday"
        private String monthlyRecurringInterval;  // should be either "Day_n_Month_n" or "Week_n_WEEKDAY_Month_n" --where 'n' is valid number --and Day or week is the option to be set
        private String yearlyRecurringInterval;  //should be in the format : "Month_n" or "Week_n_Month"
        private String recurStartDate;
        private String recurEndDate;


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

        public void  setMonthlyRecurringInterval(String monthlyRecurringInterval){
            this.monthlyRecurringInterval=monthlyRecurringInterval;
        }

        public String getMonthlyRecurringInterval(){
            return monthlyRecurringInterval;
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

        public void setRecurringType(String recurringType) {
            this.recurringType = recurringType;
        }

        public String getRecurringType() {
            return recurringType;
        }

    }

}
