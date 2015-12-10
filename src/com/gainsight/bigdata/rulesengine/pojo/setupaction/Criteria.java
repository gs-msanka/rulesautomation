package com.gainsight.bigdata.rulesengine.pojo.setupaction;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by vmenon on 9/13/2015.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Criteria {

    private String showField = "";
    private String operator = "";
    private String field = "";
    private String value = "";
    @JsonProperty("isNullCheck")
    private boolean nullCheck;


	public boolean isNullCheck() {
		return nullCheck;
	}

	public void setNullCheck(boolean nullCheck) {
		this.nullCheck = nullCheck;
	}

	public String getShowField() {
        return showField;
    }

    public void setShowField(String showField) {
        this.showField = showField;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
