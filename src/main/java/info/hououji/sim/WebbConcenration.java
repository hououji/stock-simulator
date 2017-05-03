package info.hououji.sim;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WebbConcenration {

	static class Result implements Comparable {
		public Result(String code, double con, double nonCCASS, int tradeDay) {
			this.code = code ;
			this.con = con ;
			this.nonCCASS = nonCCASS;
			this.tradeDay = tradeDay;
		}
		public String toString() {
			return code + ";" + Misc.trim(con, 4) + ";" +  Misc.trim(nonCCASS, 4) + ";" + tradeDay ;
		}
		String code ;
		double con ;
		double nonCCASS;
		int tradeDay;
		public int compareTo(Object o) {
			Result r = (Result) o;
			if(r.con > con) return 1 ;
			if(r.con < con) return -1 ;
			return 0;
		}
	}
	
	public static void main(String args[]) throws Exception 
	{
		SSLTool.disableCertificateValidation();
		String html = CachedDownload.getString(new URL("https://webb-site.com/dbpub/mcap.asp")) ;
//		System.out.println(html) ;
		
		List<Result> results = new ArrayList<Result>();
		
		Document doc ;
		doc = Jsoup.parse(html ) ;
		Elements stockHref = doc.select("table.numtable tr td:eq(1) a") ;
		for(int i=0; i<stockHref.size() ; i++) {
			String href = stockHref.get(i).attr("href") ; 
			String code  = stockHref.get(i).text() ;
			String issue = href.split("=")[1] ;
			System.out.println(code + " -> "+issue);
			
			URL stockUrl = new URL("https://webb-site.com/ccass/cconchist.asp?issue=" + issue) ;
			String stockHtml = CachedDownload.getString(stockUrl) ;
			Document stockDoc  = Jsoup.parse(stockHtml) ;
			Elements tds = stockDoc.select("table.numtable tr:eq(1) td") ; // here some problem, there are 2 table.numtable in the page
//			for(int j=0;j<tds.size();j++) {
//				System.out.println(j + ":"+tds.get(j)); 
//			}
			
			double inCCASS = Double.parseDouble(tds.get(10).text());
			double top5 = Double.parseDouble(tds.get(7).text());
			double top5AndNonCCASS = inCCASS * top5 / 100 + (100 -inCCASS) ;
			int minTradeDay = stockDoc.select("table.numtable tr").size();
//			System.out.println("top5 : " + top5) ;
//			System.out.println("inCCASS : " + inCCASS) ;
//			System.out.println("top5AndNonCCASS : " + top5AndNonCCASS) ;
////			System.out.println(stockHtml) ; 
			Detail d = new Detail(code) ;
			results.add(new Result(code + " " +d.name, top5AndNonCCASS, (100 -inCCASS),minTradeDay)) ;
		}
		
		Collections.sort(results);
		for(Result r : results) {
			System.out.println(r.toString()) ;
		}
	}
	
}
