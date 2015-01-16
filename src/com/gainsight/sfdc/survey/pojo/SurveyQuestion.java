package com.gainsight.sfdc.survey.pojo;

import java.util.ArrayList;
import java.util.List;

import com.gainsight.sfdc.workflow.pojos.CTA.Attribute;

/**
 * Created by gainsight on 05/12/14.
 */
public class SurveyQuestion {
	private String surveyPage;
	private String questionType;
	private String question;
    private boolean isActive;
    private boolean isRequired;
    private boolean singleAnswer;
    private boolean allowComment;
    private String commentLabel;    
    private boolean addOthers;
    private String otherLabel;
    private ArrayList<SurveyAllowedAnswer> allowedAnswers = new ArrayList<SurveyAllowedAnswer>();
    private ArrayList<SurveySubQuestions> subQuestions = new ArrayList<SurveySubQuestions>();
    
       
    public String getquestionType () {
    	return questionType;
    }
    
    public void setquestionType(String questionType) {
        this.questionType = questionType;
    }   
    
      
    public String getsurveyPage() {
    	return surveyPage;
    }
    
    public void setsurveyPage(String surveyPage) {
        this.surveyPage = surveyPage;
    }
    
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }
    

    public boolean isAllowComment() {
        return allowComment;
    }

    public void setAllowComment(boolean allowComment) {
        this.allowComment = allowComment;
    }
    
    public boolean isaddOthers() {
        return addOthers;
    }

    public void setaddOthers(boolean addOthers) {
        this.addOthers = addOthers;
    }

    public String getCommentLabel() {
        return commentLabel;
    }

    public void setCommentLabel(String commentLabel) {
        this.commentLabel = commentLabel;
    }
    
    
    public String getotherLabel() {
        return otherLabel;
    }

    public void setotherLabel(String otherLabel) {
        this.otherLabel = otherLabel;
    }
    
    public boolean isSingleAnswer() {
        return singleAnswer;
    }

    public void setSingleAnswer(boolean singleAnswer) {
        this.singleAnswer = singleAnswer;
    }  
    
    public ArrayList<SurveyAllowedAnswer> getAllowedAnswers() {
		return allowedAnswers;
	}

	public void setAllowedAnswers(ArrayList<SurveyAllowedAnswer> allowedAnswers) {
		this.allowedAnswers = allowedAnswers;
	}

	public static class SurveyAllowedAnswer {
		private String ansLabel;
		private String ansValue;
		private String rows;
		private String columns;
		
		public String getColumns() {
			return columns;
		}

		public void setColumns(String columns) {
			this.columns = columns;
		}

		public String getRows() {
			return rows;
		}

		public void setRows(String rows) {
			this.rows = rows;
		}
        public String getAnsLabel() {
			return ansLabel;
		}

		public void setAnsLabel(String attLabel) {
			this.ansLabel = attLabel;
		}

		public String getAnsValue() {
			return ansValue;
		}

		public void setAnsValue(String ansValue) {
			this.ansValue = ansValue;
		}
	}	
	
	 public ArrayList<SurveySubQuestions> getsubQuestions() {
			return subQuestions;
		}

		public void setsubQuestions(ArrayList<SurveySubQuestions> subQuestions) {
			this.subQuestions = subQuestions;
		}

		public static class SurveySubQuestions {
			private String subQuesLabel;
			private String subQuesValue;
			private String ansLabel;
			private String ansValue;
			
	        public String getsubQuesLabel() {
				return subQuesLabel;
			}

			public void setsubQuesLabel(String subQuesLabel) {
				this.subQuesLabel = subQuesLabel;
			}

			public String getsubQuesValue() {
				return subQuesValue;
			}

			public void setsubQuesValue(String subQuesValue) {
				this.subQuesValue = subQuesValue;
			}
			
//			public String getAnsLabel() {
//				return ansLabel;
//			}
//
//			public void setAnsLabel(String attLabel) {
//				this.ansLabel = attLabel;
//			}
//
//			public String getAnsValue() {
//				return ansValue;
//			}
//
//			public void setAnsValue(String ansValue) {
//				this.ansValue = ansValue;
//			}
//		}
		}
}
