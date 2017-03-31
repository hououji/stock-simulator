package info.hououji.sim;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class DownloaderOption implements Runnable {

	String code ; 
	File dir ;
	
	public DownloaderOption(String code, File dir) {
		this.code = code ;
		this.dir = dir ;
	}
	
	public static File getRecentDirectory() {
		File root = new File("./option") ;
		File target = null ;
		for(File dir : root.listFiles()) {
			if( ! dir.isDirectory()) continue;
			if(target == null) {
				target = dir;
				continue;
			}
			if(dir.getName().compareTo(target.getName()) > 0) {
				target = dir ;
			}
		}
		return target;
	}
	
	public static String getName(File file) {
		String filename = file.getName() ;
		return filename.substring(0, 4) ;
	}
	
	public static void main(String args[]) throws Exception{
		
		// Find a location
		File dir = new File("./option/" + new SimpleDateFormat("yyyyMMdd").format(new Date())) ;

		URL url = new URL("https://www.hkex.com.hk/chi/ddp/List_Of_Underlying_C.asp?PgId=13") ;
		File file = new File(dir, "index.html") ;
		FileUtils.copyURLToFile(url,file) ;
		Log.log("download complete : index") ;
		
		String html = FileUtils.readFileToString(file) ;
		Document doc = Jsoup.parse(html ) ;
		
		
		Elements trs = doc.select("a[href^=Contract_Details_c.asp?PId=]") ;
		for(int count=0; count<trs.size(); count++ ){
			Element e = trs.get(count) ;
			System.out.println(e.text() + "," + e.attr("href")) ;
			url = new URL("https://www.hkex.com.hk/chi/ddp/" + e.attr("href")) ;
			file = new File(dir, e.text() + ".html") ;
			FileUtils.copyURLToFile(url,file) ;
		}

		
//		ExecutorService executor = Executors.newFixedThreadPool(5);  
//		for(int i=1; i<=9999; i++) {
//			String currCode = i + "" ;
//			currCode = StringUtils.leftPad(currCode, 4, '0') ;
//			executor.execute(new DownloaderOption(currCode, dir));
//		}
//		executor.shutdown();
		
		
	}
	
	public void run()  {
		File file = new File(dir, code + ".csv") ;
		try {
			Date now = new Date() ;
			//URL url = new URL("http://real-chart.finance.yahoo.com/table.csv?s="+code+".HK&d=8&e=13&f=2016&g=d&a=0&b=4&c=2000&ignore=.csv") ;
			URL url = new URL("http://real-chart.finance.yahoo.com/table.csv?s="+code+".HK&d="+now.getMonth()+"&e="+now.getDate()+"&f="+(now.getYear() + 1900)+"&g=d&a=0&b=4&c=2000&ignore=.csv") ;
			FileUtils.copyURLToFile(url, file) ;
			Log.log("download complete : " + code) ;
		}catch(FileNotFoundException fnfe) {
			// no such stock, just skip
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
