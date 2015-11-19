package com.gainsight.bigdata.rulesengine.pojo.enums;

import com.gainsight.bigdata.rulesengine.pojo.setupaction.*;

/**
 * Created by vmenon on 9/14/2015.
 */
public enum ActionType {
    CTA("createCta"), LoadToMileStone("loadToMileStone"),
    LoadToCustomers("loadToCustomers"), LoadToFeature("loadToFeature"),
    LoadToUsage("loadToUsage"), SendEmail("sendEmail"), SetScore("setScore"),LoadToSFDCObject("loadToSFDCObject"),LoadToMDASubjectArea("loadToMDASubjectArea"),
    CloseCTA("closeCta");

    private String type;

    private ActionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
