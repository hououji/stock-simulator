package info.hououji.sim;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FinancialModeling {

	String code;
	String name;
	String currency;
	public String thisyear;
	double currRate ;
	int unit;
	Boolean lastYearIsHalf = false ;
	
	List<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>() ;
	
	public FinancialModeling(String code) {
		this.code = code ;
		try{
			String json = "" ;
			JSONParser parser = new JSONParser();
			JSONArray keyMetrics=null;
			
			URL url = new URL("https://financialmodelingprep.com/api/v3/key-metrics/"+code+"?period=annual&apikey=43cbGz3j2s0mbIazs0Lctq1PWMLHONY8") ;
			json = CachedDownload.getString(url) ;
			keyMetrics = (JSONArray) parser.parse(json);
			
			
			for(int i =0; i<keyMetrics.size() ; i++) {
				JSONObject income = (JSONObject)keyMetrics.get(i);
				HashMap<String,String> map = new HashMap<String,String>() ;
				data.add(map) ;
				for(Object key : income.keySet()) {
					map.put((String)key, income.get(key)+"") ;
				}
			}
			
		}catch(Exception ex	){
			ex.printStackTrace();
		}
	}
	
	public static double parseDouble(String s) {
		s = s.replaceAll(",", "").trim() ;
		if(s.startsWith("(") && s.endsWith(")")) {
			s = "-" +s.substring(1, s.length() - 1) ;
		}
		return Double.parseDouble(s) ;
	}
	
	
	public static void main(String args[]) throws Exception {
		FinancialModeling mwi = new FinancialModeling("XOM") ;
		
		System.out.println(mwi.data.get(0).get("date")) ;
		System.out.println(mwi.data.get(0).get("revenuePerShare")) ;
		
	}
	
	
	public static void main3(String args[]) throws Exception {
		
	}
}
