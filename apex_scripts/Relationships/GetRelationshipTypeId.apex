//deleting RelationshipTypes prior to creating one
        Delete [SELECT ID,Name FROM JBCXM__GSRelationshipType__c WHERE Name = 'Automated RelationshipType' ];
        Delete [SELECT ID,Name FROM JBCXM__GSRelationship__c WHERE Name = 'Automated RelationshipType' ];


        JBCXM__GSRelationshipType__c relationshipType = new JBCXM__GSRelationshipType__c();
        relationshipType.JBCXM__Active__c = true;
        relationshipType.JBCXM__Attributes__c = '[{"name":"Account__r.Name","label":"Account Name","objectName":"JBCXM__GSRelationship__c","fieldType":"TEXT","fieldAssociation":"direct","childObject":null,"referenceTo":null}]';
        relationshipType.JBCXM__CardConfig__c = '[{"type":"Field","displayOrder":1,"meta":{"name":"Account__r.Name","label":"Account Name","displayOrder":1,"visibilityMode":true,"objectName":"JBCXM__GSRelationship__c","fieldType":"TEXT","isGroupable":true,"hasJBCXMNS":false,"isFormulaField":false,"isUpdateable":false},"label":"Account Name"}]';
        relationshipType.Name = 'Automated RelationshipType';
    	insert relationshipType;


        JBCXM__GSRelationshipSection__c section = new JBCXM__GSRelationshipSection__c();
        relationshipType = [SELECT id,Name FROM JBCXM__GSRelationshipType__c WHERE Name = 'Automated RelationshipType'];
        section.Name = 'Scorecard';
        section.JBCXM__Label__c = 'Scorecard';
        section.JBCXM__Type__c ='section';
        section.JBCXM__DisplayOrder__c = 1.0;
        section.JBCXM__RelationshipType__c = relationshipType.Id;
        insert section;


        Account accsList = new Account();
        accsList =[Select id,Name from Account where Name ='AAR Corp Hardware Abscoa Division'];
        relationshipType = [SELECT ID,Name FROM JBCXM__GSRelationshipType__c WHERE Name = 'Automated RelationshipType' ];
        JBCXM__GSRelationship__c relationship = new JBCXM__GSRelationship__c();
        relationship.Name = relationshipType.Name;
        relationship.JBCXM__RelationshipType__c = relationshipType.Id;
        relationship.JBCXM__Account__c = accsList.id;
        insert relationship ;