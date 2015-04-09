package com.gainsight.sfdc.survey.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SurveyDistribution{
	
private String scheduleName;
private String scheduleType;
private String scheduleDate;
private String hours;
private String minutes;

public void setScheduleName(String scheduleName) {
	this.scheduleName = scheduleName;
}

public String getScheduleName() {
	return scheduleName;
}

public void setScheduleType(String scheduleType) {
	this.scheduleType = scheduleType;
}

public String getScheduleType(){
	return scheduleType;	
}

public void setScheduleDate(String scheduleDate) {
	this.scheduleDate = scheduleDate;
}

public String getScheduleDate(){
	return scheduleDate;
}

public void setHours(String hours) {
	this.hours = hours;
}

public String getHours(){
	return hours;	
}

/*public void setMinutes(String minutes) {
	this.minutes = minutes;
}*/

public String getMinutes(){
	return minutes;	
}

}
