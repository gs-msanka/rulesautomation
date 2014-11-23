package com.gainsight.bigdata.connectors.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.gainsight.bigdata.connectors.enums.ConnConstants;
import com.gainsight.bigdata.connectors.enums.ConnConstants.AggType;
import com.gainsight.bigdata.connectors.enums.Field;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ReportInfo {
	@JsonProperty("SchemaName")
	String collectionName;
	@JsonProperty("CollectionID")
	String collectionID;
	@JsonProperty("limit")
	int limit = 50;
	@JsonProperty("Type")
	String type = "adhoc";
	@JsonProperty("Dimensions")
	List<Map<String, Object>> dimensions = new ArrayList<Map<String, Object>>();
	@JsonProperty("ReportReadLimit")
	int reportReadLimit = 50;
	@JsonProperty("DimensionBrowserReadLimit")
	int dimensionBrowserReadLimit = 50;
	@JsonProperty("ReportId")
	int reportId = 0;
	@JsonProperty("reportName")
	String reportName = "";

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getCollectionID() {
		return collectionID;
	}

	public void setCollectionID(String collectionID) {
		this.collectionID = collectionID;
	}

	public void setDimension(Field field) {
		Map<String, Object> dimension = new HashMap<String, Object>();
		dimension.put("col", field.getDBName());
		if (!field.equals(Field.SYS_EVENTCOUNT)) {
			dimension.put("axis", "row");
			dimension.put("type", "0");
		} else {
			dimension.put("axis", "measure");
			dimension.put("type", new Integer(1));
			dimension.put("agg_func", "sum");
		}
		dimensions.add(dimension);
	}

	public void setMeasures(int noOfMeasures) {
		for (int i = 1; i <= noOfMeasures; i++) {
			Map<String, Object> dimension = new HashMap<String, Object>();
			dimension.put("col", "gsmeasure" + i);
			dimension.put("axis", "measure");
			dimension.put("type", 1);
			dimension.put("agg_func", AggType.SUM.getAggType());
			dimensions.add(dimension);
		}
	}

	public void setFlippedMeasures(int noOfFlippedMeasures) {
		for (int i = 1; i <= noOfFlippedMeasures; i++) {
			Map<String, Object> dimension = new HashMap<String, Object>();
			dimension.put("col", "gsm" + i);
			dimension.put("axis", "measure");
			dimension.put("type", 1);
			dimension.put("agg_func", AggType.SUM.getAggType());
			dimensions.add(dimension);
		}
	}

	public void setCustomFields(int noOfCustomFields) {
		for (int i = 1; i <= noOfCustomFields; i++) {
			Map<String, Object> dimension = new HashMap<String, Object>();
			dimension.put("col", "gscustom" + i);
			dimension.put("axis", "row");
			dimension.put("type", 0);
			dimensions.add(dimension);
		}
	}
}
