package com.gainsight.bigdata.connectors;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import com.gainsight.bigdata.connectors.enums.Field;
import com.gainsight.bigdata.connectors.enums.SfdcIdentifier;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataAPITestData {
	RequestInfo requestInfo;
	String collectionId = "6c26cc3e-b75a-4cae-86e0-402ec0830121";
	String timeZone = "UTC";
	String accountType = "DATA_API";
	String displayName = "Automation" + System.currentTimeMillis();

	public DataAPITestData() {
	}

	public RequestInfo getMappingWithAccNDateIdentifiers() {
		requestInfo = new RequestInfo();
		GlobalMapping globalMapping = requestInfo.globalMapping;
		globalMapping.setCommonIdentifiers(Field.SIO_ACCOUNTID, null, null, Field.SIO_TIMESTAMP,
				SfdcIdentifier.AM_ACCOUNTID, null);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, null, null);
		globalMapping.addMeasure(Field.SIO_MEA1, "AVG");
		requestInfo.addScheduler();
		requestInfo.setUsageConfig();
		requestInfo.setProperties(collectionId, timeZone);
		requestInfo.setAccountType(accountType);
		requestInfo.setDisplayName(displayName);
		return requestInfo;		
	}
}
