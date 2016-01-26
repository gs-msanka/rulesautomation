package com.gainsight.bigdata.reportBuilder.pojos;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Created by Gainsight on 21/05/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportInfoSFDC {

	private String name;
	private String graphType;
	private String visualType;
	private String baseObject;
	private List<ReportColumnsSFDC> columns;
	@JsonProperty("whereAdvanceFilter")
	private ReportAdvanceFilterSFDC whereAdvanceFilter;
	@JsonProperty("havingAdvanceFilter")
	private ReportAdvanceFilterSFDC havingAdvanceFilter;
	@JsonProperty("Dimensions")
	private List<DimensionSFDC> dimensions;
	@JsonProperty("Measures")
	private List<DimensionSFDC> measures;
	private ReportQueryOption reportQueryOption;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGraphType() {
		return graphType;
	}

	public void setGraphType(String graphType) {
		this.graphType = graphType;
	}

	public String getVisualType() {
		return visualType;
	}

	public String getBaseObject() {
		return baseObject;
	}

	public void setBaseObject(String baseObject) {
		this.baseObject = baseObject;
	}

	public void setVisualType(String visualType) {
		this.visualType = visualType;
	}

	public ReportAdvanceFilterSFDC getWhereAdvanceFilter() {
		return whereAdvanceFilter;
	}

	public void setWhereAdvanceFilter(ReportAdvanceFilterSFDC whereAdvanceFilter) {
		this.whereAdvanceFilter = whereAdvanceFilter;
	}

	public ReportAdvanceFilterSFDC getHavingAdvanceFilter() {
		return havingAdvanceFilter;
	}

	public void setHavingAdvanceFilter(ReportAdvanceFilterSFDC havingAdvanceFilter) {
		this.havingAdvanceFilter = havingAdvanceFilter;
	}

	public List<DimensionSFDC> getDimensions() {
		return dimensions;
	}

	public void setDimensions(List<DimensionSFDC> dimensions) {
		this.dimensions = dimensions;
	}

	public List<DimensionSFDC> getMeasures() {
		return measures;
	}

	public void setMeasures(List<DimensionSFDC> measures) {
		this.measures = measures;
	}

	public List<ReportColumnsSFDC> getColumns() {
		return columns;
	}

	public void setColumns(List<ReportColumnsSFDC> columns) {
		this.columns = columns;
	}

	public ReportQueryOption getReportQueryOption() {
		return reportQueryOption;
	}

	public void setReportQueryOption(ReportQueryOption reportQueryOption) {
		this.reportQueryOption = reportQueryOption;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class DimensionSFDC {
		private String objectName;
		private String name;
		private String label;
		private String aliasName;
		private String fieldType;
		private boolean hasJBCXMNS;
		private String summerizedBy;
		@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
		private boolean groupable;
		private boolean stacked;
		private String aggType;
		private String graphType;
		@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
		private int decimalPlaces = 0;

		public String getObjectName() {
			return objectName;
		}

		public void setObjectName(String objectName) {
			this.objectName = objectName;
		}

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

		public String getAliasName() {
			return aliasName;
		}

		public void setAliasName(String aliasName) {
			this.aliasName = aliasName;
		}

		public String getFieldType() {
			return fieldType;
		}

		public void setFieldType(String fieldType) {
			this.fieldType = fieldType;
		}

		public boolean isHasJBCXMNS() {
			return hasJBCXMNS;
		}

		public void setHasJBCXMNS(boolean hasJBCXMNS) {
			this.hasJBCXMNS = hasJBCXMNS;
		}

		public String getSummerizedBy() {
			return summerizedBy;
		}

		public void setSummerizedBy(String summerizedBy) {
			this.summerizedBy = summerizedBy;
		}

		public boolean isGroupable() {
			return groupable;
		}

		public void setGroupable(boolean groupable) {
			this.groupable = groupable;
		}

		public boolean isStacked() {
			return stacked;
		}

		public void setStacked(boolean stacked) {
			this.stacked = stacked;
		}

		public String getAggType() {
			return aggType;
		}

		public void setAggType(String aggType) {
			this.aggType = aggType;
		}

		public String getGraphType() {
			return graphType;
		}

		public void setGraphType(String graphType) {
			this.graphType = graphType;
		}

		public int getDecimalPlaces() {
			return decimalPlaces;
		}

		public void setDecimalPlaces(int decimalPlaces) {
			this.decimalPlaces = decimalPlaces;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class MeasuresSFDC {
		private String objectName;
		private String name;
		private String label;
		private String aliasName;
		private String fieldType;
		private boolean hasJBCXMNS;
		private String summerizedBy;
		@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
		private boolean groupable;
		private boolean stacked;
		@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
		private String agg_func;
		@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
		private String graphType;
		@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
		private int decimalPlaces = 0;

		public String getObjectName() {
			return objectName;
		}

		public void setObjectName(String objectName) {
			this.objectName = objectName;
		}

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

		public String getAliasName() {
			return aliasName;
		}

		public void setAliasName(String aliasName) {
			this.aliasName = aliasName;
		}

		public String getFieldType() {
			return fieldType;
		}

		public void setFieldType(String fieldType) {
			this.fieldType = fieldType;
		}

		public boolean isHasJBCXMNS() {
			return hasJBCXMNS;
		}

		public void setHasJBCXMNS(boolean hasJBCXMNS) {
			this.hasJBCXMNS = hasJBCXMNS;
		}

		public String getSummerizedBy() {
			return summerizedBy;
		}

		public void setSummerizedBy(String summerizedBy) {
			this.summerizedBy = summerizedBy;
		}

		public boolean isGroupable() {
			return groupable;
		}

		public void setGroupable(boolean groupable) {
			this.groupable = groupable;
		}

		public boolean isStacked() {
			return stacked;
		}

		public void setStacked(boolean stacked) {
			this.stacked = stacked;
		}

		public String getAgg_func() {
			return agg_func;
		}

		public void setAgg_func(String agg_func) {
			this.agg_func = agg_func;
		}

		public String getGraphType() {
			return graphType;
		}

		public void setGraphType(String graphType) {
			this.graphType = graphType;
		}

		public int getDecimalPlaces() {
			return decimalPlaces;
		}

		public void setDecimalPlaces(int decimalPlaces) {
			this.decimalPlaces = decimalPlaces;
		}
	}

}