package demo.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Base64.Decoder;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelBase64ToImage {
	/**
	 * 读取第三列的base64字符串，转图片（不会过滤标题，所以要将标题列删掉，多条相同人的数据需要做好排序）<br/>
	 * 数据格式如下：<br/>
	 * <table border="true">
	 * <tr><td>保存名称</td><td>多条数据序号</td><td>图片字符串</td></tr>
	 * <tr><td>张**</td><td>1</td><td>ABxC...</td></tr>
	 * <tr><td>张**</td><td>2</td><td>74hs...</td></tr>
	 * </table>
	 */
	public static void readExcel(String filePath, String savePath, String prefix) {
		if(filePath==null || filePath.length()==0) {
			System.err.println("excel文件位置错误...");
			return;
		}
		if(savePath==null || savePath.length()==0) {
			System.err.println("图片保存位置错误...");
			return;
		}
		if(savePath==null || savePath.length()==0) {
			prefix = ".png";
		}
		File f = new File(filePath);
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
						generateImage(sbf.toString(),savePath+name.replace("|","_")+prefix);
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
		readExcel("D:\\BaiduNetdiskDownload\\1.xlsx","D:\\BaiduNetdiskDownload\\Pics\\",".png");
	}
}
