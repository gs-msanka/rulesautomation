package com.gainsight.sfdc.rulesEngine.pojos;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: gainsight
 * Date: 12/09/14
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class RuleAdvancedCriteria {

    private String filterLogic;
    private ArrayList<FilterCriteria> filterCriteria;

    public String getFilterLogic() {
        return filterLogic;
    }

    public void setFilterLogic(String filterLogic) {
        this.filterLogic = filterLogic;
    }

    public ArrayList<FilterCriteria> getFilterCriteria() {
        return filterCriteria;
    }

    public void setFilterCriteria(ArrayList<FilterCriteria> filterCriteria) {
        this.filterCriteria = filterCriteria;
    }


    public static class FilterCriteria {
        private String name;
        private String value;
        private String objectName;
        private String fieldType;
        private String operator;
        private String logicalOperator;
        private String fieldLabel;
        private String operatorLabel;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getObjectName() {
            return objectName;
        }

        public void setObjectName(String objectName) {
            this.objectName = objectName;
        }

        public String getFieldType() {
            return fieldType;
        }

        public void setFieldType(String fieldType) {
            this.fieldType = fieldType;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getLogicalOperator() {
            return logicalOperator;
        }

        public void setLogicalOperator(String logicalOperator) {
            this.logicalOperator = logicalOperator;
        }

        public String getFieldLabel() {
            return fieldLabel;
        }

        public void setFieldLabel(String fieldLabel) {
            this.fieldLabel = fieldLabel;
        }

        public String getOperatorLabel() {
            return operatorLabel;
        }

        public void setOperatorLabel(String operatorLabel) {
            this.operatorLabel = operatorLabel;
        }
    }

}
