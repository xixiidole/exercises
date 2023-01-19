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
		//读取文件
		String realPath = getSession().getServletContext().getRealPath("download");
		setAttr("REALPATH", realPath);
		render("todo.html");
    }
	
	@ActionKey("/getWeather")
	public void getWeather( ){
		//��ȡ����
		String url = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl",namespace="http://WebXml.com.cn/",method="getWeatherbyCityName";
		QName returnType = org.apache.axis.encoding.XMLType.SOAP_VECTOR;
		//type="tns:ArrayOfString" ��������
		//QName returnType = org.apache.axis.encoding.XMLType.SOAP_VECTOR;
		List<Map<String,Object>> inParams = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("paramName", "theCityName");
		String city = getPara("city");
		map.put("paramValue", city);
		//�����÷���ֵ����string����һ������ //org.apache.axis.encoding.XMLType.XSD_STRING
		map.put("paramType", org.apache.axis.encoding.XMLType.XSD_STRING);
		//map.put("paramName", "byProvinceName");
		//map.put("paramValue", "��ɳ");
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
	 * ���üӰ࣬workΪ��Ĭ��Ϊ��1���Ӱࣩ����0ȡ���Ӱ��¼
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
				r.set("msg", "δ��ȡ���������ڣ��޷����ã�");
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
			String json = "{\"total\":1,\"data\":[{\"title\":\"<span>����Ժ</span><span>�칫��</span><span>����</span><span>2022</span><span>��</span><span>���ֽڼ��հ���</span><span>��</span><span>֪ͨ</span>\",\"tagno\":\"���췢���硲2021��11��\",\"tag\":\"���췢����\",\"articleid\":\"5644835\",\"writetime\":\"2021��10��25��\",\"pubtime\":\"2021��10��25��\",\"_score\":52.49575,\"url\":\"http://www.gov.cn/zhengce/content/2021-10/25/content_5644835.htm\",\"id\":1,\"theme\":\"�ۺ�����\\\\����\"}],\"zhutitag_total\":1,\"errno\":0,\"gongwentag\":[{\"name\":\"����\",\"count\":0},{\"name\":\"����\",\"count\":0},{\"name\":\"����\",\"count\":0},{\"name\":\"��������\",\"count\":0},{\"name\":\"���췢\",\"count\":0},{\"name\":\"���캯\",\"count\":0},{\"name\":\"���췢����\",\"count\":1},{\"name\":\"����\",\"count\":0}],\"zhutitag\":[{\"name\":\"����Ժ��֯����\",\"child\":[{\"name\":\"����Ժ\",\"count\":0},{\"name\":\"����Ժ�칫��\",\"count\":0},{\"name\":\"����Ժ��ɲ���\",\"count\":0},{\"name\":\"����Ժֱ���������\",\"count\":0},{\"name\":\"����Ժֱ������\",\"count\":0},{\"name\":\"����Ժ���»���\",\"count\":0},{\"name\":\"����Ժֱ����ҵ��λ\",\"count\":0},{\"name\":\"����Ժ��ί����Ĺ��Ҿ�\",\"count\":0},{\"name\":\"����Ժ����Э������\",\"count\":0},{\"name\":\"����\",\"count\":0}],\"count\":0},{\"name\":\"�ۺ�����\",\"child\":[{\"name\":\"���񹫿�\",\"count\":0},{\"name\":\"���񶽲�\",\"count\":0},{\"name\":\"Ӧ������\",\"count\":0},{\"name\":\"��������\",\"count\":0},{\"name\":\"���ع���\",\"count\":0},{\"name\":\"���ܹ���\",\"count\":0},{\"name\":\"�ŷ�\",\"count\":0},{\"name\":\"���¡���ʷ\",\"count\":0},{\"name\":\"����\",\"count\":1}],\"count\":1},{\"name\":\"���񾭼ù��������ʲ����\",\"child\":[{\"name\":\"��۾���\",\"count\":0},{\"name\":\"�������Ƹĸ�\",\"count\":0},{\"name\":\"ͳ��\",\"count\":0},{\"name\":\"���\",\"count\":0},{\"name\":\"�����ʲ����\",\"count\":0},{\"name\":\"�ش�����Ŀ\",\"count\":0},{\"name\":\"����\",\"count\":0}],\"count\":0},{\"name\":\"���������ڡ����\",\"child\":[{\"name\":\"����\",\"count\":0},{\"name\":\"˰��\",\"count\":0},{\"name\":\"����\",\"count\":0},{\"name\":\"���ң�����㣩\",\"count\":0},{\"name\":\"֤ȯ\",\"count\":0},{\"name\":\"����\",\"count\":0},{\"name\":\"���������ϵ����\",\"count\":0},{\"name\":\"���\",\"count\":0},{\"name\":\"����\",\"count\":0}],\"count\":0},{\"name\":\"������Դ����Դ\",\"child\":[{\"name\":\"����\",\"count\":0},{\"name\":\"���\",\"count\":0},{\"name\":\"ˮ��Դ\",\"count\":0},{\"name\":\"����\",\"count\":0},{\"name\":\"ú̿\",\"count\":0},{\"name\":\"ʯ������Ȼ��\",\"count\":0},{\"name\":\"����\",\"count\":0},{\"name\":\"����\",\"count\":0}],\"count\":0},{\"name\":\"ũҵ����ҵ��ˮ��\",\"child\":[{\"name\":\"ũҵ������ҵ����ҵ\",\"count\":0},{\"name\":\"��ҵ\",\"count\":0},{\"name\":\"ˮ��\",\"count\":0},{\"name\":\"����\",\"count\":0}],\"count\":0},{\"name\":\"��ҵ����ͨ\",\"child\":[{\"name\":\"��е�������ع�ҵ\",\"count\":0},{\"name\":\"�Ṥ��֯\",\"count\":0},{\"name\":\"����\",\"count\":0},{\"name\":\"������ҵ\",\"count\":0},{\"name\":\"���졢����\",\"count\":0},{\"name\":\"��Ϣ��ҵ�������ţ�\",\"count\":0},{\"name\":\"��·\",\"count\":0},{\"name\":\"ˮ��\",\"count\":0},{\"name\":\"��·\",\"count\":0},{\"name\":\"��\",\"count\":0},{\"name\":\"����\",\"count\":0},{\"name\":\"����\",\"count\":0}],\"count\":0},{\"name\":\"��ó�����ء�����\",\"child\":[{\"name\":\"���⾭ó����\",\"count\":0},{\"name\":\"����ó�ף���������\",\"count\":0},{\"name\":\"����\",\"count\":0},{\"name\":\"���顢����\",\"count\":0},{\"name\":\"����\",\"count\":0},{\"name\":\"����\",\"count\":0}],\"count\":0},{\"name\":\"�г���ܡ���ȫ�������\",\"child\":[{\"name\":\"����\",\"count\":0},{\"name\":\"�����ල\",\"count\":0},{\"name\":\"��׼\",\"count\":0},{\"name\":\"ʳƷҩƷ���\",\"count\":0},{\"name\":\"��ȫ�������\",\"count\":0},{\"name\":\"����\",\"count\":0}],\"count\":0},{\"name\":\"���罨�衢��������\",\"child\":[{\"name\":\"���й滮\",\"count\":0},{\"name\":\"���罨�裨��ס����\",\"count\":0},{\"name\":\"������⡢����������\",\"count\":0},{\"name\":\"��������Դ�ۺ�����\",\"count\":0},{\"name\":\"����ˮ�ġ���桢����\",\"count\":0},{\"name\":\"����\",\"count\":0}],\"count\":0},{\"name\":\"�Ƽ�������\",\"child\":[{\"name\":\"�Ƽ�\",\"count\":0},{\"name\":\"����\",\"count\":0},{\"name\":\"֪ʶ��Ȩ\",\"count\":0},{\"name\":\"����\",\"count\":0}],\"count\":0},{\"name\":\"�Ļ�����硢���ų���\",\"child\":[{\"name\":\"�Ļ�\",\"count\":0},{\"name\":\"����\",\"count\":0},{\"name\":\"���ų���\",\"count\":0},{\"name\":\"�㲥����Ӱ������\",\"count\":0},{\"name\":\"����\",\"count\":0}],\"count\":0},{\"name\":\"����������\",\"child\":[{\"name\":\"����\",\"count\":0},{\"name\":\"ҽҩ����\",\"count\":0},{\"name\":\"����\",\"count\":0},{\"name\":\"����\",\"count\":0}],\"count\":0},{\"name\":\"�˿���ƻ���������Ů��ͯ����\",\"child\":[{\"name\":\"�˿���ƻ�����\",\"count\":0},{\"name\":\"��Ů��ͯ\",\"count\":0},{\"name\":\"����\",\"count\":0}],\"count\":0},{\"name\":\"�Ͷ������¡����\",\"child\":[{\"name\":\"�Ͷ���ҵ\",\"count\":0},{\"name\":\"��ᱣ��\",\"count\":0},{\"name\":\"���¹���\",\"count\":0},{\"name\":\"��ת����\",\"count\":0},{\"name\":\"���\",\"count\":0},{\"name\":\"������ҵ����֮��\",\"count\":0},{\"name\":\"����\",\"count\":0}],\"count\":0},{\"name\":\"��������ȫ��˾��\",\"child\":[{\"name\":\"����\",\"count\":0},{\"name\":\"���Ұ�ȫ\",\"count\":0},{\"name\":\"˾��\",\"count\":0},{\"name\":\"����\",\"count\":0}],\"count\":0},{\"name\":\"��������ƶ������\",\"child\":[{\"name\":\"���־���\",\"count\":0},{\"name\":\"�Ÿ�����\",\"count\":0},{\"name\":\"��ḣ��\",\"count\":0},{\"name\":\"�������������\",\"count\":0},{\"name\":\"���Ź���\",\"count\":0},{\"name\":\"��ƶ\",\"count\":0},{\"name\":\"����\",\"count\":0}],\"count\":0},{\"name\":\"���塢�ڽ�\",\"child\":[{\"name\":\"��������\",\"count\":0},{\"name\":\"�ڽ�����\",\"count\":0}],\"count\":0},{\"name\":\"��������\",\"child\":[{\"name\":\"�⽻������\",\"count\":0},{\"name\":\"������Լ��������֯\",\"count\":0},{\"name\":\"����\",\"count\":0}],\"count\":0},{\"name\":\"�۰�̨�ȹ���\",\"child\":[{\"name\":\"�۰Ĺ���\",\"count\":0},{\"name\":\"��̨����\",\"count\":0},{\"name\":\"������\",\"count\":0}],\"count\":0},{\"name\":\"����\",\"child\":[{\"name\":\"��������\",\"count\":0},{\"name\":\"������Ա\",\"count\":0}],\"count\":0},{\"name\":\"����\",\"child\":{},\"count\":0}],\"gongwentag_total\":1,\"msg\":\"ok\",\"count\":1}";
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
