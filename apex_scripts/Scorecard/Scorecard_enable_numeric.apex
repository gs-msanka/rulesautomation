//ENABLING NUMERIC SCHEME
List<JBCXM__ScoringScheme__c> scheme_type=[select JBCXM__CustomerScheme__c,Name from JBCXM__ScoringScheme__c ];

if (scheme_type.size()!=0) {
    for(JBCXM__ScoringScheme__c cur_scheme_type : scheme_type) {
        if(cur_scheme_type.JBCXM__CustomerScheme__c) cur_scheme_type.JBCXM__CustomerScheme__c=false;
        if(cur_scheme_type.Name.startswith('Numeric')) cur_scheme_type.JBCXM__CustomerScheme__c=true;
    }
}

update(scheme_type);
