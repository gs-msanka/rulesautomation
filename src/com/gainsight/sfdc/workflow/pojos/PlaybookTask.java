package com.gainsight.sfdc.workflow.pojos;

import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by gainsight on 19/11/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaybookTask {

    private String fieldName;
    private String objectName;
    private String ctrlType;
    private String label;
    private String fieldType;
    private boolean isRequired;
    private List<HashMap<String, String>> ctrlData;
    private List<HashMap<String, String>> value;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getCtrlType() {
        return ctrlType;
    }

    public void setCtrlType(String ctrlType) {
        this.ctrlType = ctrlType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public List<HashMap<String, String>> getCtrlData() {
        return ctrlData;
    }

    public void setCtrlData(List<HashMap<String, String>> ctrlData) {
        this.ctrlData = ctrlData;
    }

    public List<HashMap<String, String>> getValue() {
        return value;
    }

    public void setValue(List<HashMap<String, String>> value) {
        this.value = value;
    }
}
