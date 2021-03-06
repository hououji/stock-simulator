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

public class EtnetHistCommon {

	String code;
	String name;
//	String currency;
//	double currRate ;
//	int unit;
//	Boolean lastYearIsHalf = false ;
	
	class Row {
		public String name ;
		public String data = null;
		public String toString() {
			String result =  "*** " + name + "***\r\n"   ;
			result = result + data;
			return result;
		}
	}
	class Dataset {
//		public Row date;
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
//		public double getDouble(String name, int i) {
//			String s = getStr(name, i) ;
//			double m = 1 ;
//			String[] needUnit = new String[] {
//				"營業額 / 收益","銷售成本","毛利","投資物業公平值變動及減值","投資物業公平值變動及減值", 
//				"其他項目公平值變動及減值","出售項目溢利 / (虧損)","其他非經營項目",
//				"分佔聯營公司及共同控制公司 業績","除稅前溢利 / (虧損)","稅項  ",
//				"已終止經營業務溢利 / (虧損)","非控股權益","其他項目","股東應佔溢利 / (虧損)",
//				"淨財務支出 / (收入)","折舊及攤銷 ","董事酬金"
//			};
//			for(String n : needUnit) {
//				if(n.indexOf(name) >= 0) {
//					m = unit ;
//					break;
//				}
//			}
//			
//			return Misc.parseDouble(getStr(name, i)) * m ;
//		}
		public String getStr(String name) {
			return this.getRow(name).data ; 
		}
		public String toString() {
			String result = "" ;
			for(Row r : rows) {
				result = result + "\r\n" + r.toString() ;
			}
			return result ;
		}
	}
	
	Dataset dataset = new Dataset() ;
	
	public EtnetHistCommon(String code) {
		this.code = code ;
		try{
			String html = "" ;
			Document doc ;

			URL url = new URL("http://www.etnet.com.hk/www/tc/stocks/realtime/quote_ci_brief.php?code=" + code) ;
			html = CachedDownload.getString(url) ;
			doc = Jsoup.parse(html ) ;
			
			
			name = doc.select("#QuoteNameA").text() ;
			if(name.indexOf(" ") >= 0) {
				name = name.substring(name.indexOf(" ")).trim() ;
			}
			
			Elements trs = doc.select("table.figureTable tr") ;
			{
				for(int count=0; count<trs.size(); count++ ){
					Elements es = trs.get(count).select("td") ;
					Row r = new Row() ;
					try{
						r.name = es.get(0).text() ;
						r.data = es.get(1).text() ;
//						r.data.add(es.get(3).text()) ;
//						r.data.add(es.get(4).text()) ;
//						r.data.add(es.get(5).text()) ;
//						r.data.add(es.get(6).text()) ;
					}catch(Exception ex) {
						
					}
					dataset.rows.add(r) ;
				}
			}
			
//			String thisyear = dataset.date.data.get(0) ;
//			String[] currencies = new String[]{
//					"港元","美元","人民幣", "加元", "歐元" ,"日圓", "坡元", "英鎊"
//			};
//			double currRates[] = new double[]{
//					1, 7.8, 1.12, 5.9, 8.325, 0.06676, 5.42, 9.757
//			};
//			for(int i=0; i<currencies.length; i++) {
//				String c = currencies[i] ;
//				if(thisyear.indexOf(c) >= 0 ) {
//					this.currency = c;
//					this.currRate = currRates[i] ;
//					break;
//				}
//			}
//			if(thisyear.indexOf("(K") >= 0) {
//				unit = 1000 ;
//			}else{
//				unit = 1;
//			}
//			if(thisyear.indexOf("中期") >= 0) {
//				this.lastYearIsHalf = true;
//			}else if(thisyear.indexOf("中期") >= 0) {
//				this.lastYearIsHalf = false;
//			}
//			
//			if(currency == null || lastYearIsHalf == null) {
//				System.out.println("parse fail:" + code + ",currency:" + currency + ",lastYearIsHalf:" + this.lastYearIsHalf) ;
//			}
			
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
		EtnetHistCommon ehi = new EtnetHistCommon("0040") ;
		System.out.println(ehi.name) ;
		System.out.println(ehi.dataset.toString()) ;
		System.out.println(ehi.dataset.getStr("上市日期"))  ;
	}
	
}
