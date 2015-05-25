package com.gainsight.sfdc.util.datagen;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.testdriver.Application;

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

    public static File getDateProcessedFile(JobInfo jobInfo) throws IOException {
        return getDateProcessedFile(new File(Application.basedir+jobInfo.getDateProcess().getInputFile()),
                new File(Application.basedir+jobInfo.getDateProcess().getOutputFile()), jobInfo.getDateProcess().getFields());
    }

    public static File getDateProcessedFile(File inputFile, File outputFile, ArrayList<JobInfo.DateProcess.Fields> fields) throws IOException {
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
        cols = reader.readNext();
        while(cols != null) {
            for(JobInfo.DateProcess.Fields field : fields) {
                try {
                    String val = cols[field.getFieldIndex()];
                    if(val == null || val == "") {
                        System.out.println("No field Value..., just skipping date/datetime conversion...");
                    } else {
                        int value = Integer.parseInt(cols[field.getFieldIndex()]);
                        cols[field.getFieldIndex()] =  getDate(field, value);
                    }

                } catch (NumberFormatException e) {
                    //Just Ignore.
                }
            }
            writer.writeNext(cols);
            writer.flush();
            cols = reader.readNext();
        }
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

}
