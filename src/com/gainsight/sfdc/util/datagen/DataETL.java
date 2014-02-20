package com.gainsight.sfdc.util.datagen;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.util.bulk.SFDCInfo;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.sfdc.util.bulk.SfdcBulkApi;
import com.gainsight.sfdc.util.bulk.SfdcBulkOperationImpl;
import com.gainsight.sfdc.util.datagen.JobInfo.PreProcess;
import com.gainsight.sfdc.util.datagen.JobInfo.SfdcExtract;
import com.gainsight.sfdc.util.datagen.JobInfo.SfdcLoad;
import com.gainsight.sfdc.util.datagen.JobInfo.Transform;
import com.gainsight.sfdc.util.datagen.JobInfo.Transform.TableInfo;
import com.gainsight.sfdc.util.datagen.JobInfo.Transform.TableInfo.Columns;
import com.gainsight.sfdc.util.db.H2Db;
import com.gainsight.sfdc.util.db.QueryBuilder;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class DataETL implements IJobExecutor {

	public static String api_version = "28.0";
	public static String async_url = "/services/async/";
	public static String async_job_url = "";
	public static String pickListObject = "JBCXM__PickList__c";
	static Map<String, String> pMap;
	
	static String dropTableQuery = "DROP TABLE IF EXISTS ";
	static String resDir = "./resources/datagen/";
	static SfdcBulkOperationImpl op;
	static SFDCInfo info;
	static JobInfo jobInfo;
	static ObjectMapper mapper = new ObjectMapper();
	static H2Db db;
	
	static {
		info = SFDCUtil.fetchSFDCinfo();
		op = new SfdcBulkOperationImpl(info.getSessionId());
		async_job_url = info.getEndpoint() + async_url + api_version + "/job";
		
		try {
			//Pulling Pick List Object
			String picklistPath = resDir + "process/" + pickListObject + ".csv";
			String query1 = QueryBuilder.buildSOQLQuery(pickListObject, "JBCXM__SystemName__c", "Id");
			SfdcBulkApi.pullDataFromSfdc(pickListObject, query1, picklistPath);
		
			//Converting Pick List to Hash Map
			pMap = convertPickListToMap(picklistPath);
			System.out.println(pMap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			DataETL gen = new DataETL();
			//Step 1
			/*SfdcBulkApi.pushDataToSfdc("JBCXM__PickList__c", "insert", new File("./testdata/sfdc/PicklistSettings.csv"));
			//Step 2
			gen.cleanUp("JBCXM__ApplicationSettings__c");
			SfdcBulkApi.pushDataToSfdc("JBCXM__ApplicationSettings__c", "insert", new File("./testdata/sfdc/AppSettings.csv"));
			//Step 3
			SfdcBulkApi.pushDataToSfdc("Account", "insert", new File("./testdata/sfdc/Accounts.csv"));*/
			//Step 4
			//Decide which job to execute
			//Currently hard-coding to Job1
			jobInfo = mapper.readValue(new FileReader(resDir + "jobs/Job_Insert_SurveyUserAnswers"), JobInfo.class);
			gen.init();
			gen.execute(jobInfo);
			
			//gen.cleanUp("JBCXM__UsageData__c", 10000);
			//CleanUp Code if required
			/*new DataETL().cleanUp("Opportunity");
			new DataETL().cleanUp("Account");*/
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		/*info = SFDCUtil.fetchSFDCinfo();
		op = new SfdcBulkOperationImpl(info.getSessionId());
		async_job_url = info.getEndpoint() + async_url + api_version + "/job";
		
		try {
			//Pulling Pick List Object
			String picklistPath = resDir + "process/" + pickListObject + ".csv";
			String query1 = QueryBuilder.buildSOQLQuery(pickListObject, "JBCXM__SystemName__c", "Id");
			SfdcBulkApi.pullDataFromSfdc(pickListObject, query1, picklistPath);
		
			//Converting Pick List to Hash Map
			pMap = convertPickListToMap(picklistPath);
			System.out.println(pMap);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
	}

	@Override
	public void getListOfJobs() {
		// TODO Auto-generated method stub

	}

	/**
	 * Execute the Job Defined in the Json
	 */
	@Override
	public void execute(JobInfo jobInfo) {
		// TODO Auto-generated method stub
		try{
			//Creates a db for the particular job
			db = new H2Db("jdbc:h2:~/" + jobInfo.getJobName(),"sa","");
			
			//Pre Process Especially for Usage Data
			PreProcess preProcess = jobInfo.getPreProcessRule();
			if(preProcess != null){
				File inputFile = new File(preProcess.getInputFile());
				File outputFile = new File(preProcess.getOutputFile());
				if(preProcess.isMonthly()) {
					outputFile = FileProcessor.generateMonthlyUsageData(inputFile, preProcess.getFieldName(), outputFile);
				}
				else if(preProcess.isWeekly()) {
					outputFile = FileProcessor.generateWeeklyUsageData(inputFile, preProcess.getFieldName(), outputFile);
				}
				else if(preProcess.isDaysToAdd()){
					outputFile=FileProcessor.generateResponseSubmissionDate(inputFile, preProcess.getFieldName(), outputFile);
				}
				if(FileUtils.sizeOf(outputFile) > 0) {
					System.out.println("Data is Ready");
				}
				else
					throw new RuntimeException("Sorry Boss Pre Processing Failed");
			}
			
			//Extraction Code
			SfdcExtract pull = jobInfo.getExtractionRule();
			if(pull != null && !pull.toString().contains("null")) {
				String query = QueryBuilder.buildSOQLQuery(pull.getTable(), pull.getFields());
				SfdcBulkApi.pullDataFromSfdc(pull.getTable(), query, pull.getOutputFileLoc());
			}
			else
				System.out.println("Nothing to extract. Check the Job Details");
			
			//Mapping and Transformation Code.
			Transform transform = jobInfo.getTransformationRule();
			File transFile = null;
            File pushFile  = null;
			LinkedHashMap<String, List<Columns>> finalFields= new LinkedHashMap<String, List<Columns>>();
			ArrayList<String> joinColumn = new ArrayList<String>();
            SfdcLoad load = jobInfo.getLoadRule();
			if(transform != null && !transform.toString().contains("null")) {
				if(transform.isJoin()) {
					transFile = new File(transform.getOutputFileLoc());
					ArrayList<TableInfo> tables = transform.getTableInfo();
					for(TableInfo tableInfo : tables) {
						db.executeStmt(dropTableQuery + tableInfo.getTable());
						String createTableFromCSv = "CREATE TABLE " + tableInfo.getTable() + " AS SELECT * FROM CSVREAD('"+ tableInfo.getCsvFile() +"')";
						db.executeStmt(createTableFromCSv);
						finalFields.put(tableInfo.getTable(), tableInfo.getColumns());
						joinColumn.add(tableInfo.getJoinColumnName());
					}
					
					//Building Join Query
					String finalQuery;
					if(!transform.isJoinUsingQuery()) {
                        if(load != null && load.getOperation().equals("upsert")) {
                            finalQuery = QueryBuilder.buildRightJoinQuery(finalFields, joinColumn, "=");
                        } else {
                            finalQuery = QueryBuilder.buildJoinQuery(finalFields, joinColumn, "=");
                        }

						System.out.println("Final Query by forming join query at run time : " + finalQuery);
					}//Or use Query provided by the user
					else {
						finalQuery = transform.getQuery();
						System.out.println("Final Query provided by the user : " + finalQuery);
					}
						
					db.executeStmt("call CSVWRITE ( '" + transform.getOutputFileLoc() + "', '" + finalQuery + "' ) ");
					if(transFile.exists())
						System.out.println("Success");
					else
						System.out.println("Something went wrong");
				}
				//Applying picklist transformation

				if(transform.isPicklist()) {
					 pushFile = doPickListTransformation(transFile, pMap);
					if(pushFile.exists())
						System.out.println("Another Scucess");
					else
						System.out.println("Sorry Boss");
				}
			}
			
			//Load the Data back to SFDC
			if(transform!=null) {
                if(load.getOperation().equals("upsert")) {
                    SfdcBulkApi.pushDataToSfdc(load.getsObject(), load.getOperation(), (transform.isPicklist() ) ? pushFile : resolveNameSpace(load.getFile()), load.getExternalIDField());
                } else{
                    SfdcBulkApi.pushDataToSfdc(load.getsObject(), load.getOperation(), (transform.isPicklist() ) ? pushFile : resolveNameSpace(load.getFile()));
                }
            } else{ //in case there is no transform part...only loading
            	if(load.getOperation().equals("upsert")) {
                    SfdcBulkApi.pushDataToSfdc(load.getsObject(), load.getOperation(),new File(load.getFile()),load.getExternalIDField());
                }
            	else {
                    SfdcBulkApi.pushDataToSfdc(load.getsObject(), load.getOperation(),new File(load.getFile()));
                }

            }
		}
		catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			//Closing Db Connection
			db.close();
		}
	}
	
	/**
	 * Read line by line and replace the values of picklist with its corresponding ID value
	 * @param transFile
	 * @param pickListMap
	 * @return
	 * @throws IOException
	 */
	public File doPickListTransformation(File transFile, Map<String, String> pickListMap) throws IOException {
		// TODO Auto-generated method stub
		CSVReader reader = new CSVReader(new FileReader(transFile));
		String[] cols;
		File outputFile = new File(resDir+"job_final.csv");
		CSVWriter writer = new CSVWriter(new FileWriter(outputFile), ',', '"', '\\', "\n");
		cols = reader.readNext();
		while(cols != null) {
			for(int i = 0; i < cols.length; i++) {
				if(pickListMap.get(cols[i]) != null){
					cols[i] = pickListMap.get(cols[i]);
				}
			}
			writer.writeNext(cols);
			writer.flush();
			cols = reader.readNext();
		}
		
		return outputFile;
	}
	
	/**
	 * Simple Clean Up Operation, It queries the ID by pull mechanism and uses the same to delete the id with push mechanism
	 * @param sObject - Name of the object where data should be deleted.
     * @param condition - Where condition need to be supplied - {Ex: JBCXM__Stage__r.Name = 'New Business' (or) JBCXM__ASV__c > 2000}
	 * @throws IOException
	 */
	public void cleanUp(String sObject, String condition) throws IOException {
		System.out.println("Pulling " + sObject);
		String query = QueryBuilder.buildSOQLQuery(sObject, "Id");
		System.out.println("Pull Query : " + query);
        if(condition !=null) {
            query = query+" Where "+condition;
            System.out.println("Where Attached Pull Query : " + query);
        }
		String path = "./resources/datagen/process/" + sObject + "_cleanup.csv";
		System.out.println("Output File Loc : " + path);
		SfdcBulkApi.pullDataFromSfdc(sObject, query, path);
		File f = new File(path);
		if(f.exists())
			System.out.println("Pull Completed");
		else
			System.out.println("Pull Failed");
		
		System.out.println("Now Lets Delete some data");
		SfdcBulkApi.pushDataToSfdc(sObject, "delete", f);
		System.out.println("push done");
		
	}
	
	/**
	 * Simple Clean Up Operation, It queries the ID by pull mechanism and uses the same to delete the id with push mechanism
	 * @param sObject
	 * @throws IOException
	 */
	public void cleanUp(String sObject, int limit) throws IOException {
		//I need to write logic considering governor limits
		System.out.println("Pulling " + sObject);
		String query = QueryBuilder.buildSOQLQuery(sObject, limit, "Id");
		System.out.println("Pull Query : " + query);
		String path = "./resources/datagen/process/" + sObject + "_cleanup.csv";
		System.out.println("Output File Loc : " + path);
		SfdcBulkApi.pullDataFromSfdc(sObject, query, path);
		File f = new File(path);
		if(f.exists())
			System.out.println("Pull Completed");
		else
			System.out.println("Pull Failed");
		
		System.out.println("Now Lets Delete some data");
		SfdcBulkApi.pushDataToSfdc(sObject, "delete", f);
		System.out.println("push done");
		
	}

	private static Map<String, String> convertPickListToMap(String pickListFile) throws IOException {
		File pickList = new File(pickListFile);
		Map<String, String> pickListMap = new HashMap<String, String>();
		CSVReader reader = new CSVReader(new FileReader(pickList));
		String[] nextLine;
		while((nextLine = reader.readNext()) != null) {
			pickListMap.put(nextLine[0],nextLine[1]);
		}
		return pickListMap;
	}


    public File resolveNameSpace(String  fileName) throws IOException {
        TestEnvironment env =new TestEnvironment();
        boolean isPackage = Boolean.valueOf(env.getProperty("sfdc.managedPackage"));
        if(!isPackage) {
            CSVReader csvReader = new CSVReader(new FileReader(fileName));

            List<String[]> csvData = csvReader.readAll();
            String[] headerRows = csvData.get(0);
            for(int i=0; i< headerRows.length; i++) {
                if(!isPackage) {
                    headerRows[i] = headerRows[i].replaceAll("JBCXM__", "");
                }
            }
            csvData.remove(0);
            csvData.add(0, headerRows);
            csvReader.close();

            CSVWriter csvWriter = new CSVWriter(new FileWriter("./resources/process/Temp.csv"));
            csvWriter.writeAll(csvData);
            csvWriter.flush();
            csvWriter.close();

            File f = new File("./testdata/sfdc/Temp.csv");
            return f;
        } else {
            return new File(fileName);
        }


    }

}
