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
        if(params != null && params.length() > 0) {
               parameters = (Map<String, Object>) JSON.deserializeUntyped(params);
        }
        System.debug(parameters);

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
        res.responseBody = Blob.valueOf(JSON.serialize(resObj));
        return;
    }

    global static Map<String, Object> createObjectFieldPermission(Map<String, List<String>> objectFieldsMap, Boolean isObjectPermissionReq) {
        Boolean createPermission = true;
        Boolean editPermission = true;
        Map<String, Object> resObject;
        try {
            System.debug('Giving Permissions...');
            if(objectFieldsMap != null && !objectFieldsMap.isEmpty()) {
                String APP_NAMESPACE_PREFIX = 'JBCXM';
                System.debug(APP_NAMESPACE_PREFIX);
                //To get all the users those are assigned to JBCXM Package.
                
                Set<String> userIds = new Set<String>();
                
                List<ObjectPermissions> objectPermissionList    = new List<ObjectPermissions>();
                List<FieldPermissions> fieldPermissionList      = new List<FieldPermissions>();
                List<PermissionSetAssignment> assignmentList    = new List<PermissionSetAssignment>();
                ObjectPermissions objectPermission;
                FieldPermissions fldPermission;
                PermissionSetAssignment perAssignment;
                PermissionSet permissonSet;

                //check if custom permission set is present or not
                List<PermissionSet> existingPermission = [SELECT Id FROM PermissionSet WHERE Label = 'GS_Automation_Permission' AND Name = 'GS_Automation_Permission' ];
                
                if(existingPermission.size() == 0) { 
                    //Nothing is there. Create everything new
                    permissonSet = new PermissionSet(Description = 'To give rights on Gainsight components', Label = 'GS_Automation_Permission', Name = 'GS_Automation_Permission');
                    insert permissonSet;
                    System.debug('Permission set created.');
                    //loop through each object and fields sent
                    for(String objectName : objectFieldsMap.keySet()) {
                        if(isObjectPermissionReq) { //do not give permission to object
                            if(objectName != 'Account' && objectName != 'Contact' && objectName !='Opportunity') {
                                objectPermission = new ObjectPermissions(SobjectType = objectName,
                                                                        PermissionsRead = true,
                                                                        PermissionsEdit = true,
                                                                        PermissionsCreate = true,
                                                                        ParentId = permissonSet.Id);
                            objectPermissionList.add(objectPermission);
                            }
                        }
                        SObjectType accountType                 = Schema.getGlobalDescribe().get(objectName);
                        Map<String,Schema.SObjectField> mfields = accountType.getDescribe().fields.getMap();

                        for(String fieldName : objectFieldsMap.get(objectName)) {
                            Schema.SObjectField field               = mfields.get(fieldName);
                            Schema.DescribeFieldResult fieldDes     = field.getDescribe();                            
                            
                            fieldName = objectName+'.'+fieldName;
                            System.debug('Created Field Permissions : ' +fieldName);
                            System.debug('Permissions Edit : ' +fieldDes.isUpdateable());
                            if(fieldDes.isPermissionable()) {
                                fldPermission = new FieldPermissions(ParentId           = permissonSet.Id,
                                                                    Field           = fieldName,
                                                                    SobjectType     = objectName,
                                                                    PermissionsEdit = fieldDes.isCalculated() || fieldDes.isAutoNumber() ? false : true,
                                                                    PermissionsRead = true);
                                fieldPermissionList.add(fldPermission);                            
                            }
                        }
                    }
                    //Account Object Permissions Are Manditory.
                    objectPermission = new ObjectPermissions(SobjectType = 'Account',
                                                                PermissionsRead = true,
                                                                PermissionsEdit = true,
                                                                PermissionsCreate = true,
                                                                ParentId = permissonSet.Id);

                    insert objectPermission;
                    //Contact Object Permission are added by default.
                    objectPermission = new ObjectPermissions(SobjectType = 'Contact',
                                                                PermissionsRead = true,
                                                                PermissionsEdit = true,
                                                                PermissionsCreate = true,
                                                                ParentId = permissonSet.Id);

                    insert objectPermission;
                    //Opportunity Object Permission are added by default.
                    objectPermission = new ObjectPermissions(SobjectType = 'Opportunity',
                                                                PermissionsRead = true,
                                                                PermissionsEdit = true,
                                                                PermissionsCreate = true,
                                                                ParentId = permissonSet.Id);

                    insert objectPermission;                
                    perAssignment = new PermissionSetAssignment( PermissionSetId = permissonSet.Id, AssigneeId = UserInfo.getUserId());
                    assignmentList.add(perAssignment);                                        
                } else {
                    Set<String> existingObjectPermissions   = new Set<String>();
                    Set<String> existingFieldPermissions    = new Set<String>();
                    Set<String> existingAssigedUserIds      = new Set<String>();

                    permissonSet = existingPermission.get(0);

                    //check if for sent objects, Object Permissions exists already
                    if(isObjectPermissionReq) {
                        for(ObjectPermissions objPermission : [SELECT SobjectType FROM ObjectPermissions WHERE ParentId =: permissonSet.Id AND SobjectType IN: objectFieldsMap.keySet()]) {
                            existingObjectPermissions.add(objPermission.SobjectType.toLowerCase());
                        }
                    }
                    for(FieldPermissions fieldPermissions : [SELECT Field FROM FieldPermissions WHERE ParentId =: permissonSet.Id AND SobjectType IN: objectFieldsMap.keySet()]) {
                        System.debug('Field :' +fieldPermissions.Field);
                        existingFieldPermissions.add(String.valueOf(fieldPermissions.Field).toLowerCase());
                    }

                    for(String objectName : objectFieldsMap.keySet()) {
                        //do not create duplicate object permission
                        if(isObjectPermissionReq){
                            if(!existingObjectPermissions.contains(objectName.toLowerCase())) {
                                objectPermission = new ObjectPermissions(SobjectType = objectName, PermissionsRead = true, PermissionsEdit = true, PermissionsCreate = true, ParentId = permissonSet.Id);
                                objectPermissionList.add(objectPermission);
                                existingObjectPermissions.add(objectName.toLowerCase());
                            }
                        }                        
                        SObjectType accountType                 = Schema.getGlobalDescribe().get(objectName);
                        Map<String,Schema.SObjectField> mfields = accountType.getDescribe().fields.getMap();
                        for(String fieldName : objectFieldsMap.get(objectName)) {
                            String f1 = fieldName.trim();
                            fieldName = (objectName+'.'+fieldName).trim();
                            System.debug('Created Field Permissions : ' +fieldName);
                            if(!existingFieldPermissions.contains(fieldName.toLowerCase())) {

                                Schema.SObjectField field = mfields.get(f1);
                                Schema.DescribeFieldResult fieldDes = field.getDescribe();
                                System.debug('isCalulated :' +fieldDes.isCalculated());
                                System.debug('isAutonumber :' +fieldDes.isAutoNumber());
                                System.debug('Permissions Edit : ' +fieldDes.isUpdateable());
                                if(fieldDes.isPermissionable()) {
                                    fldPermission = new FieldPermissions(ParentId           = permissonSet.Id,
                                                                        Field           = fieldName,
                                                                        SobjectType     = objectName,
                                                                        PermissionsEdit = (fieldDes.isCalculated() || fieldDes.isAutoNumber()) ? false : true,
                                                                        PermissionsRead = true);
                                    fieldPermissionList.add(fldPermission);                            
                                    existingFieldPermissions.add(fieldName.toLowerCase());
                                }                                 
                            }
                        }
                    }

                    //Get the users to whom permission set is assigned.    
                    List<PermissionSetAssignment> userPermSetAssign = [SELECT AssigneeId FROM PermissionSetAssignment WHERE PermissionSetId =: permissonSet.Id AND AssigneeId =: UserInfo.getUserId()];
                    if(userPermSetAssign.size() == 0) {
                        perAssignment = new PermissionSetAssignment( PermissionSetId = permissonSet.Id, AssigneeId = UserInfo.getUserId());
                        assignmentList.add(perAssignment);
                    }    
                } 

                if(isObjectPermissionReq && objectPermissionList.size() > 0) {
                    System.debug('Insert Object Permissions...');
                    System.debug('Object Permissions ' + Json.serialize(objectPermissionList));
                    insert objectPermissionList;
                }

            
                if(fieldPermissionList.size() > 0 ) {
                    System.debug('Inserting Field Permissions...');
                    System.debug('Field Permissions ' + Json.serialize(fieldPermissionList));
                    insert fieldPermissionList;
                }                
            
                if(assignmentList.size() > 0 ) {
                    System.debug('Inserting Assignemnts to Permissions...');
                    System.debug('Assignemnts ' + Json.serialize(fieldPermissionList));
                    insert assignmentList;
                }         
                resObject = new Map<String, Object>{'status' => 'SUCCESS', 'errMsg' => ''};
            } else {
                resObject = new Map<String, Object>{'status' => 'FAILED', 'errMsg' => 'No field are specified'};
            }
        } catch(System.Exception e) {
            resObject = new Map<String, Object>{'status' => 'FAILED', 'errMsg' => e.getMessage()+e.getStackTraceString()};
        }
        System.debug(resObject);
        return resObject;
    }    
}
