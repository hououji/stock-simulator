import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;


public class Aastock {

	String html = "" ;
	
	public Aastock(String code) {
		InputStream in = null;
		try{
			URL url = new URL("http://www.aastocks.com/tc/mobile/Quote.aspx?symbol=0" + code) ;
			in = url.openStream() ; 
			html = IOUtils.toString(in) ;
//			System.out.println(html) ;
		}catch(Exception ex){
			throw new RuntimeException(ex) ;
		}finally{
			IOUtils.closeQuietly(in);
		}
	}
	
	public float getMarketCap() {
		int i1 = html.indexOf("市值") ;
		i1 = html.indexOf(">", i1) ;
		int i2 = html.indexOf("億", i1) ;
		if(i1 > 0 && i2 > 0) {
			String s = html.substring(i1+1, i2) ;
			return decode(s) ;
		}
		return 0 ;
	}

	
	public String getPe() {
		int i1 = html.indexOf("市盈率") ;
		i1 = html.indexOf(">", i1) ;
		int i2 = html.indexOf("<", i1) ;
		if(i1 > 0 && i2 > 0) {
			String s = html.substring(i1+1, i2) ;
			return s;
		}
		return "" ;
	}
	
	public String getInt() {
		int i1 = html.indexOf("收益率") ;
		i1 = html.indexOf(">", i1) ;
		int i2 = html.indexOf("%", i1) ;
		if(i1 > 0 && i2 > 0) {
			String s = html.substring(i1+1, i2) ;
			return s ;
		}
		return "" ;
	}
	
	public String getClose() {
		int i1 = html.indexOf("前收市價") ;
		i1 = html.indexOf(">", i1) ;
		int i2 = html.indexOf("<", i1) ;
		if(i1 > 0 && i2 > 0) {
			String s = html.substring(i1+1, i2) ;
			return s ;
		}
		return "" ;
	}
	
	public String getName() {
		int i1 = html.indexOf("quote_table_header_text") ;
		i1 = html.indexOf(">", i1) ;
		int i2 = html.indexOf("&", i1) ;
		if(i1 > 0 && i2 > 0) {
			String s = html.substring(i1+1, i2) ;
			return s ;
		}
		return "" ;
	}
	
	public String getVol() {
		int i1 = html.indexOf("成交金額") ;
		i1 = html.indexOf(">", i1) ;
		int i2 = html.indexOf("<", i1) ;
		if(i1 > 0 && i2 > 0) {
			String s = html.substring(i1+1, i2) ;
			return s ;
		}
		return "" ;
	}
	
	public String getHist52w() {
		int i1 = html.indexOf("52週波幅") ;
		i1 = html.indexOf(">", i1) ;
		int i2 = html.indexOf("<", i1) ;
		if(i1 > 0 && i2 > 0) {
			String s = html.substring(i1+1, i2) ;
			return s ;
		}
		return "" ;
	}
	
	
	
	private static float decode(String s) {
		s = s.replace(",", "") ;
		float i = Float.parseFloat(s) ;
		return i;
		
	}
	
	public String toString() {
		return this.getName() +","+ this.getMarketCap() + "," + this.getVol()
				+","+this.getPe()+","+this.getInt()
				+","+this.getClose() + "," +this.getHist52w() ;
	}
	
	public static void main(String args[]) throws Exception {
//		Aastock aa = new Aastock("0002") ;
//		System.out.println(aa.toString());
		
		// Real
		File dir = Downloader.getRecentDirectory() ;
		File[] files = dir.listFiles() ;
		Arrays.sort(files);
		List<String> codes = new ArrayList<String>() ;
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("data/market_cap.csv"),"utf8") ;
		PrintWriter out = new PrintWriter(osw); 
		out.print("\uFEFF"); // BOM, make Excel auto use UTF8
		for(File file : files) {
			try{
				String code = Downloader.getName(file) ;
				Aastock a = new Aastock(code) ;
				out.println(a.toString()) ;
				System.out.println(a.toString());
				Thread.sleep(1000);
			}catch(Exception ex){
				ex.printStackTrace(); 
			}
		}
		out.flush(); 
		out.close();
		osw.close();
	

	}
	
}
