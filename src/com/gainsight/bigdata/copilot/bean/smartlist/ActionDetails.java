package com.gainsight.bigdata.copilot.bean.smartlist;

import com.gainsight.bigdata.rulesengine.bean.RuleAction.ActionInfo;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Parthibhan on 04/12/15.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class ActionDetails {

	private String actionType;
	private String actionInfo; //This String can be parsed to ActionInfo Class.


    public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

    public String getActionInfo() {
        return actionInfo;
    }

    public void setActionInfo(String actionInfo) {
        this.actionInfo = actionInfo;
    }
}
