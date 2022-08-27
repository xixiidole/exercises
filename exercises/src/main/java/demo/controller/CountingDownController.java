package demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import demo.Utils.getWeatherUtil;
import demo.service.CalendarService;

public class CountingDownController extends Controller {
	
	@ActionKey("/CD")
	public void cdp() {
		System.err.println(PathKit.getWebRootPath());
       render("cdp3.html");
    }
	
	public void index() {
       renderText("Not In Here!");
    }

	@ActionKey("/todo")
	public void todo() {
		//璇诲彇鏂囦欢
		String realPath = getSession().getServletContext().getRealPath("download");
		setAttr("REALPATH", realPath);
		render("todo.html");
    }
	
	@ActionKey("/getWeather")
	public void getWeather( ){
		//获取天气
		String url = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl",namespace="http://WebXml.com.cn/",method="getWeatherbyCityName";
		QName returnType = org.apache.axis.encoding.XMLType.SOAP_VECTOR;
		//type="tns:ArrayOfString" 返回数组
		//QName returnType = org.apache.axis.encoding.XMLType.SOAP_VECTOR;
		List<Map<String,Object>> inParams = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("paramName", "theCityName");
		String city = getPara("city");
		map.put("paramValue", city);
		//测试用返回值不是string，是一个数组 //org.apache.axis.encoding.XMLType.XSD_STRING
		map.put("paramType", org.apache.axis.encoding.XMLType.XSD_STRING);
		//map.put("paramName", "byProvinceName");
		//map.put("paramValue", "长沙");
		//map.put("paramType", org.apache.axis.encoding.XMLType.XSD_STRING);
		inParams.add(map);
		Map<String, Object> RV = getWeatherUtil.SimpleCall(url, namespace, method, returnType, inParams);
		if(null!=RV && "success".equals(RV.get("result"))){
			Vector<String> v= (Vector)RV.get("data");
			Iterator<String> iterator = v.iterator();
			while(iterator.hasNext()){
				System.err.println(iterator.next());
			}
		}
		renderNull();
	}

	@ActionKey("/CDX")
	public void cdx() {
       render("cdx.html");
    }
	
	@ActionKey("/undoneThings")
	public void undoneThings() {
		String JSID = getPara("JSID");
		setAttr("JSID", JSID);
		render("undoneThings.html");
	}
	
	@ActionKey("/calender")
	public void calender() {
		render("calender.html");
	}
	
	@ActionKey("/calender2")
	public void calender2() {
		render("calender2.html");
	}
	
	@ActionKey("/calenderData")
	public void calendarData(){
		String year = getPara("year");
		String month = getPara("month");
		String json = CalendarService.calendarData(year,month);
		renderJson(json);
	}
	
	@ActionKey("/calender2Data")
	public void calendar2Data(){
		String year = getPara("year");
		String json = CalendarService.calendar2Data(year);
		renderJson(json);
	}
	
	/**
	 * 设置加班，work为空默认为：1（加班）；传0取消加班记录
	 */
	@ActionKey("/hardTime")
	@Before(Tx.class)
	public void hardTime(){
		Record r = new Record();
		try{
			String date = getPara("date");
			String work = getPara("work","1");
			if(!StrKit.isBlank(date)){
				CalendarService.hardTime(date, work);
				r.set("result", "success");
			}else{
				r.set("result", "error");
				r.set("msg", "未获取到设置日期！无法设置！");
			}
		}catch(Exception e){
			r.set("result", "error");
			r.set("msg", e.getMessage());
		}
		
		renderJson(r);
	}
	
	@ActionKey("/txData")
	public void jbtxData() {
		List<Record> txData = CalendarService.txData();
		renderJson(txData);
    }
	
	@ActionKey("/jbData")
	public void jbData() {
		List<Record> jbData = CalendarService.jbTxData();
		renderJson(jbData);
    }
	
	@ActionKey("/todoRules")
	public void todoRules() {
		//
		render("todoRules.html");
    }
	
	
}
