package com.gainsight.sfdc.tests;

import java.text.DecimalFormat;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.utils.TestDataHolder;

public class BaseTest {
	protected TestDataHolder testDataLoader=new TestDataHolder();
	String[] dirs={"testdata","sfdc"};
	public String TEST_DATA_PATH_PREFIX;
    TestEnvironment env=new TestEnvironment();	
	public BasePage basepage;
	
	@BeforeSuite
	public void init(){
		env.start();
		basepage = new BasePage();
		TEST_DATA_PATH_PREFIX = TestEnvironment.basedir + "/" + generatePath(dirs);
	}
	
	@AfterSuite
	public void fini(){
		env.stop();
	}
	
	public String generatePath(String[] dirs){
		String path="";
		for(String dir : dirs){
			path=path+dir+"/";			
		}
		return path;
	}
	
	public String currencyFormat(String amt){
		DecimalFormat moneyFormat = new DecimalFormat("$###,###");
		return moneyFormat.format(new Long(amt)).replace("$", "$ ");
	}
}
