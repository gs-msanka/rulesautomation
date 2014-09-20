package com.gainsight.bigdata.pojo;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class DimensionBrowserInfo {

	@JsonProperty("DimensionBrowserReadLimit")
	int dimBrowReadLimit = 1000;

	@JsonProperty("columns")
	List<String> columns;

	public int getDimBrowReadLimit() {
		return dimBrowReadLimit;
	}

	public void setDimBrowReadLimit(int dimBrowReadLimit) {
		this.dimBrowReadLimit = dimBrowReadLimit;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumn(String column) {
		if (columns == null) {
			columns = new ArrayList<>();
		}
		this.columns.add(column);
	}

}
