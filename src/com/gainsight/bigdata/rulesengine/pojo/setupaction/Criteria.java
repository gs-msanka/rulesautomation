package com.gainsight.bigdata.rulesengine.pojo.setupaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vmenon on 9/13/2015.
 */
public class Criteria {

    private String showField = "";
    private String operator = "";
    private String field = "";
    private String value = "";

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
