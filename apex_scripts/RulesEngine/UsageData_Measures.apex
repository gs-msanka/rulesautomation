List<JBCXM__PickList__c> deleteList = [SELECT id FROM JBCXM__PickList__c WHERE JBCXM__CATEGORY__C = 'Adoption Measure Types' AND JBCXM__DISPLAYORDER__C >= 10];
delete deleteList;
List<JBCXM__PickList__c> pkList = new List<JBCXM__PickList__c>();
List<String> measureNameList = new List<String>{'Page Views', 'Page Visits', 'No of Report Run', 'Files Downloaded'};
List<String> colorList = new List<String>{'#8b8989', '#ffdead', '#000000', '#0000cd', '#006400', '#adff2f', '#ffff00',
										'#cd5c5c', '#b22222', '#ff4500', '#ff1493', '#9932cc', '#dda0dd', '#2f4f4f',
										'#696969', '#8470ff', '#eee8aa', '#a0522d', '#d2691e' , '#9370db' };
Integer i = 10;
Integer j = 0;
JBCXM__PickList__c pkMeasure = null;
for(String measure : measureNameList) {
	pkMeasure = new JBCXM__PickList__c(Name = measure,
										JBCXM__ADOPTIONMEASURE__C = TRUE,
										JBCXM__ATTRIBUTE1__C = colorList.get(j),
										JBCXM__ATTRIBUTE4__C = TRUE,
										JBCXM__CATEGORY__C = 'Adoption Measure Types',
										JBCXM__DISPLAYORDER__C = i,
										JBCXM__SYSTEMNAME__C = measure
										);
	i++;
	j++;
	if(j == colorList.size()) {
		j= 0;
	}
	pkList.add(pkMeasure);
}
insert pkList;