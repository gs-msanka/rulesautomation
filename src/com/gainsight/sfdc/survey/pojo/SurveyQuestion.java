package com.gainsight.sfdc.survey.pojo;

import java.util.List;

/**
 * Created by gainsight on 05/12/14.
 */
public class SurveyQuestion {

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public List<String> getSubQuestion() {
        return subQuestion;
    }

    public void setSubQuestion(List<String> subQuestion) {
        this.subQuestion = subQuestion;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isAllowComment() {
        return allowComment;
    }

    public void setAllowComment(boolean allowComment) {
        this.allowComment = allowComment;
    }

    public String getCommentLabel() {
        return commentLabel;
    }

    public void setCommentLabel(String commentLabel) {
        this.commentLabel = commentLabel;
    }

    private String question;
    private List<String> answers;
    private List<String> subQuestion;
    private boolean isActive;
    private boolean allowComment;
    private String commentLabel;





}
