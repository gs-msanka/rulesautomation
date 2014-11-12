package com.gainsight.bigdata.connectors.pojo;

import org.codehaus.jackson.annotate.JsonProperty;

public class EventMeasureMapping {

	@JsonProperty("event")
	String event;
	@JsonProperty("aggregationFunction")
	String aggregationFunction;
	@JsonProperty("aggregationKey")
	String aggregationKey;
	@JsonProperty("flippedMeasureDisplayName")
	String flippedMeasureDisplayName;
	@JsonProperty("flippedMeasureDbName")
	String flippedMeasureDbName;

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getAggregationFunction() {
		return aggregationFunction;
	}

	public void setAggregationFunction(String aggregationFunction) {
		this.aggregationFunction = aggregationFunction;
	}

	public String getAggregationKey() {
		return aggregationKey;
	}

	public void setAggregationKey(String aggregationKey) {
		this.aggregationKey = aggregationKey;
	}

	public String getFlippedMeasureDisplayName() {
		return flippedMeasureDisplayName;
	}

	public void setFlippedMeasureDisplayName(String flippedMeasureDisplayName) {
		this.flippedMeasureDisplayName = flippedMeasureDisplayName;
	}

	public String getFlippedMeasureDbName() {
		return flippedMeasureDbName;
	}

	public void setFlippedMeasureDbName(String flippedMeasureDbName) {
		this.flippedMeasureDbName = flippedMeasureDbName;
	}

}
