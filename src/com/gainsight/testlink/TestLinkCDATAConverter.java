package com.gainsight.testlink;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Class is written to Convert excel sheet usecases to Test link format.
 * @author gainsight1
 *
 */
public class TestLinkCDATAConverter {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws BiffException 
	 */
	public static void main(String[] args) throws BiffException, IOException {
		// TODO Auto-generated method stub

		System.out.println("Usage Java TestLinkCDATAConverter <path of excel file> <sheet name>");
		Workbook workbook;
		Sheet dataSheet;
		if(args.length == 1)
			workbook = Workbook.getWorkbook(new File(args[0]));
		else
			workbook = Workbook.getWorkbook(new File("TestLinkTemplate.xls"));
		
		// Get the sheet name
		if(args.length == 2)
			dataSheet = workbook.getSheet(args[1]);
		else
			dataSheet = workbook.getSheet("Sheet5");
		
		//GATHERING ALL COLUMN DATA
		Cell[] c = dataSheet.getColumn(0);
		ArrayList<String> list = new ArrayList<String>();
		for(Cell content : c) {
			String str = content.getContents();
			if(!str.isEmpty())
				list.add(str);
		}

		businessLogic1(dataSheet, list);
		System.out.println("Done");
		
	}

	private static void businessLogic1(Sheet dataSheet, ArrayList<String> tcList) {
		//BUILDING LOGIC to loop through test case id
		File xmlFile = new File("testLinkFinalOutput1.xml");
		Element rootNode = new Element("testcases");
		Document doc = new Document(rootNode);
		FileOutputStream fos = null;
		
		for(int i = 0 ; i < tcList.size(); i++) {
			if(tcList.get(i).equals("End")) break;
			if(tcList.get(i).equals("Testcase ID")) continue;
			int cellStart = dataSheet.findCell(tcList.get(i)).getRow();
			int cellEnd = dataSheet.findCell(tcList.get(i+1)).getRow();
			//Adding testcase to root note testcases
			businessLogic2(rootNode, dataSheet, cellStart, cellEnd);		
		}
		
		XMLOutputter xmlOutput = new XMLOutputter(); 
		// display nice
		xmlOutput.setFormat(Format.getPrettyFormat());
		try {
			fos = new FileOutputStream(xmlFile, false);
			Writer writer = new OutputStreamWriter(fos, "utf-8");
			xmlOutput.output(doc, writer);
			xmlOutput.output(doc, System.out);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void businessLogic2(Element rootNode, Sheet dataSheet, int cellStart,
			int cellEnd) {
		// TODO Auto-generated method stub
		System.out.println("logic 2 " + cellStart + " " + cellEnd);
		
		try {
			Element testCase = new Element("testcase");
			/*Element nodeOrder = new Element("node_order");
			Element externalId = new Element("externalid");
			Element version = new Element("version");*/
			Element summary = new Element("summary");
			Element preconditions = new Element("preconditions");
			Element executionType = new Element("execution_type");
			Element importance = new Element("importance");
			Element steps = new Element("steps");
			
			rootNode.addContent(testCase);
			
			/*testCase.addContent(nodeOrder);
			testCase.addContent(externalId);
			testCase.addContent(version);*/
			testCase.addContent(summary);
			testCase.addContent(preconditions);
			testCase.addContent(executionType);
			testCase.addContent(importance);
			testCase.addContent(steps);
			
			//Reading Cell data to construct the test link xml data.
			for(int i = cellStart ; i < cellEnd ; i++){
				Cell[] c = dataSheet.getRow(i);
				if(i == cellStart) {
					testCase.setAttribute(new Attribute("name", c[0].getContents() + "(" + c[1].getContents() + ")"));
					summary.setContent(new CDATA("<p>" + c[1].getContents() + "</p><p>&nbsp;</p>"));
					if(c[2].getContents().isEmpty())
						preconditions.setContent(new CDATA("<p>" + "Please Enter the Pre-Conditions Here" + "</p>"));
					else
						preconditions.setContent(new CDATA("<p>" + c[2].getContents() + "</p>"));
					
					executionType.setContent(new CDATA(c[3].getContents()));
					importance.setContent(new CDATA(c[4].getContents()));
				}
				
				Element step = new Element("step");
				Element step_number = new Element("step_number");
				Element actions = new Element("actions");
				Element expectedresults = new Element("expectedresults");
				Element execution_type = new Element("execution_type");
				step.addContent(step_number);
				step.addContent(actions);
				step.addContent(expectedresults);
				step.addContent(execution_type);
				
				steps.addContent(step);
				
				step_number.setContent(new CDATA(c[5].getContents()));
				actions.setContent(new CDATA(c[6].getContents()));
				if(c.length > 7)
					expectedresults.setContent(new CDATA(c[7].getContents()));
				else
					expectedresults.setContent(new CDATA("Please Enter the Expected Result"));
				execution_type.setContent(new CDATA("2"));
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
