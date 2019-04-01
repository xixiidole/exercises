package demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFName;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Base64;
import java.util.Base64.Decoder;

import com.jfinal.core.Controller;

public class HelloController extends Controller {
	public void index() {
	       renderText("Hello JFinal World.");
	}
	
	public static void readExcel() {
		String path = "D:\\BaiduNetdiskDownload\\1.xlsx";
		File f = new File(path);
		try {
			InputStream is = new FileInputStream(f);
			
			XSSFWorkbook wb = new XSSFWorkbook(is);
			XSSFSheet sheet = wb.getSheetAt(0);
			String name = "";
			String tname= "";
			StringBuffer sbf = new StringBuffer();
			for(int i=sheet.getFirstRowNum();i<sheet.getLastRowNum();i++) {
				System.err.println("开始遍历..."+i);
				XSSFRow row = sheet.getRow(i);
				XSSFCell cellName = row.getCell(0);
				tname = cellName.getStringCellValue().toString();
				if(name.equals(tname)) {
					XSSFCell cell = row.getCell(2);
					cell.getStringCellValue().toString();
					sbf.append(cell.getStringCellValue().toString());
				}else {
					if(sbf.length()>0) {
						System.err.println("开始解析图片...");
						generateImage(sbf.toString(),"D:\\\\BaiduNetdiskDownload\\Pics\\"+name.replace("|","_")+".png");
					}
					
					name = tname;
					XSSFCell cell = row.getCell(2);
					cell.getStringCellValue().toString();
					sbf.setLength(0);
					sbf.append(cell.getStringCellValue().toString());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean generateImage(String imgStr, String path) {
		if (imgStr == null) return false;
		Decoder decoder = Base64.getDecoder();
		try {
			// 解密
			byte[] b = decoder.decode(imgStr.getBytes());
			// 处理数据
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			OutputStream out = new FileOutputStream(path);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void main(String[] args) {
		readExcel();
	}
}
