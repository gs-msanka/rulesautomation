package com.gainsight.sfdc.survey.pojo;

public class SurveyAddParticipants {

	private String LoadParticipants;
	private String SelectRole;
	private String SelectRoleField;
	private String Filter;
	private String Operator;
	private String Value;
	private String AdvancedLogic;
	private String ExcludeParticipants;
	
	public String getLoadParticipants() {
		return LoadParticipants;
	}
	public void setLoadParticipants(String loadParticipants) {
		LoadParticipants = loadParticipants;
	}
	public String getSelectRole() {
		return SelectRole;
	}
	public void setSelectRole(String selectRole) {
		SelectRole = selectRole;
	}
	public String getSelectRoleField() {
		return SelectRoleField;
	}
	public void setSelectRoleField(String selectRoleField) {
		SelectRoleField = selectRoleField;
	}
	public String getOperator() {
		return Operator;
	}
	public void setOperator(String operator) {
		Operator = operator;
	}
	public String getValue() {
		return Value;
	}
	public void setValue(String value) {
		Value = value;
	}
	public String getAdvancedLogic() {
		return AdvancedLogic;
	}
	public void setAdvancedLogic(String advancedLogic) {
		AdvancedLogic = advancedLogic;
	}
	public String getExcludeParticipants() {
		return ExcludeParticipants;
	}
	public void setExcludeParticipants(String excludeParticipants) {
		ExcludeParticipants = excludeParticipants;
	}
	
	
		/*  "LoadParticipants": "Contact Object",
		  "SelectRole": "Contact",
		  "SelectRoleField": "Title",
		  "Filter": "Account ID Name",
		  "Operator": "starts with",
		  "Value": "SURVEY Account 1",
		  "AdvancedLogic": "",
		  "ExcludeParticipants": ""*/
		
}
