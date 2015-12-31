package com.gainsight.bigdata.rulesengine.bean.RuleSetup;

import com.gainsight.bigdata.rulesengine.bean.RuleSetup.Field;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Created by Giribabu on 03/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class CalculatedField {

    private String fieldName;
    private String field;
    private String objectName;
    private String type;
    private String valueType;
    private String operator;
    private CalculatedFieldValue left;
    private CalculatedFieldValue right;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public CalculatedFieldValue getLeft() {
        return left;
    }

    public void setLeft(CalculatedFieldValue left) {
        this.left = left;
    }

    public CalculatedFieldValue getRight() {
        return right;
    }

    public void setRight(CalculatedFieldValue right) {
        this.right = right;
    }

    public static class CalculatedFieldValue {
        private String dataSourceType;
        private String field;
        private String entity;
        private String alias;
        private String aggregation;
        private String operandType;
        private String fieldName;
        private String averagingStrategy;
        @JsonProperty("aggregationInfo")
        private FieldAggregationInfo aggregationInfo;

        public String getDataSourceType() {
            return dataSourceType;
        }

        public void setDataSourceType(String dataSourceType) {
            this.dataSourceType = dataSourceType;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getEntity() {
            return entity;
        }

        public void setEntity(String entity) {
            this.entity = entity;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getAggregation() {
            return aggregation;
        }

        public void setAggregation(String aggregation) {
            this.aggregation = aggregation;
        }

        public String getOperandType() {
            return operandType;
        }

        public void setOperandType(String operandType) {
            this.operandType = operandType;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getAveragingStrategy() {
            return averagingStrategy;
        }

        public void setAveragingStrategy(String averagingStrategy) {
            this.averagingStrategy = averagingStrategy;
        }

        public FieldAggregationInfo getAggregationInfo() {
            return aggregationInfo;
        }

        public void setAggregationInfo(FieldAggregationInfo aggregationInfo) {
            this.aggregationInfo = aggregationInfo;
        }
    }

    public static class FieldAggregationInfo {
        private String periodType;
        private String granularity;
        private int numberOfPeriods;
        private List<Field> groupBy;
        private Field timeIdentifier;

        public String getPeriodType() {
            return periodType;
        }

        public void setPeriodType(String periodType) {
            this.periodType = periodType;
        }

        public String getGranularity() {
            return granularity;
        }

        public void setGranularity(String granularity) {
            this.granularity = granularity;
        }

        public int getNumberOfPeriods() {
            return numberOfPeriods;
        }

        public void setNumberOfPeriods(int numberOfPeriods) {
            this.numberOfPeriods = numberOfPeriods;
        }

        public List<Field> getGroupBy() {
            return groupBy;
        }

        public void setGroupBy(List<Field> groupBy) {
            this.groupBy = groupBy;
        }

        public Field getTimeIdentifier() {
            return timeIdentifier;
        }

        public void setTimeIdentifier(Field timeIdentifier) {
            this.timeIdentifier = timeIdentifier;
        }
    }
}
