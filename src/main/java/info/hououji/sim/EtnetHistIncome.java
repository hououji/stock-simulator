package info.hououji.sim;

import info.hououji.sim.util.CachedDownload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class EtnetHistIncome {

	String code;
	double i2015;
	double i2014;
	double iChange;
	double earn2015;
	double earn2014;
	double goss2014;
	double goss2015;
	Double gossChange = null;

	public EtnetHistIncome(String code) {
		this.code = code ;
		try{
			String html = "" ;
			Document doc ;

			URL url = new URL("http://www.etnet.com.hk/www/tc/stocks/realtime/quote_ci_pl.php?code=" + code) ;
			html = CachedDownload.getString(url, CachedDownload.PREFIX_QUARTERLY) ;
//			File file = new File(getDirectory(), code + ".html") ;
//			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8")) ;
//			html = IOUtils.toString(in) ;
			doc = Jsoup.parse(html ) ;
			
			{
				Elements es = doc.select("tr:contains(除稅前溢利 / (虧損)) td") ;
				String income2015 = es.get(3).html() ;
				String income2014 = es.get(4).html() ;
				
//				System.out.println(income2014 + "," + income2015) ;
				i2015 = parseDouble(income2015.replaceAll(",", "")) ;
				i2014 = parseDouble(income2014.replaceAll(",", "")) ;
				iChange = (i2015 - i2014) / i2014 ;
//				System.out.println("change : " + iChange) ;
			}
			
			{
				Elements es = doc.select("tr:contains(每股盈利 (仙)) td") ;
				String earn2015s = es.get(3).html() ;
				String earn2014s = es.get(4).html() ;
				
				earn2015 = parseDouble(earn2015s.replaceAll(",", "")) / 100 ;
				earn2014 = parseDouble(earn2014s.replaceAll(",", "")) / 100 ;
				
			}

			try
			{
				Elements es = doc.select("tr:contains(毛利) td") ;
				String goss2015s = es.get(3).html() ;
				String goss2014s = es.get(4).html() ;
				
				goss2015 = parseDouble(goss2015s.replaceAll(",", "")) / 100 ;
				goss2014 = parseDouble(goss2014s.replaceAll(",", "")) / 100 ;
				gossChange = (goss2015-goss2014) / goss2014 ;
			}catch(Exception ex) {
				
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

	public static File getDirectory() {
		return new File("fix-data/etnet-income"); 
	}
	
//	public static void download() {
//		File dir = Downloader.getRecentDirectory() ;
//		File[] files = dir.listFiles() ;
//		Arrays.sort(files);
//		for(File file : files) {
//			try{
//				String code = file.getName().substring(0,4) ;
//				System.out.println("code:" + code) ;
//				URL url = new URL("http://www.etnet.com.hk/www/tc/stocks/realtime/quote_ci_pl.php?code=" + code) ;
//				URLConnection hc = url.openConnection() ;
//				hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
//				String html = IOUtils.toString(hc.getInputStream()) ;
//				File outfile = new File(getDirectory(), code + ".html") ;
//				outfile.getParentFile().mkdirs() ;
//				PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outfile), "utf-8")) ;
//				out.println(html);
//				out.flush();
//				out.close();
//			}catch(Exception ex) {
//				//ex.printStackTrace();
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
		
		File dir = Downloader.getRecentDirectory() ;
		File[] files = dir.listFiles() ;
		Arrays.sort(files);
		List<String> codes = new ArrayList<String>() ;
		for(File file : files) {
			try{
				String code = file.getName().substring(0,4) ;
				
				EtnetHistIncome ehi = new EtnetHistIncome(code) ;
				
				if(ehi.i2015 < 800000) continue;

				CSV csv = new CSV(code) ;
				int i = csv.getItemNumFromDate("2015-12-31") ;
				double price1 = csv.get(i, CSV.ADJ_CLOSE) ;
				i = csv.getItemNumFromDate("2016-03-31") ;
				double price2 = csv.get(i, CSV.ADJ_CLOSE) ;
				double pe1 = price1 / ehi.earn2015 ;
				double pe2 = price2 / ehi.earn2015 ;
				double peg1 = pe1 / (ehi.iChange * 100);
				double peg2 = pe2 / (ehi.iChange * 100);;

				
				if(		ehi.iChange > 0.40 
						&& (peg1 < 0.2 || peg2 < 0.2)
						&& (pe1 < 7.9 || pe2 < 7.9)
						&& ( ehi.gossChange == null || ehi.gossChange > 0.2 )
				){
					System.out.println(code 
							+"\tpe:" + Misc.trim(pe1) + "," + Misc.trim(pe2) 
							+"\tpeg:" + Misc.trim(peg1) + "," + Misc.trim(peg2)
							+ "\tchange:" + Misc.trim(ehi.iChange) 
							+ "\tearn:" + Misc.formatMoney(ehi.i2015)
							+ "\tearn/share:" + Misc.trim(ehi.earn2015)
							+ "\tgoss/c:" + (ehi.gossChange !=null?Misc.trim(ehi.gossChange):"--")
					) ;
				}
				
			}catch(Exception ex) {
				//ex.printStackTrace();
			}
		}

		
	}
}
