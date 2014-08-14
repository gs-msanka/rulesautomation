package com.gainsight.sfdc.customer360.pages;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.administration.pages.AdminScorecardSection;
import com.gainsight.sfdc.tests.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.Arrays;
import java.util.List;

public class Customer360Scorecard extends Customer360Page  {

    private String scheme               = "Grade";
    private String[] grades_array       = {"F","E","D","C","B","A"};
    private String[] colors_array       = {"#790400","#c46d6c","#d5bf50","#4daddd","#97d477","#4a841e"};

    private final String READY_INDICATOR            = "//div[@class='gs_section_title']/h1[contains(.,'Scorecard')]";

	private final String OVERALL_SCORE              = "//div[contains(@class,'overallscore')]/descendant::li[@class='score']";
    private final String OVERALL_SCORE_BG_ELEMENT   = "//div[contains(@class, 'overallscore')]/div[@class='score-area']";
    private final String OVERALL_TREND              = "//div[contains(@class, 'overallscore')]/descendant::li[contains(@class, 'score-trend')]";
	private final String OVERALL_SUMMARY            = "//div[@class='discription']";
    private final String EDIT_OVERALL_SUMMARY       = "//div[@class='discription' and @contenteditable='true']";
	private final String SAVE_OVERALL_SUMMARY       = "//div[@class='discription' and @contenteditable='true']/parent::div/descendant::a[@data-action='SAVE']";
    private final String GOALS_EXPAND_ICON          = "//div[@class='goalsheader clearfix']/div[@class='gs-head-tgl-btn goals-arrow-down']";
    private final String GOALS_COLLAPSE_ICON        = "//div[@class='goalsheader clearfix']/div[@class='gs-head-tgl-btn goals-arrow-up']";
    private final String GOALS_VIEW                 = "//div[@class='goalslist_content' and @contenteditable='false']";
    private final String GOALS_INPUT                =  "//div[@class='goalslist_content' and @contenteditable='true']";
    private final String GOALS_SAVE                 = "//div[@class='goalslist_editble']/descendant::a[@data-action='SAVE']";
    private final String GOALS_CANCEL               = "//div[@class='goalslist_editble']/descendant::a[@data-action='CANCEL']";
    private final String GROUP_DIV                  = "//div[@class='scorecardsbody']/div[@class='matrix-heading']/h2[contains(text(), '%s')]";
	private final String SCORECARD_MEASURE_CARD     = "//h2[contains(text(), '%s')]/parent::div[@class='matrix-heading']" +
                                                        "/following-sibling::div[1]/descendant::div[@class='floatleft heading' and contains(text(), '%s')]" +
                                                        "/ancestor::div[@class='card-holder']";

	private final String MEASURE_TREND              = "//div[@title='%s']/following-sibling::div[@class='floatleft trend trend-%s']"; // 2nd %s can be up,down or flat
	private final String MEASURE_SCORE              = "//div[@title='%s']/parent::div/descendant::div/div[@title='Click to edit']";
	private final String MEASURE_SCORE_SAVE         = "//div[contains(text(), '%s')]/parent::div/div[@class='sliderH' and contains(@id, 'gs')]/descendant::a[@data-action='SAVE']";
	private final String MEASURE_COMMENTS           = "//div[@title='%s']/parent::div/following-sibling::div[@class='editable comment-container']/div/div[@class='text-edit-area']";
	private final String MEASURE_COMMENTS_EDIT      = "//div[@title='%s']/parent::div/following-sibling::div[@class='editable comment-container edit_on']/div/div[@class='text-edit-area']"; // %s is measure
	private final String MEASURE_COMMENTS_SAVE      = "//div[contains(.,'%s') and @class='card-holder']//div[@class='editable comment-container edit_on']//a[@data-action='SAVE']";
	private final String MEASURE_FOOTER_MSG         = "//div[@title='%s']/parent::div/parent::div/div[@class='status']";
    private final String SCORECARD_COMPACT_VIEW     = "//div[@class='gs-views-main']/a[@data-tabname='DETAIL-COMPACT']";
    private final String SCORECARD_DETAIL_VIEW      = "//div[@class='gs-views-main']/a[@data-tabname='DETAIL-FULL']";
    private final String CUSTOMER_GOALS_HEADER      = "div.goalsheader.clearfix";
    private final String MEASURE_SCORE_SLIDER_CIRCLE = "//*[local-name() = 'svg' and namespace-uri()='http://www.w3.org/2000/svg']/*[local-name()='circle']";


    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public void setGrades_array(String[] grades_array) {
        this.grades_array = grades_array;
    }

    public void setColors_array(String[] colors_array) {
        this.colors_array = colors_array;
    }

    public Customer360Scorecard() {
		wait.waitTillElementDisplayed(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

    public Customer360Scorecard openDetailView() {
        item.click(SCORECARD_DETAIL_VIEW);
        amtDateUtil.stalePause();
        return this;
    }

    public Customer360Scorecard openCompactView() {
        item.click(SCORECARD_COMPACT_VIEW);
        amtDateUtil.stalePause();
        return this;
    }

	private String getOverallScore() {
        wait.waitTillElementDisplayed(OVERALL_SCORE, MIN_TIME, MAX_TIME);
		return (item.getText(OVERALL_SCORE));
	}

    public Boolean verifyOverallScore(String score) {
        String actScore = getOverallScore();
        Report.logInfo("Actual Score : " +actScore);
        if(actScore.trim().equalsIgnoreCase(score)) {
            return true;
        }
        return false;
    }

    private String getOverAllScoreColour() {
        wait.waitTillElementDisplayed(OVERALL_SCORE_BG_ELEMENT,MIN_TIME, MAX_TIME);
        WebElement ele = element.getElement(OVERALL_SCORE_BG_ELEMENT);
        String style = ele.getAttribute("style");
        Report.logInfo("Style Value : "+style);
        return style.substring("background-color:".length());
    }
	
	public boolean verifyOverallScoreColor(String score_color){
		String actualColor = getOverAllScoreColour();
        Boolean result = actualColor.contains(score_color);
        return result;
	}
	public boolean verifyOverallScoreTrend(String Trend) {
        if(Trend == null) {
            WebElement ele = element.getElement(OVERALL_TREND);
            String actualTrend = ele.getAttribute("class");
            Report.logInfo("Actual Class Name : " +actualTrend);
            if(actualTrend == null || actualTrend=="") {
                return false;
            }
            if(Trend.equalsIgnoreCase("None")) {
                return actualTrend.contains("trend-none");
            } else if(Trend.equalsIgnoreCase("Up")) {
                return actualTrend.contains("trend-up");
            } else if(Trend.equalsIgnoreCase("Down")) {
                return actualTrend.contains("trend-down");
            } else if(Trend.equalsIgnoreCase("Flat")) {
                return actualTrend.contains("trend-flat");
            }
        }
        return false;
	}

	public Customer360Scorecard updateCustomerSummary(String scorecardComments) {
		driver.findElement(By.xpath(OVERALL_SUMMARY)).click();
        wait.waitTillElementDisplayed(SAVE_OVERALL_SUMMARY, MIN_TIME, MAX_TIME);
		driver.findElement(By.xpath(EDIT_OVERALL_SUMMARY)).sendKeys(scorecardComments);
		item.click(SAVE_OVERALL_SUMMARY);
		waitForLoadingImagesNotPresent();
        return this;
	}

	private String getOverallSummary() {
        String result = item.getText(OVERALL_SUMMARY);
        Report.logInfo("Actual Summary : " +result);
		return result;
	}

    public Boolean verifyOverAllSummary(String expResult) {
        String actResult = getOverallSummary();
        Report.logInfo("Expected Summary : "+expResult);
        return actResult.trim().toLowerCase().contains(expResult.trim().toLowerCase());
    }

    public Customer360Scorecard expandCustomerGoalsSec() {
        Actions builder = new Actions(driver);
        WebElement ele = driver.findElement(By.xpath("//div[@class='goals-section clearfix']/div[contains(@class, 'goalsheader')]"));
        builder.moveToElement(ele).build().perform();
        amtDateUtil.sleep(5);
        builder.click().build().perform();
        amtDateUtil.stalePause();
        return this;
    }

	private String getCustomerGoals() {
        expandCustomerGoalsSec();
		String customerGoals = item.getText(GOALS_VIEW);
        Report.logInfo("Actual Customer Goals : " +customerGoals);
        return  customerGoals;
	}

    public Boolean verifyCustomerGoals(String expGoals) {
        String actGoals = getCustomerGoals();
        Report.logInfo("Expected Customer Goals : " +expGoals);
        return actGoals.trim().toLowerCase().contains(expGoals.trim().toLowerCase());
    }

    public Customer360Scorecard updateCustomerGoals(String goals) {
        expandCustomerGoalsSec();
        Actions builder = new Actions(driver);
        WebElement ele = driver.findElement(By.xpath("//div[@class='goalslist editable-parent']/descendant::div[@class='goalslist_content']"));
        builder.moveToElement(ele).build().perform();
        builder.click().build().perform();
        amtDateUtil.stalePause();
        ele = driver.findElement(By.xpath("//div[@class='goalslist editable-parent']/descendant::div[@class='goalslist_content']"));
        ele.sendKeys(goals);
        amtDateUtil.sleep(5);
        new Actions(driver).sendKeys(ele).perform();
        amtDateUtil.sleep(5);
        item.click(GOALS_SAVE);
        waitForLoadingImagesNotPresent();
        return this;
    }

    public boolean isGroupDisplayed(String groupName) {
        return isElementPresentAndDisplay(By.xpath(String.format(GROUP_DIV, groupName)));
    }

    public boolean isMeasureDisplayed(String groupName, String measureName) {
        return isElementPresentAndDisplay(By.xpath(getMeasureXpath(groupName, measureName)));
    }

    private String getMeasureXpath(String groupName, String measureName) {
        String xPath =  String.format(SCORECARD_MEASURE_CARD, groupName, measureName);
        Report.logInfo("Scorecard Measure XPath : " +xPath);
        return xPath;
    }

    public Boolean verifyMeasureScore(String groupName, String measureName, String expScore) {
        String measure = getMeasureXpath(groupName, measureName);
        measure += "/descendant::div[@class='score']";
        Report.logInfo("Xpath : " +measure);
        String actScore = item.getText(measure);
        Report.logInfo("Measure Score : " +actScore);
        if(actScore != null && actScore != "") {
            if(actScore.trim().equalsIgnoreCase(expScore)) {
                return true;
            }
        }
        return false;
    }

    public boolean verifyMeasureTrend(String groupName, String measureName, String trend) {
        if(trend  != null) {
            String measure = getMeasureXpath(groupName, measureName);
            measure += "/descendant::div[contains(@class, 'trend')]";
            Report.logInfo("Xpath : " +measure);
            String actTrend = element.getElement(measure).getAttribute("class");
            Report.logInfo("Measure Trend : " +actTrend);
            String temp = trend.equalsIgnoreCase("Up") ? "trend-up" : trend.equalsIgnoreCase("down") ? "trend-down" : "trend-none";
            if(actTrend != null && actTrend != "") {
                if(actTrend.contains(temp)) {
                    return true;
                }
            }
        }
        return false;
    }


    public Customer360Scorecard removeMeasureScore(String groupName, String measureName, String score) {
        String xPath = getMeasureXpath(groupName, measureName);

        xPath += "/descendant::div[@class='gs-remove-score']/a";

        return this;
    }

    public Customer360Scorecard updateMeasureScore(String groupName, String measureName, String score, boolean add) {
        String xpath = getMeasureXpath(groupName, measureName);
        xpath += "/descendant::div[@class='grade-score']";
		item.click(xpath);
		amtDateUtil.stalePause();
		driver.switchTo().activeElement();
		Actions builder = new Actions(driver);
		List<WebElement> svgObject = driver.findElements(By
				.xpath(MEASURE_SCORE_SLIDER_CIRCLE));
		for (WebElement svg : svgObject) {
			if (svg.isDisplayed())
			{
				builder.moveToElement(svg);
				builder.dragAndDropBy(svg, getOffsetForScore(score, add), 0)
                        .build().perform();
			}
		}
		amtDateUtil.stalePause();
		item.click(String.format(MEASURE_SCORE_SAVE, measureName));
		amtDateUtil.stalePause();
        waitForLoadingImagesNotPresent();
        return this;
	}
	
	private int getOffsetForScore(String score, boolean add) {
        int returnVal       = 0;
        int sliderStart     = 10;
		int sliderEnd       = 243;
        int numOfColors     = colors_array.length;
        int numOfGrades     = grades_array.length;

		List<WebElement> ele=driver.findElements(By.xpath(MEASURE_SCORE_SLIDER_CIRCLE));
		if(scheme.equals("Score")) {
			if (add) {
				returnVal =(int)Math.floor(((sliderEnd - sliderStart) * (Float.parseFloat(score) / 100)));
			} else {
				int sliderCurrentPos=0;
				for(WebElement e : ele){
					if(e.isDisplayed()) {
                        sliderCurrentPos = (int)Float.parseFloat(e.getAttribute("cx")); // need to get current x pos of the slider
                        break;
                    }
				}
				returnVal = (int) Math.floor(((((sliderEnd - sliderStart) * (Float.parseFloat(score) / 100))) + sliderStart - sliderCurrentPos));
				Report.logInfo("Initial Position:"+sliderCurrentPos+", Offset : "+returnVal);
			}
		}
		else if(scheme.equals("Grade")){
			int pos=Arrays.asList(grades_array).indexOf(score);
			if (add) {
				returnVal = (int)Math.floor(((((sliderEnd - sliderStart) / (numOfGrades-1)) * pos) ));
			} else {
				int sliderCurrentPos=0;
				for(WebElement e : ele){
					if(e.isDisplayed()) {
                        sliderCurrentPos = (int)Float.parseFloat(e.getAttribute("cx")); // need to get current x pos of the slider
                        break;
                    }
				}
				returnVal = (int)Math.floor((((((sliderEnd - sliderStart) / (numOfGrades - 1)) * pos) + sliderStart) - sliderCurrentPos));
				Report.logInfo("Initial Position:"+sliderCurrentPos+",,Offset:"+returnVal);
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
					if(e.isDisplayed())sliderCurrentPos = (int)Float.parseFloat(e.getAttribute("cx")); // need to get current x pos of the slider
                    break;
				}
				returnVal = (int)Math.floor((((((sliderEnd - sliderStart) / (numOfColors - 1)) * c_pos) + sliderStart) - sliderCurrentPos));
				Report.logInfo("Initial Position:"+sliderCurrentPos+",,Offset:"+returnVal);
			}
		}
        Report.logInfo("Offset to Move : " +returnVal);
		return returnVal;
	}

	public Customer360Scorecard updateMeasureComments(String groupName, String measureName, String comments) {
        openDetailView();
        amtDateUtil.sleep(5);
        String xPath = getMeasureXpath(groupName, measureName);
        List<WebElement> eleList = element.getAllElement(xPath);
        Report.logInfo(String.valueOf(eleList.size()));
        for(WebElement ele : eleList) {
            if(ele.isDisplayed()) {
                ele.findElement(By.cssSelector("div.text-edit-area")).click();
                amtDateUtil.stalePause();
                ele.findElement(By.cssSelector("div.text-edit-area")).sendKeys(comments);
            }
        }

        String save = xPath+"/descendant::div[@class='save-mark']";
        eleList = element.getAllElement(save);
        Report.logInfo(String.valueOf(eleList.size()));
        for(WebElement ele :eleList) {
            Report.logInfo(String.valueOf(ele.isDisplayed()));
            if(ele.isDisplayed()) {
                ele.click();
                break;
            }
        }
        waitForLoadingImagesNotPresent();
        return this;
    }

	private String getCommentsOfMeasure(String groupName, String measureName) {
        String xPath = getMeasureXpath(groupName, measureName);
        String  viewableTextArea = xPath+"/descendant::div[@class='text-edit-area']";
        String comments = item.getText(viewableTextArea);
        Report.logInfo("Actual Measure (Name -) '"+measureName+"' comments : " +comments);
		return comments;
	}

    public Boolean verifyCommentsOfMeasure(String groupName, String measureName, String expComments) {
        String actComments = getCommentsOfMeasure(groupName, measureName);
        Report.logInfo("Expected Comments : " +actComments);
        return actComments.trim().toLowerCase().contains(expComments.trim().toLowerCase());
    }

    public boolean verifyFooterMsg(String measName) {
        wait.waitTillElementDisplayed(
                String.format(MEASURE_FOOTER_MSG, measName), MIN_TIME, MAX_TIME);
        return true;
    }


    public AdminScorecardSection changeToScheme(String schemeName,AdminScorecardSection as) throws InterruptedException{

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
        return new AdminScorecardSection();
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

}
