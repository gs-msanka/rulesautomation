package com.gainsight.bigdata.connectors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.gainsight.bigdata.connectors.enums.Field;
import com.gainsight.bigdata.connectors.enums.SfdcIdentifier;

public class DataAPITestData {

	String collectionId = "3231f98f-05a3-483f-a256-5aafc5b64328";
	String timeZone = "UTC";
	String accountType = "DATA_API";
	GlobalMapping mapping;
	String displayName;
	ObjectMapper mapper = new ObjectMapper();

	public GlobalMapping getMappingWithAccNDateIdentifiers() {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("collectionId", collectionId);
		properties.put("timeZone", timeZone);
		mapping = new GlobalMapping();
		mapping.setCommonIdentifiers(Field.SIO_ACCOUNTID, null, null, Field.SIO_TIMESTAMP,
				SfdcIdentifier.AM_ACCOUNTID, null);
		mapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, null, null);
		mapping.addMeasure(Field.SIO_EVENT, "COUNT");
		mapping.addScheduler();
		mapping.setUsageConfig();
		mapping.setProperties(properties);
		try {
			System.out.println("Result Json::" + mapper.writeValueAsString(mapping));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping;
	}

	public static void main(String[] args) {
		new DataAPITestData().getMappingWithAccNDateIdentifiers();
	}
}
