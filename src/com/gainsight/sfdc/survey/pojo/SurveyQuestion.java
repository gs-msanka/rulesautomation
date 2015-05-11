package com.gainsight.sfdc.survey.pojo;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by gainsight on 05/12/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SurveyQuestion {


    private String pageTitle    = "Untitled Page";
    private String questionText = "Untitled question";
    private String pageId;
    private String questionId;
    private String questionType;  //Expected Values - CHECKBOX, SELECT, NPS, RATING, RANKING, TEXT_INPUT, TEXT_AREA, MATRIX
    private String parentQuestionText;
    private String parentQuestionId;
    private String headerId;
    private String headerTitle;
    private boolean active          = true;
    private boolean required        = false;
    private boolean allowComments   = false;
    private boolean addOther        = false;
    private boolean singleAnswer    = false;
    private boolean sectionHeader   = false;
    private String image;
    private String commentsLabel    = "Comments";
    private String otherLabel = "Other";
    private String newPageTitle;
    
    public String getOtherLabel() {
        return otherLabel;
    }

    public void setOtherLabel(String otherLabel) {
        this.otherLabel = otherLabel;
    }

    private int displayOrder;
    private ArrayList<SurveyAllowedAnswer> allowedAnswers = new ArrayList<SurveyAllowedAnswer>();
    private ArrayList<SurveySubQuestions> subQuestions = new ArrayList<SurveySubQuestions>();
    private SurveyProperties surveyProperties;

    public String getHeaderId() {
        return headerId;
    }

    public void setHeaderId(String headerId) {
        this.headerId = headerId;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public String getParentQuestionText() {
        return parentQuestionText;
    }

    public void setParentQuestionText(String parentQuestionText) {
        this.parentQuestionText = parentQuestionText;
    }

    public String getParentQuestionId() {
        return parentQuestionId;
    }

    public void setParentQuestionId(String parentQuestionId) {
        this.parentQuestionId = parentQuestionId;
    }

    public String getCommentsLabel() {
        return commentsLabel;
    }

    public void setCommentsLabel(String commentsLabel) {
        this.commentsLabel = commentsLabel;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }
    
    public String getnewPageTitle(){
    	return newPageTitle;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isAllowComments() {
        return allowComments;
    }

    public void setAllowComments(boolean allowComments) {
        this.allowComments = allowComments;
    }

    public boolean isAddOther() {
        return addOther;
    }

    public void setAddOther(boolean addOther) {
        this.addOther = addOther;
    }

    public boolean isSingleAnswer() {
        return singleAnswer;
    }

    public void setSingleAnswer(boolean singleAnswer) {
        this.singleAnswer = singleAnswer;
    }

    public boolean isSectionHeader() {
        return sectionHeader;
    }

    public void setSectionHeader(boolean sectionHeader) {
        this.sectionHeader = sectionHeader;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<SurveyAllowedAnswer> getAllowedAnswers() {
        return allowedAnswers;
    }

    public void setAllowedAnswers(ArrayList<SurveyAllowedAnswer> allowedAnswers) {
        this.allowedAnswers = allowedAnswers;
    }

    public ArrayList<SurveySubQuestions> getSubQuestions() {
        return subQuestions;
    }

    public void setSubQuestions(ArrayList<SurveySubQuestions> subQuestions) {
        this.subQuestions = subQuestions;
    }

    public static class SurveyAllowedAnswer {
        private String sId;
		private String answerText;
        private String displayOrder;

        public String getsId() {
            return sId;
        }

        public void setsId(String sId) {
            this.sId = sId;
        }

        public String getAnswerText() {
            return answerText;
        }

        public void setAnswerText(String answerText) {
            this.answerText = answerText;
        }

        public String getDisplayOrder() {
            return displayOrder;
        }

        public void setDisplayOrder(String displayOrder) {
            this.displayOrder = displayOrder;
        }
    }

    public static class SurveySubQuestions {
        private String sId;
        private String subQuestionText;
        private String displayOrder;

        public String getsId() {
            return sId;
        }

        public void setsId(String sId) {
            this.sId = sId;
        }

        public String getSubQuestionText() {
            return subQuestionText;
        }

        public void setSubQuestionText(String subQuestionText) {
            this.subQuestionText = subQuestionText;
        }

        public String getDisplayOrder() {
            return displayOrder;
        }

        public void setDisplayOrder(String displayOrder) {
            this.displayOrder = displayOrder;
        }
    }

    public SurveyProperties getSurveyProperties() {
        return surveyProperties;
    }

    public void setSurveyProperties(SurveyProperties surveyProperties) {
        this.surveyProperties = surveyProperties;
    }
}
