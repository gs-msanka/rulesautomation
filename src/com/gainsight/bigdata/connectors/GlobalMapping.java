package com.gainsight.bigdata.connectors;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import com.gainsight.bigdata.connectors.enums.*;
import com.gainsight.bigdata.connectors.mapping.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalMapping {
	@JsonProperty("accountIdentifier")
	CommonMappingInfo accountIdentifier;
	@JsonProperty("userIdentifier")
	CommonMappingInfo userIdentifier;
	@JsonProperty("timestampIdentifier")
	CommonMappingInfo timestampIdentifier;
	@JsonProperty("eventIdentifier")
	CommonMappingInfo eventIdentifier;
	@JsonProperty("custom")
	List<CustomMappingInfo> custom;
	@JsonProperty("measures")
	List<MeasureMappingInfo> measures;
	@JsonProperty("systemDefined")
	List<SysDefMappingInfo> systemDefined;
	@JsonProperty("eventMeasureMappings")
	List<SysDefMappingInfo> eventMeasureMappings;
	@JsonProperty("instanceIdentifier")
	CommonMappingInfo instanceIdentifier;
	@JsonProperty("gsDefined")
	List<GSDefinedMappingInfo> gsDefined;

	public GlobalMapping() {
		try {
			userIdentifier = new CommonMappingInfo();
			systemDefined = new ArrayList<SysDefMappingInfo>();
			measures = new ArrayList<MeasureMappingInfo>();
			custom = new ArrayList<CustomMappingInfo>();
			gsDefined = new ArrayList<GSDefinedMappingInfo>();
			// Setting GSDefined variables
			gsDefined.add(new GSDefinedMappingInfo(Field.SFDC_ACCOUNTID));
		} catch (Exception ex) {
			System.out.println(ex);
		}
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
		SysDefMappingInfo fieldMappingInfo = new SysDefMappingInfo(accountName, Field.SYS_ACCOUNTNAME);
		systemDefined.add(fieldMappingInfo);
		if (userEmail != null) {
			fieldMappingInfo = new SysDefMappingInfo(userEmail, Field.SYS_USEREMAIL);
			systemDefined.add(fieldMappingInfo);
		}
		if (userName != null) {
			fieldMappingInfo = new SysDefMappingInfo(userName, Field.SYS_USERNAME);
			systemDefined.add(fieldMappingInfo);
		}
	}

	public void setDefaultCommonIdentifiers() {
		setCommonIdentifiers(Field.SIO_ACCOUNTID, Field.SIO_USERID, Field.SIO_EVENT, Field.SIO_TIMESTAMP,
				SfdcIdentifier.AM_ACCOUNTID, SfdcIdentifier.UM_USERID);
	}

	public void setCommonIdentifiers(Field sioAcc, Field sioUser, Field sioEvent, Field sioTimestamp,
			SfdcIdentifier sfdcAcc, SfdcIdentifier sfdcUser) {
		CommonMappingInfo fieldMappingInfo = new CommonMappingInfo(sioAcc, Field.SYS_ACCOUNTID, sfdcAcc);
		accountIdentifier = fieldMappingInfo;
		if (sfdcUser != null) {
			fieldMappingInfo = new CommonMappingInfo(sioUser, Field.SYS_USERID, sfdcUser);
			// If user Lookup is optional
			/*
			 * if (sfdcUser == null) { fieldMappingInfo.setLookup(false); }
			 */
			userIdentifier = fieldMappingInfo;
		}
		if (sioEvent != null) {
			fieldMappingInfo = new CommonMappingInfo(sioEvent, Field.SYS_EVENT, null);
			eventIdentifier = fieldMappingInfo;
		}
		fieldMappingInfo = new CommonMappingInfo(sioTimestamp, Field.SYS_TIMESTAMP, null);
		timestampIdentifier = fieldMappingInfo;
	}

	public void setDefaultConfig() {
		this.setCommonIdentifiers(Field.SIO_ACCOUNTID, Field.SIO_USERID, Field.SIO_EVENT, Field.SIO_TIMESTAMP,
				SfdcIdentifier.AM_ACCOUNTID, SfdcIdentifier.UM_USERID);
		this.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SFDC_USEREMAIL, Field.SFDC_USERNAME);
		this.addCustomField(Field.SIO_EVENT, Field.SYS_CUSTOM1);
		this.addCustomField(Field.SIO_USERID, Field.SYS_CUSTOM2);
		this.addMeasure(Field.SIO_MEA1, "SUM");
		/*
		 * identifiers.setUsageConfig(); ObjectMapper mapper = new
		 * ObjectMapper(); try { System.out.println("Result Json::" +
		 * mapper.writeValueAsString(identifiers)); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */

	}
}
