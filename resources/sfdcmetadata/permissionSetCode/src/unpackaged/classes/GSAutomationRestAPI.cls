@RestResource(urlMapping='/GSAutomation/*')
global without sharing class GSAutomationRestAPI {  

    @HttpPost
    global static void orgInfo(String params) {
        RestRequest req = RestContext.request;
        RestResponse res = RestContext.response;
        Boolean success;
        String errMsg, errCode;
        Object response = '';
        Map<String, Object> resObj;
        String moduleName;
        String methodName;

        Map<String, Object> parameters = new Map<String, Object>();
        system.debug(params);
        if(params != null && params.length() > 0){
               parameters = (Map<String, Object>) JSON.deserializeUntyped(params);
        }
        system.debug(parameters);

        moduleName = String.valueOf(parameters.get('modulename'));
        system.debug('Module Name ' +moduleName);
        if(modulename !=null && modulename == 'GS_Auto_Permissions') {
            List<Object> objectPerm = (List<Object>)(parameters.get('data'));
            system.debug(objectPerm.size());
            Map<String, List<String>> objectFieldMap = new Map<String, List<String>>();
            for(Object obj : objectPerm) {
                Map<String, Object> subObj = (Map<String, Object>)Json.deserializeUnTyped(Json.serialize(obj));
                for(String stemp1 : subObj.keySet()) {
                  //  subObj.put(stemp1, JSON.)
                        List<String> st = (List<String>) Json.deserialize(Json.serialize(subObj.get(stemp1)), List<String>.class);
                        system.debug(st);            
                    objectFieldMap.put(stemp1, st);
                }           
            }   
            system.debug('ObjectFieldMap ' +objectFieldMap);
            if(objectFieldMap.size() >0 ) {
                resObj = createObjectFieldPermission(objectFieldMap, true);    
            }


        } else {
            resObj = new Map<String, Object>{'status' => 'FAILED', 'errMsg' => 'In Correct Module Name :' +modulename};         
        }
        
            Map<String, List<String>> map1 = new Map<String, List<String>>();
            List<String> acclist = new List<String>{'AccountNumber', 'Site', 'AccountSource', 'AnnualRevenue', 'Description',
                                    'NumberOfEmployees', 'Industry', 'Ownership', 'Phone', 'Rating', 'TickerSymbol', 'Type', 'Website'};
            map1.put('Account', acclist);                       
            List<String> oppList = new List<String>{ 'Amount', 'Description',  'ExpectedRevenue',
                                        'LeadSource', 'NextStep',  'IsPrivate', 'Probability', 'TotalOpportunityQuantity',
                                         'Type'};
            map1.put('Opportunity', oppList);                           
            List<String> conList = new List<String>{ 'AssistantName', 'AssistantPhone', 'Birthdate', 'Department', 'Description',
                                    'DoNotCall', 'Email', 'HasOptedOutOfEmail', 'Fax', 'HasOptedOutOfFax', 'HomePhone', 
                                    'LeadSource',  'OtherPhone', 'Phone','Title'};
            map1.put('Contact', conList);
            createObjectFieldPermission(map1, false);    
        
        res.responseBody = Blob.valueOf(JSON.serialize(resObj));
        return;
    }

    private static Map<String, Object> createObjectFieldPermission(Map<String, List<String>> objectFieldsMap, Boolean isObjectPermissionReq) {
        Boolean createPermission = true;
        Boolean editPermission = true; 
        Map<String, Object> resObject;
        try {

            System.debug('Going to start');
            if(!objectFieldsMap.isEmpty() && objectFieldsMap != null) {                                             
                String APP_NAMESPACE_PREFIX = 'JBCXM';
                System.debug(APP_NAMESPACE_PREFIX);
                List<User> licensedUsers = [SELECT Id, Name, ProfileId FROM User WHERE Id in (SELECT UserId FROM UserPackageLicense WHERE (PackageLicense.NamespacePrefix = :APP_NAMESPACE_PREFIX))];   
                System.debug('Licensed Users :' +licensedUsers.size());
                List<User> systemAdminUsers = [SELECT Id, Name FROM User WHERE Profile.Name = 'System Administrator'];
                System.debug('SystemAdminUsers Users :' +systemAdminUsers.size());
                Set<String> userIds = new Set<String>();
                for(User u : licensedUsers) {
                    userIds.add(u.Id);
                } 
                for(User u : systemAdminUsers) {
                    userIds.add(u.Id);
                }
                
                List<ObjectPermissions> objectPermissionList = new List<ObjectPermissions>();
                List<FieldPermissions> fieldPermissionList = new List<FieldPermissions>();
                List<PermissionSetAssignment> assignmentList = new List<PermissionSetAssignment>();                                     
                ObjectPermissions objectPermission;
                FieldPermissions fldPermission;
                PermissionSetAssignment perAssignment;
                PermissionSet permissonSet;         
                            
                //check if custom permission set is present or not          
                List<PermissionSet> existingPermission = [SELECT Id FROM PermissionSet WHERE Label = 'GS_Automation_Permission' AND Name = 'GS_Automation_Permission' ];        
                /////////////////
                if(existingPermission.size() == 0) { //Nothing is there. Create everything new          
                    //update the "NamespacePrefix" also in PermissionSet.                           
                    permissonSet = new PermissionSet(Description = 'To give rights on Gainsight components', Label = 'GS_Automation_Permission', Name = 'GS_Automation_Permission');                    
                    insert permissonSet;                            
                    System.debug('Permission set created');
                    //loop through each object and fields sent              
                    for(String objectName : objectFieldsMap.keySet()) {         
                        if(isObjectPermissionReq) { //do not give permission to object
                            if(objectName != 'Account' && objectName != 'Contact' && objectName !='Opportunity') {
                                objectPermission = new ObjectPermissions(SobjectType = objectName, 
                                                                        PermissionsRead = true, 
                                                                        PermissionsEdit = editPermission,
                                                                        PermissionsCreate = createPermission, 
                                                                        PermissionDelete = true,
                                                                        ParentId = permissonSet.Id);        
                            objectPermissionList.add(objectPermission);    
                            }
                            
                        }
                        //Account Object Permissions Are Manditory.
                        objectPermission = new ObjectPermissions(SobjectType = 'Account', 
                                                                    PermissionsRead = true, 
                                                                    PermissionsEdit = true,
                                                                    PermissionsCreate = true, 
                                                                    PermissionDelete = true,
                                                                    ParentId = permissonSet.Id);        
                        
                        insert objectPermission;
                        //Contact Object Permission are added by default.
                        objectPermission = new ObjectPermissions(SobjectType = 'Contact', 
                                                                    PermissionsRead = true, 
                                                                    PermissionsEdit = true,
                                                                    PermissionsCreate = true, 
                                                                    PermissionDelete = true,
                                                                    ParentId = permissonSet.Id);        
                        
                        insert objectPermission;
                        //Opportunity Object Permission are added by default.
                        objectPermission = new ObjectPermissions(SobjectType = 'Opportunity', 
                                                                    PermissionsRead = true, 
                                                                    PermissionsEdit = true,
                                                                    PermissionsCreate = true, 
                                                                    PermissionDelete = true,
                                                                    ParentId = permissonSet.Id);        
                        
                        insert objectPermission;

                    
                        for(String fieldName : objectFieldsMap.get(objectName)) {
                            SObjectType accountType = Schema.getGlobalDescribe().get(objectName);
                            Map<String,Schema.SObjectField> mfields = accountType.getDescribe().fields.getMap();
                            system.debug(mfields);
                            Schema.SObjectField field = mfields.get(fieldName);
                            Schema.DescribeFieldResult fieldDes = field.getDescribe();
                            editPermission = fieldDes.isUpdateable();

                            System.debug('Created Field Permissions : ' +objectName);
                            fieldName = objectName+'.'+fieldName;
                            fldPermission = new FieldPermissions(ParentId = permissonSet.Id, 
                                                                    Field = fieldName, 
                                                                    SobjectType = objectName, 
                                                                    PermissionsEdit = editPermission, 
                                                                    PermissionsRead = true);            
                            fieldPermissionList.add(fldPermission);
                        }                               
                    }
                    
                    if(isObjectPermissionReq) {                
                        insert objectPermissionList;            
                        System.debug('Object Permissions');
                        }
                    insert fieldPermissionList;
                    System.debug('Field Permissions');

                    for(String  userId : userIds) {                                
                        perAssignment = new PermissionSetAssignment( PermissionSetId = permissonSet.Id, AssigneeId = userId);
                        assignmentList.add(perAssignment);
                    }                   
                    System.debug(assignmentList.size());
                    if(assignmentList.size() > 0 )insert assignmentList;                              
                } else {                                                                
                    Set<String> existingObjectPermissions = new Set<String>();
                    Set<String> existingFieldPermissions = new Set<String>();               
                    Set<String> existingAssigedUserIds = new Set<String>();

                    permissonSet = existingPermission[0];
                                    
                    //check if for sent objects, Object Permissions exists already
                    if(isObjectPermissionReq) {             
                        for(ObjectPermissions objPermission : [SELECT SobjectType FROM ObjectPermissions WHERE ParentId =: permissonSet.Id AND SobjectType IN: objectFieldsMap.keySet()]) {                 
                            existingObjectPermissions.add(objPermission.SobjectType);
                        }
                    }                
                    for(FieldPermissions fieldPermissions : [SELECT Field FROM FieldPermissions WHERE ParentId =: permissonSet.Id AND SobjectType IN: objectFieldsMap.keySet()]) {                  
                        System.debug('Field :' +fieldPermissions.Field);
                        existingFieldPermissions.add(String.valueOf(fieldPermissions.Field).toLowerCase());
                    }                           
                    for(String objectName : objectFieldsMap.keySet()) {                 
                        //do not create duplicate object permission
                        if(isObjectPermissionReq){
                            if(!existingObjectPermissions.contains(objectName)) {
                                objectPermission = new ObjectPermissions(SobjectType = objectName, PermissionsRead = true, PermissionsEdit = editPermission, PermissionsCreate = createPermission, ParentId = permissonSet.Id);                     
                                objectPermissionList.add(objectPermission);
                                existingObjectPermissions.add(objectName);
                            }
                        }
                        System.debug(existingFieldPermissions);
                        for(String fieldName : objectFieldsMap.get(objectName)) {
                            SObjectType accountType = Schema.getGlobalDescribe().get(objectName);
                            Map<String,Schema.SObjectField> mfields = accountType.getDescribe().fields.getMap();
                            system.debug(mfields);
                            String f1 = fieldName.trim();
                            fieldName = (objectName+'.'+fieldName).trim();
                            System.debug(!existingFieldPermissions.contains(fieldName));
                            System.debug('Field Name :' +fieldName);
                            if(!existingFieldPermissions.contains(fieldName.toLowerCase())) {
                                Schema.SObjectField field = mfields.get(f1);
                                Schema.DescribeFieldResult fieldDes = field.getDescribe();
                                editPermission = fieldDes.isUpdateable();
                                fldPermission = new FieldPermissions(ParentId = permissonSet.Id, Field = fieldName, SobjectType = objectName, PermissionsEdit = editPermission, PermissionsRead = true);
                                fieldPermissionList.add(fldPermission);                         
                                existingFieldPermissions.add(fieldName.toLowerCase());
                            }
                        }                               
                    }
                    if(isObjectPermissionReq && objectPermissionList.size() > 0) {                      
                        insert objectPermissionList;
                    }
                        
                    system.debug('Field Permissions ' +fieldPermissionList);
                    if(fieldPermissionList.size() > 0 ) {
                        insert fieldPermissionList;
                    }
                    
                    for(PermissionSetAssignment existingUsers : [SELECT AssigneeId FROM PermissionSetAssignment WHERE PermissionSetId =: permissonSet.Id]) {                
                        existingAssigedUserIds.add(String.valueOf(existingUsers.AssigneeId));
                    }   
                    for(String  userId : userIds) { 
                    System.debug(userId);  
                        if(!existingAssigedUserIds.contains(userId)) {
                            perAssignment = new PermissionSetAssignment( PermissionSetId = permissonSet.Id, AssigneeId = userId);
                            assignmentList.add(perAssignment);  
                        }                                                   
                    }                   
                    System.debug(assignmentList.size());
                    if(assignmentList.size() > 0 )insert assignmentList;                              
          
                }
                
                //////////////////
                resObject = new Map<String, Object>{'status' => 'SUCCESS', 'errMsg' => ''};
            } else {
                    resObject = new Map<String, Object>{'status' => 'FAILED', 'errMsg' => 'No field are specified'};
            }
        } catch(System.Exception e) {
            resObject = new Map<String, Object>{'status' => 'FAILED', 'errMsg' => e.getMessage()};
        }
        System.debug(resObject);
        return resObject;
    }   
}