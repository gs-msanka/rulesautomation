package com.gainsight.sfdc.adoption.pages;


public class AdoptionUsagePage extends AdoptionBasePage {
	private final String READY_INDICATOR = "//div[@id='Adoption-Usage']";
	private final String PIN_ICON = "//img[@id='pinIcon']";
	private final String MONTH_SELECT = "//div[@class='JbaraMonthlyFilter hideForOldAdoption']/select";
	private final String YEAR_SELECT = "//div[@class='JbaraMonthlyFilter changeMyFloat']/select";
	private final String MEASURE_SELECT = "//select[@class='jbaraDummyAdoptionMeasureSelectControl min-width']"; //"//div[@class='newFilters']/div[2]/select";
	private final String GO_BUTTON = "//div[@class='newFilters']/div[2]/input[@value='View Results']";
	
	String month = "";
	String year = "";
	String measure = "";
	
	public AdoptionUsagePage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	/**
	 * Month value Format is like Apr for April, Jan for January
	 * @param month
	 */
	public void setMonth(String month) {
		this.month = month;
	}

	/**
	 * Year value format 2013, 2012
	 * @param year
	 */
	public void setYear(String year) {
		this.year = year;
	}

	public void setMeasure(String measure) {
		this.measure = measure;
	}

	public AdoptionBasePage displayUsageData() {
		button.click(PIN_ICON);
		field.selectFromDropDown(MONTH_SELECT, month);
		field.selectFromDropDown(YEAR_SELECT, year);
		field.setSelectField(MEASURE_SELECT, measure);
		button.click(GO_BUTTON);
		
		return this;
	}

}
