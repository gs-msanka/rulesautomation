package com.gainsight.bigdata.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailContent {

	private String from_name;

	public String getFrom_name() {
		return from_name;
	}

	public void setFrom_name(String from_name) {
		this.from_name = from_name;
	}

	public String getFrom_mail() {
		return from_mail;
	}

	public void setFrom_mail(String from_mail) {
		this.from_mail = from_mail;
	}

	public String getTo_name() {
		return to_name;
	}

	public void setTo_name(String to_name) {
		this.to_name = to_name;
	}

	public String getTo_mail() {
		return to_mail;
	}

	public void setTo_mail(String to_mail) {
		this.to_mail = to_mail;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	private String from_mail;
	private String to_name;
	private String to_mail;
	private String text;
	private String html;
	private String subject;
	// private String date;

	public boolean equals(EmailContent ec) {
		return from_mail.equals(ec.from_mail) && from_name.equals(ec.from_name) && to_name.equals(ec.to_name)
				&& to_mail.equals(ec.to_mail) && text.equals(ec.text) && html.equals(ec.html)
				&& subject.equals(ec.subject);
	}

}
