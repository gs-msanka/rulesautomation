package com.gainsight.bigdata.rulesengine.bean.RuleAction;

import com.gainsight.bigdata.copilot.bean.outreach.ReportTokenMapping;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Created by Giribabu on 04/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ActionTrueCase {
    //Used in co-pilot.
    private int order;
    private String actionType;
    private String target;
    private String recipientStrategy;
    private String identifierType;
    private String recipientFieldName;
    private ActionParam params;
    private ActionMapping[] mappings;
    private List<ActionQuery> queries;
    //Used in send email - (ex: copilot).
    private String emailTemplateName;
    //Used in send email - (ex: copilot).
    private String emailTemplateId;
    //Used in send email - (ex: copilot).
    private ActionFromAddress fromAddress;
    //Used in send email - (ex: copilot).
    private TokenMapping tokenMapping;
    //Used in send email - (ex: copilot).
    private String[] copyToAddress;
    //Used in send email - (ex: copilot).
    private boolean reportNotNullable;
    //Used in send email - (ex: copilot).
    @JsonProperty("addUnsubscribeFooter")
    private boolean addUnSubscribeFooter;
    @JsonProperty("isTransactional")
    private boolean transactional;
    private int preventDuplicateDays;
    //Used in send email - (ex: copilot).
    private List<ReportTokenMapping> reportTokenMappings;
    //Used in Copilot.
    private String actionInfo;

    public String getActionInfo() {
        return actionInfo;
    }

    public void setActionInfo(String actionInfo) {
        this.actionInfo = actionInfo;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<ReportTokenMapping> getReportTokenMappings() {
        return reportTokenMappings;
    }

    public void setReportTokenMappings(List<ReportTokenMapping> reportTokenMappings) {
        this.reportTokenMappings = reportTokenMappings;
    }

    public int getPreventDuplicateDays() {
        return preventDuplicateDays;
    }

    public void setPreventDuplicateDays(int preventDuplicateDays) {
        this.preventDuplicateDays = preventDuplicateDays;
    }

    public boolean isTransactional() {
        return transactional;
    }

    public void setTransactional(boolean transactional) {
        this.transactional = transactional;
    }

    public boolean isAddUnSubscribeFooter() {
        return addUnSubscribeFooter;
    }

    public void setAddUnSubscribeFooter(boolean addUnSubscribeFooter) {
        this.addUnSubscribeFooter = addUnSubscribeFooter;
    }

    public boolean isReportNotNullable() {
        return reportNotNullable;
    }

    public void setReportNotNullable(boolean reportNotNullable) {
        this.reportNotNullable = reportNotNullable;
    }

    public String[] getCopyToAddress() {
        return copyToAddress;
    }

    public void setCopyToAddress(String[] copyToAddress) {
        this.copyToAddress = copyToAddress;
    }

    public TokenMapping getTokenMapping() {
        return tokenMapping;
    }

    public void setTokenMapping(TokenMapping tokenMapping) {
        this.tokenMapping = tokenMapping;
    }

    public ActionFromAddress getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(ActionFromAddress fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getEmailTemplateName() {
        return emailTemplateName;
    }

    public void setEmailTemplateName(String emailTemplateName) {
        this.emailTemplateName = emailTemplateName;
    }

    public String getEmailTemplateId() {
        return emailTemplateId;
    }

    public void setEmailTemplateId(String emailTemplateId) {
        this.emailTemplateId = emailTemplateId;
    }

    public List<ActionQuery> getQueries() {
        return queries;
    }

    public void setQueries(List<ActionQuery> queries) {
        this.queries = queries;
    }

    public ActionParam getParams() {
        return params;
    }

    public void setParams(ActionParam params) {
        this.params = params;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getRecipientStrategy() {
        return recipientStrategy;
    }

    public void setRecipientStrategy(String recipientStrategy) {
        this.recipientStrategy = recipientStrategy;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    public String getRecipientFieldName() {
        return recipientFieldName;
    }

    public void setRecipientFieldName(String recipientFieldName) {
        this.recipientFieldName = recipientFieldName;
    }

    public ActionMapping[] getMappings() {
        return mappings;
    }

    public void setMappings(ActionMapping[] mappings) {
        this.mappings = mappings;
    }
}
