package com.gainsight.bigdata.rulesengine.bean.RuleAction;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Created by Giribabu on 04/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class ActionConditionExpression {


    private String operator;
    private List<ActionInnerCondition> right;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<ActionInnerCondition> getRight() {
        return right;
    }

    public void setRight(List<ActionInnerCondition> right) {
        this.right = right;
    }
}
