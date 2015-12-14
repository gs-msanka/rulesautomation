package com.gainsight.bigdata.rulesengine.pojo.setupaction;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by vmenon on 9/13/2015.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class SetScoreAction {
    private String selectMeasure = "";
    private String setScoreFrom = "";
    private String comments = "";
    @JsonProperty("isStaticScore")
    private boolean staticScore;
    private String staticScoreValue;
    private String scoringSchemeType;
    @JsonProperty("isScaleFieldValue")
    private boolean scaleFieldValue;
    private int sourceRangeFrom;
    private int sourceRangeTo;

	public int getSourceRangeFrom() {
		return sourceRangeFrom;
	}

	public void setSourceRangeFrom(int sourceRangeFrom) {
		this.sourceRangeFrom = sourceRangeFrom;
	}

	public int getSourceRangeTo() {
		return sourceRangeTo;
	}

	public void setSourceRangeTo(int sourceRangeTo) {
		this.sourceRangeTo = sourceRangeTo;
	}

	public boolean isScaleFieldValue() {
		return scaleFieldValue;
	}

	public void setScaleFieldValue(boolean scaleFieldValue) {
		this.scaleFieldValue = scaleFieldValue;
	}

	public String getScoringSchemeType() {
		return scoringSchemeType;
	}

	public void setScoringSchemeType(String scoringSchemeType) {
		this.scoringSchemeType = scoringSchemeType;
	}

	public boolean isStaticScore() {
		return staticScore;
	}

	public void setStaticScore(boolean staticScore) {
		this.staticScore = staticScore;
	}

	public String getStaticScoreValue() {
		return staticScoreValue;
	}

	public void setStaticScoreValue(String staticScoreValue) {
		this.staticScoreValue = staticScoreValue;
	}

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
