package com.gainsight.bigdata.rulesengine.pojo;

import com.gainsight.bigdata.rulesengine.pojo.scheduler.ShowScheduler;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.RuleAction;
import com.gainsight.bigdata.rulesengine.pojo.setuprule.SetupRulePojo;
import com.gainsight.sfdc.workflow.pojos.CTA.EventRecurring;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vmenon on 9/14/2015.
 */
public class RulesPojo {
    private String ruleType = "";
    private String ruleName = "";
    private String ruleFor = "Account";
    private String relationshipType;
    private String ruleDescription = "";
    private SetupRulePojo setupRule = new SetupRulePojo();
    private List<RuleAction> setupActions = new ArrayList<>();
    private ShowScheduler showScheduler;

	public ShowScheduler getShowScheduler() {
		return showScheduler;
	}

	public void setShowScheduler(ShowScheduler showScheduler) {
		this.showScheduler = showScheduler;
	}

	public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getRuleFor() {
        return ruleFor;
    }

    public void setRuleFor(String ruleFor) {
        this.ruleFor = ruleFor;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }

    public SetupRulePojo getSetupRule() {
        return setupRule;
    }

    public void setSetupRule(SetupRulePojo setupRule) {
        this.setupRule = setupRule;
    }

    public List<RuleAction> getSetupActions() {
        return setupActions;
    }

    public void setSetupActions(List<RuleAction> setupActions) {
        this.setupActions = setupActions;
    }
}
