package info.hououji.sim;

import info.hououji.sim.util.CachedDownload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class EtnetRemainValue {

	String code;
	double i2015;
	double i2014;
	double iChange;
	double earn2015;
	double earn2014;
	double goss2015 ;
	double goss2014 ;
	double gossChange = -1;
	double sales2015 ;
	double sales2014 ;
	double pureIncomeFull2015 ;
	double pureIncomeFull2014 ;
	double pureIncomeShare2015 ;
	double pureIncomeShare2014 ;
	double totalShare2015 ;
	double totalShare2014 ;
	
	
	public EtnetRemainValue(String code) {
		this.code = code ;
		try{
			String html = "" ;
			Document doc ;

			URL url = new URL("http://www.etnet.com.hk/www/tc/stocks/realtime/quote_ci_pl.php?code=" + code) ;
			html = CachedDownload.getString(url, CachedDownload.PREFIX_QUARTERLY) ;
			doc = Jsoup.parse(html ) ;
			
			{
				Elements es = doc.select("tr:contains(除稅前溢利 / (虧損)) td") ;
				String income2015 = es.get(3).html() ;
				String income2014 = es.get(4).html() ;
				
//				System.out.println(income2014 + "," + income2015) ;
				i2015 = parseDouble(income2015) ;
				i2014 = parseDouble(income2014) ;
				iChange = (i2015 - i2014) / i2014 ;
//				System.out.println("change : " + iChange) ;
			}
			
			{
				Elements es = doc.select("tr:contains(每股盈利 (仙)) td") ;
				String earn2015s = es.get(3).html() ;
				String earn2014s = es.get(4).html() ;
				
				earn2015 = parseDouble(earn2015s) / 100 ;
				earn2014 = parseDouble(earn2014s) / 100 ;
			}
			
			{
				try{
					Elements es = doc.select("tr:contains(毛利) td") ;
					String goss2015s = es.get(3).html() ;
					String goss2014s = es.get(4).html() ;
					
					goss2015 = parseDouble(goss2015s) / 100 ;
					goss2014 = parseDouble(goss2014s) / 100 ;
					gossChange = (goss2015 - goss2014) / goss2014 ;
				}catch(Exception ex) {
				}
			}
			
			{
				try{
					Elements es = doc.select("tr:contains(營業額 / 收益) td") ;
					sales2015 = parseDouble(es.get(3).html()) * 1000;
					sales2014 = parseDouble(es.get(4).html()) * 1000;
				}catch(Exception ex) {
				}
			}
			
			{
				try{
					Elements es = doc.select("tr:contains(股東應佔溢利) td") ;
					pureIncomeFull2015 = parseDouble(es.get(3).html());
					pureIncomeFull2014 = parseDouble(es.get(4).html());
					
					es = doc.select("tr:contains(每股盈利 ) td") ;
					pureIncomeShare2015 = parseDouble(es.get(3).html()) / 100;
					pureIncomeShare2014 = parseDouble(es.get(4).html()) / 100;
					totalShare2015 = pureIncomeFull2015 * 1000 / pureIncomeShare2015;
					totalShare2014 = pureIncomeFull2014 * 1000 / pureIncomeShare2014;
					
				}catch(Exception ex) {
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

//	public static File getDirectory() {
//		return new File("fix-data/etnet-income"); 
//	}
//	
//	public static void download() {
//		File dir = Downloader.getRecentDirectory() ;
//		File[] files = dir.listFiles() ;
//		Arrays.sort(files);
//		for(File file : files) {
//			try{
//				String code = file.getName().substring(0,4) ;
//				System.out.println("code:" + code) ;
//				URL url = new URL("http://www.etnet.com.hk/www/tc/stocks/realtime/quote_ci_pl.php?code=" + code) ;
//				URLConnection hc = null ;
//				hc = url.openConnection() ;
//				hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
//				String html = IOUtils.toString(hc.getInputStream()) ;
//				File outfile = new File(getDirectory(), code + ".html") ;
//				outfile.getParentFile().mkdirs() ;
//				PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outfile), "utf-8")) ;
//				out.println(html);
//				out.flush();
//				out.close();
//			}catch(Exception ex) {
//				ex.printStackTrace();
//			}
//		}
//	}
	
	public static void main2(String args[]) throws Exception {
		EtnetHistIncome e = new EtnetHistIncome("0062") ;
		System.out.println(Misc.formatMoney(e.i2014)) ;
		System.out.println(Misc.formatMoney(e.i2015)) ;
		System.out.println(Misc.trim(e.iChange)) ;
		
	}
	public static void main(String args[]) throws Exception {
		
		System.out.println(" -- calc -- ");
		
		File dir = Downloader.getRecentDirectory() ;
		File[] files = dir.listFiles() ;
		Arrays.sort(files);
		List<String> codes = new ArrayList<String>() ;
		
		System.out.println("code" 
				+ "\tday:" 
				+ "\tlow:" 
				+ "\tps 0.1 remain:");

		for(File file : files) {
			try{
				String code = file.getName().substring(0,4) ;
				
				EtnetRemainValue ehi = new EtnetRemainValue(code) ;
				
				double remain2015 = ehi.sales2015 / ehi.totalShare2015 * 0.1 ; 
				
//				System.out.println(code + ",total share:" + ehi.totalShare2015 + ", sale:" + ehi.sales2015) ;
				
				CSV csv = new CSV(code) ;
				int i1 = csv.getItemNumFromDate("2015-01-01") ;
				int i2 = csv.getItemNumFromDate("2016-03-30") ;
				int dayCount = 0 ;
				for(int i=i1; i>=i2; i--) {
					if(csv.get(i,CSV.LOW) < remain2015) {
//						System.out.println(code + ",date:" + csv.getDate(i) 
//								+ ",low:" + csv.get(i, CSV.LOW)
//								+ ",remain:" + Misc.trim(remain2015) + "");
						dayCount ++ ;
					}
				}
				if(dayCount > 0) {
					double lowest = csv.min(i2, i1-i2 + 1, CSV.LOW) ;
					System.out.println(code 
							+ "\t" + dayCount 
							+ "\t"+ Misc.df3.format(lowest)
							+ "\t"+ Misc.df3.format(remain2015)) ;
				}
				
//				System.out.println("code:" + code + ",remain:" + remain2015 + ",year avg:" + csv.avg(i2, (i1-i2), CSV.CLOSE )) ;
				
			}catch(Exception ex) {
				//ex.printStackTrace();
			}
		}

		
	}
}
