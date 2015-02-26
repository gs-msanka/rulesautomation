package com.gainsight.sfdc.survey.pojo;

import java.util.ArrayList;
import java.util.HashMap;

import com.gainsight.sfdc.workflow.pojos.CTA;

public class SurveyRuleProperties {
	private ArrayList<String> ruleQues;
	private HashMap<String,ArrayList<String>> ruleAnswers;
	private String advLogic;
	private CTA ctaProps=new CTA();
	
	public ArrayList<String> getRuleQues() {
		return ruleQues;
	}
	public void setRuleQues(ArrayList<String> ruleQues) {
		this.ruleQues = ruleQues;
	}
	public HashMap<String, ArrayList<String>> getRuleAnswers() {
		return ruleAnswers;
	}
	public void setRuleAnswers(HashMap<String, ArrayList<String>> ruleAnswers) {
		this.ruleAnswers = ruleAnswers;
	}
	public String getAdvLogic() {
		return advLogic;
	}
	public void setAdvLogic(String advLogic) {
		this.advLogic = advLogic;
	}
	public CTA getCtaProps() {
		return ctaProps;
	}
	public void setCtaProps(CTA ctaProps) {
		this.ctaProps = ctaProps;
	} 
	
	
}
