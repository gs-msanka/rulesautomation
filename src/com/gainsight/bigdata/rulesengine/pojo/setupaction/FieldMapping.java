package com.gainsight.bigdata.rulesengine.pojo.setupaction;

/**
 * Created by vmenon on 9/14/2015.
 */
public class FieldMapping {
    private String sourceObject = "";
    private String sourceField = "";
    private String destination = "";
    private boolean isCustom = false;
    
    public String getSourceField() {
		return sourceField;
	}

	public void setSourceField(String sourceField) {
		this.sourceField = sourceField;
	}

	

    public String getSourceObject() {
        return sourceObject;
    }

    public void setSourceObject(String source) {
        this.sourceObject = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setIsCustom(boolean isCustom) {
        this.isCustom = isCustom;
    }
}