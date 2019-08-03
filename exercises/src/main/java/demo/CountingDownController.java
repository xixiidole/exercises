package demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;

import demo.Utils.getWeatherUtil;

public class CountingDownController extends Controller {
	@ActionKey("/CD")
	public void cdp() {
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
}
