package demo.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.support.json.JSONParser;
import com.alibaba.druid.support.json.JSONUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfinal.json.Json;
import com.jfinal.kit.HttpKit;
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
	
	public static String searchVacation(){
		String a = "jQuery112407066158341257142_";
		long b = new Date().getTime();
		String c = "";
		try {
			c = URLEncoder.encode("国务院办公厅关于"+Calendar.getInstance().get(Calendar.YEAR)+"年部分节假日安排的通知","UTF8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String su = "http://xxgk.www.gov.cn/search-zhengce/?callback="+a+b+"&mode=smart"+"&sort=relevant"+"&page_index=1"+"&page_size=10"+"&title="+c+"&_="+(b+1);
		String data = HttpKit.get(su);
		System.err.println("data"+data);
		
		if(StrKit.notBlank(data)){
			//返回的数据格式是 jQueryXX_NN(json) 字符串
			data = data.replace(a+b+"(", "");
			data = data.substring(0,data.length()-1);	//去除最后的）
		}
		//System.err.println("F data"+data); 
		
		//开始解析获取通知url
		String url = "";
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String,Object> dataJson = mapper.readValue(data, Map.class);
			int z = (Integer)dataJson.get("total");
			if(z>0){
				List<Map<String,String>> datas = (List<Map<String,String>>)dataJson.get("data");
				url = (String)datas.get(0).get("url");
			}
		} catch (Exception e) {
			System.err.println("解析放假通知出错！");
			e.printStackTrace();
		}
		return url;
	}
	
	public static String getVacation(String url){
		//String article = HttpKit.get(url);
		//System.err.println(article);
		
		//懒得调用网站获取了，直接复制格式解析
		
		List<String> vct = new ArrayList<String>();
		vct.add("一、元旦：2022年12月31日至2023年1月2日放假调休，共3天。");
		vct.add("二、春节：1月21日至27日放假调休，共7天。1月28日（星期六）、1月29日（星期日）上班。");
		vct.add("三、清明节：4月5日放假，共1天。");
		vct.add("四、劳动节：4月29日至5月3日放假调休，共5天。4月23日（星期日）、5月6日（星期六）上班。");
		vct.add("五、端午节：6月22日至24日放假调休，共3天。6月25日（星期日）上班。");
		vct.add("六、中秋节、国庆节：9月29日至10月6日放假调休，共8天。10月7日（星期六）、10月8日（星期日）上班。");
		
		
		List<Record> vcts = parseVacation(vct);
		if(null!=vcts && vcts.size()>0){
			Db.batchSave("holidays", vcts, vcts.size());
		}
		
		System.err.println("getVacation - 处理完成");
		return "success";
	}
	
	/**
	 * 解析假期
	 * @param vcts
	 * @return
	 */
	public static List<Record> parseVacation(List<String> vcts){
		boolean isnormal = true; //是否解析正常
		List<Record> vacations = new ArrayList<Record>(); //放假数据
		try{
			Integer mY = null;
			Integer mM = null;
			for(String a : vcts){
				String[] infos = null;
				boolean tx = false;
				if(a.indexOf("放假调休")>0){
					infos = a.split("：|放假调休，|。");
					tx = true;
				}else{
					infos = a.split("：|放假，|。");
					tx = false;
				}
				//
				//
				String vctName = infos[0].substring( infos[0].indexOf("、")+1); //节假日名称
				
				Date d1 = null;
				Date d2 = null;
				
				boolean duraVac = false; //时间段放假
				
				if(infos[1].indexOf("至")>0){
					duraVac = true;
				}
				
				if(duraVac){
					String[] vdate = infos[1].split("至");
					
					boolean getYear_D1 = false; //获取年
					boolean getMonth_D1 = false;
					
					if(vdate[0].indexOf("年")>0){
						getYear_D1 = true;
					}
					if(vdate[0].indexOf("月")>0){
						getMonth_D1 = true;
					}
					
					boolean getYear_D2 = false; //获取年
					boolean getMonth_D2 = false;
					
					if(vdate[1].indexOf("年")>0){
						getYear_D2 = true;
					}
					if(vdate[1].indexOf("月")>0){
						getMonth_D2 = true;
					}
					
					if(getYear_D1){
						d1 = new Date(vdate[0].replace("年", "/").replace("月", "/").replace("日", ""));
						
						mY = d1.getYear()+1900; //后续没有年的日期默认年
						mM = d1.getMonth()+1; //后续没有年的日期默认年
					}else{
						if(!getMonth_D1){
							vdate[0] = mM+"月"+vdate[0];
						}
						if(!getYear_D1){
							vdate[0] = mY+"年"+vdate[0];
						}
						
						d1 = new Date(vdate[0].replace("年", "/").replace("月", "/").replace("日", ""));
						
						if(getMonth_D1){
							mM = d1.getMonth()+1; //记录当前的月
						}
						if(getYear_D1){
							mY = d1.getYear()+1900; //记录当前的年
						}
						
					}
					
					if(getYear_D2){
						d2 = new Date(vdate[1].replace("年", "/").replace("月", "/").replace("日", ""));
						
						mY = d2.getYear()+1900; //后续没有年的日期默认年
						mM = d2.getMonth()+1; //后续没有年的日期默认年
					}else{
						if(!getMonth_D2){
							vdate[1] = mM+"月"+vdate[1];
						}
						if(!getYear_D2){
							vdate[1] = mY+"年"+vdate[1];
						}
						
						d2 = new Date(vdate[1].replace("年", "/").replace("月", "/").replace("日", ""));
						
						if(getMonth_D2){
							mM = d2.getMonth()+1; //记录当前的月
						}
						if(getYear_D2){
							mY = d2.getYear()+1900; //记录当前的年
						}
						
					}
					
				}else{
					//只放一天
					String t = infos[1];
					
					boolean getYear_D1 = false; //获取年
					boolean getMonth_D1 = false;
					
					if(t.indexOf("年")>0){
						getYear_D1 = true;
					}
					if(t.indexOf("月")>0){
						getMonth_D1 = true;
					}
					
					if(!getMonth_D1){
						t = mM+"月"+t;
					}
					if(!getYear_D1){
						t = mY+"年"+t;
					}
					
					d1 = new Date(t.replace("年", "/").replace("月", "/").replace("日", ""));
					d2 = new Date(t.replace("年", "/").replace("月", "/").replace("日", ""));
					
					if(getMonth_D1){
						mM = d2.getMonth()+1;
					}
					if(getYear_D1){
						mY = d2.getYear()+1900;
					}
					
				}
					
					
//					Calendar cd1 = Calendar.getInstance();
//					cd1.set(Calendar.YEAR, Integer.parseInt(vdate[0].substring(0,vdate[0].indexOf("年"))));
//					cd1.set(Calendar.MONTH, Integer.parseInt(vdate[0].substring(vdate[0].indexOf("年")+1,vdate[0].indexOf("月"))));
//					cd1.set(Calendar.DATE, Integer.parseInt(vdate[0].substring(vdate[0].indexOf("月")+1,vdate[0].indexOf("日"))));
//					
//					Calendar cd2 = Calendar.getInstance();
//					cd2.set(Calendar.YEAR, Integer.parseInt(vdate[1].substring(0,vdate[1].indexOf("年"))));
//					cd2.set(Calendar.MONTH, Integer.parseInt(vdate[1].substring(vdate[1].indexOf("年")+1,vdate[1].indexOf("月"))));
//					cd2.set(Calendar.DATE, Integer.parseInt(vdate[1].substring(vdate[1].indexOf("月")+1,vdate[1].indexOf("日"))));
//					
					
				
				
				while(d1.before(d2) || d1.getTime() == d2.getTime()){
					Record r = new Record();
					r.set("date", new Date(d1.getTime()));
					r.set("work", 0);
					r.set("note", vctName+"("+infos[2]+")");
					vacations.add(r);
					
					d1.setDate(d1.getDate()+1); 
					
				}
				
				//补班
				if(tx && infos.length>3){
					String bbxx = infos[3];
					//1月28日（星期六）、1月29日（星期日）上班
					String[] bbs = bbxx.split("、|上班");
					for(String c : bbs){
						
						boolean getYear_D1 = false; //打个标识，不用获取
						boolean getMonth_D1 = false;
						
						c = c.substring(0,c.indexOf("（"));
						
						if(c.indexOf("年")>0){
							getYear_D1 = true;
						}
						if(c.indexOf("月")>0){
							getMonth_D1 = true;
						}
						
						if(!getMonth_D1){
							c = mM+"月"+c;
						}
						if(!getYear_D1){
							c = mY+"年"+c;
						}
						
						Date sb = new Date(c.replace("年", "/").replace("月", "/").replace("日", ""));
						
						Record r2 = new Record();
						r2.set("date", new Date(sb.getTime()));
						r2.set("work", 1);
						r2.set("note", "补班");
						vacations.add(r2);
						
					}
				}
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return vacations;
	}
	
	/**
	 * 获取已经有假期数据的年份
	 * @return
	 */
	public static List<Record> getAllYears(){
		String sql = "select distinct date_format(date,'%Y') year  from holidays a order by a.date ";
		List<Record> yearsList = Db.find(sql);
		return yearsList;
	}
	
	public static void main(String[] args) {
		getVacation("");
	}
	
}
