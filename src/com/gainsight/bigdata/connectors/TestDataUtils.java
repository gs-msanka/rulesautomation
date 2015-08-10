package com.gainsight.bigdata.connectors;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gainsight.bigdata.connectors.pojo.AccountProperties;
import com.gainsight.sfdc.util.DateUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

public class TestDataUtils {
	DB db = null;
	static int timeDiff = -1;
	static String TEST_DATA_FILE = "testdata/newstack/connectors/connectorsdata.json";
	String collectionName = "connectorsdata";

	Map<String, String> source2TargetMap = new HashMap<String, String>();

	@SuppressWarnings("deprecation")
	public static void importJSONFileToDBUsingJavaDriver(String pathToFile, DB db, String collectionName) {
		// open file
		FileInputStream fstream = null;
		try {
			fstream = new FileInputStream(pathToFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("file not exist, exiting");
			return;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		// read it line by line
		String strLine;
		DBCollection newColl = db.getCollection(collectionName);
		newColl.drop();
		List<DBObject> data = new ArrayList<DBObject>();
		try {
			while ((strLine = br.readLine()) != null) {

				// convert line by line to BSON
				DBObject bson = (DBObject) JSON.parse(strLine);

				if (timeDiff == -1) {
					Date edate = (Date) bson.get("edate");
					Date etimestamp = (Date) bson.get("etimestamp");
					Date currentDate = new Date();
					timeDiff = currentDate.getDate() - edate.getDate();
					if (timeDiff > 0) {
						Calendar c = Calendar.getInstance();
						c.setTime(edate);
						c.add(Calendar.DATE, timeDiff);
						bson.put("edate", c.getTime());
						bson.put("ldate", c.getTime());
						c.setTime(etimestamp);
						c.add(Calendar.DATE, timeDiff);
						bson.put("etimestamp", c.getTime());
						bson.put("ltimestamp", c.getTime());
					}
				}
				data.add(bson);
				try {
					if (data.size() >= 100) {
						newColl.insert(data);
						data.clear();
					}
				} catch (MongoException e) {
					e.printStackTrace();
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public void setSource2TargetDates(Date sourceDate) {
		String format = "yyyy-MM-dd";
		if (timeDiff == -1) {
			Date currentDate = new Date();
			timeDiff = currentDate.getDate() - sourceDate.getDate();
			for (int i = 0; i < 30; i++) {
				String sourceAsString = DateUtil.addDays(sourceDate, -i, format);
				String targetAsString = DateUtil.addDays(currentDate, -i, format);
				source2TargetMap.put(sourceAsString, targetAsString);
			}
		}
	}

	public DB getRemoteDBConnection() {
		if (db != null) {
			return db;
		}
		/*String client = "kahana.mongohq.com";
		int port = 10085;
		String dbName = "test_automation";
		String user = "mani";
		// char[] password = new char[] { 'T', '4', 'F', 'a', '3', '6', 'H', 'r'
		// };
		char[] password = new char[] { 'j', 'b', 'a', 'r', 'a', '1', '2', '3' };

		MongoClient mongo;
		try {
			mongo = new MongoClient(client, port);
			db = mongo.getDB(dbName);
			boolean authenticated = db.authenticate(user, password);

			if (authenticated) {
				System.out.println("Successfully logged in to MongoDB!");
			} else {
				System.out.println("Invalid username/password");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} */
		return db;

	}

	public void loadTestData() {
		TestDataUtils utils = new TestDataUtils();
		DB db = utils.getRemoteDBConnection();
		TestDataUtils.importJSONFileToDBUsingJavaDriver(TEST_DATA_FILE, db, collectionName);
	}

	public AccountProperties getAccountProperties(String accountName, boolean isFlipped) throws Exception {
		DB dbCon = new TestDataUtils().getRemoteDBConnection();
		AccountProperties accProp = new AccountProperties();
		String accountID;
		String dayAggCollection;
		String flippedColleciton;
		// Get AccountDetails ID
		DBObject object = mongoFind(dbCon, "accountDetail", "displayName", accountName);
		accountID = object.get("accountId").toString();
		accProp.setAccountName(accountName);
		accProp.setAccountDetailsID(accountID);
		dayAggCollection = ((DBObject) object.get("properties")).get("VIEW_FOR_BDA_COLLECTION_MASTER_ID").toString();
		accProp.setDayAggCollection(dayAggCollection);
		if (isFlipped) {
			String flippedMeasuresSyncInfoId = object.get("flippedMeasuresSyncInfoId").toString();
			object = mongoFind(dbCon, "syncInfo", "syncInfoId", flippedMeasuresSyncInfoId);
			String flipColDBName = ((DBObject) object.get("target")).get("name").toString();
			object = mongoFind(dbCon, "collectionmaster", "CollectionDetails.dbCollectionName", flipColDBName);
			flippedColleciton = ((DBObject) object.get("CollectionDetails")).get("CollectionID").toString();
			accProp.setFlippedColleciton(flippedColleciton);
		}
		return accProp;
	}
	
	public DBObject mongoFind(DB dbCon, String collectionName, String filterName, String filterValue)
			throws Exception {
		DBCollection collection = dbCon.getCollection(collectionName);
		BasicDBObject doc = new BasicDBObject(filterName, filterValue);
		DBCursor cursor = collection.find(doc);
		return (DBObject) cursor.next();
	}
	
	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();
		System.out.println((new SimpleDateFormat("yyyy-MM-dd")).format(Calendar.getInstance().getTime()));

		Date currentDate = new Date();
		cal.setTime(currentDate);
		cal.add(Calendar.DATE, -10);
		new TestDataUtils().setSource2TargetDates(cal.getTime());
	}
}
