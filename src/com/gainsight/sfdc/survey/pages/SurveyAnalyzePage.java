package com.gainsight.sfdc.survey.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.gainsight.pageobject.core.WebPage;
import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.survey.pojo.SurveyAnalyze;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;


/**
 * Created by gainsight on 05/12/14.
 */
public class SurveyAnalyzePage extends SurveyPage{
	
	private final String ANALYZE_TEXT                = "//h4[text()='Survey Response Statistics']";
	private final String BARGRAPH_XPATH              = "//*[name()='g' and @class='highcharts-series-group']/*[name()='g']/*[name()='rect' and @fill='%s']";
	private final String CHART_TOOLTIP               = "//*[name()='g' and @class='highcharts-tooltip']/*[name()='text']/*[name()='tspan' and @dx='0']";
	
	public SurveyAnalyzePage() {
  	wait.waitTillElementPresent(ANALYZE_TEXT, MIN_TIME, MAX_TIME);

	}

	public void mouseOnToAnalyzeCharts(SurveyAnalyze surveyAnalyzeDetails) {
		List<WebElement> allGraphs = element.getAllElement(String.format(
				BARGRAPH_XPATH, surveyAnalyzeDetails.getColorCode()));
		Log.info("Size is" + allGraphs.size());
		Actions action = new Actions(Application.getDriver());
		for (WebElement singleChart : allGraphs) {
			action.moveToElement(singleChart).build().perform();
		}
	}
    
	public int getAnalyzeCount() {
		By by = element.getElementBy(CHART_TOOLTIP);
		WebPage webpage = new WebPage();
		String temp = webpage.getWebElement(by).getText();
		int toolTipCount = Integer.parseInt(temp);
		Log.info("Count in Analyze bar is " + toolTipCount);
		return toolTipCount;
	}
}
