package com.gainsight.bigdata.rulesengine.bean.RuleSetup;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Created by Giribabu on 04/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class TriggerCriteria {

    private String whereLogic;
    private String refField;
    private String timeIdentifier;
    private List<Field> select;
    private List<CalculatedField> calculatedFields;
    private String collectionId;
    private List<Criteria> criteria;
    private int preventDuplicateDays;

    public int getPreventDuplicateDays() {
        return preventDuplicateDays;
    }

    public void setPreventDuplicateDays(int preventDuplicateDays) {
        this.preventDuplicateDays = preventDuplicateDays;
    }

    public String getWhereLogic() {
        return whereLogic;
    }

    public void setWhereLogic(String whereLogic) {
        this.whereLogic = whereLogic;
    }

    public String getRefField() {
        return refField;
    }

    public void setRefField(String refField) {
        this.refField = refField;
    }

    public String getTimeIdentifier() {
        return timeIdentifier;
    }

    public void setTimeIdentifier(String timeIdentifier) {
        this.timeIdentifier = timeIdentifier;
    }

    public List<Field> getSelect() {
        return select;
    }

    public void setSelect(List<Field> select) {
        this.select = select;
    }

    public List<CalculatedField> getCalculatedFields() {
        return calculatedFields;
    }

    public void setCalculatedFields(List<CalculatedField> calculatedFields) {
        this.calculatedFields = calculatedFields;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public List<Criteria> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<Criteria> criteria) {
        this.criteria = criteria;
    }
}
