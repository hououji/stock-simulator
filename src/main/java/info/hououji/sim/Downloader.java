package info.hououji.sim;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;


public class Downloader implements Runnable {

	String code ; 
	File dir ;
	
	public Downloader(String code, File dir) {
		this.code = code ;
		this.dir = dir ;
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
	
	public static void main(String args[]) throws Exception{

		SSLTool.disableCertificateValidation();
		
		CookieManager manager = new CookieManager();
		manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(manager);
		CookieStore cookieJar =  manager.getCookieStore();

		String urlstr = "https://hk.finance.yahoo.com/quote/0016.HK/history?period1=0&period2=1495382400&interval=1d&filter=history&frequency=1d" ;
//		URL url = new URL(urlstr);
//		URLConnection connection = url.openConnection();
//		connection.getContent();



		String html = CachedDownload.getString(new URL(urlstr),false) ;
//		System.out.println(html);
		PrintStream out = new PrintStream(new FileOutputStream("out.html")) ;
		out.println(html) ;
		out.flush(); 
		out.close();
		String crumb = getYahooCrumb(html) ;
		System.out.println(crumb) ;

		List <HttpCookie> cookies =
				cookieJar.getCookies();
		List <HttpCookie> c2 = new ArrayList <HttpCookie>() ;
		for (HttpCookie cookie: cookies) {
			System.out.println("CookieHandler retrieved cookie: " + cookie);
			c2.add(cookie) ;
		}

		
		urlstr = "https://query1.finance.yahoo.com/v7/finance/download/8409.HK?period1=0&period2=1495382400&interval=1d&events=history&crumb=" + crumb ;
		URL url = new URL(urlstr) ;
		CookieManager manager2 = new CookieManager();
		manager2.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(manager2);
		CookieStore cookieJar2 =  manager.getCookieStore();
		
		for (HttpCookie c: c2) {
			System.out.println("CookieHandler add cookie: " + c);
			cookieJar2.add(url.toURI(), new HttpCookie(c.getName(),c.getValue()));
		}
		
		String csv = CachedDownload.getString(url,false) ;
		PrintStream out2 = new PrintStream(new FileOutputStream("out.csv")) ;
		out2.println(csv) ;
		out2.flush();
		out2.close();
	}
	
	public static String getYahooCrumb(String html) {
		html = html.replaceAll("\\{", "\n").replaceAll("\\}", "\n") ;
		String crumb = "" ;
		for(String line : html.split("\n")) {
			if(line.indexOf("\"firstName\"") != -1 && line.indexOf("\"crumb\"") != -1) {
				crumb = line.split("\\\"")[3] ;
			}
		}
		crumb = crumb.replaceAll("\\u002F", "\\") ;
		return crumb;
	}
	
	public static void main1(String args[]) throws Exception{
		
		// Find a location
		File dir = new File("./data/" + new SimpleDateFormat("yyyyMMdd").format(new Date())) ;
		
		ExecutorService executor = Executors.newFixedThreadPool(5);  
		for(int i=1; i<=9999; i++) {
			String currCode = i + "" ;
			currCode = StringUtils.leftPad(currCode, 4, '0') ;
			executor.execute(new Downloader(currCode, dir));
			break;
		}
		executor.shutdown();
		
		
//		Downloader dl = new Downloader() ;
//		dl.download("ABCD", new File("."));
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
