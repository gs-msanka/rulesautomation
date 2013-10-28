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
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class TestLinkFormatConverter {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws BiffException 
	 */
	public static void main(String[] args) throws BiffException, IOException {
		// TODO Auto-generated method stub

		Workbook workbook = Workbook.getWorkbook(new File("TestLinkTemplate.xls"));
		// Get the sheet name
		Sheet dataSheet = workbook.getSheet("Sheet5");
		
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
		// TODO Auto-generated method stub
		//BUILDING LOGIC to loop through test case id
		for(int i = 0 ; i < tcList.size(); i++) {
			if(tcList.get(i).equals("End")) break;
			if(tcList.get(i).equals("Testcase ID")) continue;
			int cellStart = dataSheet.findCell(tcList.get(i)).getRow();
			int cellEnd = dataSheet.findCell(tcList.get(i+1)).getRow();
			businessLogic2(dataSheet, cellStart, cellEnd);		
		}
	}

	private static void businessLogic2(Sheet dataSheet, int cellStart,
			int cellEnd) {
		// TODO Auto-generated method stub
		System.out.println("logic 2 " + cellStart + " " + cellEnd);
		FileOutputStream fos = null;
		try {
			String valStart = "<![CDATA[";
			String valEnd = "]]";
			
//			SAXBuilder builder = new SAXBuilder();
			File xmlFile = new File("testLinkFinalOutput.xml");
			//Document doc = (Document) builder.build(xmlFile);
			Element rootNode = new Element("testcases");
			Document doc = new Document(rootNode);
//			Element rootNode = doc.getRootElement();
			Element testCase = new Element("testcase");
			Element nodeOrder = new Element("node_order");
			Element externalId = new Element("externalid");
			Element version = new Element("version");
			Element summary = new Element("summary");
			Element preconditions = new Element("preconditions");
			Element executionType = new Element("execution_type");
			Element importance = new Element("importance");
			Element steps = new Element("steps");
			
			rootNode.addContent(testCase);
			
			testCase.addContent(nodeOrder);
			testCase.addContent(externalId);
			testCase.addContent(version);
			testCase.addContent(summary);
			testCase.addContent(preconditions);
			testCase.addContent(executionType);
			testCase.addContent(importance);
			testCase.addContent(steps);
			
			for(int i = cellStart ; i < cellEnd ; i++){
				Cell[] c = dataSheet.getRow(i);
				if(i == cellStart) {
					testCase.setAttribute(new Attribute("name", c[0].getContents() + "(" + c[1].getContents() + ")"));
					nodeOrder.setText(valStart + valEnd);
					externalId.setText(valStart + valEnd);
					version.setText(valStart + valEnd);
					summary.setText(valStart + "<p>" + c[1].getContents() + "</p><p>&nbsp;</p>" + valEnd);
					if(c[2].getContents().isEmpty())
						preconditions.setText(valStart + "<p>" + "Please Enter the Pre-Conditions Here" + "</p>" + valEnd);
					else
						preconditions.setText(valStart + "<p>" + c[2].getContents() + "</p>" + valEnd);
					
					executionType.setText(valStart + c[3].getContents() + valEnd);
					importance.setText(valStart + c[4].getContents() + valEnd);
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
				
				step_number.setText(valStart + c[5].getContents() + valEnd);
				actions.setText(valStart + c[6].getContents() + valEnd);
				if(c.length > 7)
					expectedresults.setText(valStart + c[7].getContents() + valEnd);
				else
					expectedresults.setText(valStart + "Please Enter the Expected Result" + valEnd);
				execution_type.setText(valStart + "2" + valEnd);
			}
			
			XMLOutputter xmlOutput = new XMLOutputter(); 
			// display nice
			xmlOutput.setFormat(Format.getPrettyFormat());
			fos = new FileOutputStream(xmlFile);
			Writer writer = new OutputStreamWriter(fos, "utf-8");
			xmlOutput.output(doc, writer);
			xmlOutput.output(doc, System.out);
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		finally {
			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
