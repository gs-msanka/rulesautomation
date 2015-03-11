package com.gainsight.sfdc.survey.pojo;

public class SurveyProperties {

    private String sId;
	private String surveyName;
    private String emailService = "Salesforce";
    private String startDate;
    private String endDate;
    private boolean anonymous = false;
    private String type = "Anonymous with account tracking";
    private String anonymousAccount;
    private boolean allowInternalSub = true;
    private String description = "This is loaded form pojo class";
    private String thankYouType = "Message";
    private String thankYouNote = "Thank you for submitting your feedback.";
    private String footerMsg = "Copyright © 2009-2014 Gainsight.com, inc. All rights reserved";
    private String surveyCode;
    private String surveyTitle;
    private String bgColor = "ffffff";
    private String siteURL;
	private String emailTemplate;
	private String defaultAddress;
    private String status;
    private boolean cloneLogicRules = true;
    private boolean cloneParticipants = true;
    private boolean cloneAlertRules = true;
    private boolean loadPartFromCustomObj = false;

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getAnonymousAccount() {
        return anonymousAccount;
    }

    public String getThankYouType() {
        return thankYouType;
    }

    public void setThankYouType(String thankYouType) {
        this.thankYouType = thankYouType;
    }

    public String getThankYouNote() {
        return thankYouNote;
    }

    public void setThankYouNote(String thankYouNote) {
        this.thankYouNote = thankYouNote;
    }

    public void setSurveyCode(String surveyCode) {
        this.surveyCode = surveyCode;
    }

    public void setSurveyTitle(String surveyTitle) {
        this.surveyTitle = surveyTitle;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public String getEmailService() {
		return emailService;
	}

	public void setEmailService(String emailService) {
		this.emailService = emailService;
	}

	public String getSiteURL() {
		return siteURL;
	}

	public void setSiteURL(String siteURL) {
		this.siteURL = siteURL;
	}

	public String getEmailTemplate() {
		return emailTemplate;
	}

	public void setEmailTemplate(String emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	public String getDefaultAddress() {
		return defaultAddress;
	}

	public void setDefaultAddress(String defaultAddress) {
		this.defaultAddress = defaultAddress;
	}


	public String getFooterMsg() {
		return footerMsg;
	}

	public void setFooterMsg(String footerMsg) {
		this.footerMsg = footerMsg;
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

	public void setCode(String surveyCode) {
		this.surveyCode = surveyCode;
	}

	public String getSurveyCode() {

		return surveyCode;
	}

	public void setTitle(String surveyTitle) {
		this.surveyTitle = surveyTitle;
	}

	public String getSurveyTitle() {

		return surveyTitle;
	}

	public void setAnonymousAccount(String anonymousAccount) {
		this.anonymousAccount = anonymousAccount;
	}

	public String getAccountName() {
		return anonymousAccount;
	}


	public void setAnonymous(boolean flag) {
		this.anonymous = flag;
	}

	public boolean isAnonymous() {
		return anonymous;
	}
}




