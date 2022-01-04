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
		//��¼��ǰʱ��
		int curY = ca.get(Calendar.YEAR);
		int curM = ca.get(Calendar.MONTH)+1;
		int curD = ca.get(Calendar.DATE);
		
		
		ca.set(Calendar.DATE, 1);
		//����Ϊ���ε��·�
		if(StrKit.notBlank(year)){
			ca.set(Calendar.YEAR, Integer.parseInt(year));
		}
		if(StrKit.notBlank(month)){
			ca.set(Calendar.MONTH, Integer.parseInt(month)-1);
		}
		//ת���ַ���
		DecimalFormat dfmt = new DecimalFormat("00");
		//json��ʽ�ַ���
		//{year:2021,month:5,maxdate:31,startWeek:2,current:{year:2021,month:5,date:18},holiday:[{date:1,note:'�Ͷ���',work:0},{date:2,work:0},{date:3,work:0},{date:4,work:0},{date:5,work:0}]}
		Map<String,Object> calen = new HashMap<String, Object>();
		//���������·ݸ��ӵĲ���
		calen.put("year", ca.get(Calendar.YEAR));
		calen.put("month", dfmt.format(ca.get(Calendar.MONTH)+1));
		calen.put("maxdate", ca.getActualMaximum(Calendar.DAY_OF_MONTH)); //���������
		//���ñ���1�ţ���ȡ���ڼ�
		ca.set(Calendar.DATE, 1);
		calen.put("startWeek", dayOfWeek[ca.get(Calendar.DAY_OF_WEEK)]); //������=1������һ=2
		//��ǰʱ��
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
		//�����·ݵĿ�ʼ�ͽ���������ѯ�ڼ�������
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
			calen.put("holiday", holidays); //���������
		}
		
		String json = JsonKit.toJson(calen);
		System.err.println(json);
		return json;
		
	}
	
	public static void hardTime(String date, String work){
		if("0".equals(work)){
			//����
			Db.delete("delete from holidays where date='"+date+"' and note='�Ӱ�'");
		}else if("1".equals(work)){
			Record hd = new Record();
			hd.set("date", date);
			hd.set("work", "1");
			hd.set("note", "�Ӱ�");
			Db.save("holidays", hd);
		}
	}
	
	
	public static String calendar2Data(String year){
		
		//ֱ����ʾȫ��
		//{year:2021,months:[{month:1,days:31,week:5},{month:2,days:28},...],holidays:[{date:2021-01-01,work:'0',note:'Ԫ����'},{date:2021-01-02,work:'1',note:'����'}]}
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
			month.put("week", dayOfWeek[c.get(Calendar.DAY_OF_WEEK)]); //������=1������һ=2
			months.add(month);
		}
		
		data.put("months", months);
		
		//�ڼ���
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
		String sql = "select y.date from holidays y where y.note = '�Ӱ�'  order by y.date ";
		return Db.find(sql);
	}

	public static List<Record> jbTxData(){
		String sql = "select y.date from holidays y where y.note = '�Ӱ�' order by y.date ";
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
