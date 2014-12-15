package com.gainsight.sfdc.survey.pages;

import com.gainsight.pageobject.core.Report;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.HashMap;
import java.util.List;

public class SurveyDesignPage extends SurveyBasePage {
	
	private final String DESIGN_PAGE  = "//div[@class='survey-sub-menu survey-menu']";

	public SurveyDesignPage() {
		wait.waitTillElementPresent(DESIGN_PAGE, MIN_TIME, MAX_TIME);
	}





	
}
