package com.gainsight.bigdata.connectors.pojo;

import java.util.HashMap;
import java.util.Map;

import com.gainsight.bigdata.connectors.enums.ConnConstants;
import com.gainsight.bigdata.connectors.enums.Field;

public class EventMeasureMapping {

	Map<String, String> eventMeasureMapping;

	public EventMeasureMapping(ConnConstants.Events event, Field field, ConnConstants.AggType aggType) {
		eventMeasureMapping = new HashMap<String, String>();
		eventMeasureMapping.put("event", event.getEvent());
		eventMeasureMapping.put("aggregationFunction", aggType.getAggType());
		eventMeasureMapping.put("aggregationKey", field.getDBName());
		eventMeasureMapping.put("flippedMeasureDisplayName", field.getDisplayName() + " " + aggType.getAggType());
		eventMeasureMapping.put("flippedMeasureDbName", "");
	}

	public Map<String, String> getEventMeasureMapping() {
		return eventMeasureMapping;
	}

}