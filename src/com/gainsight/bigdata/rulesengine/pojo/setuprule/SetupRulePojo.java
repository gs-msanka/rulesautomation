package com.gainsight.bigdata.rulesengine.pojo.setuprule;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by vmenon on 9/14/2015.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class SetupRulePojo {

	private String dataSource = "";
	private String selectObject = "";
	private List<SetupData> setupData = new ArrayList<>();
	private List<CalculatedField> calculatedFields = new ArrayList<>();
	private String advancedLogic = "";
	private String timeIdentifier = "";
	private String joinOnCollection = "";
	private String joinWithCollection = "";
	private String joinOnCollectionField;
	private boolean lookUpField=false;

	public String getSelectObject() {
		return selectObject;
	}

	public void setSelectObject(String selectObject) {
		this.selectObject = selectObject;
	}

	public List<SetupData> getSetupData() {
		return setupData;
	}

	public void setSetupData(List<SetupData> setupData) {
		this.setupData = setupData;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public List<CalculatedField> getCalculatedFields() {
		return calculatedFields;
	}

	public void setCalculatedFields(List<CalculatedField> calculatedFields) {
		this.calculatedFields = calculatedFields;
	}

	public String getAdvancedLogic() {
		return advancedLogic;
	}

	public void setAdvancedLogic(String advancedLogic) {
		this.advancedLogic = advancedLogic;
	}

	public String getTimeIdentifier() {
		return timeIdentifier;
	}

	public void setTimeIdentifier(String timeIdentifier) {
		this.timeIdentifier = timeIdentifier;
	}

	public String getJoinOnCollection() {
		return joinOnCollection;
	}

	public void setJoinOnCollection(String joinOnCollection) {
		this.joinOnCollection = joinOnCollection;
	}

	public String getJoinWithCollection() {
		return joinWithCollection;
	}

	public void setJoinWithCollection(String joinWithCollection) {
		this.joinWithCollection = joinWithCollection;
	}

	public String getJoinOnCollectionField() {
		return joinOnCollectionField;
	}

	public void setJoinOnCollectionField(String joinOnCollectionField) {
		this.joinOnCollectionField = joinOnCollectionField;
	}

	public boolean isLookUpField() {
		return lookUpField;
	}

	public void setLookUpField(boolean lookUpField) {
		this.lookUpField = lookUpField;
	}
}
