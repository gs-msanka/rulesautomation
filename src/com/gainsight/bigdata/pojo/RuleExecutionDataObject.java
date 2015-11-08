/**
 * 
 */
package com.gainsight.bigdata.pojo;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * @author Abhilash Thaduka
 *
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleExecutionDataObject {

	private String createdBy;
	private String createdByName;
	private String modifiedBy;
	private String modifiedByName;
	private String tenantId;
	private String statusId;
	private String requestId;
	private String status;
	private String messageType;
	private String sourceType;
	private String message;
	private List<String> executionMessages = new ArrayList<String>();
	private long scheduledTime;
	private long queuedTime;
	private long startTime;
	private long endTime;
	private String processSpecificId;
	private String instructionType;
	private Integer nextScheduledRun;
	private Parameters parameters;
	private String errorCode;
	private Integer retryCount;
	private String executionType;
	private String createdDateStr;
	private String modifiedDateStr;

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

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getStatusId() {
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getExecutionMessages() {
		return executionMessages;
	}

	public void setExecutionMessages(List<String> executionMessages) {
		this.executionMessages = executionMessages;
	}

	public long getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(Long scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public long getQueuedTime() {
		return queuedTime;
	}

	public void setQueuedTime(long queuedTime) {
		this.queuedTime = queuedTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getProcessSpecificId() {
		return processSpecificId;
	}

	public void setProcessSpecificId(String processSpecificId) {
		this.processSpecificId = processSpecificId;
	}

	public String getInstructionType() {
		return instructionType;
	}

	public void setInstructionType(String instructionType) {
		this.instructionType = instructionType;
	}

	public Integer getNextScheduledRun() {
		return nextScheduledRun;
	}

	public void setNextScheduledRun(Integer nextScheduledRun) {
		this.nextScheduledRun = nextScheduledRun;
	}

	public Parameters getParameters() {
		return parameters;
	}

	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public Integer getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}

	public String getExecutionType() {
		return executionType;
	}

	public void setExecutionType(String executionType) {
		this.executionType = executionType;
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

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Parameters {

		private long ruleDate;
		private String ruleId;
		private Boolean isScheduledRun;
		private Boolean isTestRun;

		public long getRuleDate() {
			return ruleDate;
		}

		public void setRuleDate(long ruleDate) {
			this.ruleDate = ruleDate;
		}

		public String getRuleId() {
			return ruleId;
		}

		public void setRuleId(String ruleId) {
			this.ruleId = ruleId;
		}

		public Boolean getIsScheduledRun() {
			return isScheduledRun;
		}

		public void setIsScheduledRun(Boolean isScheduledRun) {
			this.isScheduledRun = isScheduledRun;
		}

		public Boolean getIsTestRun() {
			return isTestRun;
		}

		public void setIsTestRun(Boolean isTestRun) {
			this.isTestRun = isTestRun;
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ProcessReport {

		private String processReportType;
		private Boolean queryPassed;
		private List<Object> actionResults = new ArrayList<Object>();
		private String processResult;
		private Integer externalSystemCallCount;

		public String getProcessReportType() {
			return processReportType;
		}

		public void setProcessReportType(String processReportType) {
			this.processReportType = processReportType;
		}

		public Boolean getQueryPassed() {
			return queryPassed;
		}

		public void setQueryPassed(Boolean queryPassed) {
			this.queryPassed = queryPassed;
		}

		public List<Object> getActionResults() {
			return actionResults;
		}

		public void setActionResults(List<Object> actionResults) {
			this.actionResults = actionResults;
		}

		public String getProcessResult() {
			return processResult;
		}

		public void setProcessResult(String processResult) {
			this.processResult = processResult;
		}

		public Integer getExternalSystemCallCount() {
			return externalSystemCallCount;
		}

		public void setExternalSystemCallCount(Integer externalSystemCallCount) {
			this.externalSystemCallCount = externalSystemCallCount;
		}

	}
}
