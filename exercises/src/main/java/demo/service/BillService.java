package demo.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

public class BillService {
	
	@Before(Tx.class)
	public static Record inputBill(String name, String cost, String type, Integer offset){
		Record result = new Record();
		
		Record rec = new Record();
		rec.set("item_name", name);
		rec.set("cost", cost);
		rec.set("rec_time", new Date());
		if(null!=offset){
			//
			Calendar ca = Calendar.getInstance();
			ca.add(Calendar.DATE, offset);
			rec.set("item_date", ca.getTime());
		}else{
			//û��offsetĬ�ϵ���
			rec.set("item_date", new Date());
		}
		
		rec.set("item_note", "���¼��");
		rec.set("item_type", type);
		boolean save = Db.save("bill", rec);
		if(save){
			result.set("result", "success");
			result.set("cost", cost);
		}else{
			result.set("result", "error");
			result.set("msg", "���淵��ʧ�ܣ�");
		}
		return result;
	}
	
	@Before(Tx.class)
	public static Record updateBill(String name, String date, String cost){
		Record result = new Record();
		
		try{		
			if(StrKit.isBlank(date) || StrKit.isBlank(name)){
				result.set("result", "error");
				result.set("msg", "��Ҫ����Ϊ�գ�");
				return result;
			}
			int upd = Db.update("update bill set cost=?, upd_time=? where item_name=? and item_date=?", cost, new Date(), name, date);
			if(upd>0){
				result.set("result", "success");
				result.set("cost", cost);
			}else{
				result.set("result", "error");
				result.set("msg", "����ʧ�ܣ�û�����ݱ����£�");
			}
		}catch(Exception e){
			e.printStackTrace();
			result.set("result", "error");
			result.set("msg", "����ʧ�ܣ�");
		}
		return result;
	}
	
	public static List<Record> checkBill(String date){
		List<Record> query = Db.find("select b.item_Name itemName,b.item_Date itemDate, b.cost cost from bill b where b.item_date = ? order by item_type asc",date);
		return query;
	}
	
	public static Record checkToday(String date){
		List<Record> query = checkBill(date);
		Record rec = new Record();
		double allCost = 0;
		for(int i=0;i<query.size();i++){
			if("���".equals(query.get(i).getStr("itemName"))){
				rec.set("breakfast", "1");
				allCost += query.get(i).getDouble("cost");
				rec.set("breakfastMsg", "��¼�룻��"+query.get(i).getDouble("cost")+"Ԫ");
			}else if("���".equals(query.get(i).getStr("itemName"))){
				rec.set("lunch", "1");
				allCost += query.get(i).getDouble("cost");
				rec.set("lunchMsg", "��¼�룻��"+query.get(i).getDouble("cost")+"Ԫ");
			}else if("���".equals(query.get(i).getStr("itemName"))){
				rec.set("dinner", "1");
				allCost += query.get(i).getDouble("cost");
				rec.set("dinnerMsg", "��¼�룻��"+query.get(i).getDouble("cost")+"Ԫ");
			}else if("����".equals(query.get(i).getStr("itemName"))){
				rec.set("other", "1");
				allCost += query.get(i).getDouble("cost");
				rec.set("otherMsg", "��¼�룻��"+query.get(i).getDouble("cost")+"Ԫ");
			}else if("ˮ��ȼ��".equals(query.get(i).getStr("itemName"))){
				rec.set("eaw", "1");
				allCost += query.get(i).getDouble("cost");
				rec.set("eawMsg", "��¼�룻��"+query.get(i).getDouble("cost")+"Ԫ");
			}else if("����".equals(query.get(i).getStr("itemName"))){
				rec.set("rent", "1");
				allCost += query.get(i).getDouble("cost");
				rec.set("rentMsg", "��¼�룻��"+query.get(i).getDouble("cost")+"Ԫ");
			}
		}
		rec.set("allCost", allCost);
		return rec;
	}
	
	public static List<Record> billStatistics(String duration){
		List<Record> statisticsData = null;
		if("1".equals(duration)){
			statisticsData = getMeCurWeekData();
			//��ǰ��
		}else if("2".equals(duration)){
			statisticsData = getMeCurMonthData();
		}
		
		return statisticsData;
		
	}
	
	private static List<Record> getMeCurWeekData(){
		//��ѯ����-����һû¼�Ϳ���������
		/*String sql = "select b.item_Date itemDate, b.item_name itemName, b.cost cost "+
							"from bill b "+
							"where b.item_date between date_sub(curdate(),INTERVAL WEEKDAY(curdate()) + 0 DAY) and date_sub(curdate(),INTERVAL WEEKDAY(curdate()) - 6 DAY) "+
							"order by b.item_date asc, b.item_type";*/
		//��7������-û�е���������Ĭ��ֵ
		String sql = "select a.date itemDate,b.item_Date itemDate2, IFNULL(b.item_name,'���') itemName, IFNULL(b.cost,0) cost from ( "+
								"select date from getDateList a "+
								"where a.Date BETWEEN date_sub(curdate(),INTERVAL 6 day) and curdate() "+
							") a "+
							"left join bill b on a.date = b.item_date "+
							"order by a.date asc, b.item_type ";
		
		List<Record> query = Db.find(sql);
		return query;
	}
	
	private static List<Record> getMeCurMonthData(){
		String sql = "select b.item_Date itemDate, b.item_name itemName, b.cost cost "+
							"from bill b "+
							"where b.item_date between concat(date_format(LAST_DAY(now()),'%Y-%m-'),'01') and LAST_DAY(now()) "+
							"order by b.item_date asc, b.item_type";
		List<Record> query = Db.find(sql);
		return query;
	}
	
	/**
	 * @param year
	 * @param month 1��12
	 * @return
	 */
	public static Double countMonth(int year, int month){
		Calendar c = Calendar.getInstance();
		c.set(year, month-1, 1);
		int lastDay = c.getActualMaximum(Calendar.DATE);
		return getMeCurMonthCount(year,month,lastDay);
	}
	
	private static Double getMeCurMonthCount(int year,int month,int lastDay){
		String sql = "select sum(b.cost) cost "+
							"from bill b "+
							"where b.item_date between '"+year+"-"+month+"-01' and '"+year+"-"+month+"-"+lastDay+"' ";
		Double mnthCost = Db.queryDouble(sql);
		return mnthCost;
	}
	
	public static List<Record> PieMonth(int year, int month){
		Calendar c = Calendar.getInstance();
		c.set(year, month-1, 1);
		int lastDay = c.getActualMaximum(Calendar.DATE);
		return getMeMonthPieData(year,month,lastDay);
	}
	
	private static List<Record> getMeMonthPieData(int year,int month,int lastDay){
		String sql = "select '����' as type, round(sum(case when item_type in (1,2,3) then b.cost else 0 end),2) cost "+
							"from bill b "+
							"where b.item_date between '"+year+"-"+month+"-01' and '"+year+"-"+month+"-"+lastDay+"' "+
							"UNION ALL "+
							"select '����' as type, sum(case when item_type = 6 then b.cost else 0 end) cost "+
							"from bill b "+
							"where b.item_date between '"+year+"-"+month+"-01' and '"+year+"-"+month+"-"+lastDay+"' "+
							"UNION ALL "+
							"select '��������' as type, sum(case when item_type = 4 then b.cost else 0 end) cost "+
							"from bill b "+
							"where b.item_date between '"+year+"-"+month+"-01' and '"+year+"-"+month+"-"+lastDay+"' "+
							"UNION ALL "+
							"select 'ˮ��ȼ' as type, sum(case when item_type = 5 then b.cost else 0 end) cost "+
							"from bill b "+
							"where b.item_date between '"+year+"-"+month+"-01' and '"+year+"-"+month+"-"+lastDay+"' ";
		List<Record> PieMonth = Db.find(sql);
		return PieMonth;
	}
	
}
