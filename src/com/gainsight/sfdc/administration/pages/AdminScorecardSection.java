package com.gainsight.sfdc.administration.pages;

import org.openqa.selenium.NoAlertPresentException;

public class AdminScorecardSection extends AdministrationBasepage{

		final String READY_INDICATOR="//h1[text()='Scorecard Configuration']";
		final String GLOBAL_SETTINGS="";
		final String GRADING_SCHEME="//h1[contains(text(),'Grading Scheme')]";
		final String SCORING_NUMERIC="//a[@id='numericGradingBtn']";
		final String SCORING_GRADING_AtoF="//a[@id='atofGradingBtn']";
		final String SCORING_COLO_RYG="//a[@id='colorGradingBtn']";
		final String APPLY_NUMERIC="//a[@class='apply applyNumeric']";
		final String APPLY_GRADING="//a[@class='apply applyGradeScoring']";
		final String APPLY_COLOR="//a[@class='apply applyColor']";
		
		public AdminScorecardSection(){
			wait.waitTillElementDisplayed(READY_INDICATOR, MIN_TIME, MAX_TIME);
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