package com.gainsight.bigdata.rulesengine.bean.RuleSetup;

import com.gainsight.bigdata.rulesengine.bean.RuleSetup.Field;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Giribabu on 04/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class Criteria {
    @JsonProperty("isGainSightCustomer")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
    private boolean isGainSightCustomer = false;
    private String alias;
    private Field left;
    private String operator;
    private FilterValue right;

    public boolean isGainSightCustomer() {
        return isGainSightCustomer;
    }

    public void setIsGainSightCustomer(boolean isGainSightCustomer) {
        this.isGainSightCustomer = isGainSightCustomer;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Field getLeft() {
        return left;
    }

    public void setLeft(Field left) {
        this.left = left;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public FilterValue getRight() {
        return right;
    }

    public void setRight(FilterValue right) {
        this.right = right;
    }

    //TODO -  This filter value can also be field, As filter can be on other field / a static value - Need to Look back.
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
    public static class FilterValue {
        //TODO - have not found the actual use of this, so making as object.
        private Object[] keys;
        private String type;
        private String valueType;
        @JsonProperty("isNull")
        private boolean isNull;
        private Object value;
        private String format;
        private boolean isExpression;
        private Object expression;

        public boolean isExpression() {
            return isExpression;
        }

        public void setIsExpression(boolean isExpression) {
            this.isExpression = isExpression;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public Object[] getKeys() {
            return keys;
        }

        public void setKeys(Object[] keys) {
            this.keys = keys;
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

        public boolean isNull() {
            return isNull;
        }

        public void setIsNull(boolean isNull) {
            this.isNull = isNull;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}
