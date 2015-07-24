package com.gainsight.bigdata.TouchHub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.testdriver.Log;
import com.sforce.soap.metadata.CustomField;
import com.sforce.soap.metadata.CustomObject;
import com.sforce.soap.metadata.DeleteResult;
import com.sforce.soap.metadata.DeploymentStatus;
import com.sforce.soap.metadata.EncryptedFieldMaskChar;
import com.sforce.soap.metadata.EncryptedFieldMaskType;
import com.sforce.soap.metadata.Error;
import com.sforce.soap.metadata.FieldType;
import com.sforce.soap.metadata.Metadata;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.metadata.Picklist;
import com.sforce.soap.metadata.PicklistValue;
import com.sforce.soap.metadata.RemoteSiteSetting;
import com.sforce.soap.metadata.SaveResult;
import com.sforce.soap.metadata.SharingModel;
import com.sforce.ws.ConnectionException;

/**
 * Sample Metadata Util with different Functions focused on creation of Objects
 * and Fields
 * 
 * Made it Singleton - Figure out why
 * @author Sunand
 *
 */
public class CreateCTA {

	private static CreateCTA client = null;
	MetadataConnection metadataConnection;
	
	private CreateCTA() {}
	
	private CreateCTA (MetadataConnection connection) {
		this.metadataConnection = connection;
	}

	public static CreateCTA createDefault(MetadataConnection metadataConnection) {
		if(client != null) return client;
		else {
			if(metadataConnection == null) return null;
			client = new CreateCTA(metadataConnection);
			return client;
		}
	}

	public void createRemoteSiteSetting(String fullName, String url) throws Exception {
		Metadata[] metadata = new Metadata[1];
		RemoteSiteSetting remoteSiteSetting = new RemoteSiteSetting();
		remoteSiteSetting.setDescription("Created From Api");
		remoteSiteSetting.setFullName(fullName);
		remoteSiteSetting.setIsActive(true);
		remoteSiteSetting.setUrl(url);
		metadata[0] = remoteSiteSetting;
		createAndCheckStatus(metadata);
	}

	public void createMasterDetailRelationField(String objName, String fieldName, String relatedTo) throws Exception {
		Metadata[] metadata = new Metadata[1];
		CustomField custField = new CustomField();
		custField.setType(FieldType.MasterDetail);
		custField.setLabel(fieldName);
		custField.setReferenceTo(relatedTo);
		custField.setRelationshipName(StringUtils.stripEnd(objName, "_c") + "Objects");
		custField.setFullName(objName + "." + fieldName.replaceAll(" ", "_") + "__c");
		metadata[0] = custField;
		createAndCheckStatus(metadata);
	}

	public void createFormulaFields(String objName, List<HashMap<String, String>> formulafieldsList) throws Exception {
		Metadata[] metadata = new Metadata[formulafieldsList.size()];
		int i = 0;
		for (HashMap<String, String> testData : formulafieldsList) {
			CustomField custField = new CustomField();
			if (testData.get("Type").equals("CheckBox")) {
				custField.setType(FieldType.Checkbox);
			} else if (testData.get("Type").equals("Currency")) {
				custField.setType(FieldType.Currency);
				custField.setPrecision(2);
				custField.setScale(18);
			} else if (testData.get("Type").equals("Date")) {
				custField.setType(FieldType.Date);
			} else if (testData.get("Type").equals("DateTime")) {
				custField.setType(FieldType.DateTime);
			} else if (testData.get("Type").equals("Number")) {
				custField.setType(FieldType.Number);
				custField.setPrecision(2);
				custField.setScale(18);
			} else if (testData.get("Type").equals("Percent")) {
				custField.setType(FieldType.Percent);
				custField.setPrecision(2);
				custField.setScale(18);
			} else if (testData.get("Type").equals("Text")) {
				custField.setType(FieldType.Text);
			}
			custField.setFormula(testData.get("Formula"));
			custField.setLabel(testData.get("FieldName"));
			custField.setFullName(objName + "." + testData.get("FieldName").trim().replaceAll(" ", "_") + "__c");
			custField.setDescription(testData.get("Description"));
			custField.setInlineHelpText(testData.get("HelpText"));
			metadata[i] = custField;
			i++;
		}
		createAndCheckStatus(metadata);
	}

	 /**
     * Make sure not more than 10 objects are referenced in one call - salesforce limitation.
     * @param objName
     * @param fields
      *
     */
    public void deleteFields(String objName, String[] fields) {
        try {
            String[] fieldToDelete = new String[fields.length];
            for(int i=0; i< fields.length; i++) {
                fieldToDelete[i]=objName+fields[i];
            }
            DeleteResult[] results = metadataConnection.deleteMetadata(
                    "CustomField", fieldToDelete);
            for (DeleteResult r : results) {
                if (r.isSuccess()) {
                    Log.info("Deleted component: " + r.getFullName());
                } else {
                    Log.info("Errors were encountered while deleting "
                                    + r.getFullName());
                    for (Error e: r.getErrors()) {
                        Log.error("Error message: " + e.getMessage());
                        Log.error("Status code: " + e.getStatusCode());
                    }
                }
            }
        } catch (ConnectionException ce) {
            ce.printStackTrace();
        }
    }

	public void createFields(String objName, String[] fields, boolean isCheckBox, boolean isPhone, boolean isUrl)
			throws Exception {
		Metadata[] metadata = new Metadata[fields.length];
		int i = 0;
		for (String field : fields) {
			CustomField custField = new CustomField();
			if (isCheckBox) {
				custField.setType(FieldType.Checkbox);
				custField.setDefaultValue("false");
			} else if (isPhone) {
				custField.setType(FieldType.Phone);
			} else if (isUrl) {
				custField.setType(FieldType.Url);
			}
			custField.setLabel(field);
			custField.setFullName(objName + "." + field.trim().replaceAll(" ", "_") + "__c");
			custField.setDescription("This field is created from metadata api script");

			metadata[i] = custField;
			i++;
		}
		createAndCheckStatus(metadata);
	}

	public void createPickListField(String objName, HashMap<String, String[]> pickListFields, boolean isMutipickList)
			throws Exception {
		Metadata[] metadata = new Metadata[pickListFields.size()];
		int i = 0;
		Iterator<String> itr = pickListFields.keySet().iterator();
		String[] pkValues = null;
		String fieldName;
		while (itr.hasNext()) {
			fieldName = (String) itr.next();
			pkValues = pickListFields.get(fieldName);
			CustomField custField = new CustomField();
			if (isMutipickList) {
				custField.setType(FieldType.MultiselectPicklist);
				custField.setVisibleLines(4);
			} else {
				custField.setType(FieldType.Picklist);
			}

			custField.setLabel(fieldName);
			custField.setFullName(objName + "." + fieldName.trim().replaceAll(" ", "_") + "__c");
			custField.setDescription("This field is created from meta data script");
			Picklist p = new Picklist();
			PicklistValue[] pkValueArray = new PicklistValue[pkValues.length];
			int j = 0;
			for (String value : pkValues) {
				PicklistValue picklistValue = new PicklistValue();
				picklistValue.setFullName(value.trim());
				pkValueArray[j] = picklistValue;
				j++;
			}
			p.setPicklistValues(pkValueArray);
			custField.setPicklist(p);
			metadata[i] = custField;
			i++;
		}
		createAndCheckStatus(metadata);
	}

	public void createTextFields(String objName, String[] fields, boolean isExternalID, boolean isUnique,
			boolean isTextField, boolean isTextArea, boolean isTextRich) throws Exception {
		Metadata[] metadata = new Metadata[fields.length];
		int i = 0;
		for (String field : fields) {
			CustomField custField = new CustomField();
			if (isTextField) {
				custField.setType(FieldType.Text);
				custField.setLength(250);
			} else if (isTextArea) {
				custField.setType(FieldType.TextArea);
			} else if (isTextRich) {
				custField.setType(FieldType.Html);
				custField.setLength(32768);
				custField.setVisibleLines(10);
			}
			if (isExternalID) {
				custField.setExternalId(true);
			}
			if (isUnique) {
				custField.setUnique(true);
				custField.setCaseSensitive(false);
			}
			custField.setLabel(field);
			custField.setFullName(objName + "." + field.replaceAll(" ", "_") + "__c");
			metadata[i] = custField;
			i++;
		}
		createAndCheckStatus(metadata);
	}

	public void createCurrencyField(String objName, String[] fields) throws Exception {
		Metadata[] metadata = new Metadata[fields.length];
		int i = 0;
		for (String field : fields) {
			CustomField custField = new CustomField();
			custField.setType(FieldType.Currency);
			custField.setLabel(field);
			custField.setScale(2);
			custField.setPrecision(18);
			custField.setFullName(objName + "." + field.replaceAll(" ", "_") + "__c");
			metadata[i] = custField;
			i++;
		}
		createAndCheckStatus(metadata);
	}

	public void createDateField(String objName, String[] fields, boolean isDateTime) throws Exception {
		Metadata[] metadata = new Metadata[fields.length];
		int i = 0;
		for (String field : fields) {
			CustomField custField = new CustomField();
			if (isDateTime) {
				custField.setType(FieldType.DateTime);
			} else {
				custField.setType(FieldType.Date);
			}
			custField.setLabel(field);
			custField.setFullName(objName + "." + field.replaceAll(" ", "_") + "__c");
			metadata[i] = custField;
			i++;
		}
		createAndCheckStatus(metadata);
	}

	public void createNumberField(String objName, String[] fields, boolean isPercentage) throws Exception {
		Metadata[] metadata = new Metadata[fields.length];
		int i = 0;
		for (String field : fields) {
			CustomField custField = new CustomField();
			if (isPercentage) {
				custField.setType(FieldType.Percent);
			} else {
				custField.setType(FieldType.Number);
			}
			custField.setLabel(field);
			custField.setScale(2);
			custField.setPrecision(18);
			custField.setFullName(objName + "." + field.replaceAll(" ", "_") + "__c");
			metadata[i] = custField;
			i++;
		}
		createAndCheckStatus(metadata);
	}

	public void createCustomObject(String name) throws ConnectionException {
        CustomObject co = new CustomObject();
        co.setFullName(name + "__c");
        co.setDeploymentStatus(DeploymentStatus.Deployed);
        co.setDescription("Created by the Metadata API");
        co.setEnableActivities(true);
        co.setLabel(name + " Object");
        co.setPluralLabel(co.getLabel() + "s");
        co.setSharingModel(SharingModel.ReadWrite);
        CustomField nf = new CustomField();
        nf.setType(FieldType.Text);
        nf.setLabel("Name");
        co.setNameField(nf);
        SaveResult[] results = metadataConnection.createMetadata(new Metadata[]{co});
        for (SaveResult r : results) {
            if (r.isSuccess()) {
                Log.info("Created component: " + r.getFullName());
            } else {
                Log.info("Errors were encountered while creating " + r.getFullName());
                for (Error e : r.getErrors()) {
                    Log.error("Error message: " + e.getMessage());
                    Log.error("Status code: " + e.getStatusCode());
                }
            }
        }
    }

	public void deleteCustomObject(String name) throws ConnectionException {
        try {
            DeleteResult[] results = metadataConnection.deleteMetadata("CustomObject", new String[]{name});
            for (DeleteResult r : results) {
                if (r.isSuccess()) {
                    Log.info("Deleted component: " + r.getFullName());
                } else {
                    Log.info("Errors were encountered while deleting " + r.getFullName());
                    for (Error e : r.getErrors()) {
                        Log.error("Error message: " + e.getMessage());
                        Log.error("Status code: " + e.getStatusCode());
                    }
                }
            }
        } catch (ConnectionException ce) {
            ce.printStackTrace();
        }
    }

	public void createAndCheckStatus(Metadata[] metadata)  {
        SaveResult[] results = null;
        try {
            results  = metadataConnection.createMetadata(metadata);
            for (SaveResult r : results) {
                if (r.isSuccess()) {
                    Log.info("Created component: " + r.getFullName());
                } else {
                    Log.info("Errors were encountered while creating " + r.getFullName());
                    for (Error e : r.getErrors()) {
                        Log.error("Error message: " + e.getMessage());
                        Log.error("Status code: " + e.getStatusCode());
                    }
                }
            }
        } catch (ConnectionException e) {
            Log.error(e.getLocalizedMessage(), e);
            throw new RuntimeException("Checking Field Creation Failed " +e.getLocalizedMessage());
        }
    }
	
	public void createLookupField(String objName, String[] fields, String[] Reference)  {
        Metadata[] metadata = new Metadata[fields.length];
        int i=0;
        for(String field : fields) {
            CustomField custField = new CustomField();
          if(Reference!=null) {
                custField.setType(FieldType.Lookup);
                custField.setReferenceTo(Reference[0]); //Reference to which object
                custField.setRelationshipName(Reference[1]); //What is the RelationName
            }
            
            custField.setLabel(field);         
            custField.setFullName(objName+"."+field.trim().replaceAll(" ", "_")+"__c");
            custField.setDescription("This field is created from metadata api script");

            metadata[i] = custField;
            i++;
        }
        createAndCheckStatus(metadata);
    }
	
	public void createEmailField(String objName, String[] fields)  {
        Metadata[] metadata = new Metadata[fields.length];
        int i=0;
        for(String field : fields) {
            CustomField custField = new CustomField();
            custField.setType(FieldType.Email);
            custField.setLabel(field);         
            custField.setFullName(objName+"."+field.trim().replaceAll(" ", "_")+"__c");
            custField.setDescription("This field is created from metadata api script");

            metadata[i] = custField;
            i++;
        }
        createAndCheckStatus(metadata);
    }
	
	public void createEncryptedTextFields(String objName, String[] fields) {
        Metadata[] metadata = new Metadata[fields.length];
        int i=0;
        for(String field : fields) {
            CustomField custField = new CustomField();           
            custField.setType(FieldType.EncryptedText);            
            custField.setLength(50);            
            custField.setMaskChar(EncryptedFieldMaskChar.asterisk);
            custField.setMaskType(EncryptedFieldMaskType.lastFour);
            custField.setLabel(field);
            custField.setFullName(objName+"."+field.replaceAll(" ", "_")+"__c");
            metadata[i] = custField;
            i++;
        }
        createAndCheckStatus(metadata);
    }
	
	public void createAutoNumberFields(String objName, String[] fields) {
        Metadata[] metadata = new Metadata[fields.length];
        int i=0;
        for(String field : fields) {
            CustomField custField = new CustomField();           
            custField.setType(FieldType.AutoNumber);            
            custField.setStartingNumber(0);
            custField.setLabel(field);
            custField.setFullName(objName+"."+field.replaceAll(" ", "_")+"__c");
            metadata[i] = custField;
            i++;
        }
        createAndCheckStatus(metadata);
    }
	
	
	
}
