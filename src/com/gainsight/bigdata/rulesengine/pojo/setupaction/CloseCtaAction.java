/**
 * 
 */
package com.gainsight.bigdata.rulesengine.pojo.setupaction;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * @author Abhilash Thaduka
 *
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class CloseCtaAction {

	private String type;
	private String source;
	private String reason;
	private String setCtaStatusTo;
	private String comments;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSource() {
		return source;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getSetCtaStatusTo() {
		return setCtaStatusTo;
	}

	public void setSetCtaStatusTo(String setCtaStatusTo) {
		this.setCtaStatusTo = setCtaStatusTo;
	}

}
