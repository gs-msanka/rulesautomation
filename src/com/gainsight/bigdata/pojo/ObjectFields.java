package com.gainsight.bigdata.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ObjectFields {
	
	//List of fields of all data types available in SFDC
	
	List<String>  textFields = new ArrayList<String>();	
	List<String>  autoNumber = new ArrayList<String>();
	List<String>  numberFields = new ArrayList<String>();
	List<String>  checkBoxes = new ArrayList<String>();
	List<String>  Currencies = new ArrayList<String>();
	List<String>  Emails = new ArrayList<String>();
	List<String>  Percents = new ArrayList<String>();
	List<String>  Phones = new ArrayList<String>();
	List<HashMap<String,String[]>> pickLists = new ArrayList<HashMap<String,String[]>>();
	List<HashMap<String,String[]>> multiPickLists = new ArrayList<HashMap<String,String[]>>();	
	List<String>  textAreas = new ArrayList<String>();
	List<String>  encryptedStrings = new ArrayList<String>();
	List<String>  URLs = new ArrayList<String>();
	List<HashMap<String, String>> lookups= new ArrayList<HashMap<String, String>>();
	List<String>  Dates = new ArrayList<String>();
	List<String>  dateTimes = new ArrayList<String>();
	List<String>  geoLocation = new ArrayList<String>();	
	List<String>  textArea_Long = new ArrayList<String>();
	List<String>  textArea_Rich = new ArrayList<String>();
	List<HashMap<String, String>> FormulaFieldsList= new ArrayList<HashMap<String, String>>();
	List<String>  externalID_Text = new ArrayList<String>();
	List<String>  externalID_TextArea = new ArrayList<String>();
	List<String>  externalID_TextAreaRich = new ArrayList<String>();
	
	
	public List<String> getExternalID_Text() {
		return externalID_Text;
	}
	public void setExternalID_Text(List<String> externalID_Text) {
		this.externalID_Text = externalID_Text;
	}	
	public List<HashMap<String, String[]>> getMultiPickLists() {
		return multiPickLists;
	}
	public void setMultiPickLists(List<HashMap<String, String[]>> multiPickLists) {
		this.multiPickLists = multiPickLists;
	}
	public List<HashMap<String, String[]>> getPickLists() {
		return pickLists;
	}
	public void setPickLists(List<HashMap<String, String[]>> pickLists) {
		this.pickLists = pickLists;
	}
	public List<HashMap<String, String>> getLookups() {
		return lookups;
	}
	public void setLookups(List<HashMap<String, String>> lookups) {
		this.lookups = lookups;
	}
	public List<HashMap<String, String>> getFormulaFieldsList() {
		return FormulaFieldsList;
	}
	public void setFormulaFieldsList(List<HashMap<String, String>> formulaFieldsList) {
		FormulaFieldsList = formulaFieldsList;
	}
	public void clearFormulaFieldsList() {
		if(FormulaFieldsList != null)
			FormulaFieldsList.clear();
	}
	public List<String> getTextArea_Long() {
		return textArea_Long;
	}
	public void setTextArea_Long(List<String> textArea_Long) {
		this.textArea_Long = textArea_Long;
	}
	public List<String> getTextArea_Rich() {
		return textArea_Rich;
	}
	public void setTextArea_Rich(List<String> textArea_Rich) {
		this.textArea_Rich = textArea_Rich;
	}	
	public List<String> getGeoLocation() {
		return geoLocation;
	}
	public void setGeoLocation(List<String> geoLocation) {
		this.geoLocation = geoLocation;
	}
	public List<String> getAutoNumber() {
		return autoNumber;
	}
	public void setAutoNumber(List<String> autoNumber) {
		this.autoNumber = autoNumber;
	}
	public List<String> getTextFields() {
		return textFields;
	}
	public void setTextFields(List<String> textFields) {
		this.textFields = textFields;
	}
	public List<String> getNumberFields() {
		return numberFields;
	}
	public void setNumberFields(List<String> numberFields) {
		this.numberFields = numberFields;
	}
	public List<String> getCheckBoxes() {
		return checkBoxes;
	}
	public void setCheckBoxes(List<String> checkBoxes) {
		this.checkBoxes = checkBoxes;
	}
	public List<String> getCurrencies() {
		return Currencies;
	}
	public void setCurrencies(List<String> currencies) {
		Currencies = currencies;
	}
	public List<String> getEmails() {
		return Emails;
	}
	public void setEmails(List<String> emails) {
		Emails = emails;
	}
	public List<String> getPercents() {
		return Percents;
	}
	public void setPercents(List<String> percents) {
		Percents = percents;
	}
	public List<String> getPhones() {
		return Phones;
	}
	public void setPhones(List<String> phones) {
		Phones = phones;
	}
	public List<String> getTextAreas() {
		return textAreas;
	}
	public void setTextAreas(List<String> textAreas) {
		this.textAreas = textAreas;
	}
	public List<String> getEncryptedStrings() {
		return encryptedStrings;
	}
	public void setEncryptedStrings(List<String> encryptedStrings) {
		this.encryptedStrings = encryptedStrings;
	}
	public List<String> getURLs() {
		return URLs;
	}
	public void setURLs(List<String> uRLs) {
		URLs = uRLs;
	}
	public List<String> getDates() {
		return Dates;
	}
	public void setDates(List<String> dates) {
		Dates = dates;
	}
	public List<String> getDateTimes() {
		return dateTimes;
	}
	public void setDateTimes(List<String> dateTimes) {
		this.dateTimes = dateTimes;
	}
	
	
	
}
