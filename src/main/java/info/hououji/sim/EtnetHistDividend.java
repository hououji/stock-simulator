package info.hououji.sim;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class EtnetHistDividend {

	String code;
	String name;
	
	Map<Integer, Double> yDiv = new LinkedHashMap<Integer, Double>() ;
	
	public String toString() {
		String result = "" ;
		for(Integer year : yDiv.keySet()) {
			result += year + ":" + yDiv.get(year)+"," ;
		}
		return result ;
	}
	
	public EtnetHistDividend(String code) {
		this.code = code ;
		try{
			String html = "" ;
			Document doc ;

			URL url = new URL("http://www.etnet.com.hk/www/tc/stocks/realtime/quote_dividend.php?code=" + code) ;
			html = CachedDownload.getString(url) ;
			doc = Jsoup.parse(html ) ;
			
			
			name = doc.select("#QuoteNameE").text() ;
			if(name.indexOf(" ") >= 0) {
				name = name.substring(name.indexOf(" ")).trim() ;
			}
			
			Elements trs = doc.select("table.figureTable tr") ;
			{
				for(int count=1; count<trs.size(); count++ ){
					Elements es = trs.get(count).select("td") ;
					String yearStr = es.get(1).text() ;
					String event = es.get(2).text() ;
					try{
						if(event.indexOf("特別") >= 0) continue ;
						if(event.indexOf("不派") >= 0) continue ;
						if(event.indexOf("息") == -1) continue ;
						
						int year = Integer.parseInt(yearStr.substring(0, 4)) ;
						double div = 0 ;
						if(year < new Date().getYear() + 1900 - 6 ) continue;
						event = numConv(event) ;
						
						if(event.indexOf("港元") >= 0) { // 992
							int idxE = event.indexOf("港元") ;
							int idxB; 
							for(idxB = idxE; idxB>0; idxB--) {
								String t = event.substring(idxB-1, idxB) ;
								if("1234567890.".indexOf(t) >= 0) {
									continue;
								}else{
									break;
								}
							}
							String hkd = event.substring(idxB, idxE) ;
							div = Double.parseDouble(hkd) ;
//							System.out.println(year + "," + event + "," + hkd) ;
						}else if(event.indexOf("人民幣") >= 0) {
							if(event.indexOf("分") >=0 ) {
								div = getValueBefore(event,"分") * 0.01 * 1.2 ;
							}
							if(event.indexOf("元") >=0 ) {
								div = getValueBefore(event,"元") * 0.01 * 1.2 ;
							}
						}else if (event.indexOf("便士") >= 0) {
							div = getValueBefore(event,"便士") * 0.01 * 10.8 ;
						}else if (event.indexOf("日圓") >= 0) {
							div = getValueBefore(event,"日圓") * 0.071734 ;
						}else if (event.indexOf("港仙") >= 0) {
							div = getValueBefore(event,"港仙") * 0.01 ;
						}else if (event.indexOf("美仙") >= 0) {
							div = getValueBefore(event,"美仙") * 7.8 ;
						}else if (event.indexOf("歐元") >= 0) {
							div = getValueBefore(event,"歐元") * 9.47 ;
						}else{// normal
							
							// TODO : check if there only one set of double number 
							
							if(event.indexOf("仙") >=0 ) {
								div = getValueBefore(event,"仙") * 0.01 ;
							}
							if(event.indexOf("元") >= 0) {
								div = getValueBefore(event,"元") ;
							}
							
						}
						
						if(div == 0) { // for debug use
							System.out.println("Error : " + code + " " + name + ":" + year + "," + event) ;
//							System.exit(-1);
						}
						
						Double d = yDiv.get(year) ;
						if(d == null) d = new Double(0) ;
						d = new Double(Misc.trim(d.doubleValue() + div)) ;
						yDiv.put(year, d) ;
						
					}catch(Exception ex) {
						System.err.println(event) ;
						ex.printStackTrace();
					}
				}
			}
			
		}catch(Exception ex	){
			ex.printStackTrace();
		}
	}
	
	public static double getValueBefore(String event, String s) {
		int idxE = event.indexOf(s) ;
		int idxB ;
		for(idxB = idxE; idxB>0; idxB--) {
			String t = event.substring(idxB-1, idxB) ;
			if("1234567890.".indexOf(t) >= 0) {
				continue;
			}else{
				break;
			}
		}
		String hkd = event.substring(idxB, idxE) ;
		if("".equals(hkd)) return 0;
		double div = Double.parseDouble(hkd) ;
		return div ;
	}
	
	public static double parseDouble(String s) {
		s = s.replaceAll(",", "").trim() ;
		if(s.startsWith("(") && s.endsWith(")")) {
			s = "-" +s.substring(1, s.length() - 1) ;
		}
		return Double.parseDouble(s) ;
	}
	public static String numConv(String s) {
		s = s.replaceAll("１", "1") ;
		s = s.replaceAll("２", "2") ;
		s = s.replaceAll("３", "3") ;
		s = s.replaceAll("４", "4") ;
		s = s.replaceAll("５", "5") ;
		s = s.replaceAll("６", "6") ;
		s = s.replaceAll("７", "7") ;
		s = s.replaceAll("８", "8") ;
		s = s.replaceAll("９", "9") ;
		s = s.replaceAll("０", "0") ;
		s = s.replaceAll("﹒", ".") ;
		return s;
	}
	
	public static void main(String args[]) throws Exception {
		File dir = Downloader.getRecentDirectory() ;
		File[] files = dir.listFiles() ;
		Arrays.sort(files);
		List<String> results = new ArrayList<String>() ;
		for(File file : files) {
			String code = file.getName().substring(0,4) ;
//			if(Integer.parseInt(code) < 973) continue;
			EtnetHistDividend ehi = new EtnetHistDividend(code) ;
			
			CSV csv = new CSV(code) ;
			double price = csv.get(0, CSV.ADJ_CLOSE) ;
			double div = 0 ;
			for(int year = 2015; year <=2017; year ++) {
				if(ehi.yDiv.get(year) != null) {
					div += ehi.yDiv.get(year) ; 
				}
			}
			div = div / 3 ;
			if(div / price > 0.05) {
				results.add(ehi.name + ",current rate : " + Misc.trim(div/price * 100) + "%" + "," + ehi) ;
			}		
		}
		
		for(String s : results) {
			System.out.println(s) ;
		}
	}
	
	public static void main1(String args[]) throws Exception {
//		EtnetHistDividend ehi = new EtnetHistDividend("0941") ;
		EtnetHistDividend ehi = new EtnetHistDividend("0067") ;
		
		System.out.println(ehi.name) ;
		System.out.println(ehi) ;
//		System.out.println(ehi.dataset.toString()) ;
	}
	
}
