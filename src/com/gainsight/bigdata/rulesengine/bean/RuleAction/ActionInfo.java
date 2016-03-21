package com.gainsight.bigdata.rulesengine.bean.RuleAction;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Created by Giribabu on 04/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionInfo {
    //Used in copilot out reach.
    private int order;
    private String actionType;
    private String recipientStrategy;
    private String identifierType;
    private String recipientFieldName;
    private ActionCondition condition;
    private List<ActionTrueCase> trueCase;
    private List<ActionQuery> queries;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

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

    public ActionCondition getCondition() {
        return condition;
    }

    public void setCondition(ActionCondition condition) {
        this.condition = condition;
    }

    public List<ActionTrueCase> getTrueCase() {
        return trueCase;
    }

    public void setTrueCase(List<ActionTrueCase> trueCase) {
        this.trueCase = trueCase;
    }

    public List<ActionQuery> getQueries() {
        return queries;
    }

    public void setQueries(List<ActionQuery> queries) {
        this.queries = queries;
    }
}
