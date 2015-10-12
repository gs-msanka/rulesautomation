package com.gainsight.bigdata.rulesengine.pojo.setupaction;

/**
 * Created by vmenon on 9/13/2015.
 */
public class SetScoreAction {
    private String selectMeasure = "";
    private String setScoreFrom = "";
    private String comments = "";

    public String getSelectMeasure() {
        return selectMeasure;
    }

    public void setSelectMeasure(String selectMeasure) {
        this.selectMeasure = selectMeasure;
    }

    public String getSetScoreFrom() {
        return setScoreFrom;
    }

    public void setSetScoreFrom(String setScoreFrom) {
        this.setScoreFrom = setScoreFrom;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
