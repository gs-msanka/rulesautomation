package com.gainsight.bigdata.rulesengine.pojo.setuprule;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by vmenon on 9/14/2015.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class SetupData {

    private String sourceObject = "";
    private List<ShowFields> showFields = new ArrayList<>();
    private List<FilterFields> filterFields = new ArrayList<>();
 
 
    public String getSourceObject() {
        return sourceObject;
    }

    public void setSourceObject(String sourceObject) {
        this.sourceObject = sourceObject;
    }

    public List<ShowFields> getShowFields() {
        return showFields;
    }

    public void setShowFields(List<ShowFields> showFields) {
        this.showFields = showFields;
    }

    public List<FilterFields> getFilterFields() {
        return filterFields;
    }

    public void setFilterFields(List<FilterFields> filterFields) {
        this.filterFields = filterFields;
    }

  
}
