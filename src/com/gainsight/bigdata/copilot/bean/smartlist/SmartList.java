package com.gainsight.bigdata.copilot.bean.smartlist;

import com.gainsight.bigdata.pojo.Schedule;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Created by Parthibhan on 04/12/15.
 * Updated by Giribabu on 05/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class SmartList {

	private String createdBy;
	private String createdByName;
	private String modifiedBy;
	private String modifiedByName;
	private String smartListId;
	private String tenantId;
	private String name;
	private String description;
	private String type;
	private String status;
	private String dataSourceType;
	private boolean refreshList;
	private String createdDateStr;
	private String modifiedDateStr;
	private String smartListName;
	private SmartListRule automatedRule;
	private SmartListStats stats;
	private SmartListSchedulerInfo schedulerInfo;
	private Object showFields;     //Contains Map of CollectionId, List of fields information.

	public Object getShowFields() {
		return showFields;
	}

	public void setShowFields(Object showFields) {
		this.showFields = showFields;
	}

	public SmartListSchedulerInfo getSchedulerInfo() {
		return schedulerInfo;
	}

	public void setSchedulerInfo(SmartListSchedulerInfo schedulerInfo) {
		this.schedulerInfo = schedulerInfo;
	}

	public String getSmartListName() {
		return smartListName;
	}

	public void setSmartListName(String smartListName) {
		this.smartListName = smartListName;
	}

	public SmartListStats getStats() {
		return stats;
	}

	public void setStats(SmartListStats stats) {
		this.stats = stats;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getModifiedByName() {
		return modifiedByName;
	}

	public void setModifiedByName(String modifiedByName) {
		this.modifiedByName = modifiedByName;
	}

	public String getSmartListId() {
		return smartListId;
	}

	public void setSmartListId(String smartListId) {
		this.smartListId = smartListId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public boolean isRefreshList() {
		return refreshList;
	}

	public void setRefreshList(boolean refreshList) {
		this.refreshList = refreshList;
	}

	public String getCreatedDateStr() {
		return createdDateStr;
	}

	public void setCreatedDateStr(String createdDateStr) {
		this.createdDateStr = createdDateStr;
	}

	public String getModifiedDateStr() {
		return modifiedDateStr;
	}

	public void setModifiedDateStr(String modifiedDateStr) {
		this.modifiedDateStr = modifiedDateStr;
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

	public String getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(String dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

	public SmartListRule getAutomatedRule() {
		return automatedRule;
	}

	public void setAutomatedRule(SmartListRule automatedRule) {
		this.automatedRule = automatedRule;
	}

	public static class SmartListStats {
		private int contactCount = 0;
		private int customerCount = 0;

		public int getContactCount() {
			return contactCount;
		}

		public void setContactCount(int contactCount) {
			this.contactCount = contactCount;
		}

		public int getCustomerCount() {
			return customerCount;
		}

		public void setCustomerCount(int customerCount) {
			this.customerCount = customerCount;
		}
	}

	@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
	public static class SmartListSchedulerInfo {
		@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
		String method;
		List<Schedule> schedules;

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public List<Schedule> getSchedules() {
			return schedules;
		}

		public void setSchedules(List<Schedule> schedules) {
			this.schedules = schedules;
		}
	}

}
