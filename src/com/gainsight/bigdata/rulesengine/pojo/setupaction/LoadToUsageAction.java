package com.gainsight.bigdata.rulesengine.pojo.setupaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vmenon on 9/13/2015.
 */
public class LoadToUsageAction {

    private List<FieldMapping> fieldMappings = new ArrayList<>();

    public List<FieldMapping> getFieldMappings() {
        return fieldMappings;
    }

    public void setFieldMappings(List<FieldMapping> fieldMappings) {
        this.fieldMappings = fieldMappings;
    }
}
