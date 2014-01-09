package com.gainsight.sfdc.customer360.pages;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.administration.pages.AdminScorecardSection;
import com.gainsight.sfdc.administration.pages.AdministrationBasepage;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.SOQLUtil;

public class Customer360Scorecard extends Customer360Page {
	
	
	private final String READY_INDICATOR = "//div[@class='gs_section_title']/h1[contains(.,'Scorecard')]";

	private final String OVERALL_SCORE = "//div[@class='score-area']/ul/li[@class='score']";
	private final String OVERALL_SCORE_BACKGROUND="//div[@style='background-color:%s;' and @class='score-area']";
	private final String OVERALL_TREND = "//div[@class='score-area']/ul/li[@class='score-trend trend-%s']";// %s can	be up,down or flat

	private final String OVERALL_SUMMARY = "//div[@class='discription']";
	private final String EDIT_OVERALL_SUMMARY = "//div[@class='discription' and @contenteditable='true']";
	private final String SAVE_OVERALL_SUMMARY = "//div[@class='discription' and @contenteditable='true']/parent::div/div[@class='save-options clearfix']/a[@data-action='SAVE']";

	private final String CUSTOMER_GOALS_HEADER="div.goalsheader.clearfix";
	private final String CUSTOMER_GOALS = "//div[@class='goalslist_content' and @title='Click to edit']";
	private final String EDIT_CUSTOMER_GOALS = "//div[@class='goalslist_content' and @contenteditable='true']";
	private final String SAVE_CUSTOMER_GOALS = "//div[@class='goalslist_content' and @contenteditable='true']/parent::div//a[@data-action='SAVE']";
	private final String HIDE_OR_SHOW_CUSTOMER_GOALS = "//div[@class='goalsheader clearfix']/div[@class='gs-head-tgl-btn goals-arrow-down']";// or//div[@class='gs-head-tgl-btn goals-arrow-down']

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

	public String getOverallScore() {
		amtDateUtil.stalePause();
		amtDateUtil.stalePause();
		return (item.getText(OVERALL_SCORE));
	}
	
	public boolean verifyOverallScoreForColor(String score_color){
		return (item.isElementPresent(String.format(OVERALL_SCORE_BACKGROUND,score_color)));
	}
	public boolean verifyOverallTrend(String Trend) {

		if(Trend.equals("Up")) {
			return item.isElementPresent(String.format(OVERALL_TREND, "up"));
		}
			
		else if(Trend.equals("Down")){
			return item.isElementPresent(String.format(OVERALL_TREND, "down"));
		}
		else{
			return item.isElementPresent(String.format(OVERALL_TREND, "flat"));
		}
	}

	public void addOrEditOverallSummary(String summary, boolean add) {
		driver.findElement(By.xpath(OVERALL_SUMMARY)).click();
		driver.findElement(By.xpath(EDIT_OVERALL_SUMMARY)).sendKeys(summary);
		item.click(SAVE_OVERALL_SUMMARY);
		amtDateUtil.stalePause();
	}

	public String getOverallSummary() {
		amtDateUtil.stalePause();
		return item.getText(OVERALL_SUMMARY);
	}

	public void addOrEditCustomerGoals(String goals, boolean add) {
		for(int i=0;i<3;i++){
			driver.findElement(By.cssSelector(CUSTOMER_GOALS_HEADER)).click();
			WebElement goals_ele=driver.findElement(By.xpath(CUSTOMER_GOALS));
			if(goals_ele.isDisplayed()){
				break;
			}
			else{
				driver.navigate().refresh();
				goToSection("Scorecard");
			}
		}
		
		amtDateUtil.stalePause();
		Actions builder=new Actions(driver);
		WebElement goals_edit=driver.findElement(By.xpath(CUSTOMER_GOALS));
		builder.moveToElement(goals_edit).click().sendKeys(goals).click(driver.findElement(By.xpath(SAVE_CUSTOMER_GOALS))).perform();
		amtDateUtil.stalePause();
		/*item.click(SAVE_CUSTOMER_GOALS);
		amtDateUtil.stalePause();*/
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
		int numOfColors=6;
		int returnVal = 0;
		String[] grades_array={"F","E","D","C","B","A"};
		String[] colors_array={"#790400","#c46d6c","#d5bf50","#4daddd","#97d477","#4a841e"};
		if(scheme.equals("Numeric")) {
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
		}
		else if(scheme.equals("Grade")){
			int pos=Arrays.asList(grades_array).indexOf(score);
			if (add) {
				
				returnVal = (int)Math
						.floor(((((sliderEnd - sliderStart) / (numOfGrades-1)) * pos) ));
			} else {
				int sliderCurrentPos=0;
				for(WebElement e : ele){
					if(e.isDisplayed())
						sliderCurrentPos = (int)Float.parseFloat(e.getAttribute("cx")); // need to get current x pos of the slider
				}
				returnVal = (int)Math
						.floor((((((sliderEnd - sliderStart) / (numOfGrades-1)) * pos) + sliderStart) - sliderCurrentPos));
				System.out.println("Initial Position:"+sliderCurrentPos+",,Offset:"+returnVal);
			}
		}
		else {   //scheme is "Color":
			int c_pos=Arrays.asList(colors_array).indexOf(score);
			if (add) {
				
				
				returnVal = (int)Math
						.floor(((((sliderEnd - sliderStart) / (numOfColors-1)) * c_pos) ));
			} else {
				int sliderCurrentPos=0;
				for(WebElement e : ele){
					if(e.isDisplayed())
						sliderCurrentPos = (int)Float.parseFloat(e.getAttribute("cx")); // need to get current x pos of the slider
				}
				returnVal = (int)Math
						.floor((((((sliderEnd - sliderStart) / (numOfGrades-1)) * c_pos) + sliderStart) - sliderCurrentPos));
				System.out.println("Initial Position:"+sliderCurrentPos+",,Offset:"+returnVal);
			}
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
		amtDateUtil.stalePause();;
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
	
	public void changeToScheme(String schemeName,AdminScorecardSection as) throws InterruptedException{
	
		if(schemeName.equals("Numeric")) {
				as.applyNumericScheme();
		}
		if(schemeName.equals("Grade")){
				as.applyGradeScheme();
		}
		if(schemeName.equals("Color")){
				as.applyColorScheme();
		}
	   	Report.logInfo("Job added... proceeding with polling");
    	BaseTest bt=new BaseTest();
    	int noOfRunningJobs =0;
	for(int l= 0; l < 100; l++) {
		String query = "SELECT Id, JobType, ApexClass.Name, Status FROM AsyncApexJob " +
                "WHERE JobType ='BatchApex' and Status IN ('Queued', 'Processing', 'Preparing') " +
                "and ApexClass.Name = 'BatchHandler'";
		
        noOfRunningJobs = bt.getQueryRecordCount(query);
        if(noOfRunningJobs==0) {
            Report.logInfo("Scorecard schem changed to Grading...proceeding with execution of tests.....");
            break;
        } else {
            Report.logInfo("Waiting for Scorecard scheme to be changed to Grading");
            Thread.sleep(3000L);
        }
    }
		clickOnC360Tab();
		searchCustomer("Scorecard Account", true);
		goToScorecardSection();
	}
}
