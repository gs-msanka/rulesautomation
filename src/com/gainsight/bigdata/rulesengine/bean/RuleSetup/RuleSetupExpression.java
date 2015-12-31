package com.gainsight.bigdata.rulesengine.bean.RuleSetup;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Giribabu on 04/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class RuleSetupExpression {

    String operator;
    FieldInfo left;
    FieldInfo[] right;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public FieldInfo getLeft() {
        return left;
    }

    public void setLeft(FieldInfo left) {
        this.left = left;
    }

    public FieldInfo[] getRight() {
        return right;
    }

    public void setRight(FieldInfo[] right) {
        this.right = right;
    }
}
