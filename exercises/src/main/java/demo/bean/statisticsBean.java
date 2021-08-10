package demo.bean;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;

public class statisticsBean {
	//
	private String[] x;
	//
	private List<Map<String,Double>> data;
	//
	public String[] getX() {
		return x;
	}
	public void setX(String[] x) {
		this.x = x;
	}
	public List<Map<String, Double>> getData() {
		return data;
	}
	public void setData(List<Map<String, Double>> data) {
		this.data = data;
	}
	
}
