package com.gainsight.bigdata.scorecards2.pojos;

/**
 * Created by nyarlagadda on 23/02/16.
 */
public class Status {

    private Integer measure;
    private Integer rollup;
    private Integer group;

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public Integer getMeasure() {
        return measure;
    }

    public void setMeasure(Integer measure) {
        this.measure = measure;
    }

    public Integer getRollup() {
        return rollup;
    }

    public void setRollup(Integer rollup) {
        this.rollup = rollup;
    }
}
