package demo.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;

import demo.service.BillService;
import demo.service.FileService;
import freemarker.template.utility.StringUtil;

public class HelloController extends Controller {
	
	public void index() {
		//renderText("Hello JFinal World.");
	}
	
	public void test() {
		getPara("layoutKey");
		renderText("Hello JFinal World2.");
	}
	
	@ActionKey("/doodle")
	public void doodle(){
		render("hello/doodle.html");
	}
	
	@ActionKey("/iframe")
	public void iframe(){
		render("hello/iframe.html");
	}
	
	@ActionKey("/spin")
	public void bigPrice(){
		render("hello/spin.html");
	}
	
	@ActionKey("/click")
	public void click(){
		render("hello/click.html");
	}
	
	@ActionKey("/music")
	public void music(){
		render("hello/music.html");
	}
	
	@ActionKey("/undoneData")
	public void undoneData() {
		String JSID = getPara("JSID");
		try {
			OkHttpClient client = new OkHttpClient();

			MediaType mediaType = MediaType.parse("text/plain");
			RequestBody body = RequestBody.create(mediaType, "startIndex=0&filterId=-1&jql=assignee+%3D+currentUser()+AND+resolution+%3D+Unresolved+ORDER+BY+updatedDate+DESC&layoutKey=list-view");
			Request request = new Request.Builder()
			  .url("http://dev.gklqt.com:58082/jira/rest/issueNav/1/issueTable")
			  .post(body)
			  .addHeader("Accept", "*/*")
			  .addHeader("Accept-Encoding", "gzip, deflate")
			  .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
			  .addHeader("Connection", "keep-alive")
			  .addHeader("Content-Length", "131")
			  .addHeader("Content-Type", "text/plain")
			  .addHeader("Host", "dev.gklqt.com:58082")
			  .addHeader("Origin", "http://dev.gklqt.com:58082")
			  .addHeader("Referer", "http://dev.gklqt.com:58082/jira/issues/?filter=-1")
			  .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36")
			  .addHeader("X-Atlassian-Token", "nocheck")
			  .addHeader("X-Requested-With", "XMLHttpRequest")
			  .addHeader("Cookie", "JSESSIONID=B7BB83C773A9102F625DB7904FB09FCE,JSESSIONID=B7BB83C773A9102F625DB7904FB09FCE; JSESSIONID=E493E3BBE6A42684115B8F29AABAA553; atlassian.xsrf.token=B5KX-G33A-RGYP-HX5A|8fd8fe268a34c76314ed25be9485a7458fc56d44|lin")
			  .addHeader("Cache-Control", "no-cache")
			  .addHeader("Postman-Token", "190513f3-1813-4da8-bb41-b0ab0d9d2d18,b39ab1e1-a39a-4ca6-a3ea-07411b250750")
			  .addHeader("cache-control", "no-cache")
			  .addHeader("Charset", "UTF-8")
			  .build();

			Response response = client.newCall(request).execute();
			String x = response.body().string();
			System.err.println(x);
			renderText(x, "gbk");
		}catch(Exception e){
			e.printStackTrace();
			renderText("获取出错！", "utf-8");
		}
	}
	
	/**
	 * 返回获取到的是乱码，utf8 , gbk都试了还是乱码；
	 **/
	public void undoneDataXXX() {
		String JSID = getPara("JSID");
		InputStream is = null;
		try {
			URL url = new URL("http://dev.gklqt.com:58082/jira/rest/issueNav/1/issueTable");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
			conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
			conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
			conn.setRequestProperty("Content-Type", "text/plain");
			//conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", "131");
			conn.setRequestProperty("Cookie", "JSESSIONID="+JSID);
			conn.setRequestProperty("Host", "dev.gklqt.com:58082");
			conn.setRequestProperty("Origin", "http://dev.gklqt.com:58082");
			conn.setRequestProperty("Referer", "http://dev.gklqt.com:58082/jira/issues/?filter=-1");
			conn.setRequestProperty("X-Atlassian-Token", "nocheck");
			conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
	        conn.setRequestProperty("Charset", "UTF-8");
			//Cookie: JSESSIONID=F8F637AA8F79B9A42F20C2A24E5DCD28; atlassian.xsrf.token=B5KX-G33A-RGYP-HX5A|80a8f86f77f67d045673b2135f9478061abcd66f|lin
			//params
			String param = "startIndex=0&filterId=-1&jql=assignee+%3D+currentUser()+AND+resolution+%3D+Unresolved+ORDER+BY+updatedDate+DESC&layoutKey=list-view";
			DataOutputStream dos=new DataOutputStream(conn.getOutputStream());
	        dos.writeBytes(param);
	        dos.flush();
	        dos.close();
	        conn.connect();
	      //获得响应状态
	        int resultCode=conn.getResponseCode();
	        if(HttpURLConnection.HTTP_OK==resultCode){
	        	/*StringBuffer sb=new StringBuffer();
	        	String readLine = "";
	        	BufferedReader responseReader=new BufferedReader(new InputStreamReader(conn.getInputStream(),"GBK"));
	            while((readLine=responseReader.readLine())!=null){
	                sb.append(readLine).append("\n");
	            }
	        	responseReader.close();*/
	            is = conn.getInputStream();
	            String result = convertStreamToString(is);
	            System.out.println(result);
	            renderText(result);
	        }else{
	        	renderHtml(resultCode+"-"+conn.getResponseMessage());
	        }
			//conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	private String convertStreamToString(InputStream is) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try{
            while((line=reader.readLine())!=null){
                sb.append(line+"/n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return sb.toString();
    }
	
	@ActionKey("/quickbill")
	public void bill(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String offset = getPara("offset");
		Date d = new Date();
		Calendar c = Calendar.getInstance();
		if(StrKit.notBlank(offset)){
			setAttr("offset", offset);
			c.add(Calendar.DATE, Integer.parseInt(offset));
			d = c.getTime();
		}
		//默认当天
		Record today = BillService.checkToday(sdf.format(d));
		
		Double monthCost = BillService.countMonth(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1);
		setAttr("date", sdf.format(d));
		setAttr("bill", today);
		setAttr("monthCost", monthCost);
		render("hello/billShotcut.html");
	}
	
	@ActionKey("/inputBill")
	public void inputBill(){
		Record r = new Record();
		try{
			String name = getPara("name");
			String cost = getPara("cost");
			String type = getPara("type");
			Integer offset = getParaToInt("offset");
			r = BillService.inputBill(name, cost, type, offset);
		}catch(Exception e){
			r.set("result", "error");
			r.set("msg", e.getMessage());
		}
		renderJson(r);
	}
	
	@ActionKey("/updateBill")
	public void updateBill(){
		Record r = new Record();
		try{
			String name = getPara("name");
			String date = getPara("date");
			String cost = getPara("cost");
			r = BillService.updateBill(name, date, cost);
		}catch(Exception e){
			r.set("result", "error");
			r.set("msg", e.getMessage());
		}
		renderJson(r);
	}
	
	@ActionKey("/quickbill/statistic/week")
	public void statisticWeek(){
		List<Record> data = null;
		try{
			data = BillService.billStatistics("1");
			setAttr("data", data);
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("统计数据获取失败！");
		}
		render("hello/statisticWeek.html");
	}
	
	@ActionKey("/quickbill/statistic/month")
	public void statisticMonth(){
		List<Record> data = null;
		try{
			data = BillService.billStatistics("2");
			setAttr("data", data);
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("统计数据获取失败！");
		}
		render("hello/statisticMonth.html");
	}
	
	@ActionKey("/quickbill/statistic/pieMonth")
	public void pieMonth(){
		List<Record> data = null;
		try{
			Calendar c = Calendar.getInstance();
			data = BillService.PieMonth(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1);
			setAttr("pieData", data);
			setAttr("year", c.get(Calendar.YEAR));
			setAttr("month", c.get(Calendar.MONTH)+1);
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("pieMonth统计数据获取失败！");
		}
		render("hello/statisticMonthPie.html");
	}
	
	@ActionKey("/fileCabinet")
	public void fileCabinet(){
		render("hello/fileCabinet.html");
	}
	
	/**
	 * 上传
	 */
	@ActionKey("/fileUpload")
	public void fileUpload(){
		
		Record r = new Record();
		try{
			List<UploadFile> files = getFiles();
			Record uploadResult = FileService.startUpload(files);
			renderJson(uploadResult);
		}catch(Exception e){
			e.printStackTrace();
			r.set("result", "error");
			r.set("msg", "上传失败："+e.getMessage());
			renderJson(r);
		}
	}
	
	/**
	 * fileList
	 */
	@ActionKey("/fileList")
	public void fileList(){
		Map<String, List<Record>> fileList = FileService.getFileList(3);
		renderJson(fileList);
	}
	
	@ActionKey("/fileCabinet/getFile")
	public void getCabinetFile(){
		String fid = getPara("uid");
		File file = null;
		String fn = "";
		if(StrKit.notBlank(fid)){
			file = FileService.getFile(fid);
			if(null!=file){
				//重命名下
				fn = file.getName();
				renderFile(file,fn.substring(0, fn.lastIndexOf("__")));
				return;
			}
		}
		renderText("获取失败！");
	}
	
}
