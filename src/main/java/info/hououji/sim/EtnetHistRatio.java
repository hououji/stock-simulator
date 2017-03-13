package info.hououji.sim;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class EtnetHistRatio {

	String code;
	String name;
	
	class Row {
		public String name ;
		public List<String> data = new ArrayList<String>();
		public String toString() {
			String result =  name + " : "   ;
			for(String d : data) {
				result = result +d + "|" ;
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
			return this.getRow(name).data.get(i) ; 
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
	
	public EtnetHistRatio(String code) {
		this.code = code ;
		try{
			String html = "" ;
			Document doc ;

			URL url = new URL("http://www.etnet.com.hk/www/tc/stocks/realtime/quote_ci_ratio.php?code=" + code) ;
			html = CachedDownload.getString(url) ;
			doc = Jsoup.parse(html ) ;
			
			
			name = doc.select("#QuoteNameA").text() ;
			if(name.indexOf(" ") >= 0) {
				name = name.substring(name.indexOf(" ")).trim() ;
			}
			
			Elements trs = doc.select("table.figureTable tr") ;
			{
				Elements es = trs.get(0).select("td") ;
				Row r = new Row() ;
				r.name = "year" ;
				r.data.add(es.get(1).text()) ;
				r.data.add(es.get(2).text()) ;
				r.data.add(es.get(3).text()) ;
				r.data.add(es.get(4).text()) ;
				r.data.add(es.get(5).text()) ;
				dataset.date = r ;
			}
			{
				for(int count=1; count<trs.size(); count++ ){
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

	public static void main(String args[]) throws Exception {
		EtnetHistRatio ehi = new EtnetHistRatio("0040") ;
		System.out.println(ehi.name) ;
		System.out.println(ehi.dataset.toString()) ;
	}
	
}
