package com.gainsight.sfdc.util.datagen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.gainsight.sfdc.SalesforceConnector;
import com.gainsight.sfdc.beans.SFDCInfo;
import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.SfdcConfig;
import com.gainsight.util.ConfigLoader;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

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

public class DataETL implements IJobExecutor {

	public static String pickListObject = "JBCXM__PickList__c";
	static Map<String, String> pMap;
	
	static String dropTableQuery = "DROP TABLE IF EXISTS ";
    static String userDir = System.getProperty("basedir", ".");
	static String resDir = userDir+"/resources/datagen/";
	static JobInfo jobInfo;
	static ObjectMapper mapper = new ObjectMapper();
	static H2Db db;

	public static SfdcConfig sfdcConfig = ConfigLoader.getSfdcConfig();


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			DataETL gen = new DataETL();
			jobInfo = mapper.readValue(new FileReader( "./testdata/sfdc/reporting/jobs/Job_Reports.txt"), JobInfo.class);
			gen.execute(jobInfo);
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

            //dateValue Processing
            JobInfo.DateProcess  dateProcess = jobInfo.getDateProcess();
            if(dateProcess != null) {
                File inputFile = new File(userDir+dateProcess.getInputFile());
                File outputFile = new File(userDir+dateProcess.getOutputFile());
                outputFile = FileProcessor.getDateProcessedFile(inputFile, outputFile, dateProcess.getFields(), null);

            }


			//Pre Process Especially for Usage Data
			PreProcess preProcess = jobInfo.getPreProcessRule();
			if(preProcess != null){
				File inputFile = new File(userDir+preProcess.getInputFile());
				File outputFile = new File(userDir+preProcess.getOutputFile());
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
				SfdcBulkApi.pullDataFromSfdc(pull.getTable(), query, userDir+pull.getOutputFileLoc());
			}
			else {
				System.out.println("Nothing to extract. Check the Job Details");
            }
			//Mapping and Transformation Code.
			Transform transform = jobInfo.getTransformationRule();
			File transFile = null;
            File pushFile  = null;
			LinkedHashMap<String, List<Columns>> finalFields= new LinkedHashMap<String, List<Columns>>();
			ArrayList<String> joinColumn = new ArrayList<String>();
            SfdcLoad load = jobInfo.getLoadRule();
			if(transform != null && !transform.toString().contains("null")) {
				if(transform.isJoin()) {
					transFile = new File(userDir+transform.getOutputFileLoc());
					ArrayList<TableInfo> tables = transform.getTableInfo();
					for(TableInfo tableInfo : tables) {
						db.executeStmt(dropTableQuery + tableInfo.getTable());
						String createTableFromCSv = "CREATE TABLE " + tableInfo.getTable() + " AS SELECT * FROM CSVREAD('"+ userDir+tableInfo.getCsvFile() +"')";
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
						
					db.executeStmt("call CSVWRITE ( '" + userDir+transform.getOutputFileLoc() + "', '" + finalQuery + "' ) ");
					if(transFile.exists())
						System.out.println("Success");
					else
						System.out.println("Something went wrong");
				}
				//Applying picklist transformation

				if(transform.isPicklist()) {
                    //Pulling Pick List Object
                    String picklistPath = resDir + "process/" + pickListObject + ".csv";
                    String query1 = QueryBuilder.buildSOQLQuery(pickListObject, "JBCXM__SystemName__c", "Id");
                    SfdcBulkApi.pullDataFromSfdc(pickListObject, query1, picklistPath);

                    //Converting Pick List to Hash Map
                    pMap = convertPickListToMap(picklistPath);
                    System.out.println(pMap);
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
                    SfdcBulkApi.pushDataToSfdc(FileUtil.resolveNameSpace(load.getsObject(), sfdcConfig.getSfdcNameSpace()), load.getOperation(),
                            (transform.isPicklist() ) ? pushFile : resolveNameSpace(userDir + load.getFile()), load.getExternalIDField());
                } else{
                    SfdcBulkApi.pushDataToSfdc(FileUtil.resolveNameSpace(load.getsObject(), sfdcConfig.getSfdcNameSpace()), load.getOperation(),
                            (transform.isPicklist() ) ? pushFile : resolveNameSpace(userDir+load.getFile()));
                }
            } else{ //in case there is no transform part...only loading
            	if (load!=null) {
            	if(load.getOperation().equals("upsert")) {
                    SfdcBulkApi.pushDataToSfdc(FileUtil.resolveNameSpace(load.getsObject(), sfdcConfig.getSfdcNameSpace()), load.getOperation(),resolveNameSpace(userDir+load.getFile()),load.getExternalIDField());
                }
            	else {
                    SfdcBulkApi.pushDataToSfdc(FileUtil.resolveNameSpace(load.getsObject(), sfdcConfig.getSfdcNameSpace()), load.getOperation(),resolveNameSpace(userDir+load.getFile()));
                }
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
            new File(System.getProperty("user.home")+"/"+jobInfo.getJobName()+".h2.db").delete();
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
		File outputFile = new File(resDir+"process/job_final.csv");
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
	 * @param objectName - Name of the object where data should be deleted.
     * @param condition - Where condition need to be supplied - {Ex: JBCXM__Stage__r.Name = 'New Business' (or) JBCXM__ASV__c > 2000}
	 * @throws IOException
	 */
	public void cleanUp(String objectName, String condition) throws IOException {
        String query = QueryBuilder.buildSOQLQuery(objectName, "Id");
        if(condition !=null) {
            query = query+" Where "+condition;
        }
        Log.info("Query : " +query);
        SfdcBulkApi.cleanUp(query);
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

    /**
     * Resolves Name Space for only the header row.
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public File resolveNameSpace(String  fileName) throws IOException {
        boolean isPackage = sfdcConfig.getSfdcManagedPackage();
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

            CSVWriter csvWriter = new CSVWriter(new FileWriter(userDir+"/resources/datagen/process/Temp.csv"));
            csvWriter.writeAll(csvData);
            csvWriter.flush();
            csvWriter.close();

            File f = new File(userDir+"/resources/datagen/process/Temp.csv");
            return f;
        } else {
            return new File(fileName);
        }
    }
}
