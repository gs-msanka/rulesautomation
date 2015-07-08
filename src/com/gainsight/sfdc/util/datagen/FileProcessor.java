package com.gainsight.sfdc.util.datagen;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.gainsight.bigdata.pojo.MDADateProcessor;
import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import org.apache.commons.lang3.time.DateUtils;

public class FileProcessor {

	static String resDir = "./resources/datagen/";
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File inputFile = new File("./testdata/sfdc/UsageData/Data/XXX.csv");
		File outputFile = new File(resDir + "process/InstanceMonthlyFinal.csv");
		System.out.println("File exists : " + outputFile.exists());
		File f = generateMonthlyUsageData(inputFile, "Monthly", outputFile);
		System.out.println("File exists : " + f.exists());
	}
	
	public static File generateMonthlyUsageData(File inputFile, String fieldName, File outputFile) throws IOException {
		CSVReader reader = new CSVReader(new FileReader(inputFile));
		String[] cols;
		int fieldIndex = -1;
        outputFile.getParentFile().mkdirs();
		CSVWriter writer = new CSVWriter(new FileWriter(outputFile), ',', '"', '\\', "\n");
		cols = reader.readNext(); 
		System.out.println(cols.length);
		writer.writeNext(cols);
		writer.flush();
		for(String str : cols) {
			System.out.println(str);
		}
		//TO find the index position of field for which we are going to add Monthly Date Diff.
		for(int i = 0; i < cols.length; i++) {
			if(cols[i].trim().equalsIgnoreCase(fieldName)) {
				fieldIndex	= i;
				cols = reader.readNext();
				break;
			}
		}
		
		if(fieldIndex > -1) {
		
			while(cols != null) {
				int monthDiff = Integer.parseInt(cols[fieldIndex]);
				cols[fieldIndex] = DateUtil.addMonths(Calendar.getInstance(), monthDiff, "yyyy-MM-dd");
				writer.writeNext(cols);
				writer.flush();
				cols = reader.readNext();
			}
			
		}
		else
			throw new RuntimeException("The input file : " + inputFile.getAbsolutePath() + " did not have the required field name : " + fieldName);
		
		return outputFile;
	}
	
	public static File generateWeeklyUsageData(File inputFile, String fieldName, File outputFile) throws IOException {
		CSVReader reader = new CSVReader(new FileReader(inputFile));
		String[] cols;
		int fieldIndex = -1;
        outputFile.getParentFile().mkdir();
		CSVWriter writer = new CSVWriter(new FileWriter(outputFile), ',', '"', '\\', "\n");
		cols = reader.readNext(); 
		System.out.println(cols.length);
		writer.writeNext(cols);
		writer.flush();
		for(String str : cols) {
			System.out.println(str);
		}
		//TO find the index position of field for which we are going to add Monthly Date Diff.
		for(int i = 0; i < cols.length; i++) {
			if(cols[i].trim().equalsIgnoreCase(fieldName)) {
				fieldIndex	= i;
				cols = reader.readNext();
				break;
			}
		}
		
		if(fieldIndex > -1) {
		
			while(cols != null) {
				int weekDiff = Integer.parseInt(cols[fieldIndex]);
				cols[fieldIndex] = DateUtil.addWeeks(Calendar.getInstance(), weekDiff, "yyyy-MM-dd");
				writer.writeNext(cols);
				writer.flush();
				cols = reader.readNext();
			}
			
		}
		else
			throw new RuntimeException("The input file : " + inputFile.getAbsolutePath() + " did not have the required field name : " + fieldName);
		
		return outputFile;
	}
	
	public static File generateResponseSubmissionDate(File inputFile, String fieldName, File outputFile) throws IOException {
		CSVReader reader = new CSVReader(new FileReader(inputFile));
		String[] cols;
		int fieldIndex = -1;
        outputFile.getParentFile().mkdir();
		CSVWriter writer = new CSVWriter(new FileWriter(outputFile), ',', '"', '\\', "\n");
		cols = reader.readNext(); 
		System.out.println(cols.length);
		writer.writeNext(cols);
		writer.flush();
		for(String str : cols) {
			System.out.println(str);
		}
		//TO find the index position of field for which we are going to add Monthly Date Diff.
		for(int i = 0; i < cols.length; i++) {
			if(cols[i].trim().equalsIgnoreCase(fieldName)) {
				fieldIndex	= i;
				cols = reader.readNext();
				break;
			}
		}
		
		if(fieldIndex > -1) {
		
			while(cols != null) {
				int daysDiff = Integer.parseInt(cols[fieldIndex]);
				cols[fieldIndex] = DateUtil.addDays(Calendar.getInstance(),daysDiff, "yyyy-MM-dd'T'HH:mm:ss");
				writer.writeNext(cols);
				writer.flush();
				cols = reader.readNext();
			}
			
		}
		else
			throw new RuntimeException("The input file : " + inputFile.getAbsolutePath() + " did not have the required field name : " + fieldName);
		
		return outputFile;
	}

    public static File getDateProcessedFile(JobInfo jobInfo, Date date) throws IOException {
        return getDateProcessedFile(new File(Application.basedir+jobInfo.getDateProcess().getInputFile()),
                new File(Application.basedir+jobInfo.getDateProcess().getOutputFile()), jobInfo.getDateProcess().getFields(), date);
    }

    public static File getFormattedCSVFile(JobInfo.CSVFormat csvFormatter) throws IOException {
        if(csvFormatter==null) {
            throw new IllegalArgumentException("CSV Formatter Should not be Null");
        }
        Log.info("Started Creating a new CSV with specified format... "+csvFormatter.getOutputFile());
        if(csvFormatter.getInputFile()==null || csvFormatter.getOutputFile()==null || csvFormatter.getCsvProperties() ==null) {
            throw new IllegalArgumentException("Input File, Output File, CSV Properties are mandatory." +
            " Input File is : "+csvFormatter.getInputFile() +
            " Output Fiel is : "+csvFormatter.getOutputFile() +
            " Csv properties : "+csvFormatter.getCsvProperties().toString());
        }

        File outputFile = new File(Application.basedir+csvFormatter.getOutputFile());
        outputFile.getParentFile().mkdir();
        CSVReader reader = new CSVReader(new FileReader(Application.basedir+csvFormatter.getInputFile()));
        CSVWriter writer = new CSVWriter(new FileWriter(outputFile), csvFormatter.getCsvProperties().getSeparator(),
                                    csvFormatter.getCsvProperties().getQuoteChar(), csvFormatter.getCsvProperties().getEscapeChar(), csvFormatter.getCsvProperties().getLineEnd());
        List<String[]> rows = reader.readAll();

        writer.writeAll(rows);
        writer.flush();
        writer.close();
        reader.close();
        Log.info("CSV File created Successfully. " + csvFormatter.getOutputFile());
        return outputFile;
    }

    public static File getDateProcessedFile(File inputFile, File outputFile, ArrayList<JobInfo.DateProcess.Fields> fields, Date date) throws IOException {
        Log.info("Started Date Processing....");

        outputFile.getParentFile().mkdirs();
        CSVReader reader = new CSVReader(new FileReader(inputFile));
        String[] cols;
        int fieldIndex = -1;
        CSVWriter writer = new CSVWriter(new FileWriter(outputFile), ',', '"', '\\', "\n");
        cols = reader.readNext();
        System.out.println(cols.length);
        writer.writeNext(cols);
        writer.flush();
        for(String str : cols) {
            System.out.println(str);
        }

        if(fields!=null) {
            for(JobInfo.DateProcess.Fields field : fields) {
                for(int i=0; i < cols.length ; i++) {
                    if(cols[i].trim().equalsIgnoreCase(field.getFieldName())) {
                        field.setFieldIndex(i);
                    }
                }
                if(!(field.getFieldIndex() > -1)) {
                    throw new RuntimeException("Field not present in the file supplied " +field.getFieldName());
                }
            }
        }
        cols = reader.readNext();
        while(cols != null) {
            if(fields!=null) {
                for(JobInfo.DateProcess.Fields field : fields) {
                    try {
                        String val = cols[field.getFieldIndex()];
                        if (val == null || val == "") {
                            System.out.println("No field Value..., just skipping date/datetime conversion...");
                        } else {
                            int value = Integer.parseInt(cols[field.getFieldIndex()]);
                            if(date!=null) {
                                cols[field.getFieldIndex()] = getDate(field, value, date);
                            } else {
                                cols[field.getFieldIndex()] = getDate(field, value);
                            }
                        }

                    } catch (NumberFormatException e) {
                        //Just Ignore.
                    }
                }
            }
            writer.writeNext(cols);
            writer.flush();
            cols = reader.readNext();
        }
        Log.info("Completed Date Processing.");
        return outputFile;
    }

    private static String getDate(JobInfo.DateProcess.Fields field, int value) {
        if(field.isDateTime())  {
            if(field.isDaily()) {
                return DateUtil.addDays(Calendar.getInstance(), value, field.getDateFormat()==null ? "yyyy-MM-dd'T'HH:mm:ss" : field.getDateFormat());
            } else if (field.isWeekly()){
                return DateUtil.addWeeks(Calendar.getInstance(), value, field.getDateFormat()==null ? "yyyy-MM-dd'T'HH:mm:ss" : field.getDateFormat());
            } else {
                return DateUtil.addMonths(Calendar.getInstance(), value, field.getDateFormat()==null ? "yyyy-MM-dd'T'HH:mm:ss" : field.getDateFormat());
            }
        } else {
            if(field.isDaily()) {
                return DateUtil.addDays(Calendar.getInstance(), value, field.getDateFormat()==null ? "yyyy-MM-dd" :field.getDateFormat());
            } else if (field.isWeekly()){
                return DateUtil.addWeeks(Calendar.getInstance(), value, field.getDateFormat()==null ? "yyyy-MM-dd" :field.getDateFormat());
            } else {
                return DateUtil.addMonths(Calendar.getInstance(), value, field.getDateFormat()==null ? "yyyy-MM-dd" :field.getDateFormat());
            }
        }
    }

    private static String getDate(JobInfo.DateProcess.Fields field, int value, Date date) {
        if(field.isDateTime())  {
            if(field.isDaily()) {
                return DateUtil.addDays(date, value, field.getDateFormat()==null ? "yyyy-MM-dd'T'HH:mm:ss" : field.getDateFormat());
            } else if (field.isWeekly()){
                return DateUtil.addWeeks(date, value, field.getDateFormat()==null ? "yyyy-MM-dd'T'HH:mm:ss" : field.getDateFormat());
            } else {
                return DateUtil.addMonths(date , value, field.getDateFormat()==null ? "yyyy-MM-dd'T'HH:mm:ss" : field.getDateFormat());
            }
        } else {
            if(field.isDaily()) {
                return DateUtil.addDays(date, value, field.getDateFormat()==null ? "yyyy-MM-dd" :field.getDateFormat());
            } else if (field.isWeekly()){
                return DateUtil.addWeeks(date, value, field.getDateFormat()==null ? "yyyy-MM-dd" :field.getDateFormat());
            } else {
                return DateUtil.addMonths(date, value, field.getDateFormat()==null ? "yyyy-MM-dd" :field.getDateFormat());
            }
        }
    }

    private static String getDate(MDADateProcessor.DateColumnProperties dateProperties, int amount, Date date) {
        String formattedDate = "";
        Date actualDate = DateUtils.addDays(date, amount);
        if(dateProperties.isMonth()) {
            formattedDate = DateUtil.getMonthFirstDate(actualDate, dateProperties.getDateFormat());
        } else if(dateProperties.isQuarter()) {
            formattedDate = DateUtil.getQuarterFirstDate(actualDate, dateProperties.getDateFormat());
        } else if(dateProperties.isYear()) {
            formattedDate = String.valueOf(DateUtil.getYear(actualDate));
        } else if(dateProperties.isWeekLabel()) {
           formattedDate = DateUtil.getWeekLabelDate(actualDate,dateProperties.getWeekStartsOn(), dateProperties.isUsersEndDate(), dateProperties.getDateFormat());
        } else {
            formattedDate = DateUtil.getFormattedDate(actualDate, dateProperties.getDateFormat());
        }
        return formattedDate;
    }


    public static File getDateProcessedFile(MDADateProcessor mdaDateProcessor, Date date) throws IOException {
        Log.info("Started Date Processing....");

        File outputFile = new File(Application.basedir+mdaDateProcessor.getOutputFilePath());
        outputFile.getParentFile().mkdirs();
        CSVReader reader = new CSVReader(new FileReader(new File(Application.basedir+mdaDateProcessor.getInputFilePath())));

        String[] cols;
        CSVWriter writer = new CSVWriter(new FileWriter(outputFile), ',', '"', '\\', "\n");
        cols = reader.readNext();
        Log.info("No of Columns Found : " +cols.length);
        writer.writeNext(cols);
        writer.flush();
        for(String str : cols) {
            Log.info("Column : " +str);
        }

        if(mdaDateProcessor.getDateColumnProperties()!=null) {
            for(MDADateProcessor.DateColumnProperties dateProperties : mdaDateProcessor.getDateColumnProperties()) {
                for(int i=0; i < cols.length ; i++) {
                    if(cols[i].trim().equalsIgnoreCase(dateProperties.getFieldName())) {
                        dateProperties.setFieldIndex(i);
                    }
                }
                if(!(dateProperties.getFieldIndex() > -1)) {
                    throw new RuntimeException("Field not present in the file supplied " +dateProperties.getFieldName());
                }
            }
        }

        cols = reader.readNext();
        while(cols != null) {
            if(mdaDateProcessor.getDateColumnProperties()!=null) {
                for(MDADateProcessor.DateColumnProperties dateProperties : mdaDateProcessor.getDateColumnProperties()) {
                    try {
                        String val = cols[dateProperties.getFieldIndex()];
                        if (val == null || val == "") {
                            Log.info("No field Value..., just skipping date/datetime conversion...");
                        } else {
                            int value = Integer.parseInt(cols[dateProperties.getFieldIndex()]);
                            cols[dateProperties.getFieldIndex()] = getDate(dateProperties, value, date);
                        }

                    } catch (NumberFormatException e) {
                        Log.error("Unable to do date formatting" , e);
                    }
                }
            }
            writer.writeNext(cols);
            writer.flush();
            cols = reader.readNext();
        }
        Log.info("Completed Date Processing.");
        return outputFile;
    }


}
