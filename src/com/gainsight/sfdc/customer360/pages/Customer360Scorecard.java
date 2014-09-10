package com.gainsight.sfdc.customer360.pages;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.administration.pages.AdminScorecardSection;
import com.gainsight.sfdc.tests.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.*;

public class Customer360Scorecard extends Customer360Page  {

    private String scheme               = "Grade";
    private String[] grades_array       = {"F","E","D","C","B","A"};
    private String[] colors_array       = {"#c53536","#fd9b9c","#eba638", "#32a5c7", "#76ad27"};
    private static HashMap<String, String> gradeColorMap;
    private static HashMap<String, String> scoreColorMap;
    static {
        gradeColorMap = new HashMap<String, String>();
        gradeColorMap.put("A", "#76ad27");
        gradeColorMap.put("B", "#97cb4c");
        gradeColorMap.put("C", "#eba638");
        gradeColorMap.put("D", "#fbc064");
        gradeColorMap.put("E", "#f17273");
        gradeColorMap.put("F", "#c53536");

        scoreColorMap = new HashMap<String, String>();
        scoreColorMap.put("0-50" , "#c53536");
        scoreColorMap.put("50-75" , "#eba638");
        scoreColorMap.put("75-100" , "#76ad27");
    }
    private final String READY_INDICATOR            = "//div[@class='gs_section_title']/h1[contains(.,'Scorecard')]";
	private final String OVERALL_SCORE              = "//div[contains(@class,'overallscore')]/descendant::li[@class='score']";
    private final String OVERALL_SCORE_BG_ELEMENT   = "//div[contains(@class, 'overallscore clearfix')]/div[@class='score-area']";
    private final String OVERALL_TREND              = "//div[contains(@class, 'overallscore clearfix')]/descendant::li[contains(@class, 'score-trend')]";
	private final String OVERALL_SUMMARY            = "//div[@class='discription']";
    private final String EDIT_OVERALL_SUMMARY       = "//div[@class='discription' and @contenteditable='true']";
	private final String SAVE_OVERALL_SUMMARY       = "//div[@class='discription' and @contenteditable='true']/parent::div/descendant::a[@data-action='SAVE']";
    private final String SAVE_OVERALL_SCORE         = "//div[@class='score-area']/descendant::a[@data-action='SAVE']";
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
    private final String REMOVE_SCORE_DIALOG        = "//div[contains(@class, 'ui-dialog ui-widget') and @role='dialog']";
    private final String REMOVE_OVERALL_SCORE       = "//div[@class='score-area']/descendant::div[@class='gs-remove-score']/a";
    private final String REMOVE_SCORE_DIALOG_YES    = REMOVE_SCORE_DIALOG +"/descendant::input[@data-action='Yes']";
    private final String REMOVE_SCORE_DIALOG_NO     = REMOVE_SCORE_DIALOG+"/descendant::input[@data-action='Yes']";

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public Customer360Scorecard() {
		wait.waitTillElementDisplayed(READY_INDICATOR, MIN_TIME, MAX_TIME);
        waitForLoadingImagesNotPresent();
	}

    public Customer360Scorecard openDetailView() {
        item.click(SCORECARD_DETAIL_VIEW);
        amtDateUtil.stalePause();
        return this;
    }

    public Customer360Scorecard removeOverAllCustomerScore() {
        item.click(OVERALL_SCORE_BG_ELEMENT);
        amtDateUtil.stalePause();
        item.click(REMOVE_OVERALL_SCORE);
        wait.waitTillElementDisplayed(REMOVE_SCORE_DIALOG, MIN_TIME, MAX_TIME);
        item.click(REMOVE_SCORE_DIALOG_YES);
        waitForLoadingImagesNotPresent();
        return this;
    }

    public Customer360Scorecard updateOverAllScore(String score, Boolean add) {
        List<WebElement> elements = element.getAllElement(OVERALL_SCORE_BG_ELEMENT);
        Report.logInfo(String.valueOf(elements.size()));
        for(WebElement ele : elements) {
            ele.isDisplayed();
        }
        item.click(OVERALL_SCORE_BG_ELEMENT);
        amtDateUtil.stalePause();
        driver.switchTo().activeElement();
        Actions builder = new Actions(driver);
        List<WebElement> svgObject = driver.findElements(By
                .xpath(MEASURE_SCORE_SLIDER_CIRCLE));
        for (WebElement svg : svgObject) {
            if (svg.isDisplayed()) {
                builder.moveToElement(svg);
                builder.dragAndDropBy(svg, getOffsetForScore(score, add)+ ((add) ? 1 :0) , 0)
                        .build().perform();
            }
        }
        amtDateUtil.stalePause();
        item.click(SAVE_OVERALL_SCORE);
        waitForLoadingImagesNotPresent();
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
        Report.logInfo("Expected Score : " +score);
        if(actScore.trim().equalsIgnoreCase(score)) {
            return true;
        }
        return false;
    }

    private String getNumberColor(String score) {
        int a = Integer.valueOf(score);
        Iterator it = scoreColorMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String key = pair.getKey().toString();
            int highBoundary = Integer.valueOf(key.substring(key.indexOf("-") + 1));
            int lowBoundary = Integer.valueOf(key.substring(0,key.indexOf("-")));
            if((lowBoundary <a && a <=highBoundary )|| (a == 0 && a <=highBoundary && a==lowBoundary)) {
                return pair.getValue().toString();
            }
        }
        return null;
    }

	public Boolean verifyOverallScoreColor(String score){
        String expColor  = score; //Default to color scheme.
        if(scheme.equals("Grade")) {
            expColor = gradeColorMap.get(expColor);
        } else if(scheme.equals("Score")) {
            expColor = getNumberColor(expColor);
        }
        WebElement ele = element.getElement(OVERALL_SCORE_BG_ELEMENT);
        String actualColor = ele.getAttribute("style");
        Report.logInfo("Actual OverAll Score Color :" +actualColor);
        Report.logInfo("Expected OverAll Score Color :" +expColor);
        return isColorExists(actualColor, expColor);
	}

    public Boolean verifyMeasureColor(String groupName, String measureName, String score) {
        WebElement ele = element.getElement(getMeasureXpath(groupName, measureName)+"/descendant::div[@class='grade-score']");
        String actualColor = ele.getAttribute("style");
        Report.logInfo("Actual OverAll Score Color :" +actualColor);
        String expColor  = score; //Default to color scheme.
        if(scheme.equals("Grade")) {
            expColor = gradeColorMap.get(expColor);
        } else if(scheme.equals("Score")) {
            expColor = getNumberColor(expColor);
        }
        Report.logInfo("Expected OverAll Score Color :" +expColor);
        return isColorExists(actualColor, expColor);
    }

    private Boolean isColorExists(String actualColor, String expColor) {
        if(actualColor.contains("#")) {
            Report.logInfo("Style has hexadecimal value");
            if(actualColor.contains(expColor)) {
                return true;
            }
        } else if(actualColor.contains("rgb")){
            Report.logInfo("Style has RGB value");
            String[] rgb = actualColor.substring(actualColor.indexOf("(")+1, actualColor.lastIndexOf(")")).split(",");
            String hexadecimal = rgbToHex(Integer.valueOf(rgb[0].trim()),Integer.valueOf(rgb[1].trim()),Integer.valueOf(rgb[2].trim()));
            Report.logInfo("Expected color Hexa : " +hexadecimal);
            if(hexadecimal.equalsIgnoreCase(expColor)) {
                return true;
            }
        }
        return false;
    }

    private String rgbToHex(int r, int g, int b) {
        return "#" + toBrowserHexValue(r) + toBrowserHexValue(g) + toBrowserHexValue(b);
    }

    private String toBrowserHexValue(int number) {
        StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
        while (builder.length() < 2) {
            builder.append("0");
        }
        return builder.toString().toUpperCase();
    }

	public boolean verifyOverallScoreTrend(String Trend) {
        try {
            if(Trend != null) {
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
        }  catch (Exception e) {
            return false;
        }

        return false;
	}

	public Customer360Scorecard updateCustomerSummary(String scorecardComments) {
        new Actions(driver).moveToElement(driver.findElement(By.xpath(OVERALL_SUMMARY))).build().perform();
        new Actions(driver).doubleClick(driver.findElement(By.xpath(OVERALL_SUMMARY))).build().perform();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String a = "var a = document.getElementsByClassName('discription');\n" +
                "a[0].click();\n" +
                "a[0].innerHTML = '"+scorecardComments+"';";
        js.executeScript(a);
        new Actions(driver).moveToElement(driver.findElement(By.xpath(READY_INDICATOR))).build().perform();
		//item.click(SAVE_OVERALL_SUMMARY);
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
        Report.logInfo("Actual Summary : "+actResult);
        return actResult.trim().toLowerCase().contains(expResult.trim().toLowerCase());
    }

    /**
     * This never works due to java script issues in 360 Page.
     * @return
     */
    public Customer360Scorecard expandCustomerGoalsSec() {
        String script = "var a = document.getElementsByClassName('goalsheader');\n" +
                "a[0].click();" ;
        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript(script);

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
        String script = "var a = document.getElementsByClassName('goalslist_content');\n" +
                "a[0].click();\n" +
                "a[0].innerHTML = '"+goals+"';";
        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript(script);
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
        Report.logInfo("Actual Score : " +actScore);
        Report.logInfo("Expected Score : " +expScore);
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
            measure += "/descendant::div[@class='grade-score']/div[2]";
            Report.logInfo("Xpath : " +measure);
            List<WebElement> eleList = element.getAllElement(measure);
            WebElement el = null;
            Report.logInfo(String.valueOf(eleList.size()));
            for(WebElement ele : eleList) {
                if(ele.isDisplayed()) {
                    el = ele;
                    String actTrend = el.getAttribute("class");
                    Report.logInfo("Measure Trend : " +actTrend);
                    String temp = trend.equalsIgnoreCase("Up") ? "trend-up" : trend.equalsIgnoreCase("down") ? "trend-down" : "trend-none";
                    if(actTrend != null && actTrend != "") {
                        if(actTrend.contains(temp)) {
                            return true;
                        }
                    }
                }
                break;
            }
        }
        return false;
    }

    public Customer360Scorecard removeMeasureScore(String groupName, String measureName) {
        String measureXpath = getMeasureXpath(groupName, measureName);
        item.click(measureXpath+"/descendant::div[@class='grade-score']");
        wait.waitTillElementDisplayed(measureXpath+"/descendant::div[@class='slider-container clearfix']", MIN_TIME, MAX_TIME);
        item.click(measureXpath+"/descendant::div[@class='gs-remove-score']/a[text()='Remove Score']");
        wait.waitTillElementDisplayed(REMOVE_SCORE_DIALOG, MIN_TIME, MAX_TIME);
        item.click(REMOVE_SCORE_DIALOG_YES);
        waitForLoadingImagesNotPresent();
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
        String script = "var a = document.getElementsByClassName('floatleft');\n" +
                "a[0].click();\n" +
                "a[0].innerHTML;\n" +
                "a.length;\n" +
                "var text =\"\";\n" +
                "var i;\n" +
                "var scorecard;\n" +
                "for (i = 0; i < a.length; i++) {\n" +
                "    if((a[i].innerHTML === '"+measureName+"') ) {\n" +
                "        scorecard = a[i];\n" +
                "        break;\n" +
                "    }\n" +
                "}\n" +
                "scorecard.parentNode.parentNode.getElementsByClassName('text-edit-area')[0].click();\n" +
                "scorecard.parentNode.parentNode.getElementsByClassName('text-edit-area')[0].innerHTML='"+comments+"';";

        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript(script);
        new Actions(driver).moveToElement(driver.findElement(By.cssSelector(CUSTOMER_GOALS_HEADER))).build().perform();
        waitForLoadingImagesNotPresent();
        return this;
    }

	private String getCommentsOfMeasure(String groupName, String measureName) {
        String xPath = getMeasureXpath(groupName, measureName);
        String  viewableTextArea = xPath+"/descendant::div[@class='text-edit-area']";
        String comments = item.getText(viewableTextArea);
        Report.logInfo("Actual Measure '"+measureName+"' comments : " +comments);
		return comments;
	}

    public Boolean verifyCommentsOfMeasure(String groupName, String measureName, String expComments) {
        String actComments = getCommentsOfMeasure(groupName, measureName);
        Report.logInfo("Expected Comments : " +expComments);
        Report.logInfo("Actual Comments : " +actComments);
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
}
