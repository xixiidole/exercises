package demo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;

public class HelloController extends Controller {
	
	public void index() {
	       renderText("Hello JFinal World.");
	}
	
	public void test() {
		getPara("layoutKey");
		renderText("Hello JFinal World2.");
	}
	
	public void doodle(){
		render("doodle.html");
	}
	
	public void iframe(){
		render("iframe.html");
	}
	
	public void bigPrice(){
		render("spin.html");
	}
	
	@ActionKey("/click")
	public void click(){
		render("click.html");
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
	
	
}
