package com.gainsight.sfdc.workflow.tests;

import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import io.lamma.Date;
import io.lamma.Dates;
import io.lamma.DayOfWeek;
import io.lamma.HolidayRules;
import io.lamma.Locators;
import io.lamma.Month;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.testng.annotations.AfterClass;

import com.gainsight.sfdc.administration.pages.AdminCockpitConfigPage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.workflow.pojos.CTA;
import com.gainsight.sfdc.workflow.pojos.CockpitConfig;
import com.gainsight.sfdc.workflow.pojos.PlaybookTask;
import com.gainsight.sfdc.workflow.pojos.Task;
import com.sforce.soap.partner.sobject.SObject;

public class WorkflowSetup extends BaseTest{
    ObjectMapper mapper                         = new ObjectMapper();
    private final String DEFAULT_PLAYBOOKS_SCRIPT = Application.basedir+"/testdata/sfdc/workflow/scripts/DefaultPlaybooks.txt";
    
    public void enableSFDCSync_Manual() throws IOException {
         SObject[] appSettings=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__CockpitConfig__c FROM JBCXM__ApplicationSettings__c"));
        if(appSettings[0].getField(resolveStrNameSpace("JBCXM__CockpitConfig__c"))!=null && appSettings[0].getField(resolveStrNameSpace("JBCXM__CockpitConfig__c"))!=""){
        String JBCXM__CockpitConfig__c = appSettings[0].getField(resolveStrNameSpace("JBCXM__CockpitConfig__c")).toString();
        CockpitConfig config = mapper.readValue(JBCXM__CockpitConfig__c, CockpitConfig.class);
        boolean autoSync_FromConfig=Boolean.valueOf(config.getAutoSync());
        
        if(config.getPriorityMapping()=="{}" || !autoSync_FromConfig) { //priority mapping is empty ==> no SF to GS task mapping
            AdminCockpitConfigPage admin = basepage.clickOnAdminTab().clickOnCockpitConfigSubTab();
            if(autoSync_FromConfig){ //in case autosync is enabled...disabling it...since this is only manual Sync case
            	admin=admin.disableAutoSync();            	
            }
            admin = admin.editAndSaveTaskMapping();
        }
    }
        else{
        	  AdminCockpitConfigPage admin = basepage.clickOnAdminTab().clickOnCockpitConfigSubTab();
        	  admin = admin.editAndSaveTaskMapping();
        }
    }
    
    public void enableSFDCSync_Auto() throws IOException {
        SObject[] appSettings=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__CockpitConfig__c FROM JBCXM__ApplicationSettings__c"));
        if(appSettings[0].getField(resolveStrNameSpace("JBCXM__CockpitConfig__c"))!=null && appSettings[0].getField(resolveStrNameSpace("JBCXM__CockpitConfig__c"))!=""){
       String JBCXM__CockpitConfig__c = appSettings[0].getField(resolveStrNameSpace("JBCXM__CockpitConfig__c")).toString();
       CockpitConfig config = mapper.readValue(JBCXM__CockpitConfig__c, CockpitConfig.class);
       boolean autoSync_FromConfig=Boolean.valueOf(config.getAutoSync());
       
       if(!autoSync_FromConfig) { //If autosync is true from config ==> already synced...so not entering the method
           AdminCockpitConfigPage admin = basepage.clickOnAdminTab().clickOnCockpitConfigSubTab();
           admin=admin.enableAutoSync();
           admin = admin.editAndSaveTaskMapping();
       }
   }
        else{
            AdminCockpitConfigPage admin = basepage.clickOnAdminTab().clickOnCockpitConfigSubTab();
            admin=admin.enableAutoSync();
            admin = admin.editAndSaveTaskMapping();
        }
        
   }
    public void disableSFAutoSync() throws IOException {
        SObject[] appSettings=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__CockpitConfig__c FROM JBCXM__ApplicationSettings__c"));
        String JBCXM__CockpitConfig__c = appSettings[0].getField(resolveStrNameSpace("JBCXM__CockpitConfig__c")).toString();
        CockpitConfig config = mapper.readValue(JBCXM__CockpitConfig__c, CockpitConfig.class);
        if(Boolean.valueOf(config.getAutoSync()))  { //If auto sync is already disabled..nothing to do
            AdminCockpitConfigPage admin = basepage.clickOnAdminTab().clickOnCockpitConfigSubTab();
          	 admin = admin.disableAutoSync();
            }
    }
    public int countOfRecords(CTA cta, boolean recurring, List<String> dueDates) {
        String query = "Select id, Name, JBCXM__Account__R.Name, JBCXM__Assignee__c, " +
                "JBCXM__DueDate__c, JBCXM__IsRecurring__c, JBCXM__Reason__r.Name, " +
                "JBCXM__Priority__r.Name, JBCXM__Stage__r.Name, JBCXM__Type__r.Name From JBCXM__CTA__C where " +
                "Name like '%"+cta.getSubject()+"%'  AND JBCXM__Account__R.Name Like '%"+cta.getCustomer()+"%' " +
                "AND JBCXM__IsRecurring__c = "+recurring+" AND JBCXM__Reason__r.Name='"+cta.getReason()+"' AND " +
                "JBCXM__Priority__r.Name = '"+cta.getPriority()+"' AND JBCXM__Stage__r.Name = '"+cta.getStatus()+"' " +
                "AND isDeleted = false";
        String filter = "";
        if(dueDates != null) {
            for(String s : dueDates) {
                filter = filter+" JBCXM__DueDate__c = "+s+" OR ";
            }
            if(filter.length() > 1) {
                query = query+ " AND ( "+filter.substring(0, filter.length()-3)+" )";
            }
        }
        Log.info("Query : " +resolveStrNameSpace(query));
        return getQueryRecordCount(resolveStrNameSpace(query));
    }

    public List<String> getDates(CTA.EventRecurring recurring) {
        int start = Integer.valueOf(recurring.getRecurStartDate());
        int end = Integer.valueOf(recurring.getRecurEndDate());
        HashMap<String , Integer> monthlyMap = new HashMap<>();
        monthlyMap.put("Jan", 1);
        monthlyMap.put("Feb", 2);
        monthlyMap.put("Mar", 3);
        monthlyMap.put("Apr", 4);
        monthlyMap.put("May", 5);
        monthlyMap.put("Jun", 6);
        monthlyMap.put("Jul", 7);
        monthlyMap.put("Aug", 8);
        monthlyMap.put("Sep", 9);
        monthlyMap.put("Oct", 10);
        monthlyMap.put("Nov", 11);
        monthlyMap.put("Dec", 12);
        HashMap<String, Integer> weekDayMap = new HashMap<>();
        weekDayMap.put("Mon", 1);
        weekDayMap.put("Tue", 2);
        weekDayMap.put("Wed", 3);
        weekDayMap.put("Thu", 4);
        weekDayMap.put("Fri", 5);
        weekDayMap.put("Sat", 6);
        weekDayMap.put("Sun", 7);
        Calendar calendar =   Calendar.getInstance(userTimezone);
        Date startDate = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE));
        Date endDate =  new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE));
        Date cal =  new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE));
        if(!recurring.getRecurringType().equalsIgnoreCase("Yearly")) {
            startDate= startDate.plusDays(start);
            endDate = endDate.plusDays(end);
        }

        List<String> dates = new ArrayList<>();
        if(recurring.getRecurringType().equalsIgnoreCase("Daily")) {
            if(recurring.getDailyRecurringInterval().equalsIgnoreCase("EveryWeekday")) {
                List<io.lamma.Date> a = Dates.from(startDate).to(endDate).except(HolidayRules.weekends()).build();
                return getFormatDates(a);
            } else {
                int byNoDays =  Integer.valueOf(recurring.getDailyRecurringInterval());
                List<io.lamma.Date> a = Dates.from(startDate).to(endDate).byDays(byNoDays).build();
                return getFormatDates(a);
            }
        } else if(recurring.getRecurringType().equalsIgnoreCase("Weekly")) {
            String exp[] = recurring.getWeeklyRecurringInterval().split("_");
            int byNoWeeks =  Integer.valueOf(exp[0]);
            for(int x=1; x<exp.length; x++) {
                dates.addAll(getFormatDates(Dates.from(startDate).to(endDate).byWeeks(byNoWeeks).on(DayOfWeek.of(weekDayMap.get(exp[x]))).build()));
            }
            return dates;
        }

        else if(recurring.getRecurringType().equalsIgnoreCase("Monthly")) {
            String exp[] = recurring.getMonthlyRecurringInterval().split("_");
            if(exp[0].equalsIgnoreCase("Day")) {
                int day = Integer.valueOf(exp[1].substring(0, exp[1].length()-2));
                int months = Integer.valueOf(exp[2]);
                List<io.lamma.Date> a =Dates.from(startDate).to(endDate).byMonths(months).on(Locators.nthDay(day)).build();
                return getFormatDates(a);
            } else {
                int day = Integer.valueOf(exp[1].substring(0, exp[1].length()-2));
                String weekDay = exp[2].substring(0,3);
                int months = Integer.valueOf(exp[3]);
                List<io.lamma.Date> a = Dates.from(startDate).to(endDate).byMonths(months).on(Locators.nth(day, DayOfWeek.of(weekDayMap.get(weekDay)))).build();
                return getFormatDates(a);
            }
        } else if(recurring.getRecurringType().equalsIgnoreCase("Yearly")) {
            String exp[] = recurring.getYearlyRecurringInterval().split("_");
            if(exp[0].equalsIgnoreCase("Day")) {
                int day = Integer.valueOf(exp[1].substring(0, exp[1].length()-2));
                String weekDay = exp[2].substring(0, 3);
                String month = exp[3].substring(0, 3);
                List<io.lamma.Date> a = Dates.from(start, cal.mm(), cal.dd()).to(end, cal.mm(), cal.dd()).byYear().byYear().on(Locators.nth(day, DayOfWeek.of(weekDayMap.get(weekDay))).of(Month.of(monthlyMap.get(month)))).build();
                return getFormatDates(a);
            } else {
                int day = Integer.valueOf(exp[1]);
                String month = exp[0].substring(0, 3);
                List<io.lamma.Date> a = Dates.from(start, cal.mm(), cal.dd()).to(end, cal.mm(), cal.dd()).byYear().on(Locators.nthDay(day).of(Month.of(monthlyMap.get(month)))).build();
                return getFormatDates(a);
            }
        }
        return dates;
    }

    public List<String> getFormatDates(List<io.lamma.Date>  dates) {
        List<String> fDates = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(userTimezone);
        for(io.lamma.Date date : dates) {
            Calendar cal = Calendar.getInstance(userTimezone);
            cal.set(date.yyyy(), date.mm()-1, date.dd(), 0, 0, 0);
            fDates.add(dateFormat.format(cal.getTime()));
        }
        System.out.println(dates);
        return fDates;
    }

    public String getTaskDateForPlaybook(int day) {
        Calendar sDate = Calendar.getInstance(userTimezone);
        Calendar eDate = Calendar.getInstance(userTimezone);
        eDate.add(Calendar.DATE, day);
        while(sDate.getTimeInMillis() <= eDate.getTimeInMillis()) {
            if(sDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || sDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                eDate.add(Calendar.DATE, 1);
            }
            sDate.add(Calendar.DATE, 1);
        }
        DateFormat dateFormat = new SimpleDateFormat(USER_DATE_FORMAT);
        dateFormat.setTimeZone(userTimezone);
        return dateFormat.format(eDate.getTime());
    }

    public ArrayList<Task> getTaskFromSFDC(String playbookName) {
        ArrayList<Task> tasks = new ArrayList<>();
        SObject[] records = sfdc.getRecords(resolveStrNameSpace("Select name,JBCXM__DynamicOwner__c,JBCXM__IsDynamicOwner__c,JBCXM__DynamicOwnerLabel__c,JBCXM__DeltaDays__c, JBCXM__Subject__c,JBCXM__Priority__c,JBCXM__Status__c, JBCXM__PlaybookId__r.Name from JBCXM__PlaybookTasks__c where  JBCXM__PlaybookId__r.Name='"+playbookName+"'"));
        if(!(records.length > 0)) {
            throw new RuntimeException("No tasks where found for the playbook");
        }
        for(SObject record : records) {
             Task t = new Task();
             t.setSubject(record.getField(resolveStrNameSpace("JBCXM__Subject__c")).toString());
             t.setDate(record.getField(resolveStrNameSpace("JBCXM__DeltaDays__c")).toString());
             t.setPriority(record.getField(resolveStrNameSpace("JBCXM__Priority__c")).toString());
             t.setStatus(record.getField(resolveStrNameSpace("JBCXM__Status__c")).toString());
             if(record.getField(resolveStrNameSpace("JBCXM__IsDynamicOwner__c")).toString().equalsIgnoreCase("true")) {
                t.setAssignee(record.getField(resolveStrNameSpace("JBCXM__DynamicOwner__c")).toString());
             }
             tasks.add(t);
        }
       return tasks;
    }

    public String getHighestTaskDate(List<Task> tasks) {
        java.util.Date date;
        SimpleDateFormat dateFormat = new SimpleDateFormat(USER_DATE_FORMAT);
        try {
            date = dateFormat.parse(tasks.get(0).getDate());
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Please check the date format "+e.getErrorOffset());
        }
        for(Task t : tasks) {
            try {
                java.util.Date temp = dateFormat.parse(t.getDate());
                if(temp.getTime() > date.getTime()) {
                    date = temp;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                throw new RuntimeException("Please check the date format "+e.getErrorOffset());
            }
        }
        Log.info("Highest Task Date : " +dateFormat.format(date));
        return dateFormat.format(date);
    }

    public int getCountOfUserCTAs(String assignee, String type, boolean isOpen) {
        int count;
        String query = "Select id From JBCXM__CTA__C " +
                " where isDeleted = false AND JBCXM__Type__r.Name='"+type+"' AND JBCXM__Assignee__r.name='"+assignee+"' ";
        if(isOpen) {
            query = query+" AND JBCXM__Stage__r.JBCXM__IncludeInWidget__c = true ";
        } else {
            query = query+" AND JBCXM__Stage__r.JBCXM__IncludeInWidget__c = false ";
        }
        Log.info("Query : " +resolveStrNameSpace(query));
        count = sfdc.getRecordCount(resolveStrNameSpace(query));
        return count;
    }

    public int getCountOfUserCTAsByCustomer(String assignee, String type) {
        int count = 0;
        String query = "select count(JBCXM__Account__c), JBCXM__assignee__c from JBCXM__CTA__c where " +
                "JBCXM__Stage__r.IncludeInWidget__c = true AND JBCXM__Type__r.Name='"+type+"' AND" +
                "JBCXM__Assignee__r.name='"+assignee+"' group by JBCXM__assignee__c";
        Log.info("Query : " +resolveStrNameSpace(query));
        SObject[] records = sfdc.getRecords(resolveStrNameSpace(query));
        if(records.length >0) {
            count = Integer.valueOf(records[0].getField("expr0").toString());
        }
        return count;
    }

    public void cleanPlaybooksData() {
        sfdc.runApexCode(resolveStrNameSpace("delete [Select id from JBCXM__playbook__c];"));
    }
    public void loadDefaultPlaybooks() {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(DEFAULT_PLAYBOOKS_SCRIPT));
    }
}
