import info.hououji.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;


public class MarketCapExcel {
	public static void main(String args[]) throws Exception {
//		Aastock aa = new Aastock("0002") ;
//		System.out.println(aa.toString());
		
		// Real
		File dir = Downloader.getRecentDirectory() ;
		System.out.println(dir.getAbsolutePath()) ;
		File[] files = dir.listFiles() ;
		Arrays.sort(files);
		
		// Get from Aastock, one by one
		Map<String, Detail> details = new LinkedHashMap<String,Detail>() ;
		Log.log("file count:" + files.length);
		for(File file : files) {
			try{
				String code = Downloader.getName(file) ;
				System.out.println("aa:"+code) ;
				Detail a = new Detail(code) ;
				details.put(code, a) ;
				Thread.sleep(1000);
			}catch(Exception ex){
				ex.printStackTrace(); 
			}
		}
		
		// Get supp. info from Yahoo, 100 for One batch, since yahoo will block IP, for > 200 request 1 day
		String[] codes =details.keySet().toArray(new String[0]) ;
		for(int i=0; i<codes.length; i=i+100) {
			try {
				List<String> batch = new ArrayList<String>() ;
				for(int j=i; j<codes.length && j<i+100; j++ ) {
					batch.add(codes[j] + ".HK") ;
				}
				String s = "http://finance.yahoo.com/d/quotes.csv?s=" + StringUtils.join(batch.toArray(new String[0]), "+")  + "&f=sp6y";
				System.out.println(s);
				URL url = new URL(s) ;
				InputStream in = url.openStream() ; 
				String csv = IOUtils.toString(in) ;
				BufferedReader br = new BufferedReader(new StringReader(csv)) ;
				while(true) {
					try{
						String line = br.readLine() ;
						if(line == null) break;
						StringTokenizer stok = new StringTokenizer(line, ",") ;
						String code = stok.nextToken() ;
						Detail d = details.get(code.substring(1, 5)) ;
						if(d==null) continue;
						d.pb = Float.parseFloat(stok.nextToken()) ;
						d.dividendyield = Float.parseFloat(stok.nextToken()) ;
					}catch(Exception ex)
					{
						ex.printStackTrace(); 
					}
				}
				Thread.sleep(1000);
			}catch(Exception ex) {
				ex.printStackTrace(); 
			}
		}
		
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("output/market_cap.csv"),"utf8") ;
		PrintWriter out = new PrintWriter(osw) ;
		out.print("\uFEFF"); // BOM, make Excel auto use UTF8
		out.println(Detail.title());
		for(String code : codes) {
			out.println(details.get(code).toString()) ;
		}
		out.flush(); 
		out.close();
		osw.close();
	}
}
