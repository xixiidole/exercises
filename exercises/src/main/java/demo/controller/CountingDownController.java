package demo.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;

import com.alibaba.druid.support.json.JSONUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
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
		List<Record> years = CalendarService.getAllYears();
		setAttr("years",years);
		Calendar c = Calendar.getInstance();
		setAttr("cy",c.get(Calendar.YEAR));
		setAttr("cm",c.get(Calendar.MONTH)+1);
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
	
	@ActionKey("/genVacation")
	public void genVacation(){
		/*try {
			//String json = CalendarService.searchVacation();
			String json = "{\"total\":1,\"data\":[{\"title\":\"<span>国务院</span><span>办公厅</span><span>关于</span><span>2022</span><span>年</span><span>部分节假日安排</span><span>的</span><span>通知</span>\",\"tagno\":\"国办发明电〔2021〕11号\",\"tag\":\"国办发明电\",\"articleid\":\"5644835\",\"writetime\":\"2021年10月25日\",\"pubtime\":\"2021年10月25日\",\"_score\":52.49575,\"url\":\"http://www.gov.cn/zhengce/content/2021-10/25/content_5644835.htm\",\"id\":1,\"theme\":\"综合政务\\\\其他\"}],\"zhutitag_total\":1,\"errno\":0,\"gongwentag\":[{\"name\":\"国令\",\"count\":0},{\"name\":\"国发\",\"count\":0},{\"name\":\"国函\",\"count\":0},{\"name\":\"国发明电\",\"count\":0},{\"name\":\"国办发\",\"count\":0},{\"name\":\"国办函\",\"count\":0},{\"name\":\"国办发明电\",\"count\":1},{\"name\":\"其他\",\"count\":0}],\"zhutitag\":[{\"name\":\"国务院组织机构\",\"child\":[{\"name\":\"国务院\",\"count\":0},{\"name\":\"国务院办公厅\",\"count\":0},{\"name\":\"国务院组成部门\",\"count\":0},{\"name\":\"国务院直属特设机构\",\"count\":0},{\"name\":\"国务院直属机构\",\"count\":0},{\"name\":\"国务院办事机构\",\"count\":0},{\"name\":\"国务院直属事业单位\",\"count\":0},{\"name\":\"国务院部委管理的国家局\",\"count\":0},{\"name\":\"国务院议事协调机构\",\"count\":0},{\"name\":\"其他\",\"count\":0}],\"count\":0},{\"name\":\"综合政务\",\"child\":[{\"name\":\"政务公开\",\"count\":0},{\"name\":\"政务督查\",\"count\":0},{\"name\":\"应急管理\",\"count\":0},{\"name\":\"电子政务\",\"count\":0},{\"name\":\"文秘工作\",\"count\":0},{\"name\":\"保密工作\",\"count\":0},{\"name\":\"信访\",\"count\":0},{\"name\":\"参事、文史\",\"count\":0},{\"name\":\"其他\",\"count\":1}],\"count\":1},{\"name\":\"国民经济管理、国有资产监管\",\"child\":[{\"name\":\"宏观经济\",\"count\":0},{\"name\":\"经济体制改革\",\"count\":0},{\"name\":\"统计\",\"count\":0},{\"name\":\"物价\",\"count\":0},{\"name\":\"国有资产监管\",\"count\":0},{\"name\":\"重大建设项目\",\"count\":0},{\"name\":\"其他\",\"count\":0}],\"count\":0},{\"name\":\"财政、金融、审计\",\"child\":[{\"name\":\"财政\",\"count\":0},{\"name\":\"税务\",\"count\":0},{\"name\":\"银行\",\"count\":0},{\"name\":\"货币（含外汇）\",\"count\":0},{\"name\":\"证券\",\"count\":0},{\"name\":\"保险\",\"count\":0},{\"name\":\"社会信用体系建设\",\"count\":0},{\"name\":\"审计\",\"count\":0},{\"name\":\"其他\",\"count\":0}],\"count\":0},{\"name\":\"国土资源、能源\",\"child\":[{\"name\":\"土地\",\"count\":0},{\"name\":\"矿产\",\"count\":0},{\"name\":\"水资源\",\"count\":0},{\"name\":\"海洋\",\"count\":0},{\"name\":\"煤炭\",\"count\":0},{\"name\":\"石油与天然气\",\"count\":0},{\"name\":\"电力\",\"count\":0},{\"name\":\"其他\",\"count\":0}],\"count\":0},{\"name\":\"农业、林业、水利\",\"child\":[{\"name\":\"农业、畜牧业、渔业\",\"count\":0},{\"name\":\"林业\",\"count\":0},{\"name\":\"水利\",\"count\":0},{\"name\":\"其他\",\"count\":0}],\"count\":0},{\"name\":\"工业、交通\",\"child\":[{\"name\":\"机械制造与重工业\",\"count\":0},{\"name\":\"轻工纺织\",\"count\":0},{\"name\":\"化工\",\"count\":0},{\"name\":\"国防工业\",\"count\":0},{\"name\":\"航天、航空\",\"count\":0},{\"name\":\"信息产业（含电信）\",\"count\":0},{\"name\":\"公路\",\"count\":0},{\"name\":\"水运\",\"count\":0},{\"name\":\"铁路\",\"count\":0},{\"name\":\"民航\",\"count\":0},{\"name\":\"邮政\",\"count\":0},{\"name\":\"其他\",\"count\":0}],\"count\":0},{\"name\":\"商贸、海关、旅游\",\"child\":[{\"name\":\"对外经贸合作\",\"count\":0},{\"name\":\"国内贸易（含供销）\",\"count\":0},{\"name\":\"海关\",\"count\":0},{\"name\":\"检验、检疫\",\"count\":0},{\"name\":\"旅游\",\"count\":0},{\"name\":\"其他\",\"count\":0}],\"count\":0},{\"name\":\"市场监管、安全生产监管\",\"child\":[{\"name\":\"工商\",\"count\":0},{\"name\":\"质量监督\",\"count\":0},{\"name\":\"标准\",\"count\":0},{\"name\":\"食品药品监管\",\"count\":0},{\"name\":\"安全生产监管\",\"count\":0},{\"name\":\"其他\",\"count\":0}],\"count\":0},{\"name\":\"城乡建设、环境保护\",\"child\":[{\"name\":\"城市规划\",\"count\":0},{\"name\":\"城乡建设（含住房）\",\"count\":0},{\"name\":\"环境监测、保护与治理\",\"count\":0},{\"name\":\"节能与资源综合利用\",\"count\":0},{\"name\":\"气象、水文、测绘、地震\",\"count\":0},{\"name\":\"其他\",\"count\":0}],\"count\":0},{\"name\":\"科技、教育\",\"child\":[{\"name\":\"科技\",\"count\":0},{\"name\":\"教育\",\"count\":0},{\"name\":\"知识产权\",\"count\":0},{\"name\":\"其他\",\"count\":0}],\"count\":0},{\"name\":\"文化、广电、新闻出版\",\"child\":[{\"name\":\"文化\",\"count\":0},{\"name\":\"文物\",\"count\":0},{\"name\":\"新闻出版\",\"count\":0},{\"name\":\"广播、电影、电视\",\"count\":0},{\"name\":\"其他\",\"count\":0}],\"count\":0},{\"name\":\"卫生、体育\",\"child\":[{\"name\":\"卫生\",\"count\":0},{\"name\":\"医药管理\",\"count\":0},{\"name\":\"体育\",\"count\":0},{\"name\":\"其他\",\"count\":0}],\"count\":0},{\"name\":\"人口与计划生育、妇女儿童工作\",\"child\":[{\"name\":\"人口与计划生育\",\"count\":0},{\"name\":\"妇女儿童\",\"count\":0},{\"name\":\"其他\",\"count\":0}],\"count\":0},{\"name\":\"劳动、人事、监察\",\"child\":[{\"name\":\"劳动就业\",\"count\":0},{\"name\":\"社会保障\",\"count\":0},{\"name\":\"人事工作\",\"count\":0},{\"name\":\"军转安置\",\"count\":0},{\"name\":\"监察\",\"count\":0},{\"name\":\"纠正行业不正之风\",\"count\":0},{\"name\":\"其他\",\"count\":0}],\"count\":0},{\"name\":\"公安、安全、司法\",\"child\":[{\"name\":\"公安\",\"count\":0},{\"name\":\"国家安全\",\"count\":0},{\"name\":\"司法\",\"count\":0},{\"name\":\"其他\",\"count\":0}],\"count\":0},{\"name\":\"民政、扶贫、救灾\",\"child\":[{\"name\":\"减灾救灾\",\"count\":0},{\"name\":\"优抚安置\",\"count\":0},{\"name\":\"社会福利\",\"count\":0},{\"name\":\"行政区划与地名\",\"count\":0},{\"name\":\"社团管理\",\"count\":0},{\"name\":\"扶贫\",\"count\":0},{\"name\":\"其他\",\"count\":0}],\"count\":0},{\"name\":\"民族、宗教\",\"child\":[{\"name\":\"民族事务\",\"count\":0},{\"name\":\"宗教事务\",\"count\":0}],\"count\":0},{\"name\":\"对外事务\",\"child\":[{\"name\":\"外交、外事\",\"count\":0},{\"name\":\"国际条约、国际组织\",\"count\":0},{\"name\":\"其他\",\"count\":0}],\"count\":0},{\"name\":\"港澳台侨工作\",\"child\":[{\"name\":\"港澳工作\",\"count\":0},{\"name\":\"对台工作\",\"count\":0},{\"name\":\"侨务工作\",\"count\":0}],\"count\":0},{\"name\":\"国防\",\"child\":[{\"name\":\"国防建设\",\"count\":0},{\"name\":\"国防动员\",\"count\":0}],\"count\":0},{\"name\":\"其他\",\"child\":{},\"count\":0}],\"gongwentag_total\":1,\"msg\":\"ok\",\"count\":1}";
			ObjectMapper mapper = new ObjectMapper();
			Map<String,Object> data = mapper.readValue(json, Map.class);
			int z = (Integer)data.get("total");
			System.err.println("total="+z);
			if(z>0){
				List<Map<String,String>> datas = (List<Map<String,String>>)data.get("data");
				String url = (String)datas.get(0).get("url");
				System.err.println("url="+url);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		//String url = "http://www.gov.cn/zhengce/content/2021-10/25/content_5644835.htm";
		String url = "http://www.gov.cn/zhengce/content/2022-12/08/content_5730844.htm";
		CalendarService.getVacation(url);
		
//		String url = CalendarService.searchVacation();
//		if(StrKit.notBlank(url)){
//			CalendarService.getVacation(url);
//		}
		
		renderNull();
	}
}
