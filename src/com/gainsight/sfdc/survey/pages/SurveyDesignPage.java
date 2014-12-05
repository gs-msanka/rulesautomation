package com.gainsight.sfdc.survey.pages;

import com.gainsight.pageobject.core.Report;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.HashMap;
import java.util.List;

public class SurveyDesignPage extends SurveyBasePage {
	
	private final String READY_INDICATOR  = "";

	public SurveyDesignPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}





	
}
