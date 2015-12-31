package com.gainsight.bigdata.rulesengine.bean.RuleAction;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Giribabu on 08/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class ActionInnerCondition {

    private String type;
    private String valueType;
    private ActionInnerExpression expression;

    public ActionInnerExpression getExpression() {
        return expression;
    }

    public void setExpression(ActionInnerExpression expression) {
        this.expression = expression;
    }

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
}
