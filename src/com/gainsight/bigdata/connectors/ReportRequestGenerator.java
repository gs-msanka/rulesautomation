package com.gainsight.bigdata.connectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.gainsight.bigdata.connectors.enums.Field;
import com.gainsight.bigdata.connectors.pojo.ReportInfo;

public class ReportRequestGenerator {
	Map<String, List<ReportInfo>> report;
	ReportInfo reportInfo;
	ObjectMapper mapper = new ObjectMapper();

	public ReportRequestGenerator() {
		report = new HashMap<String, List<ReportInfo>>();
		reportInfo = new ReportInfo();
	}

	public void setCollectionName(String collectionName) {
		reportInfo.setCollectionName(collectionName);
	}

	public void setCollectionID(String collectionID) {
		reportInfo.setCollectionID(collectionID);
	}

	public void setMeasures(int noOfMeasures) {
		reportInfo.setMeasures(noOfMeasures);
	}

	public void setFlippedMeasures(int noOfFlippedMeasures) {
		reportInfo.setFlippedMeasures(noOfFlippedMeasures);
	}

	public void setCustomFields(int noOfCustomFields) {
		reportInfo.setCustomFields(noOfCustomFields);
	}

	public void setField(Field field) {
		reportInfo.setDimension(field);
	}

	public void setAccount() {
		reportInfo.setDimension(Field.SYS_ACCOUNTID);
		reportInfo.setDimension(Field.SYS_ACCOUNTNAME);
	}

	public void setUser() {
		reportInfo.setDimension(Field.SYS_USERID);
		reportInfo.setDimension(Field.SYS_USERNAME);
		reportInfo.setDimension(Field.SYS_USEREMAIL);
	}

	public void setEvent() {
		reportInfo.setDimension(Field.SYS_EVENT);
		reportInfo.setDimension(Field.SYS_EVENTCOUNT);
	}

	public void setAccDate() {
		setAccount();
		reportInfo.setDimension(Field.SYS_DATE);
	}

	public void setAccUserDate() {
		setAccount();
		setUser();
		reportInfo.setDimension(Field.SYS_DATE);
	}

	public void setAccEventDate() {
		setAccount();
		setEvent();
		reportInfo.setDimension(Field.SYS_DATE);
	}

	public void setAccUserEventDate() {
		setAccount();
		setUser();
		setEvent();
		reportInfo.setDimension(Field.SYS_DATE);
	}

	public String requestInfoAsString() throws Exception {
		List<ReportInfo> list = new ArrayList<ReportInfo>();
		list.add(reportInfo);
		report.put("ReportInfo", list);
		return mapper.writeValueAsString(report);
	}
}
