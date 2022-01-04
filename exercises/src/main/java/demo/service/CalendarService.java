package demo.service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.support.json.JSONUtils;
import com.jfinal.json.Json;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class CalendarService {
	
	final static String[] dayOfWeek = {"0","7","1","2","3","4","5","6"};
	
	public static String calendarData(String year, String month){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar ca = Calendar.getInstance();
		//记录当前时间
		int curY = ca.get(Calendar.YEAR);
		int curM = ca.get(Calendar.MONTH)+1;
		int curD = ca.get(Calendar.DATE);
		
		
		ca.set(Calendar.DATE, 1);
		//设置为传参的月份
		if(StrKit.notBlank(year)){
			ca.set(Calendar.YEAR, Integer.parseInt(year));
		}
		if(StrKit.notBlank(month)){
			ca.set(Calendar.MONTH, Integer.parseInt(month)-1);
		}
		//转成字符串
		DecimalFormat dfmt = new DecimalFormat("00");
		//json格式字符串
		//{year:2021,month:5,maxdate:31,startWeek:2,current:{year:2021,month:5,date:18},holiday:[{date:1,note:'劳动节',work:0},{date:2,work:0},{date:3,work:0},{date:4,work:0},{date:5,work:0}]}
		Map<String,Object> calen = new HashMap<String, Object>();
		//用于生成月份格子的参数
		calen.put("year", ca.get(Calendar.YEAR));
		calen.put("month", dfmt.format(ca.get(Calendar.MONTH)+1));
		calen.put("maxdate", ca.getActualMaximum(Calendar.DAY_OF_MONTH)); //当月最大天
		//设置本月1号，获取星期几
		ca.set(Calendar.DATE, 1);
		calen.put("startWeek", dayOfWeek[ca.get(Calendar.DAY_OF_WEEK)]); //星期天=1，星期一=2
		//当前时间
		Map<String,Object> current = new HashMap<String, Object>();
		current.put("year", curY);
		current.put("month", dfmt.format(curM));
		current.put("date", dfmt.format(curD));
		calen.put("current", current);
		
		List<Map<String,Object>> holidays = new ArrayList<Map<String,Object>>();
		
//		Calendar ca2 = Calendar.getInstance();
//		if(StrKit.notBlank(year)){
//			ca2.set(Calendar.YEAR, Integer.parseInt(year));
//		}
//		if(StrKit.notBlank(month)){
//			ca2.set(Calendar.MONTH, Integer.parseInt(month)-1);
//		}
//		ca2.set(Calendar.DATE, 1);
		//传入月份的开始和结束用来查询节假日数据
		String start = sdf.format(ca.getTime());
		ca.set(Calendar.DATE, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		String end = sdf.format(ca.getTime());
		List<Record> list = Db.find("select date,work,note from holidays where date between '"+start+"' and '"+end+"'");
		if(null!=list && list.size()>0){
			for(int i=0;i<list.size();i++){
				Map<String,Object> holiday = new HashMap<String, Object>();
				holiday.put("date", sdf.format(list.get(i).getDate("date")));
				holiday.put("work", list.get(i).getStr("work"));
				holiday.put("note", (StrKit.notBlank(list.get(i).getStr("note"))?list.get(i).getStr("note"):""));
				holidays.add(holiday);
			}
			calen.put("holiday", holidays); //当月最大天
		}
		
		String json = JsonKit.toJson(calen);
		System.err.println(json);
		return json;
		
	}
	
	public static void hardTime(String date, String work){
		if("0".equals(work)){
			//清理
			Db.delete("delete from holidays where date='"+date+"' and note='加班'");
		}else if("1".equals(work)){
			Record hd = new Record();
			hd.set("date", date);
			hd.set("work", "1");
			hd.set("note", "加班");
			Db.save("holidays", hd);
		}
	}
	
	
	public static String calendar2Data(String year){
		
		//直接显示全年
		//{year:2021,months:[{month:1,days:31,week:5},{month:2,days:28},...],holidays:[{date:2021-01-01,work:'0',note:'元旦节'},{date:2021-01-02,work:'1',note:'补班'}]}
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("year", year);
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, Integer.parseInt(year));
		
		List<Map<String,String>> months = new ArrayList<Map<String,String>>();
		
		for(int x=0;x<12;x++) {
			Map<String,String> month = new HashMap<String,String>();
			c.set(Calendar.MONTH, x);
			c.set(Calendar.DATE, 1);
			month.put("month", (x+1)+"");
			month.put("days", c.getActualMaximum(Calendar.DAY_OF_MONTH)+"");
			month.put("week", dayOfWeek[c.get(Calendar.DAY_OF_WEEK)]); //星期天=1，星期一=2
			months.add(month);
		}
		
		data.put("months", months);
		
		//节假日
		String start = year+"-01-01";
		String end = year+"-12-31";
		List<Record> list = Db.find("select date,work,note from holidays where date between '"+start+"' and '"+end+"'");
		
		List<Map<String,Object>> holidays = new ArrayList<Map<String,Object>>();
		
		if(null!=list && list.size()>0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			for(int i=0;i<list.size();i++){
				Map<String,Object> holiday = new HashMap<String, Object>();
				holiday.put("date", sdf.format(list.get(i).getDate("date")));
				holiday.put("work", list.get(i).getStr("work"));
				holiday.put("note", (StrKit.notBlank(list.get(i).getStr("note"))?list.get(i).getStr("note"):""));
				holidays.add(holiday);
			}
			data.put("holidays", holidays); 
		}
		
		String json = JsonKit.toJson(data);
		System.err.println(json);
		return json;
	}
	
	public static List<Record> txData(){
		String sql = "select o.* from takeoff o order by o.wodate ";
		return Db.find(sql);
	}
	
	public static List<Record> jbData(){
		String sql = "select y.date from holidays y where y.note = '加班'  order by y.date ";
		return Db.find(sql);
	}

	public static List<Record> jbTxData(){
		String sql = "select y.date from holidays y where y.note = '加班' order by y.date ";
		List<Record> jbList = Db.find(sql);
		String sql2 = "select o.wodate,o.woduration,o.takeoffdate,o.takeoffduration,o.remark from takeoff o ";
		List<Record> txList = Db.find(sql2);
		if(null!=jbList && jbList.size()>0){
			for(int i=0;i<jbList.size();i++){
				String f = jbList.get(i).getStr("date");
				List<Record> children = new ArrayList<Record>();
				double duration = 0;
				for(int x=0;x<txList.size();x++){
					String k = txList.get(x).getStr("wodate");

					if(k.equals(f)){
						duration += txList.get(x).getDouble("takeoffduration");
						children.add(txList.get(x));
					}
				}
				if(children.size()>0){
					jbList.get(i).set("children",children);
				}
				if(duration>=1){
					jbList.get(i).set("state","unavailable");
				}else{
					jbList.get(i).set("state","available");
				}
			}
		}
		return jbList;
	}
	
}
