package com.gainsight.bigdata.rulesengine.bean.RuleAction;

import com.gainsight.bigdata.rulesengine.bean.RuleSetup.Field;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Giribabu on 04/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class ActionExpression {

    private String alias;
    private String operator;
    private Field left;
    private ActionFieldValue right;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Field getLeft() {
        return left;
    }

    public void setLeft(Field left) {
        this.left = left;
    }

    public ActionFieldValue getRight() {
        return right;
    }

    public void setRight(ActionFieldValue right) {
        this.right = right;
    }
}
