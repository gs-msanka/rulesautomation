package com.gainsight.bigdata.rulesengine.pojo.setuprule;

/**
 * Created by vmenon on 9/14/2015.
 */
public class FilterFields {

    private String fieldName = "";
    private String operator = "";
    private String value = "";

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
