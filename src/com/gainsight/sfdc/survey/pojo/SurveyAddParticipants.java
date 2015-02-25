package com.gainsight.sfdc.survey.pojo;

import java.util.ArrayList;
import java.util.List;

import com.gainsight.sfdc.workflow.pojos.CTA.Attribute;

public class SurveyAddParticipants implements Cloneable {

	private String loadParticipantsFrom;
	private String selectRole;
	private String selectRoleField;
	private String filter;
	private String operator;
	private String value;
	private String advancedLogic;
	private String excludeParticipants_Type;
	private String excludePtp_Survey;
	private boolean loadAll;
	private List<ParticipantDetails> participantsList = new ArrayList<ParticipantDetails>();
	
	public boolean isLoadAll() {
		return loadAll;
	}
	public void setLoadAll(boolean loadAll) {
		this.loadAll = loadAll;
	}
	public String getExcludePtp_Survey() {
		return excludePtp_Survey;
	}
	public void setExcludePtp_Survey(String excludePtp_Survey) {
		this.excludePtp_Survey = excludePtp_Survey;
	}
	public String getLoadParticipantsFrom() {
		return loadParticipantsFrom;
	}
	public void setLoadParticipantsFrom(String loadParticipantsFrom) {
		this.loadParticipantsFrom = loadParticipantsFrom;
	}
	public String getSelectRole() {
		return selectRole;
	}
	public void setSelectRole(String selectRole) {
		this.selectRole = selectRole;
	}
	public String getSelectRoleField() {
		return selectRoleField;
	}
	public void setSelectRoleField(String selectRoleField) {
		this.selectRoleField = selectRoleField;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getAdvancedLogic() {
		return advancedLogic;
	}
	public void setAdvancedLogic(String advancedLogic) {
		this.advancedLogic = advancedLogic;
	}
	public String getExcludeParticipants_Type() {
		return excludeParticipants_Type;
	}
	public void setExcludeParticipants_Type(String excludeParticipants_Type) {
		this.excludeParticipants_Type = excludeParticipants_Type;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public List<ParticipantDetails> getParticipantsList() {
		return participantsList;
	}

	public void setParticipantsList(List<ParticipantDetails> participantsList) {
		this.participantsList = participantsList;
	}
	public static class ParticipantDetails{
		private String name;
		private String email;
		private String accName;
		private String role;
		
		public void setName(String name){
			this.name=name;
		}
		public String getName(){
			return name;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getAccName() {
			return accName;
		}
		public void setAccName(String accName) {
			this.accName = accName;
		}
		public String getRole() {
			return role;
		}
		public void setRole(String role) {
			this.role = role;
		}
	}
	}
