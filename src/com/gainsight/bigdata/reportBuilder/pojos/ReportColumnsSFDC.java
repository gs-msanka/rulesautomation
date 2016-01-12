package com.gainsight.bigdata.reportBuilder.pojos;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by Gainsight on 07/12/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportColumnsSFDC<T> {

    private String name;
    private String label;
    private int displayOrder;
    private String fieldType;
    private T decimalPlaces;
    private String objectName;
    private boolean visibilityMode;
    @JsonProperty("isGroupable")
    private boolean groupable;
    private boolean hasJBCXMNS;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public T getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(T decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public boolean isVisibilityMode() {
        return visibilityMode;
    }

    public void setVisibilityMode(boolean visibilityMode) {
        this.visibilityMode = visibilityMode;
    }

    public boolean isGroupable() {
        return groupable;
    }

    public void setGroupable(boolean groupable) {
        this.groupable = groupable;
    }

    public boolean isHasJBCXMNS() {
        return hasJBCXMNS;
    }

    public void setHasJBCXMNS(boolean hasJBCXMNS) {
        this.hasJBCXMNS = hasJBCXMNS;
    }

}
