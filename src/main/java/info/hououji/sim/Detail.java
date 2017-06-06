package info.hououji.sim;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Detail {

	String code = "" ;
	double marketCap ;
	double pe ;
	double div ;
	double close;
	String name = "";
	String vol = "";
	double p52wl ;
	double p52wh ;
	double pb;
	double nav;
	double earn;
	boolean stockSuspend = false ;
	
//	public float pb = -1 ;
//	public float dividendyield = 0 ;
	
	public Detail() {}
	
	public Detail(String code) {
		InputStream in = null;
		this.code = code ;
		try{
			String html = "" ;
			Document doc ;
			
//			URL url = new URL("http://www.aastocks.com/tc/mobile/Quote.aspx?symbol=0" + code) ;
//			URL url = new URL("http://www.aastocks.com/tc/stocks/quote/detail-quote.aspx?symbol=0" + code) ;
			URL url = new URL("http://www.quamnet.com/Quote.action?stockCode=" + code) ;
			
//			System.out.println(url.toString()) ;
//			URLConnection hc = url.openConnection() ;
//			hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
//			in = hc.getInputStream() ;
//			html = IOUtils.toString(in) ;
			html = CachedDownload.getString(url) ;
			doc = Jsoup.parse(html ) ;
//			System.out.println(html) ;
			
			Elements es ;
			
			es = getElements(doc,"市值");
			for(Element e :es) {
				try{
					String s = e.html() ;
					if(s.indexOf("B") > 0) {
						this.marketCap  = trim(Double.parseDouble(e.html().replaceAll(",", "").replaceAll("B", "")) * 10) ;
					}
					if(s.indexOf("M") > 0) {
						this.marketCap  = trim(Double.parseDouble(e.html().replaceAll(",", "").replaceAll("M", "")) / 100) ;
					}
					break;
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			
			es = getElements(doc,"市盈率");
			for(Element e :es) {
				try{
					this.pe = parseDouble(e.html().replaceAll("%", "")) ;
					break;
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			es = getElements(doc,"週息率");
			for(Element e :es) {
				try{
					div = parseDouble(e.html().replaceAll("%", "")) ;
					break;
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			es = doc.select("div:contains(現價) + div") ;
			for(Element e :es) {
				try{
					if("停牌".equals(e.html())) {
						this.stockSuspend = true;
						return ;
					}
					close = parseDouble(e.html()) ;
					break;
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			es = doc.select("#chartAnnouncement a.content_lt_blue_link") ;
			for(Element e :es) {
				String name = e.html() ;
				if(name == null) continue;
				name = name.trim() ;
				if("".equals(name)) continue;
				int idx = name.indexOf("(") ;
				if(idx == -1) {
					this.name = name ;
					break;
				}
				this.name = name.substring(0,idx) ;
				break;
			}
			
			es = doc.select("div:contains(成交額) + div") ;
			for(Element e :es) {
				try{
					vol = e.html() ;
					break;
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			es = doc.select("div:contains(52週高位) + div") ;
			for(Element e :es) {
				try{
					this.p52wh =  parseDouble(e.html()) ;
					break;
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			es = doc.select("div:contains(52週低位) + div") ;
			for(Element e :es) {
				try{
					this.p52wl = parseDouble(e.html()) ;
					break;
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			es = getElements(doc,"市賬率");
			for(Element e :es) {
				try{
					String s = e.html();
					this.pb =  parseDouble(e.html()) ;
					break;
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			es = getElements(doc,"每股淨值");
			for(Element e :es) {
				try{
					this.nav = parseDouble(e.html()) ;
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			es = getElements(doc,"每股盈利");
			for(Element e :es) {
				try{
					this.earn = parseDouble(e.html()) ;
					break;
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
		}catch(Exception ex){
			throw new RuntimeException(ex) ;
		}finally{
			IOUtils.closeQuietly(in);
		}
	}

	private Elements getElements(Document doc, String s ) {
		return doc.select("#chartSummaryLeft td:contains("+s+") + td span") ;
	}
	
	public double getMarketCap() {
		return this.marketCap ;
	}

	public double getPe() {
		return this.pe ;
	}

	public double getDiv() {
		return this.div ;
	}
	
	public double getClose() {
		return this.close ;
	}
	
	public String getName() {
		return this.name ;
	}
	
	public String getVol() {
		return this.vol ;
	}
	
	public double get52wh() {
		return this.p52wh ;
	}

	public double get52wl() {
		return this.p52wl ;
	}
	
	public double getPb() {
		return this.pb;
	}

	public double getNav() {
		return this.nav ;
	}
	
	public double getEarn() {
		return this.earn ;
	}
	
	private static float decode(String s) {
		s = s.replace(",", "") ;
		float i = Float.parseFloat(s) ;
		return i;
		
	}
	
	public String toString() {
		return code 
				+ "," +this.getName() 
				+","+ this.getMarketCap() 
				+ "," + this.getVol()
				+","+this.getPe()
				
				+"," + this.getPb() 
				+","+this.getDiv()
				+"," + this.getEarn()
				+"," + this.getNav()
				+"," + this.getClose()
				
				+"," + this.get52wl()
				+"," + this.get52wh()
				
				;
	}
	public static String title() {
		return "code,name,cap,vol,pe,pb,div,earn,nav,close,w52h,w52l" ;
	}
	
	public static double parseDouble(String s) {
		double d = Misc.parseDouble(s) ;
		return trim(d) ;
	}
	public static double trim(double d) {
		if(d > 100) return Math.round(d) ;
		return Math.round(d * 100) / 100d ;
	}
	
	public static void main(String args[]) throws Exception {
//		Detail d = new Detail("0005") ;
//		d.writeToFile();
		
		Detail d = new Detail("0342") ;
		System.out.println("PB: " + d.getPb()) ;
		System.out.println("PE: " + d.getPe()) ;
		System.out.println("Div:"+d.getDiv()) ;
		System.out.println("Cap:"+d.getMarketCap()) ;
		System.out.println("Name:"+d.getName()) ;
		System.out.println("Close:"+d.getClose()) ;
		System.out.println("Vol:"+d.getVol()) ;
		System.out.println("52H:"+d.get52wh()) ;
		System.out.println("52L:"+d.get52wl()) ;
		System.out.println("NAV:"+d.getNav()) ;
		System.out.println("Earn:"+d.getEarn()) ;
		
		
//		Document doc = Jsoup.parse(d.html ) ;
//		Elements es = doc.select("#chartSummaryLeft td:contains(市賬率) + td span") ;
//		System.out.println("size:" + es.size());
//		for(Element e :es) {
//			System.out.println(">>>" + e.html() + "<<<") ;
//		}
		
//		System.out.println("pe:" + d.getPe());
		
		// Real
//		File dir = Downloader.getRecentDirectory() ;
//		System.out.println(dir.getAbsolutePath()) ;
//		File[] files = dir.listFiles() ;
//		Arrays.sort(files);
//		List<String> codes = new ArrayList<String>() ;
//		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("output/market_cap.csv"),"utf8") ;
//		PrintWriter out = new PrintWriter(osw); 
//		out.print("\uFEFF"); // BOM, make Excel auto use UTF8
//		for(File file : files) {
//			try{
//				String code = Downloader.getName(file) ;
//				Detail a = new Detail(code) ;
//				out.println(a.toString()) ;
//				System.out.println(a.toString());
//				out.flush(); 
//				Thread.sleep(1000);
//			}catch(Exception ex){
//				ex.printStackTrace(); 
//			}
//		}
//		out.flush(); 
//		out.close();
//		osw.close();
//	

	}
	
}
