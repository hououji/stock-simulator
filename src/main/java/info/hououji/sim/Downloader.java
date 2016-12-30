package info.hououji.sim;
import info.hououji.sim.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
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
		
		// Find a location
		File dir = new File("./data/" + new SimpleDateFormat("yyyyMMdd").format(new Date())) ;
		
		ExecutorService executor = Executors.newFixedThreadPool(5);  
		for(int i=1; i<=9999; i++) {
			String currCode = i + "" ;
			currCode = StringUtils.leftPad(currCode, 4, '0') ;
			executor.execute(new Downloader(currCode, dir));
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
