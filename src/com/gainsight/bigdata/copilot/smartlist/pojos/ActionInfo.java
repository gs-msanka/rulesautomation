package com.gainsight.bigdata.copilot.smartlist.pojos;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;


@JsonPropertyOrder({ "params", "actionType", "recipientStrategy",
		"identifierType", "queries", "recipientFieldName" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionInfo {

	@JsonProperty("params")
	private SmartListParams params;
	@JsonProperty("actionType")
	private String actionType;
	@JsonProperty("recipientStrategy")
	private String recipientStrategy;
	@JsonProperty("identifierType")
	private String identifierType;
	@JsonProperty("queries")
	private List<Query> queries = new ArrayList<Query>();
	@JsonProperty("recipientFieldName")
	private String recipientFieldName;

	@JsonProperty("params")
	public SmartListParams getParams() {
		return params;
	}

	@JsonProperty("params")
	public void setParams(SmartListParams params) {
		this.params = params;
	}

	@JsonProperty("actionType")
	public String getActionType() {
		return actionType;
	}

	@JsonProperty("actionType")
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	@JsonProperty("recipientStrategy")
	public String getRecipientStrategy() {
		return recipientStrategy;
	}

	@JsonProperty("recipientStrategy")
	public void setRecipientStrategy(String recipientStrategy) {
		this.recipientStrategy = recipientStrategy;
	}

	@JsonProperty("identifierType")
	public String getIdentifierType() {
		return identifierType;
	}

	@JsonProperty("identifierType")
	public void setIdentifierType(String identifierType) {
		this.identifierType = identifierType;
	}

	@JsonProperty("queries")
	public List<Query> getQueries() {
		return queries;
	}

	@JsonProperty("queries")
	public void setQueries(List<Query> queries) {
		this.queries = queries;
	}

	@JsonProperty("recipientFieldName")
	public String getRecipientFieldName() {
		return recipientFieldName;
	}

	@JsonProperty("recipientFieldName")
	public void setRecipientFieldName(String recipientFieldName) {
		this.recipientFieldName = recipientFieldName;
	}

	
	public static class SmartListParams {

		@JsonProperty("areaName")
		private String areaName;

		@JsonProperty("areaName")
		public String getAreaName() {
			return areaName;
		}

		@JsonProperty("areaName")
		public void setAreaName(String areaName) {
			this.areaName = areaName;
		}
	}

	@JsonPropertyOrder({ "SFDC" })
	public static class SmartListProperties {

		@JsonProperty("SFDC")
		private SFDC SFDC;

		@JsonProperty("SFDC")
		public SFDC getSFDC() {
			return SFDC;
		}

		@JsonProperty("SFDC")
		public void setSFDC(SFDC SFDC) {
			this.SFDC = SFDC;
		}
	}

	@JsonPropertyOrder({ "externalIdentifier", "lookUpFieldInfos",
			"identifier", "query" })
	public static class Query {

		@JsonProperty("externalIdentifier")
		private SmartListExternalIdentifier externalIdentifier;
		@JsonProperty("lookUpFieldInfos")
		private List<SmartListLookUpFieldInfo> lookUpFieldInfos = new ArrayList<SmartListLookUpFieldInfo>();
		@JsonProperty("identifier")
		private SmartListExternalIdentifier identifier;
		@JsonProperty("query")
		private String query;

		@JsonProperty("externalIdentifier")
		public SmartListExternalIdentifier getExternalIdentifier() {
			return externalIdentifier;
		}

		@JsonProperty("externalIdentifier")
		public void setExternalIdentifier(SmartListExternalIdentifier externalIdentifier) {
			this.externalIdentifier = externalIdentifier;
		}

		@JsonProperty("lookUpFieldInfos")
		public List<SmartListLookUpFieldInfo> getLookUpFieldInfos() {
			return lookUpFieldInfos;
		}

		@JsonProperty("lookUpFieldInfos")
		public void setLookUpFieldInfos(List<SmartListLookUpFieldInfo> lookUpFieldInfos) {
			this.lookUpFieldInfos = lookUpFieldInfos;
		}

		@JsonProperty("identifier")
		public SmartListExternalIdentifier getIdentifier() {
			return identifier;
		}

		@JsonProperty("identifier")
		public void setIdentifier(SmartListExternalIdentifier identifier) {
			this.identifier = identifier;
		}

		@JsonProperty("query")
		public String getQuery() {
			return query;
		}

		@JsonProperty("query")
		public void setQuery(String query) {
			this.query = query;
		}
	}

	public static class SFDC {

		@JsonProperty("keys")
		private List<String> keys = new ArrayList<String>();

		@JsonProperty("keys")
		public List<String> getKeys() {
			return keys;
		}

		@JsonProperty("keys")
		public void setKeys(List<String> keys) {
			this.keys = keys;
		}
	}
}
