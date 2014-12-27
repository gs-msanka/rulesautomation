package com.gainsight.sfdc.workflow.pojos;

/**
 * Created by gainsight on 16/12/14.
 */
public class Playbook {

    private String type = "All";
    private String name = "This is All Playbook";
    private String comments = "Here are my comments";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
