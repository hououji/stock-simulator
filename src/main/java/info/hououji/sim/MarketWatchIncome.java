package info.hououji.sim;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class MarketWatchIncome {

	String code;
	String name;
	String currency;
	public String thisyear;
	double currRate ;
	int unit;
	Boolean lastYearIsHalf = false ;
	
	class Row {
		public String name ;
		public List<String> data = new ArrayList<String>();
		public String toString() {
			String result =  Misc.pad(name, 20)   ;
			for(String d : data) {
				result = result + "|" + Misc.pad(d,10) ;
			}
			return result;
		}
	}
	class Dataset {
		public Row date;
		public List<Row> rows = new ArrayList<Row>();
		public Row getRow(String name) {
			if(name == null)return null ;
			for(Row r : rows) {
				if(r.name == null) continue ;
				if(r.name.indexOf(name) >= 0) {
					return r ;
				}
			}
			return null ;
		}
		public double getDouble(String name, int i) {
			String s = getStr(name, i) ;
			return Misc.parseDouble(getStr(name, i)) ;
		}
		public String getStr(String name, int i) {
			try{
				return this.getRow(name).data.get(i) ;
			}catch(NullPointerException ex) {
				System.out.println("fail when getStr : " + name);
				throw ex;
			}
		}
		public String toString() {
			String result = date.toString() ;
			for(Row r : rows) {
				result = result + "\r\n" + r.toString() ;
			}
			return result ;
		}
	}
	
	Dataset dataset = new Dataset() ;
	
	public MarketWatchIncome(String code) {
		this.code = code ;
		try{
			String html = "" ;
			Document doc ;

			URL url = new URL("https://www.marketwatch.com/investing/stock/"+code+"/financials") ;
			html = CachedDownload.getString(url) ;
//System.out.println(html) ;			
			doc = Jsoup.parse(html ) ;
			
			
//			name = doc.select("#QuoteNameA").text() ;
//			if(name.indexOf(" ") >= 0) {
//				name = name.substring(name.indexOf(" ")).trim() ;
//			}
			
			{
				Elements trsYear = doc.select("table.crDataTable thead tr.topRow") ;
				Elements es = trsYear.get(0).select("th") ;
				Row r = new Row() ;
				r.name = "year" ;
				r.data.add(es.get(1).text()) ;
				r.data.add(es.get(2).text()) ;
				r.data.add(es.get(3).text()) ;
				r.data.add(es.get(4).text()) ;
				r.data.add(es.get(5).text()) ;
				dataset.date = r ;
			}
			
			Elements trs = doc.select("table.crDataTable tbody tr") ;
			{
				for(int count=0; count<trs.size(); count++ ){
					Elements es = trs.get(count).select("td") ;
					Row r = new Row() ;
					try{
						r.name = es.get(0).text() ;
						r.data.add(es.get(1).text()) ;
						r.data.add(es.get(2).text()) ;
						r.data.add(es.get(3).text()) ;
						r.data.add(es.get(4).text()) ;
						r.data.add(es.get(5).text()) ;
					}catch(Exception ex) {
						
					}
					dataset.rows.add(r) ;
				}
			}
			
			
		}catch(Exception ex	){
//			ex.printStackTrace();
		}
	}
	
	public static double parseDouble(String s) {
		s = s.replaceAll(",", "").trim() ;
		if(s.startsWith("(") && s.endsWith(")")) {
			s = "-" +s.substring(1, s.length() - 1) ;
		}
		return Double.parseDouble(s) ;
	}
	
	public Date getStartDate(int i) throws Exception {
		Date endDate = getEndDate(i);
		Date startDate = null;
		if(this.lastYearIsHalf && i == 0) {
			startDate = new Date(endDate.getTime() - 364L * 1000 * 60 * 60 * 24 / 2) ;
		}else{
			startDate = new Date(endDate.getTime() - 364L * 1000 * 60 * 60 * 24 ) ;
		}

		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
		return c.getTime() ;
	}
	public Date getEndDate(int i) throws Exception {
		String d = dataset.date.data.get(i) ;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM") ;
		Date date = sdf.parse(d.substring(0, 7)) ;
		
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		return c.getTime() ;
	}

	
	public static void main(String args[]) throws Exception {
		MarketWatchIncome mwi = new MarketWatchIncome("unh") ;
		
		for(Row r : mwi.dataset.rows) {
			System.out.println(r.toString()) ;
		}
		
		System.out.println(mwi.dataset.date.data.get(4)) ;
		System.out.println(Misc.formatMoney(mwi.dataset.getDouble("Sales/Revenue", 4))) ;
		System.out.println(Misc.formatMoney(mwi.dataset.getDouble("Diluted Shares Outstanding", 4))) ;
		
//		System.out.println(ehi.name) ;
//		System.out.println(ehi.dataset.toString()) ;
	}
	
//	public static void main2(String args[] ) throws Exception {
//		File dir = Downloader.getRecentDirectory() ;
//		File[] files = dir.listFiles() ;
//		Arrays.sort(files);
//		List<String> codes = new ArrayList<String>() ;
//		Set<String> titles = new HashSet<String>() ;
//		for(File file : files) {
//			String code = file.getName().substring(0,4) ;
//			try{
//				MarketWatchIncome ehi = new MarketWatchIncome(code) ;
////				System.out.println(ehi.dataset.date.data.get(0)) ;
//				titles.add(ehi.dataset.date.data.get(0) + "|" + ehi.currency+"|" + ehi.unit + "|" +ehi.lastYearIsHalf) ;
//			}catch(Exception e) {
//				System.out.println("fail:" + code) ;
//			}
//		}
//		for(String s: titles) {
//			System.out.println(s) ;
//		}
//	}
	
	public static void main3(String args[]) throws Exception {
		
	}
}
