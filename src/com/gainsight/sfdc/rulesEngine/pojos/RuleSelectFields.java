package com.gainsight.sfdc.rulesEngine.pojos;

/**
 * Created with IntelliJ IDEA.
 * User: gainsight
 * Date: 23/08/14
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * [{"ctrlType":"TEXT","fieldName":"Name","fieldType":"STRING","hasJBCXMNS":false,
 * "isCreateable":true,"isFilterable":true,"isReference":false,"label":"Account Name",
 * "logicalOperator":"","name":"Name","objectName":"Account","operatorsList":"[]",
 * "selectedOperator":"","value":"","valueType":"STRING","entity":"Account"},
 * {"ctrlType":"TEXT","fieldName":"Account__r.Name","fieldType":"STRING",
 * "hasJBCXMNS":true,"isCreateable":true,"isFilterable":true,"isReference":true,
 * "label":"Account","logicalOperator":"","name":"Account__c","objectName":"CustomerInfo__c",
 * "operatorsList":"[]","referenceObjList":["Account"],
 * "selectedOperator":"","value":"","valueType":"STRING","entity":"CustomerInfo__c"}]
 */
public class RuleSelectFields {

    String ctrlType;
    String fieldName;
    String fieldType;
    String hasJBCXMNS;
    Boolean isCreateable;
    String isFilterable;
    String isReference;
    String label;
    String logicalOperator;
    String name;
    String objectName;

    public Boolean getCreateable() {
        return isCreateable;
    }

    public void setCreateable(Boolean createable) {
        isCreateable = createable;
    }

    String operatorsList;
    String selectedOperator;
    String value;
    String valueType;
    String entity;

    public String getCtrlType() {
        return ctrlType;
    }

    public void setCtrlType(String ctrlType) {
        this.ctrlType = ctrlType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getHasJBCXMNS() {
        return hasJBCXMNS;
    }

    public void setHasJBCXMNS(String hasJBCXMNS) {
        this.hasJBCXMNS = hasJBCXMNS;
    }


    public String getFilterable() {
        return isFilterable;
    }

    public void setFilterable(String filterable) {
        isFilterable = filterable;
    }

    public String getReference() {
        return isReference;
    }

    public void setReference(String reference) {
        isReference = reference;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLogicalOperator() {
        return logicalOperator;
    }

    public void setLogicalOperator(String logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getOperatorsList() {
        return operatorsList;
    }

    public void setOperatorsList(String operatorsList) {
        this.operatorsList = operatorsList;
    }

    public String getSelectedOperator() {
        return selectedOperator;
    }

    public void setSelectedOperator(String selectedOperator) {
        this.selectedOperator = selectedOperator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
}
