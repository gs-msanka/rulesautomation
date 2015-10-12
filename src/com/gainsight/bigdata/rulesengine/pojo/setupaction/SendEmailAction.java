package com.gainsight.bigdata.rulesengine.pojo.setupaction;

/**
 * Created by vmenon on 9/13/2015.
 */
public class SendEmailAction {

    private String emailService = "";
    private String emailTemplate = "";
    private String fromName = "";
    private String fromEmail = "";
    private String to = "";
    private String replyTo = "";
    private String operationalEmail = "";
    private String subscription = "";

    public String getEmailService() {
        return emailService;
    }

    public void setEmailService(String emailService) {
        this.emailService = emailService;
    }

    public String getEmailTemplate() {
        return emailTemplate;
    }

    public void setEmailTemplate(String emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getOperationalEmail() {
        return operationalEmail;
    }

    public void setOperationalEmail(String operationalEmail) {
        this.operationalEmail = operationalEmail;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }
}
