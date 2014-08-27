package com.gainsight.sfdc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.pdfbox.PDFToImage;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PDFTestUtil {
	private static File TMP_DIR=new File("pdf_temp");
	
		
	public static boolean compareTwoPDFs(String filePath1, String filePath2) {
		boolean same = true;
		if (!TMP_DIR.exists()){
			TMP_DIR.mkdir();
		}
		try {
			Path pdf1=copyToTempFile(filePath1);
			Path pdf2=copyToTempFile(filePath2);
			String pdf1Path= pdf1.toString();
			String pdf2Path=pdf2.toString();
			PDDocument doc1 = PDDocument.load(pdf1Path);
			PDDocument doc2 = PDDocument.load(pdf2Path);
			if (doc1.getNumberOfPages() == doc2.getNumberOfPages()) {
				String[] args1 = { pdf1Path};
				String[] args2 = {  pdf2Path};
				PDFToImage.main(args1);
				PDFToImage.main(args2);
				for (int i = 1; i <= doc1.getNumberOfPages(); i++) {
					String image1=pdf1Path.replace(".pdf", i+".jpg");
					String image2=pdf2Path.replace(".pdf", i+".jpg");
					System.out.println("PDF Comparision : Page " + i);
					if (!md5(image1).equals( md5(image2))) {
						System.out.println("PDF comparision failed at page " + i);
						same = false;
						break;
					}
				}
			} else {
				same = false;
			}
		} catch (Exception e) {
			same=false;
			e.printStackTrace();
		}
		return same;
	}

	public static String md5(String fileName) throws NoSuchAlgorithmException,
			IOException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		FileInputStream fis = new FileInputStream(fileName);
		byte[] dataBytes = new byte[1024];
		int nread = 0;
		while ((nread = fis.read(dataBytes)) != -1) {
			md.update(dataBytes, 0, nread);
		}
		;
		fis.close();
		byte[] mdbytes = md.digest();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16)
					.substring(1));
		}
		return sb.toString();
	}
	
	private static Path copyToTempFile(String filePath) throws IOException{
		Path file1=FileSystems.getDefault().getPath(filePath);
		Path file2=FileSystems.getDefault().getPath(File.createTempFile("test", ".pdf",TMP_DIR).getAbsolutePath());
		Files.copy(file1,file2,StandardCopyOption.REPLACE_EXISTING);
		return file2;		
	}
		
}
