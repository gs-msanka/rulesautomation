Map<String, JBCXM__ScoringScheme__c> scoringSchemeMap = new Map<String, JBCXM__ScoringScheme__c>();
    	scoringSchemeMap.put('Numeric: 0 to 100', new JBCXM__ScoringScheme__c(Name='Numeric: 0 to 100', JBCXM__CustomerScheme__c = false, JBCXM__IsActive__c = true, JBCXM__Type__c = 'Score',
    						 JBCXM__SystemDefined__c = true, JBCXM__From__c = 0, JBCXM__To__c = 100));
    	scoringSchemeMap.put('Grade: A to F', new JBCXM__ScoringScheme__c(Name='Grade: A to F', JBCXM__CustomerScheme__c = true, JBCXM__IsActive__c = true, JBCXM__Type__c = 'Grade',
    						 JBCXM__SystemDefined__c = true, JBCXM__From__c = 0, JBCXM__To__c = 100));
    	scoringSchemeMap.put('Color: RYG', new JBCXM__ScoringScheme__c(Name='Color: RYG', JBCXM__CustomerScheme__c = false, JBCXM__IsActive__c = true, JBCXM__Type__c = 'Color',
    						 JBCXM__SystemDefined__c = true, JBCXM__From__c = 0, JBCXM__To__c = 100));
    	
    	insert scoringSchemeMap.values(); //To insert scoringSchemes.    	
    	
    	List<Map<String, Object>> gradeDetails = new List<Map<String, Object>>(); 
    	gradeDetails.add(new Map<String, Object>{'color' => '#76ad27', 'label' => 'A', 'from' => 90, 'to'=> 100, 'score' => 95});
    	gradeDetails.add(new Map<String, Object>{'color' => '#97cb4c', 'label' => 'B', 'from' => 80, 'to'=> 90, 'score' => 85});
    	gradeDetails.add(new Map<String, Object>{'color' => '#eba638', 'label' => 'C', 'from' => 60, 'to'=> 80, 'score' => 75});
    	gradeDetails.add(new Map<String, Object>{'color' => '#fbc064', 'label' => 'D', 'from' => 50, 'to'=> 60, 'score' => 55});
    	gradeDetails.add(new Map<String, Object>{'color' => '#f17273', 'label' => 'E', 'from' => 40, 'to'=> 50, 'score' => 45});
    	gradeDetails.add(new Map<String, Object>{'color' => '#c53536', 'label' => 'F', 'from' => 0, 'to'=> 40, 'score' => 20});
    	
    	List<Map<String, Object>> colorDetails = new List<Map<String, Object>>(); 
    	colorDetails.add(new Map<String, Object>{'color' => '#76ad27', 'label' => '#76ad27', 'from' => 80, 'to'=> 100, 'score' => 90, 'displayVal' => '80-100'});
    	colorDetails.add(new Map<String, Object>{'color' => '#eba638', 'label' => '#eba638', 'from' => 50, 'to'=> 80, 'score' => 65, 'displayVal' => '50-80'});
    	colorDetails.add(new Map<String, Object>{'color' => '#c53536', 'label' => '#b3322d', 'from' => 0, 'to'=> 50, 'score' => 25, 'displayVal' => '0-50'});
    	Map<String, String> ScoreColorDetails = new Map<String, String>{'0-50' => '#c53536', '50-75' => '#eba638', '75-100' => '#76ad27'};
    	
    	// #76ad27 - green, #97cb4c, #b3e767, #32a5c7, #4abfe2, #7fe2ff, #eba638, #fbc064, #ffd28b, #b3322d, #cb6362, #e88e8d - pink
    	
    	//To populate the default grading scheme definition.
    	List<JBCXM__ScoringSchemeDefinition__c> ssDObjList = new List<JBCXM__ScoringSchemeDefinition__c>();
    	JBCXM__ScoringSchemeDefinition__c ssdObj;
    	for(Map<String, Object> gMap : gradeDetails) {
    		ssdObj = new JBCXM__ScoringSchemeDefinition__c(Name = String.valueOf(gMap.get('label')), JBCXM__IsActive__c = true, JBCXM__Color__c = String.valueOf(gMap.get('color')), JBCXM__Display__c = String.valueOf(gMap.get('label')), 
    													JBCXM__From__c = Integer.valueOf(gMap.get('from')), JBCXM__Grade__c = String.valueOf(gMap.get('label')),
    													JBCXM__SchemeId__c = scoringSchemeMap.get('Grade: A to F').Id, 
    													JBCXM__Score__c = Integer.valueOf(gMap.get('score')), JBCXM__To__c = Integer.valueOf(gMap.get('to')));
    		ssDObjList.add(ssdObj);
    	}    	
    	//To populate the default coloring scheme definition.
    	for(Map<String, Object> cMap : colorDetails) {
    		ssdObj = new JBCXM__ScoringSchemeDefinition__c(Name = String.valueOf(cMap.get('label')), JBCXM__IsActive__c = true, JBCXM__Color__c = String.valueOf(cMap.get('color')), JBCXM__Display__c = String.valueOf(cMap.get('label')), 
    													JBCXM__From__c = Integer.valueOf(cMap.get('from')), JBCXM__SchemeId__c = scoringSchemeMap.get('Color: RYG').Id, 
    													JBCXM__Score__c = Integer.valueOf(cMap.get('score')), JBCXM__To__c = Integer.valueOf(cMap.get('to')));
    		ssDObjList.add(ssdObj);
    	} 
    	//To populate the score scheme definition.    	 
    	for(Integer i = 0; i <= 100; i++) {
    		ssdObj = new JBCXM__ScoringSchemeDefinition__c(Name = String.valueOf(i), JBCXM__IsActive__c = true, JBCXM__Display__c = String.valueOf(i), JBCXM__Score__c = i,
    													JBCXM__SchemeId__c = scoringSchemeMap.get('Numeric: 0 to 100').Id);
    		ssdObj.JBCXM__From__c 		= ((i <= 50) ? 0 : ((i > 50 && i <= 75) ? 50 : 75)); 
    		ssdObj.JBCXM__To__c	 	= ((i <= 50) ? 50 : ((i > 50 && i <= 75) ? 75 : 100));
    		ssdObj.JBCXM__Display__c   = ''+ssdObj.JBCXM__From__c+'-'+ssdObj.JBCXM__To__c+'';
    		ssdObj.JBCXM__Color__c 	=  ScoreColorDetails.get(''+Integer.valueOf(ssdObj.JBCXM__From__c)+'-'+Integer.valueOf(ssdObj.JBCXM__To__c)+'');											
    		ssDObjList.add(ssdObj);
    	}
    	insert ssDObjList; ////To insert scoringSchemeDefinitions.
    	
    	//To create Groups and metrics.
		JBCXM__ScorecardMetric__c scGroup = new JBCXM__ScorecardMetric__c(Name = 'Default', JBCXM__IsActive__c = true, JBCXM__IsGroup__c = true, JBCXM__DisplayOrder__c = 1);
		insert scGroup;
		//To insert metric.
		Id defaultSchemaId = scoringSchemeMap.get('Grade: A to F').Id;
		List<JBCXM__ScorecardMetric__c> defaultMetrics = new List<JBCXM__ScorecardMetric__c>();
		Map<String, JBCXM__GSMetaInfo__c> metricTypeMap = populateDefaultMetricTypes(); 
		JBCXM__ScorecardMetric__c custRelationMetric = new JBCXM__ScorecardMetric__c(Name = 'Customer Relationship', JBCXM__IsActive__c = true, JBCXM__IsGroup__c = false, JBCXM__DisplayOrder__c = 1, 
									       JBCXM__ValidityPeriod__c = 0, JBCXM__PeriodType__c = 'WEEKS', JBCXM__ScoringSchemeId__c = defaultSchemaId, JBCXM__MetricType__c = metricTypeMap.get('Manual').Id, 
									       JBCXM__AllowOverride__c = true, JBCXM__InputType__c = 'Manual', JBCXM__GroupId__c = scGroup.Id, JBCXM__HelpText__c = '{"0-34":"Strained relationship","35-74" : "Good relationship, but not at the Executive-level","75-100":"Great relationship at the Executive-Level"}', JBCXM__Weight__c = 15); 
		JBCXM__ScorecardMetric__c adoptionMetric = new JBCXM__ScorecardMetric__c(Name = 'Adoption', JBCXM__IsActive__c = true, JBCXM__IsGroup__c = false, JBCXM__DisplayOrder__c = 2, 
									   JBCXM__ValidityPeriod__c = 0, JBCXM__PeriodType__c = 'WEEKS', JBCXM__ScoringSchemeId__c = defaultSchemaId, JBCXM__MetricType__c = metricTypeMap.get('Manual').Id, 
									   JBCXM__AllowOverride__c = true, JBCXM__InputType__c = 'Manual', JBCXM__GroupId__c = scGroup.Id, JBCXM__HelpText__c = '{"0-34":"Low usage","35-74" : "Medium usage","75-100":"High usage"}', JBCXM__Weight__c = 45); 
		JBCXM__ScorecardMetric__c supportMetric = new JBCXM__ScorecardMetric__c(Name = 'Support', JBCXM__IsActive__c = true, JBCXM__IsGroup__c = false, JBCXM__DisplayOrder__c = 3, 
									  JBCXM__ValidityPeriod__c = 0, JBCXM__PeriodType__c = 'WEEKS', JBCXM__ScoringSchemeId__c = defaultSchemaId, JBCXM__MetricType__c = metricTypeMap.get('Manual').Id, 
									  JBCXM__AllowOverride__c = true, JBCXM__InputType__c = 'Manual', JBCXM__GroupId__c = scGroup.Id, JBCXM__HelpText__c = '{"0-34":"Too high/too low count or frequency of tickets","35-74" : "Above/Below healthy count or frequency of tickets","75-100":"Healthy count or frequency of tickets"}', JBCXM__Weight__c = 10); 
		JBCXM__ScorecardMetric__c npsMetric = new JBCXM__ScorecardMetric__c(Name = 'Last NPS (Average)', JBCXM__IsActive__c = true, JBCXM__IsGroup__c = false, JBCXM__DisplayOrder__c = 4, 
								      JBCXM__ValidityPeriod__c = 0, JBCXM__PeriodType__c = 'WEEKS', JBCXM__ScoringSchemeId__c = defaultSchemaId, JBCXM__MetricType__c = metricTypeMap.get('Manual').Id, 
								      JBCXM__AllowOverride__c = true, JBCXM__InputType__c = 'Manual', JBCXM__GroupId__c = scGroup.Id, JBCXM__HelpText__c = '{"0-34":"Detractor (0-6)","35-74" : "Passive (7-8)","75-100":"Promoter (9-10)"}', JBCXM__Weight__c = 10); 
		JBCXM__ScorecardMetric__c growthMetric = new JBCXM__ScorecardMetric__c(Name = 'Growth', JBCXM__IsActive__c = true, JBCXM__IsGroup__c = false, JBCXM__DisplayOrder__c = 5, 
									 JBCXM__ValidityPeriod__c = 0, JBCXM__PeriodType__c = 'WEEKS', JBCXM__ScoringSchemeId__c = defaultSchemaId, JBCXM__MetricType__c = metricTypeMap.get('Manual').Id, 
									 JBCXM__AllowOverride__c = true, JBCXM__InputType__c = 'Manual', JBCXM__GroupId__c = scGroup.Id, JBCXM__HelpText__c = '{"0-34":"Contract value down","35-74" : "Contract value flat","75-100":"Contract value up"}', JBCXM__Weight__c = 20); 
		defaultMetrics.add(custRelationMetric);
		defaultMetrics.add(adoptionMetric);
		defaultMetrics.add(supportMetric);
		defaultMetrics.add(npsMetric);
		defaultMetrics.add(growthMetric);	
		insert defaultMetrics;
		
		
public static Map<String, JBCXM__GSMetaInfo__c> populateDefaultMetricTypes() { 
		Map<String, JBCXM__GSMetaInfo__c> defaultMetricTypes = new Map<String, JBCXM__GSMetaInfo__c>();
		defaultMetricTypes.put('Manual', new JBCXM__GSMetaInfo__c(Name = 'Manual', JBCXM__SystemName__c = 'Manual', JBCXM__DisplayOrder__c = 1, JBCXM__Category__c = 'SCMETRIC'));
		defaultMetricTypes.put('SmartSign', new JBCXM__GSMetaInfo__c(Name = 'SmartSign', JBCXM__SystemName__c = 'SmartSign', JBCXM__DisplayOrder__c = 2, JBCXM__Category__c = 'SCMETRIC'));
		insert defaultMetricTypes.values();
		return defaultMetricTypes;
	}