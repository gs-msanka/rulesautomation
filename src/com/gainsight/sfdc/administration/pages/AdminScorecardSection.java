package com.gainsight.sfdc.administration.pages;

import com.gainsight.pageobject.util.Timer;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;

import com.gainsight.sfdc.pages.BasePage;

import java.sql.Time;

public class AdminScorecardSection extends BasePage {

		private final String READY_INDICATOR1       = "//li[contains(.,'Enable Scorecard')]";
        private final String READY_INDICATOR2       = "//div[@class='overall-score-heading']";
        private final String GLOBAL_SETTINGS        = "//h1[text()='Global Settings']";
        private final String ENABLE_SCORECARD       = "//input[@id='enableScoreCard-Radio']";
        private final String CUST_ROLLUP            = "//input[@id='enableCustomerRollup-Radio']";
        private final String GRADING_SCHEME         = "//h1[contains(text(),'Grading Scheme')]";
        private final String SCORING_NUMERIC        = "//a[@id='numericGradingBtn']";
        private final String SCORING_GRADING_AtoF   = "//a[@id='atofGradingBtn']";
        private final String SCORING_COLO_RYG       = "//a[@id='colorGradingBtn']";
        private final String APPLY_NUMERIC          = "//a[@class='apply applyNumeric']";
        private final String APPLY_GRADING          = "//a[@class='apply applyGradeScoring']";
        private final String APPLY_COLOR            = "//a[@class='apply applyColor']";
        private final String APPLY_GLOBAL_SETTINGS  = "//a[@id='saveOrgSettings']";
		
		public AdminScorecardSection(){
			try {
                wait.waitTillElementDisplayed(READY_INDICATOR1, MIN_TIME, MAX_TIME);
			}
			catch(TimeoutException e){
                wait.waitTillElementDisplayed(READY_INDICATOR2, MIN_TIME, MAX_TIME);
			}
		}
		
		public void enableScorecard(){
			item.click(GLOBAL_SETTINGS);
			Timer.sleep(2);
			Timer.sleep(2);
			item.selectCheckBox(ENABLE_SCORECARD);
			item.selectCheckBox(CUST_ROLLUP);
			item.click(APPLY_GLOBAL_SETTINGS);
			waitTillNoLoadingIcon();			
		}
		
		public void applyNumericScheme(){
			item.click(GRADING_SCHEME);
			Timer.sleep(2);
			item.click(SCORING_NUMERIC);
			Timer.sleep(2);
			item.click(APPLY_NUMERIC);
			Timer.sleep(2);
			try{
				driver.switchTo().alert().accept();
				waitTillNoLoadingIcon();
			}
			catch(NoAlertPresentException ae){
				
			}
		}
		
		public void applyGradeScheme(){
			item.click(GRADING_SCHEME);
			Timer.sleep(2);
			item.click(SCORING_GRADING_AtoF);
			Timer.sleep(2);
			item.click(APPLY_GRADING);
			Timer.sleep(2);
			try{
				driver.switchTo().alert().accept();
				waitTillNoLoadingIcon();
			}
			catch(NoAlertPresentException ae){
				
			}
		}
		
		public void applyColorScheme(){
			item.click(GRADING_SCHEME);
			Timer.sleep(2);
			item.click(SCORING_COLO_RYG);
			item.click(APPLY_COLOR);
			Timer.sleep(2);
			try{
				driver.switchTo().alert().accept();
				waitTillNoLoadingIcon();
			}
			catch(NoAlertPresentException ae){
				
			}
		}
}		