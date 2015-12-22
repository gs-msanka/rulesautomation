package com.gainsight.sfdc.util;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.gainsight.sfdc.tests.BaseTest;

import org.apache.commons.lang3.time.DateUtils;

import com.gainsight.testdriver.Log;

/**
 * Date Utils abstraction on top Apache commons Lang 3
 * @author Sunand
 *
 */
public class DateUtil {

    public static TimeZone timeZone = TimeZone.getTimeZone("GMT");
    public static final String DEFAULT_UTC_DATE_FORMAT = "yyyy-MM-dd'T'00:00:00.000";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

	/**
	 * Increment or decrement months and also fetch the date in the required format.
	 * @param cal Calendar Object
	 * @param amount Can take +ve and -ve integers for increase/decrease
	 * @param format Sample format yyyy-MM-dd
	 * @return
	 */
	public static String addMonths(Calendar cal, int amount, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        cal.add(Calendar.MONTH, amount);
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(cal.getTime());
	}
	
	/**
	 * Method to add number of months in particular timezone
	 * @param cal
	 * @param amount
	 * @param format
	 * @param userSpecifiedTimeZone
	 * @return
	 */
	public static String addMonths(Calendar cal, int amount, String format, String userSpecifiedTimeZone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        cal.add(Calendar.MONTH, amount);
		//setting timezone
		if(userSpecifiedTimeZone == null || userSpecifiedTimeZone.isEmpty()) {
			dateFormat.setTimeZone(timeZone);
		} else {
			dateFormat.setTimeZone(TimeZone.getTimeZone(userSpecifiedTimeZone));
		}
		Log.info((dateFormat.format(cal.getTime())));
        return dateFormat.format(cal.getTime());
	}

    public static String addMonths(TimeZone timeZone, int amount, String format) {
        Calendar cal = Calendar.getInstance(timeZone);
        return addMonths(cal, amount, format);
    }

    public static String addMonths(Date date, int amount, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(DateUtils.addMonths(date, amount));
    }

    public static Calendar addMonths(TimeZone timeZone, int amount) {
        Calendar cal = Calendar.getInstance(timeZone);
        cal.add(Calendar.MONTH, amount);
        return cal;
    }

    public static Calendar addMonths(Calendar cal, int amount) {
        cal.add(Calendar.MONTH, amount);
        return cal;
    }

    /**
     * Date string with specified form will be return.
     * @param cal
     * @param amount
     * @param format
     * @return
     */
    public static String addDays(Calendar cal, int amount, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        cal.add(Calendar.DATE, amount);
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(cal.getTime());
    }
    
	/**
	 * Method to add number of days in particular timezone
	 * @param cal
	 * @param amount
	 * @param format
	 * @param userSpecifiedTimeZone
	 * @return
	 */
	public static String addDays(Calendar cal, int amount, String format, String userSpecifiedTimeZone) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		cal.add(Calendar.DATE, amount);
		//setting timezone
		if(userSpecifiedTimeZone == null || userSpecifiedTimeZone.isEmpty()) {
			dateFormat.setTimeZone(timeZone);
		} else {
			dateFormat.setTimeZone(TimeZone.getTimeZone(userSpecifiedTimeZone));
		}
		Log.info((dateFormat.format(cal.getTime())));
		return dateFormat.format(cal.getTime());
	}

    public static String addDays(Date date, int amount, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(DateUtils.addDays(date, amount));
    }

    public static String addDays(TimeZone timeZone, int amount, String format) {
        Calendar cal = Calendar.getInstance(timeZone);
        return addDays(cal, amount, format);
    }

    public static Calendar addDays(TimeZone timeZone, int amount) {
        Calendar cal = Calendar.getInstance(timeZone);
        cal.add(Calendar.DATE, amount);
        return cal;
    }

    public static Calendar addDays(Calendar cal, int amount) {
        cal.add(Calendar.DATE, amount);
        return cal;
    }

    /**
     * Date by adding weeks with format.
     * @param cal
     * @param amount
     * @param format
     * @return
     */
    public static String addWeeks(Calendar cal, int amount, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(timeZone);
        cal.add(Calendar.WEEK_OF_YEAR, amount);
        return dateFormat.format(cal.getTime());
    }
    
    /** Method to add number of weeks in particular timezone
     * @param cal
     * @param amount
     * @param format
     * @param userSpecifiedTimeZone
     * @return
     */
    public static String addWeeks(Calendar cal, int amount, String format, String userSpecifiedTimeZone) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		//setting timezone
		if(userSpecifiedTimeZone == null || userSpecifiedTimeZone.isEmpty()) {
			dateFormat.setTimeZone(timeZone);
		} else {
			dateFormat.setTimeZone(TimeZone.getTimeZone(userSpecifiedTimeZone));
		}
		Log.info((dateFormat.format(cal.getTime())));
		cal.add(Calendar.WEEK_OF_YEAR, amount);
		return dateFormat.format(cal.getTime());
    }

    public static String addWeeks(Date date, int amount, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(DateUtils.addWeeks(date, amount));
    }

    public static String addWeeks(TimeZone timeZone, int amount, String format) {
        Calendar cal = Calendar.getInstance(timeZone);
        return addWeeks(cal, amount, format);
    }

    public static Calendar addWeeks(TimeZone timeZone, int amount) {
        Calendar cal = Calendar.getInstance(timeZone);
        cal.add(Calendar.WEEK_OF_YEAR, amount);
        return cal;
    }

    public static Calendar addWeeks(Calendar cal, int amount) {
        cal.add(Calendar.WEEK_OF_YEAR, amount);
        return cal;
    }

    public static String getFormattedDate(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return  dateFormat.format(date);
    }

    /**
     * Returns the week label with the specified format.
     *
     * @param date Date
     * @param weekDay - Expected values - Sun, Mon, Tue, Wed, Thu, Fri, Sat
     * @param usesEndDate - true / false
     * @param format - Date format is null then uses default date format.
     * @return String - Formatted date string.
     */
    public static String getWeekLabelDate(Date date, String weekDay, boolean usesEndDate, String format) {
        Calendar cal = Calendar.getInstance();
        int amount = daysBetween(cal.getTime(), date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format ==null ? DEFAULT_DATE_FORMAT : format);
        String sDate = simpleDateFormat.format(getWeekLabelDate(weekDay, timeZone,  amount, usesEndDate).getTime());
        //Log.info("Formatted Date : " +sDate);
        return sDate;
    }

    public static int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }


    /**
     * This parameter returns the String with comprises of yyyy|mm|dd format.
     *
     * @param weekDay - Expected values Sun, Mon, Tue, Wed, Thu, Fri, Sat.
     * @param format - return data format like "mm/dd/yyy", "d/m/yy" etc.
     * @param amount - no of days to add/subtract.
     * @return String - Date.
     */
    public static String getWeekLabelDate(String weekDay, String format, TimeZone timeZone, int amount, boolean usesEndDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(timeZone);
        String sDate = simpleDateFormat.format(getWeekLabelDate(weekDay, timeZone, amount, usesEndDate).getTime());
        Log.info("Formatted Date : " +sDate);
        return sDate;
    }

    /**
     * Get the week label date calendr
     *
     * @param weekDay - Expected values Sun, Mon, Tue, Wed, Thu, Fri, Sat.
     * @param timeZone - Time Zone on which the calculations should be performed.
     * @param amount - no of days to add/subtract.
     * @param usesEndDate - true if week label is based on end date of week.
     * @return Calendar
     */
    public static Calendar getWeekLabelDate(String weekDay, TimeZone timeZone, int amount, boolean usesEndDate) {
        Calendar cal = Calendar.getInstance(timeZone);
        cal.add(Calendar.DATE, amount);
        Map<String, Integer> days = new HashMap<String, Integer>();
        days.put("Sun", 1);
        days.put("Mon", 2);
        days.put("Tue", 3);
        days.put("Wed", 4);
        days.put("Thu", 5);
        days.put("Fri", 6);
        days.put("Sat", 7);
        if(usesEndDate) {
            int weekDate = days.get(weekDay);
            int calLabel = cal.get(Calendar.DAY_OF_WEEK);
            weekDate = (weekDate == 1) ? 7 : weekDate - 1;
            cal.set(Calendar.DAY_OF_WEEK, weekDate);
            if(weekDate < calLabel) {
                cal.add(Calendar.DATE, 7);
            }
        }
        else {
            int a = cal.get(Calendar.DAY_OF_WEEK);
            cal.set(Calendar.DAY_OF_WEEK, days.get(weekDay));
            if(a <  days.get(weekDay)) {
                cal.add(Calendar.DATE, -7);
            }
        }
        //Log.info("Final Week Label Date : "+cal.getTime());
        return cal;
    }

    public static Map<String, String> localMapValues() {
        Map<String, String> localeMap = new HashMap<String, String>(); //holds the locale to timedate formats
        localeMap.put("ar", "dd/MM/yyyy hh:mm a");
        localeMap.put("ar_AE", "dd/MM/yyyy hh:mm a");
        localeMap.put("ar_BH", "dd/MM/yyyy hh:mm a");
        localeMap.put("ar_EG", "dd/MM/yyyy hh:mm a");
        localeMap.put("ar_JO", "dd/MM/yyyy hh:mm a");
        localeMap.put("ar_KW", "dd/MM/yyyy hh:mm a");
        localeMap.put("ar_LB", "dd/MM/yyyy hh:mm a");
        localeMap.put("ar_SA", "dd/MM/yyyy hh:mm a");
        localeMap.put("bg", "yyyy-M-d H:mm");
        localeMap.put("bg_BG", "yyyy-M-d H:mm");
        localeMap.put("ca", "dd/MM/yyyy HH:mm");
        localeMap.put("ca_ES", "dd/MM/yyyy HH:mm");
        localeMap.put("ca_ES_EURO", "dd/MM/yyyy HH:mm");
        localeMap.put("cs", "d.M.yyyy H:mm");
        localeMap.put("cs_CZ", "d.M.yyyy H:mm");
        localeMap.put("da", "dd-MM-yyyy HH:mm");
        localeMap.put("da_DK", "dd-MM-yyyy HH:mm");
        localeMap.put("de", "dd.MM.yyyy HH:mm");
        localeMap.put("de_AT", "dd.MM.yyyy HH:mm");
        localeMap.put("de_AT_EURO", "dd.MM.yyyy HH:mm");
        localeMap.put("de_CH", "dd.MM.yyyy HH:mm");
        localeMap.put("de_DE", "dd.MM.yyyy HH:mm");
        localeMap.put("de_DE_EURO", "dd.MM.yyyy HH:mm");
        localeMap.put("de_LU", "dd.MM.yyyy HH:mm");
        localeMap.put("de_LU_EURO", "dd.MM.yyyy HH:mm");
        localeMap.put("el_GR", "d/M/yyyy h:mm a");
        localeMap.put("en_AU", "d/MM/yyyy HH:mm");
        localeMap.put("en_B", "M/d/yyyy h:mm a");
        localeMap.put("en_BM", "M/d/yyyy h:mm a");
        localeMap.put("en_CA", "dd/MM/yyyy h:mm a");
        localeMap.put("en_IN", "d/M/yyyy h:mm a");
        localeMap.put("en_GB", "dd/MM/yyyy HH:mm");
        localeMap.put("en_GH", "M/d/yyyy h:mm a");
        localeMap.put("en_ID", "M/d/yyyy h:mm a");
        localeMap.put("en_IE", "dd/MM/yyyy HH:mm");
        localeMap.put("en_IE_EURO", "dd/MM/yyyy HH:mm");
        localeMap.put("en_NZ", "d/MM/yyyy HH:mm");
        localeMap.put("en_SG", "M/d/yyyy h:mm a");
        localeMap.put("en_US", "M/d/yyyy h:mm a");
        localeMap.put("en_ZA", "yyyy/MM/dd hh:mm a");
        localeMap.put("es", "d/MM/yyyy H:mm");
        localeMap.put("es_AR", "dd/MM/yyyy HH:mm");
        localeMap.put("es_BO", "dd-MM-yyyy hh:mm a");
        localeMap.put("es_CL", "dd-MM-yyyy hh:mm a");
        localeMap.put("es_CO", "d/MM/yyyy hh:mm a");
        localeMap.put("es_CR", "dd/MM/yyyy hh:mm a");
        localeMap.put("es_EC", "dd/MM/yyyy hh:mm a");
        localeMap.put("es_ES", "d/MM/yyyy H:mm");
        localeMap.put("es_ES_EURO", "d/MM/yyyy H:mm");
        localeMap.put("es_GT", "d/MM/yyyy hh:mm a");
        localeMap.put("es_HN", "MM-dd-yyyy hh:mm a");
        localeMap.put("es_MX", "d/MM/yyyy hh:mm a");
        localeMap.put("es_PE", "dd/MM/yyyy hh:mm a");
        localeMap.put("es_PR", "MM-dd-yyyy hh:mm a");
        localeMap.put("es_PY", "dd/MM/yyyy hh:mm a");
        localeMap.put("es_SV", "MM-dd-yyyy hh:mm a");
        localeMap.put("es_UY", "dd/MM/yyyy hh:mm a");
        localeMap.put("es_VE", "dd/MM/yyyy hh:mm a");
        localeMap.put("et_EE", "d.MM.yyyy H:mm");
        localeMap.put("fi", "d.M.yyyy H:mm");
        localeMap.put("fi_FI", "d.M.yyyy H:mm");
        localeMap.put("fi_FI_EURO", "d.M.yyyy H:mm");
        localeMap.put("fr", "dd/MM/yyyy HH:mm");
        localeMap.put("fr_BE", "d/MM/yyyy H:mm");
        localeMap.put("fr_CA", "yyyy-MM-dd HH:mm");
        localeMap.put("fr_CH", "dd.MM.yyyy HH:mm");
        localeMap.put("fr_FR", "dd/MM/yyyy HH:mm");
        localeMap.put("fr_FR_EURO", "dd/MM/yyyy HH:mm");
        localeMap.put("fr_LU", "dd/MM/yyyy HH:mm");
        localeMap.put("fr_MC", "dd/MM/yyyy HH:mm");
        localeMap.put("hr_HR", "yyyy.MM.dd HH:mm");
        localeMap.put("hu", "yyyy.MM.dd. H:mm");
        localeMap.put("hy_AM", "M/d/yyyy h:mm a");
        localeMap.put("is_IS", "d.M.yyyy HH:mm");
        localeMap.put("it", "dd/MM/yyyy H.mm");
        localeMap.put("it_CH", "dd.MM.yyyy HH:mm");
        localeMap.put("it_IT", "dd/MM/yyyy H.mm");
        localeMap.put("iw", "HH:mm dd/MM/yyyy");
        localeMap.put("iw_IL", "HH:mm dd/MM/yyyy");
        localeMap.put("ja", "yyyy/MM/dd H:mm");
        localeMap.put("ja_JP", "yyyy/MM/dd H:mm");
        localeMap.put("kk_KZ", "M/d/yyyy h:mm a");
        localeMap.put("km_KH", "M/d/yyyy h:mm a");
        localeMap.put("lt_LT", "yyyy.M.d HH.mm");
        localeMap.put("lv_LV", "yyyy.d.M HH:mm");
        localeMap.put("ms_MY", "dd/MM/yyyy h:mm a");
        localeMap.put("nl", "d-M-yyyy H:mm");
        localeMap.put("nl_BE", "d/MM/yyyy H:mm");
        localeMap.put("nl_NL", "d-M-yyyy H:mm");
        localeMap.put("nl_SR", "d-M-yyyy H:mm");
        localeMap.put("no", "dd.MM.yyyy HH:mm");
        localeMap.put("no_NO", "dd.MM.yyyy HH:mm");
        localeMap.put("pl", "yyyy-MM-dd HH:mm");
        localeMap.put("pt", "dd-MM-yyyy H:mm");
        localeMap.put("pt_AO", "dd-MM-yyyy H:mm");
        localeMap.put("pt_BR", "dd/MM/yyyy HH:mm");
        localeMap.put("pt_PT", "dd-MM-yyyy H:mm");
        localeMap.put("ro_RO", "dd.MM.yyyy HH:mm");
        localeMap.put("ru", "dd.MM.yyyy H:mm");
        localeMap.put("sk_SK", "d.M.yyyy H:mm");
        localeMap.put("sl_SI", "d.M.y H:mm");
        localeMap.put("sv", "yyyy-MM-dd HH:mm");
        localeMap.put("sv_SE", "yyyy-MM-dd HH:mm");
        localeMap.put("th", "M/d/yyyy h:mm a");
        localeMap.put("th_TH", "d/M/yyyy, H:mm ?.");
        localeMap.put("tr", "dd.MM.yyyy HH:mm");
        localeMap.put("ur_PK", "M/d/yyyy h:mm a");
        localeMap.put("vi_VN", "HH:mm dd/MM/yyyy");
        localeMap.put("zh", "yyyy-M-d ah:mm");
        localeMap.put("zh_CN", "yyyy-M-d ah:mm");
        localeMap.put("zh_HK", "yyyy-M-d ah:mm");
        localeMap.put("zh_TW", "yyyy/M/d a h:mm");
        localeMap.put("el", "d/M/yyyy h:mm a");
        localeMap.put("en_BB", "d/M/yyyy h:mm a");
        localeMap.put("en_MY", "M/d/yyyy h:mm a");
        localeMap.put("en_NG", "M/d/yyyy h:mm a");
        localeMap.put("en_PH", "M/d/yyyy h:mm a");
        localeMap.put("es_DO", "MM/dd/yyyy hh:mm a");
        localeMap.put("es_PA", "MM/dd/yyyy hh:mm a");
        localeMap.put("es_SVUS", "MM-dd-yyyy hh:mm a");
        localeMap.put("hu_HU", "yyyy.MM.dd HH:mm");
        localeMap.put("in", "yyyy/MM/dd HH:mm");
        localeMap.put("in_ID", "dd/MM/yyyy HH:mm");
        localeMap.put("ka", "M/d/yyyy h:mm a");
        localeMap.put("ka_GE", "M/d/yyyy h:mm a");
        localeMap.put("ro", "dd.MM.yyyy HH:mm");
        localeMap.put("ru_RU", "dd.MM.yyyy HH:mm");
        localeMap.put("sh", "dd.MM.yyyy HH:mm");
        localeMap.put("sh_BA", "dd.MM.yyyy HH:mm");
        localeMap.put("sh_CS", "dd.MM.yyyy HH:mm");
        localeMap.put("sk", "d.M.yyyy HH:mm");
        localeMap.put("sr", "d.M.yyyy HH:mm");
        localeMap.put("sr_BA", "yyyy-MM-dd HH:mm");
        localeMap.put("sr_CS", "d.M.yyyy HH:mm");
        localeMap.put("tl", "M/d/yyyy h:mm a");
        localeMap.put("tl_PH", "M/d/yyyy h:mm a");
        localeMap.put("tr_TR", "dd.MM.yyyy HH:mm");
        localeMap.put("uk", "dd.MM.yyyy HH:mm");
        localeMap.put("uk_UA", "dd.MM.yyyy HH:mm");
        return localeMap;
    }
    
    public static String getMonthName(Calendar cal) {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("MMM");
        dateFormat.setTimeZone(timeZone);
    	return dateFormat.format(cal.getTime());
    }

    public static String parseFixedFmtDate(String dateStr, TimeZone userTimeZone, String USER_DATE_FORMAT) {
        SimpleDateFormat dateFmt =  new SimpleDateFormat("MM/dd/yyyy");
        dateFmt.setTimeZone(userTimeZone);
        Date formattedDate = null;
        try {
            formattedDate = dateFmt.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(formattedDate);
        dateFmt = new SimpleDateFormat(USER_DATE_FORMAT);
        dateFmt.setTimeZone(BaseTest.userTimezone);
        return dateFmt.format(c.getTime());
    }

    /**
     * Returns Week Day Short Name.
     * @param cal
     * @return
     */
    public static String getShortWeekDayName(Calendar cal) {
        cal.setTimeZone(timeZone);
        DateFormatSymbols symbols = new DateFormatSymbols(new Locale("en"));
        String[] dayNames = symbols.getShortWeekdays();
        String dayName = dayNames[cal.get(Calendar.DAY_OF_WEEK)];
        Log.info("Day Name : " + dayName);
        return dayName;
    }

    /**
     * Returns First day of month as date.
     * @param calendar
     * @return
     */
    public static String getMonthFirstDate(Calendar calendar) {
        calendar.set(Calendar.DATE, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Returns First day of month as date.
     * @param date
     * @param format
     * @return
     */
    public static String getMonthFirstDate(Date date, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format==null ? DEFAULT_DATE_FORMAT : format);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Returns First day of month as date.
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static String getMonthFirstDate(String dateStr) throws ParseException {
        SimpleDateFormat dateFmt =  new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        Date formattedDate = dateFmt.parse(dateStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(formattedDate);
        calendar.set(Calendar.DATE, 1);
        return dateFmt.format(calendar.getTime());
    }

    /**
     * Returns First day of month as date String with format specified.
     * @param dateStr
     * @param format
     * @return
     * @throws ParseException
     */
    public static String getMonthFirstDate(String dateStr, String format) throws ParseException {
        SimpleDateFormat dateFmt =  new SimpleDateFormat(format);
        Date formattedDate = dateFmt.parse(dateStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(formattedDate);
        calendar.set(Calendar.DATE, 1);
        return dateFmt.format(calendar.getTime());
    }

    /**
     * Returns the first day of quarter as date string.
     * @param calendar
     * @return
     */
    public static String getQuarterFirstDate(Calendar calendar) {
        int month = calendar.get(Calendar.MONTH);
        month = getQuarterOfMonth(month);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Returns the first day of quarter as date string  of the date supplied.
     * @param date
     * @param format
     * @return
     */
    public static String getQuarterFirstDate(Date date, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        month = getQuarterOfMonth(month);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format ==null ? DEFAULT_DATE_FORMAT : format);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Returns the first day of quarter as date string  of the date supplied.
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static String getQuarterFirstDate(String dateStr) throws ParseException {
        SimpleDateFormat dateFmt =  new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        Date date = dateFmt.parse(dateStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        month = getQuarterOfMonth(month);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, 1);
        return dateFmt.format(calendar.getTime());
    }

    /**
     * Returns year in date.
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * Return year in a date String
     *
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static int getYear(String dateStr) throws ParseException {
        SimpleDateFormat dateFmt =  new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        Date date = dateFmt.parse(dateStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * Return the quarter of the month.
     * @param currentMonth
     * @return
     */
    private static int getQuarterOfMonth(int currentMonth) {
        if(currentMonth < 3) {
            currentMonth = 0;
        } else if(currentMonth < 6) {
            currentMonth = 3;
        } else if(currentMonth < 9) {
            currentMonth = 6;
        } else {
            currentMonth = 9;
        }
        return currentMonth;
    }
    
    public static String  getDateWithRequiredFormat(int days, int months, String format) {
		String date = null;
		date = DateUtil.addDays(DateUtil.addMonths(timeZone, months), days, format);
		Log.info("Formatted Date :" + date);
		return date;
    }

	/**
	 * @param date - date as string
	 * @param format - dateformat {eg: yyyy-MM-dd}
	 * @param timeZone - timezone
	 * @return - date in particular timezone
	 * @throws ParseException
	 */
	public static String getFormattedDate(String date,String format, String timeZone) throws ParseException{	
		DateFormat dateFormat = new SimpleDateFormat(format);
		Date parsedDate = dateFormat.parse(date);
		Log.info("Epoch time is " + parsedDate.getTime());
		dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
		return dateFormat.format(new Date(parsedDate.getTime()));		
	}
	
	/**
	 * @param date - date as string 
	 * @param format - dateformat {eg: yyyy-MM-dd}
	 * @return - date as string in required format
	 * @throws ParseException
	 */
	public static String addDays(String date, String format, int days) throws ParseException{
		DateFormat dateFormat = new SimpleDateFormat(format);
		Date parsedDate = dateFormat.parse(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parsedDate);
		calendar.add(Calendar.DATE, days);
		Log.info("Actual Date is " + calendar.getTime());
		Log.info(dateFormat.format(calendar.getTime()));
		return dateFormat.format(calendar.getTime());
	}
	
    public static String getDateWithFormat(int days, int months ,TimeZone timeZone, String format) {
        String date = null;
         date = DateUtil.addDays(DateUtil.addMonths(timeZone, months), days, format);
        Log.info("Date :" +date);
        return date;
    }
}
