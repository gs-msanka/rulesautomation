package com.gainsight.bigdata.copilot.pojos;


public class SmartList {
	
	private String name;
	private String description;
	private String type;
	private String status;
	private String refreshList;
	private String dataSourceType;	
	private AutomatedRule automatedRule;
	private Stats stats;
	
	public AutomatedRule getAutomatedRule() {
		return automatedRule;
	}

	public void setAutomatedRule(AutomatedRule automatedRule) {
		this.automatedRule = automatedRule;
	}

	public Stats getStats() {
		return stats;
	}
	
	public void setStats(Stats stats) {
		this.stats = stats;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRefreshList() {
		return refreshList;
	}
	public void setRefreshList(String refreshList) {
		this.refreshList = refreshList;
	}
	public String getDataSourceType() {
		return dataSourceType;
	}
	public void setDataSourceType(String dataSourceType) {
		this.dataSourceType = dataSourceType;
	}
	
	/*public static class Stats{
    	private String contactCount;
    	private String customerCount;
    	
    	public String getContactCount() {
			return contactCount;
		}
		public void setContactCount(String contactCount) {
			this.contactCount = contactCount;
		}
		public String getCustomerCount() {
			return customerCount;
		}
		public void setCustomerCount(String customerCount) {
			this.customerCount = customerCount;
		}
		
    }*/
	
	/* public static class AutomatedRule{
	    	private String relatedId;
	    	private String ruleType;
	    	private String description;
	    	private String triggerCriteria;
	    	private String sourceType;
	    	private ArrayList<ActionDetails> actionDetails;
	    	private String triggerUsageOn;
	    	
	    	
			public ArrayList<ActionDetails> getActionDetails() {
				return actionDetails;
			}
			public void setActionDetails(ArrayList<ActionDetails> actionDetails) {
				this.actionDetails = actionDetails;
			}
			
	    	
	    	public String getRelatedId() {
				return relatedId;
			}
			public void setRelatedId(String relatedId) {
				this.relatedId = relatedId;
			}
			public String getRuleType() {
				return ruleType;
			}
			public void setRuleType(String ruleType) {
				this.ruleType = ruleType;
			}
			public String getDescription() {
				return description;
			}
			public void setDescription(String description) {
				this.description = description;
			}
			public String getTriggerCriteria() {
				return triggerCriteria;
			}
			public void setTriggerCriteria(String triggerCriteria) {
				this.triggerCriteria = triggerCriteria;
			}
			public String getSourceType() {
				return sourceType;
			}
			public void setSourceType(String sourceType) {
				this.sourceType = sourceType;
			}

			public String getTriggerUsageOn() {
				return triggerUsageOn;
			}
			public void setTriggerUsageOn(String triggerUsageOn) {
				this.triggerUsageOn = triggerUsageOn;
			}
			
	    }*/
	

}
