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
    private String recipientStrategy;
    private String identifierType;
    private String recipientFieldName;
    private String actionInfo; //This String can be parsed to ActionInfo Class
    private Object params;

    public String getRecipientStrategy() {
        return recipientStrategy;
    }

    public void setRecipientStrategy(String recipientStrategy) {
        this.recipientStrategy = recipientStrategy;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    public String getRecipientFieldName() {
        return recipientFieldName;
    }

    public void setRecipientFieldName(String recipientFieldName) {
        this.recipientFieldName = recipientFieldName;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

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
