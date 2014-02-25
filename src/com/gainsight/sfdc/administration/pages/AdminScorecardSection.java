package com.gainsight.sfdc.administration.pages;

import java.util.NoSuchElementException;

import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;

public class AdminScorecardSection extends AdministrationBasepage{

		final String READY_INDICATOR1="//li[contains(.,'Enable Scorecard')]";
		final String READY_INDICATOR2="//div[@class='overall-score-heading']";
		final String GLOBAL_SETTINGS="//h1[text()='Global Settings']";
		final String ENABLE_SCORECARD="//input[@id='enableScoreCard-Radio']";
		final String CUST_ROLLUP="//input[@id='enableCustomerRollup-Radio']";
		final String GRADING_SCHEME="//h1[contains(text(),'Grading Scheme')]";
		final String SCORING_NUMERIC="//a[@id='numericGradingBtn']";
		final String SCORING_GRADING_AtoF="//a[@id='atofGradingBtn']";
		final String SCORING_COLO_RYG="//a[@id='colorGradingBtn']";
		final String APPLY_NUMERIC="//a[@class='apply applyNumeric']";
		final String APPLY_GRADING="//a[@class='apply applyGradeScoring']";
		final String APPLY_COLOR="//a[@class='apply applyColor']";
	    final String APPLY_GLOBAL_SETTINGS = "//a[@id='saveOrgSettings']";

		
		public AdminScorecardSection(){
			try{
			wait.waitTillElementDisplayed(READY_INDICATOR2, MIN_TIME, MAX_TIME);
			}
			catch(TimeoutException te){
				wait.waitTillElementDisplayed(READY_INDICATOR1, MIN_TIME, MAX_TIME);
			}
		}
		
		public void enableScorecard(){
			item.click(GLOBAL_SETTINGS);
			amtDateUtil.stalePause();
			amtDateUtil.stalePause();
			item.selectCheckBox(ENABLE_SCORECARD);
			item.selectCheckBox(CUST_ROLLUP);
			item.click(APPLY_GLOBAL_SETTINGS);
			amtDateUtil.stalePause();			
		}
		
		public void applyNumericScheme(){
			item.click(GRADING_SCHEME);
			amtDateUtil.stalePause();
			item.click(SCORING_NUMERIC);
			amtDateUtil.stalePause();
			item.click(APPLY_NUMERIC);
			amtDateUtil.stalePause();	
			try{
				driver.switchTo().alert().accept();
			}
			catch(NoAlertPresentException ae){
				
			}
		}
		
		public void applyGradeScheme(){
			item.click(GRADING_SCHEME);
			amtDateUtil.stalePause();
			item.click(SCORING_GRADING_AtoF);
			amtDateUtil.stalePause();
			item.click(APPLY_GRADING);
			amtDateUtil.stalePause();	
			try{
				driver.switchTo().alert().accept();
			}
			catch(NoAlertPresentException ae){
				
			}
		}
		
		public void applyColorScheme(){
			item.click(GRADING_SCHEME);
			amtDateUtil.stalePause();
			item.click(SCORING_COLO_RYG);
			item.click(APPLY_COLOR);
			amtDateUtil.stalePause();	
			try{
				driver.switchTo().alert().accept();
			}
			catch(NoAlertPresentException ae){
				
			}
		}
}		