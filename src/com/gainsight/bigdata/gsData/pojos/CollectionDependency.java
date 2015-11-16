package com.gainsight.bigdata.gsData.pojos;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by Giribabu on 01/10/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectionDependency {

    private String createdBy;
    private String createdByName;
    private String modifiedBy;
    private String modifiedByName;
    private String collectionId;
    private String entityType;
    private String entityId;
    private String entityName;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getModifiedByName() {
        return modifiedByName;
    }

    public void setModifiedByName(String modifiedByName) {
        this.modifiedByName = modifiedByName;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @Override
    public String toString() {
        return "CollectionMasterReference{" +
                "createdBy='" + createdBy + '\'' +
                ", createdByName='" + createdByName + '\'' +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", modifiedByName='" + modifiedByName + '\'' +
                ", collectionId='" + collectionId + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", entityName='" + entityName + '\'' +
                '}';
    }
}
