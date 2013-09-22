/**
 * 
 */
package com.gainsight.sfdc.util;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author sjaggu
 * 
 */
public class Utilities {

	/**
	 * @getRandomString method generates a unique random string
	 * @return
	 */
	public static String getRandomString() {
		String RandomString = null;
		Calendar objCalendar = new GregorianCalendar();
		RandomString = Integer.toString(objCalendar.get(Calendar.MONTH) + 1)
				+ Integer.toString(objCalendar.get(Calendar.DATE))
				+ Integer.toString(objCalendar.get(Calendar.YEAR))
				+ Integer.toString(objCalendar.get(Calendar.HOUR))
				+ Integer.toString(objCalendar.get(Calendar.MINUTE))
				+ Integer.toString(objCalendar.get(Calendar.SECOND));
		return "test"+RandomString;
	}

	public static String generateDate(int day) {

		Format formatter;
		Calendar cal = Calendar.getInstance();

		cal.add(Calendar.DAY_OF_YEAR, day);

		Date date = cal.getTime();
		formatter = new SimpleDateFormat("M/dd/yyyy");
		String dateStr = formatter.format(date);
		System.out.println(dateStr);

		return dateStr;

	}

	public static void main(String a[]) {


		System.out.println(Utilities.generateDate(2));
		System.out.println(Utilities.getRandomString());
	}
}
