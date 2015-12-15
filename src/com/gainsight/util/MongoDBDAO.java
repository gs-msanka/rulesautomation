package com.gainsight.util;

import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.MongoUtil;
import com.mongodb.BasicDBObject;

import org.bson.Document;
import org.bson.types.Binary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giribabu on 06/08/15.
 */
public class MongoDBDAO  {

    private CryptHandler cryptHandler = CryptHandler.getInstance();
    private static final String COLLECTION_MASTER   = "collectionmaster";
    private static final String TENANT_MASTER      = "tenantmaster";
    Application application = new Application();

    public MongoUtil mongoUtil = new MongoUtil();


    public MongoDBDAO(String host, int port, String userName, String password, String database) {
        mongoUtil.createConnection(host, port, userName, password, database);
    }

    //Just in case to test locally.
    public static void main(String[] args) {
        MongoDBDAO mongoDBDAO = new  MongoDBDAO("54.204.251.136", 27017, "mdaqa02", "fgFByS5D", "test_gsglobaldb");
        //MongoDBDAO mongoDBDAO = new  MongoDBDAO("52.0.148.18", 27017, "gsuser", "T4Fa36Hr", "gsglobaldb");
        //MongoDBDAO mongoDBDAO = new  MongoDBDAO("52.0.148.18", 27017, "gsuser", "T4Fa36Hr", "gsglobaldb");
        //mongoDBDAO.updateCollectionDBStoreType("e5a05708-4337-4d04-821e-82f454b1a746", "99281caf-e4cd-4a85-b2f0-4b07a9a49e9b", DBStoreType.POSTGRES);
    }
    
    

    /**
     * Updated the tenant details with the DB details of tenant, Schema, Data, Postgres, RedShift.
     * @param tenantId - TenantId.
     * @return - Tenant Details updated with all the existing data base details.
     */
    public TenantDetails getAllDBDetails(String tenantId) {
        TenantDetails tenantDetails = new TenantDetails();
        Log.info("Updating db detail");

        tenantDetails.setSchemaDBDetail(getSchemaDBDetail(tenantId));
        tenantDetails.setDataDBDetail(getDataDBDetail(tenantId));
        tenantDetails.setPostgresDBDetail(getPostgresDetail(tenantId));
        tenantDetails.setRedshiftDBDetail(getRedShiftDBDetail(tenantId));

        Log.info("Updated all db details.");
        return  tenantDetails;
    }

    /**
     * Gets the Schema db details for the tenant.
     *
     * @param tenantId - Tenant Id
     * @return DBDetails.
     */
    public TenantDetails.DBDetail getSchemaDBDetail(String tenantId) {
        return getDBDetailsUpdated(tenantId, "schemaDBDetail");
    }

    /**
     * Gets the Data db details for the tenant.
     *
     * @param tenantId - Tenant Id
     * @return DBDetails.
     */
    public TenantDetails.DBDetail getDataDBDetail(String tenantId) {
        return getDBDetailsUpdated(tenantId, "dataDBDetail");
    }

    /**
     * Gets the postgres db details for the tenant.
     *
     * @param tenantId - Tenant Id
     * @return DBDetails.
     */
    public TenantDetails.DBDetail getPostgresDetail(String tenantId) {
        return getDBDetailsUpdated(tenantId, "postgresDBDetail");
    }

    /**
     * Gets the redshift db details for the tenant.
     *
     * @param tenantId - Tenant Id
     * @return DBDetails.
     */
    public TenantDetails.DBDetail getRedShiftDBDetail(String tenantId){
        return getDBDetailsUpdated(tenantId, "redshiftDBDetail");
    }

    /**
     * Updated the tenant details with the db - details stored in the server tenantmaster collection.
     *
     * @param tenantId - TenantId
     * @param dbNameDetail - DB Detail Name expected value {schemaDBDetail,dataDBDetail, postgresDBDetail, redshiftDBDetail }
     * @return
     */
    public TenantDetails.DBDetail getDBDetailsUpdated(String tenantId, String dbNameDetail) {
        Log.info("Printing ::: " + dbNameDetail);
        Document dbDocument = mongoUtil.getFirstRecord(TENANT_MASTER, new Document("TenantId", tenantId));
        if(dbDocument == null) {
            throw new RuntimeException("Failed to find the tenant record");
        }

        Document basicDBDetail =   (Document)dbDocument.get(dbNameDetail);
        if(basicDBDetail == null) {
            Log.info(dbNameDetail +" Not Found for tenant");
            return null;
        }
        Binary dbNameByte = (Binary)basicDBDetail.get("dbName");
        Binary sslEnabledByte = (Binary) basicDBDetail.get("sslEnabled");
        String dbName = cryptHandler.decrypt(dbNameByte.getData());
        String ssLEnabled =   cryptHandler.decrypt(sslEnabledByte.getData());

        Log.info("DB Name       : " + dbName);
        Log.info("SSL Enabled   : " + ssLEnabled);

        TenantDetails.DBDetail dbDetail = new TenantDetails.DBDetail();
        dbDetail.setDbName(dbName);
        dbDetail.setSslEnabled(Boolean.valueOf(ssLEnabled));

        List<Document> basicDBServerDetails =(ArrayList) basicDBDetail.get("dbServerDetails");
        List<TenantDetails.DBServerDetail> dbServerDetails = new ArrayList<>();

        for(int i=0; i< basicDBServerDetails.size(); i++) {
            TenantDetails.DBServerDetail dbServerDetail = new TenantDetails.DBServerDetail();
            Document dbObject = basicDBServerDetails.get(i);
            Binary hostByte = (Binary) dbObject.get("host");
            Binary userNameByte = (Binary) dbObject.get("userName");
            Binary passwordByte = (Binary) dbObject.get("password");
            String host = cryptHandler.decrypt(hostByte.getData());
            String userName = cryptHandler.decrypt(userNameByte.getData());
            String password = cryptHandler.decrypt(passwordByte.getData());
            Log.info("User name : "+userName);
            Log.info("Password  : " + password);
            Log.info("Host      : " + host);
            dbServerDetail.setUserName(userName);
            dbServerDetail.setPassword(password);
            dbServerDetail.setHost(host);
            dbServerDetails.add(dbServerDetail);
        }
        dbDetail.setDbServerDetails(dbServerDetails);

        return dbDetail;
    }


    /**
     * Updates the collection master data base type.
     * @param  tenantId
     * @param collectionId
     * @param dbStoreType
     * @return true - on successful change in db store type, else false and prints the errors.
     */
    public boolean updateCollectionDBStoreType(String tenantId, String collectionId, DBStoreType dbStoreType) {
        Log.info("Updating collection db store type to : " + dbStoreType + " for collection : " + collectionId);

        Document document = mongoUtil.getFirstRecord(COLLECTION_MASTER, new Document().append("TenantId", tenantId).append("CollectionDetails.CollectionID", collectionId));
        Log.info("Document to Update :" +document);
        if(document ==null) {
            throw new RuntimeException("Failed to get collection master record.");
        }

        Document updateDocument = new Document();
        updateDocument.append("$set", new Document().append("CollectionDetails.dataStoreType", dbStoreType.name()));
        Log.info("To Update : "+updateDocument);

        return mongoUtil.updateSingleRecord(COLLECTION_MASTER, document, updateDocument);
    }

    /**
     * Updates the collection master data base store type.
     * @param  tenantId
     * @param collectionName
     * @param dbStoreType
     * @return true - on successful change in db store type, else false and prints the errors.
     */
    public boolean updateCollectionDBStoreTypeByCollectionName(String tenantId, String collectionName, DBStoreType dbStoreType) {
        Log.info("Updating collection db store type to : " +dbStoreType + " for collection : " +collectionName);

        Document document = mongoUtil.getFirstRecord(COLLECTION_MASTER, new Document().append("TenantId", tenantId).append("CollectionDetails.CollectionName", collectionName));
        if(document ==null) {
            throw new RuntimeException("Failed to get collection master record.");
        }

        Document updateDocument = new Document();
        updateDocument.append("$set", new Document().append("CollectionDetails.dataStoreType", dbStoreType.name()));

        return mongoUtil.updateSingleRecord(COLLECTION_MASTER, document, updateDocument);
    }

    /**
     * Updates the collection master data base store type.
     * @param  tenantId
     * @param dbCollectionName
     * @param dbStoreType
     * @return true - on successful change in db store type, else false and prints the errors.
     */
    public boolean updateCollectionDBStoreTypeByDBCollectionName(String tenantId, String dbCollectionName, DBStoreType dbStoreType) {
        Log.info("Updating collection db store type to : " +dbStoreType + " for collection : " +dbCollectionName);

        Document document = mongoUtil.getFirstRecord(COLLECTION_MASTER, new Document().append("TenantId", tenantId).append("CollectionDetails.dbCollectionName", dbCollectionName));
        if(document ==null) {
            throw new RuntimeException("Failed to get collection master record.");
        }

        Document updateDocument = new Document();
        updateDocument.append("$set", new Document().append("CollectionDetails.dataStoreType", dbStoreType.name()));

        return mongoUtil.updateSingleRecord(COLLECTION_MASTER, document, updateDocument);
    }
    
    /**
     * Deletes all Rrecord from mongo collection based on tenantID
     * @param tenantID Tenant id
     * @param mongoCollection Collection name
     */
    public boolean deleteAllRecordsFromMongoCollectionBasedOnTenantID(String tenantID, String mongoCollection){
    	BasicDBObject query = new BasicDBObject();
		query.put("tenantId", tenantID);
		return mongoUtil.removeMany(mongoCollection, query);
    }
    
    /**
     * Deletes all Rrecord from mongo collection from collection master
     * @param tenantID Tenant id
     * @param mongoCollection Collection name
     */
    public boolean deleteCollectionSchemaFromCollectionMaster(String tenantID, String mongoCollection){
		BasicDBObject andQuery = new BasicDBObject();
		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
		obj.add(new BasicDBObject("TenantId", tenantID));
		andQuery.put("$and", obj);
        return mongoUtil.removeMany(mongoCollection, andQuery);
    }
    
	/**
	 * Method to delete a particular document based on collectionName from collectionMaster
	 * @param tenantID - Tenant id
	 * @param mongoCollection - collectionName
	 * @param documentMatcher - collectionName Matcher
	 * @return
	 */
	public boolean deleteMongoDocumentFromCollectionMaster(String tenantID, String mongoCollection, String documentMatcher) {
		Document whereQuery = new Document();
		whereQuery.put("TenantId", tenantID);
		whereQuery.put("CollectionDetails.CollectionName", new Document("$regex", documentMatcher).append("$options", "i"));
		Log.info(whereQuery.toString());
		return mongoUtil.removeMany(mongoCollection, whereQuery);
    }
}
