package com.gainsight.sfdc.survey.pojo;

public class SurveyProperties {

    private String code;
    private String title;
    private String anonymous_option;
    private String accountName;
    private String tOption;
    private String imageName;
    private String filePath;
    private String startDate;
    private String endDate;
    private String description = "This is loaded form pojo class";
    private String thankYou;
    private String status = "Design";
    private String footerMsg = "Copyright © 2009-2014 Gainsight.com, inc. All rights reserved";
    private boolean anonymous = false;
    private boolean cloneLogicRules = true;
    private boolean cloneParticipants = true;
    private boolean cloneAlertRules = true;
    private boolean allowInternalSub = false;
    private boolean loadPartFromCustomObj = false;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFooterMsg() {
        return footerMsg;
    }

    public void setFooterMsg(String footerMsg) {
        this.footerMsg = footerMsg;
    }

    public String getThankYou() {
        return thankYou;
    }

    public void setThankYou(String thankYou) {
        this.thankYou = thankYou;
    }

    public boolean isAllowInternalSub() {

        return allowInternalSub;
    }

    public void setAllowInternalSub(boolean allowInternalSub) {
        this.allowInternalSub = allowInternalSub;
    }

    public boolean isLoadPartFromCustomObj() {
        return loadPartFromCustomObj;
    }

    public void setLoadPartFromCustomObj(boolean loadPartFromCustomObj) {
        this.loadPartFromCustomObj = loadPartFromCustomObj;
    }


    public String getAnonymous_option() {
        return anonymous_option;
    }

    public void setAnonymous_option(String anonymous_option) {
        this.anonymous_option = anonymous_option;
    }

    public String gettOption() {
        return tOption;
    }

    public void settOption(String tOption) {
        this.tOption = tOption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCloneLogicRules() {
        return cloneLogicRules;
    }

    public void setCloneLogicRules(boolean cloneLogicRules) {
        this.cloneLogicRules = cloneLogicRules;
    }

    public boolean isCloneAlertRules() {
        return cloneAlertRules;
    }

    public void setCloneAlertRules(boolean cloneAlertRules) {
        this.cloneAlertRules = cloneAlertRules;
    }

    public boolean isCloneParticipants() {
        return cloneParticipants;
    }

    public void setCloneParticipants(boolean cloneParticipants) {
        this.cloneParticipants = cloneParticipants;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {

        return code;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {

        return title;
    }

    public void setAOption(String anomymous_option) {
        this.anonymous_option = anomymous_option;
    }

    public String getAOption() {
        return anonymous_option;
    }

    public void setAccountName(String accountname) {
        this.accountName = accountname;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setTUOption(String toption) {
        this.tOption = toption;
    }

    public String getTUOption() {
        return tOption;
    }


    public void setImageName(String imagename) {
        this.imageName = imagename;
    }

    public String getImageName() {
        return imageName;
    }

    public void setAnonymous(boolean flag) {
        this.anonymous = flag;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setFilePath(String filepath) {
        this.filePath = filepath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setStartDate(String startdate) {
        this.startDate = startdate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setEndDate(String enddate) {
        this.endDate = enddate;
    }

    public String getEndDate() {
        return endDate;
    }
}

