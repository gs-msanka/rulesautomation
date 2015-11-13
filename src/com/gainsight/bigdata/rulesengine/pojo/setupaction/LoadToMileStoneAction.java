package com.gainsight.bigdata.rulesengine.pojo.setupaction;

/**
 * Created by vmenon on 9/13/2015.
 */
public class LoadToMileStoneAction {

	
    private String selectMilestone = "";
    private String comments = "";
    private MilestoneDate milestoneDate;

    public String getSelectMilestone() {
        return selectMilestone;
    }

    public void setSelectMilestone(String selectMilestone) {
        this.selectMilestone = selectMilestone;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public MilestoneDate getMilestoneDate() {
        return milestoneDate;
    }

    public void setMilestoneDate(MilestoneDate milestoneDate) {
        this.milestoneDate = milestoneDate;
    }

    public static class MilestoneDate {
        private String type = "";
        private String dateField = "";
        private String dateFieldValue = "";

        public String getDateFieldValue() {
			return dateFieldValue;
		}

		public void setDateFieldValue(String dateFieldValue) {
			this.dateFieldValue = dateFieldValue;
		}

		public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDateField() {
            return dateField;
        }

        public void setDateField(String dateField) {
            this.dateField = dateField;
        }
    }
}