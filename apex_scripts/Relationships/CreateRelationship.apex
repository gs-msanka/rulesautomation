// Deleting relationship config with name 'RelationShip for RulesEngine' if any
delete [SELECT JBCXM__Active__c, JBCXM__Description__c, Name FROM JBCXM__GSRelationshipType__c where Name = 'RelationShip for RulesEngine'];

List<JBCXM__GSRelationshipType__c> rel = new List<JBCXM__GSRelationshipType__c>();
rel.add(new JBCXM__GSRelationshipType__c(JBCXM__Active__c = true, JBCXM__Description__c = 'Relationship for RulesEngine-Desc', Name = 'RelationShip for RulesEngine', JBCXM__Attributes__c = '[{"name":"JBCXM__Account__c","label":"Account","objectName":"JBCXM__GSRelationship__c","fieldType":"REFERENCE","fieldAssociation":"direct","childObject":null,"referenceTo":"Account"},{"name":"JBCXM__Account__r.Name","label":"Account Name","objectName":"JBCXM__GSRelationship__c","fieldType":"TEXT","fieldAssociation":"direct","childObject":null,"referenceTo":null},{"name":"JBCXM__CTA__c","label":"Call To Action","objectName":"JBCXM__GSRelationship__c","fieldType":null,"referenceTo":null,"fieldAssociation":"indirect","childObject":"JBCXM__CTA__c","relationshipLookUp":"JBCXM__Relationship__c"},{"name":"Name","label":"Name","objectName":"JBCXM__GSRelationship__c","fieldType":"STRING","fieldAssociation":"direct","childObject":null,"referenceTo":null},{"name":"JBCXM__RelationshipType__c","label":"Relationship Type","objectName":"JBCXM__GSRelationship__c","fieldType":"REFERENCE","fieldAssociation":"direct","childObject":null,"referenceTo":"JBCXM__GSRelationshipType__c"},{"name":"SystemModstamp","label":"System Modstamp","objectName":"JBCXM__GSRelationship__c","fieldType":"DATETIME","fieldAssociation":"direct","childObject":null,"referenceTo":null}]'));
insert rel;

/*JBCXM__GSRelationshipType__c rel=new JBCXM__GSRelationshipType__c();
rel.JBCXM__Active__c=true;
rel.Name='RelationShip for RulesEngine';
rel.JBCXM__Description__c='Relationship for RulesEngine-Desc';
rel.JBCXM__Attributes__c='[{"name":"JBCXM__Account__c","label":"Account","objectName":"JBCXM__GSRelationship__c","fieldType":"REFERENCE","fieldAssociation":"direct","childObject":null,"referenceTo":"Account"},{"name":"JBCXM__Account__r.Name","label":"Account Name","objectName":"JBCXM__GSRelationship__c","fieldType":"TEXT","fieldAssociation":"direct","childObject":null,"referenceTo":null},{"name":"JBCXM__CTA__c","label":"Call To Action","objectName":"JBCXM__GSRelationship__c","fieldType":null,"referenceTo":null,"fieldAssociation":"indirect","childObject":"JBCXM__CTA__c","relationshipLookUp":"JBCXM__Relationship__c"},{"name":"Name","label":"Name","objectName":"JBCXM__GSRelationship__c","fieldType":"STRING","fieldAssociation":"direct","childObject":null,"referenceTo":null},{"name":"JBCXM__RelationshipType__c","label":"Relationship Type","objectName":"JBCXM__GSRelationship__c","fieldType":"REFERENCE","fieldAssociation":"direct","childObject":null,"referenceTo":"JBCXM__GSRelationshipType__c"},{"name":"SystemModstamp","label":"System Modstamp","objectName":"JBCXM__GSRelationship__c","fieldType":"DATETIME","fieldAssociation":"direct","childObject":null,"referenceTo":null}]';
insert rel;*/


//------------------------------------------------------------

/*
List<JBCXM__GSRelationshipType__c > xyz=[select Id from JBCXM__GSRelationshipType__c where name='RelationShip for RulesEngine' limit 1];

JBCXM__GSRelationshipAssociation__c ascw = new JBCXM__GSRelationshipAssociation__c(JBCXM__Config__c='{"filters":[]}',JBCXM__EntityType__c='SFDC',JBCXM__Entity__c='GroupSubscription',JBCXM__RelationshipType__c = xyz[0].id);
insert ascw;

JBCXM__GSRelationshipAssociation__c ascw2 = new JBCXM__GSRelationshipAssociation__c(JBCXM__Config__c='{"filters":[{"meta":{"operator":"e","fieldAssociation":"direct","logicalOperator":"AND"},"source":{"type":"field","value":"","fieldInfo":{"name":"Id","label":"Id","objectName":"JBCXM__GSRelationship__c","fieldType":"Id","childObject":"JBCXM__GSRelationship__c","orignal-objectName":"JBCXM__GSRelationship__c","orignal-fieldName":"Id"}},"target":{"type":"field","fieldInfo":{"name":"JBCXM__Relationship__c","label":"Relationship","dataType":"string","objectName":"JBCXM__CTA__c"}},"isDirectObj":true}]}',JBCXM__EntityType__c='SFDC',JBCXM__Entity__c='JBCXM__CTA__c',JBCXM__RelationshipType__c = xyz[0].id);
insert ascw2;

JBCXM__GSRelationshipAssociation__c ascw3 = new JBCXM__GSRelationshipAssociation__c(JBCXM__Config__c='{"filters":[{"meta":{"operator":"e","fieldAssociation":"direct","logicalOperator":"AND"},"source":{"type":"field","value":"","fieldInfo":{"name":"Id","label":"Id","objectName":"JBCXM__GSRelationship__c","fieldType":"Id","childObject":"JBCXM__GSRelationship__c","orignal-objectName":"JBCXM__GSRelationship__c","orignal-fieldName":"Id"}},"target":{"type":"field","fieldInfo":{"name":"JBCXM__Relationship__c","label":"Relationship","dataType":"string","objectName":"JBCXM__GSRelationshipContacts__c"}},"isDirectObj":true}]}',JBCXM__EntityType__c='SFDC',JBCXM__Entity__c='JBCXM__GSRelationshipContacts__c',JBCXM__RelationshipType__c = xyz[0].id);
insert ascw3;
*/

List<JBCXM__GSRelationshipType__c > relType = [select Id from JBCXM__GSRelationshipType__c where name = 'RelationShip for RulesEngine' limit 1];
//Creating relationships
List<JBCXM__GSRelationship__c> relationship = new List<JBCXM__GSRelationship__c>();
JBCXM__GSRelationship__c relationshp = null;
integer i = 0;
for (Account acc : [SELECT id FROM Account where name like 'RULESUI Account%']) {
    i++;
    relationshp = new JBCXM__GSRelationship__c(Name = 'Relationship' + i, JBCXM__Account__c = acc.id, JBCXM__RelationshipType__c = relType[0].id);
    relationship.add(relationshp);
}

insert relationship;

