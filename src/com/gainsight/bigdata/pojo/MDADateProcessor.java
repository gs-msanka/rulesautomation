package com.gainsight.bigdata.pojo;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Created by Giribabu on 08/07/15.
 */
public class MDADateProcessor {
    private String inputFilePath;
    private String outputFilePath;
    List<DateColumnProperties> dateColumnProperties;

    public String getInputFilePath() {
        return inputFilePath;
    }

    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public List<DateColumnProperties> getDateColumnProperties() {
        return dateColumnProperties;
    }

    public void setDateColumnProperties(List<DateColumnProperties> dateColumnProperties) {
        this.dateColumnProperties = dateColumnProperties;
    }

    public static class DateColumnProperties {
        String dateFormat;
        String fieldName;
        @JsonIgnore
        int fieldIndex;
        boolean weekLabel;
        boolean month;
        boolean quarter;
        boolean year;
        String weekStartsOn = "Mon";
        @JsonProperty("weekLabelBasedOnEndDayOfWeek")
        boolean usersEndDate;

        public String getDateFormat() {
            return dateFormat;
        }

        public void setDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }


        public int getFieldIndex() {
            return fieldIndex;
        }

        public void setFieldIndex(int fieldIndex) {
            this.fieldIndex = fieldIndex;
        }

        public boolean isWeekLabel() {
            return weekLabel;
        }

        public void setWeekLabel(boolean weekLabel) {
            this.weekLabel = weekLabel;
        }

        public boolean isMonth() {
            return month;
        }

        public void setMonth(boolean month) {
            this.month = month;
        }

        public boolean isQuarter() {
            return quarter;
        }

        public void setQuarter(boolean quarter) {
            this.quarter = quarter;
        }

        public boolean isYear() {
            return year;
        }

        public void setYear(boolean year) {
            this.year = year;
        }

        public String getWeekStartsOn() {
            return weekStartsOn;
        }

        public void setWeekStartsOn(String weekStartsOn) {
            this.weekStartsOn = weekStartsOn;
        }

        public boolean isUsersEndDate() {
            return usersEndDate;
        }

        public void setUsersEndDate(boolean usersEndDate) {
            this.usersEndDate = usersEndDate;
        }
    }
}
