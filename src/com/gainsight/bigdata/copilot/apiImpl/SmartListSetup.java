package com.gainsight.bigdata.copilot.apiImpl;

import java.util.HashMap;
import java.util.List;

import org.testng.Assert;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.copilot.smartlist.pojos.ActionInfo;
import com.gainsight.bigdata.copilot.smartlist.pojos.TriggerCriteria;
import com.gainsight.bigdata.copilot.smartlist.pojos.ActionInfo.Query;
import com.gainsight.bigdata.copilot.smartlist.pojos.TriggerCriteria.Select;
import com.gainsight.bigdata.dataload.apiimpl.DataLoadManager;
import com.gainsight.bigdata.dataload.enums.DataLoadStatusType;
import com.gainsight.bigdata.dataload.pojo.DataLoadStatusInfo;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.testdriver.Log;

public class SmartListSetup extends NSTestBase{
	
	private String actionCriteria=null;
	private String trigerCriteria=null;
	private DataLoadManager dataLoadManager;
	
	public String getTrigerCriteria(HashMap<String, String> testData,
			CollectionInfo collectionInfo) throws Exception {
		TriggerCriteria[] triggerCriteria = mapper.readValue(
				testData.get("triggerCriteria"), TriggerCriteria[].class);
		String createdDateTime = null;
		String idDbName = null;
		String nameDbName = null;
		String emailDbName = null;
		String contactDbName = null;
		for (com.gainsight.bigdata.pojo.CollectionInfo.Column column : collectionInfo.getColumns()) {
			if (column.getDisplayName().equalsIgnoreCase("CreatedDateTime")) {
				createdDateTime = column.getDbName();
			}
			if (column.getDisplayName().equalsIgnoreCase("ID")) {
				idDbName = column.getDbName();
			}
			if (column.getDisplayName().equalsIgnoreCase("Name")) {
				nameDbName = column.getDbName();
			}
			if (column.getDisplayName().equalsIgnoreCase("Email")) {
				emailDbName = column.getDbName();
			}
			if (column.getDisplayName().equalsIgnoreCase("ContactID")) {
				contactDbName = column.getDbName();
			}
		}
		List<Select> select = null;
		for (TriggerCriteria tg : triggerCriteria) {
			tg.setTimeIdentifier(createdDateTime);
			tg.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
			select = tg.getSelect();
		}
		for (Select temp : select) {
			if (temp.getLabel().equalsIgnoreCase("ID")) {
				temp.setField(idDbName);
				temp.setFieldName(idDbName);
				temp.setEntity(collectionInfo.getCollectionDetails().getCollectionId());
				temp.setObjectName(collectionInfo.getCollectionDetails()
						.getCollectionId());
				temp.setCollectionId(collectionInfo.getCollectionDetails()
						.getCollectionId());
			}
		}

		for (Select name : select) {
			if (name.getLabel().equalsIgnoreCase("Name")) {
				name.setField(nameDbName);
				name.setFieldName(nameDbName);
				name.setEntity(collectionInfo.getCollectionDetails().getCollectionId());
				name.setObjectName(collectionInfo.getCollectionDetails()
						.getCollectionId());
				name.setCollectionId(collectionInfo.getCollectionDetails()
						.getCollectionId());
			}
		}
		for (Select email : select) {
			if (email.getLabel().equalsIgnoreCase("Email")) {
				email.setField(emailDbName);
				email.setFieldName(emailDbName);
				email.setEntity(collectionInfo.getCollectionDetails().getCollectionId());
				email.setObjectName(collectionInfo.getCollectionDetails()
						.getCollectionId());
				email.setCollectionId(collectionInfo.getCollectionDetails()
						.getCollectionId());
			}
		}
		for (Select contactID : select) {
			if (contactID.getLabel().equalsIgnoreCase("ContactID")) {
				contactID.setField(contactDbName);
				contactID.setFieldName(contactDbName);
				contactID.setEntity(collectionInfo.getCollectionDetails()
						.getCollectionId());
				contactID.setObjectName(collectionInfo.getCollectionDetails()
						.getCollectionId());
				contactID.setCollectionId(collectionInfo.getCollectionDetails()
						.getCollectionId());
			}
		}
		System.out.println(triggerCriteria);
		Log.info("Trigger criteria is "
				+ mapper.writeValueAsString(triggerCriteria));
		trigerCriteria = mapper.writeValueAsString(triggerCriteria);
		return trigerCriteria;
	}

	public String getActionInfo(HashMap<String, String> testData,
			CollectionInfo collectionInfo) throws Exception {
		ActionInfo actionInfo = mapper.readValue(testData.get("ActionInfo"),
				ActionInfo.class);
		String dbName = null;
		String emailDbName = null;
		String contactDbName = null;
		for (com.gainsight.bigdata.pojo.CollectionInfo.Column column : collectionInfo.getColumns()) {
			if (column.getDisplayName().equalsIgnoreCase("ID")) {
				dbName = column.getDbName();
				Log.info(column.getDbName());
			}
			if (column.getDisplayName().equalsIgnoreCase("Email")) {
				emailDbName = column.getDbName();

			}
			if (column.getDisplayName().equalsIgnoreCase("ContactID")) {
				contactDbName = column.getDbName();

			}
		}
		for (Query query : actionInfo.getQueries()) {
			if (contactDbName != null && !contactDbName.isEmpty()) {
				query.getExternalIdentifier().setField(contactDbName);
				query.getExternalIdentifier().setUniqueName(contactDbName);

			} else {
				query.getExternalIdentifier().setField(dbName);
				query.getExternalIdentifier().setUniqueName(dbName);
			}
			query.getExternalIdentifier().setEntity(
					collectionInfo.getCollectionDetails().getCollectionId());
			query.getExternalIdentifier().setParentObj(
					collectionInfo.getCollectionDetails().getCollectionId());

		}
		// For Email strategy powerlist
		if (actionInfo.getRecipientStrategy().equalsIgnoreCase(
				"SPECIFIC_EMAIL_ADDRESSES")) {
			actionInfo.setRecipientFieldName(emailDbName);

		}
		Log.info("Action Info is " + mapper.writeValueAsString(actionInfo));
		actionCriteria = mapper.writeValueAsString(actionInfo);
		return actionCriteria;
	}
}
