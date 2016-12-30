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

	String html = "" ;
	Document doc ;
	
	String code = "" ;
	
//	public float pb = -1 ;
//	public float dividendyield = 0 ;
	
	public Detail() {}
	
	public Detail(String code) {
		InputStream in = null;
		this.code = code ;
		try{
//			URL url = new URL("http://www.aastocks.com/tc/mobile/Quote.aspx?symbol=0" + code) ;
//			URL url = new URL("http://www.aastocks.com/tc/stocks/quote/detail-quote.aspx?symbol=0" + code) ;
			URL url = new URL("http://www.quamnet.com/Quote.action?stockCode=" + code) ;
			
//			System.out.println(url.toString()) ;
			URLConnection hc = url.openConnection() ;
			hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			in = hc.getInputStream() ;
			html = IOUtils.toString(in) ;
			doc = Jsoup.parse(html ) ;
//			System.out.println(html) ;
		}catch(Exception ex){
			throw new RuntimeException(ex) ;
		}finally{
			IOUtils.closeQuietly(in);
		}
	}
	
	public void writeToFile() {
		try{
			PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream("./output/detail.html"),"utf-8")) ;
			out.print(html) ;
			out.flush();
			out.close();
		}catch(Exception ex){
			ex.printStackTrace(); 
		}
	}
	
	public void readFromFile() {
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("./output/detail.html"), "utf-8"));
			html = IOUtils.toString(in) ;
			doc = Jsoup.parse(html ) ;
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private Elements getElements(String s) {
		return doc.select("#chartSummaryLeft td:contains("+s+") + td span") ;
	}
	
	public double getMarketCap() {
		Elements es = getElements("市值");
		for(Element e :es) {
			try{
				return Double.parseDouble(e.html().replaceAll(",", "").replaceAll("B", "")) * 10 ;
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return 0;
	}

	public double getPe() {
		Elements es = getElements("市盈率");
		for(Element e :es) {
			try{
				return Double.parseDouble(e.html().replaceAll("%", "")) ;
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return 0;
	}

	public double getInt() {
		Elements es = getElements("週息率");
		for(Element e :es) {
			try{
				return Double.parseDouble(e.html().replaceAll("%", "")) ;
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return 0;
	}
	
	public double getClose() {
		Elements es = doc.select("div:contains(現價) + div") ;
		for(Element e :es) {
			try{
				return Double.parseDouble(e.html()) ;
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return 0;
	}
	
	public String getName() {
		Elements es = doc.select("#chartAnnouncement a.content_lt_blue_link") ;
		for(Element e :es) {
			String name = e.html() ;
			if(name == null) continue;
			name = name.trim() ;
			if("".equals(name)) continue;
			int idx = name.indexOf("(") ;
			if(idx == -1) return name ;
			return name.substring(0,idx) ;
		}
		return "" ;
	}
	
	public String getVol() {
		
		Elements es = doc.select("div:contains(成交額) + div") ;
		for(Element e :es) {
			try{
				return e.html() ;
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return "";
	}
	
//	public String getHist52w() {
//		int i1 = html.indexOf("52週波幅") ;
//		i1 = html.indexOf(">", i1) ;
//		int i2 = html.indexOf("<", i1) ;
//		if(i1 > 0 && i2 > 0) {
//			String s = html.substring(i1+1, i2) ;
//			return s ;
//		}
//		return "" ;
//	}
	
	public double get52wh() {
		Elements es = doc.select("div:contains(52週高位) + div") ;
		for(Element e :es) {
			try{
				return Double.parseDouble(e.html()) ;
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return 0;
	}

	public double get52wl() {
		Elements es = doc.select("div:contains(52週低位) + div") ;
		for(Element e :es) {
			try{
				return Double.parseDouble(e.html()) ;
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return 0;
	}
	
	public double getPb() {
		Elements es = getElements("市賬率");
		for(Element e :es) {
			try{
//				System.out.println(">>>" + e.html() + "<<<") ;
				return Double.parseDouble(e.html()) ;
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return 0;
	}

	public double getNav() {
		Elements es = getElements("每股淨值");
		for(Element e :es) {
			try{
				return Double.parseDouble(e.html()) ;
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return 0;
	}
	
	public double getEarn() {
		Elements es = getElements("每股盈利");
		for(Element e :es) {
			try{
				return Double.parseDouble(e.html()) ;
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return 0;
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
				+","+this.getInt()
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
	
	public static void main(String args[]) throws Exception {
//		Detail d = new Detail("0005") ;
//		d.writeToFile();
		
		Detail d = new Detail() ;
		d.readFromFile();
		System.out.println(d.getPb()) ;
		System.out.println(d.getMarketCap()) ;
		System.out.println(d.getName()) ;
		System.out.println(d.getClose()) ;
		System.out.println(d.getVol()) ;
		System.out.println(d.get52wh()) ;
		System.out.println(d.get52wl()) ;
		System.out.println(d.getNav()) ;
		System.out.println(d.getEarn()) ;
		
		
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
