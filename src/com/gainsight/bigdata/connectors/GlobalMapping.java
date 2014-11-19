package com.gainsight.bigdata.connectors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.gainsight.bigdata.connectors.enums.*;
import com.gainsight.bigdata.connectors.mapping.*;
import com.gainsight.bigdata.connectors.pojo.EventMeasureMapping;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GlobalMapping {
	@JsonProperty("accountIdentifier")
	Map<String, Object> accountIdentifier;
	@JsonProperty("userIdentifier")
	Map<String, Object> userIdentifier;
	@JsonProperty("timestampIdentifier")
	Map<String, Object> timestampIdentifier;
	@JsonProperty("eventIdentifier")
	Map<String, Object> eventIdentifier;
	@JsonProperty("custom")
	List<Map<String, Object>> custom;
	@JsonProperty("measures")
	List<Map<String, Object>> measures;
	@JsonProperty("systemDefined")
	List<SysDefFieldInfo> systemDefined;
	@JsonProperty("eventMeasureMappings")
	List<Map<String, String>> eventMeasureMappings;
	@JsonProperty("instanceIdentifier")
	Map<String, Object> instanceIdentifier;
	@JsonProperty("gsDefined")
	List<Map<String, Object>> gsDefined;

	public GlobalMapping() {
		try {
			systemDefined = new ArrayList<SysDefFieldInfo>();
			measures = new ArrayList<Map<String, Object>>();
			custom = new ArrayList<Map<String, Object>>();
			gsDefined = new ArrayList<Map<String, Object>>();
			eventMeasureMappings = new ArrayList<Map<String, String>>();
			// Setting GSDefined variables
			gsDefined.add(new MappingInfo().getGSIdentifier(Field.SFDC_ACCOUNTID));
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	public void addMeasure(Field field, String aggFunc) {
		measures.add(new MappingInfo().getMeasure(field, aggFunc));
	}

	public void addCustomField(Field source, Field target) {
		custom.add(new MappingInfo().getCustomIdentifier(source, target));
	}

	public void addEventMeasureMapping(ConnConstants.Events event, Field field, ConnConstants.AggType aggType) {
		eventMeasureMappings.add(new EventMeasureMapping(event, field, aggType).getEventMeasureMapping());
	}

	public void setDefaultSysDefIdentifiers() {
		setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SFDC_USEREMAIL, Field.SFDC_USERNAME);
	}

	public void setSysDefIdetifiers(Field accountName, Field userEmail, Field userName) {
		SysDefFieldInfo fieldMappingInfo = new SysDefFieldInfo(accountName, Field.SYS_ACCOUNTNAME);
		systemDefined.add(fieldMappingInfo);
		if (userEmail != null) {
			fieldMappingInfo = new SysDefFieldInfo(userEmail, Field.SYS_USEREMAIL);
			systemDefined.add(fieldMappingInfo);
		}
		if (userName != null) {
			fieldMappingInfo = new SysDefFieldInfo(userName, Field.SYS_USERNAME);
			systemDefined.add(fieldMappingInfo);
		}
	}

	public void setDefaultCommonIdentifiers() {
		setCommonIdentifiers(Field.SIO_ACCOUNTID, Field.SIO_USERID, Field.SIO_EVENT, Field.SIO_TIMESTAMP,
				SfdcIdentifier.AM_ACCOUNTID, SfdcIdentifier.UM_USERID);
	}

	public void setCommonIdentifiers(Field sioAcc, Field sioUser, Field sioEvent, Field sioTimestamp,
			SfdcIdentifier sfdcAcc, SfdcIdentifier sfdcUser) {
		Map<String, Object> fieldMappingInfo = new MappingInfo().getAccountIdentifier(sioAcc, Field.SYS_ACCOUNTID,
				sfdcAcc);
		accountIdentifier = fieldMappingInfo;
		fieldMappingInfo = new MappingInfo().getUserIdentifier(sioUser, Field.SYS_USERID, sfdcUser);
		userIdentifier = fieldMappingInfo;
		if (sioEvent != null) {
			fieldMappingInfo = new MappingInfo().getEventIdentifier(sioEvent, Field.SYS_EVENT, null);
			eventIdentifier = fieldMappingInfo;
		}
		fieldMappingInfo = new MappingInfo().getTimestampIdentifier(sioTimestamp, Field.SYS_TIMESTAMP, null);
		timestampIdentifier = fieldMappingInfo;
	}

	public void setDefaultConfig() {
		this.setCommonIdentifiers(Field.SIO_ACCOUNTID, Field.SIO_USERID, Field.SIO_EVENT, Field.SIO_TIMESTAMP,
				SfdcIdentifier.AM_ACCOUNTID, SfdcIdentifier.UM_USERID);
		this.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SFDC_USEREMAIL, Field.SFDC_USERNAME);
		this.addCustomField(Field.SIO_EVENT, Field.SYS_CUSTOM1);
		this.addCustomField(Field.SIO_USERID, Field.SYS_CUSTOM2);
		this.addMeasure(Field.SIO_MEA1, "SUM");

	}
}
