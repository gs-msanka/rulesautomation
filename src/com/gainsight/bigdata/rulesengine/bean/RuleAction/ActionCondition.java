package com.gainsight.bigdata.rulesengine.bean.RuleAction;


import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Giribabu on 04/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class ActionCondition {
    private String type;
    private String valueType;
    private ActionConditionExpression expression;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public ActionConditionExpression getExpression() {
        return expression;
    }

    public void setExpression(ActionConditionExpression expression) {
        this.expression = expression;
    }
}
