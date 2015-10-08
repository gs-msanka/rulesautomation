package com.gainsight.bigdata.rulesengine.pojo.setuprule;

import com.gainsight.bigdata.rulesengine.pojo.enums.CalculationType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * Created by vmenon on 9/23/2015.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CalculatedField {
	private String fieldName;

	@JsonDeserialize(using = CalculationType.CalculationTypeDeserializer.class)
	private CalculationType calculationType;
	private Aggregation aggregationConfig;
	private String calculateDifferenceType;
	private CalculationConfiguration fieldAConfig;
	private CalculationConfiguration fieldBConfig;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public CalculationType getCalculationType() {
		return calculationType;
	}

	public void setCalculationType(CalculationType calculationType) {
		this.calculationType = calculationType;
	}

	public Aggregation getAggregationConfig() {
		return aggregationConfig;
	}

	public void setAggregationConfig(Aggregation aggregationConfig) {
		this.aggregationConfig = aggregationConfig;
	}

	public String getCalculateDifferenceType() {
		return calculateDifferenceType;
	}

	public void setCalculateDifferenceType(String calculateDifferenceType) {
		this.calculateDifferenceType = calculateDifferenceType;
	}

	public CalculationConfiguration getFieldAConfig() {
		return fieldAConfig;
	}

	public void setFieldAConfig(CalculationConfiguration fieldAConfig) {
		this.fieldAConfig = fieldAConfig;
	}

	public CalculationConfiguration getFieldBConfig() {
		return fieldBConfig;
	}

	public void setFieldBConfig(CalculationConfiguration fieldBConfig) {
		this.fieldBConfig = fieldBConfig;
	}

	@JsonIgnoreProperties(ignoreUnknown=true)
	public static class CalculationConfiguration {
		@JsonDeserialize(using = CalculationType.CalculationTypeDeserializer.class)
		private CalculationType calculatedFieldType;

		private String showField;

		private Aggregation aggregation;

		public CalculationType getCalculatedFieldType() {
			return calculatedFieldType;
		}

		public void setCalculatedFieldType(CalculationType calculatedFieldType) {
			this.calculatedFieldType = calculatedFieldType;
		}

		public String getShowField() {
			return showField;
		}

		public void setShowField(String showField) {
			this.showField = showField;
		}

		public Aggregation getAggregation() {
			return aggregation;
		}

		public void setAggregation(Aggregation aggregation) {
			this.aggregation = aggregation;
		}
	}

	public static class Aggregation {
		private String aggregationCalculation;
		private String sourceField;
		private String periodType;
		private String noOfPeriods;
		private String granularity;
		private boolean adjustForMissingData;

		public String getAggregationCalculation() {
			return aggregationCalculation;
		}

		public void setAggregationCalculation(String aggregationCalculation) {
			this.aggregationCalculation = aggregationCalculation;
		}

		public String getSourceField() {
			return sourceField;
		}

		public void setSourceField(String sourceField) {
			this.sourceField = sourceField;
		}

		public String getPeriodType() {
			return periodType;
		}

		public void setPeriodType(String periodType) {
			this.periodType = periodType;
		}

		public String getNoOfPeriods() {
			return noOfPeriods;
		}

		public void setNoOfPeriods(String noOfPeriods) {
			this.noOfPeriods = noOfPeriods;
		}

		public String getGranularity() {
			return granularity;
		}

		public void setGranularity(String granularity) {
			this.granularity = granularity;
		}

		public boolean isAdjustForMissingData() {
			return adjustForMissingData;
		}

		public void setAdjustForMissingData(boolean adjustForMissingData) {
			this.adjustForMissingData = adjustForMissingData;
		}
	}
}
