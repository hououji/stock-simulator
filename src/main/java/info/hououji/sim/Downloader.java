package info.hououji.sim;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class Downloader implements Runnable {

	String code ; 
	File dir ;
	String crumb ; 
	
	public Downloader(String code, File dir, String crumb) {
		this.code = code ;
		this.dir = dir ;
		this.crumb = crumb ;
	}
	
	public static File getRecentDirectory() {
		File root = new File("./data") ;
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
	
	public static String getCrumb() throws Exception {
		// Get crumb
		CookieManager manager = new CookieManager();
		manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(manager);
		CookieStore cookieJar =  manager.getCookieStore();

		String urlstr = "https://hk.finance.yahoo.com/quote/0016.HK/history?period1=0&period2=1495382400&interval=1d&filter=history&frequency=1d" ;

		String crumb = null ;
		while(true) {
			String html = CachedDownload.getString(new URL(urlstr),false) ; // TODO useCache should be false after test
			crumb = getYahooCrumb(html) ;
if(1==1) return "" ;			
			System.out.println(crumb) ;
			if(crumb.indexOf("002F")== -1) break; // retry if crumb include 002F
			System.out.println("get crumb fail, wait for retry") ;
			Thread.sleep(5000);
	//		System.out.println(html);
	//		PrintStream out = new PrintStream(new FileOutputStream("out.html")) ;
	//		out.println(html) ;
	//		out.flush(); 
	//		out.close();
		}

		// Setup the cookies store
		
		CookieManager manager2 = new CookieManager();
		CookieHandler.setDefault(manager2);
		CookieStore cookieJar2 =  manager2.getCookieStore();
		List <HttpCookie> cookies = cookieJar.getCookies();
		for (HttpCookie cookie: cookies) {
			cookieJar2.add(new URL("https://query1.finance.yahoo.com/").toURI(), cookie);
		}
		return crumb;
	}
	
	public static void main(String args[]) throws Exception{

		SSLTool.disableCertificateValidation();
		
		String crumb = getCrumb() ;
		System.out.println("crumb : " + crumb) ;
//if(1==1) return;		
		// Go
		File dir = new File("./data/" + new SimpleDateFormat("yyyyMMdd").format(new Date())) ;
		dir.mkdirs() ;
		
		ExecutorService executor = Executors.newFixedThreadPool(5);
		
		String html = CachedDownload.getString(new URL("https://webb-site.com/dbpub/mcap.asp")) ;
		//String html = IOUtils.toString(new FileInputStream(new File("mcap.asp"))) ;
		Document doc ;
		doc = Jsoup.parse(html ) ;
		Elements stockHref = doc.select("table.numtable tr td:eq(1) a") ;

		
		for(int i=0; i<stockHref.size() ; i++) {
			System.out.println("list "+i+"/" + stockHref.size())  ;
			String href = stockHref.get(i).attr("href") ; 
			String code  = stockHref.get(i).text() ;
//		for(int i=1; i<=9999; i++) {
//			executor.execute(new Downloader(currCode, dir, crumb));
			new Downloader(code, dir, crumb).run();
			Thread.sleep(200);
		}
		executor.shutdown();
	}
	
	public static String getYahooCrumb(String html) {
		// "CrumbStore":{"crumb":"JbA3mLICLk8"}
		String crumb = "" ;
		String crumbHead = "\"crumb\":\"" ;
		for(String line : html.split(",")) {
			if(line.indexOf("CrumbStore") != -1) {
				int idx1 = line.indexOf(crumbHead) ;
				int idx2 = line.indexOf("\"", idx1 + crumbHead.length()) ;
				crumb = line.substring(idx1 + crumbHead.length(),idx2) ;
				break;
			}
		}
//		crumb = crumb.replaceAll("\\u002F", "\\") ;
		return crumb;
	}
	
//	public static void main1(String args[]) throws Exception{
//		
//		// Find a location
//		File dir = new File("./data/" + new SimpleDateFormat("yyyyMMdd").format(new Date())) ;
//		
//		ExecutorService executor = Executors.newFixedThreadPool(5);  
//		for(int i=1; i<=9999; i++) {
//			String currCode = i + "" ;
//			currCode = StringUtils.leftPad(currCode, 4, '0') ;
//			executor.execute(new Downloader(currCode, dir));
//			break;
//		}
//		executor.shutdown();
//		
//		
////		Downloader dl = new Downloader() ;
////		dl.download("ABCD", new File("."));
//	}
	
	public void run()  {
		try {
			Log.log("download start : " + code) ;
			File file = new File(dir, code + ".csv") ;
			if(file.exists() &&  (new Date().getTime() -  file.lastModified()) < 1000 * 60 * 60 * 24  ) {
				// same file is exist within 1 day skip.
				Log.log("File exist within 1 day, skip");
				return ;
			}
			URL url = new URL("https://query1.finance.yahoo.com/v7/finance/download/"+code+".HK?period1=0&period2="+new Date().getTime()+"&interval=1d&events=history&crumb=" + crumb) ;
			String csv = CachedDownload.getString(url,false) ;// since the crumb will change , cache is no use 
			String line[] = csv.split("\n") ;
			StringBuffer sb = new StringBuffer() ;
			sb.append(line[0].trim() + "\n") ;
			for(int i=line.length-1; i>0 ;i--) {
				if( "".equals( line[i].trim()) ) continue ;
				if(line[i].indexOf("null" )!= -1 ) continue;
				sb.append(line[i].trim() + "\n") ;
			}
			
			PrintStream out2 = new PrintStream(new FileOutputStream(file)) ;
			out2.println(sb.toString()) ;
			out2.flush();
			out2.close();
			Log.log("download complete : " + code) ;
		}catch(FileNotFoundException fnfe) {
		// no such stock, just skip
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
//		try {
//			Date now = new Date() ;
//			//URL url = new URL("http://real-chart.finance.yahoo.com/table.csv?s="+code+".HK&d=8&e=13&f=2016&g=d&a=0&b=4&c=2000&ignore=.csv") ;
//			URL url = new URL("http://real-chart.finance.yahoo.com/table.csv?s="+code+".HK&d="+now.getMonth()+"&e="+now.getDate()+"&f="+(now.getYear() + 1900)+"&g=d&a=0&b=4&c=2000&ignore=.csv") ;
//			FileUtils.copyURLToFile(url, file) ;
//			Log.log("download complete : " + code) ;
//		}catch(FileNotFoundException fnfe) {
//			// no such stock, just skip
//		}catch(Exception ex) {
//			ex.printStackTrace();
//		}
	}
}
