package com.gainsight.sfdc.survey.pojo;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.gainsight.sfdc.workflow.pojos.CTA.Attribute;
@JsonIgnoreProperties(ignoreUnknown = true)
public class SurveyAddParticipants implements Cloneable {

	private String loadParticipantsFrom;
	private String selectRole;
	private String selectRoleField;
	private String searchFilter;
	private String filter;
	private String operator;
	private String value;
	private String advancedLogic;
	private String excludeParticipants_Type;
	private String excludePtp_Survey;
	private boolean loadAll;
	private String customObjectName;
	private String displayName;
	private String displayEmail;
	private String displayRole;
	private String contactID;
	private String contactEmail;
	private String contactRole;
	private String contactName;
	

	private List<ParticipantDetails> participantsList = new ArrayList<ParticipantDetails>();
	
	public void setCustomObjectName(String customObjectName) {
		this.customObjectName = customObjectName;
	}

	public String getCustomObjectName() {
		return customObjectName;
	}

	public boolean isLoadAll() {
		return loadAll;
	}

	public void setLoadAll(boolean loadAll) {
		this.loadAll = loadAll;
	}

	public String getContactID() {
		return contactID;
	}

	public void setContactID(String contactID) {
		this.contactID = contactID;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getContactRole() {
		return contactRole;
	}

	public void setContactRole(String contactRole) {
		this.contactRole = contactRole;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
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

	public String getSearchFilter() {
		return searchFilter;
	}

	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayEmail() {
		return displayEmail;
	}

	public void setDisplayEmail(String displayEmail) {
		this.displayEmail = displayEmail;
	}

	public String getDisplayRole() {
		return displayRole;
	}

	public void setDisplayRole(String displayRole) {
		this.displayRole = displayRole;
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
