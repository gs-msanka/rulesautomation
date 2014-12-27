List<JBCXM__ApplicationSettings__c> appSettingsList = [SELECT ID, JBCXM__AdoptionAggregationType__c, JBCXM__AdoptionGranularity__c,
													JBCXM__AdoptionAggregationColumns__c, JBCXM__AdoptionMeasureColMap__c, JBCXM__WeekStartsOn__c,
													JBCXM__UsesEndDateAsWeekName__c from JBCXM__ApplicationSettings__c LIMIT 1];
JBCXM__ApplicationSettings__c appSettings;
if(appSettingsList != null && appSettingsList.size() > 0) {
	 appSettings = appSettingsList.get(0);
	 appSettings.JBCXM__AdoptionAggregationType__c = 'ACCOUNTLEVEL';
	 appSettings.JBCXM__AdoptionGranularity__c = 'WEEKLY';
	 appSettings.JBCXM__WeekStartsOn__c = 'Sat';
	 appSettings.JBCXM__UsesEndDateAsWeekName__c = TRUE;
	 appSettings.JBCXM__AdoptionMeasureColMap__c = '{"Files Downloaded":"Files_Downloaded__c","No of Report Run":"No_of_Report_Run__c","Page Views":"Page_Views__c","Page Visits":"Page_Visits__c"}';
	 update appSettings;
}


