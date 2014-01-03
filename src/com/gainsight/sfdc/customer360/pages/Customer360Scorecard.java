package com.gainsight.sfdc.customer360.pages;

import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class Customer360Scorecard extends Customer360Page {

	private final String READY_INDICATOR = "//div[@class='gs_section_title']/h1[contains(.,'Scorecard')]";

	private final String OVERALL_SCORE = "//div[@class='score-area']/ul/li[@class='score']";
	private final String OVERALL_TREND = "//div[@class='score-area']/ul/li[@class='score-trend trend-%s']";// %s can	be up,down or flat

	private final String OVERALL_SUMMARY = "//div[@class='discription']";
	private final String EDIT_OVERALL_SUMMARY = "//div[@class='discription']/following-sibling::span";
	private final String SAVE_OVERALL_SUMMARY = "//div[@class='discription-cont clearfix']/div[@class='save-options clearfix']/a[@data-action='SAVE']";

	private final String CUSTOMER_GOALS = "//div[@class='goals']";
	private final String EDIT_CUSTOMER_GOALS = "//div[@class='goalslist_editble']/span";
	private final String SAVE_CUSTOMER_GOALS = "//div[@class='goalslist_editble']/div[@class='save-options clearfix']/a[@data-action='SAVE']";
	private final String HIDE_OR_SHOW_CUSTOMER_GOALS = "//div[@class='gs-head-tgl-btn goals-arrow-up']";// or//div[@class='gs-head-tgl-btn goals-arrow-down']

	private final String GROUP_LABEL = "//div[@class='matrix-heading']/h2[contains(.,'%s')]";

	// private final String MEASURE_CARD="";
	private final String MEASURE_TITLE = "//div[@class='floatleft heading' and @title='%s']"; // %s is measure name
	private final String MEASURE_TREND = "//div[@title='%s']/following-sibling::div[@class='floatleft trend trend-%s']"; // 2nd %s can be up,down or flat																			
	private final String MEASURE_SCORE = "//div[@title='%s']/parent::div/descendant::div/div[@title='Click to edit']";
	private final String MEASURE_SCORE_SLIDER_CIRCLE = "//*[local-name() = 'svg' and namespace-uri()='http://www.w3.org/2000/svg']/*[local-name()='circle']";
	private final String MEASURE_SCORE_SAVE = "//div[contains(text(), '%s')]/parent::div/div[@class='sliderH' and contains(@id, 'gs')]/descendant::a[@data-action='SAVE']";
	private final String MEASURE_COMMENTS = "//div[@title='%s']/parent::div/following-sibling::div[@class='editable comment-container']/div/div[@class='text-edit-area']";
	private final String MEASURE_COMMENTS_EDIT = "//div[@title='%s']/parent::div/following-sibling::div[@class='editable comment-container edit_on']/div/div[@class='text-edit-area']"; // %s is measure
	private final String MEASURE_COMMENTS_SAVE = "//div[contains(.,'%s') and @class='card-holder']//div[@class='editable comment-container edit_on']//a[@data-action='SAVE']";
	private final String MEASURE_FOOTER_MSG = "//div[@title='%s']/parent::div/parent::div/div[@class='status']";

	public Customer360Scorecard() {
		wait.waitTillElementDisplayed(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

	public int getOverallScore() {
		return (Integer.parseInt(item.getText(OVERALL_SCORE)));
	}

	public boolean verifyOverallTrend(String Trend) {

		switch (Trend) {
		case "Up":
			return item.isElementPresent(String.format(OVERALL_TREND, "up"));
			
		case "Down":
			return item.isElementPresent(String.format(OVERALL_TREND, "down"));

		case "Same":
			return item.isElementPresent(String.format(OVERALL_TREND, "flat"));
		}
		return false;
	}

	public void addOrEditOverallSummary(String summary, boolean add) {
		item.click(EDIT_OVERALL_SUMMARY);
		if (add)
			item.clearAndSetText(OVERALL_SUMMARY, summary);
		else
			item.setText(OVERALL_SUMMARY, summary);
		item.click(SAVE_OVERALL_SUMMARY);
	}

	public String getOverallSummary() {
		return item.getText(OVERALL_SUMMARY);
	}

	public void addOrEditCustomerGoals(String goals, boolean add) {
		item.click(String.format(HIDE_OR_SHOW_CUSTOMER_GOALS, "up"));
		item.click(EDIT_CUSTOMER_GOALS);
		if (add)
			item.clearAndSetText(CUSTOMER_GOALS, goals);
		else
			item.setText(CUSTOMER_GOALS, goals);
		item.click(SAVE_CUSTOMER_GOALS);
	}

	public String getCustomerGoals() {
		return item.getText(CUSTOMER_GOALS);
	}

	public boolean isGroupHeaderPresent(String groupName) {
		wait.waitTillElementDisplayed(String.format(GROUP_LABEL, groupName),
				MIN_TIME, MAX_TIME);
		return true;
	}

	public boolean isMeasuerPresentUnderGroup(String groupName,
			String measureName) {
		wait.waitTillElementDisplayed(
				String.format(MEASURE_TITLE, measureName), MIN_TIME, MAX_TIME);
		return true;
	}

	public void addOrModifyMeasureScore(String measure, String score,
			String scheme, boolean add) {
		item.click(String.format(MEASURE_SCORE, measure));
		amtDateUtil.stalePause();
		driver.switchTo().activeElement();
		Actions builder = new Actions(driver);
		List<WebElement> svgObject = driver.findElements(By
				.xpath(MEASURE_SCORE_SLIDER_CIRCLE));
		for (WebElement svg : svgObject) {
			if (svg.isDisplayed())
			{
				builder.moveToElement(svg);
				builder.dragAndDropBy(svg,
						getOffsetForScore(score, scheme, add), 0).build()
						.perform();
			}
		}
		amtDateUtil.stalePause();
		item.click(String.format(MEASURE_SCORE_SAVE, measure));
		amtDateUtil.stalePause();
	}

	public enum Grades {
		A(1), B(2), C(3), D(4), E(5), F(6), G(7), H(8), I(9), J(10);

		private int value;

		private Grades(int val) {
			value = val;
		}

		public int getValue() {
			return value;
		}
	}

	private int getOffsetForScore(String score, String scheme, boolean add) {
		int sliderStart = 10;
		int sliderEnd = 243;
		List<WebElement> ele=driver.findElements(By.xpath(MEASURE_SCORE_SLIDER_CIRCLE));
		

		int numOfGrades = 6; // To decide if this is to be taken from some
								// configuration/excel
		int returnVal = 0;
		
		switch (scheme) {
		case "Numeric":{
				if (add) {
				returnVal =(int)Math.floor((((sliderEnd - sliderStart) * (Float
						.parseFloat(score) / 100))));
			} else {
				int sliderCurrentPos=0;
				for(WebElement e : ele){
					if(e.isDisplayed())
						sliderCurrentPos = (int)Float.parseFloat(e.getAttribute("cx")); // need to get current x pos of the slider
				}
				returnVal = (int) Math
						.floor(((((sliderEnd - sliderStart) * (Float
								.parseFloat(score) / 100))) +sliderStart - sliderCurrentPos));
				System.out.println("Initial Position:"+sliderCurrentPos+",,Offset:"+returnVal);
			}
			break;
		}
		case "Grade":
			if (add) {
				returnVal = Math
						.round(((((sliderEnd - sliderStart) / numOfGrades) * Grades
								.valueOf(score).getValue()) + sliderStart));
			} else {
				int sliderCurrentPos=0;
				for(WebElement e : ele){
					if(e.isDisplayed())
						sliderCurrentPos = (int)Float.parseFloat(e.getAttribute("cx")); // need to get current x pos of the slider
				}
				returnVal = Math
						.round((((((sliderEnd - sliderStart) / numOfGrades) * Grades
								.valueOf(score).getValue()) + sliderStart) - sliderCurrentPos));
				System.out.println("Initial Position:"+sliderCurrentPos+",,Offset:"+returnVal);
			}
			break;
		case "Color":
			if (add) {
				returnVal = 0;
			} else {
				returnVal = 0;
			}
			break;
		}
		return returnVal;
	}

	public void addOrEditCommentsForMeasure(String comments, String measure) {
		driver.findElement(By.xpath(String.format(MEASURE_COMMENTS, measure))).click();
		driver.findElement(By.xpath(String.format(MEASURE_COMMENTS_EDIT,measure))).sendKeys(comments);
		item.click(String.format(MEASURE_COMMENTS_SAVE, measure));
		amtDateUtil.stalePause();
	}

	public String verifyCommentForMeasure(String measure) {
		return item.getText(String.format(MEASURE_COMMENTS, measure));
	}

	public boolean verifyMeasureTrend(String Trend, String meas) {
		return (element.isElementPresent(String.format(MEASURE_TREND, meas)) ? true
				: false);
	}

	public boolean verifyFooterMsg(String measName) {
		wait.waitTillElementDisplayed(
				String.format(MEASURE_FOOTER_MSG, measName), MIN_TIME, MAX_TIME);
		return true;
	}

}
