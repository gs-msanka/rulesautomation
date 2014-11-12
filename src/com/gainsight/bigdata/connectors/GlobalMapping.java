package com.gainsight.bigdata.connectors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

import com.gainsight.bigdata.connectors.enums.Field;
import com.gainsight.bigdata.connectors.enums.SfdcIdentifier;
import com.gainsight.bigdata.connectors.mapping.CommonMappingInfo;
import com.gainsight.bigdata.connectors.mapping.CustomMappingInfo;
import com.gainsight.bigdata.connectors.mapping.GSDefinedMappingInfo;
import com.gainsight.bigdata.connectors.mapping.MeasureMappingInfo;
import com.gainsight.bigdata.connectors.mapping.SysDefMappingInfo;
import com.gainsight.bigdata.connectors.pojo.Scheduler;
import com.gainsight.bigdata.connectors.pojo.UsageConfiguration;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalMapping {
	@JsonProperty("systemDefined")
	List<SysDefMappingInfo> systemDefined;
	@JsonProperty("gsDefined")
	List<GSDefinedMappingInfo> gsDefined;
	@JsonProperty("custom")
	List<CustomMappingInfo> custom;
	@JsonProperty("measures")
	List<MeasureMappingInfo> measures;
	@JsonProperty("eventMeasureMappings")
	List<SysDefMappingInfo> eventMeasureMappings;

	@JsonProperty("accountIdentifier")
	CommonMappingInfo accountIdentifier;
	@JsonProperty("userIdentifier")
	CommonMappingInfo userIdentifier;
	@JsonProperty("eventIdentifier")
	CommonMappingInfo eventIdentifier;
	@JsonProperty("instanceIdentifier")
	CommonMappingInfo instanceIdentifier;
	@JsonProperty("timestampIdentifier")
	CommonMappingInfo timestampIdentifier;
	@JsonProperty("writeToSFDC")
	boolean writeToSFDC = false;
	@JsonProperty("schedulerDetails")
	Scheduler scheduler;
	@JsonProperty("usageConfiguration")
	UsageConfiguration usageConfiguration;

	@JsonProperty("properties")
	Map<String, Object> properties;

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public GlobalMapping() {
		measures = new ArrayList<MeasureMappingInfo>();
		custom = new ArrayList<CustomMappingInfo>();
		gsDefined = new ArrayList<GSDefinedMappingInfo>();
		// Setting GSDefined variables
		gsDefined.add(new GSDefinedMappingInfo(Field.SFDC_ACCOUNTID));
	}

	public void setUsageConfig() {
		usageConfiguration = new UsageConfiguration();
	}

	public void addScheduler() {
		scheduler = new Scheduler();
		scheduler.schedule("RUN_NOW", "2014-10-16T00:00:00.000", "2014-10-21T00:00:00.000");
	}

	public void addMeasure(Field field, String aggType) {
		measures.add(new MeasureMappingInfo(field, aggType));
	}

	public void addCustomField(Field source, Field target) {
		custom.add(new CustomMappingInfo(source, target));
	}

	public void setDefaultSysDefIdentifiers() {
		setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SFDC_USEREMAIL, Field.SFDC_USERNAME);
	}

	public void setSysDefIdetifiers(Field accountName, Field userEmail, Field userName) {
		systemDefined = new ArrayList<SysDefMappingInfo>();
		SysDefMappingInfo fieldMappingInfo = new SysDefMappingInfo(accountName, Field.SYS_ACCOUNTNAME);
		systemDefined.add(fieldMappingInfo);
		fieldMappingInfo = new SysDefMappingInfo(userEmail, Field.SYS_USEREMAIL);
		systemDefined.add(fieldMappingInfo);
		fieldMappingInfo = new SysDefMappingInfo(userEmail, Field.SYS_USERNAME);
		systemDefined.add(fieldMappingInfo);
	}

	public void setDefaultCommonIdentifiers() {
		setCommonIdentifiers(Field.SIO_ACCOUNTID, Field.SIO_USERID, Field.SIO_EVENT, Field.SIO_TIMESTAMP,
				SfdcIdentifier.AM_ACCOUNTID, SfdcIdentifier.UM_USERID);
	}

	public void setCommonIdentifiers(Field sioAcc, Field sioUser, Field sioEvent, Field sioTimestamp,
			SfdcIdentifier sfdcAcc, SfdcIdentifier sfdcUser) {
		CommonMappingInfo fieldMappingInfo = new CommonMappingInfo(sioAcc, Field.SYS_ACCOUNTID, sfdcAcc);
		accountIdentifier = fieldMappingInfo;
		fieldMappingInfo = new CommonMappingInfo(sioUser, Field.SYS_USERID, sfdcUser);
		// If user Lookup is optional
		if (sfdcUser == null) {
			fieldMappingInfo.setLookup(false);
		}
		userIdentifier = fieldMappingInfo;
		fieldMappingInfo = new CommonMappingInfo(sioEvent, Field.SYS_EVENT, null);
		eventIdentifier = fieldMappingInfo;
		fieldMappingInfo = new CommonMappingInfo(sioTimestamp, Field.SYS_TIMESTAMP, null);
		timestampIdentifier = fieldMappingInfo;
	}

	/*
	 * public static void main(String[] args) { GlobalMapping identifiers = new
	 * GlobalMapping(); identifiers.setDefaultCommonIdentifiers();
	 * identifiers.setDefaultSysDefIdentifiers();
	 * identifiers.addCustomField(Field.SIO_EVENT, Field.SYS_CUSTOM1);
	 * identifiers.addCustomField(Field.SIO_USERID, Field.SYS_CUSTOM2);
	 * identifiers.addMeasure(Field.SIO_EVENT, "COUNT");
	 * //identifiers.setGSDefinedIdentifier(); identifiers.addScheduler();
	 * identifiers.setUsageConfig(); ObjectMapper mapper = new ObjectMapper();
	 * try { System.out.println("Result Json::" +
	 * mapper.writeValueAsString(identifiers)); } catch (Exception e) {
	 * e.printStackTrace(); } }
	 */

	public static void main(String[] args) {
		GlobalMapping mapping = new GlobalMapping();
		mapping.setDefaultConfig();
		mapping.setCommonIdentifiers(Field.SIO_ACCOUNTID, Field.SIO_USERID, Field.SIO_EVENT, Field.SIO_TIMESTAMP,
				SfdcIdentifier.AM_ACCOUNTID, SfdcIdentifier.UM_USERID);
		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println("Result Json::" + mapper.writeValueAsString(mapping));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setDefaultConfig() {
		this.setCommonIdentifiers(Field.SIO_ACCOUNTID, Field.SIO_USERID, Field.SIO_EVENT, Field.SIO_TIMESTAMP,
				SfdcIdentifier.AM_ACCOUNTID, SfdcIdentifier.UM_USERID);
		this.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SFDC_USEREMAIL, Field.SFDC_USERNAME);
		this.addCustomField(Field.SIO_EVENT, Field.SYS_CUSTOM1);
		this.addCustomField(Field.SIO_USERID, Field.SYS_CUSTOM2);
		this.addMeasure(Field.SIO_EVENT, "COUNT");
		this.addScheduler();
		this.setUsageConfig();

	}
}
