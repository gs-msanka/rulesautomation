package com.gainsight.sfdc.administration.pages;

import java.util.HashMap;
import java.util.Map.Entry;

import com.gainsight.sfdc.pages.BasePage;

public class OpportunityConnectorPage extends BasePage {
	private final String FIELD_SELECT = "//td[text()='%s']/following-sibling::td/select";
	private final String RULES_FIELD_SELECT="//td[text()='%s']/following-sibling::td//select";
	private final String FIELD_MAP_ROW = "//td[text()='%s']/following-sibling::td[text()='%s']";
	private final String HEADER_FIELD_MAP = "//div[contains(@id,'JBaraHeaderFieldIdBlock')]//input[@value='Map']";
	private final String LINE_FIELD_MAP = "//div[contains(@id,'JBaraHeaderFieldIdBlock')]//input[@value='Map']";
	private final String CONNECTOR_SETTINGS_MAP="//div[contains(@id,'OpportunityConnectorSettingsIdBlock')]//input[@value='Map']";
	private final String RULES_MAP="//div[contains(@id,'bookingTypesIdBlock')]//input[@value='Map']";
	private final String SAVE_BUTTON = "//input[@value='Save']";
	private final String CONNECTOR_SETTINGS_DATE="//input[contains(@id,'jbaraDummyOpportunityDateInput')]";
	private final String CONNECTOR_SETTINGS_FIELD_SELECT="//select[@class='jbaraOppFieldsList jbaraOppFieldsUnSelectedList']";
	private final String ADD_IMG="//img[@title='Add']";
	//private final String REMOVE_IMG="//IMG[@title='Remove']";
	public OpportunityConnectorPage() {
		wait.waitTillElementPresent(HEADER_FIELD_MAP, MIN_ELEMENT_WAIT,
				MAX_ELEMENT_WAIT);
	}

	public OpportunityConnectorPage mapHeaderFields(
			HashMap<String, String> fields) {
		mapFields(HEADER_FIELD_MAP,FIELD_SELECT,fields);
		return this;
	}

	public OpportunityConnectorPage mapLineItems(HashMap<String, String> fields) {
		mapFields(LINE_FIELD_MAP,FIELD_SELECT,fields);
		return this;
	}

	public OpportunityConnectorPage addConnectorSettings(String date,String fieldsCSV) {
		item.click(CONNECTOR_SETTINGS_MAP);
		wait.waitTillElementPresent(SAVE_BUTTON, MIN_ELEMENT_WAIT,
				MAX_ELEMENT_WAIT);
		amtDateUtil.enterDate(CONNECTOR_SETTINGS_DATE,date);
		String[] fields=fieldsCSV.split(",");
		for(String cField: fields){
			field.setSelectField(CONNECTOR_SETTINGS_FIELD_SELECT, cField);			
			item.click(ADD_IMG);			
		}
		item.click(SAVE_BUTTON);
		return this;
	}

	public OpportunityConnectorPage mapBookingRules(HashMap<String, String> fields) {
		mapFields(RULES_MAP,RULES_FIELD_SELECT,fields);
		return this;
	}

	public boolean isThisMapPresent(String header, String value) {
		return item.isElementPresent(String.format(FIELD_MAP_ROW,
				header, value));
	}

	public boolean isThisMapPresent(HashMap<String, String> fields) {
		boolean flag = false;
		for (Entry<String, String> entry : fields.entrySet()) {
			if (isThisMapPresent(entry.getKey(), entry.getValue())) {
				flag = true;
			} else {
				flag = false;
				break;
			}
		}
		return flag;
	}
	private void mapFields(String mapType,String mapSelector,HashMap<String, String> fields){
		item.click(mapType);
		wait.waitTillElementPresent(SAVE_BUTTON, MIN_ELEMENT_WAIT,
				MAX_ELEMENT_WAIT);
		setFieldValues(mapSelector, fields);
		item.click(SAVE_BUTTON);
		wait.waitTillElementPresent(HEADER_FIELD_MAP, MIN_ELEMENT_WAIT,
				MAX_ELEMENT_WAIT);		
	}
	
	private void setFieldValues(String selector, HashMap<String, String> fields) {
		for (Entry<String, String> entry : fields.entrySet()) {
			field.setSelectField(
					String.format(FIELD_SELECT, entry.getKey()),
					entry.getValue());
		}
	}

}
