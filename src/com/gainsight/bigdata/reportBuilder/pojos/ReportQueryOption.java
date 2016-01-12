package com.gainsight.bigdata.reportBuilder.pojos;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Created by Gainsight on 07/12/15.
 */
public class ReportQueryOption<T> {
	@JsonProperty("orderBy")
	private List<ReportRanking> reportRanking;
	private T limit;

	public List<ReportRanking> getReportRanking() {
		return reportRanking;
	}

	public void setReportRanking(List<ReportRanking> reportRanking) {
		this.reportRanking = reportRanking;
	}

	public T getLimit() {
		return limit;
	}

	public void setLimit(T limit) {
		this.limit = limit;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ReportRanking {
		private String name;
		private String label;
		private String objectName;
		private String fieldType;
		private String aggregation;
		private String sortOrder;
		@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
		private boolean isGroupable;
		private boolean hasJBCXMNS;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getObjectName() {
			return objectName;
		}

		public void setObjectName(String objectName) {
			this.objectName = objectName;
		}

		public String getFieldType() {
			return fieldType;
		}

		public void setFieldType(String fieldType) {
			this.fieldType = fieldType;
		}

		public String getSortOrder() {
			return sortOrder;
		}

		public void setSortOrder(String sortOrder) {
			this.sortOrder = sortOrder;
		}

		public String getAggregation() {
			return aggregation;
		}

		public void setAggregation(String aggregation) {
			this.aggregation = aggregation;
		}

		public boolean isGroupable() {
			return isGroupable;
		}

		public void setisGroupable(boolean isGroupable) {
			this.isGroupable = isGroupable;
		}

		public boolean isHasJBCXMNS() {
			return hasJBCXMNS;
		}

		public void setHasJBCXMNS(boolean hasJBCXMNS) {
			this.hasJBCXMNS = hasJBCXMNS;
		}

	}
}
