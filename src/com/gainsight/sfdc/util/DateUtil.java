package com.gainsight.sfdc.util;

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

    public static String addMonths(TimeZone timeZone, int amount, String format) {
        Calendar cal = Calendar.getInstance(timeZone);
        return addMonths(cal, amount, format);
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

    public static Calendar getWeekLabelDate(String weekDay, TimeZone timeZone, int amount, boolean usesEndDate) {
        Calendar cal = Calendar.getInstance(timeZone);
        Map<String, Integer> days = new HashMap<String, Integer>();
        days.put("Sun", 1);
        days.put("Mon", 2);
        days.put("Tue", 3);
        days.put("Wed", 4);
        days.put("Thu", 5);
        days.put("Fri", 6);
        days.put("Sat", 7);
        System.out.println(cal.getTime());
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
        cal.add(Calendar.DATE, amount);
        Log.info("Final Week Label Date : "+cal.getTime());
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

    public static String getShortWeekDayName(Calendar cal) {
        cal.setTimeZone(timeZone);
        DateFormatSymbols symbols = new DateFormatSymbols(new Locale("en"));
        String[] dayNames = symbols.getShortWeekdays();
        String dayName = dayNames[cal.get(Calendar.DAY_OF_WEEK)];
        Log.info("Day Name : " +dayName);
        return dayName;
    }

}
