package com.gainsight.bigdata.dataLoadConfiguartion.mapping;

import com.gainsight.bigdata.connectors.mapping.SysDefFieldInfo;
import com.gainsight.bigdata.dataLoadConfiguartion.pojo.accountdetails.*;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.testdriver.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Giribabu on 22/06/15.
 */
public class IdentifierMapper {

    static String ACCOUNT_IDENTIFIER_DB_NAME    = "gsaccountid";
    static String USER_IDENTIFIER_DB_NAME       = "gsuserid";
    static String EVENT_IDENTIFIER_DB_NAME      = "gsevent";
    static String TIME_IDENTIFIER_DB_NAME       = "gstimestamp";
    static String DEFAULT_AGG_FUNCTION          = "SUM";

    /**
     * Gets the Account Identifier.
     * @param column - Collection Column
     * @param targetDisplayName - Target displayName
     * @param properties
     * @param directLookup
     * @param digitConversionEnable
     * @return
     */
    public static Identifier getAccountIdentifier(CollectionInfo.Column column, String targetDisplayName, HashMap<String, String> properties, boolean directLookup, boolean digitConversionEnable) {
        if(column == null) {
            Log.error("Column Should not be NULL");
            throw new RuntimeException("Column Should not be NULL");
        }
        Identifier accountIdentifier= new Identifier();
        setSource(column.getDisplayName(), column.getDbName(), accountIdentifier);
        setTarget(targetDisplayName, ACCOUNT_IDENTIFIER_DB_NAME, new HashMap<String, String>(), accountIdentifier);
        accountIdentifier.setProperties(properties);
        accountIdentifier.setDirectLookup(directLookup);
        accountIdentifier.setDigitConversionEnable(digitConversionEnable);
        return accountIdentifier;
    }

    /**
     * Sets the Source properties with the values set.
     * @param displayName
     * @param dbName
     * @param identifier
     */
    public static void setSource(String displayName, String dbName, Identifier identifier) {
        Source source = new Source();
        source.setDisplayName(displayName);
        source.setDbName(dbName);
        identifier.setSource(source);
    }

    /**
     * Returns the source property will the values set.
     * @param displayName
     * @param dbName
     * @param type
     * @param objectName
     * @param properties
     * @return
     */
    public static Source getSource(String displayName, String dbName, String type, String objectName, HashMap<String, String> properties ) {
        Source source = new Source();
        source.setDisplayName(displayName);
        source.setDbName(dbName);
        source.setType(type);
        source.setObjectName(objectName);
        source.setProperties(properties);
        return source;
    }

    /**
     * Returns the Target with the properties set.
     * @param displayName
     * @param dbName
     * @param properties
     * @return
     */
    public static Target getTarget(String displayName, String dbName, HashMap<String, String> properties ) {
        Target target = new Target();
        target.setDisplayName(displayName);
        target.setDbName(dbName);
        target.setProperties(properties);
        return target;
    }

    /**
     * Sets the Target with the provided properties.
     * @param displayName
     * @param dbName
     * @param properties
     * @param identifier
     */
    public static void setTarget(String displayName, String dbName, HashMap<String, String> properties, Identifier identifier) {
        Target target = new Target();
        target.setDisplayName(displayName);
        target.setDbName(dbName);
        target.setProperties(properties);
        identifier.setTarget(target);
    }

    /**
     * Return the User Identifier with the provided properties.
     * @param column
     * @param targetDisplayName
     * @param properties
     * @param lookup
     * @param directLookup
     * @param digitConversionEnable
     * @return
     */
    public static Identifier getUserIdentifier(CollectionInfo.Column column, String targetDisplayName, HashMap<String, String> properties, boolean lookup, boolean directLookup, boolean digitConversionEnable) {
        if(column == null) {
            Log.error("Column Should not be NULL");
            throw new RuntimeException("Column Should not be NULL");
        }
        Identifier userIdentifier= new Identifier();
        setSource(column.getDisplayName(), column.getDbName(), userIdentifier);
        setTarget(targetDisplayName, USER_IDENTIFIER_DB_NAME, new HashMap<String, String>(), userIdentifier);
        userIdentifier.setProperties(properties);
        userIdentifier.setLookup(lookup);
        userIdentifier.setDirectLookup(directLookup);
        userIdentifier.setDigitConversionEnable(digitConversionEnable);
        return userIdentifier;
    }

    /**
     * Returns Event Identifier with properties set.
     * @param column
     * @param targetDisplayName
     * @return
     */
    public static Identifier getEventIdentifier(CollectionInfo.Column column, String targetDisplayName) {
        if(column == null) {
            Log.error("Column Should not be NULL");
            throw new RuntimeException("Column Should not be NULL");
        }
        Identifier accountIdentifier= new Identifier();
        setSource(column.getDisplayName(), column.getDbName(), accountIdentifier);
        setTarget(targetDisplayName, EVENT_IDENTIFIER_DB_NAME, new HashMap<String, String>(), accountIdentifier);
        return accountIdentifier;
    }

    /**
     * Return the Time identifier with the properties set.
     * @param column
     * @param targetDisplayName
     * @return
     */
    public static Identifier getTimeIdentifier(CollectionInfo.Column column, String targetDisplayName) {
        if(column == null) {
            Log.error("Column Should not be NULL");
            throw new RuntimeException("Column Should not be NULL");
        }
        Identifier accountIdentifier= new Identifier();
        setSource(column.getDisplayName(), column.getDbName(), accountIdentifier);
        setTarget(targetDisplayName, TIME_IDENTIFIER_DB_NAME, new HashMap<String, String>(), accountIdentifier);
        return accountIdentifier;
    }

    /**
     * Returns the Measure Mapping with the properties set.
     * @param column
     * @param targetDisplayName
     * @param aggFunc
     * @return
     */
    public static Mapping getMeasureMapping(CollectionInfo.Column column, String targetDisplayName, String aggFunc) {
        if(column == null) {
            Log.error("Column Should not be NULL");
            throw new RuntimeException("Column Should not be NULL");
        }
        Mapping mapping = new Mapping();
        mapping.setSource(getSource(column.getDisplayName(), column.getDbName(), null, null, new HashMap<String, String>()));

        HashMap<String, String> properties = new HashMap<>();
        properties.put("aggregationFunction", aggFunc !=null ? aggFunc : DEFAULT_AGG_FUNCTION);

        mapping.setTarget(getTarget(targetDisplayName != null ? targetDisplayName : column.getDisplayName(), "", properties));
        return mapping;
    }

    /**
     * Returns the CustomMapping with the Properties Set.
     * @param column
     * @param targetDisplayName
     * @param targetDBName
     * @return
     */
    public static Mapping getCustomMapping(CollectionInfo.Column column, String targetDisplayName, String targetDBName) {
        if(column == null) {
            Log.error("Column Should not be NULL");
            throw new RuntimeException("Column Should not be NULL");
        }
        Mapping mapping = new Mapping();
        mapping.setSource(getSource(column.getDisplayName(), column.getDbName(), "USAGE_FEED", "", new HashMap<String, String>()));
        mapping.setTarget(getTarget(targetDisplayName, targetDBName, new HashMap<String, String>()));
        return mapping;
    }

    /**
     * Returns the default SFSystemDefined Mappings.
     * @return
     */
    public static List<Mapping> getSFSystemDefined() {
        List<Mapping> systemDefined = new ArrayList<>();
        Mapping accNameMapping = new Mapping();
        accNameMapping.setSource(getSource("Account Name", "Name", "SFDC", "account", new HashMap<String, String>()));
        accNameMapping.setTarget(getTarget("Account Name", "gsaccountname", new HashMap<String, String>()));
        systemDefined.add(accNameMapping);

        Mapping userNameMapping = new Mapping();
        userNameMapping.setSource(getSource("Contact Name", "Name", "SFDC", "contact", new HashMap<String, String>()));
        userNameMapping.setTarget(getTarget("User Name", "gsusername", new HashMap<String, String>()));
        systemDefined.add(userNameMapping);

        Mapping userEmailMapping = new Mapping();
        userEmailMapping.setSource(getSource("Email", "Email", "SFDC", "contact", new HashMap<String, String>()));
        userEmailMapping.setTarget(getTarget("User Email", "gsuseremail", new HashMap<String, String>()));
        systemDefined.add(userEmailMapping);
        return systemDefined;
    }

    /**
     * Returns Default GSMappings.
     * @return
     */
    public static Mapping getGSDefinedMapping() {
        Mapping mapping = new Mapping();
        mapping.setSource(getSource("gssfdcaccountid", "gssfdcaccountid", "GS_DEFINED", "", new HashMap<String, String>()));
        mapping.setTarget(getTarget("gssfdcaccountid", "gssfdcaccountid", new HashMap<String, String>()));
        return mapping;
    }

    /**
     * Returns the Notification Details Pojo with all the values set.
     * @param successRecipients
     * @param failureRecipients
     * @return
     */
    public static NotificationDetails getNotificationDetails(String[] successRecipients, String[] failureRecipients) {
        NotificationDetails notificationDetails = new NotificationDetails();
        notificationDetails.setFailureRecipients(failureRecipients!=null ? failureRecipients : new String[0]);
        notificationDetails.setSuccessRecipients(successRecipients!=null ? successRecipients : new String[0]);
        return notificationDetails;
    }

    /**
     * Returns event measure mapping with all the properties set.
     * @param event
     * @param aggregationFunction
     * @param aggregationKey
     * @param flippedMeasureDisplayName
     * @param flippedMeasureDbName
     * @return
     */
    public static EventMeasureMapping getEventMeasureMapping(String event, String aggregationFunction, String aggregationKey,
                                                                           String flippedMeasureDisplayName, String flippedMeasureDbName) {
        EventMeasureMapping measureMapping = new EventMeasureMapping();
        measureMapping.setEvent(event);
        measureMapping.setAggregationFunction(aggregationFunction);
        measureMapping.setAggregationKey(aggregationKey);
        measureMapping.setFlippedMeasureDisplayName(flippedMeasureDisplayName);
        measureMapping.setFlippedMeasureDbName(flippedMeasureDbName);
        return measureMapping;
    }

}

