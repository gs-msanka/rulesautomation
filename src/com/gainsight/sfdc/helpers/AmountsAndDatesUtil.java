package com.gainsight.sfdc.helpers;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import com.gainsight.pageobject.core.WebPage;
import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.tests.BaseTest;

public class AmountsAndDatesUtil {
	public final static SimpleDateFormat EN_IND_10 = new SimpleDateFormat(
			"dd/MM/yyyy");
	public final static SimpleDateFormat EN_IND = new SimpleDateFormat(
			"d/M/yyyy");
	public final static SimpleDateFormat OTHER_10 = new SimpleDateFormat(
			"MM/dd/yyyy");
	public final static SimpleDateFormat OTHER = new SimpleDateFormat(
			"M/d/yyyy");

	public String currencyFormat(String amt) {
		DecimalFormat moneyFormat = new DecimalFormat("$0");
		return moneyFormat.format(new Long(amt));
	}

	/**
	 * Even we are handling stale element exceptions at framework level it is
	 * time consuming hence if see any element happens to bound to this
	 * exception because of frequent DOM updates, please call this method before
	 * performing any action on that element so that webdriver finds elements
	 * after DOM got updated( 2 seconds is optimal time)
	 */
	public void stalePause() {
		Timer.sleep(2);
	}

	public void sleep(int seconds) {
		Timer.sleep(seconds);
	}

	
	public String formatNumber(String number) {
		NumberFormat numberFormatter = NumberFormat
				.getNumberInstance(Locale.ENGLISH);
		return numberFormatter.format(Integer.parseInt(number));
	}

	public static HashMap<String, String> updateDatesLocale(
			HashMap<String, String> nbData)
			throws ParseException {
		String locale=BaseTest.userLocale;
		HashMap<String, String> newData = new HashMap<String, String>();
		SimpleDateFormat dateFmt = null;
		Iterator<String> itr = nbData.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			String date = nbData.get(key);
			if (key.contains("Date") || key.contains("date")) {
				Date dt = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
						.parse(date);
				dateFmt = date.length() == 10 ? (locale.equals("en_IN") ? EN_IND_10
						: OTHER_10)
						: (locale.equals("en_IN") ? EN_IND : OTHER);
				String formatedDate = dateFmt.format(dt);
				newData.put(key, formatedDate);
			} else {
				newData.put(key, nbData.get(key));
			}
		}
		return newData;
	}

	public static String getCurrentDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(BaseTest.userTimezone);
		SimpleDateFormat sdf = BaseTest.userLocale.equals("en_IN") ? EN_IND : OTHER;
		sdf.setTimeZone(BaseTest.userTimezone);
		return sdf.format(cal.getTime());
	}

	public static String getFormattedDate(String dateStr, int days
			) {
		String userLocale=BaseTest.userLocale;
		SimpleDateFormat dateFmt = dateStr.length() == 10 ? (userLocale
				.equals("en_IN") ? EN_IND_10 : OTHER_10) : (userLocale
				.equals("en_IN") ? EN_IND : OTHER);
		dateFmt.setTimeZone(BaseTest.userTimezone);
		Date formatedDate = null;
		try {
			formatedDate = dateFmt.parse(dateStr);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(formatedDate);
		if (days > 0){
		c.add(Calendar.DATE, days);
		}
		dateFmt = userLocale.equals("en_IN") ? EN_IND : OTHER;
		dateFmt.setTimeZone(BaseTest.userTimezone);
		return dateFmt.format(c.getTime());
	}
	public static String parseFixedFmtDate(String dateStr) {
		SimpleDateFormat dateFmt =  OTHER_10;
		dateFmt.setTimeZone(BaseTest.userTimezone);
		Date formatedDate = null;
		try {
			formatedDate = dateFmt.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(formatedDate);
		dateFmt = BaseTest.userLocale.equals("en_IN") ? EN_IND : OTHER;
		dateFmt.setTimeZone(BaseTest.userTimezone);
		return dateFmt.format(c.getTime());
	}
}
