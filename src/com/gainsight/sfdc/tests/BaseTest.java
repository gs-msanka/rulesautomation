package com.gainsight.sfdc.tests;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.utils.TestDataHolder;

public class BaseTest {
	protected TestDataHolder testDataLoader=new TestDataHolder();
	String[] dirs={"testdata","sfdc"};
	public final String TEST_DATA_PATH_PREFIX=generatePath(dirs);
    TestEnvironment env=new TestEnvironment();	
	public BasePage basepage;
	
	@BeforeSuite
	public void init(){
		env.start();
		basepage = new BasePage();
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
	public String getCurrentDate(){
	    Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
       return sdf.format(cal.getTime());
     
}
	public String getFormattedDate(String dateStr) throws ParseException{
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date date=formatter.parse(dateStr);	    
        SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
       return sdf.format(date);
     
}
	public String getFormattedDate(String dateStr, int days) throws ParseException{
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date date=formatter.parse(dateStr);	
		Calendar c = Calendar.getInstance();
		c.setTime(date); 
		c.add(Calendar.DATE, 1); 
        SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
       return sdf.format(c.getTime());
     
}
	public HashMap<String, String> getMapFromData(String data){
		HashMap<String, String> hm = new HashMap<String, String>();
		System.out.println(data);
		String[] dataArray=data.substring(1, data.length()-1).split("\\|");
		for (String record: dataArray){
			if(record!= null){
			System.out.println(record);
			String[] pair=record.split("\\:");
			hm.put(pair[0], pair[1]);
			}
			
		}
		return hm;
	}
	
}
