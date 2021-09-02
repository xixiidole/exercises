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
			//没有offset默认当天
			rec.set("item_date", new Date());
		}
		
		rec.set("item_note", "快捷录入");
		rec.set("item_type", type);
		boolean save = Db.save("bill", rec);
		if(save){
			result.set("result", "success");
			result.set("cost", cost);
		}else{
			result.set("result", "error");
			result.set("msg", "保存返回失败！");
		}
		return result;
	}
	
	@Before(Tx.class)
	public static Record updateBill(String name, String date, String cost){
		Record result = new Record();
		
		try{		
			if(StrKit.isBlank(date) || StrKit.isBlank(name)){
				result.set("result", "error");
				result.set("msg", "必要参数为空！");
				return result;
			}
			int upd = Db.update("update bill set cost=?, upd_time=? where item_name=? and item_date=?", cost, new Date(), name, date);
			if(upd>0){
				result.set("result", "success");
				result.set("cost", cost);
			}else{
				result.set("result", "error");
				result.set("msg", "更新失败！没有数据被更新！");
			}
		}catch(Exception e){
			e.printStackTrace();
			result.set("result", "error");
			result.set("msg", "更新失败！");
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
			if("早餐".equals(query.get(i).getStr("itemName"))){
				rec.set("breakfast", "1");
				allCost += query.get(i).getDouble("cost");
				rec.set("breakfastMsg", "已录入；金额："+query.get(i).getDouble("cost")+"元");
			}else if("午餐".equals(query.get(i).getStr("itemName"))){
				rec.set("lunch", "1");
				allCost += query.get(i).getDouble("cost");
				rec.set("lunchMsg", "已录入；金额："+query.get(i).getDouble("cost")+"元");
			}else if("晚餐".equals(query.get(i).getStr("itemName"))){
				rec.set("dinner", "1");
				allCost += query.get(i).getDouble("cost");
				rec.set("dinnerMsg", "已录入；金额："+query.get(i).getDouble("cost")+"元");
			}else if("其他".equals(query.get(i).getStr("itemName"))){
				rec.set("other", "1");
				allCost += query.get(i).getDouble("cost");
				rec.set("otherMsg", "已录入；金额："+query.get(i).getDouble("cost")+"元");
			}else if("水电燃气".equals(query.get(i).getStr("itemName"))){
				rec.set("eaw", "1");
				allCost += query.get(i).getDouble("cost");
				rec.set("eawMsg", "已录入；金额："+query.get(i).getDouble("cost")+"元");
			}else if("房租".equals(query.get(i).getStr("itemName"))){
				rec.set("rent", "1");
				allCost += query.get(i).getDouble("cost");
				rec.set("rentMsg", "已录入；金额："+query.get(i).getDouble("cost")+"元");
			}
		}
		rec.set("allCost", allCost);
		return rec;
	}
	
	public static List<Record> billStatistics(String duration){
		List<Record> statisticsData = null;
		if("1".equals(duration)){
			statisticsData = getMeCurWeekData();
			//当前周
		}else if("2".equals(duration)){
			statisticsData = getMeCurMonthData();
		}
		
		return statisticsData;
		
	}
	
	private static List<Record> getMeCurWeekData(){
		//查询本周-星期一没录就看不到数据
		/*String sql = "select b.item_Date itemDate, b.item_name itemName, b.cost cost "+
							"from bill b "+
							"where b.item_date between date_sub(curdate(),INTERVAL WEEKDAY(curdate()) + 0 DAY) and date_sub(curdate(),INTERVAL WEEKDAY(curdate()) - 6 DAY) "+
							"order by b.item_date asc, b.item_type";*/
		//近7天数据-没有的天数给个默认值
		String sql = "select a.date itemDate,b.item_Date itemDate2, IFNULL(b.item_name,'早餐') itemName, IFNULL(b.cost,0) cost from ( "+
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
	 * @param month 1至12
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
		String sql = "select '三餐' as type, round(sum(case when item_type in (1,2,3) then b.cost else 0 end),2) cost "+
							"from bill b "+
							"where b.item_date between '"+year+"-"+month+"-01' and '"+year+"-"+month+"-"+lastDay+"' "+
							"UNION ALL "+
							"select '房租' as type, sum(case when item_type = 6 then b.cost else 0 end) cost "+
							"from bill b "+
							"where b.item_date between '"+year+"-"+month+"-01' and '"+year+"-"+month+"-"+lastDay+"' "+
							"UNION ALL "+
							"select '其他消费' as type, sum(case when item_type = 4 then b.cost else 0 end) cost "+
							"from bill b "+
							"where b.item_date between '"+year+"-"+month+"-01' and '"+year+"-"+month+"-"+lastDay+"' "+
							"UNION ALL "+
							"select '水电燃' as type, sum(case when item_type = 5 then b.cost else 0 end) cost "+
							"from bill b "+
							"where b.item_date between '"+year+"-"+month+"-01' and '"+year+"-"+month+"-"+lastDay+"' ";
		List<Record> PieMonth = Db.find(sql);
		return PieMonth;
	}
	
}
