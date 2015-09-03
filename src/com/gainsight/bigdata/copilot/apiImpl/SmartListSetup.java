package com.gainsight.bigdata.copilot.apiImpl;

import java.util.HashMap;
import java.util.List;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.copilot.smartlist.pojos.ActionInfo;
import com.gainsight.bigdata.copilot.smartlist.pojos.CollectionSchema;
import com.gainsight.bigdata.copilot.smartlist.pojos.TriggerCriteria;
import com.gainsight.bigdata.copilot.smartlist.pojos.ActionInfo.Query;
import com.gainsight.bigdata.copilot.smartlist.pojos.CollectionSchema.Column;
import com.gainsight.bigdata.copilot.smartlist.pojos.TriggerCriteria.Select;
import com.gainsight.testdriver.Log;

public class SmartListSetup extends NSTestBase{
	
	private String actionCriteria=null;
	private String trigerCriteria=null;
	
	public String getTrigerCriteria(HashMap<String, String> testData,
			CollectionSchema schema) throws Exception {
		TriggerCriteria[] triggerCriteria = mapper.readValue(
				testData.get("triggerCriteria"), TriggerCriteria[].class);
		String createdDateTime = null;
		String idDbName = null;
		String nameDbName = null;
		String emailDbName = null;
		String contactDbName = null;
		for (Column column : schema.getColumns()) {
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
			tg.setCollectionId(schema.getCollectionDetails().getCollectionId());
			select = tg.getSelect();
		}
		for (Select temp : select) {
			if (temp.getLabel().equalsIgnoreCase("ID")) {
				temp.setField(idDbName);
				temp.setFieldName(idDbName);
				temp.setEntity(schema.getCollectionDetails().getCollectionId());
				temp.setObjectName(schema.getCollectionDetails()
						.getCollectionId());
				temp.setCollectionId(schema.getCollectionDetails()
						.getCollectionId());
			}
		}

		for (Select name : select) {
			if (name.getLabel().equalsIgnoreCase("Name")) {
				name.setField(nameDbName);
				name.setFieldName(nameDbName);
				name.setEntity(schema.getCollectionDetails().getCollectionId());
				name.setObjectName(schema.getCollectionDetails()
						.getCollectionId());
				name.setCollectionId(schema.getCollectionDetails()
						.getCollectionId());
			}
		}
		for (Select email : select) {
			if (email.getLabel().equalsIgnoreCase("Email")) {
				email.setField(emailDbName);
				email.setFieldName(emailDbName);
				email.setEntity(schema.getCollectionDetails().getCollectionId());
				email.setObjectName(schema.getCollectionDetails()
						.getCollectionId());
				email.setCollectionId(schema.getCollectionDetails()
						.getCollectionId());
			}
		}
		for (Select contactID : select) {
			if (contactID.getLabel().equalsIgnoreCase("ContactID")) {
				contactID.setField(contactDbName);
				contactID.setFieldName(contactDbName);
				contactID.setEntity(schema.getCollectionDetails()
						.getCollectionId());
				contactID.setObjectName(schema.getCollectionDetails()
						.getCollectionId());
				contactID.setCollectionId(schema.getCollectionDetails()
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
			CollectionSchema schema) throws Exception {
		ActionInfo actionInfo = mapper.readValue(testData.get("ActionInfo"),
				ActionInfo.class);
		String dbName = null;
		String emailDbName = null;
		String contactDbName = null;
		for (Column column : schema.getColumns()) {
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
					schema.getCollectionDetails().getCollectionId());
			query.getExternalIdentifier().setParentObj(
					schema.getCollectionDetails().getCollectionId());

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
