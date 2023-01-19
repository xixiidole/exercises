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
	
	public static String searchVacation(){
		String a = "jQuery112407066158341257142_";
		long b = new Date().getTime();
		String c = "";
		try {
			c = URLEncoder.encode("����Ժ�칫������"+Calendar.getInstance().get(Calendar.YEAR)+"�겿�ֽڼ��հ��ŵ�֪ͨ","UTF8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String su = "http://xxgk.www.gov.cn/search-zhengce/?callback="+a+b+"&mode=smart"+"&sort=relevant"+"&page_index=1"+"&page_size=10"+"&title="+c+"&_="+(b+1);
		String data = HttpKit.get(su);
		System.err.println("data"+data);
		
		if(StrKit.notBlank(data)){
			//���ص����ݸ�ʽ�� jQueryXX_NN(json) �ַ���
			data = data.replace(a+b+"(", "");
			data = data.substring(0,data.length()-1);	//ȥ�����ģ�
		}
		//System.err.println("F data"+data); 
		
		//��ʼ������ȡ֪ͨurl
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
			System.err.println("�����ż�֪ͨ����");
			e.printStackTrace();
		}
		return url;
	}
	
	public static String getVacation(String url){
		//String article = HttpKit.get(url);
		//System.err.println(article);
		
		//���õ�����վ��ȡ�ˣ�ֱ�Ӹ��Ƹ�ʽ����
		
		List<String> vct = new ArrayList<String>();
		vct.add("һ��Ԫ����2022��12��31����2023��1��2�շżٵ��ݣ���3�졣");
		vct.add("�������ڣ�1��21����27�շżٵ��ݣ���7�졣1��28�գ�����������1��29�գ������գ��ϰࡣ");
		vct.add("���������ڣ�4��5�շż٣���1�졣");
		vct.add("�ġ��Ͷ��ڣ�4��29����5��3�շżٵ��ݣ���5�졣4��23�գ������գ���5��6�գ����������ϰࡣ");
		vct.add("�塢����ڣ�6��22����24�շżٵ��ݣ���3�졣6��25�գ������գ��ϰࡣ");
		vct.add("��������ڡ�����ڣ�9��29����10��6�շżٵ��ݣ���8�졣10��7�գ�����������10��8�գ������գ��ϰࡣ");
		
		
		List<Record> vcts = parseVacation(vct);
		if(null!=vcts && vcts.size()>0){
			Db.batchSave("holidays", vcts, vcts.size());
		}
		
		System.err.println("getVacation - �������");
		return "success";
	}
	
	/**
	 * ��������
	 * @param vcts
	 * @return
	 */
	public static List<Record> parseVacation(List<String> vcts){
		boolean isnormal = true; //�Ƿ��������
		List<Record> vacations = new ArrayList<Record>(); //�ż�����
		try{
			Integer mY = null;
			Integer mM = null;
			for(String a : vcts){
				String[] infos = null;
				boolean tx = false;
				if(a.indexOf("�żٵ���")>0){
					infos = a.split("��|�żٵ��ݣ�|��");
					tx = true;
				}else{
					infos = a.split("��|�ż٣�|��");
					tx = false;
				}
				//
				//
				String vctName = infos[0].substring( infos[0].indexOf("��")+1); //�ڼ�������
				
				Date d1 = null;
				Date d2 = null;
				
				boolean duraVac = false; //ʱ��ηż�
				
				if(infos[1].indexOf("��")>0){
					duraVac = true;
				}
				
				if(duraVac){
					String[] vdate = infos[1].split("��");
					
					boolean getYear_D1 = false; //��ȡ��
					boolean getMonth_D1 = false;
					
					if(vdate[0].indexOf("��")>0){
						getYear_D1 = true;
					}
					if(vdate[0].indexOf("��")>0){
						getMonth_D1 = true;
					}
					
					boolean getYear_D2 = false; //��ȡ��
					boolean getMonth_D2 = false;
					
					if(vdate[1].indexOf("��")>0){
						getYear_D2 = true;
					}
					if(vdate[1].indexOf("��")>0){
						getMonth_D2 = true;
					}
					
					if(getYear_D1){
						d1 = new Date(vdate[0].replace("��", "/").replace("��", "/").replace("��", ""));
						
						mY = d1.getYear()+1900; //����û���������Ĭ����
						mM = d1.getMonth()+1; //����û���������Ĭ����
					}else{
						if(!getMonth_D1){
							vdate[0] = mM+"��"+vdate[0];
						}
						if(!getYear_D1){
							vdate[0] = mY+"��"+vdate[0];
						}
						
						d1 = new Date(vdate[0].replace("��", "/").replace("��", "/").replace("��", ""));
						
						if(getMonth_D1){
							mM = d1.getMonth()+1; //��¼��ǰ����
						}
						if(getYear_D1){
							mY = d1.getYear()+1900; //��¼��ǰ����
						}
						
					}
					
					if(getYear_D2){
						d2 = new Date(vdate[1].replace("��", "/").replace("��", "/").replace("��", ""));
						
						mY = d2.getYear()+1900; //����û���������Ĭ����
						mM = d2.getMonth()+1; //����û���������Ĭ����
					}else{
						if(!getMonth_D2){
							vdate[1] = mM+"��"+vdate[1];
						}
						if(!getYear_D2){
							vdate[1] = mY+"��"+vdate[1];
						}
						
						d2 = new Date(vdate[1].replace("��", "/").replace("��", "/").replace("��", ""));
						
						if(getMonth_D2){
							mM = d2.getMonth()+1; //��¼��ǰ����
						}
						if(getYear_D2){
							mY = d2.getYear()+1900; //��¼��ǰ����
						}
						
					}
					
				}else{
					//ֻ��һ��
					String t = infos[1];
					
					boolean getYear_D1 = false; //��ȡ��
					boolean getMonth_D1 = false;
					
					if(t.indexOf("��")>0){
						getYear_D1 = true;
					}
					if(t.indexOf("��")>0){
						getMonth_D1 = true;
					}
					
					if(!getMonth_D1){
						t = mM+"��"+t;
					}
					if(!getYear_D1){
						t = mY+"��"+t;
					}
					
					d1 = new Date(t.replace("��", "/").replace("��", "/").replace("��", ""));
					d2 = new Date(t.replace("��", "/").replace("��", "/").replace("��", ""));
					
					if(getMonth_D1){
						mM = d2.getMonth()+1;
					}
					if(getYear_D1){
						mY = d2.getYear()+1900;
					}
					
				}
					
					
//					Calendar cd1 = Calendar.getInstance();
//					cd1.set(Calendar.YEAR, Integer.parseInt(vdate[0].substring(0,vdate[0].indexOf("��"))));
//					cd1.set(Calendar.MONTH, Integer.parseInt(vdate[0].substring(vdate[0].indexOf("��")+1,vdate[0].indexOf("��"))));
//					cd1.set(Calendar.DATE, Integer.parseInt(vdate[0].substring(vdate[0].indexOf("��")+1,vdate[0].indexOf("��"))));
//					
//					Calendar cd2 = Calendar.getInstance();
//					cd2.set(Calendar.YEAR, Integer.parseInt(vdate[1].substring(0,vdate[1].indexOf("��"))));
//					cd2.set(Calendar.MONTH, Integer.parseInt(vdate[1].substring(vdate[1].indexOf("��")+1,vdate[1].indexOf("��"))));
//					cd2.set(Calendar.DATE, Integer.parseInt(vdate[1].substring(vdate[1].indexOf("��")+1,vdate[1].indexOf("��"))));
//					
					
				
				
				while(d1.before(d2) || d1.getTime() == d2.getTime()){
					Record r = new Record();
					r.set("date", new Date(d1.getTime()));
					r.set("work", 0);
					r.set("note", vctName+"("+infos[2]+")");
					vacations.add(r);
					
					d1.setDate(d1.getDate()+1); 
					
				}
				
				//����
				if(tx && infos.length>3){
					String bbxx = infos[3];
					//1��28�գ�����������1��29�գ������գ��ϰ�
					String[] bbs = bbxx.split("��|�ϰ�");
					for(String c : bbs){
						
						boolean getYear_D1 = false; //�����ʶ�����û�ȡ
						boolean getMonth_D1 = false;
						
						c = c.substring(0,c.indexOf("��"));
						
						if(c.indexOf("��")>0){
							getYear_D1 = true;
						}
						if(c.indexOf("��")>0){
							getMonth_D1 = true;
						}
						
						if(!getMonth_D1){
							c = mM+"��"+c;
						}
						if(!getYear_D1){
							c = mY+"��"+c;
						}
						
						Date sb = new Date(c.replace("��", "/").replace("��", "/").replace("��", ""));
						
						Record r2 = new Record();
						r2.set("date", new Date(sb.getTime()));
						r2.set("work", 1);
						r2.set("note", "����");
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
	 * ��ȡ�Ѿ��м������ݵ����
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
